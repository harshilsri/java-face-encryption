import java.io.*;

// Referenced classes of package com.equitysoft.ftpgo:
//            FTPSite, ESZap, RemFile, SysProps

class SiteManager
{

    SiteManager()
    {
    }

    static void setMask(String s)
    {
        if(s == null)
            mask = null;
        else
            mask = s.getBytes();
    }

    static boolean exists(String s)
    {
        if(sites == null)
            return false;
        int i = sites.length;
        for(int j = 0; j < i; j++)
            if(sites[j].name.equals(s))
                return true;

        return false;
    }

    static void loadSites()
    {
        if(!SITE_FILE.exists())
            return;
        try
        {
            DataInputStream datainputstream = new DataInputStream(new FileInputStream(SITE_FILE));
            int i = datainputstream.readInt();
            sites = new FTPSite[i];
            for(int j = 0; j < i; j++)
                sites[j] = new FTPSite(convertIn(datainputstream), convertIn(datainputstream), convertIn(datainputstream), datainputstream.readBoolean(), convertIn(datainputstream), datainputstream.readBoolean());

            datainputstream.close();
        }
        catch(IOException ioexception)
        {
            new ESZap("Error reading sites file");
        }
    }

    static String convertIn(DataInputStream datainputstream)
        throws IOException
    {
        int i = datainputstream.readInt();
        byte abyte0[] = new byte[i];
        datainputstream.read(abyte0, 0, i);
        if(mask == null)
        {
            for(int j = 0; j < i; j++)
                abyte0[j] -= INC;

        } else
        {
            useMask(abyte0);
        }
        return new String(abyte0);
    }

    static void useMask(byte abyte0[])
    {
        int i = abyte0.length;
        int j = 0;
        int k = mask.length;
        for(int l = 0; l < i; l++)
        {
            abyte0[l] ^= mask[j];
            if(++j == k)
                j = 0;
        }

    }

    static RemFile[] getSites()
    {
        int i = sites != null ? sites.length : 0;
        RemFile aremfile[] = new RemFile[i + 2];
        aremfile[0] = RemFile.getIdle();
        aremfile[1] = RemFile.getUnlisted();
        if(i == 0)
            return aremfile;
        for(int j = 0; j < i; j++)
            aremfile[j + 2] = new RemFile(2, sites[j].name, 0L, sites[j]);

        return aremfile;
    }

    static FTPSite[] getSitesArray()
    {
        return sites;
    }

    static void writeSites()
    {
        if(sites == null)
            return;
        if(SITE_FILE.exists())
            SITE_FILE.delete();
        try
        {
            DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(SITE_FILE));
            int i = sites.length;
            dataoutputstream.writeInt(i);
            for(int j = 0; j < i; j++)
            {
                FTPSite ftpsite = sites[j];
                writeOut(dataoutputstream, ftpsite.name);
                writeOut(dataoutputstream, ftpsite.url);
                writeOut(dataoutputstream, ftpsite.username);
                dataoutputstream.writeBoolean(ftpsite.pprompt);
                writeOut(dataoutputstream, ftpsite.pass);
                dataoutputstream.writeBoolean(ftpsite.passive);
            }

            dataoutputstream.close();
        }
        catch(IOException ioexception)
        {
            new ESZap("Error writing sites file");
        }
    }

    static void writeOut(DataOutputStream dataoutputstream, String s)
        throws IOException
    {
        byte abyte0[] = s.getBytes();
        int i = abyte0.length;
        dataoutputstream.writeInt(i);
        if(mask == null)
        {
            for(int j = 0; j < i; j++)
                abyte0[j] += INC;

        } else
        {
            useMask(abyte0);
        }
        dataoutputstream.write(abyte0, 0, i);
    }

    static void addSite(FTPSite ftpsite)
    {
        if(sites == null)
        {
            sites = new FTPSite[1];
            sites[0] = ftpsite;
            writeSites();
            return;
        }
        int i = sites.length;
        FTPSite aftpsite[] = new FTPSite[i + 1];
        int j = 0;
        boolean flag = false;
        for(int k = 0; k < i; k++)
        {
            FTPSite ftpsite1 = sites[k];
            if(!flag && ftpsite.name.compareTo(ftpsite1.name) <= 0)
            {
                aftpsite[j] = ftpsite;
                j++;
                aftpsite[j] = ftpsite1;
                flag = true;
            } else
            {
                aftpsite[j] = ftpsite1;
            }
            j++;
        }

        if(!flag)
            aftpsite[i] = ftpsite;
        sites = aftpsite;
        writeSites();
    }

    static void deleteSite(int i)
    {
        int j = sites.length;
        FTPSite aftpsite[] = new FTPSite[j - 1];
        int k = 0;
        for(int l = 0; l < j; l++)
            if(l != i)
            {
                aftpsite[k] = sites[l];
                k++;
            }

        sites = aftpsite;
        writeSites();
    }

    static byte mask[];
    static File SITE_FILE;
    static FTPSite sites[];
    static int INC = 156;

    static 
    {
        SITE_FILE = new File(SysProps.PROPS_DIR, "sites.fgf");
    }
}
