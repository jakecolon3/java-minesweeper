run : Main.class
	java Main 1

clean :
	rm *.class

winexample : test/AWTExample.class
	java -cp test/ AWTExample

gridtest : GameGUI.class
	java GameGUI 10 10

Main.class : Main.java Game.class
	javac Main.java

Game.class : Game.java Board.class
	javac Game.java

Board.class : Board.java
	javac Board.java

test/AWTExample.class : test/AWTExample.java
	javac test/AWTExample.java

GameGUI.class : GameGUI.java
	javac GameGUI.java
