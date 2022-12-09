package com.example.test

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.*
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    private val imageHeight = 14.dp

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        textView.text = generateSpan()
    }

    private fun nameColorSpan() = ForegroundColorSpan(Color.parseColor("#A4A9B3"))
    private fun textColorSpan() = ForegroundColorSpan(Color.parseColor("#181E25"))
    private fun textSize() = AbsoluteSizeSpan(12, true)
    private fun bold() = StyleSpan(Typeface.BOLD)

    private fun generateSpan(): CharSequence = SpannableStringBuilder().apply {
        appendText(this, "心爱的小摩托", arrayListOf(nameColorSpan(), textSize()))
        appendDivide(this)
        appendImage(this, R.drawable.ic_nv)
        appendDivide(this)
        appendCustom(this, R.drawable.dengji, R.drawable.bg_imagespan_blue, "54")
        appendDivide(this)
        appendCustom(this, R.drawable.ic_nv, R.drawable.bg_imagespan, "test")
        appendDivide(this)
        appendCustom(this, R.drawable.dengji, R.drawable.bg_imagespan_blue, "长老")
        appendDivide(this)
        appendCustom(this, R.drawable.i_1, R.drawable.bg_level, "长老等级")
        appendDivide(this)
//        appendCustom(this, R.drawable.dengji, R.drawable.zhanglao, "长老")
        appendDivide(this)
        appendText(
            this, "请注意：绿色框住部门需要用一整个TextVew实现（从心爱的小摩托到可以换行的文本）",
            arrayListOf(textColorSpan(), textSize(), bold())
        )
    }

    private fun appendText(spannableStringBuilder: SpannableStringBuilder, text: String, characterStyles: List<CharacterStyle>) {
        val start = spannableStringBuilder.length
        val end = start + text.length
        spannableStringBuilder.append(text)
        characterStyles.forEach {
            spannableStringBuilder.setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
    }

    private fun appendDivide(spannableStringBuilder: SpannableStringBuilder) {
        spannableStringBuilder.append("  ")
    }

    private fun appendImage(spannableStringBuilder: SpannableStringBuilder, gender: Int) {
        val start = spannableStringBuilder.length
        val drawable = AppCompatResources.getDrawable(this, gender)?.apply {
            setBounds(0, 0, imageHeight, imageHeight)
        } ?: return
        spannableStringBuilder.append(" ")
        val imageSpan = QCenterAlignImageSpan(drawable)
        spannableStringBuilder.setSpan(imageSpan, start, start + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }

    private fun appendCustom(spannableStringBuilder: SpannableStringBuilder, iconRes: Int, bgRes: Int, text: String) {
        val textView = TextView(this)
        textView.setTextColor(ContextCompat.getColor(textView.context, android.R.color.white))
        textView.setBackgroundResource(bgRes)
        val drawable: Drawable? = ContextCompat.getDrawable(textView.context, iconRes)
        drawable?.setBounds(0, 0, 12.dp, 12.dp)
        textView.setCompoundDrawables(drawable, null, null, null)
        textView.compoundDrawablePadding = 5
        textView.setPadding(2.dp, 0, 6.dp, 0)
        textView.textSize = 8F
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.text = text
        textView.typeface = Typeface.DEFAULT_BOLD

        val tagBitmap = textView.toBitmap(14.dp) ?: return
        val start = spannableStringBuilder.length
        spannableStringBuilder.append(" ")
        spannableStringBuilder.setSpan(
            QCenterAlignImageSpan(this, tagBitmap),
            start,
            start + 1,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

fun View.toBitmap(height: Int): Bitmap? {
    this.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        if (height == 0) View.MeasureSpec.makeMeasureSpec(
            0,
            View.MeasureSpec.UNSPECIFIED
        ) else View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
    )
    this.layout(0, 0, this.measuredWidth, this.measuredHeight)
    val bitmap = Bitmap.createBitmap(this.measuredWidth, this.measuredHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}

val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

val Int.dp
    get() = this.toFloat().dp.toInt()

class QCenterAlignImageSpan : ImageSpan {
    constructor(drawable: Drawable) : super(drawable)

    constructor(context: Context, bitmap: Bitmap) : super(context, bitmap)

    private var mDrawableRef: WeakReference<Drawable>? = null

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val b: Drawable = getCachedDrawable() ?: return
        canvas.save()

        val transY = top + (bottom - top) / 2 - b.bounds.height() / 2

        canvas.translate(x, transY.toFloat())
        b.draw(canvas)
        canvas.restore()
    }

    private fun getCachedDrawable(): Drawable? {
        val wr: WeakReference<Drawable>? = mDrawableRef
        var d: Drawable? = null
        if (wr != null) {
            d = wr.get()
        }
        if (d == null) {
            d = drawable
            mDrawableRef = WeakReference(d)
        }
        return d
    }
}