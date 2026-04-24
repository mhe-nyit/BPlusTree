package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class RecordDialog {

    private RecordDialog() {}

    public static void show(AppFrame app, String title, boolean isUpdate) {
        JDialog dialog = new JDialog(app, title, true);
        dialog.setResizable(false);

        JPanel content = new JPanel();
        content.setBackground(UIUtils.BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 24, 16, 24));

        JLabel idLabel = new JLabel("Part ID:");
        idLabel.setFont(UIUtils.LABEL_FONT);
        idLabel.setForeground(UIUtils.NAVY);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField idField = new JTextField(20);
        UIUtils.styleTextField(idField, "e.g. AAA-077");
        idField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(UIUtils.LABEL_FONT);
        descLabel.setForeground(UIUtils.NAVY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField descField = new JTextField(20);
        UIUtils.styleTextField(descField, "e.g. ORDER A-407-PC");
        descField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton confirmBtn = UIUtils.makeBtn("Confirm", UIUtils.NAVY);
        JButton cancelBtn  = UIUtils.makeBtn("Cancel",  UIUtils.BTN_DANGER);
        confirmBtn.setPreferredSize(new Dimension(90, 30));
        cancelBtn.setPreferredSize(new Dimension(80, 30));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(UIUtils.BG);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(cancelBtn);
        btnRow.add(confirmBtn);

        confirmBtn.addActionListener(e -> {
            String id   = idField.getText().trim();
            String desc = descField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Part ID cannot be empty.", "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (isUpdate) app.update(id, desc);
            else          app.insert(id, desc);
            dialog.dispose();
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        // Enter in either field triggers confirm
        idField.addActionListener(e -> confirmBtn.doClick());
        descField.addActionListener(e -> confirmBtn.doClick());

        content.add(idLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(idField);
        content.add(Box.createVerticalStrut(12));
        content.add(descLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(descField);
        content.add(Box.createVerticalStrut(16));
        content.add(btnRow);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(app);
        dialog.setVisible(true);
    }
}
