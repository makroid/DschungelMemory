package com.example.memory3;


import android.util.Pair;

public class MemoryGame {
    public enum GameState {
        STARTED,
        STOPPED
    }

    public enum CardState {
        None,
        TurnOne
    }

    public Pair<Integer, MemoryImageView> position_turned_1;
    public Pair<Integer, MemoryImageView> position_turned_2;

    public GameState mGS;
    public CardState mCS;

    public MemoryImageIndex mGidx;

    private long mStartTime;
    private long mEndTime;

    public MemoryGame(MemoryImageIndex gidx) {
        reset();
        mGidx               = gidx;
    }

    private void reset() {
        mGS                 = GameState.STOPPED;
        mCS                 = CardState.None;
        position_turned_1   = new Pair(-1, null);
        position_turned_2   = new Pair(-1, null);
    }

    public GameState getGameState() {
        return mGS;
    }

    public void startGame(String theme, int maxNCardPairs) {
        mGS = GameState.STARTED;
        mGidx.setupIndexFromDefault(theme, maxNCardPairs);
        mStartTime = System.currentTimeMillis();
    }

    public void stopGame() {
        reset();
        mGidx.reset();
    }

    public void updateIndex(String theme, int maxNCardPairs) {
        mGidx.setupIndexFromDefault(theme, maxNCardPairs);
    }

    public long getPlayTime() {
        return System.currentTimeMillis() - mStartTime;
    }

    public boolean checkCardsEqual() {
        if (position_turned_1.first==-1 || position_turned_2.first==-1) {
            return false;
        }
        if (mGidx.position2Id(position_turned_1.first)==mGidx.position2Id(position_turned_2.first)) {
            mGidx.addCorrectId(mGidx.position2Id(position_turned_1.first));
            return true;
        }
        return false;
    }
}
