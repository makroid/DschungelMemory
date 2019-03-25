package com.example.memory3;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.os.Environment;


import java.io.File;


public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

    static final int READ_REQUEST = 1;

    private GridView        mGridView;
    private MemoryGame      mGame;
    private ImageAdapter    mGridAdapter;
    private Button          mStartStopButton;

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.registerOnSharedPreferenceChangeListener(this);

        String theme = pref.getString("pref_theme", "Flowers");
        int maxNCardPairs = pref.getInt("pref_maxNCardPairs", 10);

        MemoryImageIndex gidx = new MemoryImageIndex(this, theme, maxNCardPairs);

        mGame = new MemoryGame(gidx);

        mGridView = (GridView) findViewById(R.id.gridview);
        mGridAdapter = new ImageAdapter(this);

        mGridView.setAdapter(mGridAdapter);
        mGridView.setEnabled(false);


        mStartStopButton = (Button) findViewById(R.id.startButton);
        //ViewCompat.setBackgroundTintList(startStopButton, ContextCompat.getColorStateList(this, android.R.color.holo_blue_light));
        mStartStopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                //startActivityForResult(intent, READ_REQUEST);
                if (mGame.getGameState()== MemoryGame.GameState.STOPPED) {
                    startGame();
                } else {
                    stopGame();
                }

            }
        });


        mGridView.post( new Runnable() {
                            @Override
                            public void run() {
                                Display display = getWindowManager().getDefaultDisplay();
                                Point size = new Point();
                                display.getSize(size);
                                int width = size.x;


                                LinearLayout ll = (LinearLayout) findViewById(R.id.outerLinearLayout);

                                int gwidth = mGridView.getColumnWidth()*mGridView.getNumColumns();
                                int padding = (width - gwidth) / 2;
                                ll.setPadding(padding, 10, padding, 10);

                                ll.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
                            }
                        });

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = pref.edit();
        editor.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public SharedPreferences getSharedPreferences() {
        return pref;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
        String theme = pref.getString("pref_theme", "Flowers");
        int maxNCardPairs = pref.getInt("pref_maxNCardPairs", 10);

        if (key.equals("pref_theme")) {
            stopGame();
            updateIndexGame(theme, maxNCardPairs);
        }  else if (key.equals("pref_maxNCardPairs")) {
            stopGame();
            updateIndexGame(theme, maxNCardPairs);
        }
    }

    public void updateIndexGame(String theme, int maxNCardPairs) {
        mGame.updateIndex(theme, maxNCardPairs);
        mGridAdapter.notifyDataSetChanged();
        enableGrid(false);
    }

    public void startGame() {
        String theme = pref.getString("pref_theme", "Flowers");
        int maxNCardPairs = pref.getInt("pref_maxNCardPairs", 10);

        mGame.startGame(theme, maxNCardPairs);
        mGridAdapter.notifyDataSetChanged();
        enableGrid(true);
        mStartStopButton.setText("Stop");
    }

    public void stopGame() {
        mGame.stopGame();
        enableGrid(false);
        resetCovered();
        mStartStopButton.setText("Start...");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().show();
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        //getMenuInflater().inflate(R.menu.main, menu);
        getActionBar().setDisplayShowTitleEnabled(true);
        return true;
    }

    public boolean onClickSettings(MenuItem item) {
        Intent myIntent = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(myIntent);
        return true;
    }

    public void resetCovered() {
        ViewGroup group = (ViewGroup)mGridView;

        for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
            MemoryImageView miv = (MemoryImageView) group.getChildAt(idx);
            miv.cover();
        }
    }


    public void enableGrid(boolean enabled) {
        mGridView.setEnabled(enabled);
        if ( mGridView instanceof ViewGroup ) {
            ViewGroup group = (ViewGroup)mGridView;

            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                group.getChildAt(idx).setEnabled(enabled);
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == READ_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                //String dirPath = uri.toString();
                //String dirPath = "/storage/emulated/0/Download/flowers";

                final String[] split = uri.getPath().split(":");//split the path.
                String subdirPath = split[1];
                String dirPath = Environment.getExternalStorageDirectory().toString();
                dirPath = dirPath + "/Download/" + subdirPath;

                File directory = new File(dirPath);
                File[] files = directory.listFiles();

                Toast t;
                //t = Toast.makeText(getBaseContext(), dirPath, Toast.LENGTH_LONG);
                //t.show();

                if (files != null) {
                    String s = "";
                    for (int i=0; i<files.length; i++) {
                        s += files[i].toString() + "\n";
                    }

                    t = Toast.makeText(getBaseContext(), "ok " + s, Toast.LENGTH_LONG);
                    t.show();
                } else {
                    t = Toast.makeText(getBaseContext(), "null", Toast.LENGTH_LONG);
                    t.show();
                }
            }
        }
    }


    public class ImageAdapter extends BaseAdapter
    {
        private MainActivity mContext;

        public ImageAdapter(MainActivity c)
        {
            mContext = c;
        }

        public int getCount() {
            return mGame.mGidx.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            MemoryImageView imageView;
            if (convertView == null) {
                imageView = new MemoryImageView(mContext, position, mGame);
                imageView.setLayoutParams(new GridView.LayoutParams(194, 198));
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setPadding(2, 2, 2, 2);
            } else {
                imageView = (MemoryImageView) convertView;
            }
            //imageView.setImageResource(imageIDs[imageView.get_img_id()]);
            //imageView.setImageDrawable(mGame.mGidx.getDrawable(imageView.getPosition()));
            imageView.setImageResource(MemoryImageIndex.id_cover);
            return imageView;
        }
    }
}


