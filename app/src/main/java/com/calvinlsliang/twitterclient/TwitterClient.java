package com.calvinlsliang.twitterclient;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CALLBACK_URL = "oauth://cltwitterclient";

	public static final String REST_CONSUMER_KEY = "Z1JH9KOpqREtMjyVgvPyBees1";       // Change this
	public static final String REST_CONSUMER_SECRET = "uniZTHs68J6jDvsfSOO9CPNVKuVbjois91mwD7b6lW4059hJ30"; // Change this

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeline(long since, long offset, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", offset);

		if (since != 0) {
			params.put("max_id", since);
		}

		client.get(apiUrl, params, handler);
	}

    public void getMentionsTimeline(long since, long offset, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", offset);

        if (since != 0) {
            params.put("max_id", since);
        }

        client.get(apiUrl, params, handler);
    }


    public void getProfile(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		RequestParams params = new RequestParams();
		client.get(apiUrl, params, handler);
	}

	public void newTweet(String status, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");

		RequestParams params = new RequestParams();
		if (status != null) {
			params.put("status", status);
		}

		client.post(apiUrl, params, handler);
	}

	public void getProfileTimeline(String username, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();

		// If username is null, grab authenticated user's profile, otherwise return
		// the specific user wanted

		if (username != null) {
			params.put("username", username);
		}

		client.get(apiUrl, params, handler);
	}

}