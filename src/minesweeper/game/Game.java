package minesweeper.game;

import minesweeper.game.Board;

public class Game {

  private Board mainBoard, adjacencyBoard, actionBoard;
  private int mines, gameState, flags, defused, width, height;

  public Game(int width, int height, int mines) {
    this.gameInit(width, height, mines);
  }

  public Game(int difficulty) {
    this.gameInit(difficulty);
  }

  public void gameInit(int width, int height, int mines) {
    if (mines >= width*height) {
      System.out.println("invalid difficulty"); // TODO: actually handle invalid args
    }

    this.width = width;
    this.height = height;

    this.mainBoard = new Board(height, width, mines);
    this.mainBoard.populateBoard();

    this.adjacencyBoard = new Board(height, width, mines);
    this.adjacencyBoard.populateAdjacencyBoard(this.mainBoard);

    this.actionBoard = new Board(height, width, mines);

    this.mines = mines;
    this.flags = mines;
    this.gameState = 0;
    this.defused = 0;
  }

  public void gameInit(int difficulty) {
    switch (difficulty) {
      case 0:
        gameInit(9, 9, 10);
        break;

      case 1:
        gameInit(16, 16, 25);
        break;

      case 2:
        gameInit(30, 16, 99);
        break;

      default:
        System.out.println("Invalid difficulty");
        break;
    }
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  public Board getMainBoard() {
    return this.mainBoard;
  }

  public Board getAdjacencyBoard() {
    return this.adjacencyBoard;
  }

  public Board getActionBoard() {
    return this.actionBoard;
  }

  public int getGameState() {
    return this.gameState;
  }

  public int getMines() {
    return this.mines;
  }

  public int getFlags() {
    return this.flags;
  }

  public int getDefused() {
    return this.defused;
  }

  private void actionSweep(int x, int y) {
    this.actionBoard.setCell(x, y, 1);

    if (this.mainBoard.getCell(x, y) == 1) {
      this.gameState = -1;

      System.out.println("hit mine");
      return;
    }

    if (this.adjacencyBoard.getCell(x, y) == 0) {

      int[][] neighbors = this.adjacencyBoard.getNeighbors(x, y);

      for (int[] cell : neighbors) {
        if (cell[0] < 0
          | cell[1] < 0
          | cell[0] >= this.mainBoard.getWidth()
          | cell[1] >= this.mainBoard.getHeight())
        {
          continue;
        }

        if (this.adjacencyBoard.getCell(x, y) == 0
            && this.actionBoard.getCell(cell[0], cell[1]) == 0) {
          this.actionSweep(cell[0], cell[1]);
        }
      }
    }
  }

  private void actionFlag(int x, int y) {
    if (this.flags <= 0) {
      System.out.println("out of flags! you misplaced one or more");
      return;
    }

    if (this.actionBoard.getCell(x, y) == 1) {
      System.out.println("can't put a flag on a swept cell"); // TODO: fix printout with error messages
    }

    else if (this.actionBoard.getCell(x, y) == 2) {
      this.actionBoard.setCell(x, y, 0);
      this.flags++;

    } else if (this.mainBoard.getCell(x, y) == 1) {
      this.actionBoard.setCell(x, y, 2);
      this.defused++;
      this.flags--;

      if (this.defused == this.mines) {
        this.gameState = 1;
      }

    } else {
      this.actionBoard.setCell(x, y, 2);
      this.flags--;
    }
  }

  private void actionUnsure(int x, int y) {
    // TODO: properly implement '?' action
    this.actionBoard.setCell(x, y, 3);
  }

  public void doAction(int x, int y, int action) {
    switch (action) {
      case 1:
        this.actionSweep(x, y);
        break;

      case 2:
        this.actionFlag(x, y);
        break;

      case 3:
        this.actionUnsure(x, y);
        break;

      default:
        System.out.println("Invalid action");
        break;
    }
  }

  public void printBoard() {
    for (int j = -1, i = -1; j < this.getMainBoard().getHeight(); ++j) {
      for (i = -1; i < this.getMainBoard().getWidth(); ++i) {

        // indices
        if (j == -1) {
          System.out.print((i == -1 ? "  " : String.format("%-2d", i)));
          continue;
        } else if (i == -1) {
          System.out.print(String.format("%-2d", j));
          continue;
        }

        switch (this.getActionBoard().getCell(i, j)) {
          case 1:
            int cell = this.getAdjacencyBoard().getCell(i, j);
            System.out.print((cell == 0 ? " " : cell) + " ");
            break;

          case 2:
            System.out.print("F ");
            break;

          case 3:
            System.out.print("? ");
            break;

          default:
            System.out.print("# ");

        }
      }
      System.out.print("\n");
    }
  }
}
