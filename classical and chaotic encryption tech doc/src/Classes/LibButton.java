import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;
import javax.swing.*;

// Referenced classes of package com.equitysoft.ftpgo:
//            ESUtils

class LibButton extends JButton
{

    LibButton(String s, String s1, ActionListener actionlistener)
    {
        if(s != null)
        {
            setText(s);
            setFont(ESUtils.MSG_FONT);
        }
        setOpaque(true);
        orig = getBackground();
        if(s1 != null)
            setIcon(new ImageIcon(ESUtils.getESURL(s1)));
        if(actionlistener != null)
            addActionListener(actionlistener);
        addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseevent)
            {
                if(isEnabled())
                {
                    setBackground(orig.darker());
                    repaint();
                }
            }

            public void mouseExited(MouseEvent mouseevent)
            {
                if(isEnabled())
                {
                    setBackground(orig);
                    repaint();
                }
            }

        });
    }

    LibButton(String s) 
    {
	     this(s, "bluepage.gif", null);
		    }

    LibButton(String s, ActionListener actionlistener)
    {
				       this(s, "bluepage.gif", actionlistener);
		
    }

    LibButton(String s, String s1)
    {
        this(s, s1, null);
    }

    public void setEnabled(boolean flag)
    {
        if(!flag)
            setBackground(orig);
        super.setEnabled(flag);
    }

    Color orig;
}
