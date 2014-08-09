package com.example.hennessee.menutest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ViewItemFragment extends Fragment {

    public String Name;
    public String Price;
    public String Category;
    public String Comments;
    public String ImageURL;

    ImageView mImage;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewItemFragment newInstance(String param1, String param2) {
        ViewItemFragment fragment = new ViewItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewItemFragment(){
    }

    public ViewItemFragment(String name, String category, String price, String comments, String imageURL) {
        Name = name;
        Category = category;
        Comments = comments;
        Price = price;
        ImageURL = imageURL;
    }

    public void SetUp(String name, String category, String price, String comments, String imageURL)
    {
        TextView txtName = (TextView)getView().findViewById(R.id.txtName);
        TextView txtCategory = (TextView)getView().findViewById(R.id.txtCategory);
        TextView txtPrice = (TextView)getView().findViewById(R.id.txtPrice);
        TextView txtComments = (TextView)getView().findViewById(R.id.txtComment);

        mImage = (ImageView)getActivity().findViewById(R.id.imageView);

        txtName.setText(name);
        txtCategory.setText(category);
        txtPrice.setText(price);
        txtComments.setText(comments);

        new Thread(new Runnable() {
            public void run() {
                final Drawable d = LoadImageFromWebOperations(ImageURL);
                mImage.post(new Runnable() {
                    public void run() {
                        mImage.setImageDrawable(d);
                    }
                });
            }
        }).start();

        //Bitmap bmp = DownloadImage(imageURL);
        //mImage.setImageBitmap(bmp);

        Bitmap bmp;
        //new MyAsyncTask.execute(bmp);
    }

    class MyAsyncTask extends AsyncTask<String, String, Bitmap> {

        Bitmap bmp;

        protected void onPreExecute() {

        }

        @Override
        protected Bitmap doInBackground(String... params) {

        bmp = DownloadImage(ImageURL);
        return bmp;

        } // protected Void doInBackground(String... params)


        protected void onPostExecute(Void v) {


        } // protected void onPostExecute(Void v)

        private InputStream OpenHttpConnection(String urlString) throws IOException {
            InputStream in = null;
            int response = -1;

            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();

            if (!(conn instanceof HttpURLConnection))
                throw new IOException("Not an HTTP connection");

            try {
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                response = httpConn.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    in = httpConn.getInputStream();
                }
            } catch (Exception ex) {
                throw new IOException("Error connecting");
            }
            return in;
        }

        private Bitmap DownloadImage(String URL) {
            Bitmap bitmap = null;
            InputStream in = null;
            try {
                Log.e("URL", URL);
                in = OpenHttpConnection(URL);
                bitmap = BitmapFactory.decodeStream(in);
                in.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            return bitmap;
        }

    } //class MyAsyncTask extends AsyncTask<String, String, Void>

    public static Drawable LoadImageFromWebOperations(String url) {
        try {

            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "image.jpg");
            return d;
        } catch (Exception e) {
            Log.e("Error", e.toString());
            return null;
        }
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_item, container, false);


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onViewCreated (View view, Bundle savedInstanceState)
    {
        SetUp(Name, Category, Price, Comments, ImageURL);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
