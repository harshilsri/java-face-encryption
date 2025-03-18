import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.table.*;

// Referenced classes of package com.equitysoft.ftpgo:
//            LibButton, RemIconCell, SizeCell, RemFile, 
//            ESMsg, ESZap, FileFinder, ESUtils, 
//            FTPManager, DBManager, Bar, SiteManager, 
//            GetSite, FTPSite, GetText, NamePair, 
//            ESQ

class RemFileFinder extends JPanel
    implements ActionListener, MouseListener, Runnable
{
    class LCRenderer extends Component
        implements ListCellRenderer
    {

        public Component getListCellRendererComponent(JList jlist, Object obj, int i, boolean flag, boolean flag1)
        {
            selected = flag;
            rf = (RemFile)obj;
            if(rf.isDirectory())
            {
                int j = 0;
                int k = rf.path.length() - 1;
                int l = 0;
                do
                {
                    j = rf.path.indexOf('/', j);
                    if(j < 0 || ++j >= k)
                        break;
                    l++;
                } while(true);
                indent = 1 + l * 12;
            } else
            {
                indent = 3;
            }
            return this;
        }

        public void update(Graphics g)
        {
            paint(g);
        }

        public void paint(Graphics g)
        {
            if(back == null)
                back = getParent().getBackground();
            g.setColor(selected ? RemFileFinder.ALTCOL : back);
            g.fillRect(0, 0, DIM.width + 30, DIM.height);
            switch(rf.type)
            {
            case 0: // '\0'
                g.drawImage(idleimg, indent, 2, null);
                break;

            case 1: // '\001'
                g.drawImage(unlisted, indent, 2, null);
                break;

            case 2: // '\002'
                g.drawImage(diskimg, indent, 2, null);
                break;

            case 3: // '\003'
                g.drawImage(dirimg, indent, 2, null);
                break;

            default:
                g.drawImage(dirimg, indent, 2, null);
                break;
            }
            g.setFont(ESUtils.MSG_FONT);
            g.setColor(Color.black);
            g.drawString(rf.name, indent + 21, 15);
        }

        public Dimension getPreferredSize()
        {
            return DIM;
        }

        RemFile rf;
        Color back;
        boolean selected;
        int indent;

        public LCRenderer()
        {
        }
    }

    class Model extends AbstractTableModel
    {

        public int getColumnCount()
        {
            return 2;
        }

        public int getRowCount()
        {
//			System.out.println("************ "+RemFileFinder.vals.size());
            if(RemFileFinder.vals == null)
                return 0;
            else
                return RemFileFinder.vals.size();
        }

        public Object getValueAt(int i, int j)
        {
            if(RemFileFinder.vals.size() == 0)
                return ESUtils.EMPTY;
            if(j == 0)
                return RemFileFinder.vals.get(i);
            RemFile remfile = (RemFile)RemFileFinder.vals.get(i);
            if(remfile.isDirectory())
                return ESUtils.EMPTY;
            else
                return ESUtils.asK(remfile.size);
        }

        Model()
        {
        }
    }

    class PopupOptions extends JPopupMenu
        implements ActionListener
    {

        void set()
        {
            if(FTPManager.isAnonymous())
            {
                create.setEnabled(false);
                rename.setEnabled(false);
                delete.setEnabled(false);
				rmdir.setEnabled(false);                                                       
                return;
            }//end of if
            if(RemFileFinder.hasselection)
                delete.setEnabled(true);
            else
                delete.setEnabled(false);
            if(RemFileFinder.oneselected)
                rename.setEnabled(true);
            else
                rename.setEnabled(false);
			 if(RemFileFinder.oneselected)
                rmdir.setEnabled(true);                     
            else
               rmdir.setEnabled(false);
        }//end of set

        public void actionPerformed(ActionEvent actionevent)
        {
            String s = actionevent.getActionCommand();
            if(s == RemFileFinder.CREATE)
            {
				 File temp;
				String s1 = JOptionPane.showInputDialog("Enter the name of the new directory");
               	  File file=new File(s1);
			    String res= FTPManager.curr.mkdir(file);
				 RemFile newfile=new RemFile(3,s1, file.length(),null);
                  RemFileFinder.addOrReplace(newfile);
				  LogManager.writeTimeMsg("Directory created: " + file.getName());
				  Bar.setStatus("Directory Created");
			  }
			  if(s == RemFileFinder.RMDIR)
            {
				 RemFile remfile = (RemFile)RemFileFinder.vals.get(RemFileFinder.table.getSelectedRow());
               	 String res= FTPManager.curr.rmdir(remfile);
			       	vals.remove(remfile); 	 
				  LogManager.writeTimeMsg("Directory removed: " + remfile.getName());
				  Bar.setStatus("Directory Removed");
			  }
          	else if(s == RemFileFinder.RENAME)
              {
				 RemFile remfile = (RemFile)RemFileFinder.vals.get(RemFileFinder.table.getSelectedRow());
                String s2 = JOptionPane.showInputDialog("Enter the new name ");
                if(s2.equals(ESUtils.EMPTY))
                    return;
                s2 = RemFile.checkName(s2);
		        String  s3=remfile.getName();
                if(s2.equals(s3))
                {
                  //  new ESZap("The name is the same");
				  JOptionPane.showMessageDialog(this,"The name is the same","Message",JOptionPane.INFORMATION_MESSAGE);
                    return;
                }//end of if
                int j = RemFileFinder.vals.size();
                boolean flag = false;
                for(int i1 = 0; i1 < j; i1++)
                {
                    RemFile remfile1 = (RemFile)RemFileFinder.vals.get(i1);
                    int l = s2.compareToIgnoreCase(remfile1.name);
                    if(l == 0)
                    {
                     //   new ESZap("Ignored - the new name already exists");
					  JOptionPane.showMessageDialog(this,"The name  already exists","Message",JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }//end of if
				 }//end of for
				 File newfile=new File(s2);
				 File oldfile=new File(s3);
			     FTPManager.curr.rename(oldfile,newfile);
				vals.remove(remfile);
				RemFileFinder.addOrReplace(new RemFile(4,newfile.getName(),remfile.length(),null));
				LogManager.writeTimeMsg("File renamed from   "+s3+" to  "+s2);
				 Bar.setStatus("File Renamed");
			  }//end of else if
			else if(s == RemFileFinder.DELETE)
	          {
				 int ai[] = RemFileFinder.table.getSelectedRows();
				int i = ai.length;
				String s4 = " Are you sure  to delete these " + i + " items";
                RemFile aremfile[] = new RemFile[i];
				String res=" ";
                for(int k = 0; k < i; k++)
				  {
				 aremfile[k] = (RemFile)RemFileFinder.vals.get(ai[k]);
				  res=FTPManager.curr.delete(aremfile[k]);
				  }
				for (int counter=i-1;counter>=0 ;counter-- )
				vals.remove(ai[counter]);
			                    																										                   
                if(i == 1 && aremfile[0].isDirectory() && DBManager.hasContents(RemFileFinder.getCurrDir().path + '/' + aremfile[0].name, FTPManager.getSessionStart()))
                {
                    new ESMsg("Ignored - only empty directories can be deleted");
                    return;
                }
			  Bar.setStatus( i+"  File(s) Deleted");
        } //else if
		} //end of method actionPerformed

        JMenuItem create;
        JMenuItem rename;
        JMenuItem delete;
		JMenuItem rmdir;
		
        PopupOptions()
        {
            add(create = ESUtils.getMI(RemFileFinder.CREATE, this));
            add(rmdir=ESUtils.getMI(RemFileFinder.RMDIR, this));
			add(rename = ESUtils.getMI(RemFileFinder.RENAME, this));
            add(delete = ESUtils.getMI(RemFileFinder.DELETE, this));
			addSeparator();
            add(ESUtils.getMI(RemFileFinder.CANCEL, this));
        }
    }//end of class popupmenu
    RemFileFinder(String s) 
    {
        super(false);
        DIM = new Dimension(200, 20);
        handle = this;
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), s));
        dirimg = FileFinder.dirimg;
        diricon = FileFinder.diricon;
        docicon = FileFinder.docicon;
        diskimg = FileFinder.diskimg;
        idleimg = ESUtils.getSystemImage("ipp_0004.gif");
        unlisted = ESUtils.getSystemImage("sumbul2a.gif");
        setLayout(new BorderLayout());
        JPanel jpanel = new JPanel(false);
		jpanel.setLayout(new FlowLayout(0, 4, 4));
        jpanel.add(new JLabel("Look in:"));
        jpanel.add(lookin = new JComboBox());
        lookin.setRenderer(new LCRenderer());
        loadCombo();
        jpanel.add(goup = new LibButton(null, "file1.gif"));
        goup.addActionListener(this);
        goup.setPreferredSize(FileFinder.SQ);
        goup.setToolTipText("Go up a directory level");
        add(jpanel, "North");
        center = new JPanel();
        center.setLayout(new BorderLayout());
        table = new JTable(model = new Model());
//System.out.println(vals.size());
		table.addMouseListener(this);
        names = table.getColumnModel().getColumn(0);
        names.setPreferredWidth(210);
        names.setCellRenderer(new RemIconCell());
        names.setHeaderValue(DISCONNECT_TEXT);
        size = table.getColumnModel().getColumn(1);
        size.setPreferredWidth(3);
        size.setCellRenderer(new SizeCell());
        table.setRowSelectionAllowed(true);
        sp = new JScrollPane(table);
	sp.getViewport().setBackground(Color.white);
        center.add(sp, "Center");
        sp.setPreferredSize(new Dimension(200, 350));
        add(center, "Center");
        bottom = new JPanel(false);
        bottom.setLayout(new FlowLayout(0, 3, 0));
        bottom.add(all = new LibButton("All", this));
        all.setToolTipText("Select all files");
        bottom.add(invert = new LibButton("Inv", this));
        invert.setToolTipText("Invert selection");
        bottom.add(options = new LibButton("Opt", this));
        options.setToolTipText("Optional functions");
        bottom.add(download = new LibButton("Download", "scrmapiconleft.gif", this));
        download.setToolTipText("Download selected files");
        add(bottom, "South");
        popup = new PopupOptions();
        showDisconnect();
        lookin.addActionListener(this);
    }

    static void cancelPending()
    {
        if(!pending)
            return;
        pending = false;
        switch(pendingaction)
        {
        case 2: // '\002'
        case 8: // '\b'
        case 9: // '\t'
        case 10: // '\n'
        default:
            break;

        case 0: // '\0'
            FTPManager.setMsgBox(null);
            setVals();
            break;

        case 1: // '\001'
            FTPManager.setToDisconnect();
            RemFile remfile = (RemFile)arg1;
            if(!remfile.isIdle())
            {
                connectToSite(remfile);
                return;
            }
            break;

        case 6: // '\006'
            ((RemFile)arg1).name = (String)arg2;
            sortItems();
            break;

        case 5: // '\005'
            vals.add(new RemFile(3, RemFile.checkName((String)arg1), 0L, null));
            sortItems();
            break;

        case 7: // '\007'
            int ai[] = (int[])arg1;
            int i = ai.length;
            RemFile remfile1 = null;
            if(i == 1)
            {
                remfile1 = (RemFile)vals.get(ai[0]);
                if(!remfile1.isDirectory())
                    remfile1 = null;
            }
            for(int j = i - 1; j >= 0; j--)
                vals.remove(ai[j]);

            sortItems();
            if(remfile1 != null)
                DBManager.delete(getCurrDir().path + '/' + remfile1.name);
            break;

        case 4: // '\004'
            int k = lookin.getSelectedIndex();
            previdx = k;
            if(arg1 == GO_UP_BUTTON)
            {
                ignore = true;
                lookin.removeItemAt(k);
                k--;
                lookin.setSelectedIndex(k);
                ignore = false;
                if(getCurrDir().isSite())
                    goup.setEnabled(false);
                loadFromDB();
                break;
            }
            int l = ((Integer)arg2).intValue();
            int i1 = ((Integer)arg1).intValue();
            for(int j1 = 0; j1 < i1; j1++)
                lookin.removeItemAt(--l);

            if(getCurrDir().isSite())
                goup.setEnabled(false);
            loadFromDB();
            break;

        case 3: // '\003'
            RemFile remfile2 = (RemFile)arg1;
            addToCombo(remfile2);
            loadFromDB();
            goup.setEnabled(true);
            break;

        case 11: // '\013'
            RemFile remfile3 = (RemFile)arg1;
            addToCombo(remfile3);
            goup.setEnabled(true);
            setVals();
            break;
        }
        pendingaction = -1;
        if(FTPManager.isConnected())
            Bar.setIdle();
        else
            Bar.setDisconnect();
    }

    static void pendingError(String s)
    {
        if(!pending)
            return;
        switch(pendingaction)
        {
        case 2: // '\002'
        case 3: // '\003'
        case 5: // '\005'
        case 6: // '\006'
        case 8: // '\b'
        case 9: // '\t'
        case 10: // '\n'
        case 11: // '\013'
        default:
            break;

        case 7: // '\007'
            new ESMsg("Ignored - Unable to delete a directory which isn't empty");
            break;

        case 0: // '\0'
            FTPManager.setMsgBox(null);
            resetToDisconnect();
            FTPManager.setToDisconnect();
            if(s != FTPManager.CANCEL)
                new ESZap("Error - " + s);
            break;

        case 1: // '\001'
            showDisconnect();
            FTPManager.setToDisconnect();
            break;

        case 4: // '\004'
            resetLookin();
            break;
        }
        pendingaction = -1;
        pending = false;
        Bar.setIdle();
    }

    static void resetLookin()
    {
        ignore = true;
        lookin.setSelectedIndex(previdx);
        ignore = false;
    }

    static void addToCombo(RemFile remfile)
    {
        ignore = true;
        int i = lookin.getSelectedIndex();
        int j = lookin.getItemCount();
        if(i == j - 1)
        {
            lookin.addItem(remfile);
            lookin.setSelectedIndex(j);
            previdx = j;
        } else
        {
            i++;
            lookin.insertItemAt(remfile, i);
            lookin.setSelectedIndex(i);
            previdx = i;
        }
        ignore = false;
    }

    static void setPending(String s, int i, Object obj, Object obj1)
    {
        Bar.setStatus(s);
        pendingaction = i;
        pending = true;
        arg1 = obj;
        arg2 = obj1;
    }

    static boolean stillPending()
    {
        if(pending)
        {
            new ESZap("Confirmation of the previous operation completed is pending");
            return true;
        } else
        {
            return false;
        }
    }

    static void perform(int i, Object obj)
    {
        FTPManager.perform(i, obj);
    }

    static void deselectAll()
    {
        if(disconnected)
        {
            return;
        } else
        {
            table.clearSelection();
            displayChanged();
            return;
        }
    }

    static RemFile getCurrDir()
    {
        return (RemFile)lookin.getSelectedItem();
    }

    static void loadCombo()
    {
        ignore = true;
        lookin.removeAllItems();
        RemFile aremfile[] = SiteManager.getSites();
        int i = aremfile.length;
        for(int j = 0; j < i; j++)
            lookin.addItem(aremfile[j]);

        ignore = false;
    }

    static void setVals()
    {
        vals.clear();
        vals.addAll(FTPManager.vals);
        sortItems();
    }

    static void addRemFile(RemFile remfile)
    {
        vals.add(remfile);
        sortItems();
    }

    static void resetToDisconnect()
    {
        showDisconnect();
        ignore = true;
        lookin.setSelectedIndex(0);
        ignore = false;
        pending = false;
    }

    static void showDisconnect()
    {
        Bar.setDisconnect();
        vals.clear();
        sp.getViewport().setBackground(Color.lightGray);
        disconnected = true;
        goup.setEnabled(true);
        names.setHeaderValue(DISCONNECT_TEXT);
        size.setHeaderValue(ESUtils.EMPTY);
        previdx = 0;
        ignore = true;
        removeAllDirs();
        ignore = false;
        buttonsOff();
        options.setEnabled(false);
        changed();
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
        FileFinder.deselectAll();
        int i = table.rowAtPoint(mouseevent.getPoint());
        if(i < 0)
            return;
        RemFile remfile = (RemFile)vals.get(i);
        if((mouseevent.getModifiers() & 4) > 0)
        {
            if(stillPending())
                return;
            table.clearSelection();
            table.addRowSelectionInterval(i, i);
            if(!remfile.isDirectory())
            {
                displayChanged();
            } else
            {
                oneselected = true;
                hasselection = true;
                names.setHeaderValue("One directory selected");
                size.setHeaderValue("");
                changed();
            }
            popup.set();
            bottom.remove(popup);
            table.add(popup);
            int j = popup.getPreferredSize().height;
            popup.show(table, mouseevent.getX(), mouseevent.getY() - j);
            return;
        }
        if(remfile.isDirectory())
        {
            int k = vals.size();
            for(int l = 0; l < k; l++)
                if(i != l && table.isRowSelected(l))
                    table.removeRowSelectionInterval(l, l);

            oneselected = true;
            hasselection = true;
            names.setHeaderValue("One directory selected");
            size.setHeaderValue("");
            changed();
            return;
        } else
        {
            displayChanged();
            return;
        }
    }

    public void mouseClicked(MouseEvent mouseevent)
    {
        if(mouseevent.getClickCount() < 2)
            return;
        if((mouseevent.getModifiers() & 4) > 0)
            return;
        int i = table.rowAtPoint(mouseevent.getPoint());
        if(i < 0)
            return;
        RemFile remfile = (RemFile)vals.get(i);
        if(!remfile.isDirectory())
        {
            table.clearSelection();
            table.addRowSelectionInterval(i, i);
            return;
        }
        remfile.path = getCurrDir().path + '/' + remfile.name;
        if(!DBManager.canLoad(remfile, FTPManager.getSessionStart()))
        {
            perform(11, remfile);
            setPending("Changing to directory " + remfile.name + " and listing", 11, remfile, null);
        } else
        {
            perform(3, remfile);
            setPending("Changing to directory " + remfile.name, 3, remfile, null);
        }
        table.clearSelection();
    }

    static void displayChanged()
    {
		 int i = vals.size();
        oneselected = false;
        hasselection = false;
        if(lookin.getSelectedIndex() != 0)
            options.setEnabled(true);
        if(i == 0)
        {
            names.setHeaderValue("Directory empty");
            size.setHeaderValue(ESUtils.EMPTY);
            download.setEnabled(false);
            hasselection = false;
            all.setEnabled(false);
            invert.setEnabled(false);
            buttonsOff();
        } else
        {
            sp.getViewport().setBackground(Color.white);
            disconnected = false;
            int j = 0;
            int k = 0;
            long l = 0L;
            long l1 = 0L;
            long l3 = 0L;
            int i1 = 0;
            for(int j1 = 0; j1 < i; j1++)
            {
                RemFile remfile = (RemFile)vals.get(j1);
                if(remfile.isDirectory())
                {
                    if(table.isRowSelected(j1))
                        table.removeRowSelectionInterval(j1, j1);
                } else
                {
                    long l2 = remfile.length();
                    i1++;
                    if(table.isRowSelected(j1))
                    {
                        l += l2;
                        j++;
                    } else
                    {
                        l3 += l2;
                        k++;
                    }
                }
            }

            if(i1 > 0)
                buttonsOn();
            else
                buttonsOff();
            if(j == 0)
            {
                if(k == 1)
                    names.setHeaderValue("1 file");
                else
                    names.setHeaderValue(k + " files");
                if(l3 == 0L)
                    size.setHeaderValue(ESUtils.EMPTY);
                else
                    size.setHeaderValue(ESUtils.asK(l3));
                download.setEnabled(false);
            } else
            {
                if(j == 1)
                {
                    names.setHeaderValue("1 file selected");
                    oneselected = true;
                } else
                {
                    names.setHeaderValue(j + " files selected");
                }
                size.setHeaderValue(ESUtils.asK(l));
                download.setEnabled(true);
                hasselection = true;
            }
        }
        changed();
    }

    static void connectToSite(RemFile remfile)
    {
		rf = remfile;
        (new Thread(handle)).start();
		    }

    public void run()
    {
        if(rf.type == 1)
        {
            rf.site = GetSite.ask();
            DBManager.deleteUnlisted();
        }
        if(rf.site == null)
        {
            resetToDisconnect();
            return;
        }
        if(rf.site.pprompt)
        {
            String s = GetText.ask("Enter a password for '" + rf.site.name + "'", true);
            if(s.equals(ESUtils.EMPTY))
                return;
            rf.site.tpass = s;
        }
        setPending("Connecting to site " + rf.site.url, 0, rf.site, null);
        FTPManager.siteSelected(rf);
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        FileFinder.deselectAll();
        Object obj = actionevent.getSource();
        if(obj instanceof JComboBox)
        {
            if(ignore)
                return;
            if(stillPending())
            {
                ignore = true;
                lookin.setSelectedIndex(previdx);
                ignore = false;
                return;
            }
            int i = lookin.getSelectedIndex();
            if(i == previdx)
                return;
            RemFile remfile = getCurrDir();
            if(remfile.isSite() && !FTPManager.isConnected(remfile.site))
            {
                removeAllDirs();
                showDisconnect();
                previdx = i;
                if(FTPManager.isConnected())
                {
                    setPending("Disconnecting", 1, remfile, null);
                    FTPManager.perform(1, null);
                    return;
                }
                if(!remfile.isIdle())
                    connectToSite(remfile);
            } else
            {
                int i1 = lookin.getItemCount();
                i++;
                int k1 = 0;
                int k2 = 0;
                for(k2 = i; k2 < i1; k2++)
                {
                    RemFile remfile3 = (RemFile)lookin.getItemAt(k2);
                    if(remfile3.isSite())
                        break;
                    k1++;
                }

                Integer integer = new Integer(k1);
                perform(4, integer);
                String s = k1 != 1 ? "Moving up " + k1 + " directory levels" : "Moving up one directory level";
                setPending(s, 4, integer, new Integer(k2));
            }
        } else
        if(obj instanceof LibButton)
        {
            LibButton libbutton = (LibButton)obj;
            if(libbutton == options)
            {
                if(stillPending())
                    return;
                popup.set();
                table.remove(popup);
                bottom.remove(popup);
                bottom.add(popup);
                int j = popup.getPreferredSize().height;
                popup.show(bottom, options.getLocation().x, options.getLocation().y - j);
            } else
            if(libbutton == goup)
            {
                if(stillPending())
                    return;
                perform(4, new Integer(1));
                setPending("Moving up a directory level", 4, GO_UP_BUTTON, null);
            } else
            if(libbutton == download)
            {
                if(stillPending())
                    return;
                int ai[] = table.getSelectedRows();
                int j1 = ai.length;
                RemFile aremfile[] = new RemFile[j1];
                for(int j2 = 0; j2 < j1; j2++)
                    aremfile[j2] = (RemFile)vals.get(ai[j2]);

                perform(9, aremfile);
            } else
            if(libbutton == all)
            {
                int k = vals.size();
                int l1 = 0;
                boolean flag = false;
                for(l1 = 0; l1 < k; l1++)
                {
                    RemFile remfile1 = (RemFile)vals.get(l1);
                    if(remfile1.isDirectory())
                        continue;
                    flag = true;
                    break;
                }

                if(flag)
                    table.setRowSelectionInterval(l1, k - 1);
               displayChanged();
            } else
            if(libbutton == invert)
            {
                int l = vals.size();
                for(int i2 = 0; i2 < l; i2++)
                {
                    RemFile remfile2 = (RemFile)vals.get(i2);
                    if(!remfile2.isDirectory())
                        if(table.isRowSelected(i2))
                            table.removeRowSelectionInterval(i2, i2);
                        else
                            table.addRowSelectionInterval(i2, i2);
                }

                displayChanged();
            }
        }
    }

    static boolean loadFromDB()
    {
        vals.clear();
        if(!DBManager.load(getCurrDir(), vals))
        {
            return false;
        } else
        {
            displayChanged();
            return true;
        }
    }

    static void removeAllDirs()
    {
        ignore = true;
       goup.setEnabled(false);
        int i = lookin.getItemCount();
        for(int j = 0; j < i; j++)
        {
            RemFile remfile = (RemFile)lookin.getItemAt(j);
            if(!remfile.isDirectory())
                continue;
            lookin.removeItemAt(j);
            i--;
            if(j == i)
                break;
            j--;
        }

        ignore = false;
    }

    static void buttonsOff()
    {
        invert.setEnabled(false);
        all.setEnabled(false);
        download.setEnabled(false);
        hasselection = false;
        oneselected = false;
    }

    static void buttonsOn()
    {
        invert.setEnabled(true);
        all.setEnabled(true);
    }

    static void addOrReplace(RemFile remfile)
    {
        int i = vals.size();
        String s = remfile.getName();
        int j = 0;
        for(j = 0; j < i; j++)
        {
            RemFile remfile1 = (RemFile)vals.get(j);
            if(!remfile1.getName().equals(s))
                continue;
            vals.set(j, remfile);
            break;
        }

        if(j == i)
            vals.add(remfile);
    }

    static void sortItems()
    {
        table.clearSelection();
        int i = vals.size();
        int j = 0;
        alt.clear();
        for(int k = 0; k < i; k++)
        {
            RemFile remfile = (RemFile)vals.get(k);
            if(remfile.isDirectory())
            {
                alt.add(remfile);
                j++;
            }
        }

        remfileBubbleSort(alt, 0, j);
        for(int l = 0; l < i; l++)
        {
            RemFile remfile1 = (RemFile)vals.get(l);
            if(!remfile1.isDirectory())
                alt.add(remfile1);
        }

        remfileBubbleSort(alt, j, i);
        ArrayList arraylist = vals;
        vals = alt;
        alt = arraylist;
        displayChanged();
        DBManager.save(getCurrDir(), vals);
    }

 static void changed()
    {
        ListSelectionModel listselectionmodel = table.getSelectionModel();
        table.setSelectionModel(dummymodel);
        model.fireTableDataChanged();
        table.setSelectionModel(listselectionmodel);
        handle.repaint();
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
    }

    public void mouseExited(MouseEvent mouseevent)
    {
    }

    public void mousePressed(MouseEvent mouseevent)
    {
    }

    static void remfileBubbleSort(ArrayList arraylist, int i, int j)
    {
        int i1 = j - 1;
        boolean flag = false;
        do
        {
            for(int k = i; k < i1; k++)
            {
                int l = k + 1;
                RemFile remfile = (RemFile)arraylist.get(k);
                RemFile remfile1 = (RemFile)arraylist.get(l);
                if(remfile.name.compareToIgnoreCase(remfile1.name) > 0)
                {
                    arraylist.set(l, remfile);
                    arraylist.set(k, remfile1);
                    flag = true;
                }
            }

            if(flag)
            {
                flag = false;
                i1--;
            } else
            {
                return;
            }
        } while(true);
    }

    static String CREATE = "Create dir";
    static String RENAME = "Rename";
    static String DELETE = "Delete";
	static String RMDIR="Rmdir";
	static String CANCEL = "Cancel";
    static String DISCONNECT_TEXT = "    ";
    static Color ALTCOL = new Color(153, 153, 25);
    Dimension DIM;
    static String GO_UP_BUTTON;
    static File disks[];
    static JScrollPane sp;
    static JComboBox lookin;
    static ImageIcon diricon;
    static ImageIcon docicon;
    static LibButton goup;
    static LibButton download;
    static LibButton all;
    static LibButton invert;
    static LibButton options;
    Thread runner;
    static boolean ignore;
    static boolean ignoreselect;
    Image dirimg;
    Image diskimg;
    Image idleimg;
   Image unlisted;
    static JPanel center;
    static JPanel bottom;
    //static ArrayList vals = new ArrayList(1000);
    static ArrayList vals ;
//code change
static {	
	vals = new ArrayList(1000);
//vals.add("AccessRegulator.java");
}

    static ArrayList alt = new ArrayList(1000);
    static Model model;
    static JTable table;
    static TableColumn names;
    static TableColumn size;
    static RemFileFinder handle;
    PopupOptions popup;
    static int previdx;
    static boolean hasselection;
    static boolean oneselected;
    static boolean pending;
    static int pendingaction;
    static Object arg1;
    static Object arg2;
    static boolean disconnected;
    static ListSelectionModel dummymodel = new DefaultListSelectionModel();
    static RemFile rf;
}
