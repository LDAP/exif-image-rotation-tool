package org.lalber.tools.exifrotation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javaxt.io.Image;

/**
 * @author Lucas Alber
 */
public class Main {

    /**
     * Main entry point to application.
     *
     * @param args not used.
     */
    public static void main(String[] args) {
        File folder = showFileSelectionDialog();
        if (folder == null) return;

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new TitledBorder(new EtchedBorder(), "Process:"));

        JTextArea textArea = new JTextArea(16, 58);
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        mainPanel.add(scroll);

        JFrame frame = new JFrame();
        frame.setTitle("Exif Image-Rotation Tool");
        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        try {
            Files.walk(folder.toPath())
                    .filter(Main::isImage)
                        .collect(Collectors.toList())
                        .parallelStream()
                    .forEach(p -> {
                        textArea.append("Processing: " + p.getFileName().toString() + "\n");
                        Image img = new Image(p.toFile());
                        img.rotate();
                        img.saveAs(p.toFile());
                    });

        } catch (IOException e) {
            System.err.println("Error processing images!");
            e.printStackTrace();
        }

        textArea.append("FINISHED!");
    }

    /**
     * Shows a Dialog to choose a Directory.
     * @return the directory or null if the dialog was closed
     */
    private static File showFileSelectionDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showDialog(null, "OK") != JFileChooser.APPROVE_OPTION)
            return null;
        return fileChooser.getSelectedFile();
    }

    private static boolean isImage(Path p) {
        try {
            String content = Files.probeContentType(p);
            if (content == null) return false;
            return content.split("/")[0].equals("image");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
