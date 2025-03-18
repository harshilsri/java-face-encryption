import java.io.*;
import java.util.ArrayList;

// Referenced classes of package com.equitysoft.ftpgo:
//            RemFile, FTPManager, SysProps

class DBManager
{

    DBManager()
    {
    }

    static void save(RemFile remfile, ArrayList arraylist)
    {
        int i = arraylist.size();
        File file = new File(DB, remfile.path);
        File file1 = new File(file, "dir.fgf");
        if(i == 0)
        {
            if(file1.exists())
                file1.delete();
            if(file.exists())
                file.delete();
            return;
        }
        if(!file.exists())
            file.mkdir();
        try
        {
            DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file1));
            dataoutputstream.writeLong(System.currentTimeMillis());
            dataoutputstream.writeInt(i);
            for(int j = 0; j < i; j++)
            {
                RemFile remfile1 = (RemFile)arraylist.get(j);
                dataoutputstream.writeInt(remfile1.type);
                dataoutputstream.writeUTF(remfile1.name);
                dataoutputstream.writeLong(remfile1.size);
            }

            dataoutputstream.close();
        }
        catch(IOException ioexception)
        {
            FTPManager.logError("Unable to write directory contents to disk");
        }
    }

    static boolean canLoad(RemFile remfile, long l)
    {
        File file = new File(DB, remfile.path);
        if(!file.exists())
            return false;
        File file1 = new File(file, "dir.fgf");
        if(!file1.exists())
            return false;
        long l1 = 0L;
        try
        {
            DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1));
            l1 = datainputstream.readLong();
            datainputstream.close();
        }
        catch(IOException ioexception)
        {
            FTPManager.logError("Unable to write directory contents to disk");
        }
        return l1 >= l;
    }

    static void delete(String s)
    {
        File file = new File(DB, s);
        if(!file.exists())
            return;
        File file1 = new File(file, "dir.fgf");
        if(file1.exists())
            file1.delete();
        file.delete();
    }

    static boolean hasContents(String s, long l)
    {
        File file = new File(DB, s);
        if(!file.exists())
            return false;
        File file1 = new File(file, "dir.fgf");
        if(!file1.exists())
            return false;
        int i = 0;
        try
        {
            DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1));
            long l1 = datainputstream.readLong();
            if(l > -1L && l1 < l)
            {
                datainputstream.close();
                return false;
            }
            i = datainputstream.readInt();
            datainputstream.close();
        }
        catch(IOException ioexception)
        {
            FTPManager.logError("Unable to measure directory contents");
        }
        if(i == 0)
        {
            file1.delete();
            file.delete();
            return false;
        } else
        {
            return true;
        }
    }

    static boolean load(RemFile remfile, ArrayList arraylist)
    {
        File file = new File(DB, remfile.path);
        if(!file.exists())
            return false;
        File file1 = new File(file, "dir.fgf");
        if(!file1.exists())
            return false;
        try
        {
            DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1));
            long l = datainputstream.readLong();
            int i = datainputstream.readInt();
            for(int j = 0; j < i; j++)
                arraylist.add(new RemFile(datainputstream.readInt(), datainputstream.readUTF(), datainputstream.readLong(), null));

            datainputstream.close();
        }
        catch(IOException ioexception)
        {
            FTPManager.logError("Unable to write directory contents to disk");
        }
        return true;
    }

    static boolean hasDir(String s)
    {
        File file = new File(DB, s);
        return file.exists();
    }

    static void deleteUnlisted()
    {
        deleteDir("unlisted");
    }

    static void deleteDir(String s)
    {
        File file = new File(DB, s);
        if(file.exists())
            deleteContents(file);
    }

    static void deleteContents(File file)
    {
        File afile[] = file.listFiles();
        if(afile == null)
        {
            file.delete();
            return;
        }
        int i = afile.length;
        for(int j = 0; j < i; j++)
        {
            File file1 = afile[j];
            if(file1.isDirectory())
                deleteContents(file1);
            else
                file1.delete();
        }

        file.delete();
    }

    static File DB;
    static final String NAME = "dir.fgf";

    static 
    {
        DB = new File(SysProps.PROPS_DIR, "DB");
        if(!DB.exists())
            DB.mkdir();
    }
}
