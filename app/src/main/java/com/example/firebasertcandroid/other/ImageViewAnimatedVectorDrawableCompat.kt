package com.example.firebasertcandroid.other

import android.content.Context
import android.util.AttributeSet

class ImageViewAnimatedVectorDrawableCompat:androidx.appcompat.widget.AppCompatImageView {

    private var _stop:Boolean=false

    internal var stop:Boolean
        get() = _stop
        set(value) {
            _stop=value
            visibility = if (_stop)
                GONE
            else
                VISIBLE
        }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int)
            : super(context, attrs, defStyle) {

    }
    constructor(context: Context, attrs: AttributeSet)
            : super(context, attrs) {

    }
    constructor(context: Context)
            : super(context) {

    }

}