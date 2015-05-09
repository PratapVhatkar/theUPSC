package upscprelim2015.vhatkar.pratap.com.upscprelimapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

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

import java.util.ArrayList;


public class SecondActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        Tracker tracker = ((GlobalState) getApplication()).getTracker(GlobalState.TrackerName.GLOBAL_TRACKER);
        tracker.setScreenName("Second Activity");
        tracker.send(new HitBuilders.AppViewBuilder().build());


        //Get a Tracker (should auto-report)
        ((GlobalState) getApplication()).getTracker(GlobalState.TrackerName.APP_TRACKER);
        GoogleAnalytics.getInstance(this).setLocalDispatchPeriod(3);


        Intent intent = getIntent();
        final String para = intent.getStringExtra("value");

        String temp = para.replace("%20"," ");
        setTitle(temp.toUpperCase());

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url =" http://iasupscprelim2015.herokuapp.com/api/v1/books.json?category="+ para;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ProgressBar progressBar = (ProgressBar)findViewById(R.id.ctrlActivityIndicator);

                        progressBar.setVisibility(View.INVISIBLE);

                        System.out.println(response);

                        ListView listView = (ListView) findViewById(R.id.listView);
                        CustomAdapter mAdapter = new CustomAdapter(SecondActivity.this, parse(response),para,"Second");

                        listView.setAdapter(mAdapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    public ArrayList<String> parse(String jsonLine) {

        JsonElement jelement = new JsonParser().parse(jsonLine);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonArray jarray = jobject.getAsJsonArray("categories");

        ArrayList<String> itemArray = new ArrayList<String>();

        for(int i=0 ; i < jarray.size();i++){
            String temp = rectifyString(jarray.get(i).toString());
            itemArray.add(temp.toUpperCase());

        }


        return itemArray;
    }


    public String rectifyString(String url){
        String str = url.substring(url.lastIndexOf("/") + 1, url.length());
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
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



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
}
