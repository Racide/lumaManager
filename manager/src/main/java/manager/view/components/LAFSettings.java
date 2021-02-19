package manager.view.components;

import com.formdev.flatlaf.FlatDarkLaf;
import manager.model.LaunchParams;
import org.tinylog.Logger;

import javax.swing.UIManager;

public class LAFSettings{
    public static void set(){
        if(LaunchParams.isDecorated()){
            System.setProperty("flatlaf.useWindowDecorations", "true");
        }
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
