import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

// Referenced classes of package com.equitysoft.ftpgo:
//            ESUtils, Bar, FTPGo

class ESDialog extends JDialog
{

    ESDialog(String s)
    {
        this(s, false);
       ESUtils.disableDisplay();
    }

    ESDialog(String s, boolean flag)
    {
        super(Bar.handle, " " + s, flag);
        Bar.handle.repaint();
        jp = new JPanel();
        jp.setOpaque(true);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(jp, "Center");
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent windowevent)
            {
                finalCall();
            }

        });
    }

    void finishOff()
    {
        pack();
        Dimension dimension = getSize();
        Dimension dimension1 = Bar.handle.getSize();
        Dimension dimension2 = FTPGo.screendim;
        Point point = Bar.handle.getLocation();
        if(dimension.width < dimension1.width && dimension.height + point.y < FTPGo.screendim.height)
        {
            setLocation(point.x + (dimension1.width - dimension.width) / 2, point.y);
        } else
        {
            if(dimension.width > dimension2.width)
            {
                point.x = 0;
                dimension.width = dimension2.width;
            } else
            {
                point.x = (dimension2.width - dimension.width) / 2;
            }
            if(dimension.height > dimension2.height)
            {
                point.y = 0;
                dimension.height = dimension2.height;
            } else
            {
                point.y = (dimension2.height - dimension.height) / 2;
            }
            setLocation(point);
            setSize(dimension);
        }
//		setVisible(true);
}

    void finalCall()
    {
        closeDialog();
    }

    void closeDialog()
    {
        ESUtils.enableDisplay();
        setVisible(false);
    }

    boolean shown;
    JPanel jp;
}

