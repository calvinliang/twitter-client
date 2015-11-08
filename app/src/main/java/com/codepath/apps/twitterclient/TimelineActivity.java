package com.codepath.apps.twitterclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;

    private User currentUser = null;

    private long since = 0;
    private long offset = 25;

    private final int REQUEST_CODE = 20;
    private final int RESULT_OK = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        lvTweets = (ListView) findViewById(R.id.lvTweets);

        tweets = new ArrayList<>();

        aTweets = new TweetsArrayAdapter(this, tweets);

        lvTweets.setAdapter(aTweets);

        client = TwitterApplication.getRestClient();


        populateProfile();

        populateTimeline();

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
//                aTweets.clear();
                populateTimeline();
                aTweets.notifyDataSetChanged();
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuSettings:
                composeMessage();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void composeMessage() {
        Intent i = new Intent(TimelineActivity.this, NewTweetActivity.class);
        i.putExtra("currentUser", currentUser);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            since = 0;
            aTweets.clear();
            populateTimeline();
        }
    }

    private void populateProfile() {
        client.getProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                currentUser = User.fromJSON(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    private void populateTimeline() {
        client.getHomeTimeline(since, offset, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tweet> alTweets = Tweet.fromJSONArray(response);
                aTweets.addAll(alTweets);
                since = getMin(alTweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    private long getMin(ArrayList<Tweet> tweets) {
        long min = Long.MAX_VALUE;

        for (int i = 0; i < tweets.size(); i++) {
            if (min > tweets.get(i).getUid()) {
                min = tweets.get(i).getUid();
            }
        }
        return min;
    }
}
