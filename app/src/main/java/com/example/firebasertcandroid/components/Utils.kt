package com.example.firebasertcandroid.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch
import javax.inject.Inject

@InstallIn(SingletonComponent::class)
@Module
class Utils @Inject constructor( private val appContext: Context)
{

    fun textToImageView( paramText:String?, paramImageView: ImageView?, scale:Float=0.8f)
    {

        paramImageView
            ?.let {imageView->
                paramImageView.findViewTreeLifecycleOwner()
                    ?.lifecycle
                    ?.coroutineScope
                    ?.launch {

                        paramText
                            ?.let { text->

                                TextView(appContext)
                                    .let { textView ->

                                        textView.gravity= Gravity.CENTER

                                        var padding=0

                                        fun setNewPaddingToTextView()
                                        {

                                            textView.setPadding(
                                                padding,
                                                padding,
                                                padding,
                                                padding
                                            )

                                            imageView.setImageBitmap(
                                                drawToBitmap(textView,0,0)
                                            )

                                        }

                                        fun calcPadding(width:Int,height:Int):Int
                                        {
                                            return (kotlin.math.min(
                                                width,
                                                height
                                            )/2*(1f-scale)).toInt()
                                        }

                                        textView.text=text

                                        padding = calcPadding(
                                            imageView.width,
                                            imageView.height
                                        )

                                        if (padding>0){

                                            setNewPaddingToTextView()

                                        }else{

                                            imageView
                                                .viewTreeObserver
                                                .addOnGlobalLayoutListener(
                                                    object : ViewTreeObserver.OnGlobalLayoutListener {
                                                        override fun onGlobalLayout() {

                                                            imageView.viewTreeObserver
                                                                .removeOnGlobalLayoutListener(this)

                                                            padding = calcPadding(
                                                                imageView.width,
                                                                imageView.height
                                                            )

                                                            setNewPaddingToTextView()

                                                        }

                                                    }
                                                )

                                        }


                                    }

                            }

                    }

        }

    }

    @Suppress("DEPRECATION")
    private fun drawToBitmap(viewToDrawFrom: View, w: Int, h: Int): Bitmap?
    {
        var width = w
        var height = h
        val wasDrawingCacheEnabled = viewToDrawFrom.isDrawingCacheEnabled
        if (!wasDrawingCacheEnabled) viewToDrawFrom.isDrawingCacheEnabled = true
        if (width <= 0 || height <= 0) {
            if (viewToDrawFrom.width <= 0 || viewToDrawFrom.height <= 0) {
                viewToDrawFrom.measure(
                    View.MeasureSpec.makeMeasureSpec(
                        0,
                        View.MeasureSpec.UNSPECIFIED
                    ), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                width = viewToDrawFrom.measuredWidth
                height = viewToDrawFrom.measuredHeight
            }
            if (width <= 0 || height <= 0) {
                val bmp = viewToDrawFrom.drawingCache
                val result = if (bmp == null) null else Bitmap.createBitmap(bmp)
                if (!wasDrawingCacheEnabled) viewToDrawFrom.isDrawingCacheEnabled = false
                return result
            }
            viewToDrawFrom.layout(0, 0, width, height)
        } else {
            viewToDrawFrom.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            )
            viewToDrawFrom.layout(0, 0, viewToDrawFrom.measuredWidth, viewToDrawFrom.measuredHeight)
        }
        val drawingCache = viewToDrawFrom.drawingCache
        val bmp = ThumbnailUtils.extractThumbnail(drawingCache, width, height)
        val result = if (bmp == null || bmp != drawingCache) bmp else Bitmap.createBitmap(bmp)
        if (!wasDrawingCacheEnabled) viewToDrawFrom.isDrawingCacheEnabled = false
        return result
    }

    fun animateLoadingView(view: ImageView, stop:Boolean=false)
    {

        if (view is com.example.firebasertcandroid.other.ImageViewAnimatedVectorDrawableCompat)
        {

            view.stop=stop

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {

                if (!view.stop)
                {

                    when (val drawable = view.drawable)
                    {

                        is AnimatedVectorDrawableCompat ->
                        {
                            view.visibility=View.VISIBLE

                            drawable.start()

                            val cycle=object : Animatable2Compat.AnimationCallback()
                            {
                                override fun onAnimationEnd(param: Drawable?) {
                                    super.onAnimationEnd(param)
                                    drawable.unregisterAnimationCallback(this)
                                    if (!view.stop)
                                        animateLoadingView(view)
                                    else
                                        view.setImageBitmap(null)
                                }
                            }

                            drawable.registerAnimationCallback(cycle)

                        }

                        is AnimatedVectorDrawable ->
                        {

                            view.visibility=View.VISIBLE

                            drawable.start()

                            val cycle = object : Animatable2.AnimationCallback()
                            {
                                override fun onAnimationEnd(param: Drawable?) {
                                    super.onAnimationEnd(param)
                                    drawable.unregisterAnimationCallback(this)
                                    if (!view.stop)
                                        animateLoadingView(view)
                                    else
                                        view.setImageBitmap(null)
                                }
                            }

                            drawable.registerAnimationCallback(cycle)

                        }

                    }

                }

            }

        }

    }

    fun showToast(text: String)
    {

        Toast.makeText(appContext,text, Toast.LENGTH_LONG).show()

    }


    fun showToast(id: Int)
    {

        try
        {
            Toast.makeText(appContext,appContext.getString(id), Toast.LENGTH_LONG).show()
        }
        catch (e:Exception)
        {
            showToast(e.toString())
        }

    }


}