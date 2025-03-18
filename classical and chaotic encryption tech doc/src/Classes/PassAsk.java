import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Referenced classes of package com.equitysoft.ftpgo:
//            LibButton, FTPGo, ESUtils

class PassAsk extends JDialog
    implements ActionListener, KeyListener
{

    PassAsk(JFrame jframe)
    {
        super(jframe, " Enter access password", true);
        CANCEL = "Cancel";
        OK = "OK";
        Font font = new Font("dialog.bold", 1, 12);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Container container = getContentPane();
        container.setLayout(new FlowLayout());
        container.add(txt = new JPasswordField(20));
        txt.setFont(font);
        txt.addKeyListener(this);
        txt.addActionListener(this);
        LibButton libbutton;
        container.add(libbutton = new LibButton(OK));
        libbutton.addActionListener(this);
        container.add(libbutton = new LibButton(CANCEL));
        libbutton.addActionListener(this);
        pack();
        Dimension dimension1 = getSize();
        setLocation((dimension.width - dimension1.width) / 2, (dimension.height - dimension1.height) / 2);
        setVisible(true);
    }

    public void keyTyped(KeyEvent keyevent)
    {
        char c = keyevent.getKeyChar();
        if(c == '\033')
            System.exit(0);
    }

    public void keyPressed(KeyEvent keyevent)
    {
    }

    public void keyReleased(KeyEvent keyevent)
    {
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        setVisible(false);
        if(actionevent.getActionCommand() == CANCEL)
            System.exit(0);
    }

    static String ask()
    {
        JFrame jframe = new JFrame();
       jframe.setIconImage(ESUtils.getSystemImage(FTPGo.SYS_ICON));
        new PassAsk(jframe);
        String s = new String(txt.getPassword());
        if(s.equals(ESUtils.EMPTY))
            System.exit(0);
        return s;
    }

    static JPasswordField txt;
    String CANCEL;
    String OK;
}
