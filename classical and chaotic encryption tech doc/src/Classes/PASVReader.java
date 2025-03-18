
class PASVReader
{

    PASVReader()
    {
    }

    static int getPortNumber(String s)
    {
        int i = s.lastIndexOf(')');
        if(i < 0)
            return -1;
        int j = i;
        while(s.charAt(i--) != ',') ;
        int k = Integer.parseInt(s.substring(i + 2, j));
        j = i + 1;
        while(s.charAt(i--) != ',') ;
        int l = Integer.parseInt(s.substring(i + 2, j));
        return 256 * l + k;
    }

    static final int FAIL = -1;
}
