package minesweeper.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import minesweeper.game.*;

// TODO: scale gui based on screen resolution
// TODO: scroll bars
// TODO: gui field for Game
    // probably should do a big refactor to tie the logic and gui together better
// TODO: flag toggle switch
// TODO: top bar
public class GameJGUI extends JFrame {

    private Game g;
    private GameMainPane mainPane;
    private JScrollPane scrollPane;
    private String windowTitle;
    private int boardWidth;
    private int boardHeight;
    private int mines;

    public GameJGUI(String title, Game game) {
        this.g           = game;
        this.windowTitle = title;
        this.boardWidth  = game.getWidth();
        this.boardHeight = game.getHeight();
        this.mines       = game.getMines();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(windowTitle);

        this.mainPane = new GameMainPane(g);

        GameMenuItem exit = new GameMenuItem("Exit", e -> System.exit(0));
        GameMenuItem restart = new GameMenuItem("Restart", new RestartFunction());

        JMenuBar menuBar  = new JMenuBar();
        menuBar.add(new GameMenu("Game", new GameMenuItem[] {
            exit,
            restart,
        }));
        setJMenuBar(menuBar);
        scrollPane = new JScrollPane(mainPane);
        scrollPane.setPreferredSize(new Dimension(1920, 1080));
        getContentPane().add(scrollPane);

        setTitle(title);
        setSize(1500, 800);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

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

    protected void winLosePopup() {
        new GamePopup(this, g.getGameState());
    }

    public GameJGUI(String title, int width, int height, int mines) {
        this(title, new Game(width, height, mines));
    }

    private class RestartFunction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            mainPane = new GameMainPane(g);
            g = new Game(boardWidth, boardHeight, mines);
            scrollPane.setViewportView(mainPane);
            validate();
        }
    }

    public static void main(String[] args) {

        try {
            // making it not look terrible
            // TODO: custom look and feel
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
