package Main.TSAs.Assignment3;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Scanner;

/**
 * Main class for simulating figure battles on a game board.
 * Two teams (red and green) move across the board, collect coins, and fight each other.
 */
public class FiguresFightSimulation {
    public static void main(String[] args) {
        gameSimulation();
    }

    /**
     * Main game simulation method that handles the game flow
     * Reads input, processes actions, and manages the game state
     * Consist loop for work with n commands
     * Handles messages such as <Figure> <Action>
     */
    public static void gameSimulation() {
        Scanner input = new Scanner(System.in);
        int n = input.nextInt();

        GameFacade game = new GameFacade(n, input);

        n = input.nextInt();

        String figure;
        String action;
        Color color;

        for (int i = 0; i < n; i++) {
            try {
                Exception exception = new Exception("INVALID ACTION");
                figure = input.next();
                action = input.next();
                color = Color.getEnum(figure);

                switch (action) {
                    case "UP":
                        game.move(-1, 0, color);
                        break;
                    case "DOWN":
                        game.move(1, 0, color);
                        break;
                    case "LEFT":
                        game.move(0, -1, color);
                        break;
                    case "RIGHT":
                        game.move(0, 1, color);
                        break;
                    case "COPY":
                        game.cloneFigure(color);
                        break;
                    case "STYLE":
                        game.changeStyle(color);
                        break;
                    default:
                        throw exception;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
        game.printScore();

    }
}

/**
 * Represent the simple interface of complex structure Board (for pattern Facade)
 */
class GameFacade {
    Board board;

    /**
     * Constructor a new Game Facade
     * @param size size of board
     * @param input scanner
     */
    public GameFacade(int size, Scanner input) {
        board = createBoard(size, input);
    }

    /**
     * Creates and initializes the game board with figures and coins
     *
     * @param size  the size of the board (n x n)
     * @param input Scanner object for reading input
     * @return initialized Board object
     */
    public static Board createBoard(int size, Scanner input) {
        Board board = new Board(size);
        int x = input.nextInt();
        int y = input.nextInt();
        x--;
        y--;
        board.addGreenFigure(new GreenFigure(x, y), x, y);

        x = input.nextInt();
        y = input.nextInt();
        x--;
        y--;
        board.addRedFigure(new RedFigure(x, y), x, y);

        int n = input.nextInt();
        int value;
        for (int i = 0; i < n; i++) {
            x = input.nextInt();
            y = input.nextInt();
            x--;
            y--;
            value = input.nextInt();
            board.addCoin(x, y, value);
        }
        return board;
    }

    /**
     * Moves a figure on the board
     *
     * @param x     x-direction movement
     * @param y     y-direction movement
     * @param color color of figure to move
     * @throws Exception if move is invalid
     */
    public void move(int x, int y, Color color) throws Exception {
        board.move(x, y, color);
    }

    /**
     * Creates a clone of a figure
     *
     * @param color color of figure to clone
     * @throws Exception if cloning is not possible
     */
    public void cloneFigure(Color color) throws Exception {
        board.cloneFigure(color);
    }

    /**
     * Changes the movement style of a figure
     *
     * @param color color of figure to change
     * @throws Exception if figure doesn't exist
     */
    public void changeStyle(Color color) throws Exception{
        board.changeStyle(color);
    }

    /**
     * Prints the final game score and result
     */
    public void printScore() {
        board.printScore();
    }
}

/**
 * Represents the game board and manages game state
 */
class Board {

    private ArrayList<ArrayList<Integer>> boardTable;

    private EnumMap<Color, CloneableFigure> map;

    private Exception exception = new Exception("INVALID ACTION");

    private int greenScore = 0;
    private int redScore = 0;
    private int greenCopy = 1;
    private int redCopy = 1;
    private final int n;

    /**
     * Constructs a new game board
     *
     * @param n size of the board (n x n)
     */
    public Board(int n) {
        this.n = n;
        boardTable = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            boardTable.add(new ArrayList<>());
            for (int j = 0; j < n; j++) {
                boardTable.get(i).add(0);
            }
        }
        map = new EnumMap<>(Color.class);
        map.put(Color.REDCLONE, null);
        map.put(Color.RED, null);
        map.put(Color.GREENCLONE, null);
        map.put(Color.GREEN, null);

    }

    /**
     * Adds a coin to the board at specified position
     *
     * @param x     x-coordinate
     * @param y     y-coordinate
     * @param value coin value
     */
    public void addCoin(int x, int y, int value) {
        boardTable.get(x).set(y, value);
    }

    /**
     * Adds red figure to the board
     *
     * @param figure the figure to add
     * @param x      x-coordinate
     * @param y      y-coordinate
     */
    public void addRedFigure(CloneableFigure figure, int x, int y) {
        map.put(Color.RED, figure);
        boardTable.get(x).set(y, -2);
    }

    /**
     * Adds green figure to the board
     *
     * @param figure the figure to add
     * @param x      x-coordinate
     * @param y      y-coordinate
     */
    public void addGreenFigure(CloneableFigure figure, int x, int y) {
        map.put(Color.GREEN, figure);
        boardTable.get(x).set(y, -1);
    }

    /**
     * Creates a clone of a figure
     *
     * @param color color of figure to clone
     * @throws Exception if cloning is not possible
     */
    public void cloneFigure(Color color) throws Exception {
        if (color == null) throw exception;
        if (color == Color.RED && redCopy == 0 || color == Color.GREEN && greenCopy == 0) throw exception;
        Color newColor;
        switch (color) {
            case RED:
                newColor = Color.REDCLONE;
                break;
            case GREEN:
                newColor = Color.GREENCLONE;
                break;
            default:
                throw exception;
        }
        CloneableFigure figure = map.get(color);
        CloneableFigure figureClone = map.get(newColor);

        if (figureClone != null) throw exception;

        figureClone = figure.clone();

        int x = ((Figure) figureClone).getX();
        int y = ((Figure) figureClone).getY();

        if (getValue(x, y) != 0) {
            throw exception;
        }
        map.put(newColor, figureClone);
        if (color == Color.RED || color == Color.REDCLONE) boardTable.get(x).set(y, -2);
        if (color == Color.GREEN || color == Color.GREENCLONE) boardTable.get(x).set(y, -1);

        System.out.println(color + " CLONED TO " + (x + 1) + " " + (y + 1));
        if (color == Color.RED) redCopy--;
        else greenCopy--;
        if (color == Color.GREEN || color == Color.GREENCLONE) {
            setValue(x, y, -1);
        } else {
            setValue(x, y, -2);
        }
    }

    /**
     * Gets value at board position
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return value at position
     */
    public int getValue(int x, int y) {
        return boardTable.get(x).get(y);
    }

    /**
     * Sets value at board position
     *
     * @param x     x-coordinate
     * @param y     y-coordinate
     * @param value value to set
     */
    public void setValue(int x, int y, int value) {
        boardTable.get(x).set(y, value);
    }

    /**
     * Moves a figure on the board
     *
     * @param x     x-direction movement
     * @param y     y-direction movement
     * @param color color of figure to move
     * @throws Exception if move is invalid
     */
    public void move(int x, int y, Color color) throws Exception {
        CloneableFigure figure = map.get(color);
        if (figure == null) throw exception;
        int xOld = ((Figure) figure).getX();
        int yOld = ((Figure) figure).getY();
        ((Figure) figure).move(x, y);
        int xNew = ((Figure) figure).getX();
        int yNew = ((Figure) figure).getY();
        if (xNew > n - 1 || xNew < 0 || yNew > n - 1 || yNew < 0) {
            ((Figure) figure).move(-x, -y);
            throw exception;
        }
        int value = getValue(xNew, yNew);
        if ((color == Color.RED || color == Color.REDCLONE) && value == -2) {
            ((Figure) figure).move(-x, -y);
            throw exception;
        }
        if ((color == Color.GREEN || color == Color.GREENCLONE) && value == -1) {
            ((Figure) figure).move(-x, -y);
            throw exception;
        }

        System.out.print(color + " MOVED TO " + (xNew + 1) + " " + (yNew + 1));
        setValue(xOld, yOld, 0);
        if (color == Color.GREEN || color == Color.GREENCLONE) {
            setValue(xNew, yNew, -1);
        } else {
            setValue(xNew, yNew, -2);
        }
        if (value > 0) {
            if (color == Color.RED || color == Color.REDCLONE) redScore += value;
            else greenScore += value;
            System.out.println(" AND COLLECTED " + value);
        } else if (value < 0) {
            Color entry;
            if (value == -1) {
                if (((Figure) map.get(Color.GREEN)).getX() == xNew && ((Figure) map.get(Color.GREEN)).getY() == yNew)
                    entry = Color.GREEN;
                else entry = Color.GREENCLONE;
            } else {
                if (((Figure) map.get(Color.RED)).getX() == xNew && ((Figure) map.get(Color.RED)).getY() == yNew)
                    entry = Color.RED;
                else entry = Color.REDCLONE;
            }
            System.out.println(" AND KILLED " + entry);
            map.put(entry, null);
        } else {
            System.out.println();
        }
    }

    /**
     * Changes the movement style of a figure
     *
     * @param color color of figure to change
     * @throws Exception if figure doesn't exist
     */
    public void changeStyle(Color color) throws Exception {
        CloneableFigure figure = map.get(color);
        if (figure == null) throw exception;
        Style style = ((Figure) figure).changeStyle();
        System.out.println(color + " CHANGED STYLE TO " + style);
    }

    /**
     * Prints the final game score and result
     */
    public void printScore() {
        if (redScore > greenScore) {
            System.out.println("RED TEAM WINS. SCORE " + greenScore + " " + redScore);
        } else if (greenScore > redScore) {
            System.out.println("GREEN TEAM WINS. SCORE " + greenScore + " " + redScore);
        } else {
            System.out.println("TIE. SCORE " + redScore + " " + greenScore);
        }
    }

}

/**
 * Interface defining movement behavior (for pattern State)
 */
interface PlayingStyle {

    /**
     * Calculates x-axis movement
     *
     * @param x base movement
     * @return modified movement
     */
    public int moveX(int x);

    /**
     * Calculates y-axis movement
     *
     * @param y base movement
     * @return modified movement
     */
    public int moveY(int y);
}

/**
 * Normal movement style (1x multiplier)
 */
class NormalStyle implements PlayingStyle {

    @Override
    public int moveX(int x) {
        return x;
    }

    @Override
    public int moveY(int y) {
        return y;
    }
}

/**
 * Attack movement style (2x multiplier)
 */
class AttackStyle implements PlayingStyle {

    @Override
    public int moveX(int x) {
        return x * 2;
    }

    @Override
    public int moveY(int y) {
        return y * 2;
    }
}

/**
 * Enum representing figures
 */
enum Color {
    GREEN("GREEN"),
    GREENCLONE("GREENCLONE"),
    RED("RED"),
    REDCLONE("REDCLONE");

    Color(String color) {
    }

    /**
     * Gets Color enum from string
     *
     * @param color color string
     * @return corresponding Color enum or null
     */
    static Color getEnum(String color) {
        switch (color) {
            case "GREEN":
                return GREEN;
            case "GREENCLONE":
                return GREENCLONE;
            case "RED":
                return RED;
            case "REDCLONE":
                return REDCLONE;
            default:
                return null;
        }
    }
}

/**
 * Enum representing movement styles
 */
enum Style {
    NORMAL,
    ATTACKING
}

/**
 * Interface for cloneable figures (for pattern Prototype)
 */
interface CloneableFigure {
    public CloneableFigure clone();
}

/**
 * Base class for game figures
 */
class Figure {

    PlayingStyle style;
    int x;
    int y;

    /**
     * Creates a new figure
     *
     * @param x initial x position
     * @param y initial y position
     */
    public Figure(int x, int y) {
        this.x = x;
        this.y = y;
        style = new NormalStyle();
    }

    /**
     * Gets x position
     *
     * @return x coordinate
     */
    int getX() {
        return x;
    }

    /**
     * Gets y position
     *
     * @return y coordinate
     */
    int getY() {
        return y;
    }

    /**
     * Moves the figure
     *
     * @param x x movement
     * @param y y movement
     */
    public void move(int x, int y) {
        this.x += style.moveX(x);
        this.y += style.moveY(y);
    }

    /**
     * Changes the figure's movement style
     *
     * @return new style
     */
    public Style changeStyle() {
        if (style.getClass() == NormalStyle.class) {
            style = new AttackStyle();
            return Style.ATTACKING;
        } else {
            style = new NormalStyle();
            return Style.NORMAL;
        }
    }

}

/**
 * Green team figure implementation
 */
class GreenFigure extends Figure implements CloneableFigure {

    /**
     * Creates a new green figure
     *
     * @param x initial x position
     * @param y initial y position
     */
    public GreenFigure(int x, int y) {
        super(x, y);
    }

    @Override
    public CloneableFigure clone() {
        return new GreenFigure(y, x);
    }

}

/**
 * Red team figure implementation
 */
class RedFigure extends Figure implements CloneableFigure {

    /**
     * Creates a new red figure
     *
     * @param x initial x position
     * @param y initial y position
     */
    public RedFigure(int x, int y) {
        super(x, y);
    }

    @Override
    public CloneableFigure clone() {
        return new RedFigure(y, x);
    }
}
