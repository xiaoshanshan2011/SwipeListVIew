package com.shan.swipelistview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add("jsdhfaojsfhasdhfaiusefgauyegf" + i);
        }

        final SlideViewAdapter adapter = new SlideViewAdapter(this, list);
        final SwipeListView swipeListView = (SwipeListView) findViewById(R.id.swipeListView);
        swipeListView.setAdapter(adapter);
        adapter.setRemoveListener(new SlideViewAdapter.OnMoveListener() {
            @Override
            public void onRemoveItem(int position) {
                Toast.makeText(MainActivity.this, "删除了" + position, Toast.LENGTH_SHORT).show();
                list.remove(position);
                adapter.notifyData(list);
                swipeListView.slideBack();
            }

            @Override
            public void onSaveItem(int position) {
                Toast.makeText(MainActivity.this, "保存了" + position, Toast.LENGTH_SHORT).show();
            }
        });

        swipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "点击了" + i, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
