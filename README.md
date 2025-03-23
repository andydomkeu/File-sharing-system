# Assignment 2 - File Sharing System
CSCI 2020U: System Development and Integration

## Project Overview

This implementation of a file sharing system in Java allows users to upload and download .txt files between a client and server. The server uses a multithreaded approach to handle the connections and processes one command per connection and then disconnects. 

## Structure of Project 

1. Server.java
  - By default starts the server at port 5000 (can modify)
  - Listens for connections using ServerSocket.accept() and creates a new thread using ClientConnectionHandler
  - ClientConnectionHandler.java
  - Each instance of this class handles one request (DIR, UPLOAD, DOWNLOAD)
  - Automatically disconnects after handling each command 
2. Client.java
  - For each operation
  - Opens a new socket to server
  - Sends command
  - Receives response and disconnects
3. Client UI
- Actual UI implementation 
- Lists for client and server files to display
- Upload and download buttons
4. File preview area
  - Integrated into backend via client class
5. Resources.java
  - Where our constants are (server port, and client and server folder paths)


## Enhancements Added 
  - UI aesthetics and usability: modified color schemes, made it more user friendly by adding instructions for use at the bottom and specifying which side is client and server
  - Duplicate Handling: if user tries to perform an upload/download operation on a file that already exists, user is shown an error message and operation is cancelled
  - Selecting a file on one side automatically clears selection on other side to prevent confusion 
  - File Preview panel: scrollable text area at the bottom where user can preview files
  - Hover over upload and download buttons for instructions

## How to Run
1. Clone the repository
2. Can optionally add more files to ‘client_shared’ and ‘server_shared’ folders - test data is already there.
3. Navigate to Server.java and right click and select run Server.main()
4. Once server is running, navigate to ‘ClientUI.java’ 
  - On intelliJ, go to run -> edit configurations
  - Under program arguments, add ‘localhost’
  - Run ClientUI.main()
5. Explore application (upload and download files) 


## Demo: [Insert here]

 
