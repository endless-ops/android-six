package cn.dreamchase.android.six;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * NDK和JNI开发
 */
public class MainActivity extends AppCompatActivity {

    private TextView textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tv_jni);

        JNITest jniTest = new JNITest();
        textView.setText("运行结果：" + jniTest.plus(100,10));

    }

    static {
        // libname就是我们在app/build.gradle 中的moduleName的值
        System.loadLibrary("mylib");
    }
}