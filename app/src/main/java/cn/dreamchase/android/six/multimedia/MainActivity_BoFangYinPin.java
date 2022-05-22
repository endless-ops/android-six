package cn.dreamchase.android.six.multimedia;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;

import cn.dreamchase.android.six.R;

public class MainActivity_BoFangYinPin extends AppCompatActivity {

    private boolean isPlay = false;
    private boolean isPause = false;

    private MediaPlayer mediaPlayer;

    private ImageView ivPlay;
    private SeekBar seekBar;

    private Handler handler = new Handler();

    private TextView startTime;
    private TextView endTime;


    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);

            handler.postAtTime(ticker,next);

            if (mediaPlayer != null && isPlay && !isPause) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                startTime.setText(getTimeStr(mediaPlayer.getCurrentPosition()));
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_play:
                    if (isPlay) {
                        if (isPause) {
                            mediaPlayer.start();
                            isPause = false;

                            ivPlay.setImageResource(R.drawable.pause);
                            handler.post(ticker);
                        }else {
                            mediaPlayer.pause();
                            isPause = true;

                            ivPlay.setImageResource(R.drawable.play);
                            handler.removeCallbacks(ticker);
                        }
                    }else {
                        playMusic();
                        ivPlay.setImageResource(R.drawable.pause);
                        isPlay = true;
                    }
                    break;
            }
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            isPause = true;

            ivPlay.setImageResource(R.drawable.play);

            mediaPlayer.seekTo(0);

            seekBar.setProgress(0);

            startTime.setText("00:00:00");

            handler.removeCallbacks(ticker);
        }
    };


    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // 进度改变
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // 开始拖动
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { // 停止拖动
            if (mediaPlayer != null && isPlay) {
                // 播放中
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        }
    };





    private void playMusic() {
        try {
            mediaPlayer = new MediaPlayer();
            // Uri uri = Uri.parse("android.resource://package_name/raw/sample.png");
            Log.i("MainActivity","应用包名：" + getPackageName());
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chengdu);

            mediaPlayer.setDataSource(MainActivity_BoFangYinPin.this,uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(onCompletionListener);

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    seekBar.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();

                    handler.post(ticker);

                    endTime.setText(getTimeStr(mediaPlayer.getDuration()));
                }
            });

            mediaPlayer.prepare();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bofang_yinpin);
        ivPlay = findViewById(R.id.iv_play);
        ivPlay.setOnClickListener(onClickListener);

        seekBar = findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        startTime = findViewById(R.id.tv_start_time);
        endTime = findViewById(R.id.tv_end_time);
    }


    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();

    }


    private String getTimeStr(long time) {
        SimpleDateFormat format = new SimpleDateFormat("mm:sss");
        return format.format(time);
    }
}