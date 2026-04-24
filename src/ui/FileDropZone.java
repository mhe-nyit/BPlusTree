package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;

public class FileDropZone extends JPanel {

	private static final long serialVersionUID = 1L;

	public FileDropZone(AppFrame app) {
		setBackground(UIUtils.BG);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JLabel hint = new JLabel("Drag or import a file.");
		hint.setFont(new java.awt.Font("Segoe UI", java.awt.Font.ITALIC, 12));
		hint.setForeground(UIUtils.STATUS_CLR);
		hint.setAlignmentX(CENTER_ALIGNMENT);

		JButton importBtn = UIUtils.makeBtn("Import a file", UIUtils.NAVY);
		importBtn.setAlignmentX(CENTER_ALIGNMENT);
		importBtn.setMaximumSize(new Dimension(160, 32));
		importBtn.addActionListener(e -> openFileChooser(app));

		JPanel dropZone = new JPanel(new BorderLayout());
		dropZone.setBackground(new Color(0xF0, 0xF4, 0xFF));
		dropZone.setBorder(BorderFactory.createCompoundBorder(new LineBorder(UIUtils.BORDER_CLR, 1, true),
				new EmptyBorder(16, 16, 16, 16)));
		dropZone.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

		JLabel dropIcon = new JLabel(Icons.dragAndDrop(48), SwingConstants.CENTER);

		JLabel dropLabel = new JLabel("Drop partfile.txt here", SwingConstants.CENTER);
		dropLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.ITALIC, 12));
		dropLabel.setForeground(UIUtils.STATUS_CLR);

		dropZone.add(dropIcon, BorderLayout.CENTER);
		dropZone.add(dropLabel, BorderLayout.SOUTH);

		new DropTarget(dropZone, new DropTargetAdapter() {
			@Override
			public void dragEnter(DropTargetDragEvent dtde) {
				dropZone.setBackground(UIUtils.DROPZONE_HOVER);
				dropZone.repaint();
			}

			@Override
			public void dragExit(DropTargetEvent dte) {
				dropZone.setBackground(UIUtils.DROPZONE_BG);
				dropZone.repaint();
			}

			@Override
			public void drop(DropTargetDropEvent dtde) {
				dropZone.setBackground(UIUtils.DROPZONE_BG);
				try {
					dtde.acceptDrop(DnDConstants.ACTION_COPY);
					@SuppressWarnings("unchecked")
					List<File> files = (List<File>) dtde.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);
					if (!files.isEmpty())
						app.loadFile(files.get(0));
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(app, "Could not read dropped file:\n" + ex.getMessage(), "Drop Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		add(hint);
		add(Box.createVerticalStrut(6));
		add(importBtn);
		add(Box.createVerticalStrut(14));
		add(dropZone);
	}

	private void openFileChooser(AppFrame app) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open Part File");
		fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text files (*.txt)", "txt"));
		if (fc.showOpenDialog(app) == JFileChooser.APPROVE_OPTION)
			app.loadFile(fc.getSelectedFile());
	}
}
