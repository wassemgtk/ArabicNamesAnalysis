package com.analysis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ArabicNamesAnalysisHelperFactory {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Arabic Name Analysis");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 350);
            frame.setContentPane(new AnalysisPanel());
            frame.setVisible(true);
        });
    }

    static class AnalysisPanel extends JPanel implements ActionListener {
        private final JTextField nameField;
        private final JTextField oppositeNameField;
        private final JTextField sexField;
        private final JTextField derivationsField;
        private final JTextField meaningField;
        private final JTextField originField;

        public AnalysisPanel() {
            super(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Name
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(new JLabel("Name:"), gbc);
            nameField = new JTextField(20);
            gbc.gridx = 1;
            add(nameField, gbc);

            // Analysis Button
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            JButton analyseButton = new JButton("Analysis");
            analyseButton.setActionCommand("ANALYSIS");
            analyseButton.addActionListener(this);
            add(analyseButton, gbc);

            // Opposite Name
            gbc.gridy = 2;
            gbc.gridwidth = 1;
            add(new JLabel("Opposite Name:"), gbc);
            oppositeNameField = new JTextField(20);
            oppositeNameField.setEditable(false);
            gbc.gridx = 1;
            add(oppositeNameField, gbc);

            // Sex
            gbc.gridx = 0;
            gbc.gridy = 3;
            add(new JLabel("Sex:"), gbc);
            sexField = new JTextField(20);
            sexField.setEditable(false);
            gbc.gridx = 1;
            add(sexField, gbc);

            // Derivations
            gbc.gridx = 0;
            gbc.gridy = 4;
            add(new JLabel("Derivations:"), gbc);
            derivationsField = new JTextField(20);
            derivationsField.setEditable(false);
            gbc.gridx = 1;
            add(derivationsField, gbc);

            // Meaning
            gbc.gridx = 0;
            gbc.gridy = 5;
            add(new JLabel("Meaning:"), gbc);
            meaningField = new JTextField(20);
            meaningField.setEditable(false);
            gbc.gridx = 1;
            add(meaningField, gbc);

            // Origin
            gbc.gridx = 0;
            gbc.gridy = 6;
            add(new JLabel("Origin:"), gbc);
            originField = new JTextField(20);
            originField.setEditable(false);
            gbc.gridx = 1;
            add(originField, gbc);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ("ANALYSIS".equals(e.getActionCommand())) {
                doAnalyse();
            }
        }

        private void doAnalyse() {
            String name = nameField.getText();
            if (name == null || name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No name specified", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] words = name.split(" ");
            String firstWord = words[0];

            AnalysisManager mgr = new AnalysisManager();
            try {
                String oppositeName = mgr.languageOpposition(firstWord);
                oppositeNameField.setText(oppositeName);

                boolean isFemale = mgr.isFemale(oppositeName);
                sexField.setText(isFemale ? "Female" : "Male");

                String derivations = mgr.processDerivations(oppositeName);
                derivationsField.setText(derivations);

                String meaning = mgr.processMeaning(oppositeName);
                meaningField.setText(meaning);

                String origin = mgr.processOrigin(firstWord);
                originField.setText(origin);

            } catch (AnalysisException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
