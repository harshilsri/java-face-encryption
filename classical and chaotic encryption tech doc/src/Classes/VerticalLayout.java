
import java.awt.*;
import java.util.Hashtable;
import javax.swing.*;

public class VerticalLayout
    implements LayoutManager
{

    public VerticalLayout()
    {
        this(5, 0, 1);
    }

    public VerticalLayout(int i)
    {
        this(i, 0, 1);
    }

    public VerticalLayout(int i, int j)
    {
        this(i, j, 1);
    }

    public VerticalLayout(int i, int j, int k)
    {
        vgap = i;
        alignment = j;
        anchor = k;
    }

    private Dimension layoutSize(Container container, boolean flag)
    {
        Dimension dimension = new Dimension(0, 0);
        synchronized(container.getTreeLock())
        {
            int i = container.getComponentCount();
            for(int j = 0; j < i; j++)
            {
                Component component = container.getComponent(j);
                if(component.isVisible())
                {
                    Dimension dimension1 = flag ? component.getMinimumSize() : component.getPreferredSize();
                    dimension.width = Math.max(dimension.width, dimension1.width);
                    dimension.height += dimension1.height;
                    if(j > 0)
                        dimension.height += vgap;
                }
            }

        }
        Insets insets = container.getInsets();
        dimension.width += insets.left + insets.right;
        dimension.height += insets.top + insets.bottom + vgap + vgap;
        return dimension;
    }

    public void layoutContainer(Container container)
    {
        Insets insets = container.getInsets();
        Dimension dimension = layoutSize(container, false);
        synchronized(container.getTreeLock())
        {
            int i = container.getComponentCount();
            Dimension dimension1 = container.getSize();
            int j = 0;
            for(int k = 0; k < i; k++)
            {
                Component component = container.getComponent(k);
                Dimension dimension2 = component.getPreferredSize();
                j += dimension2.height + vgap;
            }

            j -= vgap;
            if(anchor == 1)
                j = insets.top;
            else
            if(anchor == 0)
                j = (dimension1.height - j) / 2;
            else
                j = dimension1.height - j - insets.bottom;
            for(int l = 0; l < i; l++)
            {
                Component component1 = container.getComponent(l);
                Dimension dimension3 = component1.getPreferredSize();
                int i1 = insets.left;
                int j1 = dimension3.width;
                if(alignment == 0)
                    i1 = (dimension1.width - dimension3.width) / 2;
                else
                if(alignment == 1)
                    i1 = dimension1.width - dimension3.width - insets.right;
                else
                if(alignment == 3)
                    j1 = dimension1.width - insets.left - insets.right;
                component1.setBounds(i1, j, j1, dimension3.height);
                j += dimension3.height + vgap;
            }

        }
    }

    public Dimension minimumLayoutSize(Container container)
    {
        return layoutSize(container, false);
    }

    public Dimension preferredLayoutSize(Container container)
    {
        return layoutSize(container, false);
    }

    public void addLayoutComponent(String s, Component component)
    {
    }

    public void removeLayoutComponent(Component component)
    {
    }

    public String toString()
    {
        return getClass().getName() + "[vgap=" + vgap + " align=" + alignment + " anchor=" + anchor + "]";
    }

    public static final int CENTER = 0;
    public static final int RIGHT = 1;
    public static final int LEFT = 2;
    public static final int BOTH = 3;
    public static final int TOP = 1;
    public static final int BOTTOM = 2;
    private int vgap;
    private int alignment;
    private int anchor;
    private Hashtable comps;
}
