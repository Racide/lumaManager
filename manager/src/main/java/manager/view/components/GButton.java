package manager.view.components;

import javax.swing.JButton;
import java.awt.Cursor;

public class GButton extends JButton{
    private static final long serialVersionUID = -8911635604784065891L;

    public GButton(String text){
        super(text);

        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
