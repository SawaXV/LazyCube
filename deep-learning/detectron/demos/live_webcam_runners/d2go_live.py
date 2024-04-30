from detectron2.engine import DefaultPredictor
from d2go.runner import GeneralizedRCNNRunner
from detectron2.data import MetadataCatalog
from detectron2.data.catalog import DatasetCatalog
import cv2
from matplotlib import pyplot as plt
from d2go.model_zoo import model_zoo
from detectron2.utils.visualizer import Visualizer
import os
from detectron2.data.datasets import register_coco_instances
from d2go.runner import GeneralizedRCNNRunner
import time

plt.rcParams["figure.figsize"] = (20,10)

dirname = os.path.dirname(__file__)

# TODO: make this an argument
train_path = os.path.join(dirname, "../data/f-s2v1/train")
test_path = os.path.join(dirname, "../data/f-s2v1/test")
valid_path = os.path.join(dirname, "../data/f-s2v1/valid")


register_coco_instances("train", {}, os.path.join(train_path, "_annotations.coco.json"), train_path)
register_coco_instances("val", {}, os.path.join(test_path, "_annotations.coco.json"), test_path)
register_coco_instances("test", {}, os.path.join(valid_path, "_annotations.coco.json"), valid_path)


def config_model():
    runner = GeneralizedRCNNRunner()
    cfg = runner.get_default_cfg()
    cfg.merge_from_file(model_zoo.get_config_file("faster_rcnn_fbnetv3a_C4.yaml"))
    cfg.MODEL_EMA.ENABLED = False
    cfg.DATASETS.TRAIN = ("train",)
    cfg.DATASETS.TEST = ("val",)
    cfg.DATALOADER.NUM_WORKERS = 2
    cfg.MODEL.WEIGHTS = model_zoo.get_checkpoint_url("faster_rcnn_fbnetv3a_C4.yaml")  # Let training initialize from model zoo
    cfg.MODEL.DEVICE = "cpu"
    cfg.SOLVER.IMS_PER_BATCH = 8 # 6?
    cfg.SOLVER.BASE_LR = 0.00025 # 0.001
    cfg.SOLVER.MAX_ITER = 2000 # 1333?
    cfg.SOLVER.STEPS = []        
    cfg.MODEL.ROI_HEADS.BATCH_SIZE_PER_IMAGE = 512 # 256   
    cfg.MODEL.ROI_HEADS.NUM_CLASSES = 6  
    return cfg, runner

cfg, runner = config_model()


cfg.MODEL.WEIGHTS = os.path.join(os.path.join(dirname, "..\\models\\"), "model_final_d2go.pth")
cfg.DATASETS.TEST = ("test", )
cfg.MODEL.DEVICE = "cpu"
cfg.MODEL.ROI_HEADS.NUM_CLASSES = 8  # 8, or 7 without face detection
cfg.MODEL.ROI_HEADS.SCORE_THRESH_TEST = 0.6  # set the testing threshold for this model

predictor = DefaultPredictor(cfg)
test_metadata = MetadataCatalog.get("test")
dataset_dicts = DatasetCatalog.get("test")

vid = cv2.VideoCapture(0)


while(True):
      
    ret, img = vid.read()
    
    start = time.time()
    
    outputs = predictor(img)
    img = img[:, :, ::-1]
    
    print(f"Predictor time: {time.time() - start}")
    
    v = Visualizer(img,
                  metadata=test_metadata, 
                  scale=1.5
                    )
        
    out = v.draw_instance_predictions(outputs["instances"].to("cpu"))
    print(f"Predictor + Visualization time: {time.time() - start}")

    cv2.imshow('img', out.get_image()[:, :, ::-1])

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
  
vid.release()
cv2.destroyAllWindows()
