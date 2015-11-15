package com.calvinlsliang.twitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.calvinlsliang.twitterclient.EndlessScrollListener;
import com.calvinlsliang.twitterclient.TwitterApplication;
import com.calvinlsliang.twitterclient.TwitterClient;
import com.calvinlsliang.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by cliang on 11/14/15.
 */
public class HomeTimelineFragment extends TweetsListFragment {
    private TwitterClient client;
    private long since = 0;
    private long offset = 25;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient();
        populateTimeline();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (lvTweets != null) {
            lvTweets.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    populateTimeline();
                    aTweets.notifyDataSetChanged();
                    return true;
                }
            });
        }

        return view;
    }

    public void populateTimeline() {
        client.getHomeTimeline(since, offset, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tweet> alTweets = Tweet.fromJSONArray(response);
                addAll(alTweets);
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

    public void restart() {
        since = 0;
        aTweets.clear();
        populateTimeline();

    }
}
