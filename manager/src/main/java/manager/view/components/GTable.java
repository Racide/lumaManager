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
        super();
        setShowVerticalLines(false);
        setShowHorizontalLines(false);
        setRowHeight(22);
        setBorder(BorderFactory.createEmptyBorder());
        setFillsViewportHeight(true);
        setAutoCreateRowSorter(true);
        setRowMargin(0);
        tableColumnAdjuster = new TableColumnAdjuster(this);
        tableColumnAdjuster.setColumnHeaderIncluded(true);
    }

    @Override
    public TableCellRenderer getCellRenderer(int rRow, int rColumn){
        TableCellRenderer defaultCellRenderer = super.getCellRenderer(rRow, rColumn);
        return (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) -> {
            final JLabel lb = (JLabel) defaultCellRenderer.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column);
            lb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, column == 0 ? 0 : 1, 0,
                    //(row == getRowCount() - 1 && getHeight() > getParent().getHeight()) ? 0 : 1,
                    0, table.getGridColor()), BorderFactory.createEmptyBorder(0, 5, 0, 2)));
            return lb;
        };
    }

    @Override
    public void setTableHeader(JTableHeader tableHeader){
        final TableCellRenderer defaultHeaderRenderer = tableHeader.getDefaultRenderer();
        tableHeader.setDefaultRenderer((table, value, isSelected, hasFocus, row, column) -> {
            final JLabel lb = (JLabel) defaultHeaderRenderer.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column);
            lb.setBackground(table.getBackground());
            lb.setForeground(table.getForeground());
            lb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,
                    column == 0 ? 0 : 1,
                    1,
                    0,
                    table.getGridColor()), BorderFactory.createEmptyBorder(0, 0, 2, 0)));
            return lb;
        });
        super.setTableHeader(tableHeader);
    }

    @Override
    public void setColumnModel(TableColumnModel columnModel){
        columnModel.setColumnMargin(0);
        super.setColumnModel(columnModel);
    }
}
