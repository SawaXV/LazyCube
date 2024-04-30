from multiprocessing.dummy import freeze_support
from d2go.model_zoo import model_zoo
import copy
from d2go.export.api import convert_and_export_predictor
from detectron2.checkpoint import DetectionCheckpointer

from d2go.runner import GeneralizedRCNNRunner
import os

import logging

previous_level = logging.root.manager.disable
logging.disable(logging.INFO)

dirname = os.path.dirname(__file__)

import os
from detectron2.data.datasets import register_coco_instances

train_path = os.path.join(dirname, "./data/f-s2v1/train")
test_path = os.path.join(dirname, "./data/f-s2v1/test")
valid_path = os.path.join(dirname, "./data/f-s2v1/valid")


register_coco_instances("train", {}, os.path.join(train_path, "_annotations.coco.json"), train_path)
register_coco_instances("val", {}, os.path.join(test_path, "_annotations.coco.json"), test_path)
register_coco_instances("test", {}, os.path.join(valid_path, "_annotations.coco.json"), valid_path)


import torch
TORCH_VERSION = ".".join(torch.__version__.split(".")[:2])
print(TORCH_VERSION)

def prepare_for_launch():
    runner = GeneralizedRCNNRunner()
    cfg = runner.get_default_cfg()
    cfg.merge_from_file(model_zoo.get_config_file("faster_rcnn_fbnetv3a_C4.yaml"))
    cfg.DATASETS.TRAIN = ("train",)
    cfg.DATASETS.TEST = ("val",)
    cfg.MODEL.WEIGHTS = os.path.join(os.path.join(dirname, ".\\"), "model_final_d2go.pth")
    cfg.MODEL.DEVICE = "cpu"
    cfg.MODEL.ROI_HEADS.NUM_CLASSES = 7
    os.makedirs(cfg.OUTPUT_DIR, exist_ok=True)
    return cfg, runner

cfg, runner = prepare_for_launch()


model = runner.build_model(cfg)
DetectionCheckpointer(model).load(cfg.MODEL.WEIGHTS)

data_loader = runner.build_detection_test_loader(cfg, dataset_name="val")

pytorch_model = model
pytorch_model.cpu()

if __name__ == '__main__':
    freeze_support()
    predictor_path = convert_and_export_predictor(
        copy.deepcopy(cfg),
        copy.deepcopy(pytorch_model),
        "torchscript_int8",
        os.path.join(dirname, "./quantize-output"),
        data_loader,
    )
    # recover the logging level
    logging.disable(previous_level)
    print(predictor_path)
