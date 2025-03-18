import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Referenced classes of package com.equitysoft.ftpgo:
//            ESDialog, ESLabel, LibButton, ESUtils

class ESMsg extends ESDialog
    implements KeyListener, ActionListener
{

    ESMsg(String s)
    {
        super("An FTP-Go message", true);
        super.jp.setBackground(Color.magenta.brighter());
        super.jp.setLayout(new FlowLayout());
        ESLabel eslabel = new ESLabel("  " + s + "  ");
        eslabel.setForeground(Color.black);
     eslabel.setIcon(new ImageIcon(ESUtils.getESURL("right2mouse.gif")));
        super.jp.add(eslabel);
        LibButton libbutton = new LibButton("Continue");
        super.jp.add(libbutton);
        libbutton.addActionListener(this);
        libbutton.addKeyListener(this);
        finishOff();
        libbutton.requestFocus();
    }

    public void keyTyped(KeyEvent keyevent)
    {
    }

    public void keyReleased(KeyEvent keyevent)
    {
    }

    public void keyPressed(KeyEvent keyevent)
    {
        if(keyevent.getKeyCode() == 10)
            closeDialog();
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        closeDialog();
    }
}
