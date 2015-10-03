package com.example.tia.powerslope;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class UserProfilesManagement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profiles_management);

        Spinner userProfileSpinner = (Spinner) findViewById(R.id.spi_userProfiles);
        ArrayAdapter<String> userNamesArrayAdapter = new ArrayAdapter<String>(UserProfilesManagement.this,
                android.R.layout.simple_spinner_item, this.getUserNames());
        userNamesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userProfileSpinner.setAdapter(userNamesArrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profiles_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected JSONArray readUserProfilesFromJson() {
        try {
            InputStream inputStream = openFileInput(getString(R.string.filename_userProfiles));
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, "UTF-8");
            try {
                JSONObject userProfilesObject = new JSONObject(jsonString);
                return userProfilesObject.getJSONArray("UserProfiles");
            } catch (JSONException ex) {
                ex.printStackTrace();
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected String[] getUserNames() {
        JSONArray userProfiles = this.readUserProfilesFromJson();
        try {
            String[] userNames = new String[userProfiles.length()];
            for (int i=0; i<userProfiles.length(); i++) {
                JSONObject tmpUserProfileJson = userProfiles.getJSONObject(i);
                userNames[i] = tmpUserProfileJson.getString("name");
            }
            return userNames;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
