import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

// Referenced classes of package com.equitysoft.ftpgo:
//            ESDialog, VerticalLayout, MsgBox, YesNo, 
//            LibButton, ESZap, FTPSite

class GetSite extends ESDialog
    implements ActionListener
{

    GetSite()
    {
        super("Enter FTP site details", true);
        super.jp.setLayout(new VerticalLayout(5, 3));
        Container container = new Container();
        container.setLayout(new BorderLayout());
        container.add(url = new MsgBox("URL (without 'ftp://' prefix)"), "Center");
        container.add(passive = new YesNo("Passive"), "East");
        super.jp.add(container);
        container = new Container();
        container.setLayout(new GridLayout(1, 2));
        container.add(username = new MsgBox("Username", 12, false));
        container.add(pass = new MsgBox("Password", 12, true));
        super.jp.add(container);
        container = new Container();
        container.setLayout(new FlowLayout(1, 3, 3));
        container.add(accept = new LibButton("Connect", null, this));
        container.add(cancel = new LibButton("Cancel", null, this));
        super.jp.add(container);
        finishOff();
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        fs = null;
        LibButton libbutton = (LibButton)actionevent.getSource();
        if(libbutton == accept)
        {
            String s = null;
            if(url.isEmpty())
                s = "URL";
            else
            if(username.isEmpty())
                s = "username";
            if(s != null)
            {
                new ESZap("The " + s + " field cannot be empty for a new connection");
                return;
            }
            fs = new FTPSite(null, url.getText(), username.getText(), false, pass.getText(), passive.isYes());
        }
        closeDialog();
    }

    static FTPSite ask()
    {
        fs = null;
        new GetSite();
        return fs;
    }

    YesNo passive;
    MsgBox url;
    MsgBox username;
    MsgBox pass;
    MsgBox account;
    LibButton accept;
    LibButton cancel;
    static FTPSite fs;
}
