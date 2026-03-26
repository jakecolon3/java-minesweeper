import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FrameDemo extends JPanel
                       implements ActionListener {
  protected JButton b1;

  public FrameDemo() {
    b1 = new JButton("Test button");
    b1.setActionCommand("test");

    b1.addActionListener(this);

    add(b1);
  }

  private static void createAndShowGUI() {
    JFrame frame = new JFrame("Test");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    FrameDemo newContentPane = new FrameDemo();
    newContentPane.setOpaque(true);
    frame.setContentPane(newContentPane);

    JLabel emptyLabel = new JLabel("");
    emptyLabel.setPreferredSize(new Dimension(800, 600));

    frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);


    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
    if ("test".equals(e.getActionCommand())) {
      System.out.println("test");
    }
  }

  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
    public void run() {
      createAndShowGUI();
      }
    });
  }
}
