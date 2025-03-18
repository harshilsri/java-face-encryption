import java.awt.*;

class FailBox extends Window
{

    FailBox(Frame frame)
    {
        super(frame);
        Font font = new Font("dialog.bold", 1, 12);
        setBackground(Color.magenta);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLayout(new FlowLayout());
        Label label = new Label("Incorrect password", 1);
        label.setFont(font);
        add(label);
        pack();
        Dimension dimension1 = getSize();
        setLocation((dimension.width - dimension1.width) / 2, (dimension.height - dimension1.height) / 2);
        setVisible(true);
        try
        {
            Thread.sleep(3000L);
        }
        catch(Exception exception) { }
        System.exit(0);
    }

    static void create()
    {
        Frame frame = new Frame();
        new FailBox(frame);
    }
}
