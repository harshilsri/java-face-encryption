import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;

// Referenced classes of package com.equitysoft.ftpgo:
//            ESDialog, LibButton, ESUtils

class GetText extends ESDialog
    implements ActionListener, KeyListener
{

    GetText(String s, boolean flag)
    {
        super(s, true);
        super.jp.setLayout(new FlowLayout());
        tf = ((JTextField) (flag ? ((JTextField) (new JPasswordField(ESUtils.EMPTY, 25))) : new JTextField(ESUtils.EMPTY, 25)));
        tf.setFont(ESUtils.MSG_FONT);
        super.jp.add(tf);
        tf.addKeyListener(this);
        tf.addActionListener(this);
        LibButton libbutton = new LibButton(APPLY);
        libbutton.addActionListener(this);
        super.jp.add(libbutton);
        libbutton = new LibButton(CANCEL);
        libbutton.addActionListener(this);
        super.jp.add(libbutton);
        finishOff();
        tf.requestFocus();
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        closeDialog();
        if(actionevent.getActionCommand() == CANCEL)
            tf.setText(ESUtils.EMPTY);
    }

    static String ask(String s, boolean flag)
    {
        new GetText(s, flag);
        return tf.getText();
    }

    static String ask(String s)
    {
        return ask(s, false);
    }

    public void keyTyped(KeyEvent keyevent)
    {
        char c = keyevent.getKeyChar();
        if(c == '\033')
            closeDialog();
    }

    public void keyPressed(KeyEvent keyevent)
    {
    }

    public void keyReleased(KeyEvent keyevent)
    {
    }

    static String APPLY = "Apply";
    static String CANCEL = "Cancel";
    static JTextField tf;

}
