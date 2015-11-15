package com.calvinlsliang.twitterclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.calvinlsliang.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class NewTweetActivity extends AppCompatActivity {

    User currentUser = null;

    private final int RESULT_OK = 200;
    private TextView tvRemaining;
    private EditText etNewBody;
    private TwitterClient client;

    private final TextWatcher tvRemainingWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            tvRemaining.setText(String.valueOf(140 - s.length()));
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tweet);

        initializeViews();
        client = TwitterApplication.getRestClient();
    }

    private void initializeViews() {
        currentUser = getIntent().getParcelableExtra("currentUser");

        ImageView ivNewProfile = (ImageView) findViewById(R.id.ivUserProfile);
        TextView tvNewUsername = (TextView) findViewById(R.id.tvNewUsername);
        TextView tvNewName = (TextView) findViewById(R.id.tvUserName);

        Picasso.with(this).load(currentUser.getProfileImageUrl()).into(ivNewProfile);
        tvNewUsername.setText(currentUser.getScreenName());
        tvNewName.setText(currentUser.getName());

        etNewBody = (EditText) findViewById(R.id.etNewBody);
        tvRemaining = (TextView) findViewById(R.id.tvRemaining);
        etNewBody.addTextChangedListener(tvRemainingWatcher);
    }

    public void onTweet(View v) {
        try {
            String message = (String) etNewBody.getText().toString();

            client.newTweet(message, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Intent i = new Intent();
                    setResult(RESULT_OK, i);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCancel(View v) {
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }
}
