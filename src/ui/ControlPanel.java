package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ControlPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final AppFrame app;

	private final JTextField searchField = new JTextField();
	private final JLabel statusLabel = new JLabel(" ");

	public ControlPanel(AppFrame app) {
		this.app = app;
		setBackground(UIUtils.BG);
		setLayout(new BorderLayout(0, 0));
		setBorder(new EmptyBorder(18, 18, 18, 12));

		JLabel title = new JLabel("Parts Catalog");
		title.setFont(new Font("Segoe UI", Font.BOLD, 18));
		title.setForeground(UIUtils.NAVY);
		title.setBorder(new EmptyBorder(0, 0, 14, 0));
		add(title, BorderLayout.NORTH);

		JPanel centre = new JPanel();
		centre.setBackground(UIUtils.BG);
		centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));

		centre.add(buildSearchRow());
		centre.add(Box.createVerticalStrut(16));
		centre.add(buildButtonGrid());
		centre.add(Box.createVerticalStrut(16));
		centre.add(buildSeparator());
		centre.add(Box.createVerticalStrut(12));
		centre.add(new FileDropZone(app));

		add(centre, BorderLayout.CENTER);

		statusLabel.setFont(UIUtils.BODY_FONT);
		statusLabel.setForeground(UIUtils.STATUS_CLR);

		JButton statsBtn = new JButton(Icons.clipboard(18));
		statsBtn.setBackground(UIUtils.BG);
		statsBtn.setFocusPainted(false);
		statsBtn.setBorderPainted(false);
		statsBtn.setOpaque(true);
		statsBtn.setPreferredSize(new Dimension(24, 24));
		statsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		statsBtn.setToolTipText("Show Statistics");
		statsBtn.addActionListener(e -> app.showStats());

		JPanel footer = new JPanel(new BorderLayout(8, 0));
		footer.setBackground(UIUtils.BG);
		footer.setBorder(new EmptyBorder(8, 0, 0, 0));
		footer.add(statusLabel, BorderLayout.CENTER);
		footer.add(statsBtn, BorderLayout.EAST);
		add(footer, BorderLayout.SOUTH);
	}

	private JPanel buildSearchRow() {
		JPanel row = new JPanel(new BorderLayout(6, 0));
		row.setBackground(UIUtils.BG);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

		JLabel lbl = new JLabel("Search Part: ");
		lbl.setFont(UIUtils.LABEL_FONT);
		lbl.setForeground(UIUtils.NAVY);

		UIUtils.styleTextField(searchField, "Enter a Part ID");
		searchField.setFont(UIUtils.BODY_FONT);

		JButton searchBtn = UIUtils.makeIconBtn(Icons.magnifyingGlass(16));
		searchBtn.setBackground(UIUtils.SEARCH_BTN);
		searchBtn.addActionListener(e -> triggerSearch());
		searchField.addActionListener(e -> triggerSearch());

		row.add(lbl, BorderLayout.WEST);
		row.add(searchField, BorderLayout.CENTER);
		row.add(searchBtn, BorderLayout.EAST);
		return row;
	}

	private void triggerSearch() {
		String text = searchField.getText().trim();
		if (text.isEmpty())
			return;
		app.search(text);
	}

	private JPanel buildButtonGrid() {
		JPanel grid = new JPanel(new GridLayout(2, 2, 10, 10));
		grid.setBackground(UIUtils.BG);
		grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

		JButton btnInsert = UIUtils.makeBtn("Insert", UIUtils.NAVY);
		JButton btnDelete = UIUtils.makeBtn("Delete", UIUtils.NAVY);
		JButton btnUpdate = UIUtils.makeBtn("Update", UIUtils.NAVY);
		JButton btnNext10 = UIUtils.makeBtn("Next 10 Results", UIUtils.NAVY);

		btnInsert.addActionListener(e -> RecordDialog.show(app, "Insert New Record", false));
		btnDelete.addActionListener(e -> promptDelete());
		btnUpdate.addActionListener(e -> RecordDialog.show(app, "Update Existing Record", true));
		btnNext10.addActionListener(e -> {
			String text = searchField.getText().trim();
			if (text.isEmpty()) {
				JOptionPane.showMessageDialog(app, "Enter a Part ID in the search field first.", "Next 10",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			app.getNextTen(text);
		});

		grid.add(btnInsert);
		grid.add(btnDelete);
		grid.add(btnUpdate);
		grid.add(btnNext10);
		return grid;
	}

	private void promptDelete() {
		String id = searchField.getText().trim();
		if (id.isEmpty()) {
			id = JOptionPane.showInputDialog(app, "Enter Part ID to delete:", "Delete Record",
					JOptionPane.PLAIN_MESSAGE);
			if (id == null || id.isBlank())
				return;
		}
		int confirm = JOptionPane.showConfirmDialog(app, "Delete record \"" + id.toUpperCase() + "\"?",
				"Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirm == JOptionPane.YES_OPTION) {
			app.delete(id);
		}
	}

	private JPanel buildSeparator() {
		JPanel line = new JPanel();
		line.setBackground(UIUtils.BORDER_CLR);
		line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		return line;
	}

	public void setStatus(String msg) {
		statusLabel.setText(msg);
	}
}
