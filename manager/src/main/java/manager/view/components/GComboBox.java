package manager.view.components;

import javax.swing.JComboBox;

public class GComboBox<E> extends JComboBox<E>{
    @Override
    public void setSelectedIndex(int anIndex){
        super.setSelectedIndex(anIndex);
        repaint();
    }
}
