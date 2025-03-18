import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.*;
import javax.swing.text.JTextComponent;

// Referenced classes of package com.equitysoft.ftpgo:
//            LogManager, ESUtils

class LogWindow extends JPanel
    implements ComponentListener
{

    LogWindow()
    {
        setLayout(new BorderLayout());
        JScrollPane jscrollpane = new JScrollPane();
        add(jscrollpane, "Center");
        jscrollpane.getViewport().add(jta = new JTextArea());
        jta.setEditable(false);
        addComponentListener(this);
    }

    public void componentResized(ComponentEvent componentevent)
    {
    }

    public void componentMoved(ComponentEvent componentevent)
    {
    }

    public void componentShown(ComponentEvent componentevent)
    {
        jta.setText(LogManager.getContents());
    }

    public void componentHidden(ComponentEvent componentevent)
    {
        jta.setText(ESUtils.EMPTY);
    }

    JTextArea jta;
}
