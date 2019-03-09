package com.example.yashkhem.outlab9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;

import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.Interval;
import org.joda.time.Months;
import org.joda.time.Years;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class UserActivity extends AppCompatActivity {
    TextView name;
    TextView company;
    TextView location;
    ListView listView;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        name = (TextView) findViewById(R.id.name);
        company = (TextView) findViewById(R.id.company);
        location = (TextView) findViewById(R.id.location);
        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username");
        new UserAsyncTask().execute(username);
        new RepoAsyncTask().execute(username);

    }

    class UserAsyncTask extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... keys) {
            String user = keys[0];
            String userInfo = "https://api.github.com/users/"+user;
            String result=null;
            try {

                URL url = new URL(userInfo);
                /* Open a connection to that URL. */
                // final HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                /* Define InputStreams to read from the URLConnection. */
                //InputStream in1 = new BufferedInputStream(conn1.getInputStream());
                InputStream in = new BufferedInputStream(conn.getInputStream());
                //result1 = inputStreamToString(in1);
                result= inputStreamToString(in);
            }

            catch(Exception e){ e.printStackTrace();}

            return result;

        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            try{
                JSONObject jsonObject = new JSONObject(s);
                name.setText(jsonObject.getString("name"));
                company.setText(jsonObject.getString("company"));
                location.setText(jsonObject.getString("location"));

            }

            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class RepoAsyncTask extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "Getting Repos", "Please Wait");
        }

        @Override
        protected String doInBackground(String... keys) {
            String user = keys[0];
           // System.out.println(user);
            String userInfo = "https://api.github.com/users/"+user;
            String repoInfo = userInfo+"/repos";
            //System.out.println(repoInfo);
            String result=null;
            try {
                URL url = new URL(repoInfo);
                /* Open a connection to that URL. */
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                /* Define InputStreams to read from the URLConnection. */
                InputStream in = new BufferedInputStream(conn.getInputStream());
                result = inputStreamToString(in);
            }

            catch(Exception e){ e.printStackTrace();}

            return result;

        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            progressDialog.dismiss();

            try{
                JSONArray repoArray = new JSONArray(s);
              //  System.out.println(repoArray.length());
                ArrayList<Repos> repoList = new ArrayList<Repos>();
                for(int i = 0; i < repoArray.length(); i++){
                    String name = repoArray.getJSONObject(i).getString("name");
                    String description = repoArray.getJSONObject(i).getString("description");
                    //String age = "pata nahi"; //Make changes here
                    String created_at = repoArray.getJSONObject(i).getString("created_at").split("T")[0];

                    String today_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                    Date date  = (Date)sdf.parse(created_at);
                    Date now = new Date();
                    long c_ts=date.getTime()/1000L;
                    long t_ts=now.getTime()/1000L;
                    long diff=t_ts-c_ts;
                    long days= diff/(60*60*24);
                    long years=days/365;
                    long months=((days%365)/30);

                    days = days % 30;
                    if(months==12){
                        months=0;
                        days=1;
                        years++;
                    }


//                    DateTime dt1=new DateTime(date);
//                    DateTime dt2=new DateTime(now);
//                    Interval interval = new Interval(dt1,dt2);
//                   // Interval interval1 = new Interval(new DateTime((Date)sdf.parse("2017-10-01")));
//                    //Log.v("test", interval1.toPeriod().getYears() + " " + interval1.toPeriod().getMonths() + " " + interval1.toPeriod().getDays());
//                    int years = interval.toPeriod().getYears();
//                    int months = interval.toPeriod().getMonths();
//                    int days = interval.toPeriod().getDays();

//                    int years = Years.yearsBetween(dt1,dt2).getYears();
//                    int months = Months.monthsBetween(dt1,dt2).getMonths()%12;
//                    int days = (Days.daysBetween(dt1,dt2).getDays()%30) ;
                    String syear,smonth,sday;
                    if(years > 9)  syear = Long.toString(years);
                    else syear = "0"+Long.toString(years);
                    if(months > 9)  smonth = Long.toString(months);
                    else smonth = "0"+Long.toString(months);
                    if(days > 9)  sday = Long.toString(days);
                    else sday = "0"+Long.toString(days);
                    String age = syear + " Years, " + smonth + " Months, "+ sday + " Days";

                    Repos rp = new Repos(name,age,description);
                    repoList.add(rp);

                }

               // System.out.println(repoList.get(1).getName());
                listView = (ListView) findViewById(R.id.repos);
                RepoAdapter adapter = new RepoAdapter(context, repoList);
                listView.setAdapter(adapter);

            }

            catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    private String inputStreamToString(InputStream is) {
        String rLine = "";
        StringBuilder answer = new StringBuilder();

        InputStreamReader isr = new InputStreamReader(is);

        BufferedReader rd = new BufferedReader(isr);

        try {
            while ((rLine = rd.readLine()) != null) {
                answer.append(rLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return answer.toString();
    }

}
