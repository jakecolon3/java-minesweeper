package minesweeper.gui;

import minesweeper.game.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


// TODO: gui field for Game
// TODO: flag toggle switch
// TODO: menu around main frame
public class GameJGUI extends JFrame {

    private static final int NO_MODS = 16;
    private Game g;
    private Container pane = getContentPane();

    // button callback
    private class GridListener implements ActionListener {

        // TODO: replace this with method that changes the button's appearance and behaviour
        private void removeButton(Component btn) {
            String[] strCoords = btn.getName().split(", ");
            int x = Integer.parseInt(strCoords[0]);
            int y = Integer.parseInt(strCoords[1]);
            int index = btn.getAccessibleContext().getAccessibleIndexInParent();

            pane.remove(btn);
            pane.add(new JLabel(g.getAdjacencyBoard().getCell(x, y) + "", JLabel.CENTER), index);
        }

        private static int[] parseCoords(ActionEvent event) {
            String[] actCmd = event.getActionCommand().split(", ");
            int x = Integer.parseInt(actCmd[0]);
            int y = Integer.parseInt(actCmd[1]);

            return new int[] {x, y};
        }


        // TODO: right click to flag
        public void doLabelAction(ActionEvent event, int action) {

            int[] actCoords = parseCoords(event);

            int x = actCoords[0];
            int y = actCoords[1];
            JButton source = (JButton) event.getSource();
            int index = source.getAccessibleContext().getAccessibleIndexInParent();

            int result = g.doAction(x, y, action);
            if (result < 0) return;

            String newButtonLabel = (action == 2 ? "f" : "?");
            newButtonLabel = (g.getActionBoard().getCell(x, y) == action ? newButtonLabel : "");

            JButton btn = (JButton) pane.getAccessibleContext().getAccessibleChild(index);
            btn.setText(newButtonLabel);

            validate();
        }

        // TODO: maybe refactor this into a recursive function if it's possible
        //       you can get the surrounding button's events with ActionEvent.getSource()
        //       and just call doSweep on each of them probably
        //       can even use Board.getNeighbors()
        // TODO: implement clicking satisfied cell to reveal surroundings
        public void doSweep(ActionEvent event) {

            int[] actCoords = parseCoords(event);

            int x = actCoords[0];
            int y = actCoords[1];

            int result = g.doAction(x, y, 1);

            if (result < 0) return;

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

            switch (event.getModifiers() - NO_MODS) {
                case ActionEvent.SHIFT_MASK:
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

    // TODO: constructor with difficulty instead of specific parameters
    public GameJGUI(String title, int width, int height, int mines) {

        this.g = new Game(height, width, mines); // honestly I'm not sure why I have to swap widht and height
        Container cp = getContentPane();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(height, width));

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

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
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {

        try {
            // making it not look terrible
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch(Exception ignored) {}

        // some thread safety bullshit idk
        // only the gui should run inside this, game logic should be outside
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                var gui = new GameJGUI("grid test", 30, 16, 99);
            }
        });
    }
}
