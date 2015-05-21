package appewtc.masterung.ssrurestaurant;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

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


public class OrderActivity extends ListActivity{

    private FoodTABLE objFoodTABLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_order);

        objFoodTABLE = new FoodTABLE(this);

        //Syn JSON to SQLite
        synJSONtoSQLite();

        //Create ListView
        createListView();

    }   // onCreate

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor objCursor = (Cursor) l.getItemAtPosition(position);
        String strFood = objCursor.getString(objCursor.getColumnIndex(FoodTABLE.COLUMN_FOOD));
        String strPrice = objCursor.getString(objCursor.getColumnIndex(FoodTABLE.COLUMN_PRICE));

        Log.d("ssru", strFood + " ราคา " + strPrice);


    }   //onListItemClick

    private void createListView() {

        Cursor objCursor = objFoodTABLE.listAllData();
        String[] from = new String[]{FoodTABLE.COLUMN_FOOD};
        int[] target = new int[]{R.id.txtShowFood};
        SimpleCursorAdapter objAdapter = new SimpleCursorAdapter(this,
                R.layout.activity_order, objCursor, from, target);
        setListAdapter(objAdapter);

    }   //createListView

    private void synJSONtoSQLite() {

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
            HttpPost objHttpPost = new HttpPost("http://swiftcodingthai.com/ssru1/php_get_data_food.php");
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
                String strFood = jsonObject.getString("Food");
                String strPrice = jsonObject.getString("Price");

                objFoodTABLE.addFood(strFood, strPrice);

            }   // for

        } catch (Exception e) {
            Log.d("ssru", "Update SQlite ==> " + e.toString());
        }



    }   // synJSON

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order, menu);
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
