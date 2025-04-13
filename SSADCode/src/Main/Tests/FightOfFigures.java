package Main.Tests;

import java.util.*;

public class FightOfFigures {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int len = scanner.nextInt();
        int greenX = scanner.nextInt();
        int greenY = scanner.nextInt();
        int redX = scanner.nextInt();
        int redY = scanner.nextInt();
        int numberOfCoins = scanner.nextInt();
        GameBoard board = new GameBoard(len);
        for (int i = 0; i < numberOfCoins; i++) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            int value = scanner.nextInt();
            board.addCoin(x, y, value);
        }
        TeamComposite greenTeam = new TeamComposite();
        TeamComposite redTeam = new TeamComposite();
        BaseFigure greenFigure = new BaseFigure("GREEN", "GREEN", greenX, greenY);
        BaseFigure redFigure = new BaseFigure("RED", "RED", redX, redY);
        FigureLeaf greenLeaf = new FigureLeaf(greenFigure);
        FigureLeaf redLeaf = new FigureLeaf(redFigure);
        greenTeam.add(greenLeaf);
        redTeam.add(redLeaf);
        Map<String, BaseFigure> figureMap = new HashMap<>();
        figureMap.put("GREEN", greenFigure);
        figureMap.put("RED", redFigure);
        int numberOfActions = scanner.nextInt();
        scanner.nextLine();
        for (int actionIndex = 1; actionIndex <= numberOfActions; actionIndex++) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                actionIndex--;
                continue;
            }
            String[] parts = line.split("\\s+");
            if (parts.length != 2) {
                System.out.println("INVALID ACTION");
                continue;
            }
            String figureName = parts[0];
            String action = parts[1];
            BaseFigure figure = figureMap.get(figureName);
            if (figure == null) {
                System.out.println("INVALID ACTION");
                continue;
            }
            if (!figure.isAlive()) {
                System.out.println("INVALID ACTION");
                continue;
            }
            if (action.equals("STYLE")) {
                figure.changeState();
                System.out.println(figureName + " CHANGED STYLE TO " + figure.getCurrentState().getStateName());
            } else if (action.equals("COPY")) {
                if (figure.isClone()) {
                    System.out.println("INVALID ACTION");
                    continue;
                }
                int x = figure.getX();
                int y = figure.getY();
                if (x == y) {
                    System.out.println("INVALID ACTION");
                    continue;
                }
                if (board.getCoinValue(y, x) != null) {
                    System.out.println("INVALID ACTION");
                    continue;
                }
                if (isFigureInCell(y, x, greenTeam, redTeam)) {
                    System.out.println("INVALID ACTION");
                    continue;
                }
                String cloneName;
                if (figure.getTeamColor().equals("GREEN"))
                    cloneName = "GREENCLONE";
                else cloneName = "REDCLONE";
                if (figureMap.containsKey(cloneName) && figureMap.get(cloneName).isAlive()) {
                    System.out.println("INVALID ACTION");
                    continue;
                }
                BaseFigure newClone = figure.createClone(cloneName);
                newClone.setPosition(y, x);
                if (figure.getTeamColor().equals("GREEN")) {
                    greenTeam.add(new FigureLeaf(newClone));
                } else {
                    redTeam.add(new FigureLeaf(newClone));
                }
                figureMap.put(cloneName, newClone);
                System.out.println(figureName + " CLONED TO " + y + " " + x);
            } else if (action.equals("UP") || action.equals("DOWN") || action.equals("LEFT") || action.equals("RIGHT")) {
                int step;
                if (figure.getCurrentState() instanceof AttackingState)
                    step = 2;
                else step = 1;
                int targetX = figure.getX();
                int targetY = figure.getY();
                switch (action) {
                    case "UP":
                        targetX -= step;
                        break;
                    case "DOWN":
                        targetX += step;
                        break;
                    case "LEFT":
                        targetY -= step;
                        break;
                    case "RIGHT":
                        targetY += step;
                        break;
                }
                if (!board.isOnTheBoard(targetX, targetY)) {
                    System.out.println("INVALID ACTION");
                    continue;
                }
                if (!figure.getCurrentState().canMove(figure, targetX, targetY)) {
                    System.out.println("INVALID ACTION");
                    continue;
                }
                if (isSameTeamInTheCell(targetX, targetY, figure.getTeamColor(), greenTeam, redTeam)) {
                    System.out.println("INVALID ACTION");
                    continue;
                }
                BaseFigure enemy = findEnemy(targetX, targetY, figure.getTeamColor(), greenTeam, redTeam);
                Integer coinValue = board.getCoinValue(targetX, targetY);
                figure.setPosition(targetX, targetY);
                if (enemy != null && enemy.isAlive()) {
                    enemy.setAlive(false);
                    System.out.println(figure.getName() + " MOVED TO " + targetX + " " + targetY + " AND KILLED " + enemy.getName());
                } else if (coinValue != null) {
                    figure.addScore(coinValue);
                    board.removeCoin(targetX, targetY);
                    System.out.println(figure.getName() + " MOVED TO " + targetX + " " + targetY + " AND COLLECTED " + coinValue);
                } else {
                    System.out.println(figure.getName() + " MOVED TO " + targetX + " " + targetY);
                }
            } else {
                System.out.println("INVALID ACTION");
            }
        }
        int greenScore = greenTeam.getScore();
        int redScore = redTeam.getScore();
        if (greenScore == redScore) {
            System.out.println("TIE. SCORE " + greenScore + " " + redScore);
        } else if (greenScore > redScore) {
            System.out.println("GREEN TEAM WINS. SCORE " + greenScore + " " + redScore);
        } else {
            System.out.println("RED TEAM WINS. SCORE " + greenScore + " " + redScore);
        }
    }

    private static boolean isFigureInCell(int x, int y, TeamComposite greenTeam, TeamComposite redTeam) {
        List<BaseFigure> figures = new ArrayList<>();
        figures.addAll(greenTeam.getAllFigures());
        figures.addAll(redTeam.getAllFigures());
        for (BaseFigure figure : figures) {
            if (figure.isAlive() && figure.getX() == x && figure.getY() == y) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSameTeamInTheCell(int x, int y, String color, TeamComposite greenTeam, TeamComposite redTeam) {
        List<BaseFigure> figures = new ArrayList<>();
        figures.addAll(greenTeam.getAllFigures());
        figures.addAll(redTeam.getAllFigures());
        for (BaseFigure figure : figures) {
            if (figure.isAlive() && figure.getX() == x && figure.getY() == y && figure.getTeamColor().equals(color)) {
                return true;
            }
        }
        return false;
    }

    private static BaseFigure findEnemy(int x, int y, String color, TeamComposite greenTeam, TeamComposite redTeam) {
        List<BaseFigure> figures = new ArrayList<>();
        figures.addAll(greenTeam.getAllFigures());
        figures.addAll(redTeam.getAllFigures());
        for (BaseFigure figure : figures) {
            if (figure.isAlive() && figure.getX() == x && figure.getY() == y
                    && !figure.getTeamColor().equals(color)) {
                return figure;
            }
        }
        return null;
    }
}

interface FigurePrototype {
    FigurePrototype cloneFigure();
}

interface FigureState {
    boolean canMove(BaseFigure figure, int newX, int newY);

    String getStateName();
}

interface TeamComponent {
    int getScore();

    boolean isFigureAlive();

    List<BaseFigure> getAllFigures();
}

class NormalState implements FigureState {
    @Override
    public boolean canMove(BaseFigure figure, int newX, int newY) {
        int shiftX = Math.abs(newX - figure.getX());
        int shiftY = Math.abs(newY - figure.getY());
        return (shiftX + shiftY == 1);
    }

    @Override
    public String getStateName() {
        return "NORMAL";
    }
}

class AttackingState implements FigureState {
    @Override
    public boolean canMove(BaseFigure figure, int newX, int newY) {
        int shiftX = Math.abs(newX - figure.getX());
        int shiftY = Math.abs(newY - figure.getY());
        return ((shiftX == 2 && shiftY == 0) || (shiftX == 0 && shiftY == 2));
    }

    @Override
    public String getStateName() {
        return "ATTACKING";
    }
}

class BaseFigure implements FigurePrototype {
    private final String name;
    private final String teamColor;
    private int x;
    private int y;
    private boolean alive;
    private final boolean isClone;
    private int score;
    private FigureState currentState;

    public BaseFigure(String name, String teamColor, int x, int y) {
        this.name = name;
        this.teamColor = teamColor;
        this.x = x;
        this.y = y;
        this.alive = true;
        this.isClone = false;
        this.score = 0;
        this.currentState = new NormalState();
    }

    public BaseFigure(BaseFigure original, String cloneName) {
        this.name = cloneName;
        this.teamColor = original.teamColor;
        this.x = original.x;
        this.y = original.y;
        this.alive = true;
        this.isClone = true;
        this.score = 0;
        this.currentState = new NormalState();
    }

    @Override
    public FigurePrototype cloneFigure() {
        return null;
    }

    public BaseFigure createClone(String cloneName) {
        return new BaseFigure(this, cloneName);
    }

    public String getName() {
        return name;
    }

    public String getTeamColor() {
        return teamColor;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isClone() {
        return isClone;
    }

    public int getScore() {
        return score;
    }

    public FigureState getCurrentState() {
        return currentState;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    public void addScore(int value) {
        this.score += value;
    }

    public void changeState() {
        if (currentState instanceof NormalState) {
            this.currentState = new AttackingState();
        } else {
            this.currentState = new NormalState();
        }
    }
}

class FigureLeaf implements TeamComponent {
    private final BaseFigure figure;

    public FigureLeaf(BaseFigure figure) {
        this.figure = figure;
    }

    @Override
    public int getScore() {
        return figure.getScore();
    }

    @Override
    public boolean isFigureAlive() {
        return figure.isAlive();
    }

    @Override
    public List<BaseFigure> getAllFigures() {
        return Collections.singletonList(figure);
    }
}

class TeamComposite implements TeamComponent {
    private final List<TeamComponent> children = new ArrayList<>();

    public void add(TeamComponent component) {
        children.add(component);
    }

    @Override
    public int getScore() {
        int totalScore = 0;
        for (TeamComponent component : children) {
            totalScore += component.getScore();
        }
        return totalScore;
    }

    @Override
    public boolean isFigureAlive() {
        for (TeamComponent component : children) {
            if (component.isFigureAlive()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<BaseFigure> getAllFigures() {
        List<BaseFigure> result = new ArrayList<>();
        for (TeamComponent component : children) {
            result.addAll(component.getAllFigures());
        }
        return result;
    }
}

class GameBoard {
    int len;
    Map<String, Integer> coins;

    public GameBoard(int len) {
        this.len = len;
        this.coins = new HashMap<>();
    }

    private String key(int x, int y) {
        return x + " " + y;
    }

    public void addCoin(int x, int y, int value) {
        coins.put(key(x, y), value);
    }

    public Integer getCoinValue(int x, int y) {
        return coins.get(key(x, y));
    }

    public void removeCoin(int x, int y) {
        coins.remove(key(x, y));
    }

    public boolean isOnTheBoard(int x, int y) {
        return (x >= 1 && x <= len && y >= 1 && y <= len);
    }
}