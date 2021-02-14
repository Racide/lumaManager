package manager.view.components;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class GSplitPane extends JSplitPane{
    public GSplitPane(){
        setResizeWeight(1.0);
        setContinuousLayout(true);
        setDividerSize(5);
        setUI(new BasicSplitPaneUI(){
            @Override
            public BasicSplitPaneDivider createDefaultDivider(){
                return new BasicSplitPaneDivider(this){
                    private static final long serialVersionUID = -2173287749704570315L;

                    @Override
                    public void setBorder(Border border){
                        super.setBorder(BorderFactory.createEmptyBorder());
                    }

                    @Override
                    public void paint(Graphics og){
                        final Graphics g = og.create();
                        final int centerY = getSize().height / 2, start = 0, offs = getSize().width;
                        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        g.setColor(UIManager.getColor("SplitPane.highlight"));
                        g.fillOval(start, centerY - 13, offs, offs);
                        g.fillOval(start, centerY, offs, offs);
                        g.fillOval(start, centerY + 13, offs, offs);
                        super.paint(g);
                    }
                };
            }
        });
    }
}
