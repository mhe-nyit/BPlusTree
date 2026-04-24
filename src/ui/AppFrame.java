package ui;

import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;

public class AppFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private BPlusTree bPlusTree = new BPlusTree();
	private final ControlPanel controlPanel;
	private final ResultsPanel resultsPanel;

	private File loadedFile = null;

	public AppFrame() {
		setTitle("Part File Indexer");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setMinimumSize(new Dimension(900, 600));
		setPreferredSize(new Dimension(1050, 640));

		resultsPanel = new ResultsPanel();
		controlPanel = new ControlPanel(this);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controlPanel, resultsPanel);
		split.setDividerLocation(480);
		split.setDividerSize(2);
		split.setResizeWeight(0.45);
		split.setBorder(null);

		add(split);
		pack();
		setLocationRelativeTo(null);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (!bPlusTree.isEmpty()) {
					int choice = JOptionPane.showConfirmDialog(
							AppFrame.this,
							"Save changes to file before exiting?",
							"Save Changes",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (choice == JOptionPane.YES_OPTION) {
						saveData();
						bPlusTree.printStatistics();
						System.exit(0);
					} else if (choice == JOptionPane.NO_OPTION) {
						bPlusTree.printStatistics();
						System.exit(0);
					}
				} else {
					bPlusTree.printStatistics();
					System.exit(0);
				}
			}
		});
	}

	public void loadFile(File file) {
		try {
			bPlusTree = new BPlusTree();
			int loaded = FileIO.load(file, bPlusTree);
			loadedFile = file;
			controlPanel.setStatus("Loaded " + loaded + " records from " + file.getName());
			resultsPanel.showMessage("Start searching...");
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this,
					"Could not read file:\n" + ex.getMessage(),
					"File Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void search(String partId) {
		if (partId.isBlank()) { resultsPanel.showMessage("Enter a Part ID to search."); return; }
		String desc = bPlusTree.searchNode(partId.trim().toUpperCase());
		if (desc != null) {
			resultsPanel.showSingleResult(partId.trim().toUpperCase(), desc);
		} else {
			resultsPanel.showMessage("Part ID \"" + partId.trim().toUpperCase() + "\" not found.");
		}
	}

	public void insert(String partId, String description) {
		if (partId.isBlank()) { showError("Part ID cannot be empty."); return; }
		bPlusTree.insertNode(partId.trim().toUpperCase(), description.trim());
		controlPanel.setStatus("Inserted: " + partId.trim().toUpperCase());
		resultsPanel.showMessage("Record inserted: " + partId.trim().toUpperCase()
				+ " \u2014 " + description.trim());
	}

	public void delete(String partId) {
		if (partId.isBlank()) { showError("Part ID cannot be empty."); return; }
		boolean ok = bPlusTree.deleteNode(partId.trim().toUpperCase());
		if (ok) {
			controlPanel.setStatus("Deleted: " + partId.trim().toUpperCase());
			resultsPanel.showMessage("Record deleted: " + partId.trim().toUpperCase());
		} else {
			resultsPanel.showMessage("Part ID \"" + partId.trim().toUpperCase() + "\" not found.");
		}
	}

	public void update(String partId, String newDescription) {
		if (partId.isBlank()) { showError("Part ID cannot be empty."); return; }
		boolean ok = bPlusTree.updateNode(partId.trim().toUpperCase(), newDescription.trim());
		if (ok) {
			controlPanel.setStatus("Updated: " + partId.trim().toUpperCase());
			resultsPanel.showMessage("Updated: " + partId.trim().toUpperCase()
					+ " \u2014 " + newDescription.trim());
		} else {
			resultsPanel.showMessage("Part ID \"" + partId.trim().toUpperCase() + "\" not found.");
		}
	}

	public void getNextTen(String partId) {
		if (partId.isBlank()) { resultsPanel.showMessage("Enter a Part ID to start from."); return; }
		ArrayList<Product> list = bPlusTree.getNextTenNode(partId.trim().toUpperCase());
		if (list == null || list.isEmpty()) {
			resultsPanel.showMessage("No records found after \"" + partId.trim().toUpperCase() + "\".");
		} else {
			resultsPanel.showNextTen(list);
		}
	}

	public void showStats() {
		resultsPanel.showMessage(bPlusTree.getStatistics());
	}


	public void saveData() {
		if (loadedFile == null) {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Save Part File");
			if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
			loadedFile = fc.getSelectedFile();
		}
		try {
			FileIO.save(loadedFile, bPlusTree);
			controlPanel.setStatus("Saved to " + loadedFile.getName());
		} catch (FileNotFoundException ex) {
			showError("Could not save file:\n" + ex.getMessage());
		}
	}

	private void showError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
