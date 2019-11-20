package com.brndl.hourplan.Views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toRectF

class CameraTextView(context: Context, val rect: Rect?) : View(context) {

    private val paint: Paint = Paint().apply {
        setARGB(80, 255, 255, 255)
        style = Paint.Style.FILL

    }
    private val strokePaint: Paint = Paint().apply {
        setARGB(255, 255, 255, 255)
        style = Paint.Style.STROKE
        strokeWidth = 2f

    }

    var visible = true
    var highlighted = false
    set(value) {
        field = value
        if(value) {
            paint.setARGB(150, 50, 150, 255)
            strokePaint.setARGB(255, 50, 200, 255)
            strokePaint.strokeWidth = 5f
            invalidate()
        } else {
            paint.setARGB(80, 255, 255, 255)
            strokePaint.setARGB(255, 255, 255, 255)
            strokePaint.strokeWidth = 2f
            invalidate()
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (visible) {
            if (rect != null) {
                canvas?.drawRect(rect, paint)
                canvas?.drawRoundRect(rect.toRectF(), 4f, 4f, strokePaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                if (rect != null)
                    if (rect.top < event.y && event.y < rect.bottom) {
                        if (rect.right > event.x && event.x > rect.left) {
                            callOnClick()
                            return true
                        }
                    }
            }
            MotionEvent.ACTION_DOWN ->{
                if (rect != null)
                    if (rect.top < event.y && event.y < rect.bottom) {
                        if (rect.right > event.x && event.x > rect.left) {
                            return true
                        }
                    }
                return false
            }
        }
        return super.onTouchEvent(event)
    }
}