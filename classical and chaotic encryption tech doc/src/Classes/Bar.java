import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.*;

// Referenced classes of package com.equitysoft.ftpgo:
//            MBar, FileFinder, RemFileFinder, SiteViewer, 
//            LogWindow, ESUtils, FTPGo, FTPSite

class Bar extends JFrame
    implements WindowListener
{

    Bar()
    {
        handle = this;
        ESUtils.cls = getClass();
		try
		{
       setIconImage(ESUtils.getSystemImage(FTPGo.SYS_ICON));
        setJMenuBar(new MBar());
        jtp = new JTabbedPane();
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(jtp, "Center");
        Container container1 = new Container();
        container1.setLayout(new BorderLayout());
        Container container2 = new Container();
        container2.setLayout(new GridLayout(1, 2));
        container2.add(ff = new FileFinder("Local file system"));
        container2.add(rff=new RemFileFinder("Remote FTP site"));
        container1.add(container2, "Center");
        container1.add(status = new JLabel(), "South");
        jtp.addTab("  FTP connection      ", container1);
        jtp.setIconAt(0, new ImageIcon(ESUtils.getESURL("Mailsession.gif")));
        jtp.addTab("  Manage sites      ", new SiteViewer());
        jtp.setIconAt(1, new ImageIcon(ESUtils.getESURL("JavaMailResource_16.gif")));
        jtp.addTab("  View log      ", new LogWindow());
        jtp.setIconAt(2, new ImageIcon(ESUtils.getESURL("JMSResource_16.gif")));
       setDisconnect();
        addWindowListener(this);
        pack();
		}catch(Exception ecx){}
    }
    static void setDisconnect()
    {
        if(status != null)
            status.setText("   Disconnected");
    }
    static void setIdle()
    {
        status.setText("   Connection idle");
    }
    static void setStatus(String s)
    {
        status.setText("   Connection active : " + s);
    }
    static void setSite(FTPSite ftpsite)
    {
        handle.setTitle(ftpsite.name == null ? ftpsite.url + " - " + ESUtils.PRODUCT : ftpsite.name);
    }
    static void setHeader()
    {
        handle.setTitle(ESUtils.PRODUCT);
    }
    public void windowActivated(WindowEvent windowevent)
    {
    }
    public void windowClosed(WindowEvent windowevent)
    {
    }
    public void windowClosing(WindowEvent windowevent)
    {
        FTPGo.closeIt();
    }
    public void windowDeactivated(WindowEvent windowevent)
    {
    }
    public void windowIconified(WindowEvent windowevent)
    {
    }
    public void windowOpened(WindowEvent windowevent)
    {
    }
    public void windowDeiconified(WindowEvent windowevent)
    {
    }
    static Bar handle;
    static FileFinder ff;
	static RemFileFinder rff;
    static JTabbedPane jtp;
    static JLabel status;
}
