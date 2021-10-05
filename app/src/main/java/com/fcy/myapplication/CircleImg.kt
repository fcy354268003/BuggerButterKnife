package com.fcy.myapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.withTranslation
import java.net.URL

private const val TAG = "CircleImg"

class CircleImg(context: Context, attributeSet: AttributeSet? = null) :
    AppCompatImageView(context, attributeSet, 0) {
//    private val handler = Handler(Looper.getMainLooper())
    var rectf: RectF
    var radius: Float = 0F
    var bitmap: Bitmap? = null
    var imgUrl: String? = null
        set(value) {
            Log.d(TAG, ": $value")
            Thread{
                kotlin.runCatching {
                    bitmap = BitmapFactory.decodeStream(
                        URL(value).openStream()
                    )
                    Log.d(TAG, ": $bitmap")
                    field = value
                    handler.post {
                        invalidate()
                    }
                }.exceptionOrNull()?.printStackTrace()
            }.start()
        }
    val path:Path

    init {
        val obtainStyledAttributes =
            context.obtainStyledAttributes(attributeSet, R.styleable.CircleImg)
        radius = obtainStyledAttributes.getDimension(R.styleable.CircleImg_radius, 0F)
        imgUrl = obtainStyledAttributes.getString(R.styleable.CircleImg_imgUrl)
        rectf = RectF().apply {
            set(
                width() / 2 - radius,
                height() / 2 - radius,
                width() / 2 + radius,
                height() / 2 + radius
            )
        }
        obtainStyledAttributes.recycle()
        path = Path().apply {
            addCircle(width / 2F, height / 2F, radius, Path.Direction.CW)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        Log.d(TAG, "onDraw: $radius $bitmap $imgUrl")
        super.onDraw(canvas)
        // 剪切canvas为圆形
        canvas?.withTranslation(width / 2F, height / 2F) {
            save()
            clipPath(path)
            bitmap?.also {
                drawBitmap(it, null, rectf, null)
            }
            restore()
        }

    }
}