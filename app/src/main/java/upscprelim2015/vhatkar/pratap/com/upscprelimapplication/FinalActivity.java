package upscprelim2015.vhatkar.pratap.com.upscprelimapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Random;


public class FinalActivity extends ActionBarActivity {

    ProgressBar pb;
    Dialog dialog;
    int downloadedSize = 0;
    int totalSize = 0;
    String para;
    float per;
    String titleString;
    //intersteller ads
    InterstitialAd mInterstitialAd;


    ArrayList<String> itemArray = new ArrayList<String>();
    ArrayList<String> rawArray = new ArrayList<String>();

    String MainFolderName ;
    String SubFolderName ;
    String ArticleName ;

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    public ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);


        //banner ads
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //intersteller ads

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7583084659008726/6654498811");
        requestNewInterstitial();

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });


        Tracker tracker = ((GlobalState) getApplication()).getTracker(GlobalState.TrackerName.GLOBAL_TRACKER);
        tracker.setScreenName("Download Activity");
        tracker.send(new HitBuilders.AppViewBuilder().build());
        GoogleAnalytics.getInstance(this).setLocalDispatchPeriod(3);


        //Get a Tracker (should auto-report)
        ((GlobalState) getApplication()).getTracker(GlobalState.TrackerName.APP_TRACKER);


        Intent intent = getIntent();
        para = intent.getStringExtra("value");

         titleString = para.replace("%20", " ");
        setTitle(titleString.toUpperCase());

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = " http://iasupscprelim2015.herokuapp.com/api/v1/books.json?category=" + para;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.ctrlActivityIndicator);

                        progressBar.setVisibility(View.INVISIBLE);

                        ListView listView = (ListView) findViewById(R.id.listView);
                        CustomAdapter mAdapter = new CustomAdapter(FinalActivity.this, parse(response), para, "Final");

                        listView.setAdapter(mAdapter);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);





    }

    public ArrayList<String> parse(String jsonLine) {

        JsonElement jelement = new JsonParser().parse(jsonLine);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonArray jarray = jobject.getAsJsonArray("books");


        for (int i = 0; i < jarray.size(); i++) {
            String temp = rectifyString(jarray.get(i).toString());
            // temp = temp.replace("_"," ");
            itemArray.add(temp.toUpperCase());
            rawArray.add(jarray.get(i).toString());
        }
        return itemArray;
    }


    public String rectifyString(String url) {
        url = removelastCharmethod(url);
        String str = url.substring(url.lastIndexOf("/") + 1, url.length());
        str = removelastExtension(str);
        return removelastCharmethod(str);
    }


    public String removelastCharmethod(String str) {
        if (str.length() > 0 && str.charAt(str.length() - 1) == '"') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public String removelastExtension(String url) {
        return url.substring(0, url.lastIndexOf('.'));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_final, menu);
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



    public void CheckDownload(String tempstr, final int position)
    {

    //    mInterstitialAd.show();

        tempstr = null;
        tempstr = rawArray.get(position);

      //  System.out.print("File location--->"+tempstr + "\n\n");


        String[] separated = tempstr.split("/");
//        separated[0]  ; // = http
//        separated[1]; // = (nothing)
//        separated[2]; // = stackoverflow.com
//        separated[3]; // = ncert
//        separated[4]; // = bookname
//        separated[5];//file name


         MainFolderName = separated[3].toString();
         SubFolderName = separated[4].toString();
         ArticleName = separated[5].toString();

      //  ArticleName = ArticleName.substring(0,ArticleName.length() - 1);

    //    System.out.print("Article Name --->" + ArticleName);



        File AppFolder = new File(Environment.getExternalStorageDirectory() + "/UPSC");

        if (isFileExist(AppFolder.getPath().toString()))
        {
            File MainFolder = new File(AppFolder.getPath() + "/" + MainFolderName);

            if (isFileExist(MainFolder.getPath().toString()))
            {

                File SubFolder = new File(MainFolder.getPath() + "/" + SubFolderName);

                if (isFileExist(SubFolder.getPath().toString()))
                {
                    File Article = new File(SubFolder.getPath() + "/" + ArticleName);

                    ArticleName.substring(0,ArticleName.length() - 1);
                    if (isDoc(Article.getPath())== true)
                    {

                        String path = Article.getPath();

                         path = path.substring(0,path.length() - 1);

                        File file = new File(path);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                    else
                    {
                        //Download
                        download(rawArray.get(position).toString(),position);


                    }

                }
                else
                {
                    SubFolder.mkdir();
                    CheckDownload(tempstr,position);

                }

            }
            else
            {
                MainFolder.mkdir();
                CheckDownload(tempstr,position);
            }

        }
        else
        {
            AppFolder.mkdir();
            CheckDownload(tempstr,position);
        }

    }

    public boolean isDoc(String path)
    {
        System.out.print("\n\nArticle path" + path);

        path = path.substring(0,path.length() - 1);

        File myFile = new File(path);
        boolean flag = myFile.isFile();
        return flag;
    }

    public boolean isFileExist(String path)
    {
        File myFile = new File(path);

        boolean flag = myFile.exists();

        return flag;
    }


    public void download(final String dwnload_file_path, final int position) {
      
        
        String dest_file_path = null;
        File savePdf = null;

        File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/UPSC" + "/" + titleString.replace('"', ' '));
        boolean success = true;
        // Do something on success]
        savePdf = new File(folder.getAbsolutePath(), "/" + ArticleName);
        dest_file_path = savePdf.toString();



        mInterstitialAd.show();

      //  DownloadTask(rawArray.get(position).replace('"', ' '), savePdf);


        final DownloadTask downloadTask = new DownloadTask(this);
        downloadTask.execute(rawArray.get(position).toString(),dest_file_path);

        dest_file_path = dest_file_path.substring(0,dest_file_path.length()-1);

        System.out.print("\n\n\n Destination file save"+dest_file_path+"\n\n\n");
        System.out.print("\n\n\nURL sendig--->"+ rawArray.get(position).toString());


        // declare the dialog as a member field of your activity
        ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(FinalActivity.this);
        mProgressDialog.setMessage("Downloading...\n"+ArticleName );
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);


    }



    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;



        public DownloadTask(Context context) {
            this.context = context;
        }




        @Override
        protected String doInBackground(String... sUrl) {


            String ss = sUrl[1];

            System.out.print("\n\n\nPratap....."+ ss);

            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {



                String str = sUrl[0].substring(1, sUrl[0].length() - 1);

                str = str.replace(" ","%20");

                URL url = new URL(str);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                String str2 = sUrl[1].substring(1, sUrl[1].length() - 1);
                output = new FileOutputStream(str2);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }



            } catch (Exception e) {
                System.out.print("\n\n\nErrorr in downloding---->" + e.toString());
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return sUrl[1];
        }



        ProgressDialog mProgressDialog;
        protected void onPreExecute() {
            super.onPreExecute();
            mInterstitialAd.show();
            mProgressDialog = new ProgressDialog(FinalActivity.this);

            String str = ArticleName.substring(0, ArticleName.length() - 1);

            mProgressDialog.setMessage("Downloading file..\n" + str);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }



        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            mProgressDialog.setProgress(Integer.parseInt(String.valueOf(progress[0])));

        }


        protected void onPostExecute(String result) {
            mInterstitialAd.show();
            mProgressDialog.dismiss();
            String str =  result.substring(0, result.length()-1);
            Toast.makeText(context,"File downloaded" + str, Toast.LENGTH_SHORT).show();

            File file = new File(str);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);

        }

      /*  */
    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
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
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading file..");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }





}


