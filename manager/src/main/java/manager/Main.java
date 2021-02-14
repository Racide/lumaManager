package manager;

import manager.control.Controller;
import manager.view.components.LAFSettings;

public final class Main{
    public static void main(String[] args){
        LAFSettings.set();
        @SuppressWarnings("unused") Controller controller = new Controller();
    }
}
