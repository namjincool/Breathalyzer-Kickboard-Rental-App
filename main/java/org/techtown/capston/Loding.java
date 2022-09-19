package org.techtown.capston;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class Loding extends Dialog {
    public Loding(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading);
    }
}