package ui;

import core.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

public class ResultsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final JPanel contentArea = new JPanel();
	private final JScrollPane scroll;

	public ResultsPanel() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(18, 12, 18, 18));

		contentArea.setBackground(Color.WHITE);
		contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));

		scroll = new JScrollPane(contentArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBorder(null);
		scroll.getViewport().setBackground(Color.WHITE);

		add(scroll, BorderLayout.CENTER);

		showMessage("No file loaded. Start building....");
	}

	public void showMessage(String text) {
		contentArea.removeAll();

		JLabel lbl = new JLabel("<html><div style='text-align:center'>" + text + "</div></html>",
				SwingConstants.CENTER);
		lbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
		lbl.setForeground(UIUtils.MSG_CLR);
		lbl.setAlignmentX(CENTER_ALIGNMENT);

		contentArea.add(Box.createVerticalGlue());
		contentArea.add(lbl);
		contentArea.add(Box.createVerticalGlue());

		refresh();
	}

	public void showSingleResult(String partId, String description) {
		contentArea.removeAll();

		contentArea.add(sectionHeader("Search Result"));
		contentArea.add(Box.createVerticalStrut(10));
		contentArea.add(buildSingleCard(partId, description));
		contentArea.add(Box.createVerticalGlue());

		refresh();
	}

	public void showNextTen(ArrayList<Product> products) {
		contentArea.removeAll();

		contentArea.add(sectionHeader("Next " + products.size() + " Results"));
		contentArea.add(Box.createVerticalStrut(10));
		contentArea.add(buildTableHeader());

		for (int i = 0; i < products.size(); i++) {
			Product p = products.get(i);
			contentArea.add(buildTableRow(p.getId(), p.getDescription(), i % 2 == 0));
		}

		contentArea.add(Box.createVerticalGlue());
		refresh();
	}

	private JPanel sectionHeader(String title) {
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(Color.WHITE);
		p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
		p.setAlignmentX(LEFT_ALIGNMENT);

		JLabel lbl = new JLabel(title);
		lbl.setFont(UIUtils.TITLE_FONT);
		lbl.setForeground(UIUtils.NAVY);
		p.add(lbl, BorderLayout.WEST);

		JSeparator sep = new JSeparator();
		sep.setForeground(UIUtils.BORDER_CLR);
		p.add(sep, BorderLayout.SOUTH);
		return p;
	}

	private JPanel buildSingleCard(String partId, String description) {
		JPanel card = new JPanel();
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBackground(UIUtils.ROW_ODD);
		card.setBorder(BorderFactory.createCompoundBorder(new LineBorder(UIUtils.BORDER_CLR, 1, true),
				new EmptyBorder(14, 16, 14, 16)));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
		card.setAlignmentX(LEFT_ALIGNMENT);

		JLabel idLabel = new JLabel("Part ID");
		idLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
		idLabel.setForeground(UIUtils.MSG_CLR);
		idLabel.setAlignmentX(LEFT_ALIGNMENT);

		JLabel idValue = new JLabel(partId);
		idValue.setFont(UIUtils.MONO_FONT);
		idValue.setForeground(UIUtils.NAVY);
		idValue.setAlignmentX(LEFT_ALIGNMENT);

		JLabel descLabel = new JLabel("Description");
		descLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
		descLabel.setForeground(UIUtils.MSG_CLR);
		descLabel.setAlignmentX(LEFT_ALIGNMENT);

		JLabel descValue = new JLabel(description.isEmpty() ? "(no description)" : description);
		descValue.setFont(UIUtils.BODY_FONT_LG);
		descValue.setForeground(Color.DARK_GRAY);
		descValue.setAlignmentX(LEFT_ALIGNMENT);

		card.add(idLabel);
		card.add(Box.createVerticalStrut(2));
		card.add(idValue);
		card.add(Box.createVerticalStrut(8));
		card.add(descLabel);
		card.add(Box.createVerticalStrut(2));
		card.add(descValue);
		return card;
	}

	private JPanel buildTableHeader() {
		JPanel row = new JPanel(new GridLayout(1, 2, 0, 0));
		row.setBackground(UIUtils.HEADER_BG);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		row.setAlignmentX(LEFT_ALIGNMENT);

		row.add(headerCell("Part ID"));
		row.add(headerCell("Description"));
		return row;
	}

	private JLabel headerCell(String text) {
		JLabel lbl = new JLabel(text);
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lbl.setForeground(Color.WHITE);
		lbl.setBorder(new EmptyBorder(4, 10, 4, 10));
		return lbl;
	}

	private JPanel buildTableRow(String partId, String description, boolean odd) {
		JPanel row = new JPanel(new GridLayout(1, 2, 0, 0));
		row.setBackground(odd ? UIUtils.ROW_ODD : UIUtils.ROW_EVEN);
		row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER_CLR));
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		row.setAlignmentX(LEFT_ALIGNMENT);

		JLabel idLbl = new JLabel(partId);
		idLbl.setFont(UIUtils.MONO_FONT);
		idLbl.setForeground(UIUtils.NAVY);
		idLbl.setBorder(new EmptyBorder(4, 10, 4, 10));

		JLabel descLbl = new JLabel(description.isEmpty() ? "\u2014" : description);
		descLbl.setFont(UIUtils.BODY_FONT_LG);
		descLbl.setForeground(Color.DARK_GRAY);
		descLbl.setBorder(new EmptyBorder(4, 10, 4, 10));

		row.add(idLbl);
		row.add(descLbl);
		return row;
	}

	private void refresh() {
		contentArea.revalidate();
		contentArea.repaint();
		scroll.getVerticalScrollBar().setValue(0);
	}
}
