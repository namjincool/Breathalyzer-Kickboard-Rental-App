package org.techtown.capston;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class Loding extends Dialog {
    public Loding(Context context) {
        super(context);
        /*타이틀 없이 로딩뷰를 띄우기*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading);

    }
}