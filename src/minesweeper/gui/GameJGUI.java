package minesweeper.gui;

import minesweeper.game.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


// TODO: gui field for Game
// TODO: flag toggle switch
// TODO: menu around main frame
public class GameJGUI extends JFrame {

    private Game g;
    private Container pane = getContentPane();

    private class MinesweeperButton extends JButton {

        private int x;
        private int y;
        private int index;

        public MinesweeperButton(int x, int y, int index) {
            super();
            this.x = x;
            this.y = y;
            this.index = index;
        }

        public int getCoordX() {
            return this.x;
        }

        public int getCoordY() {
            return this.y;
        }

        public int getIndex() {
            return this.index;
        }

        // TODO: replace this with method that changes the button's appearance and behaviour
        public void sweepButton() {
            pane.remove(this);
            pane.add(new JLabel(g.getAdjacencyBoard().getCell(this.x, this.y) + "", JLabel.CENTER), index);
        }
    }

    // button callback
    private class ButtonListener implements MouseListener {

            @Override
            public void mouseClicked(MouseEvent e) {
                MinesweeperButton source = (MinesweeperButton) e.getSource();
                int result;
                String newButtonLabel;
                MinesweeperButton btn;

                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        result = g.doAction(source.getCoordX(), source.getCoordY(), 1);
                        if (result < 0) break;

                        for (Component cmp : pane.getComponents()) { // recursively delete buttons over 0 cells

                            if (cmp instanceof JLabel) continue;

                            btn = (MinesweeperButton) cmp;

                            if (g.getActionBoard().getCell(btn.x, btn.y) == 1) {
                                btn.sweepButton();
                            }
                        }
                        validate();
                        break;

                    case MouseEvent.BUTTON2:
                        result = g.doAction(source.getCoordX(), source.getCoordY(), 3);
                        if (result < 0) break;

                        newButtonLabel = "?";
                        newButtonLabel = (g.getActionBoard().getCell(source.getCoordX(), source.getCoordY()) == 3 ? newButtonLabel : "");

                        btn = (MinesweeperButton) pane.getAccessibleContext().getAccessibleChild(source.getIndex());
                        btn.setText(newButtonLabel);

                        validate();
                        break;

                    case MouseEvent.BUTTON3:
                        result = g.doAction(source.getCoordX(), source.getCoordY(), 2);
                        if (result < 0) break;

                        newButtonLabel = "f";
                        newButtonLabel = (g.getActionBoard().getCell(source.getCoordX(), source.getCoordY()) == 2 ? newButtonLabel : "");

                        btn = (MinesweeperButton) pane.getAccessibleContext().getAccessibleChild(source.getIndex());
                        btn.setText(newButtonLabel);

                        validate();
                        break;
                }

                if (g.getGameState() != 0) System.exit(0); // TODO: win/lose screen
            }

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}
    }

    // TODO: constructor with difficulty instead of specific parameters
    public GameJGUI(String title, int width, int height, int mines) {

        this.g = new Game(height, width, mines); // honestly I'm not sure why I have to swap widht and height
        Container cp = getContentPane();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(height, width));

        int index = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                MinesweeperButton btn = new MinesweeperButton(i, j, index++);

                btn.addMouseListener(new ButtonListener());

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
