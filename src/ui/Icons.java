package ui;

import javax.swing.*;
import java.awt.*;

public class Icons {

    private Icons() {}

    public static ImageIcon magnifyingGlass(int size) {
        return scaled("/images/search.png", size);
    }

    public static ImageIcon clipboard(int size) {
        return scaled("/images/clipboard.png", size);
    }

    public static ImageIcon dragAndDrop(int size) {
        return scaled("/images/drag-and-drop.png", size);
    }

    private static ImageIcon scaled(String path, int size) {
        java.net.URL url = Icons.class.getResource(path);
        if (url == null) return new ImageIcon();
        Image img = new ImageIcon(url).getImage()
                        .getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
