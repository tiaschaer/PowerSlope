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
import java.util.Arrays;

public class UserProfilesManagement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profiles_management);

        Spinner userProfileSpinner = (Spinner) findViewById(R.id.spi_userProfiles);
        String[] userProfiles = this.getUserNames();
        ArrayAdapter<String> userNamesArrayAdapter = new ArrayAdapter<String>(UserProfilesManagement.this,
                android.R.layout.simple_spinner_item, userProfiles);
        userNamesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userProfileSpinner.setAdapter(userNamesArrayAdapter);
        userProfileSpinner.setSelection(Arrays.asList(userProfiles).indexOf(this.getLastUserName()));
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

    protected JSONObject readUserProfilesJson() {
        try {
            InputStream inputStream = openFileInput(getString(R.string.filename_userProfiles));
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, "UTF-8");
            try {
                return new JSONObject(jsonString);
            } catch (JSONException ex) {
                ex.printStackTrace();
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected JSONArray getUserProfiles() {
        try {
            return this.readUserProfilesJson().getJSONArray(getString(R.string.json_UserProfiles));
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected String[] getUserNames() {
        JSONArray userProfiles = this.getUserProfiles();
        try {
            String[] userNames = new String[userProfiles.length()];
            for (int i=0; i<userProfiles.length(); i++) {
                userNames[i] = userProfiles.getJSONObject(i).getString("name");
            }
            return userNames;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected String getLastUserName() {
        try {
            return this.readUserProfilesJson().getString(getString(R.string.json_LastUserName));
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
