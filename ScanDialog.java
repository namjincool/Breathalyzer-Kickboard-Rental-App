package org.techtown.capston;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ScanDialog extends AppCompatActivity {
    Button scan_btn;
    Dialog dialog;
    Boolean state;
    private static CameraManager mCameraManager;
    private static ImageButton mImageButtonFlashOnOff;
    private static boolean mFlashOn = false;
    private String mCameraId;
    private Button complete;
    private EditText codeText;
    private TextView textView;
    private Button exam_bt;
    //플래시
    //ImageButton flash_btn;
    //CameraManager cameraManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //Action bar 미사용, Manifest에  theme 두번 사용하기 위함
        complete=findViewById(R.id.btn2);
        codeText=findViewById(R.id.codeText);
        complete.setFocusable(false);
        textView=findViewById(R.id.textView);
        scan_btn = findViewById(R.id.scan_btn);//굳이 필요 x
        scan_btn.setFocusable(false);
        exam_bt=findViewById(R.id.bt);
        exam_bt.setFocusable(false);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(ScanDialog.this, ScannerActivity.class);
               ScanDialog.this.startActivity(intent);
            }
        });
        String value=codeText.getText().toString();
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value.equals("01") || value.equals("02") || value.equals("03")){
                    textView.setText(value+"모델을 대여완료하였습니다");
                    finish();
                } else {
                    textView.setText(value+"모델을 대여실패하였습니다");
                }
            }
        });
        initFlashlight();
        //플래시 버튼 구동 안돼
        mImageButtonFlashOnOff = findViewById(R.id.flash_btn);
        mImageButtonFlashOnOff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mFlashOn) {
                    flashLightOff();
                } else {
                    flashLightOn();
                }
            }
        });

        exam_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //다이얼로그 바깥쪽 터치해도 창이 안 꺼지게끔
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    void initFlashlight() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(getApplicationContext(), "This device is not support camera flash.\n The app will be terminated!", Toast.LENGTH_LONG).show();
            delayedFinish();
            return;
        }

        mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        if (mCameraId == null) {
            try {
                for (String id : mCameraManager.getCameraIdList()) {
                    CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
                    Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
                    if (flashAvailable != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                        mCameraId = id;
                        break;
                    }
                }
            } catch (CameraAccessException e) {
                mCameraId = null;
                e.printStackTrace();
                return;
            }
        }
    }

    public void flashLightOn() {
        mFlashOn = true;
        try {
            mCameraManager.setTorchMode(mCameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        mImageButtonFlashOnOff.setImageResource(android.R.drawable.presence_online);
    }

    public void flashLightOff() {
        mFlashOn = false;
        //stopFlicker();
        //stopSOS();
        try {
            mCameraManager.setTorchMode(mCameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        mImageButtonFlashOnOff.setImageResource(android.R.drawable.presence_invisible);
    }
    private void delayedFinish() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }
}