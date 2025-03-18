
class FTPSite
{

    FTPSite(String s, String s1, String s2, boolean flag, String s3, boolean flag1)
    {
        name = s;
        url = s1;
        username = s2;
        pprompt = flag;
        pass = s3;
        passive = flag1;
    }

    String name;
    String url;
    String username;
    boolean pprompt;
    String pass;
    String tpass;
    boolean passive;
}
