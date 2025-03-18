import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;
import javax.swing.*;

// Referenced classes of package com.equitysoft.ftpgo:
//            ESDialog, ESLabel, LibButton, ESUtils

class ESQ extends ESDialog
    implements KeyListener, ActionListener
{

    ESQ(String s, String s1, String s2)
    {
        super("An FTP-Go question.", true);
        super.jp.setBackground(Color.cyan.brighter());
        super.jp.setLayout(new FlowLayout());
        ESLabel eslabel = new ESLabel("  " + s + "  ");
        eslabel.setForeground(Color.black);
     eslabel.setIcon(new ImageIcon(ESUtils.getESURL("right2mouse.gif")));
        super.jp.add(eslabel);
        c1 = new LibButton(s1);
        super.jp.add(c1);
        c1.addActionListener(this);
        c2 = new LibButton(s2);
        super.jp.add(c2);
        c2.addActionListener(this);
        finishOff();
        c1.addKeyListener(this);
        c1.requestFocus();
    }

    static boolean ask(String s, String s1, String s2)
    {
        new ESQ(s, s1, s2);
        return result == c1;
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
        result = (LibButton)actionevent.getSource();
    }

    static LibButton result;
    static LibButton c1;
    static LibButton c2;
}
