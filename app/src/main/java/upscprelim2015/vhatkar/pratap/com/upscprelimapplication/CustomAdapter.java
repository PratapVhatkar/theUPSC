package upscprelim2015.vhatkar.pratap.com.upscprelimapplication;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by pratap on 30/4/15.
 */
public class CustomAdapter extends BaseAdapter {

    private ArrayList <String> mainList;

    private Context appContext;

    public String para;

    public String className;

    public CustomAdapter(Context applicationContext,ArrayList<String> itemList,String para,String className) {
        super();
        this.mainList = itemList;
        this.appContext = applicationContext;
        this.para = para;
        this.className = className;
    }

    public CustomAdapter() {
        super();
        this.mainList = mainList;
        this.para = para;
        this.className = className;
    }

    @Override
    public int getCount() {
        return mainList.size();
    }

    @Override
    public Object getItem(int position) {
        return mainList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater =  (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.customcell, null);
        }

        TextView tv1 = (TextView) convertView
                .findViewById(R.id.textview);

        try {

            tv1.setText(mainList.get(position));
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    if(className == "Main"){

                        Intent intent = new Intent(appContext, SecondActivity.class);
                        String str = (String) getItem(position);

                        String query = null;
                        query = str.replace(" ", "%20");
                        intent.putExtra("value",query);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        appContext.startActivity(intent);

                    }else if (className == "Second"){

                        Intent intent = new Intent(appContext, FinalActivity.class);
                        String str = (String) getItem(position);
                        str = para + "/" + str;
                        String query = null;
                        query = str.replace(" ", "%20");
                        intent.putExtra("value",query);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        appContext.startActivity(intent);
                    }else {
                        if (className == "Final") {

                            //Build url
                            Intent intent = new Intent(appContext, FinalActivity.class);
                            String str = (String) getItem(position);
                            str = para + "/" + str ;
                            String query = null;
                           query = str.replace(" ", "%20");
                            String url = "http://iasupscprelim2015.herokuapp.com/" + query + ".pdf";

                            Log.i(url,url);
                            //download file
                           // ((FinalActivity) appContext).CheckDownload("http://upsc.herokuapp.com/syllabus/mains/Agriculture.pdf",position);


                            ((FinalActivity) appContext).CheckDownload("str",position);
                        }
                    }

                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }


}
