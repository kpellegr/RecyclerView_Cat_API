package com.example.recyclerviewjsonexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ExampleAdapter.OnItemClickListener {
    public static final String EXTRA_URL = "imageUrl";
    public static final String EXTRA_CREATOR = "creatorName";
    public static final String EXTRA_LIKES = "likeCount";
    public static final String EXTRA_CAT_ID = "catID";

    private RecyclerView mRecyclerView;
    private ExampleAdapter mExampleAdapter;
    public static ArrayList<ExampleItem> mExampleList;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mExampleList = new ArrayList<>();
        mExampleAdapter = new ExampleAdapter(MainActivity.this, mExampleList);
        mRecyclerView.setAdapter(mExampleAdapter);
        mExampleAdapter.setOnItemClickListener(MainActivity.this);

        mRequestQueue = Volley.newRequestQueue(this);
        parseJSON();
    }

    private void parseJSON(){
        String url = "https://pixabay.com/api/?key=5303976-fd6581ad4ac165d1b75cc15b3&q=kitten&image_type=photo&pretty=true";

        //eerste 3 paramaters
        JsonRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //parameter 4 vr succes, hier komt request aan indien succesvol
                try {
                    JSONArray jsonArray = response.getJSONArray("hits");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject hit = jsonArray.getJSONObject(i);

                        String creatorName = hit.getString("user");
                        String imageUrl = hit.getString("webformatURL");
                        int likeCount = hit.getInt("likes");

                        mExampleList.add(new ExampleItem(imageUrl, creatorName, likeCount));
                    }

                    mExampleAdapter.notifyDataSetChanged();
                    //zegt de adapter dat data is aangepast en dus opnieuw moet worden geüpdatet

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            //parameter 5 voor error, hier komt request aan indien error
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        ExampleItem clickedItem = mExampleList.get(position);

        // Er zijn drie opties om de cat-details naar de DetailActivity te krijgen:
        // 1: alle nodige details één voor één in als EXTRA in de Intent stoppen
        //    Dit was de originele code van Sarah en staat hieronder in comments
        // 2: De Array met katten ergens "publiek" beschikbaar maken en enkel de index van
        //    de gewenste kat in de Intent te steken.
        //    We doen dit in dit voorbeeld door hem public te zetten in MainActivity en dan
        //    vanuit DetailActivity de kat op te zoeken in de Array.
        //    In een "echt" project zou je hier een ViewModel voor gebruiken.
        // 3. Je kan de kat ook als object in de rugzak steken, maar daarvoor moet je
        //    de "Serializable" interface implementeren, zodat Android weet hoe het een hele
        //    kan in een zak kan krijgen (en er weer uit)...
        //    Dat voorbeeld werken we hier niet uit.


        // Stop alle onderdelen van de kat apart in een zak
        // detailIntent.putExtra(EXTRA_URL, clickedItem.getImageUrl());
        // detailIntent.putExtra(EXTRA_CREATOR, clickedItem.getCreator());
        // detailIntent.putExtra(EXTRA_LIKES, clickedItem.getLikeCount());

        // Stop alleen het nummer van de kat in de zak
        detailIntent.putExtra(EXTRA_CAT_ID, position);

        startActivity(detailIntent);
    }
}