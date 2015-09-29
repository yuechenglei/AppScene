package com.example.myapplication.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
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
import com.example.wn.myapplication.R;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuechenglei on 2015/9/10
 */
public class CreateActivity extends ActionBarActivity implements View.OnClickListener {
    String dateStr;
    TextView choose_time;
    int arrive_year, arrive_month, arrive_day;
    EditText name, introduce, inspector, xkh, place;
    Button handin, back;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarLayout(R.layout.layout_actionbar);
        setContentView(R.layout.create_layout);

        choose_time = (TextView) findViewById(R.id.choose_time_tv);
        name = (EditText) findViewById(R.id.edit_name);
        introduce = (EditText) findViewById(R.id.edit_introduce);
        inspector = (EditText) findViewById(R.id.edit_inspector);
        xkh = (EditText) findViewById(R.id.et_xkh);
        place = (EditText) findViewById(R.id.et_place);
        handin = (Button) findViewById(R.id.handin_btn);
        back = (Button) findViewById(R.id.back_btn);
        choose_time.setOnClickListener(this);
        handin.setOnClickListener(this);
        back.setOnClickListener(this);

        intent = getIntent();
    }

    private void showAler() {
        View view = View.inflate(getApplicationContext(), R.layout.date_time_picker, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.new_act_date_picker);

        int year;
        final int month;
        int day;
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        if (choose_time.getText().toString().trim().length() == 0) {
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        } else {
            year = arrive_year;
            month = arrive_month - 1;
            day = arrive_day;
        }
        arrive_year = year;
        arrive_month = month + 1;
        arrive_day = day;

        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                arrive_year = year;
                arrive_month = monthOfYear + 1;
                arrive_day = dayOfMonth;
            }
        });

        // Build DateTimeDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
        builder.setView(view);
        builder.setTitle("选择案件时间");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dateStr = arrive_year + "-" + arrive_month + "-" + arrive_day;
                choose_time.setText(dateStr);
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_time_tv://选择时间
                showAler();
                break;

            case R.id.handin_btn:
                if (name.getText().toString().trim().length() == 0) {
                    Toast.makeText(CreateActivity.this, "案件名称不能为空！", Toast.LENGTH_SHORT).show();
                } else if (introduce.getText().toString().trim().length() == 0) {
                    Toast.makeText(CreateActivity.this, "说明不能为空！", Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra("name", name.getText().toString());
                    intent.putExtra("introduce", introduce.getText().toString());
                    CreateActivity.this.setResult(102, intent);
                    CreateActivity.this.finish();
                    createCase();
                }
                break;

            case R.id.back_btn:
                CreateActivity.this.finish();
                break;
        }
    }

    //创建案件
    void createCase() {
//        final Dialog dialog = DialogUtil.createLoadingDialog(this, "正在创建");
//        dialog.show();
        StringRequest createRequest = new StringRequest(Request.Method.POST, URI.createAddr,
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
                        message = response;
                        // dialog.dismiss();
                        Toast.makeText(CreateActivity.this,
                                message, Toast.LENGTH_SHORT)
                                .show();
//                        if (message.trim().equals("注册成功"))
//                            IdentifyNumPage.this.finish();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                //  dialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", name.getText().toString());
                map.put("inspector", inspector.getText().toString());
                map.put("xkh", xkh.getText().toString());
                map.put("date_anfa", dateStr);
                map.put("place", place.getText().toString());
                map.put("desc", introduce.getText().toString());
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
        MyApplication.mQueue.add(createRequest);

    }
}