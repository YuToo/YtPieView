package xyz.yutoo.ytpieview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import java.text.DecimalFormat;
import java.util.List;

public class YtPieView extends View {

	// 圆环画笔
	private Paint mRingPaint;
	// 线段和文字画笔
	private Paint mLinePaint;
	// 实心圆画笔
	private Paint mCirclePaint;

	// 圆环半径
	private float mRingRadius;
	// 圆环宽度
	private float mRingWidth;
	// 实心园半径
	private float mCircleRadius = 5;
	// 字的高度
	private int mTextHeight;
	//默认文字大小
	private float mTextSize;
	// 线段长度
	private float mLineLength;

	// 圆心x坐标
	private int mXCenter;
	// 圆心y坐标
	private int mYCenter;

	private List<Data> mData;// 数据集合
	private float mSum;// 总数

	private float mHeight;
	private float mTextMargin = 20;// 底部文字说明的边距
	private float mRectWdith;// 底部方块边长
	private float mScreenWdith;

	public YtPieView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		mScreenWdith = getScreenWidth(context);
		TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.YtPieView, 0, 0 );
		// 圆环大小默认为屏幕的六分之一宽度
		mRingRadius = typeArray.getDimension(R.styleable.YtPieView_radius, mScreenWdith / 6);
		//默认为半径的三分之一
		mRingWidth = typeArray.getDimension(R.styleable.YtPieView_ringWidth, mRingRadius / 3);
		//默认线长为圆环宽度
		mLineLength = typeArray.getDimension(R.styleable.YtPieView_lineLength, mRingRadius / 3 );
		//文字大小
		mTextSize = typeArray.getDimension(R.styleable.YtPieView_textSize, sp2px(context, 10));
		//方块边长默认为文字大小
		mRectWdith = typeArray.getDimension(R.styleable.YtPieView_squareSide, mTextSize);
		
		//释放资源
		typeArray.recycle();
		
		
		// 圆环画笔
		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setStyle(Paint.Style.STROKE);
		mRingPaint.setStrokeWidth(mRingWidth);

		// 线段和文字画笔
		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setStrokeWidth(0);
		mLinePaint.setTextSize(sp2px(context, 10));

		// 实心圆画笔
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setStyle(Paint.Style.FILL);
		mCirclePaint.setStrokeWidth(mCircleRadius);

		FontMetrics fm = mLinePaint.getFontMetrics();
		mTextHeight = (int) Math.ceil(fm.descent - fm.ascent);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		mHeight = 2 * (mRingRadius + mLineLength + mTextHeight);
		// super.onDraw(canvas);
		if (mSum == 0) {
			// 只画一个灰色的圆形
			return;
		}
		if (mData != null) {
			mXCenter = getWidth() / 2;
			mYCenter = (int) (mRingRadius + mLineLength + mTextHeight);

			RectF oval = new RectF();
			oval.left = (mXCenter - mRingRadius);
			oval.top = (mYCenter - mRingRadius);
			oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
			oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
			float start = -90f;
			for (Data data : mData) {
				mRingPaint.setColor(data.color);
				float ring = (data.value / mSum) * 360f;
				canvas.drawArc(oval, start, ring, false, mRingPaint);

				// 画线段
				Path path = new Path();
				mLinePaint.setColor(data.color);
				// 画第一个线段
				float x = mXCenter + (float) Math.sin(Math.PI / 180.0 * (start + 90.0 + ring / 2.0)) * mRingRadius;
				float y = mYCenter - (float) Math.cos(Math.PI / 180.0 * (start + 90.0 + ring / 2.0)) * mRingRadius;
				path.moveTo(x, y);
				x = mXCenter + (float) Math.sin(Math.PI / 180.0 * (start + 90.0 + ring / 2.0)) * (mRingRadius + mLineLength);
				y = mYCenter - (float) Math.cos(Math.PI / 180.0 * (start + 90.0 + ring / 2.0)) * (mRingRadius + mLineLength);
				path.lineTo(x, y);

				// 画第二条线段
				if (x >= mXCenter) {
					x = x + mLineLength;
					path.lineTo(x, y);
				} else {
					x = x - mLineLength;
					path.lineTo(x, y);
				}
				canvas.drawPath(path, mLinePaint);

				// 画圆点
				mCirclePaint.setColor(data.color);
				canvas.drawCircle(x, y, mCircleRadius, mCirclePaint);
				start += ring;

				// 画文字
				if (x >= mXCenter) {
					canvas.drawText(data.title, x + mCircleRadius, y + mTextHeight / 4, mLinePaint);
				} else {
					canvas.drawText(data.title, x - mLinePaint.measureText(data.title) - mCircleRadius,
							y + mTextHeight / 4, mLinePaint);
				}
			}

			// 绘制底部说明
			DecimalFormat df = new DecimalFormat("#.##");
			for (int i = 0; i < mData.size(); i++) {
				Data data = mData.get(i);
				mLinePaint.setColor(data.color);
				mCirclePaint.setColor(data.color);
				if (i % 2 == 0) {
					mHeight += (3 * mRectWdith);
					canvas.drawRect(mTextMargin, mHeight, mTextMargin + mRectWdith, mHeight + mRectWdith, mCirclePaint);
					canvas.drawText(data.title + " ［"+ df.format(data.value / mSum * 100) +"％］", mTextMargin + mRectWdith * 2, mHeight + mTextHeight / 2, mLinePaint);
				} else {
					canvas.drawRect(mScreenWdith / 2 + mTextMargin, mHeight,
							mScreenWdith / 2 + mTextMargin + mRectWdith, mHeight + mRectWdith, mCirclePaint);
					canvas.drawText(data.title + " ［"+ df.format(data.value / mSum * 100) +"％］", mScreenWdith / 2 + mTextMargin + mRectWdith * 2,
							mHeight + mTextHeight / 2, mLinePaint);
				}
			}

		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) mScreenWdith, MeasureSpec.AT_MOST);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) mHeight, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setData(List<Data> datas) {
		this.mData = datas;
		mSum = 0;
		for (Data data : datas) {
			mSum += data.value;
		}

		mHeight = 2 * (mRingRadius + mLineLength + mTextHeight);
		mHeight += ((datas.size() / 2 + 1 + (datas.size() % 2 == 0 ? 0 : 1)) * (3 * mRectWdith));
		LayoutParams params = getLayoutParams();
		params.height = (int) mHeight;
		setLayoutParams(params);

		invalidate();
	}

	private int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	private float sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return  (spValue * fontScale + 0.5f);
	}

	public static class Data {
		String title;// 标题
		int value;// 值
		int color;// 颜色


		public Data(){

		}

		public Data(String title, int value, int color){
			this.title = title;
			this.value = value;
			this.color = color;
		}
	}
}
