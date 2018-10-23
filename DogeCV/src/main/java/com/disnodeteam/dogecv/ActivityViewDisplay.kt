package com.disnodeteam.dogecv

import android.app.Activity
import android.content.Context
import android.view.View

/**
 * Created by guinea on 6/23/17.
 * -------------------------------------------------------------------------------------
 * Copyright (c) 2018 FTC Team 5484 Enderbots
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 * By downloading, copying, installing or using the software you agree to this license.
 * If you do not agree to this license, do not download, install,
 * copy or use the software.
 * -------------------------------------------------------------------------------------
 * This ViewDisplay displays a View over the entire screen.
 * As a singleton, you'll want to pass ActivityViewDisplay.getInstance() instead of directly instantiating it.
 */

class ActivityViewDisplay private constructor() : ViewDisplay {

    /**
     * Sets this display to be the current one in use, and starts it on the UI thread (as opposed to the robot controller thread)
     * @param context The context of the OpMode, can be obtained via hardwaremap.appContext;
     * @param view The view upon which this activity is to be displayed
     */
    override fun setCurrentView(context: Context, view: View) {
        val activity = context as Activity
        activity.runOnUiThread {
            if (main == null)
                main = activity.currentFocus
            activity.setContentView(view)
        }
    }

    override fun removeCurrentView(context: Context) {
        val activity = context as Activity
        activity.runOnUiThread { activity.setContentView(main!!.rootView) }
    }

    companion object {

        //There should only be one instance of this class, so make a static reference to it
        private var instance: ActivityViewDisplay? = null
        private var main: View? = null

        fun getInstance(): ActivityViewDisplay {
            if (instance == null) instance = ActivityViewDisplay()
            return instance
        }
    }
}
