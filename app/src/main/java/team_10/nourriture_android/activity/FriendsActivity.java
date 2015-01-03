package team_10.nourriture_android.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import team_10.nourriture_android.R;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

/**
 * Created by ping on 2014/12/28.
 */
public class FriendsActivity extends ActionBarActivity {

    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        sp = getApplicationContext().getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        getFriends();
    }

    public void getFriends() {
        String userName = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");
       /* String str = userName + ":" + password;
        String encodeStr = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
        String loginStr = "Basic " + encodeStr;
        NourritureRestClient.addHeader(loginStr);*/
        NourritureRestClient.getWithLogin("getMyFriend", null, userName, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("getMyFriend", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("getMyFriend", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("statusCode", String.valueOf(statusCode));
                Log.e("responseString", responseString);
                Log.e("throwable", throwable.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
