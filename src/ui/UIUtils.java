package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class UIUtils {

	private UIUtils() {
	}

	public static final Color BG = new Color(0xF8, 0xFA, 0xFF);
	public static final Color NAVY = new Color(0x1E, 0x27, 0x61);
	public static final Color ACCENT = new Color(0x4F, 0xC3, 0xF7);
	public static final Color BTN_FG = Color.WHITE;
	public static final Color BORDER_CLR = new Color(0xCC, 0xD6, 0xF5);
	public static final Color STATUS_CLR = new Color(0x55, 0x66, 0x88);
	public static final Color ROW_ODD = new Color(0xF5, 0xF8, 0xFF);
	public static final Color ROW_EVEN = Color.WHITE;
	public static final Color HEADER_BG = new Color(0x1E, 0x27, 0x61);
	public static final Color MSG_CLR = new Color(0x88, 0x99, 0xBB);
	public static final Color BTN_DANGER = new Color(0x99, 0x44, 0x44);
	public static final Color DROPZONE_BG = new Color(0xF0, 0xF4, 0xFF);
	public static final Color DROPZONE_HOVER = new Color(0xDC, 0xEA, 0xFF);
	public static final Color SEARCH_BTN = new Color(0x29, 0x9A, 0xC8);

	public static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
	public static final Font BTN_FONT = new Font("Segoe UI", Font.BOLD, 13);
	public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
	public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 12);
	public static final Font BODY_FONT_LG = new Font("Segoe UI", Font.PLAIN, 13);
	public static final Font MONO_FONT = new Font("Consolas", Font.PLAIN, 13);

	public static JButton makeBtn(String label, Color bg) {
		JButton btn = new JButton(label);
		btn.setFont(BTN_FONT);
		btn.setBackground(bg);
		btn.setForeground(BTN_FG);
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setOpaque(true);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return btn;
	}

	public static JButton makeIconBtn(Icon icon) {
		JButton btn = new JButton(icon);
		btn.setBackground(ACCENT);
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setOpaque(true);
		btn.setPreferredSize(new Dimension(38, 32));
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return btn;
	}

	public static void styleTextField(JTextField field, String placeholder) {
		field.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		field.setForeground(Color.GRAY);
		field.setText(placeholder);
		field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(0xBB, 0xCC, 0xEE), 1, true),
				new EmptyBorder(4, 8, 4, 8)));

		field.addFocusListener(new java.awt.event.FocusAdapter() {
			@Override
			public void focusGained(java.awt.event.FocusEvent e) {
				if (field.getText().equals(placeholder)) {
					field.setText("");
					field.setForeground(Color.BLACK);
					field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				}
			}

			@Override
			public void focusLost(java.awt.event.FocusEvent e) {
				if (field.getText().isBlank()) {
					field.setText(placeholder);
					field.setForeground(Color.GRAY);
					field.setFont(new Font("Segoe UI", Font.ITALIC, 12));
				}
			}
		});
	}
}
