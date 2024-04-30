from roboflow import Roboflow
from dotenv import load_dotenv
import os
import sys

from tflite_model_maker.config import ExportFormat
from tflite_model_maker import model_spec
from tflite_model_maker import object_detector

import tensorflow as tf
assert tf.__version__.startswith('2')

tf.get_logger().setLevel('ERROR')
from absl import logging
logging.set_verbosity(logging.ERROR)

# Load dot env file
load_dotenv() 

class Args:
    def __init__(self, spec: str, out_dir: str, model_name: str, batch_size: int, epochs: int):
        """Class for holding command line arguments for training the model

        Args:
            spec (str): TFL model spec
            out_dir (str): Output directory for the .tflite file
            model_name (str): Name of the tflite file
            batch_size (int): Batch size for training
            epochs (int): Number of epochs for training
        """
        self.spec = spec
        self.out_dir = out_dir
        self.model_name = model_name
        
        self.batch_size = batch_size
        self.epochs = epochs

def parseArgs() -> Args:
    """Parses the command line argument, formatted into an Args object
    """
    # Command line option names
    opts = [opt for opt in sys.argv[1:] if opt.startswith("--")]
    # Command line arg values
    args =  [opt for opt in sys.argv[1:] if not opt.startswith("--")]
    
    # Throw if not enough args
    if len(opts) != len(args):
        raise Exception("Number of command line options not the same as number of args given")

    # Set default values
    file_name = "lazycubedet0.tflite"
    output_dir = "."
    spec = "efficientdet_lite0"
    batch_size = 32
    epochs = 100
    
    # Parse the args to values 
    for i, op in enumerate(opts):
        arg: str = args[i]
        if op == "--spec":
            spec = arg
        if op == "--out-dir":
            output_dir = arg
        if op == "--spec":
            spec = arg
        if op == "--epochs":
            epochs = int(arg)
        if op == "--batch-size":
            batch_size = int(arg)
        if op == "--model-name":
            file_name = arg
        
    return Args(spec, output_dir, file_name, batch_size, epochs)

def get_dataset() -> tuple:
    """Gets the RoboFlow data set, using our API key from a .env file

    Returns:
        tuple: the training, validation and test paths for files in the dataset
    """
    # Get the api key
    api_key = os.environ.get("ROBOFLOW_API")
    rf = Roboflow(api_key=api_key)
    # Get our roboflow project
    project = rf.workspace("lazycube").project("lazycube")

    # Get all the dataset versions in our LazyCube Roboflow project
    versions = list(project.versions())
    # Sort the versions by when they were made
    versions = sorted(versions, key = lambda ver : ver.created, reverse=True)
    # Get the latest version
    latest_version = versions[0]
    latest_ver_num = os.path.basename(str(latest_version.version))
    # Download the dataset in the pascal voc format
    dataset = project.version(latest_ver_num).download("voc")

    # Get the training, validation and test paths from the downloaded dataset
    train_path = os.path.join(dataset.location, "train")
    valid_path = os.path.join(dataset.location, "valid")
    test_path = os.path.join(dataset.location, "test")
    
    return train_path, valid_path, test_path
    
def get_voc_data(train_path: str, valid_path: str, test_path: str) -> tuple:
    """Loads the voc dataset

    Args:
        train_path (str): Training data directory path
        valid_path (str): Validation data directory path
        test_path (str): Testing data directory path

    Returns:
        tuple: _description_
    """

    train_data = object_detector.DataLoader.from_pascal_voc(
        train_path, 
        train_path,
        ['Blue', 'Green', 'Orange', 'Red', 'White', 'Yellow', 'Face']
    )

    val_data = object_detector.DataLoader.from_pascal_voc(
        valid_path,   
        valid_path,
        ['Blue', 'Green', 'Orange', 'Red', 'White', 'Yellow', 'Face']
    )

    test_data = object_detector.DataLoader.from_pascal_voc(
        test_path, 
        test_path,
        ['Blue', 'Green', 'Orange', 'Red', 'White', 'Yellow', 'Face']
    )
    
    return train_data, val_data, test_data

def train(args: Args, train_data, val_data):
    """Trains a TFL model

    Args:
        args (Args): Arguments for model type and name
        train_data (_type_): Training data set
        val_data (_type_): Validation dataset
    """
    # Get the model specification to train with
    spec = model_spec.get(args.spec)
    # Train the model
    model = object_detector.create(train_data, 
                                   validation_data=val_data,
                                   model_spec=spec, 
                                   epochs=args.epochs, 
                                   batch_size=args.batch_size, 
                                   train_whole_model=True)
    # Evaluate the model
    print("\n--- MODEL EVALUATION ---\n")
    print(model.evaluate(val_data))
    # Export the model weights
    model.export(export_dir=args.out_dir, tflite_filename=args.model_name)
    # Evaluate the exported model, since quantization will cause some loss in accuracy
    print("\n--- MODEL TFLITE EVALUATION ---\n")
    print(model.evaluate_tflite(args.model_name, val_data))

if __name__ == "__main__":
    args = parseArgs()
    train_path, valid_path, test_path = get_dataset()
    train_data, val_data, _ = get_voc_data(train_path, valid_path, test_path)
    train(args, train_data, val_data)