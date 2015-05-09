package upscprelim2015.vhatkar.pratap.com.upscprelimapplication;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static upscprelim2015.vhatkar.pratap.com.upscprelimapplication.MainActivity.AppRater.*;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Tracker tracker = ((GlobalState) getApplication()).getTracker(GlobalState.TrackerName.GLOBAL_TRACKER);
        tracker.setScreenName("Main Activity");
        tracker.send(new HitBuilders.AppViewBuilder().build());

        //Get a Tracker (should auto-report)
       	((GlobalState) getApplication()).getTracker(GlobalState.TrackerName.APP_TRACKER);
        GoogleAnalytics.getInstance(this).setLocalDispatchPeriod(3);

        setTitle("IAS Prelim 2015");

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://iasupscprelim2015.herokuapp.com/api/v1/books.json";

        AppRater.app_launched(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ProgressBar progressBar = (ProgressBar)findViewById(R.id.ctrlActivityIndicator);

                        progressBar.setVisibility(View.INVISIBLE);

                        System.out.println(response);

                        ListView listView = (ListView) findViewById(R.id.listView);
                        CustomAdapter mAdapter = new CustomAdapter(MainActivity.this, parse(response),"","Main");

                        listView.setAdapter(mAdapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);

      Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                notifyuser();
            }

        }, 1000*60, 1000*60*60*12);

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void notifyuser()
    {

        int YOUR_PI_REQ_CODE = 42;
        int YOUR_NOTIF_ID = 56;
        String str = getRemainingDays() + " days left for UPSC Prelim 2015";

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                YOUR_PI_REQ_CODE, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = getApplicationContext().getResources();
        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.upscicon)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.upscicon))
                .setTicker(res.getString(R.string.your_ticker))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("UPSC Prelim 2015")
                .setContentText(str);
        Notification n = builder.build();

        nm.notify(YOUR_NOTIF_ID, n);
    }


    public ArrayList<String> parse(String jsonLine) {

        JsonElement jelement = new JsonParser().parse(jsonLine);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonArray jarray = jobject.getAsJsonArray("categories");

        ArrayList<String> itemArray = new ArrayList<String>();

        for(int i=0 ; i < jarray.size();i++){
            String temp = rectifyString(jarray.get(i).toString());
            temp = toCamelCase(temp);
            itemArray.add(temp.toUpperCase());

        }

        return itemArray;
    }


    public String rectifyString(String url){
     String str = url.substring(url.lastIndexOf("//") + 2, url.length());
      return removelastCharmethod(str);
    }


    public String removelastCharmethod(String str) {
        if (str.length() > 0 && str.charAt(str.length()-1)=='"') {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public static String toCamelCase(final String init) {
        if (init==null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length()==init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }


    public String getRemainingDays()
    {
        long diffDays = 0;
        //diff between these 2 dates should be 1
        try {

            String PrvvDate= new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            String CurrDate=  "24/08/2015";
            Date date1 = null;
            Date date2 = null;
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            date1 = df.parse(CurrDate);
            date2 = df.parse(PrvvDate);
            long diff = Math.abs(date1.getTime() - date2.getTime());
            diffDays = diff / (24 * 60 * 60 * 1000);


            System.out.println(diffDays);

        } catch (Exception e1) {
            System.out.println("exception " + e1);
        }

        return  Long.toString(diffDays);
    }



    public static class AppRater {
        private final static String APP_TITLE = "UPSC Prelim 2015";// App Name
        private final static String APP_PNAME = "com.pratap.vhatkar.upscprelim2015";// Package Name

        private final static int DAYS_UNTIL_PROMPT = 2;//Min number of days
        private final static int LAUNCHES_UNTIL_PROMPT = 5;//Min number of launches

        public static void app_launched(Context mContext) {
            SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
            if (prefs.getBoolean("dontshowagain", false)) { return ; }

            SharedPreferences.Editor editor = prefs.edit();

            // Increment launch counter
            long launch_count = prefs.getLong("launch_count", 0) + 1;
            editor.putLong("launch_count", launch_count);

            // Get date of first launch
            Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
            if (date_firstLaunch == 0) {
                date_firstLaunch = System.currentTimeMillis();
                editor.putLong("date_firstlaunch", date_firstLaunch);
            }

            // Wait at least n days before opening
            if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
                if (System.currentTimeMillis() >= date_firstLaunch +
                        (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                    showRateDialog(mContext, editor);
                }
            }

            editor.commit();
        }

        public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
            final Dialog dialog = new Dialog(mContext);
            dialog.setTitle("Rate " + APP_TITLE);

            LinearLayout ll = new LinearLayout(mContext);
            ll.setOrientation(LinearLayout.VERTICAL);

            TextView tv = new TextView(mContext);
            tv.setText("If you enjoy using " + APP_TITLE + ", please take a moment to rate it. Thanks for your support!");
            tv.setWidth(240);
            tv.setPadding(4, 0, 4, 10);
            ll.addView(tv);

            Button b1 = new Button(mContext);
            b1.setText("Rate " + APP_TITLE);
            b1.setOnClickListener(new  View.OnClickListener() {
                public void onClick(View v) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                    dialog.dismiss();
                }
            });
            ll.addView(b1);

            Button b2 = new Button(mContext);
            b2.setText("Remind me later");
            b2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            ll.addView(b2);

            Button b3 = new Button(mContext);
            b3.setText("No, thanks");
            b3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (editor != null) {
                        editor.putBoolean("dontshowagain", true);
                        editor.commit();
                    }
                    dialog.dismiss();
                }
            });
            ll.addView(b3);

            dialog.setContentView(ll);
            dialog.show();
        }
    }


}
