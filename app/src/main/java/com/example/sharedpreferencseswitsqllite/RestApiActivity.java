package com.example.sharedpreferencseswitsqllite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.sharedpreferencseswitsqllite.databinding.ActivityRestApiBinding;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class RestApiActivity extends AppCompatActivity  implements View.OnClickListener {
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private ActivityRestApiBinding binding;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestApiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.fetchButton.setOnClickListener(this);

        //action bar
        dl = (DrawerLayout) findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();

        //action Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nav_view = (NavigationView)findViewById(R.id.nav_view);
        //shared preferences
        preferences = getSharedPreferences("AndroidHiveLogin", 0);
        editor = preferences.edit();
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_message){
                    Intent a = new Intent(RestApiActivity.this,
                            ProfileActivity.class);
                    startActivity(a);
                }else if (id == R.id.nav_chat){
                    Intent a = new Intent(RestApiActivity.this,
                            Alarm_Manager.class);
                    startActivity(a);
                }
                else if (id == R.id.nav_sql){
                    Intent a = new Intent(RestApiActivity.this,
                            DatabaseActivity.class);
                    startActivity(a);
                }else if (id == R.id.nav_api){
                    Intent a = new Intent(RestApiActivity.this,
                            RestApiActivity.class);
                    startActivity(a);
                }else if (id == R.id.nav_logout){
                    editor.clear();
                    editor.commit();
                    Intent intent = new Intent(RestApiActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    //action Bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fetch_button) {
            index = binding.inputId.getText().toString();
            try {
                getData();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    //get data using api link
    public void getData() throws MalformedURLException {
        Uri uri = Uri.parse("https://4ec3-101-128-83-56.ap.ngrok.io/api/products")
                .buildUpon().build();
        URL url = new URL(uri.toString());
        new DOTask().execute(url);
    }

    class DOTask extends AsyncTask<URL, Void, String> {
        //connection request
        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            String data = null;
            try {
                data = NetworkUtils.makeHTTPRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                parseJson(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //get data json
        public void parseJson(String data) throws JSONException {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject innerObj = jsonObject.getJSONObject("data");
            JSONArray cityArray = innerObj.getJSONArray("data");

            for (int i = 0; i < cityArray.length(); i++) {
                JSONObject obj = cityArray.getJSONObject(i);
                String Sobj = obj.get("id").toString();
                if (Sobj.equals(index)) {
                    String id = obj.get("id").toString();
                    binding.resultId.setText(id);
                    String created_at = obj.get("created_at").toString();
                    binding.resultCreated.setText(created_at);
                    String updated_at = obj.get("updated_at").toString();
                    binding.resultUpdated.setText(updated_at);
                    String name = obj.get("name").toString();
                    binding.resultName.setText(name);
                    String description = obj.get("description").toString();
                    binding.resultDescription.setText(description);
                    String qty = obj.get("qty").toString();
                    binding.resultQty.setText(qty);
                    String price = obj.get("price").toString();
                    binding.resultPrice.setText(price);
                    String image = obj.get("image").toString();
                    binding.resultImage.setText(image);
                    String rating = obj.get("rating").toString();
                    binding.resultRating.setText(rating);
                    break;
                } else {
                    binding.resultName.setText("Not Found");
                }
            }
        }
    }
}