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
        textView.setText("QR코드를 스캔해서 사용해주세요!");
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        //이 뷰를 꾸미는 것은 최후로 미루기
/*
        */
/* imagaeVeiw를 설정하고 마지막엔 this.addContentView ! *//*

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT));
*/

        /* 위에 쓴 코드를 실행시 더하는 코드 */
        this.addContentView(textView, params);
        //this.addContentView(imageView,params);
    }
}