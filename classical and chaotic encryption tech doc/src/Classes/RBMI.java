import javax.swing.JRadioButtonMenuItem;

class RBMI extends JRadioButtonMenuItem
{

    RBMI(int i, String s)
    {
        super(s);
        pos = i;
    }

    int pos;
}
