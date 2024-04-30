# Detectron2 Rubik's Cube Model

**NOTE, Detectron2 and D2GO are RCNN Deep learning models that we are not using for this project. TFL was found to be preferable to this. For reasons why, see [here](DIFFICULTIES.md).**


## Code Style

Following: https://google.github.io/styleguide/pyguide.html

## Data

Labelled data from our RoboFlow project: https://universe.roboflow.com/lazycube/lazycube/

## Directories

Outline of what each directory holds

**NOTE: all the notebook code is heavily copied from d2go tutorials, used as testing for the potential of the framework, rather than our own work. They can be found here:**
 - https://gilberttanner.com/blog/d2go-use-detectron2-on-mobile-devices/
 - https://github.com/facebookresearch/d2go/blob/main/demo/d2go_beginner.ipynb


```
detectron
│   README.md
│   DIFFICULTIES.md
│
└───data - data from RoboFlow genereted outputs (see above)
│
└───demos - Demo/tutorial code used for research of the models + package
    │
    └───notebooks - Jupyter noteboks for running models
    │   │
    │   └───d2go - d2go mobile model notebooks
    │   │
    │   └───detectron2 - detectron2 (heavier) example model notebooks
    │
    └───live_webcam_runners - Jupyter noteboks for running models
    │
    └───d2go - d2go specific scripts (for quantization)


```

## Getting Started

The following installation is based on instructions found here: https://github.com/facebookresearch/d2go, with extra troubleshooting advice

### Prerequisites

- Anaconda
- Python >= 3.8

Set up a conda environment: 

`conda create -n "env_name"` (can set a specific python version >= 3.8)

Install pytorch and torchvision nightly builds:
`conda install pytorch torchvision cudatoolkit=10.2 -c pytorch-nightly`

Install detectron2:
`python -m pip install "git+https://github.com/facebookresearch/detectron2.git"`

Install modile-vision:
`python -m pip install "git+https://github.com/facebookresearch/mobile-vision.git"`

Install d2go from git repo:
```
git clone https://github.com/facebookresearch/d2go
cd d2go & python -m pip install .
```

**Test it all works in terminal:**

```bash
$ python
>>> from d2go.model_zoo import model_zoo
```

## Troubleshooting

### Conda: `“could not create process”`

Most likely the issue is with space a in your user name. Create a conda environment with specific path instead:

`conda create -p '/path/to/thing'`

### `AttributeError: module 'signal' has no attribute 'SIGKILL'.`

Editing the torch python pip package fixes this (windows issue only?):

`your\path\to\conda\env\lib\site-packages\torch\distributed\elastic\timer\file_based_local_timer.py`

**line 81:** `signal=signal.SIGKILL` to `signal=signal.SIGTERM`

### `RuntimeError: [model_file_name].yaml not available in Model Zoo!`


Must copy d2go configs folder into conda package folder. Copy `configs` folder from d2go repo into d2go pip package located at:

`your\path\to\conda\env\lib\site-packages\d2go\`



## Installing Demo:

You can test out d2go with the demo notebook in the `d2go\demo` directory of the repo.

## Pytorch for Java

Pytorch loads a pytorch module, which is saved as a .pt or .pth.
These can be generated easily with d2go, and can be seen being made with the demo notebook for d2go.

### Installing a Demo Android Studio App

*Installation tips from https://gilberttanner.com/blog/d2go-use-detectron2-on-mobile-devices/*

Clone the pytorch example d2go android app repo:

```bash
git clone https://github.com/pytorch/android-demo-app/tree/master/D2Go
```

Then, open the project in Android Studio, at `android-demo-app/D2Go` (not `android-demo-app/D2Go/ObjectDetection`)

If an error "Gradle’s dependency may be corrupt" occurs, go to *Android Studio - File - Project Structure...* , change the Android Gradle Plugin Version to 4.0.1, and the Gradle Version to 4.10.1.

Build and enjoy!
