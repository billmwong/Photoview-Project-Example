package com.example.wwong.imagelabtest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

    private ArrayList<String> imageURLs = new ArrayList<>();
    private MainActivity mainActivity;
    private WebView webView;
    private int currImgPos = 0;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mainActivity = (MainActivity) getActivity();

        // Declare layout views
        final EditText searchInput = (EditText) view.findViewById(R.id.searchInput);
        ImageButton searchButton = (ImageButton) view.findViewById(R.id.searchButton);
        Button prevButton = (Button) view.findViewById(R.id.previousButton);
        Button nextButton = (Button) view.findViewById(R.id.nextButton);
        Button saveButton = (Button) view.findViewById(R.id.saveButton);
        webView = (WebView) view.findViewById(R.id.webView);
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        // OnClickListeners
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text of the searchInput EditText, and then do the search
                String searchTerm = searchInput.getText().toString();
                searchGoogle(searchTerm);
            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Can't go to previous image if we're at the beginning already
                if (currImgPos > 0) {
                    currImgPos--;
                    updateWebView();
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Can't go to next image if we're at the end
                if (currImgPos < imageURLs.size()) {
                    currImgPos++;
                    updateWebView();
                }
            }
        });

        return view;
    }

    /**
     * Searches google for images of the searchTerm, builds the imageURLs ArrayList with ten URLs
     * for the ten images in the search results.
     * @param searchTerm The search query to find images of
     */
    private void searchGoogle(String searchTerm) {
        String url = "https://www.googleapis.com/customsearch/v1" +
                     "?key=AIzaSyDZB0aUx8X9QuCBBYvKg3KJXodV0no2YIs" +
                     "&cx=001581967375476266624:g-yjubqpbso" +
                     "&searchType=image" +
                     "&q=" +
                     searchTerm;

        // Request a JSON response from the url
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    /**
                     * Called when the volley request gets a response back from the internet, builds
                     * the imageURLs ArrayList with the corresponding imageURLs
                     * @param response The JSON data that we got back from the internet
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MainActivity", "Successfully got JSON response");
                        imageURLs.clear();
                        try {
                            JSONArray imageJSONs = response.getJSONArray("items");
                            for (int i=0;i<imageJSONs.length();i++) {
                                JSONObject imageJSON = imageJSONs.getJSONObject(i);
                                String url = imageJSON.get("link").toString();
                                imageURLs.add(url);
                                Log.d("MainActivity","added to ImageURLs: " + url);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Update the WebView with the first image
                        currImgPos = 0;
                        updateWebView();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error: ", "An error occurred in Volley");
                    }
                });
        // Add the request to a RequestQueue
        Volley.newRequestQueue(mainActivity).add(jsObjRequest);
    }

    private void updateWebView() {
        // Only try to load images if ImageURLs is not empty
        if (!imageURLs.isEmpty()) {
            Log.d("SearchFragment","updating WebView");
            String url = imageURLs.get(currImgPos);
            webView.loadUrl(url);
        }
    }

}
