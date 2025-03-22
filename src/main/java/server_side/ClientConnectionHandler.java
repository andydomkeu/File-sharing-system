package server_side;
import config.Resources;
import java.io.*;
import java.net.Socket;

public class ClientConnectionHandler implements Runnable {
    private Socket socket;

    public ClientConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //Read and send text from and back to the client
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            //Figure out what command client sends
            String command = input.readLine();
            if (command == null) return;

            if (command.equals("DIR")) {
                handleDirectory(out);
            //Contents of text file Immediately after newline
            } else if (command.startsWith("UPLOAD ")) {
                handleUpload(command.substring(7), input);
            } else if (command.startsWith("DOWNLOAD ")) {
                handleDownload(command.substring(9), out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Helper function to list all files in servers shared folder
    private void handleDirectory(PrintWriter out) {
        File folder = new File(Resources.SERVER_SHARED_FOLDER);
        String[] files = folder.list();
        if (files != null) {
            out.println(String.join(",", files));
        } else {
            //Handle empty folder
            out.println("");
        }
    }

    //Save file from client
    private void handleUpload(String filename, BufferedReader input) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Resources.SERVER_SHARED_FOLDER + "/" + filename))) {
            String line;
            //Read file from client, write it to a new file in servers folder
            socket.shutdownInput();
            while ((line = input.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //The server will load the text from the text file filename, and will immediately send to the client
    private void handleDownload(String filename, PrintWriter out) {
        File file = new File(Resources.SERVER_SHARED_FOLDER + "/" + filename);
        //If the file exists, then read it line by line and send out to client
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //Handle exception
            out.println("File not found");
        }
    }
}
