package com.h2byte.h2icons.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;

import com.h2byte.h2icons.R;
import com.h2byte.h2icons.adapters.WallAdapter;
import com.h2byte.h2icons.items.WallpaperItem;
import com.h2byte.h2icons.others.SpacesItemDecoration;
import com.h2byte.h2icons.tasks.GetWallpapers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by architjn on 29/07/15.
 */
public class WallpaperActivity extends AppCompatActivity implements GetWallpapers.Callbacks {

    ArrayList<WallpaperItem> items = new ArrayList<>();
    private Context context;
    private WallAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_wall));
        context = this;
        Snackbar.make(findViewById(R.id.coordinating_wall), "Loading Wallpapers", Snackbar.LENGTH_SHORT).show();
        new GetWallpapers(this, this).execute();
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(getResources().getColor(R.color.primaryColorDark));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onListLoaded(String jsonResult) {
        try {
            if (jsonResult != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(jsonResult);
                    JSONArray jsonMainNode = jsonResponse.optJSONArray("walls");
                    for (int i = 0; i < jsonMainNode.length(); i++) {
                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                        items.add(new WallpaperItem(jsonChildNode.optString("name"),
                                jsonChildNode.optString("author"),
                                jsonChildNode.optString("url"),
                                jsonChildNode.optString("thumb")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        RecyclerView gridview = (RecyclerView) findViewById(R.id.wall_rv);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        (findViewById(R.id.progressBar_wall)).setVisibility(View.GONE);
        int numOfRows = (int) (size.x / getResources().getDimension(R.dimen.size_of_grid_item));
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        gridview.setLayoutManager(layoutManager);
        gridview.setHasFixedSize(true);
        gridview.addItemDecoration(new SpacesItemDecoration(8, 2));
        adapter = new WallAdapter(this, items, display);
        gridview.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == WallAdapter.REQUEST_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Storage permission has been granted
                adapter.storageRequestAccepted();
            } else {
                //Storage permission has been denied
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            super.onBackPressed();
        }
        return false;
    }
}
