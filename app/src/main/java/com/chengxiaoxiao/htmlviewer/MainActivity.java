package com.chengxiaoxiao.htmlviewer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{

    private EditText editAddress;
    private TextView textSource;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editAddress = (EditText) findViewById(R.id.editAddress);
        textSource = (TextView) findViewById(R.id.textSource);

    }

    Handler handler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            //此方法可以接收到消息

            switch (msg.what)
            {
                case 0:
                    Toast.makeText(MainActivity.this, "请求出错", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    String html =  (String)msg.obj;

                    textSource.setText(html);
                    break;
            }
        }
    };

    public void getHtml(View v)
    {
        final String address = editAddress.getText().toString().trim();
        if (TextUtils.isEmpty(address))
        {
            Toast.makeText(MainActivity.this, "请输入网址", Toast.LENGTH_SHORT).show();
            return;
        }


        new Thread()
        {
            @Override
            public void run()
            {
                //在run方法中的代码，就是通过新线程进行执行的
                //创建一个消息
                Message message = new Message();

                try
                {
                    URL url = new URL(address);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //请求方法
                    conn.setRequestMethod("GET");
                    //请求超时时间
                    conn.setConnectTimeout(500);
                    //获取响应吗，如果响应为200则请求成功
                    int code = conn.getResponseCode();


                    if (code==200)
                    {
                        //得到响应的流
                        InputStream in = conn.getInputStream();
                        //我们通过处理读取流对象就可以获取网页源代码了
                        String html =StreamTool.decodeStream(in);


                        //这算是一个标示，handler通过此标识可以处理不同的逻辑
                        message.what = 1;
                        //传递给界面UI的数据对象
                        message.obj = html;

                        //通过秘书给handler发消息
                        handler.sendMessage(message);

                    }
                    else
                    {
                        message.what = 0;
                        handler.sendMessage(message);
                    }


                } catch (Exception e)
                {
                    message.what = 0;
                    handler.sendMessage(message);
                }

            }
        }.start();

    }
}
