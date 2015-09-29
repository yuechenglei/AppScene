package com.example.myapplication.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.adapter.ListAdapter;
import com.example.myapplication.adapter.ListBean;
import com.example.myapplication.net.GsonRequest;
import com.example.myapplication.net.ResponseListener;
import com.example.myapplication.util.MyApplication;
import com.example.myapplication.util.URI;
import com.example.myapplication.util.UpLoadPhoto;
import com.example.myapplication.view.DialogUtil;
import com.example.wn.myapplication.R;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {
    ListView listView;
    List<ListBean> list;
    ListAdapter listAdapter;

    public static final int PHOTO_REQUEST = 1001;
    public static final int CAMERA_REQUEST = 1002;
    public static final int CAMERA_CUT_REQUEST = 1003;

    public static final int TEAMHEADER = 11;
    public static final int PERSONHEADER = 12;

    Button create_btn, take_photo;
    int position;//案件id


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarLayout(R.layout.layout_actionbar);
        TextView actionbar_text = (TextView) findViewById(R.id.actionbar_text);
        actionbar_text.setText("案件列表");
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.mylist);
        list = new ArrayList<ListBean>();
        getCase(MyApplication.account);
        listAdapter = new ListAdapter(list, this);
        listView.setAdapter(listAdapter);

        create_btn = (Button) findViewById(R.id.create_new);
        take_photo = (Button) findViewById(R.id.take_photo);

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivityForResult(intent, 102);
            }
        });

        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开相机
                UpLoadPhoto upLoadPhoto = new UpLoadPhoto(MainActivity.this);
                upLoadPhoto.chooseCamera();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_REQUEST:// 相册返回
                Uri uri = data.getData();
                String imgPath = getImageAbsolutePath(MainActivity.this, uri);
                Bitmap bitmap = UpLoadPhoto.lessenUriImage(imgPath);

                Log.v("===========URI=========", uri.getPath());
                Log.v("===========URI=========", bitmap.toString());
                // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                //上传案件id参数
                //upCaseId(position);
                //上传图片
                uploadImg(bitmap);
                break;

            case CAMERA_REQUEST:// 照相返回

                break;

            case 102:
                if (data != null) {
                    //getCase(MyApplication.account);
                    Log.v("main", "刷新");
                    Log.v("main", list.toString());
                    String name = data.getStringExtra("name");
                    String introduce = data.getStringExtra("introduce");
                    list.add(new ListBean(1, name, introduce));
                    listAdapter.setList(list);
                    listAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * 上传案件参数
     *
     * @param
     */
    void upCaseId(final int position) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.pushPhoto,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("case_id", Integer.toString(position));
                return map;
            }
        };
        MyApplication.mQueue.add(stringRequest);
    }

    public void uploadImg(Bitmap bitmap) {
        final Dialog mDialog = DialogUtil.createLoadingDialog(this, "正在上传");
        mDialog.show();
        // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
        UpLoadPhoto.uploadImg(URI.pushPhoto, bitmap, new ResponseListener<String>() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("zgy", "===========VolleyError=========" + error);
                // mShowResponse.setText("ErrorResponse\n" + error.getMessage());
                Toast.makeText(MainActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }

            @Override
            public void onResponse(String response) {
//                response = response.substring(response.indexOf("img src="));
//                response = response.substring(8, response.indexOf("/>"));
                Log.v("zgy", "===========onResponse=========" + response);
                // mShowResponse.setText("图片地址:\n" + response);
                mDialog.dismiss();
                Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
            }

//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<String, String>();
//                map.put("case_id", "24");
//                Log.v("IdentifyNumPage", "case_id");
//                return map;
//            }
        });
    }

    //获取运程的案件
    public void getCase(String account) {
        final Dialog dialog = DialogUtil.createLoadingDialog(this, "正在加载");
        dialog.show();
        final ArrayList<ListBean> mycase = new ArrayList<>();
        GsonRequest<ArrayList<ListBean>> caseRequest = new GsonRequest<ArrayList<ListBean>>(Request.Method.POST,
                URI.getCase, new TypeToken<ArrayList<ListBean>>() {
        }.getType(),
                new Response.Listener<ArrayList<ListBean>>() {
                    @Override
                    public void onResponse(ArrayList<ListBean> getcase) {
                        dialog.dismiss();
                        if (list.size() > 0)
                            list.clear();
                        for (int a = 0; a < getcase.size(); a++) {
                            list.add(getcase.get(a));
                            Log.v("main", getcase.get(a).toString());
                        }
                        listAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("xkh", MyApplication.account);
                return map;
            }
        };
        MyApplication.mQueue.add(caseRequest);
        Log.v("main", mycase.toString());

    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param imageUri
     * @author yaoxing
     * @date 2014-10-12
     */
    @TargetApi(19)
    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
