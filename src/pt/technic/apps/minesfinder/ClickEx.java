package pt.technic.apps.minesfinder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClickEx extends MouseAdapter
{
    private JLabel status;

    private boolean isLeftPressed;
    private boolean isRightPressed;

    public ClickEx ()
    {
        JFrame frame = new JFrame ();
        frame.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel (new FlowLayout (FlowLayout.CENTER));

        status = new JLabel ("waiting for both mouse buttons...");

        status.addMouseListener (this);

        panel.add (status);

        frame.add (panel);

        frame.pack ();
        frame.setVisible (true);

        isLeftPressed = false;
        isRightPressed = false;
    }

    @Override
    public void mousePressed (MouseEvent e)
    {
        if (SwingUtilities.isLeftMouseButton (e))
        {
            isLeftPressed = true;
        }
        else if (SwingUtilities.isRightMouseButton (e))
        {
            isRightPressed = true;
        }

        if (isLeftPressed && isRightPressed)
        {
            status.setText ("both buttons are pressed");
        }
    }

    @Override
    public void mouseReleased (MouseEvent e)
    {
        if (SwingUtilities.isLeftMouseButton (e))
        {
            isLeftPressed = false;
        }
        else if (SwingUtilities.isRightMouseButton (e))
        {
            isRightPressed = false;
        }

        status.setText ("waiting for both mouse buttons...");
    }

    public static void main (String[] args)
    {
        SwingUtilities.invokeLater (new Runnable ()
        {
            @Override
            public void run ()
            {
                new ClickEx ();
            }
        });
    }
}