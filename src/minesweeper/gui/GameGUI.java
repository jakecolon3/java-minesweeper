package minesweeper.gui;

import minesweeper.game.*;

import java.util.ArrayList;
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

    private class GridListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {

            String[] actCmd = event.getActionCommand().split(", ");

            int x = Integer.parseInt(actCmd[0]);
            int y = Integer.parseInt(actCmd[1]);

            // TODO: implement flagging
            g.doAction(x, y, 1);
            g.printBoard();

            System.out.println(event.getActionCommand());

            for (Button btn : btns) { // can probably use Frame.getComponents() instead
                if (btn.getName() == event.getActionCommand()) { // finds the button that triggered the event
                    // btn.setVisible(false);
                    // btn.setLabel(g.getAdjacencyBoard().getCell(x, y) + "");
                    // btn.removeActionListener(this);

                    // remove clicked button and replace it with a label
                        // we can find the index for the label from the button's coordinates since
                        // the buttons are added sequentially
                    remove(btn);
                    add(new Label(g.getAdjacencyBoard().getCell(x, y) + ""),
                        g.getWidth() * y + x);
                    validate();
                    break;
                }
            }
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
