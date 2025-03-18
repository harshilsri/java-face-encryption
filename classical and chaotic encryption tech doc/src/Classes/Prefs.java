import java.io.*;
import java.util.Hashtable;
import java.util.Properties;

// Referenced classes of package com.equitysoft.ftpgo:
//            ESZap, MiscMenu, ESUtils, SysProps

class Prefs
{

    Prefs()
    {
    }

    static String get(String s)
    {
        if(mainlist == null)
        {
            mainlist = new Properties();
            if(!PREFS.exists())
            {
                mainlist.put("sotimeout", "20");
                mainlist.put("initialized", FALSE_TXT);
                //mainlist.put(MiscMenu.SECURE, ZERO);
                //mainlist.put(MiscMenu.NUMBER, ZERO);
                if(!writePrefs())
                    return null;
            } else
            {
                try
                {
                    mainlist.load(new FileInputStream(PREFS));
                }
                catch(Exception exception)
                {
                    new ESZap("System preferences file not found");
                    return null;
                }
            }
        }
        Object obj = mainlist.get(s);
        if(obj == null)
            System.out.println("Corrupt file error - Can't find property " + s + " - reload defaults & restart");
        return (String)obj;
    }

    static boolean writePrefs()
    {
        try
        {
            mainlist.store(new FileOutputStream(PREFS), ESUtils.PRODUCT + " preferences");
        }
        catch(Exception exception)
        {
            new ESZap("Error writing preferences file");
            return false;
        }
        return true;
    }

    static void set(String s, String s1)
    {
        mainlist.put(s, s1);
    }

    static void setBoolean(String s, boolean flag)
    {
        if(flag)
            mainlist.put(s, TRUE_TXT);
        else
            mainlist.put(s, FALSE_TXT);
    }

    static boolean getBoolean(String s)
    {
        return get(s).equals(TRUE_TXT);
    }

    static float getFloat(String s)
    {
        return Float.parseFloat(get(s));
    }

    static void setFloat(String s, float f)
    {
        mainlist.put(s, String.valueOf(f));
    }

    static void setInt(String s, int i)
    {
        mainlist.put(s, String.valueOf(i));
    }

    static int getInt(String s)
    {
        return Integer.parseInt(get(s));
    }

    static final String CROSS = "C";
    static String TRUE_TXT = "T";
    static String FALSE_TXT = "F";
    static String ZERO = "0";
    static Properties mainlist;
    static File PREFS;

    static 
    {
        PREFS = new File(SysProps.PROPS_DIR, "prefs.fgf");
    }
}
