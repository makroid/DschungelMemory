package com.example.memory3;

import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class MemoryImageView extends ImageView implements View.OnTouchListener {

    private MainActivity mParent;
    private MemoryGame mGame;
    private int mPosition;


    public MemoryImageView(MainActivity parent, int position, MemoryGame mg) {
        super(parent);
        this.mParent = parent;
        this.mPosition = position;
        this.setOnTouchListener(this);
        this.setEnabled(false);
        this.mGame = mg;
    }

    public int getPosition() {
        return this.mPosition;
    }

    public void cover() {
        this.setImageResource(MemoryImageIndex.id_cover);
    }


    private void handleTouchDown() {
        int position = getPosition();
        int id = mGame.mGidx.position2Id(position);

        if (mGame.mGidx.isIdAlreadyCorrect(id)) {
            int i = 1+1;
            return;
        }

        if (mGame.mCS == MemoryGame.CardState.None) {
            this.setImageDrawable(mGame.mGidx.getDrawable(position));
            mGame.mCS = MemoryGame.CardState.TurnOne;
            mGame.position_turned_1 = new Pair(position, this);
        } else if (mGame.mCS == MemoryGame.CardState.TurnOne) {

            if (position == mGame.position_turned_1.first) {
                return;
            }
            this.setImageDrawable(mGame.mGidx.getDrawable(position));
            mGame.position_turned_2 = new Pair(position, this);

            if (mGame.checkCardsEqual()) {
                mGame.mCS = MemoryGame.CardState.None;
                if (mGame.mGidx.isFinished()) {
                    float playTime = mGame.getPlayTime() / 1000;
                    Toast t;
                    t = Toast.makeText(mParent, "Time: " + Float.toString(playTime) + "sec", Toast.LENGTH_LONG);
                    t.show();
                    mGame.position_turned_1.second.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mParent.stopGame();
                        }
                    }, 2000);
                }
            } else {
                this.mParent.enableGrid(false);
                long timeVisible = mParent.getSharedPreferences().getInt("pref_cardUncoveredTime", 1000);
                mGame.position_turned_1.second.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mGame.position_turned_1.second.cover();
                        mGame.position_turned_2.second.cover();
                        mGame.mCS = MemoryGame.CardState.None;
                        mParent.enableGrid(true);
                    }
                }, timeVisible);

            }
        }

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        Log.d("tag", "click");
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handleTouchDown();
                break;
        }
        return true;
    }
}
