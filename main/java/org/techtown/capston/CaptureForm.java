package org.techtown.capston;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.journeyapps.barcodescanner.CaptureActivity;

public class CaptureForm extends CaptureActivity {

    //플래시
    //ImageButton flash_btn;
    //CameraManager cameraManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        /* TextVeiw를 설정하고 마지막엔 this.addContentView ! */
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT));
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setText("QR코드 스캔하고 대여하기");
        textView.setTextSize(15);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        //우리가 만든 뷰에서 값 가지고와보기
/*
        */
/* imagaeVeiw를 설정하고 마지막엔 this.addContentView ! *//*

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT));
*/

        /* this.addContentView ! */
        this.addContentView(textView, params);
        //this.addContentView(imageView,params);
    }
}