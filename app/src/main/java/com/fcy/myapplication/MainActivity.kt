package com.fcy.myapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.fcy.apt_annotation.BindClick
import com.fcy.apt_annotation.BindLayout
import com.fcy.apt_annotation.BindUtils
import com.fcy.apt_annotation.BindView

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
        println(v.accessibilityClassName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BindUtils.bind(this)
        iv?.setOnClickListener {
            Toast.makeText(this, "woshi ${iv?.javaClass?.canonicalName}", Toast.LENGTH_LONG).show()
        }
    }
}