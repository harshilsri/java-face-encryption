import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JLabel;

// Referenced classes of package com.equitysoft.ftpgo:
//            ESUtils

class ESLabel extends JLabel
{

    ESLabel(String s)
    {
        super(s);
        setFont(ESUtils.MSG_FONT);
        setForeground(Color.black);
    }
}
