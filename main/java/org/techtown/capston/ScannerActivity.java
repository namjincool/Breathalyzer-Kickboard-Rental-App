package org.techtown.capston;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Timer;
import java.util.TimerTask;


public class ScannerActivity extends AppCompatActivity {
    private IntentIntegrator qrScan;
    Timer timer = new Timer();
    Loding loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.loading);
        qrScan = new IntentIntegrator(this);
        qrScan.setCaptureActivity(CaptureForm.class);
        qrScan.setOrientationLocked(false); // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
        qrScan.initiateScan();
        loading = new Loding(this);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //http://www.naver.com
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                // todo
            } else {
                if (result.getContents().equals("https://www.naver.com")) {
                    Toast.makeText(this, "대여성공", Toast.LENGTH_LONG).show();
                    loading.show();
                    loading.setCanceledOnTouchOutside(false);//주변터치 방지
                    loading.setCancelable(false);//뒤로가기 방지
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            loading.dismiss();
                            finish();
                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                        }
                    };
                    timer.schedule(task,3000);
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }
}