import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import javax.swing.UIManager;

// Referenced classes of package com.equitysoft.ftpgo:
//            Bar, SysProps, Prefs, PassAsk, 
//            MiscMenu, FailBox, SiteManager, FileFinder, 
//            SiteViewer, ESUtils, LogManager
public class FTPGo
{

    public FTPGo()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch(Exception exception)
        {
            System.out.println("Illegal Access Exception");
            System.exit(1);
        }
        SysProps.setDirs();

        screendim = Toolkit.getDefaultToolkit().getScreenSize();
        SiteManager.loadSites();
        if(!Prefs.getBoolean("initialized"))
        {
            initAll();
        } else
        {
            FileFinder.currdir = new File(Prefs.get("filefinderpath"));
            bar = new Bar();
            SiteViewer.loadList();
            int i = Prefs.getInt("xpos");
            if(i < 0)
                i = 0;
            int j = Prefs.getInt("ypos");
            if(j < 0)
                j = 0;
            bar.setLocation(i, j);
            int k = Prefs.getInt("width");
            if(k > screendim.width)
                k = screendim.width;
            int l = Prefs.getInt("height");
            if(l > screendim.height)
                l = screendim.height;
            bar.setSize(k, l);
            bar.setVisible(true);
        }
        Bar.setHeader();
        ESUtils.deleteAll(SysProps.TEMP);
    }
    static void initAll()
    {
        FileFinder.currdir = SysProps.CURR_DIR;
        bar = new Bar();
        SiteViewer.loadList();
        bar.setLocation(20, 20);
        bar.setVisible(true);
        Prefs.setBoolean("initialized", true);
    }
    /*public static void main(String args[])
    {
        new FTPGo();
    }*/
    static void closeIt()
    {
        Prefs.set("filefinderpath", FileFinder.currdir.getAbsolutePath());
        Point point = Bar.handle.getLocation();
        Prefs.setInt("xpos", point.x);
        Prefs.setInt("ypos", point.y);
        Dimension dimension = Bar.handle.getSize();
        Prefs.setInt("width", dimension.width);
        Prefs.setInt("height", dimension.height);
        Prefs.writePrefs();
        ESUtils.deleteAll(SysProps.TEMP);
        LogManager.endLog();
        System.exit(0);
    }
    static String SYS_ICON = "Mailsession.gif";
    static Dimension screendim;
    static Bar bar;
    static boolean test = false;
} 