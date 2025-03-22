package client_side;
import config.Resources;
import java.io.*;
import java.net.Socket;

public class Client {
    private String serverAddress;

    public Client(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String[] requestDirectory() {
        //Sends command to server
        String response = sendRequest("DIR");
        //Split results into array of file names (if not empty)
        return response.isEmpty() ? new String[0] : response.split(",");
    }

    //Send upload command with the filename and content of file
    public void uploadFile(String filename, String content) {
        sendRequest("UPLOAD " + filename + "\n" + content);
    }

    //Sends download command, requests the file, return contents of file as a string
    public String downloadFile(String filename) {
        return sendRequest("DOWNLOAD " + filename);
    }

    //function that is used for commands - each command uses new connection - disconnects after request
    private String sendRequest(String request) {
        //Connect to server using socket
        try (Socket socket = new Socket(serverAddress, Resources.SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            //Send request
            out.println(request);
            socket.shutdownOutput();
            //Read response line-by-line and store and return as string
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = input.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
