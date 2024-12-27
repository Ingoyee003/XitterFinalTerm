package com.xetterfb.xetterchat;

import android.os.Handler;
import android.view.View;

public class DoubleClick implements View.OnClickListener {
    private static final long DOUBLE_CLICK_TIME_DELTA = 300; // Milliseconds
    private final DoubleClickListener listener;
    private final Handler handler = new Handler();
    private boolean isSingleClick = true;

    public DoubleClick(DoubleClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (isSingleClick) {
            isSingleClick = false;
            handler.postDelayed(() -> {
                if (isSingleClick) {
                    listener.onSingleClick(v);
                }
                isSingleClick = true;
            }, DOUBLE_CLICK_TIME_DELTA);
        } else {
            isSingleClick = true;
            handler.removeCallbacksAndMessages(null);
            listener.onDoubleClick(v);
        }
    }
}
