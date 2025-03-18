
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventObject;
import javax.swing.*;

// Referenced classes of package com.equitysoft.ftpgo:
//            RBMI

class SelectionMenu extends JMenu
    implements ItemListener
{

    SelectionMenu(String s, String s1, String s2)
    {
        super(s);
        bg = new ButtonGroup();
        add(ch1 = new RBMI(0, s1));
        ch1.addItemListener(this);
        bg.add(ch1);
        add(ch2 = new RBMI(1, s2));
        ch2.addItemListener(this);
        bg.add(ch2);
        setState(0);
    }

    public void itemStateChanged(ItemEvent itemevent)
    {
        RBMI rbmi = (RBMI)itemevent.getSource();
        if(!rbmi.isSelected())
            return;
        if(ignore)
        {
            ignore = false;
            return;
        }
        if(rbmi.pos != state)
        {
            state = rbmi.pos;
            makeCallBack();
        }
    }

    void setState(int i)
    {
        state = i;
        ignore = true;
        if(i == 0)
            ch1.setSelected(true);
        else
            ch2.setSelected(true);
    }

    void setState(boolean flag)
    {
        if(flag)
            setState(1);
        else
            setState(0);
    }

    void setStateText(String s)
    {
        if(s.equals("0"))
        {
            state = 0;
            ch1.setSelected(true);
        } else
        {
            state = 1;
            ch2.setSelected(true);
        }
    }

    void reverse()
    {
        if(state == 0)
            setState(1);
        else
            setState(0);
    }

    String getStateText()
    {
        if(state == 0)
            return "0";
        else
            return "1";
    }

    int getState()
    {
        return state;
    }

    void makeCallBack()
    {
    }

    static final int CHOICE1 = 0;
    static final String CHOICE1_TXT = "0";
    static final int CHOICE2 = 1;
    static final String CHOICE2_TXT = "1";
    RBMI ch1;
    RBMI ch2;
    int state;
    ButtonGroup bg;
    boolean ignore;
}
