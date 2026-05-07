package minesweeper.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameMenuItem extends JMenuItem {

    public GameMenuItem(String s, ActionListener l) {
        super(s);
        setFont(new Font("Adwaita Sans", 0, 25));
        addActionListener(l);
    }
}
