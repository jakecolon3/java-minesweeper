build = build/minesweeper
src = src/minesweeper
cp = CLASSPATH=./build
javac = $(cp) javac -d ./build
srcs = $(shell find ./$(src) -name '*.java')

.PHONY: all run clean winexample gridtest oldgridtest

# MAKEFLAGS := --jobs
# TODO: pattern rules

run :
	$(cp) java minesweeper.Main 2 1

gridtest : $(build)/gui/GameJGUI.class
	$(cp) java minesweeper.gui.GameJGUI 10 10

$(build)/Main.class : $(src)/Main.java $(build)/game/Game.class $(build)/gui/GameJGUI.class
	$(javac) $(src)/Main.java

all :
	$(javac) $(srcs)

clean :
	rm -rf ./build

$(build)/game/Game.class : $(src)/game/Game.java $(build)/game/Board.class
	$(javac) $(src)/game/Game.java

$(build)/game/Board.class : $(src)/game/Board.java
	$(javac) $(src)/game/Board.java

$(build)/gui/GameGUI.class : $(src)/gui/GameGUI.java
	$(javac) $(src)/gui/GameGUI.java

$(build)/gui/GameJGUI.class : $(src)/gui/GameJGUI.java
	$(javac) $(src)/gui/GameJGUI.java

$(build)/gui/GameMenu.class : $(src)/gui/GameMenu.java
	$(javac) $(src)/gui/GameMenu.java

$(build)/gui/GameMenuItem.class : $(src)/gui/GameMenuItem.java
	$(javac) $(src)/gui/GameMenuItem.java

build/AWTExample.class : src/test/java/AWTExample.java
	$(javac) src/test/java/AWTExample.java
