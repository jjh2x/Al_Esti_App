package com.example.al_esti_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    Button startBtn, chauBtn, emerBtn;
    TextView penalView, BAC_View;
    GradientDrawable BAC_Back;
    Socket socket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = (Button) findViewById(R.id.startBtn);
        chauBtn = (Button) findViewById(R.id.chauBtn);
        emerBtn = (Button) findViewById(R.id.emerBtn);
        BAC_View = (TextView) findViewById(R.id.tempaView);
        BAC_Back = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.oval);


        /*.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog2, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("처벌");
                dlg.setIcon(R.drawable.siren);
                dlg.setView(dialogView);
                dlg.setPositiveButton("확인", null);
                dlg.show();
            }
        });*/

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyClientTask myClientTask = new MyClientTask();
                myClientTask.execute();
            }
        });

        chauBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=%EB%8C%80%EB%A6%AC%EC%9A%B4%EC%A0%84"));
                startActivity(intent);
            }
        });

        emerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:119");
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });

        /* 알코올 일정 수치 이상일 시, 다이얼로그 출력.
        .setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog1, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("경고");
                dlg.setIcon(R.drawable.siren);
                dlg.setView(dialogView);
                dlg.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                                intent.putExtra(SearchManager.QUERY, "대리운전");
                                startActivity(intent);
                            }
                        });
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });*/
    }

    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        String response = "";

        @Override
        protected Void doInBackground(Void... arg0) {
            Socket socket = null;
            try {
                socket = new Socket("192.168.0.80", 8888);

                //송신 없음

                //수신
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];
                int bytesRead;
                InputStream inputStream = socket.getInputStream();

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException:" + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            BAC_View.setText(response);
            double k = Double.parseDouble(response);

            //BAC(혈중알콜농도)가 0.03을 초과하면 하게 되는 동작.
            if(k >= 0.03) {
                // BAC 표시하는 텍스트 밑에 깔린 원판(TextView - drawable) 색깔 변경.
                Drawable roundDrawable = getResources().getDrawable(R.drawable.oval);
                roundDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    BAC_View.setBackgroundDrawable(roundDrawable);
                } else {
                    BAC_View.setBackground(roundDrawable);
                }

                //모터 동작 중지.
                //대리운전 링크 연결.
            }
            super.onPostExecute(result);
        }
    }
}


