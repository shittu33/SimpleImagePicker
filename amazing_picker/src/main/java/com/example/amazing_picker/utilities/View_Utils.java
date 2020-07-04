package com.example.amazing_picker.utilities;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;

/**
 * Created by Abu Muhsin on 12/11/2018.
 */

public class View_Utils {
    private MotionEvent e;

    public View_Utils(MotionEvent event) {
        this.e = event;
    }

    public static void hideView_with_ZoomIn(ViewGroup parent) {
        RevealView_withScale(parent, false);
    }

    public static void ShowView_with_ZoomOut(ViewGroup parent) {
        RevealView_withScale(parent, true);
    }

    private static void RevealView_withScale(ViewGroup parent, boolean visibility) {
        TransitionSet set = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            set = new TransitionSet()
                    .addTransition(new Scale(0.84f))
//                    .addTransition(new com.transitionseverywhere.Fade())
                    .setInterpolator(visibility ? new LinearOutSlowInInterpolator() :
                            new FastOutLinearInInterpolator());
            TransitionManager.beginDelayedTransition(parent, set);
        }
    }
}
