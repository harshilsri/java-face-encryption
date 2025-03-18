import java.io.File;
// Referenced classes of package com.equitysoft.ftpgo:
//            SiteManager
class SysProps
{
    SysProps()
    {
    }
    static void setDirs()
    {
        CURR_DIR = new File(System.getProperty("user.dir"));
        PROPS_DIR = new File(CURR_DIR, "ftpgo_info");
        if(!PROPS_DIR.exists())
        {
            if(SiteManager.SITE_FILE.exists())
                SiteManager.SITE_FILE.delete();
            PROPS_DIR.mkdir();
        }
        TEMP = new File(PROPS_DIR, "temp");
        if(!TEMP.exists())
            TEMP.mkdir();
    }
    static File CURR_DIR;
    static File PROPS_DIR;
    static File TEMP;
}
