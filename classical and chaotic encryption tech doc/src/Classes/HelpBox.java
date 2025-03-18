import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.EventObject;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.JTextComponent;

// Referenced classes of package com.equitysoft.ftpgo:
//            LibButton, ESZap, Bar, ESUtils, 
//            FTPGo

class HelpBox extends JFrame
    implements ActionListener, HyperlinkListener
{

    HelpBox() 
    {
        Bar.handle.repaint();
        setTitle(ESUtils.PRODUCT + " documentation.");
        setIconImage(ESUtils.getSystemImage(FTPGo.SYS_ICON));
        JPanel jpanel = new JPanel();
        jpanel.setOpaque(true);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(jpanel, "Center");
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent windowevent)
            {
                setVisible(false);
            }

        });
        jpanel.setBackground(Color.lightGray);
        jpanel.setLayout(new BorderLayout());
        pages = new Vector(1);
        JPanel jpanel1 = new JPanel();
        jpanel1.setLayout(new BorderLayout());
        jpanel1.setBackground(Color.lightGray);
        JPanel jpanel2 = new JPanel();
        jpanel2.setLayout(new FlowLayout(1, 5, 0));
        jpanel2.setBackground(Color.lightGray);
      jpanel2.add(back = new LibButton("Back", "right2mouse.gif"));
        back.setEnabled(false);
        back.addActionListener(this);
      jpanel2.add(index = new LibButton("Contents", "right2mouse.gif"));
        index.setEnabled(false);
        index.addActionListener(this);
     jpanel2.add(forward = new LibButton("Forward", "right2mouse.gif"));
        forward.setEnabled(false);
        forward.addActionListener(this);
        jpanel1.add(jpanel2, "West");
        LibButton libbutton;
        jpanel1.add(libbutton = new LibButton(FINISH), "East");
        libbutton.addActionListener(this);
        jpanel1.setBorder(BorderFactory.createEtchedBorder());
        jpanel.add(jpanel1, "North");
        URL url = getClass().getResource("help/index.html");
        pages.addElement(url);
        try
        {
            html = new JEditorPane(url);
        }
        catch(Exception exception)
        {
            new ESZap("Help not found");
            return;
        }
        html.setEditable(false);
        html.addHyperlinkListener(this);
        JScrollPane jscrollpane = new JScrollPane();
        jscrollpane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createLoweredBevelBorder()));
        javax.swing.JViewport jviewport = jscrollpane.getViewport();
        jviewport.add(html);
        jpanel.add(jscrollpane, "Center");
        setSize((2 * FTPGo.screendim.width) / 3, (2 * FTPGo.screendim.height) / 3);
        Dimension dimension = getSize();
        setLocation(FTPGo.screendim.width - dimension.width, FTPGo.screendim.height - dimension.height);
        setVisible(true);
    }

    public void hyperlinkUpdate(HyperlinkEvent hyperlinkevent)
    {
        if(hyperlinkevent.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED)
        {
            setCursor(Cursor.getDefaultCursor());
            URL url = hyperlinkevent.getURL();
            try
            {
                html.setPage(url);
            }
            catch(Exception exception)
            {
                setVisible(false);
                new ESZap(EXCEPTION);
                return;
            }
            back.setEnabled(true);
            index.setEnabled(true);
            if(pages.size() == 1)
            {
                pages.addElement(url);
                currpage++;
            } else
            if(currpage + 1 == pages.size())
            {
                pages.addElement(url);
                currpage++;
            } else
            if(url.equals((URL)pages.elementAt(currpage + 1)))
            {
                currpage++;
                if(currpage + 1 == pages.size())
                    forward.setEnabled(false);
            } else
            {
                for(int i = pages.size() - 1; i > currpage; i--)
                    pages.removeElementAt(i);

                pages.addElement(url);
                currpage++;
                forward.setEnabled(false);
            }
        } else
        if(hyperlinkevent.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ENTERED)
            setCursor(HAND);
        else
        if(hyperlinkevent.getEventType() == javax.swing.event.HyperlinkEvent.EventType.EXITED)
            setCursor(Cursor.getDefaultCursor());
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        if(actionevent.getActionCommand() == FINISH)
        {
            setVisible(false);
            return;
        }
        LibButton libbutton = (LibButton)actionevent.getSource();
        if(libbutton == index)
        {
            if(currpage == 0)
                return;
            try
            {
                html.setPage((URL)pages.elementAt(0));
            }
            catch(Exception exception)
            {
                setVisible(false);
                new ESZap(EXCEPTION);
                return;
            }
            back.setEnabled(false);
            forward.setEnabled(true);
            currpage = 0;
            index.setEnabled(false);
        } else
        {
            if(pages.size() <= 1)
                return;
            if(libbutton == back)
            {
                currpage--;
                try
                {
                    html.setPage((URL)pages.elementAt(currpage));
                }
                catch(Exception exception1)
                {
                    setVisible(false);
                    new ESZap(EXCEPTION);
                    return;
                }
                forward.setEnabled(true);
                if(currpage == 0)
                {
                    back.setEnabled(false);
                    index.setEnabled(false);
                }
            } else
            if(libbutton == forward)
            {
                currpage++;
                index.setEnabled(true);
                try
                {
                    html.setPage((URL)pages.elementAt(currpage));
                }
                catch(Exception exception2)
                {
                    setVisible(false);
                    new ESZap(EXCEPTION);
                    return;
                }
                back.setEnabled(true);
                if(currpage + 1 == pages.size())
                    forward.setEnabled(false);
            }
        }
    }

    static final Cursor HAND = new Cursor(12);
    static Dimension DIM = new Dimension(25, 25);
    static String EXCEPTION = "Exception loading page";
    static String FINISH = "Close help";
    static LibButton back;
    static LibButton forward;
    static LibButton index;
    static Vector pages;
    static int currpage;
    static JEditorPane html;
    static Dimension BDIM = new Dimension(40, 25);
    static Font TOOLTIP_FONT = new Font("SansSerif", 1, 13);
}
