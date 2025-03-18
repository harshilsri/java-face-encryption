import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

// Referenced classes of package com.equitysoft.ftpgo:
//            LibButton, VerticalLayout, MsgBox, YesNo, 
//            ESZap, FTPSite, YesNoListener, ESUtils, 
//            RemFile, DBManager, SiteManager, RemFileFinder

class SiteViewer extends Container
    implements ActionListener, ListSelectionListener, YesNoListener
{

    SiteViewer()
    {
        setLayout(new BorderLayout());
        JPanel jpanel = new JPanel(false);
        jpanel.setLayout(new BorderLayout());
        jpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "All sites"));
        JScrollPane jscrollpane = new JScrollPane();
        vals = new DefaultListModel();
        list = new JList();
        list.setSelectionMode(1);
        list.setModel(vals);
        jscrollpane.getViewport().setView(list);
        jpanel.add(jscrollpane, "Center");
        jscrollpane.setPreferredSize(new Dimension(200, 150));
        list.addListSelectionListener(this);
        JPanel jpanel1 = new JPanel(false);
        jpanel1.setBorder(BorderFactory.createEtchedBorder());
        jpanel1.setLayout(new FlowLayout(1, 3, 3));
        jpanel1.add(add = new LibButton("Add", null, this));
        jpanel1.add(modify = new LibButton("Modify", null, this));
        jpanel1.add(delete = new LibButton("Delete", null, this));
        jpanel.add(jpanel1, "South");
        add(jpanel, "West");
        JPanel jpanel2 = new JPanel(false);
        jpanel2.setLayout(new VerticalLayout(15, 3, 0));
        jpanel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Site details"));
        Container container = new Container();
        container.setLayout(new BorderLayout());
        container.add(name = new MsgBox("Name for display", 20, false), "Center");
        container.add(passive = new YesNo("Passive"), "East");
        jpanel2.add(container);
        jpanel2.add(url = new MsgBox("URL (without 'ftp://' prefix)"));
        jpanel2.add(username = new MsgBox("Username", 20, false));
        jpanel2.add(pass = new MsgBox("Password", 20, true));
        jpanel2.add(pprompt = new YesNo("Prompt for password when connecting", "No", "Yes", this));
        JPanel jpanel3 = new JPanel();
        jpanel3.setLayout(new FlowLayout(1, 10, 10));
        jpanel3.setBorder(BorderFactory.createEtchedBorder());
        jpanel3.add(erase = new LibButton("Erase cache", null, this));
        jpanel3.add(accept = new LibButton("Accept", null, this));
        jpanel3.add(cancel = new LibButton("Cancel", null, this));
        jpanel2.add(jpanel3);
        add(jpanel2, "Center");
        displayOff();
    }

    public void changedYesNo(YesNo yesno)
    {
        if(pprompt.isYes())
        {
            pass.setText(ESUtils.EMPTY);
            pass.setEditable(false);
        } else
        {
            pass.setEditable(true);
            pass.setFocus();
        }
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        LibButton libbutton = (LibButton)actionevent.getSource();
        if(libbutton == add)
        {
            listOff();
            displayOn();
            emptyFields();
            action = -1;
        } else
        if(libbutton == modify)
        {
            action = list.getSelectedIndex();
            listOff();
            displayOn();
            name.setEditable(false);
            if(DBManager.hasDir(RemFile.checkName(name.getText())))
                erase.setEnabled(true);
        } else
        if(libbutton == delete)
        {
            DBManager.deleteDir(RemFile.checkName((String)list.getSelectedValue()));
            SiteManager.deleteSite(list.getSelectedIndex());
            loadList();
            RemFileFinder.loadCombo();
        } else
        if(libbutton == cancel)
        {
            emptyFields();
            listOn();
            displayOff();
        } else
        if(libbutton == accept)
        {
            String s = null;
            if(name.isEmpty())
                s = "name";
            else
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
            if(action == -1)
            {
                if(SiteManager.exists(name.getText()))
                {
                    new ESZap("The site name must be unique and '" + name.getText() + "' already exists");
                    return;
                }
                SiteManager.addSite(new FTPSite(name.getText(), url.getText(), username.getText(), pprompt.isYes(), pass.getText(), passive.isYes()));
                RemFileFinder.loadCombo();
                loadList();
            } else
            {
                FTPSite aftpsite[] = SiteManager.getSitesArray();
                FTPSite ftpsite = aftpsite[action];
                ftpsite.url = url.getText();
                ftpsite.username = username.getText();
                ftpsite.pprompt = pprompt.isYes();
                ftpsite.pass = pass.getText();
                ftpsite.passive = passive.isYes();
                SiteManager.writeSites();
                emptyFields();
                listOn();
                displayOff();
            }
            displayOff();
        } else
        if(libbutton == erase)
        {
            DBManager.deleteDir(RemFile.checkName(name.getText()));
            erase.setEnabled(false);
        }
    }

    static void loadList()
    {
        selectionOff();
        listOn();
        vals.removeAllElements();
        FTPSite aftpsite[] = SiteManager.getSitesArray();
        if(aftpsite == null)
            return;
        int i = aftpsite.length;
        for(int j = 0; j < i; j++)
            vals.addElement(aftpsite[j].name);

    }

    public void valueChanged(ListSelectionEvent listselectionevent)
    {
        int i = list.getSelectedIndex();
        if(i < 0)
        {
            return;
        } else
        {
            showSite(i);
            selectionOn();
            return;
        }
    }

    static void showSite(int i)
    {
        FTPSite aftpsite[] = SiteManager.getSitesArray();
        FTPSite ftpsite = aftpsite[i];
        name.setText(ftpsite.name);
        url.setText(ftpsite.url);
        username.setText(ftpsite.username);
        passive.setEnabled(true);
        passive.setYes(ftpsite.passive);
        passive.setEnabled(false);
        pprompt.setEnabled(true);
        pprompt.setYes(ftpsite.pprompt);
        pprompt.setEnabled(false);
        if(!ftpsite.pprompt)
            pass.setText(ftpsite.pass);
    }

    static void displayOff()
    {
        name.setEditable(false);
        url.setEditable(false);
        username.setEditable(false);
        passive.off();
        pass.setEditable(false);
        pprompt.off();
        accept.setEnabled(false);
        cancel.setEnabled(false);
        erase.setEnabled(false);
    }

    static void displayOn()
    {
        name.setEditable(true);
        url.setEditable(true);
        username.setEditable(true);
        passive.on();
        pprompt.on();
        if(!pprompt.isYes())
            pass.setEditable(true);
        accept.setEnabled(true);
        cancel.setEnabled(true);
        name.requestFocus();
    }

    static void listOff()
    {
        list.clearSelection();
        add.setEnabled(false);
        modify.setEnabled(false);
        delete.setEnabled(false);
        list.setEnabled(false);
    }

    static void listOn()
    {
        add.setEnabled(true);
        list.setEnabled(true);
    }

    static void selectionOn()
    {
        modify.setEnabled(true);
        delete.setEnabled(true);
    }

    static void selectionOff()
    {
        modify.setEnabled(false);
        delete.setEnabled(false);
        emptyFields();
    }

    static void emptyFields()
    {
        name.setText(ESUtils.EMPTY);
        url.setText(ESUtils.EMPTY);
        username.setText(ESUtils.EMPTY);
        pass.setText(ESUtils.EMPTY);
        passive.setYes(false);
        pprompt.setYes(false);
    }

    static final int ADD = -1;
    static int action;
    static JList list;
    static DefaultListModel vals;
    static MsgBox name;
    static MsgBox url;
    static MsgBox username;
    static MsgBox pass;
    static YesNo passive;
    static YesNo pprompt;
    static LibButton accept;
    static LibButton cancel;
    static LibButton add;
    static LibButton modify;
    static LibButton delete;
    static LibButton erase;
}
