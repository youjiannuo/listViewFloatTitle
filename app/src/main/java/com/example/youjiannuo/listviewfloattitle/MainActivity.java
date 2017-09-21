package com.example.youjiannuo.listviewfloattitle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Email:38203424@qq.com
 */
public class MainActivity extends AppCompatActivity {

    List<Model> mModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);
        Adapter adapter = new Adapter();


        for (int i = 0; i < 20; i++) {
            Model model1 = new Model();
            model1.item = String.valueOf((char) ('A' + i));
            model1.type = 1;
            mModels.add(model1);
            for (int j = 0; j < 5; j++) {
                Model model = new Model();
                model.item = String.valueOf(new char[]{(char) ('A' + i), (char) ('A' + j)});
                SystemUtil.printlnInfo(model.item);
                mModels.add(model);
            }
        }

        listView.setAdapter(adapter);

        ViewGroup parent = (ViewGroup) findViewById(R.id.layout);
        ListViewFloatTitleController listViewFloatTitleController = new ListViewFloatTitleController(listView, parent, R.layout.float_view);
        listViewFloatTitleController.setOperationListener(new ListViewFloatTitleController.OperationListener() {
            private TextView titleView;

            @Override
            public boolean isResetTitle() {
                return true;
            }

            @Override
            public void setTitle(int position, ViewGroup view) {
                Model model = mModels.get(position);
                if (model != null) {
                    if (titleView == null) {
                        titleView = (TextView) view.findViewById(R.id.title);
                    }
                    titleView.setText(model.item.substring(0, 1));
                }
            }

            @Override
            public int getHead() {
                return 0;
            }

            @Override
            public boolean isShowTitle() {
                return true;
            }

            @Override
            public String getTitleString(int position) {
                Model model = mModels.get(position);
                return model.item.substring(0, 1);
            }
        });

    }


    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mModels.size();
        }

        @Override
        public Object getItem(int i) {
            return mModels.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_view, null);
            }
            TextView textView = (TextView) view.findViewById(R.id.item);
            TextView titleTextView = (TextView) view.findViewById(R.id.title);
            Model model = mModels.get(i);
            textView.setVisibility(View.GONE);
            titleTextView.setVisibility(View.GONE);
            if (model.type == 0) {
                textView.setText(model.item);
                textView.setVisibility(View.VISIBLE);
            } else {
                titleTextView.setText(model.item);
                titleTextView.setVisibility(View.VISIBLE);
            }
            return view;
        }
    }


    class Model {
        String item;
        int type = 0;
    }


}
