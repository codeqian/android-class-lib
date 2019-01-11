package com.example.qzd.utildemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button musicPageBtn;
    private Context _context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context=this;
        setContentView(R.layout.activity_main);

        musicPageBtn=findViewById(R.id.musicPageBtn);
        musicPageBtn.setOnClickListener(btnClick);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent();
            switch (view.getId()){
                case R.id.musicPageBtn:
                    intent.setClass(_context, MusicActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
}
