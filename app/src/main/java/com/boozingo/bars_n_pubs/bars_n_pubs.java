package com.boozingo.bars_n_pubs;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boozingo.SearchAnimationToolbar;
import com.boozingo.ToolbarActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.boozingo.R;
import com.boozingo.helper.DBHelper;
import com.boozingo.helper.HttpHandler;
import com.boozingo.helper.ImageUtils;
import com.boozingo.helper.SnackBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.boozingo.Boozingo.url;

public class bars_n_pubs extends AppCompatActivity implements SnackBarClass.SnackbarMessage, SearchAnimationToolbar.OnSearchQueryChangedListener {

    public CoordinatorLayout layout;
    SearchAnimationToolbar toolbar;
    AppBarLayout appBarLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;

    String city;
    TextView city_name;
    ImageView city_image;
    String TAG = "TAG";
    ProgressDialog pDialog;
    public static JSONArray bars = new JSONArray(), pubs = new JSONArray(), shops = new JSONArray(),
            lounges = new JSONArray(), clubs = new JSONArray();

    // for snack bar
    SnackBarClass snackBarClass;
    Snackbar snackbar;
    private boolean internetConnected = true;
    public static String internetStatus = "";

    DBHelper dbHelper;
    byte[] bytes;

    FragBar fragBar = new FragBar();
    FragPub fragPub = new FragPub();
    FragLounge fragLounge = new FragLounge();
    FragBeer_shop fragBeer_shop = new FragBeer_shop();
    FragNight_club fragNight_club = new FragNight_club();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.temp8);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        init();

        toolbar.setSupportActionBar(bars_n_pubs.this);
        toolbar.setOnSearchQueryChangedListener(bars_n_pubs.this);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //       city = getIntent().getStringExtra("city");
        city = "delhi";

        toolbar.setVisibility(View.GONE);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset <= 100) {
                    //     collapsingToolbarLayout.setTitle(getString(R.string.app_name));
                    //                  collapsingToolbarLayout.setTitle(city.substring(0, 1).toUpperCase() + city.substring(1));
                    toolbar.setVisibility(View.VISIBLE);
                    isShow = true;

                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    toolbar.setVisibility(View.GONE);
                    isShow = false;
                }
            }
        });


        bars = new JSONArray();


        dbHelper = new DBHelper(this);
        snackBarClass = new SnackBarClass(this);
        snackBarClass.readySnackbarMessage(this);


        city_name.setText("");


        pDialog = new ProgressDialog(bars_n_pubs.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(true);
        pDialog.show();


        setupViewPager(viewPager);

        new net().execute();

    }

    private void init() {

        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        appBarLayout = findViewById(R.id.app_bar_layout);
        toolbar = findViewById(R.id.toolbar);
        city_name = findViewById(R.id.city_name);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        city_image = findViewById(R.id.city_image);
        layout = findViewById(R.id.container);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(fragBar, "Bars");
        adapter.addFragment(fragPub, "Pubs");
        adapter.addFragment(fragLounge, "Lounges");
        adapter.addFragment(fragBeer_shop, "Shops");
        adapter.addFragment(fragNight_club, "Night Clubs");
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {

        boolean handledByToolbar = toolbar.onBackPressed();

        if (!handledByToolbar) {
            super.onBackPressed();
        }
    }

    @Override
    public void onSearchCollapsed() {
        fragBar.search("");
    }

    @Override
    public void onSearchQueryChanged(String query) {

    }

    @Override
    public void onSearchExpanded() {

    }

    @Override
    public void onSearchSubmitted(String query) {


        String f = adapter.getItem(viewPager.getCurrentItem()).getTag();

        if (f.equals(fragBar.getTag()))
            fragBar.search(query);
        else if (f.equals(fragLounge.getTag()))
            fragLounge.search(query);
        else if (f.equals(fragPub.getTag()))
            fragPub.search(query);
        else if (f.equals(fragBeer_shop.getTag()))
            fragBeer_shop.search(query);
        else if (f.equals(fragNight_club.getTag()))
            fragNight_club.search(query);

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private class net extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            final String jsonStr = sh.makeServiceCall(url + "/" + city);


            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject object = new JSONObject(jsonStr);
                    JSONObject data = object.getJSONObject("city_detail");
                    final String pic_url = url + "/storage/" + data.getString("city_image");
                    bars = object.getJSONArray("bars");

                    //change pubs ro rest
                    pubs = object.getJSONArray("pubs");

                    shops = object.getJSONArray("beer_shops");

                    lounges = object.getJSONArray("lounges");

                    clubs = object.getJSONArray("night_clubs");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            setupViewPager(viewPager);
                            tabLayout.setupWithViewPager(viewPager);

                            bytes = loadImageFromDB(city + "_full");
                            if (bytes != null) {
                                city_image.setImageBitmap(ImageUtils.getImage(bytes));
                                pDialog.dismiss();
                            } else {

                                RequestOptions options = new RequestOptions()
                                        .centerInside()
                                        .priority(Priority.HIGH);

                                Glide.with(bars_n_pubs.this)
                                        .load(pic_url)
                                        .apply(options)
                                        .into(new SimpleTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                                Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                                                city_image.setImageBitmap(bitmap);
                                                saveImageInDB(bitmap, city + "_full");
                                                pDialog.dismiss();
                                            }
                                        });
                            }

                        }
                    });

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Problem retrieving data. Restart application.",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Network problem. Check network connection.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            return null;
        }
    }


    // for database images
    void saveImageInDB(Bitmap bitmap, String id) {

        dbHelper.open();
        byte[] inputData = ImageUtils.getImageBytes(bitmap);
        dbHelper.insertImage(inputData, id);
        dbHelper.close();

    }

    byte[] loadImageFromDB(String id) {

        byte[] bytes = null;
        try {
            dbHelper.open();
            bytes = dbHelper.retreiveImageFromDB(id);
            dbHelper.close();
        } catch (Exception e) {
            Log.e(TAG, "<loadImageFromDB> Error : " + e.getLocalizedMessage());
            dbHelper.close();
        }

        return bytes;
    }


    @Override
    public void setSnackbarMessage(String status, boolean showBar) {
        internetStatus = "";
        if (status.equalsIgnoreCase("Wifi enabled") || status.equalsIgnoreCase("Mobile data enabled")) {
            internetStatus = "Internet Connected";
        } else {
            internetStatus = "Lost Internet Connection";
        }
        snackbar = Snackbar
                .make(layout, internetStatus, Snackbar.LENGTH_LONG)
                .setAction("X", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.WHITE);
        // Changing action button text color
        View sbView = snackbar.getView();

        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        if (internetStatus.equalsIgnoreCase("Lost Internet Connection")) {
            if (internetConnected) {
                snackbar.show();
                internetConnected = false;
            }
        } else {
            if (!internetConnected) {
                internetConnected = true;
                snackbar.show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();


        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_search) {
            toolbar.onSearchIconClick();
            return true;
        } else if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        snackBarClass.registerInternetCheckReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(snackBarClass.broadcastReceiver);
    }

}