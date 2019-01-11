package com.example.qzd.utildemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import util.FileUtil;

public class MusicActivity extends AppCompatActivity {
    private Button selectBtn,playBtn,pauseBtn,stopBtn;
    private TextView info_t;
    private String filePath;

    private final String TAG="MUSIC PAGE LOGCAT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        info_t=findViewById(R.id.info_t);
        selectBtn=findViewById(R.id.selectBtn);
        playBtn=findViewById(R.id.playBtn);
        pauseBtn=findViewById(R.id.pauseBtn);
        stopBtn=findViewById(R.id.stopBtn);
        selectBtn=findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(btnClick);
        playBtn.setOnClickListener(btnClick);
        pauseBtn.setOnClickListener(btnClick);
        stopBtn.setOnClickListener(btnClick);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()){
                case R.id.selectBtn://选择本地文件
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("audio/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(intent, "Select a File"), 0x1);
                    break;
                case R.id.playBtn:
                    break;
                case R.id.pauseBtn:
                    break;
                case R.id.stopBtn:
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 监听文件选择
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode:" + requestCode+"-"+"resultCode:"+resultCode);
        if (requestCode == 0x1 && resultCode == Activity.RESULT_OK && data!=null) {
            try{
                Uri audioUri = data.getData();
                filePath= FileUtil.getPath(this,audioUri);
                Log.d(TAG, "path:" + filePath);
                if(!filePath.equals("")) {
                    info_t.setText(filePath);
                }
            }catch (Exception e){
                Log.d(TAG,e.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
