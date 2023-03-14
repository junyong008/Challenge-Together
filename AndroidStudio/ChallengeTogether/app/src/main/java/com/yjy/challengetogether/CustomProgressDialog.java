/* ProgressDialog.java */
package com.yjy.challengetogether;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class CustomProgressDialog extends Dialog
{
    public CustomProgressDialog(Context context)
    {
        super(context);

        // 다이얼 로그 제목을 안보이게...
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 바탕 클릭해도 취소 안되게
        setCancelable(false);

        setContentView(R.layout.dialog_progress);
    }
}