package org.techtown.capston;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScannerActivity extends AppCompatActivity {
    TextView textView;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private Button enter_code;
    private static CameraManager mCameraManager;
    private static ImageButton mImageButtonFlashOnOff;
    private static boolean mFlashOn = false;
    private String mCameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        textView = findViewById(R.id.qrcode_text); //아래글자 나오는 뷰코드 성현

        surfaceView = findViewById(R.id.surfaceView);

        barcodeDetector = new BarcodeDetector.Builder(getApplicationContext()).setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(getApplicationContext(),barcodeDetector).setRequestedPreviewSize(640, 480).build();

       // initFlashlight();
        textView.setFocusable(false);
        surfaceView.setFocusable(false);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(surfaceHolder);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }
            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcode = detections.getDetectedItems();
                if(qrcode.size() != 0) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (qrcode.valueAt(0).displayValue.equals("https://www.naver.com")) {
                                //setText("대여성공");
                                Toast.makeText(getApplicationContext(),"대여완료 ", Toast.LENGTH_LONG).show();
                            } else {
                                //textView.setText("대여실패");
                                Toast.makeText(getApplicationContext(),"대여실패 ", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
       /* enter_code = findViewById(R.id.enter_code);
        enter_code.setFocusable(false);
        enter_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

      /*  // button turn on off
        mImageButtonFlashOnOff = findViewById(R.id.ibFlashOnoff);
        mImageButtonFlashOnOff.setFocusable(false);
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
    }
    public void openInputCode() {
        Intent intent = new Intent(ScannerActivity.class,ScanDialog.class);
        startActivity(intent);
        //Dialog dialog = new Dialog(ScannerActivity.this);
        //dialog.setContentView(R.layout.activity_scan_dialog);
        //EditText codeText = (EditText) findViewById(R.id.codeText);

        //WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        //int w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,600,getResources().getDisplayMetrics());
        //params.height = w;
        //params.width = WindowManager.LayoutParams.MATCH_PARENT;

        //dialog.getWindow().getAttributes();
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //dialog.show();
    }*/

/*    void initFlashlight() {
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
        }, 3000);*/
    }
}
