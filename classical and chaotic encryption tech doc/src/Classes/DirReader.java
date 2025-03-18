
// Referenced classes of package com.equitysoft.ftpgo:
//            RemFile

class DirReader
{

    DirReader()
    {
    }

    static RemFile processLine(String s)
    {
        line = s;
/*        nxtlen = s.length();
        nxt = nxtlen - 1;
        char c = line.charAt(0);
        if(c == 'd')
            type = 3;
        else
        if(c == '-')
            type = 4;
        else
            return null;
        name = lastToken();
        skip();
        skip();
        skip();
        size = Long.parseLong(nextToken());
*/
java.util.StringTokenizer st=new java.util.StringTokenizer(line," ");
st.nextToken();
st.nextToken();
String s20=st.nextToken();
//System.out.println(s20);
if (s20.equals("<DIR>"))
{type = 3;
size=0;}
else {type=4;
size=Integer.parseInt(s20);}
name=st.nextToken();
        return new RemFile(type, name, size, null);
    }

    static String lastToken()
    {
        int i = nextSpace();
        return line.substring(i + 1);
    }

    static String nextToken()
    {
        int i = nextNonSpace();
        int j = nextSpace();
        return line.substring(j + 1, i + 1);
    }

    static void skip()
    {
        nextNonSpace();
        nextSpace();
    }

    static int nextNonSpace()
    {
        do
        {
            char c = line.charAt(nxt);
            if(c == ' ' || c == '\t')
                nxt--;
            else
                return nxt;
        } while(true);
    }

    static int nextSpace()
    {
        do
        {
            char c = line.charAt(nxt);
            if(c != ' ' && c != '\t')
                nxt--;
            else
                return nxt;
        } while(true);
    }

    static int nxtlen;
    static int nxt;
    static long size;
    static String line;
    static String name;
    static int type;
}
