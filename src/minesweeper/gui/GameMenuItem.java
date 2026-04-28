package minesweeper.gui;

import java.awt.event.*;
import javax.swing.*;

public class GameMenuItem extends JMenuItem {

    public GameMenuItem(String s, ActionListener l) {
        super(s);
        addActionListener(l);
    }
}
