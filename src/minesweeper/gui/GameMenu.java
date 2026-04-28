package minesweeper.gui;

import javax.swing.*;
import java.awt.event.*;

public class GameMenu extends JMenu {

    public GameMenu(String label, GameMenuItem[] items) {
        super(label);
        for (GameMenuItem a : items) {
            add(a);
        }
    }
}
