package appewtc.masterung.ssrurestaurant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity {

    //Explicit
    private UserTABLE objUserTABLE;
    private FoodTABLE objFoodTABLE;
    private EditText userEditText, passwordEditText;
    private String userString, passwordString, nameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Connected Database
        connectedDatabase();

        //Tester
        //tester();

        //Delete All data
        deleteAllData();

        //Synchronize JSON to SQLite
        synchronizeJSON();



    }   // onCreate

    public void clickLogin(View view) {

        userEditText = (EditText) findViewById(R.id.editText);
        passwordEditText = (EditText) findViewById(R.id.editText2);
        userString = userEditText.getText().toString().trim();
        passwordString = passwordEditText.getText().toString().trim();

        if (userString.equals("") || passwordString.equals("") ) {

            //Have Space
            myAlertDialog("โปรดกรอกให้ครบ", "กรุณากรอก user และ password ให้ครบ นะคะ");

        } else {

            //No Space
            checkUser();

        }

    }   // clickLogin

    private void checkUser() {

        try {

            String strMySearch[] = objUserTABLE.searchUser(userString);
            nameString = strMySearch[3];
            Log.d("ssru", "Name ==> " + nameString);

            //Check Password
            checkPassword(strMySearch[2]);

        } catch (Exception e) {

            myAlertDialog("ไม่มี User", "ไม่มี " + userString + " บนฐานข้อมูลของฉัน");

        }

    }   //checkUser

    private void checkPassword(String strTruePassword) {

        if (passwordString.equals(strTruePassword)) {

            Intent objIntent = new Intent(MainActivity.this, OrderActivity.class);
            objIntent.putExtra("Officer", nameString);
            startActivity(objIntent);
            finish();

        } else {
            myAlertDialog("Password False", "Please Try Again Password False");
        }

    }   //checkPassword

    private void myAlertDialog(String strTitle, String strMessage) {

        AlertDialog.Builder objBuilder = new AlertDialog.Builder(this);
        objBuilder.setIcon(R.drawable.icon_question);
        objBuilder.setTitle(strTitle);
        objBuilder.setMessage(strMessage);
        objBuilder.setCancelable(false);
        objBuilder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        objBuilder.show();

    }   // myAlertDialog


    private void deleteAllData() {

        SQLiteDatabase deleteDatabase = openOrCreateDatabase("ssru.db", MODE_PRIVATE, null);
        deleteDatabase.delete("userTABLE", null, null);
        deleteDatabase.delete("foodTABLE", null, null);

    }   //deleteAllData

    private void synchronizeJSON() {

        //Setup Policy
        if (Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy myPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(myPolicy);

        }   // if

        InputStream objInputStream = null;
        String strJSON = "";

        //Create InputStream
        try {

            HttpClient objHttpClient = new DefaultHttpClient();
            HttpPost objHttpPost = new HttpPost("http://swiftcodingthai.com/ssru1/php_get_data_master.php");
            HttpResponse objHttpResponse = objHttpClient.execute(objHttpPost);
            HttpEntity objHttpEntity = objHttpResponse.getEntity();
            objInputStream = objHttpEntity.getContent();

        } catch (Exception e) {
            Log.d("ssru", "Create InputStream ==> " + e.toString());
        }

        //Create strJSON
        try {

            BufferedReader objBufferedReader = new BufferedReader(new InputStreamReader(objInputStream, "UTF-8"));
            StringBuilder objStringBuilder = new StringBuilder();
            String strLine = null;

            while ((strLine = objBufferedReader.readLine()) != null ) {
                objStringBuilder.append(strLine);
            }   //while

            objInputStream.close();
            strJSON = objStringBuilder.toString();


        } catch (Exception e) {
            Log.d("ssru", "Create strJSON ==> " + e.toString());
        }

        //Update Value to SQLite
        try {

            final JSONArray objJsonArray = new JSONArray(strJSON);
            for (int i = 0; i < objJsonArray.length(); i++) {

                JSONObject jsonObject = objJsonArray.getJSONObject(i);
                String strUser = jsonObject.getString("User");
                String strPassword = jsonObject.getString("Password");
                String strName = jsonObject.getString("Name");
                objUserTABLE.addNewValue(strUser, strPassword, strName);

            }   // for

        } catch (Exception e) {
            Log.d("ssru", "Update SQlite ==> " + e.toString());
        }


    }   // synJSON

    private void tester() {
        objUserTABLE.addNewValue("testUser", "testPass", "testName");
        objFoodTABLE.addFood("ข้าวหมูแดง", "50");
    }

    private void connectedDatabase() {
        objUserTABLE = new UserTABLE(this);
        objFoodTABLE = new FoodTABLE(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}   // Main Class
