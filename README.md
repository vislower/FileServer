# File Server Application

A very simple File Server application written in Java.

<a href="https://www.gnu.org/licenses/gpl-3.0" alt="License: GPLv3"><img src="https://img.shields.io/badge/License-GPL%20v3-blue.svg"></a>

## Overview
Actions supported:
* send multiple files and folders to server
* retrieve multiple files and folders from server
* delete multiple files and folders from server
* server can handle multiple client at the same time
* automatic encryption of each file sent to server
* automatic decryption of each file retrieved from server
* encryption is done with AES 256-bit key stored in a KeyStore (the KeyStore is locked with user password)

**The application is for the moment only available in CLI, a GUI version is planned.**

## Requirements :page_facing_up:
* Java version 18 or higher installed on two machines (one will be the client and the other the server), you can check your java version with:
    ```
    java -version
    ```
* You need to know the local IP address of the machine that will act as the server and choose a port on which the server will listen (you will need these information to connect clients to the server).

## Installation  🔌
If you don't want to build the project yourself, you can go to the release section and download the .jar file.
## Build the project 🔧
1. Download the repository files (project) from the green `code` button or clone the project with the following command:
    ```
    git clone https://github.com/vislower/FileServer.git
    ```
2. Go inside the project folder and run:
   * For UNIX systems:
     ```
      ./mvnw clean install
     ```
   * For Windows:
     ```
      mvnw.cmd clean install
     ```
3. Find the FileServer-1.0-SNAPSHOT.jar in your local maven repository.

## Run the application :arrow_forward:
1. Simply run the application with:
    ```
    java -jar FileServer-1.0-SNAPSHOT.jar
    ```
2. First run the serverside of the application on one machine. Then run the clientside of the application on another machine.
3. Follow the prompt.

### Contributing 💡
If you want to contribute to this project and make it better with new ideas, your pull request is very welcomed.
If you find any issue just put it in the repository issue section, thank you.

## Thank You!
Please ⭐️ this repo and share it with others
