package manager.view.components;

import com.formdev.flatlaf.FlatDarkLaf;
import org.tinylog.Logger;

import javax.swing.UIManager;

public class LAFSettings{
    public static void set(){
        try{
            UIManager.setLookAndFeel(new FlatDarkLaf());
        }catch(Exception ex){
            Logger.error(ex, "failed to load LAF");
            System.exit(1);
        }

        UIManager.put("ScrollBar.showButtons", true);
        UIManager.put("ScrollBar.width", 16);
        UIManager.put("ProgressBar.cycleTime", 1000);
    }
}
