package manager.view.components;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public interface AncestorAdded extends AncestorListener{
    @Override
    default void ancestorRemoved(AncestorEvent event){

    }

    @Override
    default void ancestorMoved(AncestorEvent event){

    }
}
