import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.StringTokenizer;
import javax.swing.*;

// Referenced classes of package com.equitysoft.ftpgo:
//            ESZap, RemFileFinder, FileFinder

class ESUtils extends Component
{

    ESUtils()
    {
    }

    static void enableDisplay()
    {
        RemFileFinder.handle.setEnabled(true);
        FileFinder.handle.setEnabled(true);
    }

    static void disableDisplay()
    {
        RemFileFinder.handle.setEnabled(false);//false is changed to true
        FileFinder.handle.setEnabled(false);
    }

    static String asK(long l)
    {
        sb.setLength(0);
        if(l < 103L)
        {
            sb.append(l);
            if(l == 1L)
                sb.append("byte");
            else
                sb.append("bytes");
            return sb.toString();
        }
        if(l < 0x100000L)
        {
            sb.append(nf.format((float)l / 1024F)).append("K");
            return sb.toString();
        } else
        {
            float f = (float)l / 1048576F;
            sb.append(nf.format(f)).append("MB");
            return sb.toString();
        }
    }

    static JMenuItem getMI(String s, ActionListener actionlistener)
    {
        JMenuItem jmenuitem = new JMenuItem(s);
        jmenuitem.addActionListener(actionlistener);
        return jmenuitem;
    }

    static void deleteAll(File file)
    {
        if(!file.exists())
            return;
        File afile[] = file.listFiles();
        int i = afile.length;
        for(int j = 0; j < i; j++)
            afile[j].delete();

    }

    static boolean saveStringAsFile(String s, File file)
    {
        StringTokenizer stringtokenizer = new StringTokenizer(s, "\r\n");
        if(file.exists())
            file.delete();
        try
        {
            BufferedWriter bufferedwriter = new BufferedWriter(new FileWriter(file));
            for(; stringtokenizer.hasMoreTokens(); bufferedwriter.write("\r\n", 0, 2))
            {
                String s1;
                bufferedwriter.write(s1 = stringtokenizer.nextToken(), 0, s1.length());
            }

            bufferedwriter.close();
        }
        catch(IOException ioexception)
        {
            new ESZap("Error writing file " + file.getAbsolutePath());
            return false;
        }
        return true;
    }

    static File checkExtension(File file, String s)
    {
        String s1 = file.getName();
        int i = s1.indexOf('.');
        int j = s1.length() - 1;
        if(i == j)
            return new File(file.getParent(), s1 + s);
        if(i > -1)
            return file;
        else
            return new File(file.getParent(), s1 + '.' + s);
    }

    static String checkTextExtension(String s, String s1)
    {
        int i = s.indexOf('.');
        int j = s.length() - 1;
        if(i == j)
            return s + s1;
        if(i > -1)
            return s;
        else
            return s + '.' + s1;
    }

    static String getSystemFileAsString(String s)
    {
        char c = '\u0100';
        byte abyte0[] = new byte[1];
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(c);
        byte byte0 = 13;
        try
        {
            InputStream inputstream = cls.getResourceAsStream(s);
            if(inputstream == null)
                return null;
            while(inputstream.read(abyte0, 0, 1) > -1) 
                if(abyte0[0] != byte0)
                    bytearrayoutputstream.write(abyte0, 0, 1);
            inputstream.close();
        }
        catch(IOException ioexception)
        {
            return null;
        }
        return bytearrayoutputstream.toString();
    }

    static String getFileAsString(File file)
    {
        if(!file.exists())
            return null;
        int i = (int)file.length();
        byte abyte0[] = new byte[i];
        try
        {
            BufferedInputStream bufferedinputstream = new BufferedInputStream(new FileInputStream(file));
            bufferedinputstream.read(abyte0, 0, i);
            bufferedinputstream.close();
        }
        catch(IOException ioexception)
        {
            return null;
        }
        return new String(abyte0, 0, i);
    }

    static URL getESURL(String s)
    {
        if(cls == null)
            cls = handle.getClass();
        URL url = cls.getResource(s);
        if(url == null)
            return null;
        else
            return url;
    }

   static Image getSystemImage(String s)
	{
        return loadImage(Toolkit.getDefaultToolkit().getImage(getESURL(s)));
    }

    static Image getImage(File file) 
    {
        return loadImage(Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath()));
    }

    static Image loadImage(Image image)
    {
        try
        {
            if(tracker == null)
                tracker = new MediaTracker(handle);
            tracker.addImage(image, 0);
            tracker.waitForAll();
        }
        catch(Exception exception)
        {
            return null;
        }
        return image;
    }

    static void copyFile(File file, File file1)
    {
        byte abyte0[] = new byte[512];
        try
        {
            FileInputStream fileinputstream = new FileInputStream(file);
            FileOutputStream fileoutputstream = new FileOutputStream(file1);
            int i;
            while((i = fileinputstream.read(abyte0)) > 0) 
                fileoutputstream.write(abyte0, 0, i);
            fileinputstream.close();
            fileoutputstream.close();
        }
        catch(IOException ioexception)
        {
            new ESZap("Error " + ioexception.getMessage() + " copying files");
        }
    }

    static void textBubbleSort(String as[], int i)
    {
        int l = i - 1;
        boolean flag = false;
        do
        {
            for(int j = 0; j < l; j++)
            {
                int k = j + 1;
                if(as[j].compareToIgnoreCase(as[k]) > 0)
                {
                    String s = as[k];
                    as[k] = as[j];
                    as[j] = s;
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

    static Font MSG_FONT = new Font("dialog", 1, 12);
    static String PRODUCT = " Java FTP";
    static MediaTracker tracker;
    static Class cls;
    static String EMPTY = "";
    static StringBuffer sb = new StringBuffer(6);
    static NumberFormat nf;
    static ESUtils handle = new ESUtils();

  static 
    {
        nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(1);
        nf.setMaximumFractionDigits(1);
    }
}

