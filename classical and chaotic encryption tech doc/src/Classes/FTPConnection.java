import java.io.*;
import java.net.*;
import java.util.ArrayList;

// Referenced classes of package com.equitysoft.ftpgo:
//            RemFile, NamePair, FTPManager, FTPSite,
//            LogManager, SysProps, FileFinder, Prefs,
//            PASVReader, FTPGo, DirReader



class FTPConnection
    implements Runnable
{

    FTPConnection(RemFile remfile)
    {
        sb = new StringBuffer(20);
        fs = remfile.site;
        action = -1;
        Thread thread = new Thread(this);
        thread.start();
    }

    void perform(int i, Object obj)
    {
       action = i;
        arg = obj;
        synchronized(this)
        {
            notify();
        }
    }

    void terminate()
    {
        perform(10, null);
        try
        {
            if(control != null)
                control.close();
        }
        catch(Exception exception)
        {
            FTPManager.logError("Terminating socket connection");
        }
    }

    public void run()
    {
        int i;
        while(action == -1)
            try
            {
                synchronized(this)
                {
                    wait();
                }
            }
            catch(InterruptedException interruptedexception)
            {
                return;
            }
        i = action;
        action = -1;

        canrun = true;

              String s=connect();
				        if(s != null)
                  FTPManager.logError(s);
	      		else
          {
		       FTPManager.setMsgBox(LIST_TXT);
            String s1;
            if((s1 = listFiles((ArrayList)arg)) != null)
                FTPManager.logError(s1);
            else
                FTPManager.logEvent("Connected to " + fs.url);
        }

//RemFileFinder.vals=(ArrayList)arg;
//RemFileFinder.sp.show();

        String s2;
     /*  if((s2 = disconnect()) != null)
		{
          FTPManager.logError("disconnecting from " + fs.url);
	     }
	          FTPManager.logEvent("Ended session with " + fs.url);*/

            try
            {
                synchronized(this)
                {
				  wait();
			   }
            }
            catch(InterruptedException interruptedexception)
            {
                return;
            }
      		if(buf == null)
            buf = new byte[1024];
        canrun = true;
   	}

    void abort()
    {
        canrun = false;
    }
    String upload(File file)
    {
		String s = null;
        if(!file.exists())
            return "Non-existent file " + file.getAbsolutePath();
        if((s = createDataLink()) != null)
            return s;
        if(sendCommand("STOR " + RemFile.checkName(file.getName())) >= 4)
            return "from List command";
        if(!fs.passive)
            try
            {
                data = server.accept();
                server.close();
            }
            catch(IOException ioexception)
            {
                return "from server socket accept";
            }
        try
        {
            FileInputStream fileinputstream = new FileInputStream(file);
            OutputStream outputstream = data.getOutputStream();
            for(int i = 0; canrun && (i = fileinputstream.read(buf, 0, 1024)) > -1;)
            {
                //System.out.println("FTPC : 133");
                FTPManager.incBar(i);
                outputstream.write(buf, 0, i);
            }

            outputstream.close();
            data.close();
            data = null;
            fileinputstream.close();
            if(!canrun)
            {
                sendCommand("ABOR");
                FTPManager.didAbort();
                return "Upload cancelled : " + file.getAbsolutePath();
            }
            readReply();
        }
        catch(IOException ioexception1)
        {
            return "I/O error uploading " + file.getName();
        }
        LogManager.writeTimeMsg("File uploaded : " + file.getAbsolutePath());
		 RemFileFinder.addOrReplace(new RemFile(4,file.getName(),file.length(),fs));
        return null;
    }
	String rename(File from,File to)
	{
			String s = null;
			 if((s = createDataLink()) != null)
            return s;
		     int x=sendCommand("RNFR"+from);
			 System.out.println(x);
		     	if(x>=4)
			return "error";
		  if(sendCommand("RNTO"+to)>=4)
				  return "Error";
				    return null;
	}
	String mkdir(File file)
	{
		String s = null;
		  if((s = createDataLink()) != null)
            return s;
		  if(sendCommand("MKD " + file) >= 4)
            return "Error from mkd command";
		  return null;
						}
	String rmdir(RemFile file)
	{
		String s = null;
		  if((s = createDataLink()) != null)
            return s;
		  if(sendCommand("RMD " + file.getName()) >= 4)
            return "Error from mkd command";
		  return null;
						}
    String delete(RemFile remfile)
	{
	         String s = null;
		  if((s = createDataLink()) != null)
            return s;
		  if(sendCommand("DELE " + remfile.getName()) >= 4)
            return "Error from Dele command";
		      	 	return null;
	}

    String download(RemFile remfile)
    {
//System.out.println("In download");
        String s = null;
        if((s = createDataLink()) != null)
            return s;
        if(sendCommand("RETR " + remfile.getName()) >= 4)
            return "Error from List command";
        if(!fs.passive)
            try
            {
                data = server.accept();
                server.close();
            }
            catch(IOException ioexception)
            {
                return "Error from server socket accept";
            }
        Object obj = null;
        try
        {
/*            File file = File.createTempFile("FTP#", "tmp", SysProps.TEMP);
            file.deleteOnExit();
*/            
//System.out.println("$$$$$$ "+remfile.getName());
File file = new File(System.getProperty("LocalDir")+remfile.getName());
//File file = new File("c:/test/"+remfile.getName());
//System.out.println("$$$$$$ "+remfile.getName());
FileOutputStream fileoutputstream = new FileOutputStream(file);
            InputStream inputstream = data.getInputStream();
            for(int i = 0; canrun && (i = inputstream.read(buf, 0, 1024)) > -1;)
            {
                FTPManager.incBar(i);
                fileoutputstream.write(buf, 0, i);
            }

            inputstream.close();
            data.close();
            data = null;
            fileoutputstream.close();
            if(!canrun)
            {
                sendCommand("ABOR");
                FTPManager.didAbort();
                return "Download cancelled : " + file.getAbsolutePath();
            }
            readReply();
            File file1 = new File(FileFinder.currdir, remfile.getName());
            file.renameTo(file1);
        }
        catch(IOException ioexception1)
        {
            return "I/O error downloading " + remfile.getName();
        }
        LogManager.writeTimeMsg("File downloaded : " + remfile.getName());
		//FileFinder.handle.vals.add(remfile);
        return null;
    }

    String connect()
    {
			 if(!canrun)
            return FTPManager.CANCEL;
        try
        {
            control = new Socket(fs.url, 21);
        }
        catch(UnknownHostException unknownhostexception)
        {
            return "The host name is unknown";
        }
        catch(IOException ioexception2)
        {
            return "Unable to open a socket to the host";
        }
        try
        {
            control.setSoTimeout(Prefs.getInt("sotimeout") * 1000);
        }
        catch(SocketException socketexception)
        {
            return "Unable to set timeout";
        }
        if(!canrun)
            return FTPManager.CANCEL;
        try
        {
            ps = new PrintStream(new BufferedOutputStream(control.getOutputStream()), true);
            br = new BufferedReader(new InputStreamReader(control.getInputStream()));
        }
        catch(IOException ioexception)
        {
            return "Unable to open socket streams";
        }
        if(readReply() >= 4)
            return "connection failed";
        if(!canrun)
            return FTPManager.CANCEL;
        sb.setLength(0);
        sb.append("USER ").append(fs.username);
        FTPManager.setMsgBox("Verifying username");
        try
        {
            if(sendCommand(sb.toString()) >= 4)
            {
                control.close();
                return "Invalid username";
            }
					         if(!canrun)
                return FTPManager.CANCEL;
            sb.setLength(0);
            sb.append("PASS ").append(fs.pprompt ? fs.tpass : fs.pass);
            FTPManager.setMsgBox("Verifying password");
            if(sendCommand(sb.toString()) >= 4)
            {
                control.close();
                return "Invalid password";
            }
        }
        catch(IOException ioexception1)
        {
            return "Exception when closing connection";
        }
        if(!canrun)
            return FTPManager.CANCEL;
        FTPManager.setMsgBox("Switching to image mode");
				        if(sendCommand("TYPE I") >= 4)
            return "Unable to switch to image mode";
						        if(!canrun)
            return FTPManager.CANCEL;
        else
			return null;
		    }

    String createDataLink()
    {
        if(fs.passive)
        {
            ps.print("PASV\r\n");
            String s;
            try
            {
                do
                    s = br.readLine();
                while(!Character.isDigit(s.charAt(0)) || !Character.isDigit(s.charAt(1)) || !Character.isDigit(s.charAt(2)) || s.charAt(3) != ' ');
            }
            catch(IOException ioexception)
            {
                return "Error reading PASV response";
            }
            if(Integer.parseInt(s.substring(0, 1)) != 2)
                return "PASV command failed";
            int i = PASVReader.getPortNumber(s);
            if(i < 0)
                return "An invalid port number was returned";
            try
            {
                data = new Socket(fs.url, i);
            }
            catch(IOException ioexception1)
            {
                return "Unable to create passive socket";
            }
        } else
        {
            InetAddress inetaddress = null;
            try
            {
                inetaddress = InetAddress.getLocalHost();
            }
            catch(UnknownHostException unknownhostexception)
            {
                return "Invalid host when creating inet address";
            }
            byte abyte0[] = inetaddress.getAddress();
            try
            {
                server = new ServerSocket(0);
            }
            catch(IOException ioexception2)
            {
                return "Creating data link server socket";
            }
            sb.setLength(0);
            sb.append("PORT ");
            for(int j = 0; j < abyte0.length; j++)
                sb.append((abyte0[j] & 0xff) + ",");

            sb.append(server.getLocalPort() >>> 8 & 0xff).append(',').append(server.getLocalPort() & 0xff);
          //  System.out.println("Port ----- : "+sb);
            if(sendCommand(sb.toString()) >= 4)
                return "Sending port command";
        }
        return null;
    }

    String listFiles(ArrayList arraylist)
    {
        arraylist.clear();
        String s = null;
        if((s = createDataLink()) != null)
            return s;
        FTPManager.setMsgBox("Waiting for server reply ...");
        //System.out.println("In before List ************************");
        if(sendCommand("LIST") >= 4){//System.out.println("In List ************************");
            return "Error from List command";}
        if(!canrun)
        {
		           if(FTPGo.test)
                System.out.println("abort sent");
            sendCommand("ABOR");
            if(data != null)
            {
                try
                {
                    data.close();
                }
                catch(Exception exception) { }
                data = null;
            }
            readReply();
            return FTPManager.CANCEL;
        }
        if(!fs.passive)
            try
            {
                data = server.accept();
                server.close();
            }
            catch(IOException ioexception)
            {
                return "Error from server socket accept";
            }
//System.out.println("In  List 392  ************************");
        Object obj = null;
        try
        {
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(data.getInputStream()));
            int i = 0;
            int j = 0;
            String s1;
            while(canrun && (s1 = bufferedreader.readLine()) != null) 
            {
//System.out.println("In  List 402  ************************ "+s1);
                if(replyEnd(s1))
                    break;
                RemFile remfile = DirReader.processLine(s1);
                if(remfile != null){
//System.out.println("In  List 413  ************************ in if : "+s1);
					arraylist.add(remfile);}
                i++;
                if(++j == 5)
                {
                    FTPManager.setMsgBox(i + " directory entries read");
                    j = 0;
                }
            }
            FTPManager.setMsgBox(i + " directory entries read");
            bufferedreader.close();
            data.close();
            data = null;
            if(!canrun)
            {
                sendCommand("ABOR");
                return FTPManager.CANCEL;
            }
            readReply();
        }
        catch(IOException ioexception1)
        {ioexception1.printStackTrace();
            return "Error reading output from list command";
        }
        return null;
    }

    int sendCommand(String s)
    {
        if(FTPGo.test)
            System.out.println("Sent command : " + s);
        ps.print(s + "\r\n");
        return readReply();
    }

    String disconnect()
    {
        sendCommand("QUIT");
        try
        {
            control.close();
        }
        catch(IOException ioexception) { }
        control = null;
        return null;
    }

    int readReply()
    {
        String s="";
        try
        {
                s = br.readLine();
               //System.out.println("S : "+s);
            while(!Character.isDigit(s.charAt(0)) || !Character.isDigit(s.charAt(1)) || !Character.isDigit(s.charAt(2)) || s.charAt(3) != ' ') 
            {            //System.out.println("while s : "+s);
                s = br.readLine();}
             }
        catch(IOException ioexception)
        {
            return 5;
        }
        if(FTPGo.test)
            System.out.println("Server reply: " + s);
		    int j=Integer.parseInt(s.substring(0, 1));
            return j;
    }

    boolean replyEnd(String s)
    {
        return s.length() >= 4 && Character.isDigit(s.charAt(0)) && Character.isDigit(s.charAt(1)) && Character.isDigit(s.charAt(2)) && s.charAt(3) == ' ';
    }

    static final int NONE = -1;
    static final int CONNECT = 0;
    static final int DISCONNECT = 1;
    static final int LIST = 2;
    static final int CHANGE_DIR = 3;
    static final int GO_UP = 4;
    static final int MKDIR = 5;
    static final int RENAME = 6;
    static final int DELETE = 7;
    static final int UPLOAD = 8;
    static final int DOWNLOAD = 9;
    static final int STOP = 10;
    static final int CHANGE_LIST = 11;
    static final int ERROR = 12;
    static final int PRELIMINARY = 1;
    static final int COMPLETED = 2;
    static final int CONTINUING = 3;
    static final int TRANSIENT = 4;
    static final int ERR = 5;
    static final int FTP_PORT = 21;
    static String LIST_TXT = " Retrieving directory list .....  ";
    FTPSite fs;
    RemFile site;
    Socket control;
    ServerSocket server;
    Socket data;
    PrintStream ps;
    BufferedReader br;
    int action;
    Object arg;
    StringBuffer sb;
    byte buf[];
    static final int BUFSIZE = 1024;
    boolean canrun;
}
