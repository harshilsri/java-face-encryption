
// Referenced classes of package com.equitysoft.ftpgo:
//            FTPSite

class RemFile
{

    RemFile(int i, String s, long l, FTPSite ftpsite)
    {
        type = i;
        name = s;
        size = l;
        site = ftpsite;
        if(i == 2)
            path = checkName(s);
    }

    static String checkName(String s)
    {
        if(s.indexOf(' ') < 0)
            return s;
        else
            return s.replace(' ', '_');
    }

    static RemFile getIdle()
    {
        return new RemFile(0, "Disconnected", 0L, null);
    }

    static RemFile getUnlisted()
    {
        RemFile remfile = new RemFile(1, "Unlisted site", 0L, null);
        remfile.path = "unlisted";
        return remfile;
    }

    boolean isIdle()
    {
        return type == 0;
    }

    boolean isSite()
    {
        return type < 3;
    }

    boolean isDirectory()
    {
        return type == 3;
    }

    long length()
    {
        return size;
    }

    String getName()
    {
        return name;
    }

    static final int IDLE = 0;
    static final int UNLISTED = 1;
    static final int SITE = 2;
    static final int DIRECTORY = 3;
    static final int FILE = 4;
    static final String UL = "unlisted";
    FTPSite site;
    int type;
    String name;
    long size;
    String path;
}
