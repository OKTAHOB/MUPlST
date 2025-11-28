package com.example.playlistmaker.features.player.presentation.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val imageRect = RectF()

    private var isPlaying = false
    private var onStateChangeListener: ((Boolean) -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            0, 0
        ).apply {
            try {
                val playDrawableId = getResourceId(R.styleable.PlaybackButtonView_playDrawable, 0)
                val pauseDrawableId = getResourceId(R.styleable.PlaybackButtonView_pauseDrawable, 0)

                if (playDrawableId != 0) {
                    playBitmap = drawableToBitmap(playDrawableId)
                }

                if (pauseDrawableId != 0) {
                    pauseBitmap = drawableToBitmap(pauseDrawableId)
                }
            } finally {
                recycle()
            }
        }
    }

    private fun drawableToBitmap(drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
        
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        val size = minOf(w, h).toFloat()
        val left = (w - size) / 2f
        val top = (h - size) / 2f
        
        imageRect.set(left, top, left + size, top + size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val bitmap = if (isPlaying) pauseBitmap else playBitmap
        bitmap?.let {
            canvas.drawBitmap(it, null, imageRect, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                toggleState()
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        onStateChangeListener?.invoke(isPlaying)
        return true
    }

    private fun toggleState() {
        isPlaying = !isPlaying
        invalidate()
    }

    fun setState(playing: Boolean) {
        if (isPlaying != playing) {
            isPlaying = playing
            invalidate()
        }
    }

    fun setOnStateChangeListener(listener: (Boolean) -> Unit) {
        onStateChangeListener = listener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = 100.dpToPx()
        
        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)
        
        setMeasuredDimension(width, height)
    }

    private fun Int.dpToPx(): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}
