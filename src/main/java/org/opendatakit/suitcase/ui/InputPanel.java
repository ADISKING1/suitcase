package org.opendatakit.suitcase.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class InputPanel extends JPanel {
	private GridBagConstraints gbc;

	public InputPanel(String[] labels, JTextField[] textFields, String[] defaultText) {
		super(new GridBagLayout());

		if ((labels.length != textFields.length) || (textFields.length != defaultText.length)) {
			throw new IllegalArgumentException("Arrays have unequal length!");
		}

		this.gbc = LayoutDefault.getDefaultGbc();
		this.gbc.gridx = GridBagConstraints.RELATIVE;
		this.gbc.gridy = 0;

		buildLabelPanel(labels);
		buildTextPanel(textFields, defaultText);
	}

	public InputPanel
	(String[] labels, 
			JComboBox<String> sTableIdComboBox, 
			Set<String> textFields) {
		super(new GridBagLayout());

		this.gbc = LayoutDefault.getDefaultGbc();
		this.gbc.gridx = GridBagConstraints.RELATIVE;
		this.gbc.gridy = 0;

		buildLabelPanel(labels);
		buildJComboBoxPanel(sTableIdComboBox, textFields);
	}

	private void buildJComboBoxPanel(
			JComboBox<String> sTableIdComboBox, 
			Set<String> textFields) {
		JPanel labelPanel = new JPanel(new GridLayout(1, 1));
		gbc.weightx = 9;
		this.add(labelPanel, gbc);
		for (String s : textFields) {
			sTableIdComboBox.addItem(s);
		}

		labelPanel.add(sTableIdComboBox);
	}

	private void buildLabelPanel(String[] labels) {
		JPanel labelPanel = new JPanel(new GridLayout(labels.length, 1));
		gbc.weightx = 1;
		this.add(labelPanel, gbc);

		for (String label : labels) {
			labelPanel.add(new JLabel(label));
		}
	}

	private void buildTextPanel(JTextField[] textFields, String[] defaultText) {
		JPanel inputPanel = new JPanel(new GridLayout(textFields.length, 1));
		gbc.weightx = 9;
		this.add(inputPanel, gbc);

		for (int i = 0; i < textFields.length; i++) {
			if (!defaultText[i].isEmpty()) {
				textFields[i].setText(defaultText[i]);
			}
			textFields[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			inputPanel.add(textFields[i]);
		}
	}
}
