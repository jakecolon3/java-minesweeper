package minesweeper.gui;

import minesweeper.game.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


// TODO: gui field for Game
// TODO: flag toggle switch
// TODO: menu around main frame
// TODO: investigate javax.swing.Action interface for button state
public class GameJGUI extends JFrame {

    private Game g;
    private Container mainPane;

    private class MinesweeperButton extends JButton {

        private int x;
        private int y;

        public MinesweeperButton(int x, int y) {
            super();
            this.x = x;
            this.y = y;
        }

        public int getCoordX() {
            return this.x;
        }

        public int getCoordY() {
            return this.y;
        }

        // TODO: add clicking to sweep surrounding cells
        public void sweepButton() {
            setFocusable(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setModel(new DefaultButtonModel() {
                public boolean isPressed()  { return false; }
                public boolean isRollover() { return false; }
                public void setRollover(boolean b) {}
            });

            setText(g.getAdjacencyBoard().getCell(this.x, this.y) + "");
            for (MouseListener a : getMouseListeners()) {
                System.out.println("removing: " + a);
                removeMouseListener(a);
            }
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
                System.out.println(e.getButton());
                System.out.println(((MinesweeperButton)e.getSource()).getListeners(ButtonListener.class));

                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        result = g.doAction(source.getCoordX(), source.getCoordY(), 1);
                        if (result < 0) break;

                        for (Component cmp : mainPane.getComponents()) { // recursively delete buttons over 0 cells

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

                        btn = (MinesweeperButton) source;
                        btn.setText(newButtonLabel);

                        validate();
                        break;

                    case MouseEvent.BUTTON3:
                        result = g.doAction(source.getCoordX(), source.getCoordY(), 2);
                        if (result < 0) break;

                        newButtonLabel = "f";
                        newButtonLabel = (g.getActionBoard().getCell(source.getCoordX(), source.getCoordY()) == 2 ? newButtonLabel : "");

                        btn = (MinesweeperButton) source;
                        btn.setText(newButtonLabel);

                        validate();
                        break;
                }

                if (g.getGameState() != 0) System.exit(0); // TODO: win/lose screen
            }

            public void mouseEntered(MouseEvent e)  { }
            public void mouseExited(MouseEvent e)   { }
            public void mousePressed(MouseEvent e)  { }
            public void mouseReleased(MouseEvent e) { }
    }

    private JPanel initMainPane(String title, int width, int height, int mines) {
        var mainPane = new JPanel();

        mainPane.setLayout(new GridLayout(height, width));
        mainPane.setPreferredSize(new Dimension(width * 50, height * 50));

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                MinesweeperButton btn = new MinesweeperButton(i, j);

                btn.addMouseListener(new ButtonListener());

                mainPane.add(btn);
            }
        }
        return mainPane;
    }

    // TODO: constructor with difficulty instead of specific parameters
    public GameJGUI(String title, int width, int height, int mines) {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.g = new Game(height, width, mines); // HACK: honestly I'm not sure why I have to swap width and height
        this.mainPane = initMainPane(title, width, height, mines);

        GameMenuItem exit = new GameMenuItem("Exit", e -> System.exit(0));
        GameMenuItem restart = new GameMenuItem("Restart", e -> {
            // don't know if this is the best way to do this
            remove(mainPane);
            this.mainPane = initMainPane(title, width, height, mines);
            this.g = new Game(height, width, mines);
            getContentPane().add(mainPane);
            validate();
        });

        JMenuBar menuBar  = new JMenuBar();
        menuBar.add(new GameMenu("Game", new GameMenuItem[] {
            exit,
            restart,
        }));
        setJMenuBar(menuBar);
        getContentPane().add(mainPane);

        setTitle(title);
        setSize(1500, 800);
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
