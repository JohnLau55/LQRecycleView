package com.example.lqrecycleview;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import com.example.lqrecycleview.component.LQRecycleView;
import com.example.lqrecycleview.component.MyAdapter;
import com.example.lqrecycleview.R;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        LQRecycleView recycleView = (LQRecycleView) findViewById(R.id.recycleView);


        List<String> list = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            list.add("liuqiang #" + i);
        }

        MyAdapter myAdapter = new MyAdapter(this);
        myAdapter.setItems(list);

        recycleView.setMyAdapter(myAdapter);
    }
}