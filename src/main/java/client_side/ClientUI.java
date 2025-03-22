package client_side;


import config.Resources;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ClientUI extends JFrame {
    private DefaultListModel<String> localFiles = new DefaultListModel<>();
    private DefaultListModel<String> serverFiles = new DefaultListModel<>();
    private JList<String> localList = new JList<>(localFiles);
    private JList<String> serverList = new JList<>(serverFiles);
    private Client client;

    public ClientUI(Client client) {
        //Set up window using BorderLayout
        this.client = client;
        setTitle("File Sharer");
        setLayout(new BorderLayout());

        //Add buttons for upload and download
        JPanel buttonPanel = new JPanel();
        JButton uploadBtn = new JButton("Upload");
        JButton downloadBtn = new JButton("Download");
        buttonPanel.add(uploadBtn);
        buttonPanel.add(downloadBtn);

        //Set left side as client and right as server
        add(buttonPanel, BorderLayout.NORTH);
        add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(localList), new JScrollPane(serverList)), BorderLayout.CENTER);

        //Event handlers - upload and download
        uploadBtn.addActionListener(e -> upload());
        downloadBtn.addActionListener(e -> download());

        refreshFileLists();

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void refreshFileLists() {
        //Refresh client files
        localFiles.clear();
        File folder = new File(Resources.CLIENT_SHARED_FOLDER);
        if (!folder.exists()) folder.mkdirs();
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                localFiles.addElement(file.getName());
            }
        }

        //Refresh server files
        serverFiles.clear();
        for (String file : client.requestDirectory()) {
            serverFiles.addElement(file);
        }
    }

    private void upload() {
        //Get the filname
        String filename = localList.getSelectedValue();
        if (filename == null) return;

        //Display message if file already exists
        if (serverFiles.contains(filename)) {
            JOptionPane.showMessageDialog(this, "Selected File already exists on the server.", "Upload Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Read contents of selected file and send to server
        File file = new File(Resources.CLIENT_SHARED_FOLDER + "/" + filename);
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            client.uploadFile(filename, content.toString());
            //Refresh UI after completion
            refreshFileLists();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void download() {
        String filename = serverList.getSelectedValue();
        if (filename == null) return;

        File file = new File(Resources.CLIENT_SHARED_FOLDER + "/" + filename);
        if (file.exists()) {
            JOptionPane.showMessageDialog(this, "Selected File already exists in client folder.", "Download Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String content = client.downloadFile(filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
            refreshFileLists();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java ClientUI <server-address>");
            System.exit(0);
        }
        new ClientUI(new Client(args[0]));
    }
}