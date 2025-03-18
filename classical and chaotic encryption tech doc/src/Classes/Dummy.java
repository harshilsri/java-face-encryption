import java.awt.Component;
import java.awt.Dimension;

class Dummy extends Component
{

    Dummy(int i)
    {
        this(new Dimension(i, i), false);
    }

    Dummy()
    {
        this(null, true);
    }

    Dummy(Dimension dimension, boolean flag)
    {
        dim = dimension;
        traversable = flag;
    }

    public Dimension getPreferredSize()
    {
        if(dim == null)
            return super.getPreferredSize();
        else
            return dim;
    }

    public boolean isFocusTraversable()
    {
        return traversable;
    }

    Dimension dim;
    boolean traversable;
}
