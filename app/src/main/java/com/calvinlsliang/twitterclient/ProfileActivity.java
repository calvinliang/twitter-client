package com.calvinlsliang.twitterclient;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.calvinlsliang.twitterclient.fragments.UserTimelineFragment;
import com.calvinlsliang.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient client;
    User currentUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        client = TwitterApplication.getRestClient();

        String screenname = getIntent().getStringExtra("screenname");

        populateProfile(screenname);

        if (savedInstanceState == null) {
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenname);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }
    }

    public void populateProfile(String screenname) {
        client.getProfileTimeline(screenname, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    if (response.length() > 0) {
                        currentUser = User.fromJSON(response.getJSONObject(0).getJSONObject("user"));
                        initializeViews();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    public void initializeViews() {
        if (currentUser == null) {
            return;
        }
        ImageView ivUserProfile = (ImageView) findViewById(R.id.ivUserProfile);
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        TextView tvUserDescription = (TextView) findViewById(R.id.tvUserDescription);
        TextView tvUserFollowers = (TextView) findViewById(R.id.tvUserFollowers);
        TextView tvUserFollowing = (TextView) findViewById(R.id.tvUserFollowing);

        Picasso.with(this).load(currentUser.getProfileImageUrl()).into(ivUserProfile);
        tvUserName.setText(currentUser.getName());
        tvUserDescription.setText(currentUser.getDescription());
        tvUserFollowers.setText(String.valueOf(currentUser.getFollowers()) + " Followers");
        tvUserFollowing.setText(String.valueOf(currentUser.getFollowing()) + " Following");
    }

}
