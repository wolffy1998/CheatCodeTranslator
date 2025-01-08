package com.example.fanyi;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.TransApi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CheatCodeTranslatorGUI extends JFrame {

    private static final String API_KEY = "";
    private static final String SECRET_KEY = "";

    public CheatCodeTranslatorGUI() {
        setTitle("Cheat Code Translator");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JButton fileButton = new JButton("Select .cht File");
        fileButton.setFont(new Font("Arial", Font.BOLD, 16));
        fileButton.setForeground(Color.BLUE);
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(panel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    processFile(selectedFile);
                }
            }
        });

        JLabel titleLabel = new JLabel("Cheat Code Translator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.DARK_GRAY);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(fileButton, BorderLayout.CENTER);

        add(panel);
    }

    private void processFile(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) {
                JOptionPane.showMessageDialog(this, "File is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check the first line for the number of cheats
            String firstLine = lines.get(0).trim();
            if (!firstLine.startsWith("cheats = ")) {
                JOptionPane.showMessageDialog(this, "Invalid file format. First line should be 'cheats = X'.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int numberOfCheats = Integer.parseInt(firstLine.split("=")[1].trim());
            List<Cheat> cheats = new ArrayList<>();

            // Process each cheat entry
            for (int i = 0; i < numberOfCheats; i++) {
                int descIndex = i * 4 + 2; // Description starts at index 2, then every 4th line
                int codeIndex = i * 4 + 3;
                int enableIndex = i * 4 + 4;

                if (descIndex >= lines.size() || codeIndex >= lines.size() || enableIndex >= lines.size()) {
                    JOptionPane.showMessageDialog(this, "Unexpected end of file. Expected more lines.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String descLine = lines.get(descIndex).trim();
                String codeLine = lines.get(codeIndex).trim();
                String enableLine = lines.get(enableIndex).trim();

                if (!descLine.startsWith("cheat" + i + "_desc") ||
                        !codeLine.startsWith("cheat" + i + "_code") ||
                        !enableLine.startsWith("cheat" + i + "_enable")) {
                    JOptionPane.showMessageDialog(this, "Invalid file format. Lines do not match expected pattern.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String desc = descLine.split("=")[1].trim().replaceAll("^\"|\"$", "");
                String code = codeLine.split("=")[1].trim().replaceAll("^\"|\"$", "");
                boolean enable = Boolean.parseBoolean(enableLine.split("=")[1].trim());

                cheats.add(new Cheat(desc, code, enable));
            }

            List<String> updatedLines = new ArrayList<>(lines);

            // Show progress dialog
            JDialog progressDialog = new JDialog(this, "Translating...", false);
            progressDialog.setSize(300, 100);
            progressDialog.setLocationRelativeTo(this);
            progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

            JLabel progressLabel = new JLabel("Translating content, please wait...", SwingConstants.CENTER);
            progressLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            progressDialog.getContentPane().add(progressLabel);
            progressDialog.setVisible(true);

            for (int i = 0; i < cheats.size(); i++) {
                Cheat cheat = cheats.get(i);
                String translatedDesc = translateToChinese(cheat.getDescription());
                updatedLines.set(i * 4 + 2, "cheat" + i + "_desc = \"" + translatedDesc + "\""); // Add spaces around '='
            }

            // Write updated lines back to the file using UTF-8 encoding
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                for (String line : updatedLines) {
                    writer.write(line + "\n");
                }
            }

            progressDialog.dispose();

            JOptionPane.showMessageDialog(this, "File has been successfully updated.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error processing file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String translateToChinese(String text) {
        TransApi api = new TransApi(API_KEY, SECRET_KEY);
        // 返回翻译结果
        String result = api.getTransResult(text, "en", "zh");

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(result, JsonObject.class);
        JsonArray jsonArray = jsonObject.getAsJsonArray("trans_result");
        JsonElement jsonElement = jsonArray.get(0);
        JsonObject jsonObject1 = jsonElement.getAsJsonObject();
        String rs = jsonObject1.get("dst").getAsString();
        System.out.println(rs);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

    static class Cheat {
        private String description;
        private String code;
        private boolean enable;

        public Cheat(String description, String code, boolean enable) {
            this.description = description;
            this.code = code;
            this.enable = enable;
        }

        public String getDescription() {
            return description;
        }

        public String getCode() {
            return code;
        }

        public boolean isEnable() {
            return enable;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CheatCodeTranslatorGUI gui = new CheatCodeTranslatorGUI();
            gui.setVisible(true);
        });
    }
}

