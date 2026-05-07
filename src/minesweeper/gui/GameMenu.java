package minesweeper.gui;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class GameMenu extends JMenu {

    public GameMenu(String label, GameMenuItem[] items) {
        super(label);
        // setPreferredSize(new Dimension(100, 30));
        System.out.println(getFont());
        setFont(new Font("Adwaita Sans", 0, 25));
        for (GameMenuItem a : items) {
            add(a);
        }
    }
}
