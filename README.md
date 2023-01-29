<p align="center">
  <img src="https://user-images.githubusercontent.com/97070073/199654750-9662d503-29fb-4030-9614-7040b20bb376.png"
</p>
<h2 align="center">AVC Interactive Map</h2>

## Table of Contents

* [Introduction](#introduction)
* [Contributors](#contributors)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Cloning the Repository](#cloning-the-repository)
  * [Setting up the Emulator](#setting-up-the-emulator)
  * [Debugging the App](#debugging-the-app)
  * [Best Practices](#best-practices)
* [Tools](#tools)
* [Features](#features)
* [Version History](#version-history)
* [Acknowledgments](#acknowledgments)

## Introduction

The interactive campus map of Antelope Valley College is currently being developed as a standalone application 
that is planned to be integrated into the myAVC app in the future. A closed beta of this app is planned to launch
May 2023. The purpose of this app is to help AVC students, faculty, and CMS to navigate the campus in efficient ways
as well as allow opportunities to explore resources available at AVC.

## Contributors

- [Peter Kallos](https://kallosp.github.io/)
- [Cristian Herrera](https://cristianherrera.dev/)
- [Sebastian Ala Torre](https://github.com/stardustgd)
- [Nick](https://github.com/nickg309)
- [Tyler](https://github.com/tcartermills)
- [Nasif Hossain](https://github.com/nhoss)
- [Ryan](https://github.com/ryanreevess)
- [Logan](https://github.com/Logsans)

## Getting Started

### Prerequisites

Before development, you must have [Android Studio](https://developer.android.com/studio/install),
[Git](https://git-scm.com/), and [GitHub Desktop](https://desktop.github.com/) installed.
To check if Git is installed, run the command `git --version` in your terminal. If it is installed,
the current version of Git will be output to the console. Make sure to sign in using your GitHub account.

### Cloning the Repository

If you are using **GitHub Desktop**: 

* Select `Clone a Repository`
* Use the following URL: `https://github.com/KallosP/AVCInteractiveMap`
* Click `Open with Android Studio`

If you are doing it manually:

```shell
# Create a directory for the project
mkdir AVCInteractiveMap

# Clone the repo into the directory
git clone https://github.com/KallosP/AVCInteractiveMap.git AVCInteractiveMap
```

To open the project in Android Studio, click `File` &rarr; `Open...` then navigate to the directory you
created. Click on the directory (it should have an Android logo next to the name of the directory), then click `OK`.

### Setting up the Emulator

To set up an Android Emulator within Android Studio, navigate to `Tools` &rarr; `Device Manager`. Click `Create device`
then select any device. Select the highest API Level available for the device. Click `Finish` and the emulator is set up!

### Debugging the App

After a device has been set up in the Device Manager, you can debug and test the app. Click the green play button at the top right
of the screen to build and run the app (shortcut is `Shift+F10`).

### Best Practices

Always make sure you **fetch** the code from the repository before you push changes. In **GitHub Desktop**,
this is done by clicking `fetch origin`. In the command line: `git pull`. Doing this ensures that you have
the latest changes applied to your local workspace and avoids conficting file errors.

Try not to push directly to the **master** branch. It is preferred that you create a separate branch and then
create a pull request to merge changes into the master branch. In **GitHub Desktop**, you can create a branch by
clicking `Current Branch`, `New Branch`, and then enter the name of the new branch. In the command line: `git checkout -b name-of-branch`.

When creating commits, try to use descriptive messages (e.g. `Fixed bug` vs `Fixed bug preventing button clicks from registering`).

### Resources

If you are not very familiar with using Git and GitHub, and/or are having issues with setting up Android Studio, check out these videos!

* [Git/GitHub Guide](https://youtu.be/8Dd7KRpKeaE)
* [Git Auto Detection in Android Studio](https://youtu.be/GhfJTOu3_SE?t=20)
* [Git Commit Messages](https://www.freecodecamp.org/news/how-to-write-better-git-commit-messages/)

## Tools

- [Android Studio](https://developer.android.com/studio)
- [Google Maps SDK for Android](https://console.cloud.google.com/marketplace/product/google/maps-android-backend.googleapis.com?authuser=2&project=testing-gm-362905)
- [Trello](https://trello.com/en)
- [Figma](https://www.figma.com/file/2KHa4Wjhsp17Dq2KBWzndX/AVC-Interactive-Map-Design-Team?node-id=0%3A1&t=7LV1NWxSdmb0zoj5-1)

## Features

* Map markers for important campus locations
* Re-center buttons for the general map and user locations
* Nearest parking lot calculator
* Help Page including FAQs, Support, and About Us sections
* Marker/Location filter
* Realtime GPS tracker
* Legend View

## Version History

* Still in development!

## Acknowledgments

Inspiration, code snippets, etc.

* [Template README File](https://gist.github.com/DomPizzie/7a5ff55ffa9081f2de27c315f5018afc)
* [Android Studio Related Tutorials](https://gist.github.com/stardustgd/12d278575125ffa1ccf4cbe6f6edd4f4)
* [GitHub Related Tutorials](https://gist.github.com/stardustgd/e9acf2fd9d432679f141713bd2fd1f0e)
* [Google Maps SDK Documentation & Tutorials](https://gist.github.com/stardustgd/6ecec4498569197a6d81f463e186b892)
* [Tutorials Used to Implement Features](https://gist.github.com/stardustgd/0909968bf5b4fd7afbc1dc6361397337)
