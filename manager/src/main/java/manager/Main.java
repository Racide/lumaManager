package manager;

import com.formdev.flatlaf.FlatLightLaf;
import manager.control.Controller;
import manager.model.LaunchParams;
import manager.view.components.LAFSettings;
import org.tinylog.Logger;

import java.util.Arrays;
import java.util.Optional;

public final class Main{
    public static void main(String[] args){
        LaunchParams.createLaunchParams(args);
        LAFSettings.set();
        @SuppressWarnings("unused") Controller controller = new Controller();
    }
}
