import cv2
from matplotlib import pyplot as plt

# Some basic setup:
# Setup detectron2 logger
import detectron2
from detectron2.utils.logger import setup_logger
setup_logger()

# import some common libraries
import numpy as np
import os, json, cv2, random

# import some common detectron2 utilities
from detectron2.model_zoo import model_zoo
from detectron2.engine import DefaultPredictor
from detectron2.config import get_cfg
from detectron2.utils.visualizer import Visualizer
from detectron2.data import MetadataCatalog, DatasetCatalog

import os
from detectron2.data.datasets import register_coco_instances

from detectron2.engine import DefaultPredictor
from detectron2.data import MetadataCatalog
from detectron2.data.catalog import DatasetCatalog

register_coco_instances("train", {}, "C:\\Users\\Alfred Roberts\\Documents\\projects\\team43_project\\detectron\\data\\f-s2v1\\train\\_annotations.coco.json", "C:\\Users\\Alfred Roberts\\Documents\\projects\\team43_project\\detectron\\data\\f-s2v1\\train")
register_coco_instances("val", {}, "C:\\Users\\Alfred Roberts\\Documents\\projects\\team43_project\\detectron\\data\\f-s2v1\\valid\\_annotations.coco.json", "C:\\Users\\Alfred Roberts\\Documents\\projects\\team43_project\\detectron\\data\\f-s2v1\\valid")
register_coco_instances("test", {}, "C:\\Users\\Alfred Roberts\\Documents\\projects\\team43_project\\detectron\\data\\f-s2v1\\test\\_annotations.coco.json", "C:\\Users\\Alfred Roberts\\Documents\\projects\\team43_project\\detectron\\data\\f-s2v1\\test")



from detectron2.engine import DefaultTrainer

cfg = get_cfg()
cfg.merge_from_file(model_zoo.get_config_file("COCO-Detection/faster_rcnn_R_101_C4_3x.yaml"))
cfg.DATASETS.TRAIN = ("train",)
cfg.DATASETS.TEST = ("val",)
cfg.DATALOADER.NUM_WORKERS = 2
cfg.MODEL.WEIGHTS = model_zoo.get_checkpoint_url("COCO-Detection/faster_rcnn_R_101_C4_3x.yaml")  # Let training initialize from model zoo
cfg.SOLVER.IMS_PER_BATCH = 4 # 6?
cfg.SOLVER.BASE_LR = 0.00025 # 0.001
cfg.SOLVER.MAX_ITER = 800 # 1333?
cfg.SOLVER.STEPS = []        
cfg.MODEL.ROI_HEADS.BATCH_SIZE_PER_IMAGE = 256 # 256   
cfg.MODEL.ROI_HEADS.NUM_CLASSES = 8
cfg.OUTPUT_DIR = 'C:\\Users\\Alfred Roberts\\Documents\\projects\\team43_project\\detectron\\' 
# NOTE: this config means the number of classes, but a few popular unofficial tutorials incorrect uses num_classes+1 here.
os.makedirs(cfg.OUTPUT_DIR, exist_ok=True)



cfg.MODEL.WEIGHTS = os.path.join(cfg.OUTPUT_DIR, "model_final_faces.pth")
cfg.DATASETS.TEST = ("test", )
cfg.MODEL.DEVICE = "cpu"
cfg.MODEL.ROI_HEADS.NUM_CLASSES = 8 
cfg.MODEL.ROI_HEADS.SCORE_THRESH_TEST = .5  # set the testing threshold for this model

predictor = DefaultPredictor(cfg)
test_metadata = MetadataCatalog.get("test")
dataset_dicts = DatasetCatalog.get("test")

    
vid = cv2.VideoCapture(0)

while(True):
      
    ret, img = vid.read()
    
    outputs = predictor(img)
    img = img[:, :, ::-1]
    v = Visualizer(img,
                  metadata=test_metadata, 
                  scale=1.5
                    )
        
    out = v.draw_instance_predictions(outputs["instances"].to("cpu"))
  
    cv2.imshow('img', out.get_image()[:, :, ::-1])
      
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
  
vid.release()
cv2.destroyAllWindows()
