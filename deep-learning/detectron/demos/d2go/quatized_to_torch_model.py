import os
import torch

# Taken mostly from https://github.com/pytorch/android-demo-app/blob/master/D2Go/create_d2go.py

dirname = os.path.dirname(__file__)

from typing import List, Dict
class Wrapper(torch.nn.Module):
    def __init__(self, model):
        super().__init__()
        self.model = model
        coco_idx_list = [1, 2, 3, 4, 5, 6, 7, 8]

        self.coco_idx = torch.tensor(coco_idx_list)

    def forward(self, inputs: List[torch.Tensor]):
        x = inputs[0].unsqueeze(0) * 255
        scale = 320.0 / min(x.shape[-2], x.shape[-1])
        x = torch.nn.functional.interpolate(x, scale_factor=scale, mode="bilinear", align_corners=True, recompute_scale_factor=True)
        out = self.model(x[0])
        res : Dict[str, torch.Tensor] = {}
        res["boxes"] = out[0] / scale
        res["labels"] = torch.index_select(self.coco_idx, 0, out[1])
        res["scores"] = out[2]
        return inputs, [res]


orig_model = torch.jit.load(os.path.join(os.path.join(dirname, "..\\new\\torchscript_int8\\"), "model.jit"))
wrapped_model = Wrapper(orig_model)
# optionally do a forward
wrapped_model([torch.rand(3, 600, 600)])
scripted_model = torch.jit.script(wrapped_model)
scripted_model.save("./d2go.pt")