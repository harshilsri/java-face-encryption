
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

// Referenced classes of package com.equitysoft.ftpgo:
//            RemFile, ESUtils

class RemIconCell extends DefaultTableCellRenderer
{

    RemIconCell() 
    {
        diricon = new ImageIcon(ESUtils.getSystemImage("dir.gif"));
       docicon = new ImageIcon(ESUtils.getSystemImage("file1.gif"));
    }

    public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean flag, boolean flag1, int i, int j)
    {
        RemFile remfile = (RemFile)obj;
        if(remfile.isDirectory())
            setIcon(diricon);
        else
          setIcon(docicon);
        return super.getTableCellRendererComponent(jtable, remfile.getName(), flag, false, i, j);
    }

    ImageIcon diricon;
   ImageIcon docicon;
}
