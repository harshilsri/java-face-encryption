import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.*;


// Referenced classes of package com.equitysoft.ftpgo:
//            FTPConnection, RemFile, ESZap, FTPSite, 
//            ESDialog, Bar, FileFinder, RemFileFinder, 
//            LogManager, VerticalLayout, ESLabel, LibButton, 
//            ESUtils, Dummy

class FTPManager
{
    class RunProgress extends ESDialog
        implements ActionListener
    {

        void inc(int i)
        {
            sofar += i;
            pb.setValue(sofar);
            loop++;
            if(loop == 5)
            {
                FTPManager.sb.setLength(0);
                loop = 0;
                FTPManager.sb.append(ESUtils.asK(sofar)).append(" of ").append(totstr);
                pb.setString(FTPManager.sb.toString());
                FTPManager.sb.setLength(0);
                long l = (System.currentTimeMillis() - init) / 1000L;
                if(l > 0L)
                {
                    long l1 = (long)sofar / l;
                    long l2 = (long)(size - sofar) / l1;
                    FTPManager.sb.append(FTPManager.asTime(l2)).append(" remaining at ").append(ESUtils.asK(l1)).append("/Sec");
                    txt2.setText(FTPManager.sb.toString());
                }
            }
        }

        void end()
        {
            closeDialog();
            FTPManager.logEvent("Transfer complete, " + FTPManager.curridx + " of " + FTPManager.filenum + " files processed");
        }

        void setBegin(String s)
        {
            FTPManager.sb.setLength(0);
            FTPManager.sb.append("Processing '").append(s).append("' (");
            FTPManager.sb.append(FTPManager.curridx).append(" of ").append(FTPManager.filenum).append(')');
            txt.setText(FTPManager.sb.toString());
        }

        public void actionPerformed(ActionEvent actionevent)
        {
            if(actionevent.getSource() instanceof LibButton)
            {
                stopAction();
            } else
            {
                FTPManager.rp = null;
                closeDialog();
                noResponse();
            }
        }

        void finalCall()
        {
            stopAction();
        }

        void stopAction()
        {
            if(FTPManager.curr != null)
            {
                FTPManager.curr.abort();
                txt2.setText(ESUtils.EMPTY);
                txt.setText("Waiting for server response");
                Timer timer = new Timer(30000, this);
                timer.setRepeats(false);
            }
        }

        ESLabel txt;
        ESLabel txt2;
        int size;
        int sofar;
        JProgressBar pb;
        int loop;
        long init;
        String totstr;
        LibButton lb;

        RunProgress(int i, String s, int j, Object obj)
        {
            super(s, true);
            FTPManager.rp = this;
            size = i;
            totstr = ESUtils.asK(i);
            super.jp.setLayout(new VerticalLayout());
            JPanel jpanel = new JPanel();
            jpanel.add(txt = new ESLabel("   Beginning processing .....   "));
            super.jp.add(jpanel);
            JPanel jpanel1 = new JPanel();
            jpanel1.setLayout(new FlowLayout(1, 10, 5));
            jpanel1.add(pb = new JProgressBar(0, i));
            pb.setPreferredSize(new Dimension(300, 25));
            pb.setStringPainted(true);
            super.jp.add(jpanel1);
            super.jp.add(txt2 = new ESLabel("                        "));
            JPanel jpanel2 = new JPanel();
            lb = new LibButton("Cancel");
            jpanel2.add(lb);
            lb.addActionListener(this);
            super.jp.add(jpanel2);
            FTPManager.curr.perform(j, obj);
            loop = 0;
            init = System.currentTimeMillis();
            finishOff();
        }
    }

    class MsgBox extends ESDialog
        implements ActionListener
    {

        void setMsg(String s)
        {
            txt.setText(s);
        }

        public void actionPerformed(ActionEvent actionevent)
        {
            stopAction();
        }

        void finalCall()
        {
            stopAction();
        }

        void stopAction()
        {
            if(FTPManager.curr != null)
            {
                FTPManager.curr.abort();
                txt.setText("Waiting for server response");
            }
        }

        ESLabel txt;

        MsgBox(String s)
        {
            super("Current activity");
            FTPManager.mb = this;
            super.jp.setLayout(new BorderLayout());
            super.jp.add(new Dummy(10), "North");
            JPanel jpanel = new JPanel();
            jpanel.setLayout(new FlowLayout(1, 10, 10));
            jpanel.add(txt = new ESLabel("       " + s + "       "));
            super.jp.add(jpanel, "Center");
            JPanel jpanel1 = new JPanel();
            LibButton libbutton = new LibButton("Cancel");
            jpanel1.add(libbutton);
            libbutton.addActionListener(this);
            super.jp.add(jpanel1, "South");
            finishOff();
        }
    }


    FTPManager()
    {
    }

    static boolean isAnonymous()
    {
        return site != null && site.username.equalsIgnoreCase("anonymous");
    }

    static void setMsgBox(String s)
    {
        if(mb != null)
            if(s == null)
            {
                mb.closeDialog();
                mb = null;
            } else
            {
                mb.setMsg(s);
            }
    }

    static void setToDisconnect()
    {
        if(curr != null)
            curr.terminate();
        curr = null;
        site = null;
        started = -1L;
        Bar.setHeader();
    }

    static long getSessionStart()
    {
        if(curr == null)
            return -1L;
        else
            return started;
    }

    static void siteSelected(RemFile remfile)
    {
        FTPConnection ftpconnection = new FTPConnection(remfile);
        site = remfile.site;
        curr = ftpconnection;
        started = System.currentTimeMillis();
        Bar.setSite(site);
        perform(0, vals);
         //System.out.println("FTPManager 226 :"+vals);
        handle. new MsgBox("Attempting FTP connection ......     ");
    }

    static void perform(int i, Object obj)
    {
	        switch(i)
        {
		   case 9: // '\t'
            RemFile  aremfile[] = (RemFile[])obj;
            long l = 0L;
             int j = aremfile.length;
            if(j == 0)
            {
                logError("Empty download selection");
                return;
            }
            for(int i1 = 0; i1 < j; i1++)
                l += aremfile[i1].length();

            remfilelist = aremfile;
            curridx = 0;
            filenum = j;
            handle. new RunProgress((int)l, "Download progress", 9, null);
            FileFinder.handle.refresh();
//code change            
			for (int s=0;s<aremfile.length;s++)
		    curr.download(aremfile[s]);
            break;

        case 8: // '\b'
            File afile[] = (File[])obj;
            long l1 = 0L;
            int k = afile.length;
            for(int j1 = 0; j1 < k; j1++)
                l1 += afile[j1].length();

            filelist = afile;
            curridx = 0;
            filenum = k;
            prev = null;
         	handle. new RunProgress((int)l1, "Upload progress", 8, null);
            RemFileFinder.sortItems();
            for (int s=0;s<afile.length;s++)
		    curr.upload(afile[s]);
            break;

         case 11: // '\013'
            handle. new MsgBox(FTPConnection.LIST_TXT);
            // fall through

        case 10: // '\n'
        default:
            curr.perform(i, obj);
            break;
        }
    }

    static File getNextFile()
    {
        if(prev != null)
            RemFileFinder.addOrReplace(new RemFile(4, RemFile.checkName(prev.getName()), prev.length(), null));
        if(curridx == filenum)
        {
            rp.end();
            rp = null;
            return null;
        } else
        {
            curridx++;
            prev = filelist[curridx - 1];
            rp.setBegin(prev.getName());
            return prev;
        }
    }

    static RemFile getNextRemFile()
    {
        if(curridx == filenum)
        {
            rp.end();
            rp = null;
            return null;
        } else
        {
            curridx++;
            rp.setBegin(remfilelist[curridx - 1].getName());
            return remfilelist[curridx - 1];
        }
    }

    static boolean isConnected(FTPSite ftpsite)
    {
        if(site == null)
            return false;
        return ftpsite == site;
    }

    static boolean isConnected()
    {
        return curr != null;
    }

    static void closeDialogs()
    {
        if(mb != null)
            mb.closeDialog();
        if(rp != null)
            rp.closeDialog();
    }

    static synchronized void logError(String s)
    {
        closeDialogs();
        RemFileFinder.pendingError(s);
        if(s == CANCEL)
            LogManager.writeTimeMsg(s);
        else
            LogManager.writeTimeMsg("Error - " + s);
    }

    static synchronized void logEvent(String s)
    {
        closeDialogs();
        LogManager.writeTimeMsg(s);
        RemFileFinder.cancelPending();
    }

    static void incBar(int i)
    {
        rp.inc(i);
    }

    static void didAbort()
    {
        if(rp != null)
        {
            rp.closeDialog();
            rp = null;
        } else
        if(mb != null)
        {
            mb.closeDialog();
            mb = null;
        }
    }

    void noResponse()
    {
        new ESZap("The server isn't responding, the connection may be broken");
    }

    static String asTime(long l)
    {
        if(l < 60L)
            return l + " seconds";
        if(l < 3600L)
        {
            String s = null;
            String s2 = null;
            int i = (int)(l / 60L);
            if(i == 1)
                s2 = "1 minute ";
            else
                s2 = i + " minutes ";
            int k = (int)(l - (long)(i * 60));
            if(k == 0)
                s = "";
            else
            if(k == 1)
                s = "1 second";
            else
                s = k + " seconds";
            return s2 + s;
        }
        String s1 = null;
        String s3 = null;
        int j = (int)(l / 3600L);
        if(j == 1)
            s3 = "1 hour ";
        else
            s3 = j + " hours ";
        int i1 = (int)((l - (long)(j * 3600)) / 60L);
        if(i1 == 0)
            s1 = "";
        else
        if(i1 == 1)
            s1 = "1 minute";
        else
            s1 = i1 + " minutes";
        return s3 + s1;
    }

    static String CANCEL = "Operation cancelled by user";
    static FTPSite site;
    static FTPConnection curr;
    static ArrayList vals ;
//code change
static {	
	vals = new ArrayList(1000);
//vals.add("AccessRegulator.java");
}
    static RunProgress rp;
    static MsgBox mb;
    static FTPManager handle = new FTPManager();
    static int filenum;
    static File filelist[];
    static RemFile remfilelist[];
    static int curridx;
    static File prev;
    static long started;
    static StringBuffer sb = new StringBuffer(30);
}
