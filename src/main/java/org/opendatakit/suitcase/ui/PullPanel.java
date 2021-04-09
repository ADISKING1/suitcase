package org.opendatakit.suitcase.ui;

import org.opendatakit.suitcase.model.CsvConfig;
import org.opendatakit.suitcase.model.ODKCsv;
import org.opendatakit.suitcase.net.AttachmentManager;
import org.opendatakit.suitcase.net.DownloadTask;
import org.opendatakit.suitcase.net.SuitcaseSwingWorker;
import org.opendatakit.suitcase.net.SyncWrapper;
import org.apache.wink.json4j.JSONException;
import org.opendatakit.suitcase.utils.FieldsValidatorUtils;
import org.opendatakit.suitcase.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

public class PullPanel extends JPanel implements PropertyChangeListener {
	private static final String DOWNLOAD_LABEL = "Download";
	private static final String DOWNLOADING_LABEL = "Downloading";
	private static final String SAVE_PATH_LABEL = "Save to";

	// ui components
	private JCheckBox sDownloadAttachment;
	private JCheckBox sApplyScanFmt;
	private JCheckBox sExtraMetadata;
	private JButton sPullButton;
	private JComboBox<String> sTableIdComboBox;
	private PathChooserPanel savePathChooser;

	// other instance vars
	private IOPanel parent;
	private AttachmentManager attachMngr;
	private ODKCsv csv;

	public PullPanel(IOPanel parent) {
		super(new GridBagLayout());

		this.parent = parent;
		this.attachMngr = null;
		this.csv = null;

		this.sDownloadAttachment = new JCheckBox();
		this.sApplyScanFmt = new JCheckBox();
		this.sExtraMetadata = new JCheckBox();
		this.sPullButton = new JButton();
		this.sTableIdComboBox = new JComboBox<>();
		// this.sTableIdComboBox.addActionListener(sTableIdComboBox);
		this.savePathChooser = new PathChooserPanel(SAVE_PATH_LABEL, FileUtils.getDefaultSavePath().toString());

		GridBagConstraints gbc = LayoutDefault.getDefaultGbc();
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;

		JPanel pullInputPanel;
		try {
			pullInputPanel = new InputPanel(new String[] { "Table ID" }, sTableIdComboBox,
					SyncWrapper.getInstance().updateTableList());
			gbc.weighty = 2;
			this.add(pullInputPanel, gbc);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}

		JPanel pullPrefPanel = new CheckboxPanel(
				new String[] { "Download attachments?", "Apply Scan formatting?", "Extra metadata columns?" },
				new JCheckBox[] { sDownloadAttachment, sApplyScanFmt, sExtraMetadata }, 3, 1);
		gbc.weighty = 5;
		this.add(pullPrefPanel, gbc);

		gbc.weighty = 1;
		this.add(this.savePathChooser, gbc);

		JPanel pullButtonPanel = new JPanel(new GridLayout(1, 1));
		buildPullButtonArea(pullButtonPanel);
		gbc.weighty = 2;
		gbc.insets = new Insets(10, 0, 0, 0);
		this.add(pullButtonPanel, gbc);
	}

	private void buildPullButtonArea(JPanel pullButtonPanel) {
		sPullButton.setText(DOWNLOAD_LABEL);
		sPullButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String error = FieldsValidatorUtils.checkDownloadFields(sTableIdComboBox.getSelectedItem().toString(),
						savePathChooser.getPath(), parent.getCloudEndpointInfo());

				if (error != null) {
					DialogUtils.showError(error, true);
				} else {
					// disable download button
					sPullButton.setEnabled(false);

					sPullButton.setText(DOWNLOADING_LABEL);

					CsvConfig config = new CsvConfig(sDownloadAttachment.isSelected(), sApplyScanFmt.isSelected(),
							sExtraMetadata.isSelected());

					if (attachMngr == null) {
						attachMngr = new AttachmentManager(parent.getCloudEndpointInfo(),
								sTableIdComboBox.getSelectedItem().toString(), savePathChooser.getPath());
					} else {
						attachMngr.setSavePath(savePathChooser.getPath());
					}

					// create a new csv instance when csv == null or when table id changed
					if (csv == null || !csv.getTableId().equals(sTableIdComboBox.getSelectedItem().toString())) {
						try {
							csv = new ODKCsv(attachMngr, parent.getCloudEndpointInfo(),
									sTableIdComboBox.getSelectedItem().toString());
						} catch (JSONException e1) {
							/* should never happen */ }
					}

					DownloadTask worker = new DownloadTask(parent.getCloudEndpointInfo(), csv, config, savePathChooser.getPath(),
							true);
					worker.addPropertyChangeListener(parent.getProgressBar());
					worker.addPropertyChangeListener(PullPanel.this);
					worker.execute();
				}
			}
		});
		pullButtonPanel.add(sPullButton);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getNewValue() != null && evt.getPropertyName().equals(SuitcaseSwingWorker.DONE_PROPERTY)) {
			// re-enable download button and restore its label
			sPullButton.setText(DOWNLOAD_LABEL);
			sPullButton.setEnabled(true);
		}
	}
}
