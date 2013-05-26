package com.example.BeThere;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: AGMacBookOscar
 * Date: 26/05/13
 * Time: 13:08
 * To change this template use File | Settings | File Templates.
 */
public class VisitedListActivity extends ListActivity {
    List<String> visited = null;
    //private ListView list;
    //private ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, elements);

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listalayout);

        Gson gson = new Gson();
        SharedPreferences preferences = getSharedPreferences("pref", 0);
        String visitedCities = preferences.getString("visitedCities", " ");
        Log.i("ArrayString", "ciudades visitadas" + visitedCities);

        visited = gson.fromJson(visitedCities, ArrayList.class);

        ListView list = (ListView) this.findViewById(android.R.id.list);
        list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, visited));
    }

    @Override
    protected void onListItemClick(ListView listView, View v, int position, long id) {
        String search = visited.get(position);
        Uri uri = Uri.parse("http://www.google.com/#q=" + search);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
