import java.awt.Container;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.text.JTextComponent;

// Referenced classes of package com.equitysoft.ftpgo:
//            ESUtils

class MsgBox extends JPanel
{

    MsgBox(String s, int i, boolean flag)
    {
        super(false);
        setLayout(new FlowLayout(1, 3, 3));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), s));
        if(!flag)
        {
            add(txt = new JTextField(i));
            txt.setFont(ESUtils.MSG_FONT);
        } else
        {
            add(pw = new JPasswordField(i));
            pw.setFont(ESUtils.MSG_FONT);
        }
    }

    MsgBox(String s)
    {
        this(s, 20, false);
    }

    void setText(String s)
    {
        if(txt != null)
            txt.setText(s);
        else
            pw.setText(s);
    }

    String getText()
    {
        if(txt != null)
            return txt.getText();
        else
            return new String(pw.getPassword());
    }

    void setFocus()
    {
        pw.requestFocus();
    }

    void setEditable(boolean flag)
    {
        if(txt != null)
        {
            txt.setEditable(flag);
        } else
        {
            if(flag)
                pw.setOpaque(true);
            else
                pw.setOpaque(false);
            pw.setEnabled(flag);
        }
    }

    boolean isEmpty()
    {
        return txt.getText().equals(ESUtils.EMPTY);
    }

    JTextField txt;
    JPasswordField pw;
}
