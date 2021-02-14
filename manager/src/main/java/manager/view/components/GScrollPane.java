package manager.view.components;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

public class GScrollPane extends JScrollPane{
    public GScrollPane(){
        super();
        getVerticalScrollBar().setUI(new ScrollBarUI());
        getHorizontalScrollBar().setUI(new ScrollBarUI());
    }

    private static class ScrollBarUI extends BasicScrollBarUI{
        private final Color darkShadow = UIManager.getColor("ScrollBar.darkShadow");

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds){
            g.setColor(trackColor);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds){
            g.setColor(thumbColor);
            g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
        }

        @Override
        protected JButton createDecreaseButton(int orientation){
            return new ScrollBarButton(orientation);
        }

        @Override
        protected JButton createIncreaseButton(int orientation){
            return new ScrollBarButton(orientation);
        }

        private class ScrollBarButton extends JButton{
            private final int direction;

            public ScrollBarButton(int direction){
                super();
                setFocusPainted(false);
                this.direction = direction;
            }

            @Override
            public void paint(Graphics g){
                super.paint(g);
                final int w = getSize().width;
                final int h = getSize().height;
                final int size = Math.max(Math.min((h - 4) / 3, (w - 4) / 3), 2);
                paintTriangle(g, (w - size) / 2, (h - size) / 2, size, direction, isEnabled());
            }

            public Dimension getPreferredSize(){
                return new Dimension(16, 16);
            }

            private void paintTriangle(Graphics og, int x, int y, int size, int direction, boolean isEnabled){
                final Graphics g = og.create();
                int mid, i, j;

                size = Math.max(size, 2);
                mid = (size / 2) - 1;

                g.translate(x, y);
                if(isEnabled){
                    g.setColor(thumbHighlightColor);
                }else{
                    g.setColor(darkShadow);
                }

                switch(direction){
                    case NORTH:
                        for(i = 0; i < size; i++){
                            g.drawLine(mid - i, i, mid + i, i);
                        }
                        break;
                    case SOUTH:
                        j = 0;
                        for(i = size - 1; i >= 0; i--){
                            g.drawLine(mid - i, j, mid + i, j);
                            j++;
                        }
                        break;
                    case WEST:
                        for(i = 0; i < size; i++){
                            g.drawLine(i, mid - i, i, mid + i);
                        }
                        break;
                    case EAST:
                        j = 0;
                        for(i = size - 1; i >= 0; i--){
                            g.drawLine(j, mid - i, j, mid + i);
                            j++;
                        }
                        break;
                }
            }
        }
    }
}
