package cn.dreamchase.android.six.multimedia;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import cn.dreamchase.android.six.R;

/**
 * -播放视频的三种方式
 * -自带播放器
 * --使用自带播放器播放视频比较简单，使用隐式Intent来调用它，通过构造方法传入一个Action，调用Intent的setDataAndType方法传入一个URL与视频格式
 * --- String path = Environment.getExternalStorageDirectory() + "/ansen.mp4";
 * --- Intent intent = new Intent(Intent.ACTION_VIEW);
 * --- intent.setDataAndType(Uri.parse("file://" + path),"video/mp4");
 * --- startActivity(intent);
 *
 * ----------------------------------------------------------------------
 * -使用VideoView方法实现
 * --这种方式不怎么灵活，很多东西不可控
 * --- Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "/ansen.mp4");
 * --- VideoView videoView = this.findViewById(R.id.video_view);
 * --- videoView.setMediaController(new MediaController(this));
 * --- videoView.setVideoURI(uri);
 * --- videoView.start();
 *
 * -----------------------------------------------------------------------
 * -MediaPlayer + TextureView
 * --如果想显示一段在线视频或者任意的数据流，例如视频或者OpeGL场景，可以使用Android中的TextureView实现
 * --1.TextureView 的兄弟 SurfaceView
 * ---应用程序的视频或OpenGL内容往往显示在一个特别的UI控件中：SurfaceView。
 * ---SurfaceView的工作方式是创建一个置于应用窗口之后的新窗口。这种方式的效率非常高，
 * ---因为SurfaceView窗口刷新时不需要重绘应用程序的窗口（Android普通窗口的视图绘制机制是一层一层的，
 * ---任何一个子元素或者局部的刷新都会导致整个视图结构全部重绘一次，因此效率非常低，不过满足普通应用界面的需求还是绰绰有余的），
 * ---当然，SurfaceView也有一些非常不便的限制。因为SurfaceView的内容不在应用窗口中，所以不能使用变换（平移，缩放、旋转等），
 * 也难以放在ListView或者ScrollView中，将不能使用UI控件的一些特性，例如View.setAlpha()。
 *
 * --2.TextureView
 * ---为了解决前面的问题，Android4.0中引入了TextureView。与SurfaceView相比，TextureView并没有创建单独的SurfaceView来绘制
 * ---这使得它可以像普通的View一样执行一些变换操作，如设置透明度等。另外，TextureView必须在硬件加速开启的窗口中。
 * ---经常会碰到一些问题，例如：
 * ----用SurfaceView 播放视频时，从图片切换到播放视频会出现黑屏的现象
 * ----SurfaceView 灵活性没有TextureView好。
 *
 * --------------------------------------------------------------------
 * 国内很好的视频播放开源框架
 * -- https://github.com/lipangit/JiaoZiVideoPlayer
 */
public class MainActivity_BoFang_Video extends AppCompatActivity {

    private final String Tag = MainActivity_BoFang_Video.class.getSimpleName();
    private MediaPlayer mediaPlayer;
    private Surface surface;
    private ImageView videoView;
    private ImageView ivPlay;
    private SeekBar seekBar;
    private Handler handler = new Handler();

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);

            handler.postAtTime(ticker,next);

            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());  //更新播放进度
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bofang_video);

        TextureView textureView = findViewById(R.id.textureview);
        textureView.setSurfaceTextureListener(surfaceTextureListener);//设置监听函数  重写4个方法

        videoView = findViewById(R.id.video_image);

        seekBar = findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            MainActivity_BoFang_Video.this.surface = new Surface(surface);
            new PlayerVideoThread().start(); // 开启一个线程去播放视频

            handler.post(ticker); // 更新进度
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            // 尺寸改变

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            // 销毁
            MainActivity_BoFang_Video.this.surface = null;
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

        }
    };


    private class PlayerVideoThread extends Thread {

        @Override
        public void run() {

            try {
                mediaPlayer = new MediaPlayer();
                Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ansen);
                mediaPlayer.setDataSource(MainActivity_BoFang_Video.this,uri);

                mediaPlayer.setSurface(surface);

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mediaPlayer.setOnCompletionListener(onCompletionListener);

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoView.setVisibility(View.GONE);
                        mediaPlayer.start();
                        seekBar.setMax(mediaPlayer.getDuration());
                    }
                });

                mediaPlayer.prepare();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // 进度改变
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // 开始拖动
            Log.i(Tag,"onStartTrackingTouch");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 停止拖动
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        }
    };


    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            videoView.setVisibility(View.VISIBLE);
            seekBar.setProgress(0);
            handler.removeCallbacks(ticker);
        }
    };
}