package com.example.administrator.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2018/3/8.
 */

public class UpOrDownActicity extends AppCompatActivity{
    private AsyncHttpClient client;
    private static final String TAG = "UpOrDownActicity";
    private static final int REQUEST_CODE = 6384;
    private EditText fileUrl;
    private EditText downFile;
    private Button btnUpload,btnDownLoad;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upordown);
        client=new AsyncHttpClient();
        fileUrl=findViewById(R.id.fileUrl);
        downFile=findViewById(R.id.downFile);
        btnUpload=findViewById(R.id.btnUpload);
        btnDownLoad=findViewById(R.id.btnDownload);
    }
    public void showChooser(View view) {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, getString(R.string.chooser_title));
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        final Uri uri = data.getData();
                        System.out.println("Uri = " + uri.toString());
                        try {
                            // Get the file path from the URI
                            final String path = FileUtils.getPath(this, uri);
                            Toast.makeText(UpOrDownActicity.this,
                                    "File Selected: " + path, Toast.LENGTH_LONG).show();
                            fileUrl.setText(path);
                        } catch (Exception e) {
                            Log.e("UpOrDownActicity", "File select error", e);
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void downLoad(View view){
        btnDownLoad.setEnabled(false);
        String path="http://10.10.10.213:8118/static/file/";
        final String fileName=downFile.getText().toString();
        String url=path+fileName;
        client.get(url, new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                Bitmap bitmap= BitmapFactory.decodeByteArray(binaryData,0,binaryData.length);
                File directory =new File(Environment.getExternalStorageDirectory(),"AndroidProject");
                if(!directory.exists()){
                    directory.mkdirs();
                }
                File file=new File(directory,fileName);
                //设置压缩格式
                Bitmap.CompressFormat format=Bitmap.CompressFormat.JPEG;
                //设置压缩比例
                int quality=100;
                //判断文件是否存在
                if(file.exists()){
                    file.delete();
                }
                try {
                    file.createNewFile();
                    OutputStream stream=new FileOutputStream(file);
                    bitmap.compress(format,quality,stream);
                    stream.close();
                    Toast.makeText(UpOrDownActicity.this,"保存成功！",Toast.LENGTH_SHORT).show();
                }catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(UpOrDownActicity.this,"保存失败！",Toast.LENGTH_SHORT).show();
                }

                setEnable();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
                setEnable();
                Toast.makeText(UpOrDownActicity.this,"通信异常,图片下载失败！",Toast.LENGTH_SHORT).show();
            }

             void setEnable(){
                btnDownLoad.setEnabled(true);
            }
        });

    }
    public void upLoad(View view) throws IOException{
        btnUpload.setEnabled(false);
        String url="http://10.10.10.213:8118/upload";
        String path=fileUrl.getText().toString();
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("file", file);
            // 上传文件
            client.post(url, params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    setEnable();
                    Toast.makeText(UpOrDownActicity.this, "上传失败"+throwable, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    if("0".equals(responseString)) {
                        Toast.makeText(UpOrDownActicity.this, "上传成功", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(UpOrDownActicity.this, "上传失败", Toast.LENGTH_LONG).show();
                    }
                    setEnable();
                }


                @Override
                public void onRetry(int retryNo) {
                    // TODO Auto-generated method stub
                    super.onRetry(retryNo);
                    // 返回重试次数
                }

                void setEnable(){
                    btnUpload.setEnabled(true);
                }
            });
        } else {
            Toast.makeText(UpOrDownActicity.this, "文件不存在", Toast.LENGTH_LONG).show();
        }
    }
}
