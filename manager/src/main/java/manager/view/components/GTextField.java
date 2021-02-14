package manager.view.components;

import manager.model.Globals;

import javax.swing.JTextField;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class GTextField extends JTextField implements FocusListener{
    private static final long serialVersionUID = 7765287227923397268L;

    public GTextField(){
        super();
        addFocusListener(this);
        focusLost(null);
    }

    @Override
    public void focusGained(FocusEvent e){
        setForeground(Globals.DARK_CHARCOAL);
        setBackground(Color.LIGHT_GRAY);
    }

    @Override
    public void focusLost(FocusEvent e){
        setText(getText().trim());
        if(getText().length() < 1){
            setForeground(Color.LIGHT_GRAY);
        }else{
            setForeground(Color.WHITE);
        }
        setBackground(Color.GRAY);
    }
}
