package com.example.owlslubic.networkinglab;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView text1;
    Button mCereal, mTea, mChocolate;
    ListView mListView;
    ArrayList<String> apiResults;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listview);
        mCereal = (Button) findViewById(R.id.button_cereal);
        mTea = (Button) findViewById(R.id.button_tea);
        mChocolate = (Button) findViewById(R.id.button_chocolate);
        text1 = (TextView) findViewById(android.R.id.text1);


        apiResults = new ArrayList<>();





        ConnectivityManager conMng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMng.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            mCereal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    apiResults.clear();
                    new ApiCallTask().execute("http://api.walmartlabs.com/v1/search?query=cereal&format=json&apiKey=shyq2qvppk5qj9h8na7476yn");
                    Log.v("API", "cereal api call went thru");
                }
            });
            mTea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    apiResults.clear();
                    new ApiCallTask().execute("http://api.walmartlabs.com/v1/search?query=tea&format=json&apiKey=shyq2qvppk5qj9h8na7476yn");
                    Log.v("API", "tea api call went thru");
                }
            });
            mChocolate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    apiResults.clear();
                    new ApiCallTask().execute("http://api.walmartlabs.com/v1/search?query=chocolate&format=json&apiKey=shyq2qvppk5qj9h8na7476yn");
                    Log.v("API", "chocolate api call went thru");
                }
            });

        } else {
            Toast.makeText(MainActivity.this, "No network connection available!", Toast.LENGTH_SHORT).show();
        }

    }

    //GET request
    public void performGetRequest(String myUrl) throws IOException, JSONException {
        InputStream is = null;
        try {
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            is = conn.getInputStream();
            String contentAsString = readIt(is);
            parseJson(contentAsString);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private void parseJson(String contentAsString) throws JSONException {
        JSONObject root = new JSONObject(contentAsString);
        JSONArray array = root.getJSONArray("items");
        for (int i = 0; i < array.length(); i++) {
            JSONObject product = array.getJSONObject(i);//have the object, get the name and add to list
            apiResults.add(product.getString("name"));
        }
    }

    private String readIt(InputStream is) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String read;
        while ((read = reader.readLine()) != null) {
            builder.append(read);
        }
        return builder.toString();
    }

    public class ApiCallTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            try {
                performGetRequest(strings[0]);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return "Unable to retrieve web page. URL may be invalid";
            } catch (JSONException e) {
                e.printStackTrace();
                return "JSON Parsing Issue";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, apiResults);
            mListView.setAdapter(adapter);
        }

    }

}
