package minesweeper.game;

public class Board {
  private int boardHeight, boardWidth, boardMines;
  private int[][] boardMatrix;
  final private boolean DEBUG = false;

  public static int[][] generateBoard(int height, int width) {
    int[][] board = new int[height][width];
    for (int i = 0; i < height; ++i) {
      for (int j = 0; j < width; ++j) {
        board[i][j] = 0;
      }
    }
    return board;
  }

  public Board(int height, int width, int mines) {
    this.boardHeight = height;
    this.boardWidth = width;
    this.boardMines = mines;
    this.boardMatrix = Board.generateBoard(height, width);
  }

  public void printBoard() {
    for (int i = 0; i < this.boardHeight; ++i) {
      for (int j = 0; j < this.boardWidth; ++j) {
        System.out.print(this.boardMatrix[i][j] + " ");
      }
      System.out.print("\n");
    }

  }

  public int getHeight() {
    return this.boardHeight;
  }

  public int getWidth() {
    return this.boardWidth;
  }

  public int getMines() {
    return this.boardMines;
  }

  public int getCell(int x, int y) {
    return this.boardMatrix[y][x];
  }

  public int[][] getMatrix() {
    return this.boardMatrix;
  }

  public void setCell(int x, int y, int v) {
    this.boardMatrix[y][x] = v;
  }

  public void populateBoard() {
    if (DEBUG) {
      this.setCell(0, 0, 1);
      this.setCell(this.boardWidth-1, 0, 1);
      this.setCell(0, this.boardHeight-1, 1);
      this.setCell(this.boardWidth-1, this.boardHeight-1, 1);
      return;
    }

    for (int i = 0; i < this.boardMines; ++i) {
      double randomY = Math.random() * this.boardHeight;
      double randomX = Math.random() * this.boardWidth;
      int y = (int) randomY;
      int x = (int) randomX;

      if (this.getCell(x, y) == 1) {
        --i;
        continue;

      } else {
        this.setCell(x, y, 1);
      }
    }
  }

  public static int[][] getNeighbors(int x, int y) {
    int[][] neighbors = {
        { x - 1, y + 1 }, { x, y + 1 }, { x + 1, y + 1 },
        { x - 1, y     },               { x + 1, y     },
        { x - 1, y - 1 }, { x, y - 1 }, { x + 1, y - 1 }
    };

    return neighbors;
  }

  private int countNeighbors(int x, int y) {
    int count = 0;

    for (int[] coordinate : getNeighbors(x, y)) {
      int i = coordinate[0];
      int j = coordinate[1];

      if (i < 0 || j < 0 || (i >= this.boardWidth) || (j >= this.boardHeight)) {
        continue;
      } else if (this.getCell(i, j) == 1) {
        count++;
      }
    }

    return count;
  }

  public void populateAdjacencyBoard(Board mainBoard) {
    int count;
    for (int j = 0; j < mainBoard.getHeight(); j++) {

      for (int i = 0; i < mainBoard.getWidth(); i++) {

        if (mainBoard.getMatrix()[j][i] == 1) {
          count = 9;
        } else {
          count = mainBoard.countNeighbors(i, j);
        }

        this.setCell(i, j, count);
      }
    }
  }
}
