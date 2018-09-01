package org.firstinspires.ftc.robotcontroller.teamcode.activites

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
            with(circlePaint) {
                isAntiAlias = true
                color = Color.BLUE
                style = Paint.Style.STROKE
                strokeJoin = Paint.Join.MITER
                strokeWidth = 4f
            }
            with(mPaint) {
                isAntiAlias = true
                isDither = true
                color = Color.BLACK
                style = Paint.Style.STROKE
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
                strokeWidth = 12f
            }
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            mCanvas = Canvas(mBitmap)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            with(canvas) {
                drawBitmap(mBitmap, 0f, 0f, mBitmapPaint)
                drawPath(mPath, mPaint)
                drawPath(circlePath, circlePaint)
            }
        }

        private fun touchStart(x: Float, y: Float) {
            mPath.reset()
            mPath.moveTo(x, y)
            if (mX == -1f || mY == -1f) {
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
