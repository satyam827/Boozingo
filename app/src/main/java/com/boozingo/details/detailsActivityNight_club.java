package com.boozingo.details;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.boozingo.helper.LocationHelper;
import com.boozingo.helper.Permission;
import com.boozingo.R;
import com.boozingo.helper.SnackBarClass;
import com.boozingo.helper.HttpHandler;
import com.boozingo.model.detailsNight_club;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import static com.boozingo.Boozingo.*;
import static com.boozingo.helper.LocationHelper.REQUEST_CHECK_SETTINGS;
import static com.boozingo.helper.LocationHelper.status;
import static com.boozingo.helper.Permission.RequestPermissionCode;

public class detailsActivityNight_club extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    ViewPager viewPager;
    //    ImageButton back;
    String TAG = "TAG", id = "2", text, geo_location, latitudeNight_club, longitudeNight_club, image, specs, cost, _type;
    List<String> images = new ArrayList<>();
    picPagerAdapter adapter;
    LinearLayout icons;
    TextView speciality, name, type, address, timing, locator;
    ImageView dot1, dot2, dot3, dot4, dot5, dot6;
    ScrollView scroll;
    detailsNight_club details;
    ProgressDialog pDialog;

    RelativeLayout container;
    LinearLayout l1;
    ImageView im1;
    HorizontalScrollView hsv;
    RelativeLayout r1, rl_map;
    ImageView frag;
    int height, width;

    Button feedback, share;

    Permission permission;
    LocationHelper locationHelper;
    private Location mLastLocation;
    Double latitude = 0.0, longitude = 0.0;

    CoordinatorLayout layout;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.temp7);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        id = getIntent().getStringExtra("id");
        _type = getIntent().getStringExtra("type");


        // link views to objects
        init();

        // set height and width programatically
        setParams();

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(getString(R.string.app_name));
                    isShow = true;
                    l1.setVisibility(View.INVISIBLE);

                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                    l1.setVisibility(View.VISIBLE);
                }
            }
        });


        permission = new Permission(this);
        locationHelper = new LocationHelper(this);

        pDialog = new ProgressDialog(detailsActivityNight_club.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(true);
        pDialog.show();


        dot1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ring));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        dot1.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.ring));
                        dot2.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.dot));
                        break;

                    case 1:
                        dot1.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.dot));
                        dot2.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.ring));
                        dot3.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.dot));
                        break;
                    case 2:
                        dot2.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.dot));
                        dot3.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.ring));
                        //            dot4.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.dot));
                        break;
                   /* case 3:
                        dot3.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.dot));
                        dot4.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.ring));
                        dot5.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.dot));
                        break;
                    case 4:
                        dot4.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.dot));
                        dot5.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.ring));
                        dot6.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.dot));
                        break;
                    case 5:
                        dot5.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.dot));
                        dot6.setImageDrawable(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.ring));
                        break;*/
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        rl_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mLastLocation = locationHelper.getLocation();
                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                }

                String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + latitude + "," + longitude + "&daddr=" + latitudeNight_club + "," + longitudeNight_club;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(Intent.createChooser(intent, "Select an application"));

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, share_message);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /*String url = "http://www.stackoverflow.com";
                Intent i = new Intent();
                i.setPackage("com.android.chrome");
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);*/

                Toast.makeText(detailsActivityNight_club.this, "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        //to get data from net
        new net().execute();

    }


    private void init() {

        layout = findViewById(R.id.layout);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        appBarLayout = findViewById(R.id.app_bar_layout);
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.pager);

        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);
        dot4 = findViewById(R.id.dot4);
        dot5 = findViewById(R.id.dot5);
        dot6 = findViewById(R.id.dot6);

        speciality = findViewById(R.id.speciality);
        name = findViewById(R.id.name);
        type = findViewById(R.id.type);
        timing = findViewById(R.id.timings);
        address = findViewById(R.id.address);
        icons = findViewById(R.id.icons);
        container = findViewById(R.id.container);
        //  scroll = (ScrollView) findViewById(R.id.scroll);

        feedback = findViewById(R.id.feedback);
        share = findViewById(R.id.share);

        l1 = findViewById(R.id.dots);
        im1 = findViewById(R.id.booze);
        hsv = findViewById(R.id.icon_holder);
        r1 = findViewById(R.id.ll4);
        frag = findViewById(R.id.map);
        rl_map = findViewById(R.id.rl_map);
        locator = findViewById(R.id.locator);

    }

    private void setParams() {

        height = getWindowManager().getDefaultDisplay().getHeight();
        width = getWindowManager().getDefaultDisplay().getWidth();

        //     f1.getLayoutParams().height = (int) (height * 0.30);
        l1.getLayoutParams().height = (int) (height * 0.02);
        im1.getLayoutParams().height = (int) (height * 0.04);
        hsv.getLayoutParams().height = (int) (height * 0.10);
        r1.getLayoutParams().height = (int) (height * 0.30);
        frag.getLayoutParams().height = (int) (height * 0.30 * 0.87);
        locator.getLayoutParams().height = (int) (height * 0.30 * 0.12);

        l1.getLayoutParams().width = (int) (width * 0.25);
        im1.getLayoutParams().width = (int) (width * 0.30);
    }

    private class net extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(URL + "/night_club/" + id);


            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    final JSONObject object = new JSONObject(jsonStr);
                    JSONArray array = object.getJSONArray("night_club");

                    JSONObject temp = array.getJSONObject(0);
                    String userJson = temp.toString();

                    Gson gson = new Gson();

                    details = new detailsNight_club();
                    details = gson.fromJson(userJson, detailsNight_club.class);

                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {

                            name.setText(details.getNight_club_name());
                            address.setText(details.getNight_club_address());
                            type.setText("(" + getIntent().getStringExtra("type") + ")");
                            timing.setText(details.getNight_club_time());
                            geo_location = details.getNight_club_geolocation();
                            specs = details.getNight_club_details();
                            cost = details.getNight_club_cost();


                            int comma = geo_location.indexOf('-');
                            latitudeNight_club = geo_location.substring(0, comma);
                            longitudeNight_club = geo_location.substring(comma + 1);


                            String path = "http://maps.google.com/maps/api/staticmap?&zoom=19&size=600x240&markers=color:blue|" + latitudeNight_club + "," + longitudeNight_club;

                            Glide.with(detailsActivityNight_club.this)
                                    .load(path)
                                    .into(frag);


                            try {
                                image = object.getJSONArray("night_club_images").getJSONObject(0).getString("night_club_images");
                                image = image.substring(2, image.length() - 2);
                                image = image.replaceAll("\\\\", "");

                                for (int i = 0; i < image.length(); ) {
                                    int j = image.indexOf(',', i);
                                    if (j == -1) {
                                        images.add(URL + "/storage/" + image.substring(i, image.length()));
                                        break;
                                    } else
                                        images.add(URL + "/storage/" + image.substring(i, j - 1));
                                    i = j + 2;

                                }

                                //to randomise pics
                                Collections.shuffle(images);

                                //to select only 3 pics
                                images = images.subList(0, 3);


                                adapter = new picPagerAdapter(detailsActivityNight_club.this, images);
                                viewPager.setAdapter(adapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            // for speciality
                            String y;
                            for (int i = 0; i < specs.length(); ) {
                                int x = specs.indexOf('/', i);
                                if (x < specs.length() && x != -1) {
                                    y = speciality.getText() + "\u25CF " + specs.substring(i, x) + "\n";
                                    speciality.setText(y);
                                    i = x + 1;
                                } else {
                                    y = speciality.getText() + "\u25CF " + specs.substring(i, specs.length());
                                    speciality.setText(y);
                                    break;
                                }
                            }

                            // for cost of 2 person
                            y = speciality.getText() + "\n\u25CF Average cost for 2 Boozinga: \u20B9" + cost;
                            speciality.setText(y);

                            facilities();

                            pDialog.dismiss();
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
                            pDialog.dismiss();

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
                        pDialog.dismiss();

                    }
                });
            }

            return null;
        }
    }

    @SuppressWarnings("ResourceType")
    private void facilities() {
        for (int i = 0; i < 8; i++) {
            View child = View.inflate(getBaseContext(), R.layout.smallpicrow, null);
            ImageView x = child.findViewById(R.id.pic);
            TextView text = child.findViewById(R.id.text);

            switch (i) {
                case 0:
                    x.setBackground(ContextCompat.getDrawable(this, R.raw.boozingo));
                    text.setText("Boozingo");
                    icons.addView(child);
                    break;
                case 1:
                    if (details.getNight_club_food().equals("both") || details.getNight_club_food().equals("veg")) {
                        x.setBackground(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.veg));
                        text.setText("Veg");
                        icons.addView(child);
                    }
                    break;
                case 2:
                    if (details.getNight_club_food().equals("both") || details.getNight_club_food().equals("nonveg")) {
                        x.setBackground(ContextCompat.getDrawable(detailsActivityNight_club.this, R.drawable.non_veg));
                        text.setText("Non Veg");
                        icons.addView(child);
                    }
                    break;
                case 3:
                    if (details.getNight_club_sitting_facility().equals("yes")) {
                        x.setBackground(ContextCompat.getDrawable(detailsActivityNight_club.this, R.raw.table));
                        text.setText("Sitting");
                        icons.addView(child);
                    }
                    break;
                case 4:
                    if (details.getNight_club_music().equals("available")) {
                        x.setBackground(ContextCompat.getDrawable(detailsActivityNight_club.this, R.raw.music_player));
                        text.setText("Music");
                        icons.addView(child);
                    }
                    break;
                case 5:
                    if (details.getNight_club_ac().equals("ac")) {
                        x.setBackground(ContextCompat.getDrawable(detailsActivityNight_club.this, R.raw.minisplit1));
                        text.setText("Ac");
                        icons.addView(child);
                    }
                    break;
                case 6:
                    if (details.getNight_club_payment().equals("cash") || details.getNight_club_payment().equals("all")) {
                        x.setBackground(ContextCompat.getDrawable(detailsActivityNight_club.this, R.raw.notes));
                        text.setText("Cash");
                        icons.addView(child);
                    }
                    break;
                case 7:
                    if (details.getNight_club_payment().equals("credit/debit card") || details.getNight_club_payment().equals("all")) {
                        x.setBackground(ContextCompat.getDrawable(detailsActivityNight_club.this, R.raw.credit_card));
                        text.setText("Card");
                        icons.addView(child);
                    }
                    break;
            }

        }
    }


    // Permission check functions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {

            case RequestPermissionCode:
                if (grantResults.length > 0) {

                    boolean CoarseLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean FineLocaion = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (CoarseLocation && FineLocaion) {

                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                        //                  setSnackbarMessage(status, false);

                    } else {

                        // permission was not granted
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            permission.requestPermission();
                        } else {
                            Toast.makeText(this, "Permissions are necessary. Allow them from app settings.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                break;
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLastLocation = locationHelper.getLocation();
        snackBarClass.registerInternetCheckReceiver(layout);

        permission.requestPermission();

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (snackBarClass.broadcastReceiver.isOrderedBroadcast())            unregisterReceiver(snackBarClass.broadcastReceiver);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("d", "d");
                        // All required changes were successfully made
                        mLastLocation = locationHelper.getLocation();
                        break;
                    case RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        try {
                            status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;
        }
    }


    @Override
    public void onConnected(Bundle arg0) {
        mLastLocation = locationHelper.getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        locationHelper.connectApiClient();
    }

}
