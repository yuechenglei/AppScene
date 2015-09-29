package com.example.myapplication.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;

import com.android.volley.Request;
import com.example.myapplication.entity.FormImage;
import com.example.myapplication.net.PostUploadRequest;
import com.example.myapplication.net.ResponseListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by yuechenglei on 2015/9/5.
 */
public class UpLoadPhoto {

    public String headerImgname;
    public final String photo_path = getSDPath() + "/MyDocument";
    public static final int PHOTO_REQUEST = 1001;
    public static final int CAMERA_REQUEST = 1002;
    public static final int CAMERA_CUT_REQUEST = 1003;

    public static final int TEAMHEADER = 11;
    public static final int PERSONHEADER = 12;

    public Activity activity;

    public UpLoadPhoto(Activity activity) {
        this.activity = activity;
    }

    /**
     * change uri to path string
     * @param uri
     * @return
     */

//    public String getPath(Uri uri)
//    {
//        String[] projection = {MediaStore.Images.Media.DATA };
//        Cursor cursor = managedQuery(uri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }

    /**
     * use to lessen pic 50%
     * @param path sd card path
     * @return bitmap
     */
    public final static Bitmap lessenUriImage(String path)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); //此时返回 bm 为空
        options.inJustDecodeBounds = false; //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = (int)(options.outHeight / (float)320);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be; //重新读入图片，注意此时已经把 options.inJustDecodeBounds 设回 false 了
        bitmap=BitmapFactory.decodeFile(path,options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        System.out.println(w+" "+h); //after zoom
        return bitmap;
    }

    /**
     * 上传图片接口
     *
     * @param bitmap   需要上传的图片
     * @param listener 请求回调
     */
    public static void uploadImg(String URI, Bitmap bitmap, ResponseListener listener) {
        List<FormImage> imageList = new ArrayList<FormImage>();
        imageList.add(new FormImage(bitmap));
        Request request = new PostUploadRequest(URI, imageList, listener);
        MyApplication.mQueue.add(request);
    }

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.getPath();
    }

    // 调用系统相册，选择并调用裁切，并储存路径
    public void chooseAlbum() {
        Intent innerIntent = new Intent(Intent.ACTION_PICK); // 调用相册
        innerIntent.putExtra("aspectX", 1);// 放大和缩小功能
        innerIntent.putExtra("aspectY", 1);
        innerIntent.putExtra("outputX", 140);// 输出图片大小
        innerIntent.putExtra("outputY", 140);
        innerIntent.setType("image/*");// 查看类型
        innerIntent.putExtra("return-data", true);
        innerIntent.putExtra("outputFormat", "JPEG"); // 输入文件格式

        activity.startActivityForResult(innerIntent, PHOTO_REQUEST); // 设返回
    }

    public void chooseCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用android自带的照相机
/*        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(makeDir()));
        cameraIntent.putExtra("return-data", true);
        cameraIntent.putExtra("outputFormat", "JPEG");*/
        activity.startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public File makeDir() {
        Date date = new Date(); // 获取当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINESE); // 时间字符串格式
        headerImgname = format.format(date);

        File tempFile = new File(photo_path + "/" + headerImgname + ".jpg");
        File file = new File(photo_path);
        if (!file.exists()) {
            file.mkdir();
        }
        return tempFile;
    }
}
