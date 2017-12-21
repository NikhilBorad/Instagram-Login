package com.test.testinsta.splash;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.test.testinsta.BaseAppCompactActivity;
import com.test.testinsta.R;
import com.test.testinsta.main.MainActivity;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SplashActivity extends BaseAppCompactActivity {

    private String tokenURLString;
    private String authURLString;
    private String request_token;
    private AlertDialog InstaLoginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        intialControl();

    }

    private void intialControl() {

       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               startActivity(new Intent(SplashActivity.this,MainActivity.class));
               finish();
           }
       },3000);

    }


    private void signInToInsta() {


        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//        authURLString = AUTHURL + "?client_id=" + IG_CLIENT + "&redirect_uri=" + CALLBACKURL + "&response_type=code&display=touch&scope=likes+comments+relationships";
//        tokenURLString = TOKENURL + "?client_id=" + IG_CLIENT + "&client_secret=" + IG_CLIENT_SECRET + "&redirect_uri=" + CALLBACKURL + "&grant_type=authorization_code";
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Login");
//
//        WebView webView = new WebView(getApplicationContext());
//        webView.setVerticalScrollBarEnabled(false);
//        webView.setHorizontalScrollBarEnabled(false);
//        webView.setWebViewClient(new AuthWebViewClient());
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                    case MotionEvent.ACTION_UP:
//                        if (!v.hasFocus()) {
//                            v.requestFocus();
//                        }
//                        break;
//                }
//                return false;
//            }
//        });
//        webView.loadUrl(authURLString);
//        builder.setView(webView);
//        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.dismiss();
//            }
//        });
//        InstaLoginDialog = builder.show();


//        final Uri.Builder uriBuilder = new Uri.Builder();
//        uriBuilder.scheme("https")
//                .authority("api.instagram.com")
//                .appendPath("oauth")
//                .appendPath("authorize")
//                .appendQueryParameter("client_id", getString(R.string.insta_client_id))
//                .appendQueryParameter("redirect_uri", "https://auth0.com/login/callback")
//                .appendQueryParameter("response_type", "token");
//        final Intent browser = new Intent(Intent.ACTION_VIEW, uriBuilder.build());
//        startActivity(browser);

    }


    public class AuthWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(CALLBACKURL)) {
                Log.e("TAG", url);
                String parts[] = url.split("=");
                request_token = parts[1];  //This is your request token.
                InstaLoginDialog.dismiss();
                new GetDatFromInsta().execute(request_token);
                return true;
            }
            return false;

        }
    }

    private class GetDatFromInsta extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(tokenURLString);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write("client_id=" + IG_CLIENT +
                        "client_secret=" + IG_CLIENT_SECRET +
                        "grant_type=authorization_code" +
                        "redirect_uri=" + CALLBACKURL +
                        "code=" + request_token);
                outputStreamWriter.flush();
                String response = streamToString(httpsURLConnection.getInputStream());
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();

//                accessTokenString = jsonObject.getString("access_token"); //Here is your ACCESS TOKEN
//                id = jsonObject.getJSONObject("user").getString("id");
//                username = jsonObject.getJSONObject("user").getString("username"); //This is how you can get the user info.
                //You can explore the JSON sent by Instagram as well to know what info you got in a response
                nbUpdatePref(PREF_USER_TOKEN, jsonObject.getString("access_token"));
                nbUpdatePref(PREF_USER_ID, jsonObject.getJSONObject("user").getString("id"));
                nbUpdatePref(PREF_USERNAME, jsonObject.getJSONObject("user").getString("username"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
