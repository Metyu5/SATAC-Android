package uigu.sisteminformasi.satac;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;


public class audio extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageButton btnPlayPause, btnPrevious, btnNext;
    private SeekBar seekBar;
    private TextView tvCurrentTime, tvTotalTime;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private int playCount = 1;
    private final int MAX_PLAY_COUNT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        seekBar = findViewById(R.id.seekBar);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        mediaPlayer = MediaPlayer.create(this, R.raw.air_on_the_g_string);
        if (mediaPlayer != null) {
            int duration = mediaPlayer.getDuration();
            tvTotalTime.setText(formatTime(duration));
            seekBar.setMax(duration);
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (playCount < MAX_PLAY_COUNT) {
                    playCount++;
                    mp.seekTo(0);
                    mp.start();
                } else {
                    playCount = 1;
                }
            }
        });
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPos = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPos);
                    tvCurrentTime.setText(formatTime(currentPos));
                }
                handler.postDelayed(this, 1000);
            }
        };
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        btnPlayPause.setImageResource(R.drawable.baseline_motion_photos_paused_24);
                        handler.post(updateSeekBar);
                    } else {
                        mediaPlayer.pause();
                        btnPlayPause.setImageResource(R.drawable.baseline_music_note_24);
                    }
                }
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    int current = mediaPlayer.getCurrentPosition();
                    int newTime = Math.max(current - 10000, 0);
                    mediaPlayer.seekTo(newTime);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    int current = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();
                    int newTime = Math.min(current + 10000, duration);
                    mediaPlayer.seekTo(newTime);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    tvCurrentTime.setText(formatTime(progress));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }
    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            handler.removeCallbacks(updateSeekBar);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

