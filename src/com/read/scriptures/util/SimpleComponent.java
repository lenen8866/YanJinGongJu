package com.read.scriptures.util;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.binioter.guideview.Component;
import com.read.scriptures.R;

public class SimpleComponent implements Component {

    @Override
    public View getView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.guide_service_layout, null);
    }

    @Override
    public int getAnchor() {
        return Component.ANCHOR_TOP;
    }

    @Override
    public int getFitPosition() {
        return Component.FIT_END;
    }

    @Override
    public int getXOffset() {
        return 0;
    }

    @Override
    public int getYOffset() {
        return 5;
    }
}
