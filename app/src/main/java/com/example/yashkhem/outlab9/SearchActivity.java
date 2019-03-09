package com.example.yashkhem.outlab9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    Context context = this;
    private ListView listView;
    private EditText key;
    private Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        key = (EditText) findViewById(R.id.UserName);
        search = (Button) findViewById(R.id.Search);
        listView = (ListView) findViewById(R.id.userList);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GitAsyncTask().execute(key.getText().toString());
            }
        });
    }




    class GitAsyncTask extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "Getting Users", "Please Wait");
        }

        @Override
        protected String doInBackground(String... keys) {
            String key = keys[0];
            String url1 = "https://api.github.com/search/users?q=";
            String url2 = "+in:login&sort=repositories";
            String result=null;
            try {

                URL url = new URL(url1+key+url2);
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
                JSONObject jsonObject = new JSONObject(s);
                JSONArray userArray = jsonObject.getJSONArray("items");
                String[] userList = new String[userArray.length()] ;
                for(int i = 0; i < userArray.length(); i++){
                    userList[i]=(userArray.getJSONObject(i).getString("login"));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context , android.R.layout.simple_list_item_1, userList);
                listView.setAdapter(adapter);
                Toast.makeText(context,"Completed Search", Toast.LENGTH_LONG).show();

            }

            catch (Exception e){
                e.printStackTrace();
                String[] userList = new String[0];
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context , android.R.layout.simple_list_item_1, userList);
                listView.setAdapter(adapter);
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected item text from ListView
                    String username = (String) parent.getItemAtPosition(position);

                    // Display the selected item text on TextView
                    Intent intent = new Intent(context, UserActivity.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                }
            });




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

}
