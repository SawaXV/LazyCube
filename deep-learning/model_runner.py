import sys

import os
os.environ["CUDA_VISIBLE_DEVICES"]="-1"   


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

def parseArgs() -> str:
    """Parses the command line arguments to get the model path
    """
    # Command line option names
    opts = [opt for opt in sys.argv[1:] if opt.startswith("--")]
    # Command line arg values
    args =  [opt for opt in sys.argv[1:] if not opt.startswith("--")]
    
    # Throw if not enough args
    if len(opts) != len(args):
        raise Exception("Number of command line options not the same as number of args given")
    # Throw if model or output not specified
    if "--model" not in opts:
        raise Exception("--model argument required")
    
    model: str = ""
    # Parse the args to values 
    for i, op in enumerate(opts):
        arg: str = args[i]
        if op == "--model":
            model = arg
        
    return model

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

def remap_boxes(size: tuple, boxes: list) -> list[tuple]:
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
    return [(x0,y0),(x1,y1)]

def get_center(top: tuple, bot: tuple) -> tuple:
    """Get center of rectangle

    Args:
        top (tuple): Top left coordinates
        bot (tuple): Bottom right coordinates

    Returns:
        tuple: Center coordinates (x,y)
    """
    return ((top[0] + bot[0]) // 2, (top[1] + bot[1]) // 2)

def get_colour(pred_class: int) -> tuple[int, int, int]:
    """Gets BGR colour from the prediction class

    Args:
        pred_class (int): Prediction class (0-7)

    Returns:
        tuple[int, int, int]: BGR Colour tuple
    """
    # In BGR colour space
    if (pred_class == 0): # Blue
        return (255,0, 0)
    if (pred_class == 1): # Green
        return (0,255, 0)
    if (pred_class == 2):
        return (0,100, 255) # Orange
    if (pred_class == 3):
        return (0,0, 255) # Red
    if (pred_class == 4):
        return (255,255, 255) # White
    if (pred_class == 5):
        return (0,255, 255) # Yellow
    return (0,0,0)

def point_in_box(point: tuple, box: tuple) -> bool:
    x0 = point[0] < box[0][0]
    x1 = point[0] > box[1][0]
    y0 = point[1] < box[0][1]
    y1 = point[1] > box[1][1]
    return x0 and x1 and y0 and y1

def get_face_dict(predictions, size, thresh):
    # Get image dimensions
    width, height = size
    # Faces dictionary
    faces = {}
    # Face group colour index
    c_index = 0
    # Look at the faces in the predictions
    for i, face_box in enumerate(predictions.boxes):
        # Don't look at annotations with score less than threshold
        if (predictions.predictions[i] < thresh):
            continue
        # Ignore anything other than faces
        if (int(predictions.classes[i]) != 6):
            continue
        c_index += 1
        # Resize boxes
        face_bot, face_top = remap_boxes((width, height), face_box)
        # For all square colour annotations
        for j, box in enumerate(predictions.boxes):
            if (i == j):
                continue
            # Don't look at annotations with score less than threshold
            # or faces
            if predictions.predictions[j] < thresh or int(predictions.classes[j]) == 6:
                continue
            # Resize boxes
            bot, top = remap_boxes((width, height), box)
            center = get_center(top, bot)
            # If box center is in face
            if point_in_box(center, (face_top,face_bot)):
                # Add center point to dictionary, with the key being the coordinates
                # The value is a colour for the face group
                faces[center] = (c_index * 70, c_index * 70, c_index * 70)
    return faces

def draw_predictions(img, predictions, size, thresh):
    # Get image dimensions
    width, height = size
    # Get face dictionary with face groupings
    faces = get_face_dict(predictions, (width, height), thresh)
    # For all annotations
    for i, box in enumerate(predictions.boxes):
        # Don't look at annotations with score less than threshold
        # or faces
        if (predictions.predictions[i] < thresh or int(predictions.classes[i]) == 6):
            continue
        # Resize boxes
        bot, top = remap_boxes((width, height), box)
        # Draw circle
        color = get_colour(int(predictions.classes[i]))
        center = get_center(top, bot)
        if (center in faces.keys()):
            cv.circle(img, center, 20, faces[center], -1)
        cv.circle(img, center, 10, color, -1)
    return img

def run_predictions(model: str):
    """Run predictions on all methods given by the user from arguments

    Args:
        opts (Options): Options object from the command line arguments
    """
    interpreter = get_interpreter(model)
    
    thresh = 0.5
        
    # Get webcam capture
    vid = cv.VideoCapture(0)
    while(True):
        _, img = vid.read()
        
        # Get original image dimensions
        height, width, _ = img.shape
        
        # Predict on image
        predictions = predict(interpreter, img)
                
        img = draw_predictions(img, predictions, (width, height), thresh)
        
        # Show image
        cv.imshow('Output', img)

        # Close window when q pressed
        if cv.waitKey(1) & 0xFF == ord('q'):
            break
    
    vid.release()
    cv.destroyAllWindows()


if __name__ == "__main__":
    model_path = parseArgs()
    print("Loading Tensorflow...")
    # Import them after parsing args, to reduce initial lag
    # if args are incorrect
    import tensorflow as tf
    tf.compat.v1.disable_eager_execution()
    import cv2 as cv
    print("Loaded Tensorflow!")
    run_predictions(model_path)