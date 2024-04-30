# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Release v0.4 - 09-05-2023

### Added

- Added FAQ questions

### Change

- Updated scanning and solving tutorials

### Fixed

- Fixed bottom navigation bar selection bug

## Release v0.3 - 28-03-2023

### Added

#### Android App

- Added working bottom nav-bar fragment navigation
- Added about page to app navigation
- Added various completion sound effects
- Added option for solving auto-play, to automatically step though the solution for you
- Added settings activity
- Added french, spanish, german and simplified chinese localisations
- Added turbo mode option in settings
  - Reduces number of frames needed to detect a face, will decrease accuracy
- Added option to change language in settings
- Added progress bar animations

#### Logic

- Tuned failure recovery to be more accurate

### Changed

- Redesigned the camera activity
- Moved debug button to settings activity

### Fixed

- Fixed bug where snackbars weren't begin correctly hidden
- Fixed bug where failure recovery wasn't being calculated

## Release v0.2 - 14-03-2023

### Added

- Designs for about screen to show licensing and credits

#### Android App

- Tapping on the camera preview will allow you to focus on a specific area
- Ticks are shown on the faces of the user's cube when they have been successfully scanned
- Re-scan button to give the ability to reset scanning progress
- Snackbar popups for scanning failure and "complete failure", where the user is prompted to reset scanning progress
- A hint is shown when a user has been scanning a face for too long, to suggest them to rotate to scan a new face.
- A tick animation is shown on full scan completion
- Progress bar face colours are removed for each face scanned
- Added re-scan buttons to the solution activity
- Confetti is played when the user finishes their solve
- Current step of the solve and the current move notation is now shown in the solution activity

#### Logic

- Added more cube validator and adder tests
- Added failure recovery mechanisms when faces are failed to be added, in two ways
  - Result "time seen" values to reset colours values of each square in a face
  - Attempt to swap square colours that have similar time seen values (for example red/orange could be detected similarly)

### Changed

- Redesigned camera and solution activities to more align with Material 3 designs
- New TensorFlow Model
- Slowed animation for double turn on the 3D solution cube
- Solution activity move buttons now use arrow icons, and have swapped positions
- Solution activity move/back buttons are disabled during the start and end of the solution sequence respectively

### Fixed

- Fixed oversight in CubeValidator where the specific order of corner "cubies" wasn't taken into account
- Added temporary cube parity fix during face addition

## Release v0.1 - 27-02-2023

### Added

- Initial version release
