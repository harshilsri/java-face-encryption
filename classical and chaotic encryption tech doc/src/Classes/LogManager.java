import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

// Referenced classes of package com.equitysoft.ftpgo:
//            ESZap, SysProps

class LogManager
{

    LogManager()
    {
    }

    static void startLog()
    {
        if(rf != null)
            return;
        if(LOG.exists())
            LOG.delete();
        try
        {
            rf = new RandomAccessFile(LOG, "rw");
            SimpleDateFormat simpledateformat = new SimpleDateFormat("MMM dd, yyyy - H:mm");
            sb.setLength(0);
            sb.append("Logging begun ").append(simpledateformat.format(new Date()));
            rf.writeUTF(sb.toString());
        }
        catch(IOException ioexception)
        {
            new ESZap("Unable to open logging file");
        }
    }

    static void endLog()
    {
        try
        {
            if(rf != null)
                rf.close();
        }
        catch(IOException ioexception)
        {
            new ESZap("Error closing");
        }
    }

    static String getContents()
    {
        boolean flag = false;
        if(!LOG.exists())
            return "The log file is empty";
        sb.setLength(0);
        try
        {
            if(rf == null)
            {
                rf = new RandomAccessFile(LOG, "r");
                flag = true;
            }
            rf.seek(0L);
            long l;
            for(l = rf.length() - 1L; rf.getFilePointer() < l; sb.append(rf.readUTF()).append('\n'));
            if(flag)
            {
                rf.close();
                rf = null;
            } else
            {
                rf.seek(l + 1L);
            }
        }
        catch(IOException ioexception)
        {
            new ESZap("Error reading log file");
        }
        return sb.toString();
    }

    static void writeTimeMsg(String s)
    {
        startLog();
        sb.setLength(0);
        sb.append(sdf.format(new Date())).append(" - ").append(s);
        try
        {
            rf.writeUTF(sb.toString());
        }
        catch(IOException ioexception)
        {
            new ESZap("Error writing to log - the disk may be full");
        }
    }

    static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
    static File LOG;
    static StringBuffer sb = new StringBuffer(256);
    static RandomAccessFile rf;

    static 
    {
        LOG = new File(SysProps.PROPS_DIR, "log.fgf");
    }
}
