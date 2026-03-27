package minesweeper.gui;

import minesweeper.game.*;

import java.awt.*;
import java.awt.event.*;


public class GameGUI extends Frame {

    private Game g;

    private Button[] btns;

    // listener class for window events
    // we only care about closing the window but need to implement the rest of the methods
    private class GameWindowListener implements WindowListener {

        @Override
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }

        @Override
        public void windowOpened(WindowEvent e) { }

        @Override
        public void windowClosed(WindowEvent e) { }

        @Override
        public void windowIconified(WindowEvent e) { }

        @Override
        public void windowDeiconified(WindowEvent e) { }

        @Override
        public void windowActivated(WindowEvent e) { }

        @Override
        public void windowDeactivated(WindowEvent e) { }
    }

    // button callback
    private class GridListener implements ActionListener {

        private void removeButton(Component btn) {
            int x, y;
            x = btn.getAccessibleContext().getAccessibleIndexInParent() % g.getWidth();
            y = btn.getAccessibleContext().getAccessibleIndexInParent() / g.getHeight();

            remove(btn);
            add(new Label(g.getAdjacencyBoard().getCell(x, y) + "", Label.CENTER),
                g.getWidth() * y + x); // index for the label
        }

        private static int[] parseCoords(ActionEvent event) {
            String[] actCmd = event.getActionCommand().split(", ");
            int x = Integer.parseInt(actCmd[0]);
            int y = Integer.parseInt(actCmd[1]);

            return new int[] {x, y};
        }


        public void doLabelAction(ActionEvent event, int action) {

            int[] actCoords = parseCoords(event);

            int x = actCoords[0];
            int y = actCoords[1];
            int index = y * g.getHeight() + x;

            String newButtonLabel = (action == 2 ? "f" : "?");

            g.doAction(x, y, action);
            newButtonLabel = (g.getActionBoard().getCell(x, y) == action ? newButtonLabel : "");

            Button btn = (Button) getAccessibleContext().getAccessibleChild(index);
            btn.setLabel(newButtonLabel);

            validate();
        }

        public void doSweep(ActionEvent event) {

            int[] actCoords = parseCoords(event);

            int x = actCoords[0];
            int y = actCoords[1];

            g.doAction(x, y, 1);

            for (Component cmp : getComponents()) { // recursively delete buttons over 0 cells

                if (cmp instanceof Label) continue;

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

    public GameGUI(String title, int rows, int cols, int mines) {
        this.g = new Game(rows, cols, mines);

        setTitle(title);
        setLayout(new GridLayout(rows, cols));
        setSize(800, 600);

        this.btns = new Button[this.g.getWidth() * this.g.getHeight()];
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {

                var btn = new Button();
                this.btns[(j * g.getWidth()) + i] = btn;

                String posStr = String.format("%d, %d", i, j);

                btn.addActionListener(new GridListener());
                btn.setName(posStr);

                // this gives us a way to match the event to the button that triggered it
                btn.setActionCommand(posStr);

                add(btn);
            }
        }

        addWindowListener(new GameWindowListener());

        setVisible(true);
    }

    public static void main(String[] args) {

        // int[] argsInt = new int[args.length];
        // for (int i = 0; i < argsInt.length; i++) {
        //     argsInt[i] = Integer.parseInt(args[i]);
        // }
        //
        // new GameGUI("grid test", argsInt[0], argsInt[1]);

        var gui = new GameGUI("grid test", 9, 9, 10);
        // for (Component cmp : gui.getComponents()) {
        //     System.out.println(cmp);
        // }


    }
}
