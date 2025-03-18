import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Referenced classes of package com.equitysoft.ftpgo:
//            ESDialog, ESLabel, LibButton, ESUtils

class ESZap extends ESDialog
    implements KeyListener
{

    ESZap(String s) 
    {
        super("An FTP-Go error message.", true);
        super.jp.setBackground(Color.red);
        super.jp.setLayout(new FlowLayout());
		try
		{
        ESLabel eslabel = new ESLabel("  " + s + "  ");
        eslabel.setIcon(new ImageIcon(ESUtils.getESURL("folder_16_pad.gif")));
        super.jp.add(eslabel);
        LibButton libbutton = new LibButton("Continue");
        super.jp.add(libbutton);
        libbutton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                closeDialog();
            }

        });
        finishOff();
        libbutton.addKeyListener(this);
        libbutton.requestFocus();
		}catch(Exception ecx){}
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
}
