package org.firstinspires.ftc.robotcontroller.teamcode

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.View


class AutoControlActivity : Activity() {

    internal val dv by lazy { DrawingView(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dv)
    }

    inner class DrawingView(internal var context: Context) : View(context) {

        private val mPaint: Paint = Paint()
        private var mBitmap: Bitmap? = null
        private var mCanvas: Canvas? = null
        private val mPath = Path()
        private val mBitmapPaint = Paint(Paint.DITHER_FLAG)
        private val circlePaint = Paint()
        private val circlePath = Path()

        private var mX = -1f
        private var mY = -1f

        private val touchTolerance = 4f

        init {
            circlePaint.isAntiAlias = true
            circlePaint.color = Color.BLUE
            circlePaint.style = Paint.Style.STROKE
            circlePaint.strokeJoin = Paint.Join.MITER
            circlePaint.strokeWidth = 4f
            mPaint.isAntiAlias = true
            mPaint.isDither = true
            mPaint.color = Color.BLACK
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeJoin = Paint.Join.ROUND
            mPaint.strokeCap = Paint.Cap.ROUND
            mPaint.strokeWidth = 12f
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            mCanvas = Canvas(mBitmap)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawBitmap(mBitmap, 0f, 0f, mBitmapPaint)
            canvas.drawPath(mPath, mPaint)
            canvas.drawPath(circlePath, circlePaint)
        }

        private fun touchStart(x: Float, y: Float) {
            mPath.reset()
            mPath.moveTo(x, y)
            if(mX == -1f || mY == -1f) {
                mX = x
                mY = y
            }
        }

        private fun touchMove(x: Float, y: Float) {
            val dx = Math.abs(x - mX)
            val dy = Math.abs(y - mY)
            if (dx >= touchTolerance || dy >= touchTolerance) {
                mPath.reset()
                mPath.moveTo(mX, mY)
                mPath.lineTo(x, y)
                circlePath.reset()
                circlePath.addCircle(mX, mY, 30f, Path.Direction.CW)
            }
        }

        private fun touchUp(x: Float, y: Float) {
            mPath.lineTo(x, y)
            mCanvas!!.drawPath(mPath, mPaint)
            mCanvas!!.drawPath(circlePath, circlePaint)
            mPath.reset()
            mX = x
            mY = y
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val x = event.x
            val y = event.y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStart(x, y)
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    touchMove(x, y)
                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    touchUp(x, y)
                    invalidate()
                }
            }

            return true
        }
    }
}
