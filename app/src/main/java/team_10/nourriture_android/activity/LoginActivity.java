package team_10.nourriture_android.activity;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.application.MyApplication;
import team_10.nourriture_android.bean.UserBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.push.Utils;
import team_10.nourriture_android.service.PollingService;
import team_10.nourriture_android.service.PollingUtils;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

/**
 * Created by ping on 2014/12/22.
 */
public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    public static final String TAG_USER_ACCOUNT = "user.s";
    public static int KEY_IS_LOGIN = 1;

    private EditText username_et;
    private EditText password_et;
    private Button login_btn;
    private Button back_btn;
    private String username;
    private String password;
    private UserBean userBean;
    private List<UserBean> userList;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    public void initView() {
        username_et = (EditText) this.findViewById(R.id.et_username);
        password_et = (EditText) this.findViewById(R.id.et_password);
        login_btn = (Button) this.findViewById(R.id.btn_login);
        back_btn = (Button) this.findViewById(R.id.btn_back);
        login_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);
    }

    public void checkLogin() {
        username = username_et.getText().toString().trim();
        password = password_et.getText().toString().trim();
        if (username == null || "".equals(username)) {
            Toast.makeText(this, "Please enter the username.", Toast.LENGTH_SHORT).show();
            username_et.setFocusable(true);
        } else if (password == null || "".equals(password)) {
            Toast.makeText(this, "Please enter the password.", Toast.LENGTH_SHORT).show();
            password_et.setFocusable(true);
        } else {
            progress = new ProgressDialog(this);
            progress.setMessage("Login...");
            progress.show();
            login();
        }
    }

    public void login() {
        String str = username + ":" + password;
        String encodeStr = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
        String loginStr = "Basic " + encodeStr;
        NourritureRestClient.addHeader(loginStr);
        NourritureRestClient.post("login", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("login", response.toString());
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (statusCode == 200) {
                    try {
                        userList = JsonTobean.getList(UserBean[].class, response.toString());
                        MyApplication.getInstance().updateOrSaveUserBean(userList.get(0));
                        MyApplication.getInstance().islogin = true;
                        SharedPreferencesUtil.saveLogin(getBaseContext(), username, password, true);
                        Toast.makeText(LoginActivity.this, "Login Success.", Toast.LENGTH_SHORT).show();

                        // start polling service
                        PollingUtils.startPollingService(LoginActivity.this, 2, PollingService.class, PollingService.ACTION);

                        Resources resource = getApplicationContext().getResources();
                        String pkgName = getApplicationContext().getPackageName();
                        // Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
                        // 这里把apikey存放于manifest文件中，只是一种存放方式，
                        // 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(MainActivity.this,
                        // "api_key")
                        PushManager.startWork(getApplicationContext(),
                                PushConstants.LOGIN_TYPE_API_KEY,
                                Utils.getMetaValue(LoginActivity.this, "api_key"));
                        // Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
                        // PushManager.enableLbs(getApplicationContext());

                        // Push: 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段代码
                        // 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
                        // 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
                        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
                                getApplicationContext(), resource.getIdentifier(
                                "notification_custom_builder", "layout", pkgName),
                                resource.getIdentifier("notification_icon", "id", pkgName),
                                resource.getIdentifier("notification_title", "id", pkgName),
                                resource.getIdentifier("notification_text", "id", pkgName));
                        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
                        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND
                                | Notification.DEFAULT_VIBRATE);
                        cBuilder.setStatusbarIcon(getApplicationContext().getApplicationInfo().icon);
                        cBuilder.setLayoutDrawable(resource.getIdentifier(
                                "simple_notification_icon", "drawable", pkgName));
                        PushManager.setNotificationBuilder(getApplicationContext(), 1, cBuilder);

                        Intent intent = new Intent();
                        setResult(KEY_IS_LOGIN, intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    username_et.setText(null);
                    password_et.setText(null);
                    username_et.requestFocus();
                    Toast.makeText(LoginActivity.this, "Username or Password is wrong.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                username_et.setText(null);
                password_et.setText(null);
                username_et.requestFocus();
                Toast.makeText(LoginActivity.this, "Username or Password is wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                checkLogin();
                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
