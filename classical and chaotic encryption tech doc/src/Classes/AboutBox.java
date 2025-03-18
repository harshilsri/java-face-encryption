import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.text.JTextComponent;

// Referenced classes of package com.equitysoft.ftpgo:
//            ESDialog, ESZap, LibButton, ESUtils, 
//            FTPGo

class AboutBox extends ESDialog
    implements ActionListener
{
    AboutBox(String s)
    {
        super("About " + ESUtils.PRODUCT);
       super.jp.setLayout(new BorderLayout());
        java.net.URL url = getClass().getResource(s);
        JEditorPane jeditorpane;
        try
        {
            jeditorpane = new JEditorPane(url);
        }
        catch(Exception exception)
        {
          new ESZap("About not found");
            return;
        }
        jeditorpane.setEditable(false);
        JScrollPane jscrollpane = new JScrollPane();
        jscrollpane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createLoweredBevelBorder()));
        javax.swing.JViewport jviewport = jscrollpane.getViewport();
        jviewport.add(jeditorpane);
        super.jp.add(jscrollpane, "Center");
        JPanel jpanel = new JPanel();
        LibButton libbutton;
        jpanel.add(libbutton = new LibButton("Continue"));
        libbutton.addActionListener(this);
        jpanel.setBorder(BorderFactory.createEtchedBorder());
        super.jp.add(jpanel, "South");
        setSize((2 * FTPGo.screendim.width) / 3, (2 * FTPGo.screendim.height) / 3);
        Dimension dimension = getSize();
        setLocation((FTPGo.screendim.width - dimension.width) / 2, (FTPGo.screendim.height - dimension.height) / 2);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        closeDialog();
    }

  static final String CONTINUE = "Continue";
}
