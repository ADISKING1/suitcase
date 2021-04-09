package org.opendatakit.suitcase.ui;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;

public class MainFrame extends JFrame {

	private static MainFrame frame;
	
	private MainFrame(String title) throws HeadlessException {
		super(title);
	}

	public static MainFrame getInstance() {
		if(frame == null)
			frame = new MainFrame("org.opendatakit.suitcase.Suitcase");
		
		return frame;
	}
}
