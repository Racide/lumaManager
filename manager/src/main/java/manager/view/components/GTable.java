package manager.view.components;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class GTable extends JTable{
    public final TableColumnAdjuster tableColumnAdjuster;

    public GTable(){
        setAutoCreateRowSorter(true);
        tableColumnAdjuster = new TableColumnAdjuster(this);
        tableColumnAdjuster.setColumnHeaderIncluded(true);
    }
}
