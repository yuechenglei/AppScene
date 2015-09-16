package com.example.myapplication.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wn.myapplication.R;

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

            }
        });
    }


}
