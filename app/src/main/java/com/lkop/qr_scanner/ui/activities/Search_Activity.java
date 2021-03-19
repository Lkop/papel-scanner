package com.lkop.qr_scanner.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lkop.qr_scanner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Search_Activity extends AppCompatActivity {

    //vars
    String name, lastname, new_QR, class_id, subject_id;
    private static final String USER = "user";

    private TextView name_screen, lastname_screen, academic_id_screen, class_name_screen;
    private Thread fetch_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent_new = getIntent();

        new_QR = intent_new.getStringExtra("QR_code");

        new Search().execute();

        new Fetch_Class().execute();




    }

    @Override
    public void onBackPressed()
    {
        Intent intent_main = new Intent(getApplicationContext(), ActivityMainMenu.class);
        startActivity(intent_main);
        finish();
    }

    public class Search extends AsyncTask<Void, Void, String> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Search_Activity.this);
            pDialog.setMessage("Connecting ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                String urlString = "https://android.zabenia.com/apps/qr_scanner/search_user.php?id=" + new_QR  ;

                HttpURLConnection urlConnection = null;

                //creating a URL
                URL url = new URL(urlString);

                //Opening the URL using HttpURLConnection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */ );
                urlConnection.setConnectTimeout(15000 /* milliseconds */ );
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                //We will use a buffered reader to read the string from service
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

                //StringBuilder object to read the string from the service
                StringBuilder sb = new StringBuilder();

                //A simple string to read values from each line
                String line;

                //reading until we don't find null
                while ((line = br.readLine()) != null) {

                    //appending it to string builder
                    sb.append(line + "\n");
                }
                br.close();

                return sb.toString().trim();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String sb) {

            pDialog.dismiss();

            //to megethos tou kenou json einai 2

            if(sb.length()==2) // []
            {
                Toast toast = Toast.makeText(Search_Activity.this, "Failed to connect", Toast.LENGTH_LONG);
                toast.show();
                class_name_screen = (TextView)findViewById(R.id.class_name);

            } else {

                try {

                    JSONObject jobj = new JSONObject(sb);
                    JSONArray jsonArray = jobj.getJSONArray(USER);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        name = obj.getString("name");
                        lastname = obj.getString("lastname");
                    }

                    name_screen = (TextView)findViewById(R.id.name);
                    lastname_screen = (TextView)findViewById(R.id.lastname);
                    academic_id_screen = (TextView)findViewById(R.id.academic_id);


                    name_screen.setText(name);
                    lastname_screen.setText(lastname);
                    academic_id_screen.setText("blah blah blah");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    class Fetch_Class extends AsyncTask<Void, Void, String> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Search_Activity.this);
            pDialog.setMessage("Connecting ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                String urlString = "http://android.zabenia.com/apps/qr_scanner/json_get_subject.php?token=1dbf7b77a2";

                HttpURLConnection urlConnection = null;

                //creating a URL
                URL url = new URL(urlString);

                //Opening the URL using HttpURLConnection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */ );
                urlConnection.setConnectTimeout(15000 /* milliseconds */ );
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                //We will use a buffered reader to read the string from service
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

                //StringBuilder object to read the string from the service
                StringBuilder sb = new StringBuilder();

                //A simple string to read values from each line
                String line;

                //reading until we don't find null
                while ((line = br.readLine()) != null) {

                    //appending it to string builder
                    sb.append(line + "\n");
                }
                br.close();

                return sb.toString().trim();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String sb) {

            pDialog.dismiss();

            //to megethos tou kenou json einai 2

            if(sb.length()==2) // []
            {

            } else {

                try {

                    JSONObject jobj = new JSONObject(sb);
                    JSONArray jsonArray = jobj.getJSONArray("class_info");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        class_id = obj.getString("id");
                        subject_id = obj.getString("subject_id");
                    }

                    class_name_screen = (TextView)findViewById(R.id.class_name);
                    class_name_screen.setText(subject_id);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

    }
}
