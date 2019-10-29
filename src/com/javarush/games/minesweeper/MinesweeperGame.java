package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;

public class MinesweeperGame extends Game {

    private static final int SIDE = 9;

    private GameObject[][] gameField = new GameObject[SIDE][SIDE];

    private int countMinesOnField;

    private static final String MINE = "\\uD83D\\uDCA3";

    @Override
    public void initialize() {
        super.initialize();
        setScreenSize(SIDE, SIDE);
        this.createGame();
    }

    private void createGame() {
        for (int x = 0; x < SIDE; x++) {
            for (int y = 0; y < SIDE; y++) {
                boolean isMine = getRandomNumber(10) == 0;
                if (isMine)
                    this.countMinesOnField++;

                this.gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
            }
        }
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
        GameObject gameObject = this.gameField[y][x];
        gameObject.isOpen = true;
        if (gameObject.isMine) {
            setCellValue(x, y, MINE);
        } else {
            setCellNumber(x, y, gameObject.countMineNeighbors);
            setCellColor(gameObject.x, gameObject.y, Color.GREEN);
        }
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        super.onMouseLeftClick(x, y);
        this.openTile(x, y);
    }
}
