package com.example.hennessee.menutest;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.hennessee.menutest.dummy.CategoryFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

//import android.support.v4.app.Fragment;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, scanFragment.OnFragmentInteractionListener, SpecialsFragment.OnFragmentInteractionListener, DessertsFragment.OnFragmentInteractionListener, AppetizersFragment.OnFragmentInteractionListener, CategoryFragment.OnFragmentInteractionListener, ViewItemFragment.OnFragmentInteractionListener,
        OrderMenu.OnFragmentInteractionListener
{

    public static User mUser;
    public static boolean scanned = false;

    public String scannedMessage=""; // Redundant, if we have it in an array below?
    public String[] elements; //Public array of QR. [0] is ID, [1] is table, etc.
    public String url_="";

    public String masterURL="http://198.84.187.102/MenuGUI/Parse.php?ID="; // Master IP, obviously this won't change on release
    public void onFragmentInteraction(Uri uri) {

    }

    public void onFragmentInteraction(String s) {

    }

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mUser = new User();

    }

    //Result of the QR scan
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {


                String contents = intent.getStringExtra("SCAN_RESULT"); // This will contain your scan result
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");




                elements = contents.split(" "); //ID table... URL?

                Toast.makeText(MainActivity.this, elements[2],
                        Toast.LENGTH_SHORT).show();
                scanned = true;
                Log.e("App", "Returned from QR");

                mNavigationDrawerFragment.SetList();

                Fragment frag = new SpecialsFragment();
                FragmentManager fragmentManager = getFragmentManager();

                scannedMessage=contents;
                //new Thread(new SignIn(scannedMessage)).start();
                //new MyAsyncTask().execute();

                //Instead of signing in or retriving menu, first retrieve the store IP
                new getIP().execute(); // Getting IP to connect to.


            }
        }

    }

    //NOT in use. Remove this function?
    public void connectToDB() {
        String result = null;
        InputStream is = null;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://198.84.187.102/MenuGUI/MenuJSN.php");
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

            Log.e("log_tag", "connection success ");
            Toast.makeText(getApplicationContext(), "pass", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
            Toast.makeText(getApplicationContext(), "Connection fail", Toast.LENGTH_SHORT).show();

        }
        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();

            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }


        try {

            JSONObject json_data = new JSONObject(result);

            CharSequence w = (CharSequence) json_data.get("re");

            Toast.makeText(getApplicationContext(), w, Toast.LENGTH_SHORT).show();


        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
            Toast.makeText(getApplicationContext(), "JsonArray fail", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = new SpecialsFragment();

        if (!scanned && position != 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("You must scan the QR code at your table first!")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        if (!scanned)
            fragment = new scanFragment();
        else if(position == 0) {
            if(mUser.mOrder.OrderItems.size() < 1)
                fragment = new OrderMenu();
            else
                fragment = new OrderMenu(mUser.mOrder.OrderItems);

        }
        else
            fragment = mNavigationDrawerFragment.DrawerFragments.get(position - 1);

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        if (!scanned) {
            switch (number) {
                case 1:
                    mTitle = /*getString(R.string.title_section1)*/ "Scan";
                    break;
                case 2:
                    mTitle = /*getString(R.string.title_section2)*/"Specials";
                    break;
                case 3:
                    mTitle = /*getString(R.string.title_section3)*/ "Appetizers";
                    break;
                case 4:
                    mTitle = /*getString(R.string.title_section3)*/ "Desserts";
                    break;
            }
        } else {
            switch (number) {
                case 1:
                    mTitle = /*getString(R.string.title_section1)*/ "Specials";
                    break;
                case 2:
                    mTitle = /*getString(R.string.title_section2)*/"Appetizers";
                    break;
                case 3:
                    mTitle = /*getString(R.string.title_section3)*/ "Desserts";
                    break;
            }
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Retrieves menu
    class MyAsyncTask extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            progressDialog.setMessage("Please wait, reading database");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    MyAsyncTask.this.cancel(true);
                }
            });
        }

        @Override
        protected Void doInBackground(String... params) {
            //Get client's server, make a call to master.
           // String url_select = "http://198.84.187.102/MenuGUI/Parse.php?ID="+elements[0];// MASTER IP will be here.
           //String url_select = "http://198.84.187.102/MenuGUI/MenuJSN.php"; // MASTER IP will be here.
           // String url_select = elements[2]+"/MenuGUI/MenuJSN.php";
            Log.e("App", "Trying to read database..." +url_);
            String url_select = url_+"/MenuGUI/MenuJSN.php";


            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

            try {
                // Set up HTTP post
                // HttpClient is more then less deprecated. Need to change to URLConnection
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(url_select);
                httpPost.setEntity(new UrlEncodedFormEntity(param));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();

                // Read content & Log
                inputStream = httpEntity.getContent();
            } catch (UnsupportedEncodingException e1) {
                Log.e("UnsupportedEncodingException", e1.toString());
                e1.printStackTrace();
            } catch (ClientProtocolException e2) {
                Log.e("ClientProtocolException", e2.toString());
                e2.printStackTrace();
            } catch (IllegalStateException e3) {
                Log.e("IllegalStateException", e3.toString());
                e3.printStackTrace();
            } catch (IOException e4) {
                Log.e("IOException", e4.toString());
                e4.printStackTrace();
            }
            // Convert response to string using String Builder
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                result = sBuilder.toString();

            } catch (Exception e) {
                Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
            }
            Log.w("DATABASE", result);
            com.example.hennessee.menutest.MenuItem item = new com.example.hennessee.menutest.MenuItem();
            // JsonParser parser = Json.createParser(new StringReader("[]"));

            //TODO string parsing, either normal string building, but there might be JSON specific tools to make it easier.
            //http://docs.oracle.com/javaee/7/api/javax/json/stream/JsonParser.html
            //http://stackoverflow.com/questions/2591098/how-to-parse-json-in-java
            //
            //item.name=result.
            progressDialog.dismiss();


            return null;
        } // protected Void doInBackground(String... params)


        protected void onPostExecute(Void v) {

            //parse JSON data
            try {
                JSONArray jArray = new JSONArray(result);
                List<String> categories = new ArrayList<String>();
                categories.add("Order Menu");
                List<com.example.hennessee.menutest.MenuItem> MenuItems = new ArrayList<com.example.hennessee.menutest.MenuItem>();


                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject jObject = jArray.getJSONObject(i);

                    com.example.hennessee.menutest.MenuItem mi = new com.example.hennessee.menutest.MenuItem();

                    mi.name = jObject.getString("Name");
                    mi.category = jObject.getString("Category");
                    mi.comment = jObject.getString("Comments");
                    mi.price = jObject.getString("Price");
                    mi.ImageURL = jObject.getString("ImageURL");

                    MenuItems.add(mi);

                    String category = jObject.getString("Category");


                    if (!categories.contains(category)) {
                        categories.add(category);
                    }
                    //String tab1_text = jObject.getString("tab1_text");
                    //int active = jObject.getInt("active");


                    //Log.e("log_text", name);

                } // End for Loop

                String[] arr = categories.toArray(new String[categories.size()]);
                mNavigationDrawerFragment.SetList(arr, MenuItems);
                //mNavigationDrawerFragment.dynamicSelectItem(0);

                this.progressDialog.dismiss();



            } catch (JSONException e) {

                Log.e("JSONException", "Error: " + e.toString());

            } // catch (JSONException e)


        } // protected void onPostExecute(Void v)

    } //class MyAsyncTask extends AsyncTask<String, String, Void>



    //Retrives IP associated with scanned store ID
    class getIP extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            progressDialog.setMessage("Please wait");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    getIP.this.cancel(true);
                }
            });
        }

        @Override
        protected Void doInBackground(String... params) {
            //Get client's server, make a call to master.
            String url_select = ""+masterURL +elements[0];// MASTER IP will be here.
//            String url_select = "http://198.84.187.102/MenuGUI/MenuJSN.php"; // MASTER IP will be here.
            // String url_select = elements[2]+"/MenuGUI/MenuJSN.php";



            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

            try {
                // Set up HTTP post
                // HttpClient is more then less deprecated. Need to change to URLConnection
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(url_select);
                httpPost.setEntity(new UrlEncodedFormEntity(param));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();

                // Read content & Log
                inputStream = httpEntity.getContent();
            } catch (UnsupportedEncodingException e1) {
                Log.e("UnsupportedEncodingException", e1.toString());
                e1.printStackTrace();
            } catch (ClientProtocolException e2) {
                Log.e("ClientProtocolException", e2.toString());
                e2.printStackTrace();
            } catch (IllegalStateException e3) {
                Log.e("IllegalStateException", e3.toString());
                e3.printStackTrace();
            } catch (IOException e4) {
                Log.e("IOException", e4.toString());
                e4.printStackTrace();
            }
            // Convert response to string using String Builder
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                result = sBuilder.toString();

            } catch (Exception e) {
                Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
            }
            Log.w("DATABASE", result);
            com.example.hennessee.menutest.MenuItem item = new com.example.hennessee.menutest.MenuItem();
            // JsonParser parser = Json.createParser(new StringReader("[]"));

            //TODO string parsing, either normal string building, but there might be JSON specific tools to make it easier.
            //http://docs.oracle.com/javaee/7/api/javax/json/stream/JsonParser.html
            //http://stackoverflow.com/questions/2591098/how-to-parse-json-in-java
            //
            //item.name=result.
            progressDialog.dismiss();


            return null;
        } // protected Void doInBackground(String... params)


        protected void onPostExecute(Void v) {

            //parse JSON data
            try {
                JSONArray jArray = new JSONArray(result);
                List<String> categories = new ArrayList<String>();
                List<com.example.hennessee.menutest.MenuItem> MenuItems = new ArrayList<com.example.hennessee.menutest.MenuItem>();


                // url_="";
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject jObject = jArray.getJSONObject(i);


                     url_=jObject.getString("URL");
                            //new Thread(new SignIn(scannedMessage)).start();
                   // Log.e("URL",jObject.getString("URL"));
                    Log.e("App", "Received URL from Master.");


                } // End for Loop

                this.progressDialog.dismiss();
                new Thread(new SignIn(url_)).start();
                new MyAsyncTask().execute();
                //String[] arr = categories.toArray(new String[categories.size()]);
               // mNavigationDrawerFragment.SetList(arr, MenuItems);
                //mNavigationDrawerFragment.dynamicSelectItem(0);





            } catch (JSONException e) {

                Log.e("JSONException", "Error: " + e.toString());

            } // catch (JSONException e)


        } // protected void onPostExecute(Void v)

    } //class MyAsyncTask extends AsyncTask<String, String, Void>

    //Sign into store table.
    private class SignIn implements Runnable {
        private String mMsg;

        public SignIn(String msg) {
            mMsg = msg;
        }

        public void run() {
            try {

               // String[] elements = mMsg.split(" "); //ID table... URL?

                Log.e("App", "Trying to sign in...");
                //Socket client = new Socket(elements[2], 7575);  //connect to server
                Socket client = new Socket(mMsg, 7575);  //connect to server
                PrintWriter printwriter = new PrintWriter(client.getOutputStream(), true);
                printwriter.write(mMsg);  //write the message to output stream

                printwriter.flush();
                printwriter.close();
                client.shutdownOutput();
                client.close();   //closing the connection

                if(client.isClosed())
                {
                    Log.e("Client", "Still connected....." );
                }

            } catch (UnknownHostException e) {
                Log.w("Error",e.toString());
            } catch (IOException e) {
                Log.w("Error",e.toString());
                //Log.w("Warning", e.printStackTrace().to);
               // e.printStackTrace();
            }
        }
    }

    private class OrderFood implements Runnable {
        private String mMsg;

        private com.example.hennessee.menutest.MenuItem item;
        public OrderFood(String msg, com.example.hennessee.menutest.MenuItem _item) {
            mMsg = msg; // TABLE?
            item =_item;
        }

        //What is this?
        public void run() {
            try {

                Socket client = new Socket("198.84.187.102", 7575);  //connect to server
                PrintWriter printwriter = new PrintWriter(client.getOutputStream(), true);
                printwriter.write(mMsg+" "+item.name);  //write the message to output stream

                printwriter.flush();
                printwriter.close();
                client.close();   //closing the connection

                if(client.isClosed())
                {
                    Log.e("Client", "Still connected....." );
                }

            } catch (UnknownHostException e) {
                Log.w("Error",e.toString());
            } catch (IOException e) {
                Log.w("Error",e.toString());
                //Log.w("Warning", e.printStackTrace().to);
                // e.printStackTrace();
            }
        }
    }

}
