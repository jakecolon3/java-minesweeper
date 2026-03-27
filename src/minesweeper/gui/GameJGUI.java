package minesweeper.gui;

import minesweeper.game.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


// TODO: flag toggle switch
// TODO: menu around main frame
public class GameJGUI extends JFrame {

    private Game g;
    private Container pane = getContentPane();

    // button callback
    private class GridListener implements ActionListener {

        private void removeButton(Component btn) {
            int x, y;
            x = btn.getAccessibleContext().getAccessibleIndexInParent() % g.getWidth();
            y = btn.getAccessibleContext().getAccessibleIndexInParent() / g.getHeight();

            pane.remove(btn);
            pane.add(new JLabel(g.getAdjacencyBoard().getCell(x, y) + "", JLabel.CENTER),
                g.getWidth() * y + x); // index for the label
        }

        private static int[] parseCoords(ActionEvent event) {
            String[] actCmd = event.getActionCommand().split(", ");
            int x = Integer.parseInt(actCmd[0]);
            int y = Integer.parseInt(actCmd[1]);

            return new int[] {x, y};
        }


        // TODO: stop sweeping flagged cells
        // TODO: right click to flag
        public void doLabelAction(ActionEvent event, int action) {

            int[] actCoords = parseCoords(event);

            int x = actCoords[0];
            int y = actCoords[1];
            int index = y * g.getHeight() + x;

            String newButtonLabel = (action == 2 ? "f" : "?");

            g.doAction(x, y, action); // HACK: should get a return value and stop if sweep fails
            newButtonLabel = (g.getActionBoard().getCell(x, y) == action ? newButtonLabel : "");

            JButton btn = (JButton) pane.getAccessibleContext().getAccessibleChild(index);
            btn.setText(newButtonLabel);

            validate();
        }

        public void doSweep(ActionEvent event) {

            int[] actCoords = parseCoords(event);

            int x = actCoords[0];
            int y = actCoords[1];

            g.doAction(x, y, 1);

            for (Component cmp : pane.getComponents()) { // recursively delete buttons over 0 cells

                if (cmp instanceof JLabel) continue;

                String[] cmpName   = cmp.getName().split(", ");
                int[]    cmpCoords = {Integer.parseInt(cmpName[0]),
                                      Integer.parseInt(cmpName[1])};

                if (g.getActionBoard().getCell(cmpCoords[0], cmpCoords[1]) == 1) {
                    removeButton(cmp);
                }
            }
            validate();
        }

        @Override
        public void actionPerformed(ActionEvent event) {

            switch (event.getModifiers() - 16) { // 16 is clicking without modifiers
                case ActionEvent.SHIFT_MASK:     // modifiers just add to the base value
                    doLabelAction(event, 2);
                    break;

                case ActionEvent.CTRL_MASK:
                    doLabelAction(event, 3);
                    break;

                default:
                    doSweep(event);
                    break;
            }

            if (g.getGameState() != 0) System.exit(0); // TODO: win/lose screen
        }
    }

    public GameJGUI(String title, int rows, int cols, int mines) {

        this.g = new Game(rows, cols, mines);
        Container cp = getContentPane();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(rows, cols));

        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {

                var btn = new JButton();

                String posStr = String.format("%d, %d", i, j);

                btn.addActionListener(new GridListener());
                btn.setName(posStr);

                // this gives us a way to match the event to the button that triggered it
                btn.setActionCommand(posStr);

                cp.add(btn);
            }
        }

        setTitle(title);
        setSize(800, 600);
        setVisible(true);
    }

    public static void main(String[] args) {

        try {
            // making it not look terrible
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch(Exception ignored) {}

        // some thread safety bullshit idk
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                var gui = new GameJGUI("grid test", 9, 9, 10);
            }
        });
    }
}
