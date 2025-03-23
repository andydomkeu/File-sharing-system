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
    private JTextArea previewArea = new JTextArea(5, 40);


    public ClientUI(Client client) {
        setInterfaceStyle();

        // Set up window using BorderLayout
        this.client = client;
        setTitle("File Sharer");
        setLayout(new BorderLayout());

        // Add buttons for upload and download
        JPanel buttonPanel = new JPanel();
        JButton uploadBtn = new JButton("Upload");
        JButton downloadBtn = new JButton("Download");
        uploadBtn.setToolTipText("Upload selected file from client (left) to server (right)");
        downloadBtn.setToolTipText("Download selected file from server (right) to client (left)");
        buttonPanel.add(uploadBtn);
        buttonPanel.add(downloadBtn);

        // add menu bar and its items
        setupMenu();

        //Set left side as client and right as server
        add(buttonPanel, BorderLayout.SOUTH);
        add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(localList), new JScrollPane(serverList)), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.NORTH);

        // Add Client side
        JPanel clientPanel = new JPanel(new BorderLayout());
        clientPanel.add(new JLabel("Client (Local Files)", JLabel.CENTER), BorderLayout.NORTH);
        clientPanel.add(new JScrollPane(localList), BorderLayout.CENTER);

        // Add Server side
        JPanel serverPanel = new JPanel(new BorderLayout());
        serverPanel.add(new JLabel("Server (Remote Files)", JLabel.CENTER), BorderLayout.NORTH);
        serverPanel.add(new JScrollPane(serverList), BorderLayout.CENTER);

        // Combine into a split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, clientPanel, serverPanel);
        add(splitPane, BorderLayout.CENTER);

        // File preview area
        previewArea = new JTextArea(5, 40);
        previewArea.setEditable(false);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);
        previewArea.setBorder(BorderFactory.createTitledBorder("File Preview"));

        //Set instructions for usage
        JLabel instructions = new JLabel("⬅ Select a file on the left and click Upload | Select a file on the right and click Download ➡", JLabel.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JScrollPane(previewArea), BorderLayout.CENTER);
        bottomPanel.add(instructions, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // Upload download action listeners
        uploadBtn.addActionListener(e -> upload());
        downloadBtn.addActionListener(e -> download());


        // File selection preview listener, clear once new file is selected - local list
        localList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                serverList.clearSelection();
                String filename = localList.getSelectedValue();
                if (filename != null) {
                    File file = new File(Resources.CLIENT_SHARED_FOLDER + "/" + filename);
                    previewArea.setText(readFileContents(file));
                } else {
                    previewArea.setText("");
                }
            }
        });

        // File selection preview listener, clear once new file is selected - server list
        serverList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                localList.clearSelection();
                String filename = serverList.getSelectedValue();
                if (filename != null) {
                    String content = client.downloadFile(filename); // preview only
                    previewArea.setText(content);
                } else {
                    previewArea.setText("");
                }
            }
        });

        refreshFileLists();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmExit();
            }
        });

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }

    // setup the menu bar and all its items
    private void setupMenu(){
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem uploadItem = new JMenuItem("Upload");
        JMenuItem downloadItem = new JMenuItem("Download");
        JMenuItem exitItem = new JMenuItem("Exit");
        menu.add(uploadItem);
        menu.add(downloadItem);
        menu.add(exitItem);
        menuBar.add(menu);
        add(menuBar, BorderLayout.NORTH);

        // add listeners the menu items
        exitItem.addActionListener(e -> confirmExit());
        uploadItem.addActionListener(e -> upload());
        downloadItem.addActionListener(e -> download());
    }

    // confirms the exit of the window
    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // styles everything
    private void setInterfaceStyle() {
        UIManager.put("Panel.background", Color.decode("#E3F2FD"));

        // button
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 14));
        UIManager.put("Button.background", Color.WHITE);
        UIManager.put("Button.foreground", Color.decode("#1E3A5F"));
        UIManager.put("Button.border", BorderFactory.createLineBorder(Color.decode("#1E3A5F"), 2));

        // background
        UIManager.put("OptionPane.background", Color.decode("#E3F2FD"));
        UIManager.put("OptionPane.messageForeground", Color.decode("#1E3A5F"));

        // text field background
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", Color.decode("#1E3A5F"));
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("TextField.border", BorderFactory.createLineBorder(Color.decode("#1E3A5F"), 1));

        // labels
        UIManager.put("Label.background", Color.decode("#E3F2FD"));
        UIManager.put("Label.foreground", Color.decode("#1E3A5F"));
        UIManager.put("Label.font", new Font("Arial", Font.BOLD, 14));

        // lists
        UIManager.put("List.selectionBackground", Color.decode("#1E3A5F"));
        UIManager.put("List.selectionForeground", Color.WHITE);
        UIManager.put("List.font", new Font("Arial", Font.PLAIN, 12));

        // scroll panesj
        UIManager.put("ScrollPane.background", Color.decode("#E3F2FD"));
        UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(Color.decode("#1E3A5F"), 1));

        // scroll bars
        UIManager.put("ScrollBar.background", Color.decode("#E3F2FD"));
        UIManager.put("ScrollBar.foreground", Color.decode("#1E3A5F"));
        UIManager.put("ScrollBar.thumb", Color.decode("#1E3A5F"));
        UIManager.put("ScrollBar.track", Color.decode("#E3F2FD"));

        // tabels
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", Color.decode("#1E3A5F"));
        UIManager.put("Table.font", new Font("Arial", Font.PLAIN, 12));
        UIManager.put("Table.selectionBackground", Color.decode("#1E3A5F"));
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", Color.decode("#1E3A5F"));

        // menu items
        UIManager.put("MenuBar.background", Color.decode("#E3F2FD"));
        UIManager.put("MenuBar.foreground", Color.decode("#1E3A5F"));
        UIManager.put("MenuBar.font", new Font("Arial", Font.BOLD, 14));
        UIManager.put("Menu.background", Color.decode("#E3F2FD"));
        UIManager.put("Menu.foreground", Color.decode("#1E3A5F"));
        UIManager.put("Menu.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("MenuItem.background", Color.decode("#E3F2FD"));
        UIManager.put("MenuItem.foreground", Color.decode("#1E3A5F"));
        UIManager.put("MenuItem.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("MenuItem.selectionBackground", Color.decode("#1E3A5F"));
        UIManager.put("MenuItem.selectionForeground", Color.WHITE);

        // progress bar (if added)
        UIManager.put("ProgressBar.background", Color.decode("#E3F2FD"));
        UIManager.put("ProgressBar.foreground", Color.decode("#1E3A5F"));
        UIManager.put("ProgressBar.border", BorderFactory.createLineBorder(Color.decode("#1E3A5F"), 1));
        try {
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private String readFileContents(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            return "Unable to read file: " + file.getName();
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