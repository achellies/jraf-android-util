/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 * 
 * Copyright (C) 2013 Benoit 'BoD' Lubek (BoD@JRAF.org)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jraf.android.util.ui.fitsize;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Chronometer;

/**
 * A Chronometer that 'fits' its container by automatically using the biggest possible font size.
 */
public class FitSizeChronometer extends Chronometer {
    public FitSizeChronometer(Context context) {
        super(context);
        init();
    }

    public FitSizeChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FitSizeChronometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // Set the layer type to software to avoid a "Font size too large to fit in cache." problem.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) setSoftwareLayerType();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setSoftwareLayerType() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void resetTextSize(int width, int height) {
        if (width == 0 || height == 0) return;

        int w = width - getPaddingLeft() - getPaddingRight();
        int h = height - getPaddingTop() - getPaddingBottom();

        int textSize = h;
        Rect bounds = new Rect();
        int textWidth = -1;
        int textHeight = -1;

        do {
            if (textWidth > w) {
                textSize *= (float) w / textWidth;
            } else if (textHeight > h) {
                textSize *= (float) h / textHeight;
            }
            textSize--;
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            measureText(getText(), bounds);
            textWidth = bounds.width();
            textHeight = bounds.height();
        } while (textWidth > w || textHeight > h);
    }

    private void measureText(CharSequence text, Rect bounds) {
        StaticLayout tempLayout = new StaticLayout(text, getPaint(), Integer.MAX_VALUE, android.text.Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
        bounds.left = (int) tempLayout.getLineLeft(0);
        bounds.right = (int) tempLayout.getLineRight(0);
        bounds.top = tempLayout.getLineTop(0);
        bounds.bottom = tempLayout.getLineBottom(0);
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        resetTextSize(getWidth(), getHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            resetTextSize(w, h);
        }
    }
}
