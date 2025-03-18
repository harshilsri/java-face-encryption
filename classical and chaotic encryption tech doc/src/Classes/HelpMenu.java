import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JMenu;

// Referenced classes of package com.equitysoft.ftpgo:
//            HelpBox, AboutBox, ESUtils, Prefs

class HelpMenu extends JMenu
    implements ActionListener
{

    HelpMenu()
    {
        super("Help    ");
        HELP = "Help documentation";
        ABOUT = "About";
        LOAD = "Reload all defaults";
        add(ESUtils.getMI(ABOUT, this));
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        String s = actionevent.getActionCommand();
        if(s == HELP)
            new HelpBox();
        else
        if(s == ABOUT)
            new AboutBox("help/about.html");
        else
        if(s == LOAD)
        {
            deleteFiles();
            System.exit(0);
        }
    }

    static void deleteFiles()
    {
        if(Prefs.PREFS.exists())
            Prefs.PREFS.delete();
    }

    String HELP;
    String ABOUT;
    String LOAD;
}
