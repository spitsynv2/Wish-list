package com.example.prm_projekt_2_s22599.fragments

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.prm_projekt_2_s22599.model.PathWithSettings

class PaintView(context: Context, attrs: AttributeSet): View(context, attrs) {

    var color: Int = Color.BLACK
    var size: Float = 20f
    var background: Bitmap? = null
        set(value) {
            field = value
            invalidate()
        }

    private val paths = mutableListOf<PathWithSettings>()
    private val defaultPaint = Paint()
    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    override fun onDraw(canvas: Canvas?) {
        drawBackground(canvas)
        drawPaths(canvas)
    }

    private fun drawBackground(canvas: Canvas?) {
        background?.let {
            val rect = Rect(0,0,width,height)
            canvas?.drawBitmap(it,null,rect,defaultPaint)
        }

    }

    private fun drawPaths(canvas: Canvas?) {
        paths.forEach{
            paint.apply {
                strokeWidth = it.size
                color = it.color
            }
            canvas?.drawPath(it.path,paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return true

        when(event.action){
            MotionEvent.ACTION_DOWN ->{
                PathWithSettings(color = color,size = size).let {
                    it.path.moveTo(event.x,event.y)
                    it.path.lineTo(event.x,event.y)
                    paths.add(it)
                }
            }
            MotionEvent.ACTION_MOVE ->{
                paths.last().path.lineTo(event.x,event.y)
            }

        }

        invalidate()


        return true
    }

    fun generateBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        drawBackground(canvas)
        drawPaths(canvas)

        return bitmap
    }


}