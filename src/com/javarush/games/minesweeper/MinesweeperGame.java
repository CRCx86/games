package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;

public class MinesweeperGame extends Game {

    private static final int SIDE = 4;

    private GameObject[][] gameField = new GameObject[SIDE][SIDE];

    private int countMinesOnField;

    private static final String MINE = "\uD83D\uDCA3";

    private static final String FLAG = "\uD83D\uDEA9";

    private int countFlags;

    private boolean isGameStopped;

    private int countClosedTiles = SIDE * SIDE;

    private int score;

    @Override
    public void initialize() {
        super.initialize();
        setScreenSize(SIDE, SIDE);
        this.createGame();
        showMine();
    }

    private void showMine() {
        for (GameObject[] gameObjects : gameField) {
            for (GameObject gameObject : gameObjects) {
                if (gameObject.isMine) {
                    System.out.println(String.format("y = %s, x = %s", gameObject.y, gameObject.x));
                }
            }
        }
    }

    private void createGame() {
        for (int x = 0; x < SIDE; x++) {
            for (int y = 0; y < SIDE; y++) {
                boolean isMine = getRandomNumber(10) == 0;
                if (isMine)
                    this.countMinesOnField++;

                this.gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y, "");
            }
        }
        this.countFlags = this.countMinesOnField;
        this.countMineNeighbors();
    }

    private void countMineNeighbors() {
        for (GameObject[] gameObjects : this.gameField) {
            for (GameObject gameObject : gameObjects) {
                if (!gameObject.isMine)
                    gameObject.countMineNeighbors = getNeighbors(gameObject);
            }
        }
    }

    private int getNeighbors(GameObject gameObject) {
        int result = 0;
        if (gameObject.y - 1 >= 0 && gameObject.x - 1 >= 0 && this.gameField[gameObject.y - 1][gameObject.x - 1].isMine)
            result += 1;
        if (gameObject.y - 1 >= 0 && this.gameField[gameObject.y - 1][gameObject.x].isMine)
            result += 1;
        if (gameObject.y - 1 >= 0 && gameObject.x + 1 < SIDE && this.gameField[gameObject.y - 1][gameObject.x + 1].isMine)
            result += 1;
        if (gameObject.x - 1 >= 0 && this.gameField[gameObject.y][gameObject.x - 1].isMine)
            result += 1;
        if (gameObject.x + 1 < SIDE && this.gameField[gameObject.y][gameObject.x + 1].isMine)
            result += 1;
        if (gameObject.y + 1 < SIDE && gameObject.x - 1 >= 0 && this.gameField[gameObject.y + 1][gameObject.x - 1].isMine)
            result += 1;
        if (gameObject.y + 1 < SIDE && this.gameField[gameObject.y + 1][gameObject.x].isMine)
            result += 1;
        if (gameObject.y + 1 < SIDE && gameObject.x + 1 < SIDE && this.gameField[gameObject.y + 1][gameObject.x + 1].isMine)
            result += 1;
        return result;
    }

    private void openTile(int x, int y) {

        if (x < 0 || x > SIDE - 1 || y < 0 || y > SIDE - 1)
            return;

        if (this.isGameStopped)
            return;

        if (this.gameField[y][x].isOpen)
            return;

        if (this.gameField[y][x].isFlag)
            return;

        GameObject gameObject = this.gameField[y][x];
        gameObject.isOpen = true;
        countClosedTiles--;

        if (countMinesOnField == countClosedTiles && !this.gameField[y][x].isMine)
            this.win();

        if (gameObject.isMine) {
            setCellValue(x, y, MINE);
            setCellValueEx(x, y, Color.RED, MINE);
            this.gameOver();
        } else {
            setCellNumber(x, y, gameObject.countMineNeighbors);
            setCellColor(gameObject.x, gameObject.y, Color.GREEN);
            score+=5;
            setScore(score);
            if (gameObject.countMineNeighbors == 0) {
                if (y - 1 >= 0 && x - 1 >= 0 && !gameField[y - 1][x - 1].isOpen)
                    openTile(gameObject.x - 1, gameObject.y - 1);
                if (y - 1 >= 0 && !gameField[y - 1][x].isOpen)
                    openTile(gameObject.x, gameObject.y - 1);
                if (y -1 >= 0 && x + 1 < SIDE && !gameField[y - 1][x + 1].isOpen)
                    openTile(gameObject.x + 1, gameObject.y - 1);
                if (x - 1 >= 0 && !gameField[y][x - 1].isOpen)
                    openTile(gameObject.x - 1, gameObject.y);
                if (x + 1 < SIDE && !gameField[y][x + 1].isOpen)
                    openTile(gameObject.x + 1, gameObject.y);
                if (y + 1 < SIDE && x - 1 >= 0 && !gameField[y + 1][x - 1].isOpen)
                    openTile(gameObject.x - 1, gameObject.y + 1);
                if (y + 1 < SIDE && !gameField[y + 1][x].isOpen)
                    openTile(gameObject.x, gameObject.y + 1);
                if (y + 1 < SIDE && x + 1 < SIDE && !gameField[y + 1][x + 1].isOpen)
                    openTile(gameObject.x + 1, gameObject.y + 1);
                int neighbors = getNeighbors(gameObject);
                setCellNumber(gameObject.x, gameObject.y, neighbors);
                setCellValue(gameObject.x, gameObject.y, "");
            }

        }
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
            return;
        }
        super.onMouseLeftClick(x, y);
        this.openTile(x, y);
    }

    private void markTile(int x, int y) {
        if (this.isGameStopped)
            return;

        if (gameField[y][x].isOpen)
            return;

        if (countFlags == 0 && !gameField[y][x].isFlag)
            return;

        if (!gameField[y][x].isFlag) {
            gameField[y][x].isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.YELLOW);
        } else {
            gameField[y][x].isFlag = false;
            countFlags++;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.ORANGE);
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        super.onMouseRightClick(x, y);
        markTile(x, y);
    }

    private void gameOver() {
        this.isGameStopped = true;
        showMessageDialog(Color.BLACK, "Game Over!", Color.RED, 25);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLUE, "WIN!", Color.GREEN, 25);
    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        countMinesOnField = 0;
        score = 0;
        setScore(score);
        createGame();
    }
}
