package com.example.memory3;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MemoryImageIndex {
    private ArrayList<Pair<Integer,Drawable>> default_drawables = new ArrayList<Pair<Integer,Drawable>>();
    private HashSet<Integer> mIdCorrect = new HashSet<Integer>();

    private static Integer[] flower_imageIDs = {

            R.drawable.f1,
            R.drawable.f2,
            R.drawable.f3,
            R.drawable.f4,
            R.drawable.f5,
            R.drawable.f6,
            R.drawable.f7,
            R.drawable.f8,
            R.drawable.f9,
            R.drawable.f10,
            R.drawable.f11,
            R.drawable.f12
    };

    private static Integer[] dschungel_imageIDs = {

            R.drawable.leila,
            R.drawable.peter,
            R.drawable.basti,
            R.drawable.evelyn,
            R.drawable.sandra,
            R.drawable.sibylle,
            R.drawable.chris,
            R.drawable.tommi,
            R.drawable.felix,
            R.drawable.gisele,
            R.drawable.domenico,
            R.drawable.doreen
    };

    public static int id_cover = R.drawable.karo;

    private Context mContext;



    public MemoryImageIndex(Context context, String theme, int maxNCardPairs) {
        this.mContext = context;
        setupIndexFromDefault(theme, maxNCardPairs);
    }

    public void setupIndexFromDefault(String theme, int maxNCardPairs) {
        default_drawables.removeAll(default_drawables);

        Integer[] cur_imageIDs;

        if (theme.equalsIgnoreCase("Dschungel")) {
            cur_imageIDs = dschungel_imageIDs;
        } else {
            cur_imageIDs = flower_imageIDs;
        }

        ArrayList<Integer> tmpIdx = new ArrayList<Integer>();

        for (int i=0; i<cur_imageIDs.length; i++) {
            if (i>=maxNCardPairs)
                break;
            tmpIdx.add(i);
            tmpIdx.add(i);
        }

        Collections.shuffle(tmpIdx);

        for (int i=0; i<tmpIdx.size(); i++) {
            int idx = tmpIdx.get(i);
            default_drawables.add(new Pair(idx, this.mContext.getResources().getDrawable(cur_imageIDs[idx])));
        }
    }

    public void setupIndexFromDir() {

    }

    public int size() {
        return default_drawables.size();
    }

    public Drawable getDrawable(int i) {
        return default_drawables.get(i).second;
    }

    public int position2Id(int position) {
        return default_drawables.get(position).first;
    }

    public void addCorrectId(int id) {
        mIdCorrect.add(id);
    }

    public boolean isIdAlreadyCorrect(int id) {
        return mIdCorrect.contains(id);
    }

    public void reset() {
        mIdCorrect.clear();
    }

    public boolean isFinished() {
        return mIdCorrect.size() == Math.round(default_drawables.size() / 2);
    }
}
