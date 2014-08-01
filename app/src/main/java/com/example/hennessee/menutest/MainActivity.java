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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,scanFragment.OnFragmentInteractionListener, SpecialsFragment.OnFragmentInteractionListener, DessertsFragment.OnFragmentInteractionListener, AppetizersFragment.OnFragmentInteractionListener {

    public static boolean scanned = false;

    public void onFragmentInteraction(Uri uri)
    {

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

        /*final Button button = (Button) findViewById(R.id.buttonScan);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        "com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            }

        });*/
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {


                String contents = intent.getStringExtra("SCAN_RESULT"); // This will contain your scan result
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");


                Toast.makeText(MainActivity.this, contents,
                        Toast.LENGTH_SHORT).show();

                scanned = true;

                mNavigationDrawerFragment.SetList();

                Fragment frag = new SpecialsFragment();
                FragmentManager fragmentManager = getFragmentManager();
                /*fragmentManager.beginTransaction()
                        .replace(R.id.container, frag)
                        .commit();*/
                mNavigationDrawerFragment.dynamicSelectItem(0);
                    ///////////////


              //  JSONObject jsonobject;
              //  jsonobject = JSONfunctions.getJSONfromURL("http://198.84.187.102/MenuGUI/Menu.php");
               // jsonobject.getString()

                //http post


               // //connectToDB();


//                URL url;
//                try {
//                    url = new URL("http://198.84.187.102/MenuGUI/Menu.php");
//
//                    URLConnection conn = url.openConnection();
//
//                    BufferedReader reader = new BufferedReader(new
//                            InputStreamReader(conn.getInputStream()));
//
//                    StringBuffer sb = new StringBuffer("");
//                    String line="";
//                    while ((line = reader.readLine()) != null) {
//                        sb.append(line);
//                        break;
//                    }
//                    reader.close();
//                    Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
//
//                }
//                catch (MalformedURLException e1) {
//                    e1.printStackTrace();
//                }
//                catch (IOException e1){
//                    e1.printStackTrace();
//                }


                new MyAsyncTask().execute();




                /*try {

                    Socket s = new Socket("localhost",12345); // Change localhost to your IP, obviously.
                    //Could even get IP from the scan.

                    //outgoing stream redirect to socket
                    OutputStream out = s.getOutputStream();

                    PrintWriter output = new PrintWriter(out);
                    output.println("Hello Android!");
                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));

                    //read line(s)
                    String st = input.readLine();
                    ///. . .
                    //Close connection
                    s.close();


                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*/

            }
        }

    }

    public void connectToDB(){
        String result = null;
        InputStream is = null;

        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://198.84.187.102/MenuGUI/Menu.php");
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

            Log.e("log_tag", "connection success ");
            Toast.makeText(getApplicationContext(), "pass", Toast.LENGTH_SHORT).show();
        }


        catch(Exception e)
        {
            Log.e("log_tag", "Error in http connection "+e.toString());
            Toast.makeText(getApplicationContext(), "Connection fail",                                        Toast.LENGTH_SHORT).show();

        }
        //convert response to string
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
                //  Intent i = new Intent(getBaseContext(),DatabaseActivity.class);
                //  startActivity(i);
            }
            is.close();

            result=sb.toString();
        }
        catch(Exception e)
        {
            Log.e("log_tag", "Error converting result "+e.toString());
        }


        try{

            JSONObject json_data = new JSONObject(result);

            CharSequence w= (CharSequence) json_data.get("re");

            Toast.makeText(getApplicationContext(), w, Toast.LENGTH_SHORT).show();


        }
        catch(JSONException e)
        {
            Log.e("log_tag", "Error parsing data " + e.toString());
            Toast.makeText(getApplicationContext(), "JsonArray fail", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = new SpecialsFragment();

        if(!scanned && position != 0)
        {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("You must scan the QR code at your table first!")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    /*.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })*/
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        if(!scanned) {
            switch (position) {
                case 0:
                    fragment = new scanFragment();
                    break;
                case 1:
                    fragment = new SpecialsFragment();

                    break;
                case 2:
                    fragment = new AppetizersFragment();

                    break;
                case 3:
                    fragment = new DessertsFragment();
                    break;
                default:
                    fragment = new SpecialsFragment();
                    break;
            }
        }
        else
        {
            switch (position) {
                case 0:
                    fragment = new SpecialsFragment();

                    break;
                case 1:
                    fragment = new AppetizersFragment();

                    break;
                case 2:
                    fragment = new DessertsFragment();
                    break;
                default:
                    fragment = new SpecialsFragment();
                    break;
            }
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        if(!scanned) {
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
        }
        else
        {
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);


            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    class MyAsyncTask extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            progressDialog.setMessage("Your progress dialog message...");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    MyAsyncTask.this.cancel(true);
                }
            });
        }

        @Override
        protected Void doInBackground(String... params) {

            String url_select = "http://198.84.187.102/MenuGUI/menuJSN.php";

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
            Log.w("myApp", result);
            return null;
        } // protected Void doInBackground(String... params)


        protected void onPostExecute(Void v) {

            //parse JSON data
            try{
                JSONArray jArray = new JSONArray(result);

                for(int i=0; i < jArray.length(); i++) {

                    JSONObject jObject = jArray.getJSONObject(i);

                    String name = jObject.getString("name");
                    String tab1_text = jObject.getString("tab1_text");
                    int active = jObject.getInt("active");


                } // End for Loop

                this.progressDialog.dismiss();

            } catch (JSONException e) {

                Log.e("JSONException", "Error: " + e.toString());

            } // catch (JSONException e)


        } // protected void onPostExecute(Void v)

    } //class MyAsyncTask extends AsyncTask<String, String, Void>

}
