import java.awt.Container;
import javax.swing.Box;
import javax.swing.JMenuBar;

// Referenced classes of package com.equitysoft.ftpgo:
//            MiscMenu, HelpMenu

public class MBar extends JMenuBar
{

    MBar()
    {
        add(Box.createHorizontalGlue());
        //add(new MiscMenu());
        add(new HelpMenu());
    }
}
