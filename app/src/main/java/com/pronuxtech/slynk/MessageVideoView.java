package com.pronuxtech.slynk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.widget.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import com.khizar1556.mkvideoplayer.MKPlayer;

import java.io.File;


public class MessageVideoView extends AppCompatActivity {

    private static final int DOWNLOAD_VIDEO_CODE = 1000;
    //    WIDGETS
    private VideoView videoView;

    //    VARIABLES
    private String videoMessage;
    private MKPlayer mkplayer;

    //    URIs
    private Uri videoUri;
    File videoPath, directory, existPath;
    String stringFilename;
    String DIR_NAME = "Sloop";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_video_view);

        videoMessage = getIntent().getStringExtra("VideoMessagesUri");

        mkplayer = new MKPlayer(this);

        videoUri = Uri.parse(videoMessage);
        videoPath = new File(videoUri.getPath());
        stringFilename = videoPath.getName();
        directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/" + DIR_NAME + "/");
        existPath = new File(directory, File.separator + File.separator + stringFilename);

//        ===============================================
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission((Manifest.permission.WRITE_EXTERNAL_STORAGE)) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, DOWNLOAD_VIDEO_CODE);
            } else {

                if (existPath.exists()) {
                    Uri finalUri = Uri.parse(existPath.toString());
                    mkplayer.play(finalUri.toString());
                } else {
                    videoDownloading();
                    mkplayer.play(videoMessage);
                }

            }
        } else {

            if (existPath.exists()) {
                Uri finalUri = Uri.parse(existPath.toString());
                mkplayer.play(finalUri.toString());
            } else {
                videoDownloading();
                mkplayer.play(videoMessage);
            }

        }
//        ===============================================


        mkplayer.setPlayerCallbacks(new MKPlayer.playerCallbacks() {
            @Override
            public void onNextClick() {
                mkplayer.forward(0.01f);
            }

            @Override
            public void onPreviousClick() {
                mkplayer.forward(-0.01f);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mkplayer.stop();
        mkplayer.pause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case DOWNLOAD_VIDEO_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (existPath.exists()) {
                        Uri finalUri = Uri.parse(existPath.toString());
                        mkplayer.play(finalUri.toString());
                    } else {
                        videoDownloading();
                        mkplayer.play(videoMessage);
                    }

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void videoDownloading() {


        directory = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                .getAbsolutePath() + "/" + DIR_NAME + "/");

        if (!directory.exists()) {
            directory.mkdir();
            Log.d("directCre", "dir created for first time");
        }

        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(videoMessage);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(stringFilename)
                .setMimeType("video/*")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES,
                        File.separator + DIR_NAME + File.separator + stringFilename);

        dm.enqueue(request);

    }
}
