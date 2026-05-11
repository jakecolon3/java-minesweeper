package minesweeper;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import minesweeper.game.*;
import minesweeper.gui.*;

public class Main {

  private static int[] parseInts(String[] arguments) {
    int[] parsedArgs = new int[arguments.length];
    for (int i = 0; i < arguments.length; ++i) {
      parsedArgs[i] = Integer.parseInt(arguments[i].trim());
    }
    return parsedArgs;
  }

  // sets the terminal cursor to the top left of the board
  // this makes it draw over the old board instead of printing a new board every time
  public static void resetTerminalCursor(int lines) {
    System.out.printf("\033[%dF", lines + 1);
  }

  public static Game createGameObject(int[] params) { // TODO: handle invalid arguments
    Game game;
    if (params.length < 3) {
      game = new Game(params[0]);
    } else {
      game = new Game(params[0],
                      params[1],
                      params[2]);
    }
    return game;
  }

  // TODO: flag for choosing between gui/tui
  public static void main(String[] args) {
    int[] parsedArgs = parseInts(args);
    Game game = createGameObject(parsedArgs);

    if (parsedArgs[parsedArgs.length - 1] == 1) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch(Exception ignored) {}

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameJGUI("Minesweeper", game);
            }
        });
    } else {
      System.out.println("input action in the format <x, y, (1-3)>:");
      System.out.println("Board:");
      game.printBoard();

      // TODO: quit keybind
      // TODO: better tui
      while (game.getGameState() == 0) {

        String input = System.console().readLine();
        String[] inputAction = input.split(",");
        int[] parsedAction = parseInts(inputAction);
        int x = parsedAction[0], y = parsedAction[1], a = parsedAction[2];

        game.doAction(x, y, a);
        resetTerminalCursor(game.getMainBoard().getHeight() + 1); // +1 cause of printing indices
        game.printBoard();

        resetTerminalCursor(0);
        System.out.println("\n                                        "); // clears the text that remains after input
        resetTerminalCursor(0);
      }
    }
  }
}
