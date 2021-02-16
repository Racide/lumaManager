package manager.view.components;

import org.tinylog.Logger;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Color;

import static manager.view.components.ViewGlobals.defaultFont;

public class LAFSettings{
    public static void set(){
        try{
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }catch(UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException | ClassNotFoundException ex){
            Logger.error(ex, "LAF loading failed");
        }

        UIManager.put("Panel.foreground", Color.WHITE);
        UIManager.put("Panel.background", Color.DARK_GRAY);
        UIManager.put("ScrollPane.background", Color.GRAY);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("ScrollBar.thumb", Color.LIGHT_GRAY);
        UIManager.put("ScrollBar.track", Color.GRAY);
        UIManager.put("ScrollBar.thumbHighlight", Color.WHITE);
        UIManager.put("SplitPane.background", Color.DARK_GRAY);
        UIManager.put("SplitPane.highlight", Color.GRAY);
        UIManager.put("SplitPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("Viewport.background", Color.DARK_GRAY);
        UIManager.put("Label.foreground", Color.WHITE);
        UIManager.put("TextField.background", Color.GRAY);
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("TextField.border", BorderFactory.createEmptyBorder());
        UIManager.put("Button.background", Color.GRAY);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.select", Color.LIGHT_GRAY);
        UIManager.put("Button.focus", Color.LIGHT_GRAY);
        UIManager.put("Button.border", BorderFactory.createEmptyBorder(0, 5, 0, 5));
        UIManager.put("ComboBox.background", Color.GRAY);
        UIManager.put("ComboBox.foreground", Color.WHITE);
        UIManager.put("ComboBox.selectionBackground", Color.LIGHT_GRAY);
        UIManager.put("List.background", Color.GRAY);
        UIManager.put("List.foreground", Color.WHITE);
        UIManager.put("List.selectionBackground", Color.LIGHT_GRAY);
        UIManager.put("List.focusCellHighlightBorder", BorderFactory.createEmptyBorder(1, 1, 1, 1));
        UIManager.put("Table.background", Color.GRAY);
        UIManager.put("Table.foreground", Color.WHITE);
        UIManager.put("Table.gridColor", Color.DARK_GRAY);
        UIManager.put("Table.selectionBackground", Color.LIGHT_GRAY);
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder(1, 1, 1, 1));
        UIManager.put("TableHeader.background", Color.DARK_GRAY);
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("ProgressBar.background", Color.GRAY);
        UIManager.put("ProgressBar.foreground", Color.LIGHT_GRAY);
        UIManager.put("ProgressBar.cycleTime", 1000);
        UIManager.put("OptionPane.background", Color.DARK_GRAY);
        UIManager.put("OptionPane.messageForeground", Color.WHITE);

        /*try{
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
                    Gui.class.getResourceAsStream("/OpenSans/OpenSans-BoldItalic.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
                    Gui.class.getResourceAsStream("/OpenSans/OpenSans-ExtraBold.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
                    Gui.class.getResourceAsStream("/OpenSans/OpenSans-ExtraBoldItalic.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
                    Gui.class.getResourceAsStream("/OpenSans/OpenSans-Italic.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
                    Gui.class.getResourceAsStream("/OpenSans/OpenSans-Light.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
                    Gui.class.getResourceAsStream("/OpenSans/OpenSans-LightItalic.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
                    Gui.class.getResourceAsStream("/OpenSans/OpenSans-Regular.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
                    Gui.class.getResourceAsStream("/OpenSans/OpenSans-SemiBold.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
                    Gui.class.getResourceAsStream("/OpenSans/OpenSans-SemiBoldItalic.ttf")));
        }catch(IOException | FontFormatException ex){
            // log "Gui: failed registering fonts"
        }*/

        UIManager.put("OptionPane.messageFont", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("List.font", defaultFont);
        UIManager.put("ProgressBar.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("TableHeader.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
    }
}
