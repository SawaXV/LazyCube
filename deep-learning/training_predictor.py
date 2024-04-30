from code import interact
from unicodedata import category
import sys
from enum import Enum
import json
import os
import glob
import shutil
from progress.bar import Bar

class ImageType(Enum):
    IMAGE = 0
    IMAGE_BATCH = 1
    VIDEO = 2
    VIDEO_FPS = 3

class Options(object):
    def __init__(self, model: str, thresh: float, output: str, methods: list[tuple[ImageType, str]]):
        """Creates an Option object to store options for predictions

        Args:
            model (str): The model path
            output (str): Annotation output path
            methods (list[tuple[ImageType, str]]): Different methods, since as a single image, or video etc.
        """
        self.model = model
        self.thresh = thresh
        self.output = output
        self.methods = methods
        
class Output(object):
    def __init__(self, img, boxes: list, predictions: list, classes: list, num: int):
        """Object for holding prediction data from a specific image

        Args:
            img (_type_): Image the data is related to
            boxes (list): List of box annotation data
            predictions (list): List of scores for each box
            classes (list): List of classes from each box
            num (int): Number of annotations
        """
        self.img = img
        self.boxes = boxes    
        self.predictions = predictions    
        self.classes = classes    
        self.num = num    

def parseArgs() -> Options:
    """Parses the command line argument, formatted into an Options object
    """
    # Command line option names
    opts = [opt for opt in sys.argv[1:] if opt.startswith("--")]
    # Command line arg values
    args =  [opt for opt in sys.argv[1:] if not opt.startswith("--")]
    
    # Throw if not enough args
    if len(opts) != len(args):
        raise Exception("Number of command line options not the same as number of args given")
    # Throw if model or output not specified
    if "--model" not in opts or "--output" not in opts:
        raise Exception("--model and --output arguments required")
    # Throw if any of the required image args are not specified
    if not set(["--image", "--image-dir", "--video"]).intersection(set(opts)):
        raise Exception("--image, --image-dir or video argument(s) required")
    
    methods: list[tuple[ImageType, str]] = []
    model: str = ""
    output: str = ""
    thresh: float = 0.5
    # Parse the args to values 
    for i, op in enumerate(opts):
        arg: str = args[i]
        if op == "--model":
            model = arg
        if op == "--thresh":
            thresh = float(arg)
        if op == "--output":
            output = arg
        if op == "--image":
            methods.append((ImageType.IMAGE, arg))
        if op == "--image-dir":
            methods.append((ImageType.IMAGE_BATCH, arg))
        if op == "--video":
            methods.append((ImageType.VIDEO, arg))
            if "--videofps" not in opts:
                raise Exception("--videofps is required with --video")
        if op == "--videofps":
            methods.append((ImageType.VIDEO_FPS, arg))
        
    return Options(model, thresh, output, methods)

def remap_boxes(size: list, boxes: list) -> tuple:
    """Resizes annotation boxes to an image's original size

    Args:
        size (list): Size of image to resize to
        boxes (list): Box data

    Returns:
        list: _description_
    """
    width, height = size
    y0 = int(boxes[0] * height)
    x0 = int(boxes[1] * width)
    y1 = int(boxes[2] * height)
    x1 = int(boxes[3] * width)
    area = (x1-x0) * (y1-y0)
    return area, [x0,y0,x1-x0,y1-y0]

def generate_coco(out_dir: str, thresh: float, outputs: list[Output]):
    """Generates the COCO format json file with annotations

    Args:
        out_dir (str): Directory to write annotations and images to
        thresh (float): Threshold for prediction scores
        outputs (list[Output]): List of outputs, with annotation data for writing
    """
    saving_bar = Bar('Saving Annotations', max=len(outputs))
    # Create a new output directory if it doesn't exist
    if not os.path.exists(out_dir):
        os.makedirs(out_dir)
    # Add annotation class json data
    coco = json.loads('{"info": {},"licenses": [],"categories": [{"id": 0,"name": "Blue"},{"id": 1,"name": "Green"},{"id": 2,"name": "Orange"},{"id": 3,"name": "Red"},{"id": 4,"name": "White"},{"id": 5,"name": "Yellow"}, {"id": 6,"name": "Face"}],"images": [], "annotations": []}')
    # Annotation id
    id = 0
    for i, annotations in enumerate(outputs):
        # Get original image dimensions
        height, width, _ = annotations.img.shape
        # Get image path to write image to
        img_name = 'img_'+str(i)+'.png'
        img_path = os.path.join(out_dir, img_name)
        cv.imwrite(img_path, annotations.img)
        # Add coco image data
        coco['images'].append(json.loads('{"id": '+str(i)+',"license": 1,"file_name": "'+img_name+'","height": '+str(height)+', "width": '+str(width)+'}'))
        # For each annotation in the image
        for j in range(0, annotations.num):
            # Don't look at annotations with score less than threshold
            if (annotations.predictions[j] < thresh):
                continue
            # Resize boxes
            area, box = remap_boxes((width, height), annotations.boxes[j])
            # Get annotation class (e.g. green, blue, etc.)
            category = annotations.classes[j]
            # Add coco annotation data
            coco['annotations'].append(json.loads('{"id": '+str(id)+', "image_id": '+str(i)+', "category_id": '+str(int(category))+', "bbox": '+str(box)+', "area": '+str(area)+', "segmentation": [],"iscrowd": 0}'))
            id += 1
        saving_bar.next()
    saving_bar.finish()
    
    # Write coco json
    output_json_path = os.path.join(out_dir, 'annotations.coco.json')
    with open(output_json_path, 'w') as f:
        json.dump(coco, f)

def preprocess_img(img, width: int, height: int):
    """Preprocess images ready to interpret

    Args:
        img (_type_): Image to preprocess
        width (int): Width needed for interpreter
        height (int): Height needed for interpreter

    Returns:
        _type_: Processed image
    """
    rgb = cv.cvtColor(img, cv.COLOR_BGR2RGB)
    resize = cv.resize(rgb, (width, height))
    reshape = resize.reshape([1, width, height, 3])
    return reshape

def get_image(img_path: str):
    """Get an image from a specific path

    Args:
        img_path (str): Path to image to read

    Returns:
        Image
    """
    return cv.imread(img_path)

def get_interpreter(model_path: str):
    """Get a TensorFlow Lite Interpreter from a given path

    Args:
        model_path (str): Path to .tfl file

    Returns:
        TensorFlow Lite Interpreter
    """
    interpreter = tf.lite.Interpreter(model_path=model_path)
    interpreter.allocate_tensors()
    return interpreter

def get_images_from_video(vid_path: str, fps: int) -> list:
    """Get frames from a given video, sampling x frames per second

    Args:
        vid_path (str): Path to video to load
        fps (int): Number of frames per second to sample

    Returns:
        list: List of images
    """
    imgs = []
    frame = -1
    # Get the video from path
    video = cv.VideoCapture(vid_path)
    # Get the fps of the video e.g. 30
    video_fps = int(video.get(cv.CAP_PROP_FPS))
    # Get total number of frames of the video
    num_frames = int(video.get(cv.CAP_PROP_FRAME_COUNT))
    # Get nth frame, depending on fps given
    frame_count = video_fps // fps
    
    frame_bar = Bar('Getting Frames', max=(num_frames / video_fps) * fps)
    
    success = True
    # While there is a valid frame to read
    while success:
        # Get the next frame
        success, image = video.read()
        frame += 1
        # Skip frame if it isn't nth frame
        if frame % frame_count != 0:
            continue
        # If valid image, add it to list of images
        if success:
            imgs.append(image)     
        frame_bar.next()
    frame_bar.finish()
    return imgs

def get_images_from_batch(dir_path: str) -> list:
    """Get all png and jpg images from a given directory

    Args:
        dir_path (str): Directory with images to get

    Returns:
        list: List of images
    """
    imgs = []
    file_types = ['*.png','*.jpg']
    # Look for png and jpg images
    for file_type in file_types:
        # Get pattern matching on directory
        path = os.path.join(dir_path, file_type)
        # Get all images from that directory
        image_paths = glob.glob(path)
        batch_bar = Bar(f'Getting Batch Images ({file_type})', max=len(image_paths))
        # For every image in the directory
        for img_path in image_paths:
            # Get the image
            imgs.append(get_image(img_path))
            batch_bar.next()
        batch_bar.finish()
    return imgs

def get_images_from_methods(opts: Options) -> list:
    """Gets all the images, using all methods given from command line args

    Args:
        opts (Options): Options from the command line args

    Returns:
        list: List of images
    """
    imgs = []
    for method in opts.methods:
        if method[0] == ImageType.IMAGE:
            imgs.append(get_image(method[1]))
        if method[0] == ImageType.IMAGE_BATCH:
            imgs.extend(get_images_from_batch(method[1]))
        if method[0] == ImageType.VIDEO:
            fps = [x[1] for x in opts.methods if x[0] == ImageType.VIDEO_FPS][0]
            imgs.extend(get_images_from_video(method[1], int(fps)))
    return imgs

def run_predictions(opts: Options):
    """Run predictions on all methods given by the user from arguments

    Args:
        opts (Options): Options object from the command line arguments
    """
    interpreter = get_interpreter(opts.model)
    imgs = get_images_from_methods(opts)
    predict_bar = Bar('Predicting', max=len(imgs))
    image_annotations = []
    # For all images from methods the user gave in arguments
    for img in imgs:
        # Predict on image
        predictions = predict(interpreter, img)
        # Add image annotations to list
        image_annotations.append(predictions)
        predict_bar.next()
    predict_bar.finish()
    generate_coco(opts.output, opts.thresh, image_annotations)

def predict(interpreter, img) -> Output:
    """Run a TensorFlow Lite Interpreter on a specific image

    Args:
        interpreter (_type_): TensorFlow Lite Interpreter
        img (_type_): Image to predict squares

    Returns:
        Output: Output of the predictions from the given image
    """
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()
    # Get dimensions needed for input images
    _, height, width, _ = input_details[0]['shape']
    process_img = preprocess_img(img, width, height)
    # Add image to predict
    interpreter.set_tensor(input_details[0]['index'], process_img)
    # Predict on image
    interpreter.invoke()
    # Get prediction data
    probs = interpreter.get_tensor(output_details[0]['index'])[0]
    boxes = interpreter.get_tensor(output_details[1]['index'])[0]
    num_pred = interpreter.get_tensor(output_details[2]['index'])[0]
    classes = interpreter.get_tensor(output_details[3]['index'])[0]
    return Output(img, boxes, probs, classes, int(num_pred))

    
if __name__ == "__main__":
    options: Options = parseArgs()
    print("Loading Tensorflow...")
    # Import them after parsing args, to reduce initial lag
    # if args are incorrect
    import tensorflow as tf
    import cv2 as cv
    print("Loaded Tensorflow!")
    run_predictions(options)
    