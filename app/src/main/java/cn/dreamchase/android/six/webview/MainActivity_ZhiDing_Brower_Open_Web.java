package cn.dreamchase.android.six.webview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import cn.dreamchase.android.six.R;

/**
 * -在Android中可以调用自带的浏览器，或者指定一个浏览器来打开一个链接，只需要传入一个URL（可以是链接地址）即可
 */
public class MainActivity_ZhiDing_Brower_Open_Web extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_zhiding_brower_open_web);

        // 启动Android 默认浏览器
        Uri uri = Uri.parse("www.baidu.com");
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);



        // 启动指定浏览器打开
        intent.setClassName("com.UCMobile","com.uc.browser.InnerUCMobile"); // 打开UC浏览器
        intent.setClassName("com.tencent.mtt","com.tencent.mtt.MainActivity"); // 打开QQ浏览器
        startActivity(intent);
    }

}