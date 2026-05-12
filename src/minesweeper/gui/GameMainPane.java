package minesweeper.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import minesweeper.game.*;

public class GameMainPane extends JPanel implements Scrollable {

    public static final int SCROLLABLE_THRESHOLD = 1080;

    private Game g;
    private int boardHeight;
    private int boardWidth;
    private GridBagConstraints c;
    private boolean tracksViewport;

    public GameMainPane(Game game) {
        super();
        g = game;
        boardHeight = game.getHeight();
        boardWidth = game.getWidth();
        c = new GridBagConstraints();
        tracksViewport = Toolkit.getDefaultToolkit().getScreenSize().getHeight() > SCROLLABLE_THRESHOLD;

        setLayout(new GridBagLayout());
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;

        setPreferredSize(new Dimension(boardWidth * 50, boardHeight * 50));

        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                MinesweeperButton btn = new MinesweeperButton(j, i);
                add(btn, c);
            }
        }
    }

    private class MinesweeperButton extends JButton {

        private int x;
        private int y;
        private boolean swept;

        public MinesweeperButton(int x, int y) {
            super();
            this.x  = x; this.y  = y;
            c.gridx = x; c.gridy = y;
            this.swept = false;
            setMinimumSize(new Dimension(60, 60));
            setPreferredSize(new Dimension(60, 60));
            addMouseListener(new ButtonListener());
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

            setText("");
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

                        for (Component cmp : getComponents()) { // "recursively" delete buttons over 0 cells

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

                // HACK: sloppy
                if (g.getGameState() != 0) ((GameJGUI) SwingUtilities.getWindowAncestor(GameMainPane.this)).winLosePopup();
            }

            public void mouseExited(MouseEvent   e) { }
            public void mouseEntered(MouseEvent  e) { }
            public void mousePressed(MouseEvent  e) { }
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

    private Component getComponentByCoordinate(int x, int y) {
        return (Component) getAccessibleContext().getAccessibleChild(y * g.getWidth() + x);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(800, 500);
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
        return 10;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return tracksViewport;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return tracksViewport;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
        return 10;
    }
}
