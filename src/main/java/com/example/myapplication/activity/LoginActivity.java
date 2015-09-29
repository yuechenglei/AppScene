package com.example.myapplication.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.util.MyApplication;
import com.example.myapplication.util.URI;
import com.example.myapplication.view.DialogUtil;
import com.example.wn.myapplication.R;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {
    private String account;
    private String passw;
    private Button bt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    void initView() {
        final EditText edit_login = (EditText) this.findViewById(R.id.et_username);
        final EditText edit_passw = (EditText) this.findViewById(R.id.et_passw);
        bt_login = (Button) this.findViewById(R.id.bt_login);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = edit_login.getText().toString();
                passw = edit_passw.getText().toString();
                startLogin(account, passw);
            }
        });
    }

    //验证成功后的注册
    private void startLogin(final String account, final String password) {
//        // 打开对话框
        final Dialog dialog = DialogUtil.createLoadingDialog(this,
                getString(R.string.waitingDialog));
        dialog.show();
        StringRequest loginRequest = new StringRequest(Request.Method.POST, URI.LoginAddr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("TAG", response);
                        String message = "网络错误";
//                        try {
//                            message = new JSONObject(response).optString("message");
//                        } catch (JSONException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
                        message = response.trim();
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this,
                                message, Toast.LENGTH_SHORT)
                                .show();
                        if (message.equals("登陆成功")) {
                            LoginActivity.this.finish();
                            MyApplication.account = account;
//登陆成功，跳转
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("account", account);
                map.put("password", password);
                map.put("app", "app");//判断是否为客户端登陆
                Log.v("IdentifyNumPage", account);
                Log.v("IdentifyNumPage", password);
                return map;
            }

            //重写方法防止乱码
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // TODO Auto-generated method stub
                String str = null;
                try {
                    str = new String(response.data, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        MyApplication.mQueue.add(loginRequest);

    }

}
