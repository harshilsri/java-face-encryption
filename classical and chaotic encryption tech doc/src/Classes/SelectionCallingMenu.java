
// Referenced classes of package com.equitysoft.ftpgo:
//            SelectionMenu, Selector

class SelectionCallingMenu extends SelectionMenu
{

    SelectionCallingMenu(String s, String s1, String s2, Selector selector1)
    {
        super(s, s1, s2);
        selector = selector1;
    }

    void makeCallBack()
    {
        selector.callBack(this);
    }

    Selector selector;
}
