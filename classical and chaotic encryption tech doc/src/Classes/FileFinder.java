import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

// Referenced classes of package com.equitysoft.ftpgo:
//            LibButton, IconCell, SizeCell, ESZap, 
//            ESUtils, RemFileFinder, FTPManager

class FileFinder extends JPanel
    implements ActionListener, MouseListener
{
    class LCRenderer extends Component
        implements ListCellRenderer
    {

        public Component getListCellRendererComponent(JList jlist, Object obj, int i, boolean flag, boolean flag1)
        {
            File file = (File)obj;
			 if(getDepth(file) > 0)
            {
                isdisk = false;
                txt = file.getName();
            } else
            {
                isdisk = true;
                txt = file.getAbsolutePath();
            }
            if(i < 0)
                indent = 0;
            else
                indent = 1 + getDepth(file) * 8;
            selected = flag;
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
            g.setColor(selected ? FileFinder.ALTCOL : back);
            g.fillRect(0, 0, DIM.width + 30, DIM.height);
            if(isdisk)
                g.drawImage(FileFinder.diskimg, indent, 2, null);
            else
                g.drawImage(FileFinder.dirimg, indent, 2, null);
            g.setFont(ESUtils.MSG_FONT);
            g.setColor(Color.black);
            g.drawString(txt, indent + 21, 15);
        }

        public Dimension getPreferredSize()
        {
            return DIM;
        }

        boolean isdisk;
        Color back;
        String txt;
        int indent;
        boolean selected;
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
            return vals.size();
        }

        public Object getValueAt(int i, int j)
        {
            if(vals.size() == 0)
                return ESUtils.EMPTY;
            if(j == 0)
                return vals.get(i);
            File file = (File)vals.get(i);
            if(file.isDirectory())
                return ESUtils.EMPTY;
            else
                return ESUtils.asK(file.length());
        }

        Model()
        {
        }
    }


    FileFinder(String s) throws Exception
    {
        super(false);
        DIM = new Dimension(200, 20);
        vals = new ArrayList(1000);
        handle = this;
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), s));
        dirimg = ESUtils.getSystemImage("dir.gif");
        diricon = new ImageIcon(dirimg);
        docicon = new ImageIcon(ESUtils.getSystemImage("file1.gif"));
        diskimg = ESUtils.getSystemImage("drive.gif");
        setLayout(new BorderLayout());
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new FlowLayout(0, 4, 4));
        jpanel.add(new JLabel("Look in:"));
        jpanel.add(lookin = new JComboBox());
        lookin.setRenderer(new LCRenderer());
        addRoots();
        jpanel.add(goup = new LibButton(null, "FILE.GIF"));
        goup.addActionListener(this);
        goup.setPreferredSize(SQ);
        goup.setToolTipText("Go up a directory level");
        add(jpanel, "North");
        JPanel jpanel1 = new JPanel();
        jpanel1.setLayout(new BorderLayout());
        table = new JTable(model = new Model());
        table.addMouseListener(this);
        names = table.getColumnModel().getColumn(0);
        names.setPreferredWidth(210);
        names.setCellRenderer(new IconCell());
        size = table.getColumnModel().getColumn(1);
        size.setPreferredWidth(3);
        size.setCellRenderer(new SizeCell());
        table.setRowSelectionAllowed(true);
        JScrollPane jscrollpane = new JScrollPane(table);
        jscrollpane.getViewport().setBackground(Color.white);
        jpanel1.add(jscrollpane, "Center");
        jscrollpane.setPreferredSize(new Dimension(200, 350));
        add(jpanel1, "Center");
        JPanel jpanel2 = new JPanel();
        jpanel2.setLayout(new FlowLayout(1, 5, 0));
        jpanel2.add(all = new LibButton("All", this));
        all.setToolTipText("Select all files");
        jpanel2.add(invert = new LibButton("Inv", this));
        invert.setToolTipText("Invert selection");
        jpanel2.add(upload = new LibButton("Upload", "scrmapiconright.gif", this));
        upload.setToolTipText("Upload selected files");
        add(jpanel2, "South");
        setDir(currdir);
        setUpDir();
        lookin.addActionListener(this);
    }

    void setUpDir()
    {
        ignore = true;
        int i = lookin.getItemCount();
        String s = currdir.getAbsolutePath();
        int j = 0;
        for(j = 0; j < i; j++)
        {
            File file = (File)lookin.getItemAt(j);
            if(s.startsWith(file.getAbsolutePath()))
                break;
        }

        int k = getDepth(currdir);
        if(k == 0)
        {
            lookin.setSelectedIndex(j);
            ignore = false;
            return;
        }
        if(j == i)
        {
            new ESZap("A storage device is missing - unable to load the 'Lookin' directories.");
            return;
        }
        j++;
        File file1 = currdir;
        do
        {
            if(j == i)
                lookin.addItem(file1);
            else
                lookin.insertItemAt(file1, j);
            file1 = file1.getParentFile();
            if(file1.getParentFile() != null)
            {
                i = -1;
            } else
            {
                lookin.setSelectedItem(currdir);
                ignore = false;
                return;
            }
        } while(true);
    }

    void setDir(File file)
    {
        if(getDepth(file) == 0)
            goup.setEnabled(false);
        else
            goup.setEnabled(true);
        upload.setEnabled(false);
        vals.clear();
        File afile[] = file.listFiles();
        if(afile == null)
            return;
        int i = afile.length;
        File afile1[] = new File[i];
        int j = 0;
        for(int k = 0; k < i; k++)
        {
            File file1 = afile[k];
            if(file1.isDirectory())
            {
                afile1[j] = file1;
                j++;
            }
        }

        if(j > 1)
            fileBubbleSort(afile1, j);
        for(int l = 0; l < j; l++)
            vals.add(afile1[l]);

        int i1 = 0;
        for(int j1 = 0; j1 < i; j1++)
        {
            File file2 = afile[j1];
            if(!file2.isDirectory())
            {
                afile1[i1] = file2;
                i1++;
            }
        }

        if(i1 > 1)
        {
            fileBubbleSort(afile1, i1);
            for(int k1 = 0; k1 < i1; k1++)
                vals.add(afile1[k1]);

            buttonsOn();
        } else
        {
            buttonsOff();
        }
        displayChanged();
        currdir = file;
    }

    void buttonsOn()
    {
        invert.setEnabled(true);
        all.setEnabled(true);
    }

    void buttonsOff()
    {
        invert.setEnabled(false);
        all.setEnabled(false);
    }

    void refresh()
    {
        table.clearSelection();
        setDir(currdir);
    }

    static void deselectAll()
    {
        handle.table.clearSelection();
        handle.displayChanged();
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
      RemFileFinder.deselectAll();
        displayChanged();
    }

    void displayChanged()
    {
        int i = vals.size();
        int j = 0;
        long l = 0L;
        long l1 = 0L;
        int k = 0;
        for(int i1 = 0; i1 < i; i1++)
        {
            File file = (File)vals.get(i1);
            if(file.isDirectory())
            {
                if(table.isRowSelected(i1))
                    table.removeRowSelectionInterval(i1, i1);
            } else
            {
                long l2 = file.length();
                l += l2;
                k++;
                if(table.isRowSelected(i1))
                {
                    j++;
                    l1 += l2;
                }
            }
        }

        if(j == 0)
        {
            if(k == 0)
                names.setHeaderValue("No files in directory");
            else
            if(k == 1)
                names.setHeaderValue("1 file in directory");
            else
                names.setHeaderValue(k + " files in directory");
            if(k == 0)
                size.setHeaderValue(ESUtils.EMPTY);
            else
                size.setHeaderValue(ESUtils.asK(l));
            upload.setEnabled(false);
        } else
        {
            if(j == 1)
                names.setHeaderValue("1 file selected");
            else
                names.setHeaderValue(j + " files selected");
            size.setHeaderValue(ESUtils.asK(l1));
            if(FTPManager.isConnected() && !FTPManager.isAnonymous())
                upload.setEnabled(true);
        }
        ListSelectionModel listselectionmodel = table.getSelectionModel();
        table.setSelectionModel(dummymodel);
        model.fireTableDataChanged();
        table.setSelectionModel(listselectionmodel);
        repaint();
    }

    public void mouseClicked(MouseEvent mouseevent)
    {
        if(mouseevent.getClickCount() < 2)
            return;
        int i = table.rowAtPoint(mouseevent.getPoint());
        if(i < 0)
            return;
        File file = (File)vals.get(i);
        if(!file.isDirectory())
            return;
        setDir(file);
        int j = lookin.getItemCount();
        String s = file.getAbsolutePath();
        int k = 0;
        for(k = 0; k < j; k++)
        {
            File file1 = (File)lookin.getItemAt(k);
            if(s.startsWith(file1.getAbsolutePath()))
                break;
        }

        if(k == j - 1)
        {
            lookin.addItem(file);
            lookin.setSelectedIndex(j);
        } else
        {
            int l = 0;
            for(l = k; l < j; l++)
            {
                File file2 = (File)lookin.getItemAt(l);
                if(!s.startsWith(file2.getAbsolutePath()))
                    break;
            }

            lookin.insertItemAt(file, l);
            ignore = true;
            lookin.setSelectedItem(file);
            ignore = false;
        }
        table.clearSelection();
    }

    int getDepth(File file)
    {
        int i = 0;
        do
        {
            file = file.getParentFile();
            if(file != null)
                i++;
            else
                return i;
        } while(true);
    }

    void addRoots()
    {
        disks = File.listRoots();
        int i = disks.length;
        ignore = true;
        for(int j = 0; j < i; j++)
            lookin.addItem(disks[j]);

        ignore = false;
    }

    boolean isDisk(File file)
    {
        int i = disks.length;
        int j = 0;
        for(j = 0; j < i; j++)
            if(file.equals(disks[j]))
                break;

        return j < i;
    }

    void removeLookin(File file)
    {
        boolean flag = false;
        if(isDisk(file))
            flag = true;
        Vector vector = new Vector();
        int i = lookin.getItemCount();
        String s = file.getAbsolutePath();
        for(int j = 0; j < i; j++)
        {
            File file1 = (File)lookin.getItemAt(j);
            String s1 = file1.getAbsolutePath();
            if(!flag)
            {
                if(s1.startsWith(s) && !s1.equals(s))
                    vector.addElement(file1);
            } else
            if(!isDisk(file1))
                vector.addElement(file1);
        }

        i = vector.size();
        ignore = true;
        for(int k = 0; k < i; k++)
            lookin.removeItem(vector.elementAt(k));

        ignore = false;
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        RemFileFinder.deselectAll();
        Object obj = actionevent.getSource();
        if(obj instanceof JComboBox)
        {
            if(!ignore)
            {
                File file = (File)lookin.getSelectedItem();
                File afile[] = file.listFiles();
                if(afile == null)
                {
                    new ESZap("The device '" + file.getAbsolutePath() + "' is not accessible.");
                    ignore = true;
                    lookin.setSelectedItem(currdir);
                    ignore = false;
                    return;
                }
                removeLookin(file);
                setDir(file);
            }
        } else
        if(obj instanceof LibButton)
        {
            LibButton libbutton = (LibButton)obj;
            if(libbutton == goup)
            {
                if(getDepth(currdir) == 0)
                    return;
                File file1 = currdir.getParentFile();
                setDir(file1);
                removeLookin(file1);
                table.clearSelection();
            } else
            if(libbutton == upload)
            {
                if(RemFileFinder.stillPending())
                    return;
                int ai[] = table.getSelectedRows();
                int k = ai.length;
                File afile1[] = new File[k];
                for(int j1 = 0; j1 < k; j1++)
                    afile1[j1] = (File)vals.get(ai[j1]);

                FTPManager.perform(8, afile1);
            } else
            if(libbutton == all)
            {
                int i = vals.size();
                int l = 0;
                boolean flag = false;
                for(l = 0; l < i; l++)
                {
                    File file2 = (File)vals.get(l);
                    if(file2.isDirectory())
                        continue;
                    flag = true;
                    break;
                }

                if(flag)
                    table.setRowSelectionInterval(l, i - 1);
                displayChanged();
            } else
            if(libbutton == invert)
            {
                int j = vals.size();
                for(int i1 = 0; i1 < j; i1++)
                {
                    File file3 = (File)vals.get(i1);
                    if(!file3.isDirectory())
                        if(table.isRowSelected(i1))
                            table.removeRowSelectionInterval(i1, i1);
                        else
                            table.addRowSelectionInterval(i1, i1);
                }

                displayChanged();
            }
        }
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

    static void fileBubbleSort(File afile[], int i)
    {
        int l = i - 1;
        boolean flag = false;
        do
        {
            for(int j = 0; j < l; j++)
            {
                int k = j + 1;
                if(afile[j].getName().compareToIgnoreCase(afile[k].getName()) > 0)
                {
                    File file = afile[k];
                    afile[k] = afile[j];
                    afile[j] = file;
                    flag = true;
                }
            }

            if(flag)
            {
                flag = false;
                l--;
            } else
            {
                return;
            }
        } while(true);
    }

    static Color ALTCOL = new Color(153, 153, 25);
    static Dimension SQ = new Dimension(26, 26);
    Dimension DIM;
    static File disks[];
    JComboBox lookin;
    static File currdir;
    static ImageIcon diricon;
    static ImageIcon docicon;
    LibButton goup;
    LibButton upload;
    LibButton all;
    LibButton invert;
    Thread runner;
    boolean ignore;
    static Image dirimg;
    static Image diskimg;
    ArrayList vals;
    Model model;
    JTable table;
    TableColumn names;
    TableColumn size;
    static FileFinder handle;
    static ListSelectionModel dummymodel = new DefaultListSelectionModel();
}
