import sys
from numpy import ones,vstack, subtract
from numpy.linalg import lstsq
import numpy as np
import math
import time

THRESH = 20
occ_THRESH = 20

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
    interpreter = tf.lite.Interpreter(model_path=model_path, num_threads=4)
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
    start = time.time()
    # Predict on image
    interpreter.invoke()
    print(time.time() - start)
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

def check_not_in_face(point, face):
    """Check if a given point belongs in a face group

    Args:
        point (tuple): 2d point to consider (POI)
        face (list): list of 2d points for a given face

    Returns:
        bool: if the point is not in the face or not
    """
    # Count of total lines from the POI to any other point
    # that have exactly 2 points that fall on it (excluding POI)
    total = 0
    # Look at every point in the face 
    for p in face:
        # Ignore same point as POI
        if (p == point):
            continue
        # Count for number of points in the line
        count = 0
        # Get an equation for a line between the POI and a given point
        # in the face
        m, c = get_line(point, p)    
        
        # Look for points that fall on that line    
        # and increase count if so
        for p1 in face:
            # Ignore same points as POI
            if p1 == point:
                continue
            if (on_line(p1, (m,c))):
                count += 1
        
        # If there are exactly 2 points that fall on the line
        # increase total accounts of this
        if (count == 2):
            total += 1
    
    # If POI is not in the face if total is < 4
    # A valid point in a face should have 4 or 6 occurrences
    return total < 4

def get_faces(predictions, size, thresh):
    """Get list of faces, removing colours duplicated in other faces
    and only allowing faces composed of 9 points.

    Args:
        predictions (list): TFL predictions
        size (tuple): Size of the scanned image
        thresh (int): Threshold for scanned objects 

    Returns:
        list: 2D array of points to hold faces, with each inner list having
              points ordered from top left to bottom right
    """
    # Get image dimensions
    width, height = size
    # Faces list
    faces = []
    # Boxes in faces
    in_face = []
    # boxes in faces' face index - will have the same length as `dupes`
    in_face_index = []
    # boxes in two or more faces, in format (face index, box index)
    dupes = []
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
        group = []
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
                
                # If point already in a face (duplicate)
                if center in in_face:
                    # Find face index it is already in
                    index = in_face.index(center)
                    # Add duplicate point to list of duplicates for current face
                    # in the form (face index, box index)
                    dupes.append((len(faces), len(group)))
                    # Find index of point in duplicate list
                    dupe_index = faces[in_face_index[index]].index(center)
                    # Add duplicate point to list of duplicates for the other face it's in,
                    # in the form (face index, box index)
                    dupes.append((in_face_index[index], dupe_index))
                
                # Add point to face
                group.append(center)
                # Add point to list of points currently in a face
                in_face.append(center)
                # Add index of the face the point is in
                in_face_index.append(len(faces))

        # Add face to the list of faces
        faces.append(group)
    
    # List of points to remove
    marked = []
    # Look at all duplicates
    for dupe in dupes:
        # Mark point for deletion if it shouldn't be in the current face it's in
        if (check_not_in_face(faces[dupe[0]][dupe[1]], faces[dupe[0]])):
            print("Colour Deleted", faces[dupe[0]][dupe[1]])
            marked.append(dupe)
            
    # Delete all points from faces that are marked for deletion
    # Delete in reverse order, so the list indexes don't change when deleting
    for index in sorted(marked, reverse=True):
        del faces[index[0]][index[1]]

    # Delete faces that don't have a size of 9
    # Delete in reverse order again.
    for i in range(len(faces)-1, -1, -1):
        if len(faces[i]) != 9:
            del faces[i]
            
    return faces


def get_line(p1, p2):
    """Calculate y=mx+c line equation that crosses both points given

    Args:
        p1 (tuple): First point coordinate
        p2 (tuple): Second point coordinate

    Returns:
        tuple: Line equation, of form (m,c)
    """
    ps = [p1,p2]
    x_coords, y_coords = zip(*ps)
    A = vstack([x_coords,ones(len(x_coords))]).T
    m, c = lstsq(A, y_coords)[0]
    return m, c


def on_line(p, eq):
    """Check of a point lays on a line, given an equation

    Args:
        p (tuple): Tuple coordinate e.g. (1,2)
        eq (_type_): Line equation tuple, of form (m,c)

    Returns:
        bool: If a point is on the line, given some "buffer zone" threshold
    """
    y = p[1]
    # y = mx + c
    y_l = eq[0] * p[0]  + eq[1]
    return math.isclose(y, y_l, abs_tol=THRESH) 

def first_occs(a_list, key):
    """Return list of candidate points that could be the top left corner
    Used to help the edge case that the cube is displayed as a "perfect" diamond shape

    Args:
        a_list (list): List of ordered pointers for finding the top left corner
        key (function): Function used for ordering points for finding the top left corner

    Returns:
        list: List of candidate points
    """
    if len(a_list) == 0:
        return []
    index = 0
    firsts = [a_list[index]]
    last = key(firsts[0])
    index += 1
    if index >= len(a_list):
        return firsts
    while math.isclose(last, key(a_list[index]), abs_tol=occ_THRESH):
        firsts.append(a_list[index])
        last = key(a_list[index])
        index += 1
        if index >= len(a_list):
            break;
    return firsts

def last_occs(a_list, key):
    """Return list of candidate points that could be the top right corner
    Used to help the edge case that the cube is displayed as a "perfect" diamond shape

    Args:
        a_list (list): List of ordered pointers for finding the top right corner
        key (function): Function used for ordering points for finding the top right corner

    Returns:
        list: List of candidate points
    """
    if len(a_list) == 0:
        return []
    index = -1
    lasts = [a_list[index]]
    last = key(lasts[0])
    index -= 1
    if index <= -len(a_list) - 1:
        return lasts
    while math.isclose(last, key(a_list[index]), abs_tol=occ_THRESH):
        lasts.append(a_list[index])
        last = key(a_list[index])
        index -= 1
        if index <= -len(a_list) - 1:
            break;
    return lasts

def order_face(box_centers):
    """Order a face's points from top left to bottom right
    
    Example face order:
        0 1 2
        3 4 5
        6 7 8
    Where these are indices of points in each face.
    
    Works by finding the top left and top right points, and drawing a line between them to find the remaining points in a row
    Once found, removes these points from consideration, and repeats until there are no points left.
    
    See for further details:
    https://stackoverflow.com/questions/29630052/ordering-coordinates-from-top-left-to-bottom-right
    https://www.researchgate.net/publication/282446068_Automatic_chessboard_corner_detection_method
    

    Args:
        box_centers (list): List of box centers as xy tuples for a face e.g. (1,2)

    Returns:
        list: list of box centers, ordered in the form explained above
    """
    # Array to hold ordered points
    points = []
    
    while len(box_centers) > 0:
        # List to find top left point
        a_list = sorted(box_centers, key=lambda p: (p[0]) + (p[1]))
        # find list of points that could be the upper left point, if multiple, find the first one when sorted
        # smallest (p[0] + p[1]) value
        a = sorted(first_occs(a_list, key=lambda p: (p[0]) + (p[1])), key=lambda p: p[0])[0] 
        # List to find top right point
        b_list = sorted(box_centers, key=lambda p: (p[0]) - (p[1]))
        # find list of points that could be the upper right point, if multiple, find the first one when sorted
        # smallest (p[0] - p[1]) value
        b = sorted(last_occs(b_list, key=lambda p: (p[0]) - (p[1])), key=lambda p: p[0])[0]
        
        # Find line between top left and top right points
        m, c = get_line(a, b)

        row_points = []
        remaining_points = []
        
        # Find all points they lay on that line
        for k in box_centers:
            if on_line(k, (m,c)):
                row_points.append(k)
            else:
                remaining_points.append(k)
        # Sort points on row via x axis
        row_points = sorted(row_points, key=lambda h: h[0])
        points.extend(row_points)

        # Remove points on line found
        box_centers = remaining_points
     
    return points

def draw_predictions(img, predictions, size, thresh):
    # Get image dimensions
    width, height = size
    for i, box in enumerate(predictions.boxes):
        if (predictions.predictions[i] < thresh):
            continue
        bot, top = remap_boxes((width, height), box)
        cv.rectangle(img, top, bot, (0,0,0), 5)
    # Get face dictionary with face groupings
    faces = get_faces(predictions, (width, height), thresh)
    for j, face in enumerate(faces):
        points = order_face(face)
        for i in range(len(points)):
            p = points[i]
            color = (255,0,255)
            cv.circle(img, p, 10, color, -1)
            cv.putText(img, str(j) + "," + str(i), p, cv.FONT_HERSHEY_SIMPLEX, 1, (255,255,255), 1, cv.LINE_AA)
    return img

def run_predictions(model: str):
    """Run predictions on all methods given by the user from arguments

    Args:
        opts (Options): Options object from the command line arguments
    """
    interpreter = get_interpreter(model)
    
    thresh = 0.5
        
    # Get webcam capture
    vid = cv.VideoCapture(1)
    while(True):
        _, img = vid.read()
        
        # Get original image dimensions
        height, width, _ = img.shape
        
        # Predict on image
        predictions = predict(interpreter, img)
                
        img = draw_predictions(img, predictions, (width, height), thresh)
        
        # Show image
        cv.imshow('Output', img)
        # cv.waitKey(0)
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
    import cv2 as cv
    print("Loaded Tensorflow!")
    run_predictions(model_path)