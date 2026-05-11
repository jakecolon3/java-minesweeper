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
    private String windowTitle;
    private int boardWidth;
    private int boardHeight;
    private int mines;

    private class GamePopup extends JDialog {

        private Container pane = getContentPane();
        private JPanel buttonBox;
        private JButton exitButton;
        private JButton restartButton;
        private static boolean exists;

        public GamePopup(Frame owner, int outcome) {
            super(owner);
            if (exists) return;
            GamePopup.exists = true;
            add(new Label(String.format("You %s!", outcome > 0 ? "won" : "lost"), Label.CENTER));
            setSize(new Dimension(150, 100));
            setLocationRelativeTo(null);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

            buttonBox = new JPanel();
            buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.X_AXIS));

            exitButton = new JButton("exit");
            restartButton = new JButton("restart");
            exitButton.addActionListener(e -> System.exit(0));
            restartButton.addActionListener(e -> {
                new RestartFunction().actionPerformed(e);
                dispose();
                GamePopup.exists = false;
            });

            buttonBox.add(exitButton);
            buttonBox.add(restartButton);
            add(buttonBox);

            setVisible(true);
        }
    }

    private class MinesweeperButton extends JButton {

        private int x;
        private int y;
        private boolean swept = false;

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

        public void sweepButton() {
            swept = true;
            setFocusable(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setModel(new DefaultButtonModel() {
                public boolean isPressed()  { return false; }
                public boolean isRollover() { return false; }
                public void setRollover(boolean b) {}
            });

            int cellText = g.getAdjacencyBoard().getCell(this.x, this.y);
            JLabel bLabel = new JLabel(cellText == 0 ? "" : cellText + "", JLabel.CENTER);
            Color c;

            switch (cellText) {
                default -> c = Color.BLACK;
                case 1  -> c = Color.BLUE;
                case 2  -> c = Color.GREEN;
                case 3  -> c = Color.RED;
                case 4  -> c = Color.BLUE.darker();
                case 5  -> c = Color.RED.darker();
                case 6  -> c = Color.CYAN;
                case 7  -> c = Color.MAGENTA.darker();
                case 8  -> c = Color.LIGHT_GRAY;
            }
            bLabel.setForeground(c);
            add(bLabel);
            for (MouseListener a : getMouseListeners()) {
                removeMouseListener(a);
            }

            addMouseListener(new SweptButtonListener());
        }

        // TODO: only run this if the cell is satisfied
        public void click() {
            if (swept) return;
            MouseEvent e = new MouseEvent(this, MouseEvent.MOUSE_CLICKED, 0, 0, getX(), getY(), 1, false, 1);
            processMouseEvent(e);
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

                        for (Component cmp : mainPane.getComponents()) { // "recursively" delete buttons over 0 cells

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

                        newButtonLabel = "P";
                        newButtonLabel = (g.getActionBoard().getCell(source.getCoordX(), source.getCoordY()) == 2 ? newButtonLabel : "");

                        btn = (MinesweeperButton) source;
                        btn.setText(newButtonLabel);

                        validate();
                        break;
                }

                if (g.getGameState() != 0) winLosePopup();
            }

            public void mouseEntered(MouseEvent e)  { }
            public void mouseExited(MouseEvent e)   { }
            public void mousePressed(MouseEvent e)  { }
            public void mouseReleased(MouseEvent e) { }
    }

    private class SweptButtonListener extends ButtonListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() != MouseEvent.BUTTON1) return;
            int sourceX = ((MinesweeperButton)e.getSource()).x;
            int sourceY = ((MinesweeperButton)e.getSource()).y;

            if (Board.countNeighbors(g.getActionBoard(), 2, sourceX, sourceY)
             >= g.getAdjacencyBoard().getCell(sourceX, sourceY)) {
                for (int[] coord : Board.getNeighbors(sourceX, sourceY)) {
                    int btnX = coord[0];
                    int btnY = coord[1];
                    if (btnX < 0 || btnY < 0 || btnX >= g.getWidth() || btnY >= g.getHeight()) continue;
                    Component cmp = getComponentByCoordinate(btnX, btnY);
                    if (!(cmp instanceof MinesweeperButton)) continue;
                    MinesweeperButton btn = (MinesweeperButton) cmp;
                    btn.click();
                }
            } else {
            }
        }
    }

    private void winLosePopup() {
        new GamePopup(this, g.getGameState());
    }

    private JPanel initMainPane(String title, int width, int height, int mines) {
        var mainPane = new JPanel();

        mainPane.setLayout(new GridLayout(height, width));
        mainPane.setPreferredSize(new Dimension(width * 50, height * 50));

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                MinesweeperButton btn = new MinesweeperButton(j, i);

                btn.setPreferredSize(new Dimension(40, 40));
                btn.addMouseListener(new ButtonListener());

                mainPane.add(btn);
            }
        }
        return mainPane;
    }

    public GameJGUI(String title, Game game) {
        this.g           = game;
        this.windowTitle = title;
        this.boardWidth  = game.getWidth();
        this.boardHeight = game.getHeight();
        this.mines       = game.getMines();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.mainPane = initMainPane(windowTitle, boardWidth, boardHeight, mines);

        GameMenuItem exit = new GameMenuItem("Exit", e -> System.exit(0));
        GameMenuItem restart = new GameMenuItem("Restart", new RestartFunction());

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
        pack();
    }

    public GameJGUI(String title, int width, int height, int mines) {
        this(title, new Game(width, height, mines));
    }

    public Component getComponentByCoordinate(int x, int y) {
        return (Component) mainPane.getAccessibleContext().getAccessibleChild(y * g.getWidth() + x);
    }

    private class RestartFunction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            remove(mainPane);
            mainPane = initMainPane(windowTitle, boardWidth, boardHeight, mines);
            g = new Game(boardWidth, boardHeight, mines);
            getContentPane().add(mainPane);
            validate();
        }
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
