package com.example.lqrecycleview.component;

import android.view.View;

import java.util.Stack;

public class Recycler {
    private Stack<View>[] mViews;


    public Recycler(int typeNum) {
        mViews = new Stack[typeNum];

        for (int i = 0; i < typeNum; i++) {
            mViews[i] = new Stack<>();
        }
    }

    public void put(View view, int type) {
        mViews[type].push(view);
    }

    public View get(int type) {
        try {
            return mViews[type].pop();
        } catch (Exception e) {
           return null;
        }
    }
}
