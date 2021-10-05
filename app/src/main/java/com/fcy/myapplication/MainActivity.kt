package com.fcy.myapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.fcy.apt_annotation.*

private const val TAG = "MainActivity"

@Test(22)
@SuppressLint("NonConstantResourceId")
@BindLayout(R.layout.activity_main)
public class MainActivity : AppCompatActivity() {
    @JvmField
    @BindView(id = R.id.tv)
    var tv: TextView? = null

    @JvmField
    @BindView(id = R.id.iv)
    var iv: ImageView? = null

    @BindClick(ids = [R.id.tv, R.id.iv])
    fun onTvClick(v: View) {
        Toast.makeText(this, "woshi ${v.javaClass.canonicalName}", Toast.LENGTH_LONG).show()
        Log.d(TAG, "onTvClick: ${v.accessibilityClassName}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BindUtils.bind(this)
    }
}