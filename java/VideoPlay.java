package com.example.pushtest;

import static android.media.MediaCodec.BUFFER_FLAG_END_OF_STREAM;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.ts.H264Reader;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.ExoPlaybackException;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

public class VideoPlay extends AppCompatActivity {
    ShowVideo showVideo;

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private SurfaceView surfaceview;
    private Surface surface;
    private MediaExtractor mExtractor;
    private MediaCodec codec;
    private MediaMuxer muxer;


    public void setShowVideo(ShowVideo showVideo) {
        this.showVideo = showVideo;
    }

    private MediaController mediaController;


    String filePath = "https://firebasestorage.googleapis.com/v0/b/embedded-pushtest.appspot.com/o/Images%2FRas_20220606_102454.h264?alt=media&token=2f1cc132-838a-440f-8eda-86b0875f45c1";
        //"https://firebasestorage.googleapis.com/v0/b/embedded-pushtest.appspot.com/o/Images%2Fpexels-ivan-khmelyuk-7222009.mp4?alt=media&token=7896076d-76e9-4345-b699-b52f2073caef";
            //"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";





    private static final boolean VERBOSE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 앱이 첫 실행됐을 때 이곳을 수행한다.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        playerView = findViewById(R.id.player_view);
        //surfaceview = findViewById(com.google.android.exoplayer2.R.id.surface_view);

        String videoURL = getIntent().getStringExtra("videourl"); //항목 선택값 받아옴
        //Toast.makeText(getApplicationContext(), videoURL, Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        //player.setVideoSurfaceView(surfaceview);

        File file = new File(filePath);
        if (new File(filePath).exists()) {
            Log.e("exoPlayer", "File exists");
        } else {
            Log.e("exoPlayer", "File no exits");
        }

        /*String mime = "video/avc";
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(mime, 480, 320);

        try {
            MediaCodec codec = MediaCodec.createDecoderByType(mime);
            codec.configure(mediaFormat, surface, null, 0);

            codec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int inputBufferIndex = codec.dequeueInputBuffer(-1);

        if (inputBufferIndex >= 0) {
            // if API level >= 21, get input buffer here
            ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferIndex);
            //inputBuffer.put(frame);
            // fill inputBuffers[inputBufferIndex] with valid data
            codec.queueInputBuffer(inputBufferIndex, 0, 5, 0, 0);
        }

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, 0);
        if (outputBufferIndex >= 0) { // 0 이상일 경우에 인코딩/디코딩 데이터가 출력됩니다.
            ByteBuffer outputBuffer = codec.getOutputBuffer(outputBufferIndex);
            MediaFormat bufferFormat = codec.getOutputFormat(outputBufferIndex);

            codec.releaseOutputBuffer(outputBufferIndex, true);

        } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            // Subsequent data will conform to new format.
            // Can ignore if using getOutputFormat(outputBufferId)
            MediaFormat outputFormat = codec.getOutputFormat();
        }

        codec.stop();
        codec.release();*/

        Uri uri = Uri.parse(filePath);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                this,
                Util.getUserAgent(this, getString(R.string.app_name)));
        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);


        MediaItem mediaItem = MediaItem.fromUri(filePath);
        player.setMediaItem(mediaItem);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true); // 플레이어 재생

    }

    @Override
    protected void onStop() {
        super.onStop();
        playerView.setPlayer(null);
        player.release();
    }

    private MediaCodec createVideoEncoder(MediaCodecInfo codecInfo, MediaFormat format, AtomicReference<Surface> surfaceReference) throws IOException {

        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", 480, 320);
        mediaFormat = format;
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 125000);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);

        MediaCodec encoder = MediaCodec.createByCodecName(codecInfo.getName());
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        // Must be called before start() is.
        surfaceReference.set(encoder.createInputSurface());
        encoder.start();

        encoder.stop();
        encoder.release();

        return encoder;
    }

    private MediaMuxer createMuxer(String filePath) throws IOException {

        return new MediaMuxer(filePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

    }

    private MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {
                continue;
            }

            for (String type : codecInfo.getSupportedTypes()) {
                if (type.equalsIgnoreCase(mimeType)) {
                    Log.i("selectCodec", "SelectCodec : " + codecInfo.getName());
                    return codecInfo;
                }
            }
        }
        return null;
    }
}
