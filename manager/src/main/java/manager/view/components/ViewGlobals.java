package manager.view.components;

import javax.swing.UIManager;
import java.awt.Font;

public interface ViewGlobals{
    Font defaultFont = UIManager.getFont("defaultFont").deriveFont(Font.PLAIN, 12);
    Font mediumFont = UIManager.getFont("defaultFont").deriveFont(Font.PLAIN, 15);
    Font bigFont = UIManager.getFont("defaultFont").deriveFont(Font.PLAIN, 20);
}
