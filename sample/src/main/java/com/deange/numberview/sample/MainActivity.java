package com.deange.numberview.sample;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

    private static final String[] TYPES = {
            "NumberView",
            "NumberViewGroup",
    };

    private static final Class[] ACTIVITES = new Class[]{
            NumberActivity.class,
            NumberGroupActivity.class,
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new TypesAdapter());
    }

    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
        startActivity(new Intent(this, ACTIVITES[position]));
    }

    private class TypesAdapter extends ArrayAdapter<String> {
        public TypesAdapter() {
            super(MainActivity.this, android.R.layout.simple_list_item_1, TYPES);
        }
    }
}
