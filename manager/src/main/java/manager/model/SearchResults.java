package manager.model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchResults extends AbstractTableModel{
    private final List<SteamApp> steamApps;
    public final Status status;

    public enum Status{
        OK(""), WARN("warning"), ERROR("error");
        public final String value;

        Status(String value){
            this.value = value;
        }
    }

    public SearchResults(Status status){
        this(status, Collections.emptyList());
    }

    public SearchResults(Status status, List<SteamApp> steamApps){
        this.status = status;
        this.steamApps = Collections.unmodifiableList(new ArrayList<>(steamApps));
    }

    public SteamApp getSteamApp(int index){
        return steamApps.get(index);
    }

    @Override
    public int getRowCount(){
        return steamApps.size();
    }

    @Override
    public int getColumnCount(){
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex){
        switch(columnIndex){
            case 0:
                return steamApps.get(rowIndex).id;
            case 1:
                return steamApps.get(rowIndex).title;
            default:
                return steamApps.get(rowIndex).type;
        }
    }

    @Override
    public String getColumnName(int columnIndex){
        switch(columnIndex){
            case 0:
                return "App ID";
            case 1:
                return "Title";
            default:
                return "Type";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex){
        switch(columnIndex){
            case 0:
                return Long.class;
            case 2:
                return SteamApp.Type.class;
            default:
                return String.class;
        }
    }
}
