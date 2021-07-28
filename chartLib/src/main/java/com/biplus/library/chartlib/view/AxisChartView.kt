package com.biplus.library.chartlib.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.doOnPreDraw
import com.biplus.library.chartlib.R
import com.biplus.library.chartlib.animation.ChartAnimation
import com.biplus.library.chartlib.animation.DefaultAnimation
import com.biplus.library.chartlib.data.AxisType
import com.biplus.library.chartlib.data.DataPoint
import com.biplus.library.chartlib.data.Frame
import com.biplus.library.chartlib.data.Scale
import com.biplus.library.chartlib.data.configuration.ChartConfiguration
import com.biplus.library.chartlib.extensions.obtainStyledAttributes
import com.biplus.library.chartlib.helpers.*
import com.biplus.library.chartlib.plugin.AxisGrid
import com.biplus.library.chartlib.plugin.AxisLabels
import com.biplus.library.chartlib.plugin.GridEffect
import com.biplus.library.chartlib.plugin.GridType
import com.biplus.library.chartlib.renderer.RendererConstants.Companion.notInitialized

@OptIn(ExperimentalFeature::class)
abstract class AxisChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var labelsSize: Float = defaultLabelsSize

    var labelsColor: Int = -0x1000000

    var labelsFont: Typeface? = null

    var axis: AxisType = AxisType.XY

    var scale: Scale = Scale(notInitialized, notInitialized)

    var labelsFormatter: (Float) -> String = { it.toString() }

    var animation: ChartAnimation<DataPoint> = DefaultAnimation()

    val labels: Labels = AxisLabels()

    var tooltip: Tooltip = object : Tooltip {
        override fun onCreateTooltip(parentView: ViewGroup) {}
        override fun onDataPointTouch(x: Float, y: Float) {}
        override fun onDataPointClick(x: Float, y: Float) {}
    }

    @ExperimentalFeature
    var grid: Grid = object : Grid {
        override fun draw(
            canvas: Canvas,
            innerFrame: Frame,
            xLabelsPositions: List<Float>,
            yLabelsPositions: List<Float>
        ) {
        }
    }

    @ExperimentalFeature
    var onDataPointClickListener: (index: Int, x: Float, y: Float) -> Unit = { _, _, _ -> }

    @ExperimentalFeature
    var onDataPointTouchListener: (index: Int, x: Float, y: Float) -> Unit = { _, _, _ -> }

    protected lateinit var canvas: Canvas

    protected val painter: Painter = Painter(labelsFont = labelsFont)

    /**
     * Initialized in init function by chart views extending [AxisChartView] (e.g. [LineChartView])
     */
    protected lateinit var renderer: ChartContract.Renderer

    private val gestureDetector: GestureDetectorCompat =
        GestureDetectorCompat(
            this.context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent?): Boolean = true
                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                    val (index, x, y) = renderer.processClick(e?.x, e?.y)
                    return if (index != -1) {
                        onDataPointClickListener(index, x, y)
                        tooltip.onDataPointClick(x, y)
                        true
                    } else false
                }
            }
        )

    init {
        handleAttributes(obtainStyledAttributes(attrs, R.styleable.ChartAttrs))
        doOnPreDraw { tooltip.onCreateTooltip(this) }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.setWillNotDraw(false)
        // style.init()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // style.clean()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        setMeasuredDimension(
            if (widthMode == MeasureSpec.AT_MOST) defaultFrameWidth else widthMeasureSpec,
            if (heightMode == MeasureSpec.AT_MOST) defaultFrameHeight else heightMeasureSpec
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.canvas = canvas
        renderer.draw()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val (index, x, y) = renderer.processTouch(event?.x, event?.y)
        if (index != -1) {
            onDataPointTouchListener(index, x, y)
            tooltip.onDataPointTouch(x, y)
        }
        return if (gestureDetector.onTouchEvent(event)) true
        else super.onTouchEvent(event)
    }

    abstract val chartConfiguration: ChartConfiguration

    @Deprecated("New method receives a List<Pair<String, Float>> as argument.")
    fun show(entries: LinkedHashMap<String, Float>) {
        doOnPreDraw { renderer.preDraw(chartConfiguration) }
        renderer.render(entries.toList())
    }

    @Deprecated("New method receives a List<Pair<String, Float>> as argument.")
    fun animate(entries: LinkedHashMap<String, Float>) {
        doOnPreDraw { renderer.preDraw(chartConfiguration) }
        renderer.anim(entries.toList(), animation)
    }

    fun show(entries: List<Pair<String, Float>>) {
        doOnPreDraw { renderer.preDraw(chartConfiguration) }
        renderer.render(entries)
    }

    fun animate(entries: List<Pair<String, Float>>) {
        doOnPreDraw { renderer.preDraw(chartConfiguration) }
        renderer.anim(entries, animation)
    }

    private fun handleAttributes(typedArray: TypedArray) {
        typedArray.apply {

            // Customize Axis
            axis = when (getString(R.styleable.ChartAttrs_chart_axis)) {
                "0" -> AxisType.NONE
                "1" -> AxisType.X
                "2" -> AxisType.Y
                else -> AxisType.XY
            }

            // Customize Labels
            labelsSize = getDimension(R.styleable.ChartAttrs_chart_labelsSize, labelsSize)

            labelsColor = getColor(R.styleable.ChartAttrs_chart_labelsColor, labelsColor)

            if (hasValue(R.styleable.ChartAttrs_chart_labelsFont) && !isInEditMode) {
                labelsFont =
                    ResourcesCompat.getFont(
                        context,
                        getResourceId(R.styleable.ChartAttrs_chart_labelsFont, -1)
                    )
                painter.labelsFont = labelsFont
            }

            // Customize Grid
            if (hasValue(R.styleable.ChartAttrs_chart_grid)) {
                grid = AxisGrid().apply {
                    this.gridType = when (getString(R.styleable.ChartAttrs_chart_grid)) {
                        "0" -> GridType.FULL
                        "1" -> GridType.VERTICAL
                        "2" -> GridType.HORIZONTAL
                        else -> GridType.FULL
                    }
                    this.color = getColor(R.styleable.ChartAttrs_chart_gridColor, color)
                    this.strokeWidth =
                        getDimension(R.styleable.ChartAttrs_chart_gridStrokeWidth, strokeWidth)
                    this.gridEffect =
                        when (getString(R.styleable.ChartAttrs_chart_gridEffect)) {
                            "0" -> GridEffect.SOLID
                            "1" -> GridEffect.DASHED
                            "2" -> GridEffect.DOTTED
                            else -> GridEffect.SOLID
                        }
                }
            }

            recycle()
        }
    }

    protected fun handleEditMode() {
        if (isInEditMode) {
            show(editModeSampleData)
        }
    }

    companion object {
        private const val defaultFrameWidth = 200
        private const val defaultFrameHeight = 100
        private const val defaultLabelsSize = 60F
        private val editModeSampleData =
            listOf(
                "Label1" to 1f,
                "Label2" to 7.5f,
                "Label3" to 4.7f,
                "Label4" to 3.5f
            )
    }
}