package minesweeper.game;

public class Game {

  // TODO: replace action and game state numbers with static constants
  private Board mainBoard, adjacencyBoard, actionBoard;
  private int mines, gameState, flags, defused, width, height;
  final private boolean DEBUG = false;

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

    this.mines = (DEBUG ? 4 : mines);
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

  private int actionSweep(int x, int y) {
    int actCell  = this.actionBoard.getCell(x, y);
    int adjCell  = this.adjacencyBoard.getCell(x, y);
    int mainCell = this.mainBoard.getCell(x, y);

    if (actCell != 0 & actCell != 3) {
      return -1;
    }

    this.actionBoard.setCell(x, y, 1);

    if (mainCell == 1) {
      this.gameState = -1;
      System.out.println("hit mine");

      return -1;

    } else if (adjCell == 0) {

      int[][] neighbors = this.adjacencyBoard.getNeighbors(x, y);

      for (int[] coords : neighbors) {
        if (
          coords[0] < 0
        | coords[1] < 0
        | coords[0] >= this.mainBoard.getWidth()
        | coords[1] >= this.mainBoard.getHeight()
        ) continue;

        if (adjCell == 0
            && this.actionBoard.getCell(coords[0], coords[1]) == 0) {
          this.actionSweep(coords[0], coords[1]);
        }
      }
    }
    return 1;
  }

  private int actionFlag(int x, int y) {
    int actCell  = this.actionBoard.getCell(x, y);
    int mainCell = this.mainBoard  .getCell(x, y);

    if (actCell == 1) {
      System.out.println("can't put a flag on a swept cell"); // TODO: fix printout with error messages
      return -1;

    } else if (actCell == 2) {
      this.actionBoard.setCell(x, y, 0);
      if (mainCell == 1) this.defused--;
      this.flags++;
      return 1;

    } else if (mainCell == 1) {
      this.actionBoard.setCell(x, y, 2);
      this.defused++;
      this.flags--;

      if (this.defused == this.mines) {
        this.gameState = 1;
      }
      return 1;

    } else if (this.flags <= 0) {
      System.out.println("out of flags! you misplaced one or more");
      return -1;

    } else {
      this.actionBoard.setCell(x, y, 2);
      this.flags--;
      return 1;
    }

  }

  private int actionUnsure(int x, int y) {
    int actCell = this.actionBoard.getCell(x, y);

    switch (actCell) {
      case 0:
        this.actionBoard.setCell(x, y, 3);
        return 1;

      case 3:
        this.actionBoard.setCell(x, y, 0);
        return 1;

      default:
        return -1;
    }
  }

  // TODO: enumeration for action values
  public int doAction(int x, int y, int action) {
    switch (action) {
      case 1:
        return this.actionSweep(x, y);

      case 2:
        return this.actionFlag(x, y);

      case 3:
        return this.actionUnsure(x, y);

      default:
        System.out.println("Invalid action");
        return -1;
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
