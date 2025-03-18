import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class SizeCell extends DefaultTableCellRenderer
{

    SizeCell()
    {
        setHorizontalAlignment(4);
    }

    public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean flag, boolean flag1, int i, int j)
    {
        return super.getTableCellRendererComponent(jtable, obj, flag, false, i, j);
    }
}
