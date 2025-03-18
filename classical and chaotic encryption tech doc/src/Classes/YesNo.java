import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

// Referenced classes of package com.equitysoft.ftpgo:
//            YesNoListener

class YesNo extends JPanel  implements ActionListener
{
	JRadioButton yes;
	JRadioButton no;
	YesNoListener ynl;

    YesNo(String s, String s1, String s2, YesNoListener yesnolistener, boolean flag)
    {
        if(flag)
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), s));
        ynl = yesnolistener;
        setLayout(new FlowLayout(1, 5, 0));
        ButtonGroup buttongroup = new ButtonGroup();
        no = new JRadioButton(s1);
        no.addActionListener(this);
        buttongroup.add(no);
        add(no);
        no.setSelected(true);
        yes = new JRadioButton(s2);
        yes.addActionListener(this);
        buttongroup.add(yes);
        add(yes);
    }

    YesNo(String s, String s1, String s2, YesNoListener yesnolistener)
    {
        this(s, s1, s2, yesnolistener, true);
    }

    YesNo(String s, String s1, String s2)
    {
        this(s, s1, s2, null);
    }

    void setGaps(int i, int j)
    {
        setLayout(new FlowLayout(1, i, j));
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        if(ynl != null)
            ynl.changedYesNo(this);
    }

    YesNo(String s)
    {
        this(s, "No", "Yes");
    }

    public void setYes(boolean flag)
    {
        if(flag)
            yes.setSelected(true);
        else
            no.setSelected(true);
    }

    boolean isYes()
    {
        return yes.isSelected();
    }

    void off()
    {
        yes.setEnabled(false);
        no.setEnabled(false);
    }

    void on()
    {
        yes.setEnabled(true);
        no.setEnabled(true);
    }
   }
    