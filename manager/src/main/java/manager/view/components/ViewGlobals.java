package manager.view.components;

import java.awt.Color;
import java.awt.Font;

public interface ViewGlobals{
    Font defaultFont = new Font("Segoe UI", Font.PLAIN, 15);
    Font mediumFont = defaultFont.deriveFont(Font.PLAIN, 20);
    Font bigFont = defaultFont.deriveFont(Font.PLAIN, 30);
    Color DARK_CHARCOAL = new Color(51, 51, 51);
}
