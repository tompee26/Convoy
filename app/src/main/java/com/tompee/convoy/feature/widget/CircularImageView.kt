package com.tompee.convoy.feature.widget


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.tompee.convoy.R

/**
 * Created by Mikhael LOPEZ on 09/10/2015.
 */
class CircularImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        AppCompatImageView(context, attrs, defStyleAttr) {

    // Properties
    private var borderWidth: Float = 0.toFloat()
    private var canvasSize: Int = 0
    private var shadowRadius: Float = 0.toFloat()
    private var shadowColor = Color.BLACK

    // Object used to draw
    private var image: Bitmap? = null
    private var mDrawable: Drawable? = null
    private var paint: Paint? = null
    private var paintBorder: Paint? = null

    init {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        // Init paint
        paint = Paint()
        paint!!.isAntiAlias = true

        paintBorder = Paint()
        paintBorder!!.isAntiAlias = true

        // Load the styled attributes and set their properties
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CircularImageView, defStyleAttr, 0)

        // Init Border
        if (attributes.getBoolean(R.styleable.CircularImageView_civ_border, true)) {
            val defaultBorderSize = DEFAULT_BORDER_WIDTH * getContext().resources.displayMetrics.density
            setBorderWidth(attributes.getDimension(R.styleable.CircularImageView_civ_border_width, defaultBorderSize))
            setBorderColor(attributes.getColor(R.styleable.CircularImageView_civ_border_color, Color.WHITE))
        }

        // Init Shadow
        if (attributes.getBoolean(R.styleable.CircularImageView_civ_shadow, false)) {
            shadowRadius = DEFAULT_SHADOW_RADIUS
            drawShadow(attributes.getFloat(R.styleable.CircularImageView_civ_shadow_radius, shadowRadius), attributes.getColor(R.styleable.CircularImageView_civ_shadow_color, shadowColor))
        }
    }
    //endregion

    //region Set Attr Method
    fun setBorderWidth(borderWidth: Float) {
        this.borderWidth = borderWidth
        requestLayout()
        invalidate()
    }

    fun setBorderColor(borderColor: Int) {
        if (paintBorder != null)
            paintBorder!!.color = borderColor
        invalidate()
    }

    fun addShadow() {
        if (shadowRadius == 0f)
            shadowRadius = DEFAULT_SHADOW_RADIUS
        drawShadow(shadowRadius, shadowColor)
        invalidate()
    }

    fun setShadowRadius(shadowRadius: Float) {
        drawShadow(shadowRadius, shadowColor)
        invalidate()
    }

    fun setShadowColor(shadowColor: Int) {
        drawShadow(shadowRadius, shadowColor)
        invalidate()
    }

    override fun getScaleType(): ImageView.ScaleType {
        return SCALE_TYPE
    }

    override fun setScaleType(scaleType: ImageView.ScaleType) {
        if (scaleType != SCALE_TYPE) {
            throw IllegalArgumentException(String.format("ScaleType %s not supported. ScaleType.CENTER_CROP is used by default. So you don't need to use ScaleType.", scaleType))
        }
    }
    //endregion

    //region Draw Method
    public override fun onDraw(canvas: Canvas) {
        // Load the bitmap
        loadBitmap()

        // Check if image isn't null
        if (image == null)
            return

        if (!isInEditMode) {
            canvasSize = canvas.width
            if (canvas.height < canvasSize) {
                canvasSize = canvas.height
            }
        }

        // circleCenter is the x or y of the view's center
        // radius is the radius in pixels of the cirle to be drawn
        // paint contains the shader that will texture the shape
        val circleCenter = (canvasSize - borderWidth * 2).toInt() / 2
        // Draw Border
        canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter + borderWidth - (shadowRadius + shadowRadius / 2), paintBorder!!)
        // Draw CircularImageView
        canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter - (shadowRadius + shadowRadius / 2), paint!!)
    }

    private fun loadBitmap() {
        if (this.mDrawable === getDrawable())
            return

        this.mDrawable = getDrawable()
        this.image = drawableToBitmap(this.mDrawable)
        updateShader()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasSize = w
        if (h < canvasSize)
            canvasSize = h
        if (image != null)
            updateShader()
    }

    private fun drawShadow(shadowRadius: Float, shadowColor: Int) {
        this.shadowRadius = shadowRadius
        this.shadowColor = shadowColor
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, paintBorder)
        }
        paintBorder!!.setShadowLayer(shadowRadius, 0.0f, shadowRadius / 2, shadowColor)
    }

    private fun updateShader() {
        if (image == null)
            return

        // Crop Center Image
        image = cropBitmap(image!!)

        // Create Shader
        val shader = BitmapShader(image!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        // Center Image in Shader
        val matrix = Matrix()
        matrix.setScale(canvasSize.toFloat() / image!!.width.toFloat(), canvasSize.toFloat() / image!!.height.toFloat())
        shader.setLocalMatrix(matrix)

        // Set Shader in Paint
        paint!!.shader = shader
    }

    private fun cropBitmap(bitmap: Bitmap): Bitmap {
        val bmp: Bitmap
        if (bitmap.width >= bitmap.height) {
            bmp = Bitmap.createBitmap(
                    bitmap,
                    bitmap.width / 2 - bitmap.height / 2,
                    0,
                    bitmap.height, bitmap.height)
        } else {
            bmp = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.height / 2 - bitmap.width / 2,
                    bitmap.width, bitmap.width)
        }
        return bmp
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        } else if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val intrinsicWidth = drawable.intrinsicWidth
        val intrinsicHeight = drawable.intrinsicHeight

        if (!(intrinsicWidth > 0 && intrinsicHeight > 0))
            return null

        try {
            // Create Bitmap object out of the mDrawable
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } catch (e: OutOfMemoryError) {
            // Simply return null of failed bitmap creations
            Log.e(javaClass.toString(), "Encountered OutOfMemoryError while generating bitmap!")
            return null
        }

    }
    //endregion

    //region Mesure Method
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measureWidth(widthMeasureSpec)
        val height = measureHeight(heightMeasureSpec)
        /*int imageSize = (width < height) ? width : height;
        setMeasuredDimension(imageSize, imageSize);*/
        setMeasuredDimension(width, height)
    }

    private fun measureWidth(measureSpec: Int): Int {
        val result: Int
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        if (specMode == View.MeasureSpec.EXACTLY) {
            // The parent has determined an exact size for the child.
            result = specSize
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize
        } else {
            // The parent has not imposed any constraint on the child.
            result = canvasSize
        }

        return result
    }

    private fun measureHeight(measureSpecHeight: Int): Int {
        val result: Int
        val specMode = View.MeasureSpec.getMode(measureSpecHeight)
        val specSize = View.MeasureSpec.getSize(measureSpecHeight)

        if (specMode == View.MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = canvasSize
        }

        return result + 2
    }

    companion object {
        private val SCALE_TYPE = ImageView.ScaleType.CENTER_CROP

        // Default Values
        private val DEFAULT_BORDER_WIDTH = 4f
        private val DEFAULT_SHADOW_RADIUS = 8.0f
    }
    //endregion
}//region Constructor & Init Method