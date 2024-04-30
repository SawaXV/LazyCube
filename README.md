# LazyCube

**By Team 43**

[![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com/studio)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Development](https://img.shields.io/badge/doc-development-blue)](https://projects.cs.nott.ac.uk/comp2002/2022-2023/team43_project/-/wikis/home)

## Description

An automatic rubik's cube scanning and solving Android app, powered by TensorFlow.

The project is split into three main components:

- Tensorflow Lite object detection model - see `deep-learning/`
- Internal logic for aligning scanned faces - see `logic/`
- Android UI representation for the project - see `app/`

## Contributing

See this [document](./CONTRIBUTING.md) for guidelines for contributing in this project.

## Changelog

Changes from major updates (sprint deliverables) can be found in the [changelog](./CHANGELOG.md).

## Wiki

Link to the development documentation can be found [here](https://projects.cs.nott.ac.uk/comp2002/2022-2023/team43_project/-/wikis/home).

## Links

- [Current Public Data set](https://universe.roboflow.com/lazycube/lazycube) - Development [link](https://app.roboflow.com/lazycube/lazycube)

## Credits

- [AnimCube](https://github.com/cjurjiu/AnimCubeAndroid) - Used to display the scanned cube in 3D
- [min2phase](https://github.com/cs0x7f/min2phase) - Used for cube solving
- [Cubot.io](https://github.com/AkshathRaghav/cubot.io ) - Simple solver, no longer in production use