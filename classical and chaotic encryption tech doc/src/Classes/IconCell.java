import java.awt.Component;
import java.io.File;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

// Referenced classes of package com.equitysoft.ftpgo:
//            ESUtils

class IconCell extends DefaultTableCellRenderer
{

    IconCell() 
    {
     diricon = new ImageIcon(ESUtils.getSystemImage("dir.gif"));
     docicon = new ImageIcon(ESUtils.getSystemImage("file1.gif"));
    }

    public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean flag, boolean flag1, int i, int j)
    {
        File file = (File)obj;
        if(file.isDirectory())
            setIcon(diricon);
        else
            setIcon(docicon);
        return super.getTableCellRendererComponent(jtable, file.getName(), flag, false, i, j);
    }

  ImageIcon diricon;
   ImageIcon docicon;
}
