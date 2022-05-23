package cn.dreamchase.android.six.copy.paste;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import cn.dreamchase.android.six.R;

/**
 * 复制和粘贴
 */
public class MainActivity_Copy_Paste extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fuzhi_zhantie);

        // 复制文本
        String text = "abacdd";
        // this.CLIPBOARD_SERVICE 也可以是 Context.CLIPBOARD_SERVICE
        ClipboardManager cmb = (ClipboardManager) getSystemService(this.CLIPBOARD_SERVICE);
        cmb.setPrimaryClip(ClipData.newPlainText(null,text));


        // 粘贴文本
        String content = cmb.getText().toString();

    }
}