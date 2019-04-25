package com.seat.view.seat;

import java.util.ArrayList;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.seat.R;

public class SelectSeatView extends View {
	private final static String TAG="SelectSeatView";
	public enum SeatStatus{
		SS_NONE, SS_NORMAL, SS_LOCKED, SS_CHECKED, SS_SOLD, SS_ORDER, SS_OVERFLOW
	}
	public enum AnimationStyle{
		AS_NONE, AS_MOVE, AS_ZOOM
	}
	public class SelectedSeatInfo{
		private int mCol;
		private int mRow;
		public SelectedSeatInfo(int col, int row){
			this.mCol = col;
			this.mRow = row;
		}
		public int getCol(){
			return mCol;
		}
		public int getRow(){
			return mRow;
		}
	}
	public class AnimationRecord{
		private AnimationStyle mAnimationStyle;
		private boolean mScaleAccelerate;
		private boolean mTranslateAccelerate;
		private float mScaleStep;
		private float mTranslateStepX,mTranslateStepY;
		private float mArithmeticStepX,mArithmeticStepY;
		private long mAnimationDuration;
		public AnimationRecord(){
			
		}
	}
	private final static float TOUCH_DELTA_DP = 8f;
	private static float TOUCH_DELTA = 10f;
	private final static float TOUCH_MIN_VELOCITY = 1000f;
	private final static long TRANSLATE_ANIMATION_STEP_TIME = 70;
	private final static long ZOOM_ANIMATION_STEP_TIME = 70;
	private final static int TRANSLATE_ANIMATION_MAX_NUM = 13;//移动动画最多次数
	private final static int TRANSLATE_ANIMATION_MIN_NUM = 3;//移动动画最小次数
	private final static int ZOOM_ANIMATION_MAX_NUM = 6;//缩放动画最多次数
	private final static int ZOOM_ANIMATION_MIN_NUM = 2;//缩放动画最小次数
	private final static float TRANSLATE_ARITHMETIC_RATIO = 6;//等差数列最小与最大的比值
	private int mViewWidth;
	private int mViewHeight;
	private int mColumns;
	private int mRows;
	private int mSelectedMax;//最多选中
	private float mCurrentSeatWidth;
	private float mCurrentSeatHeight;
	private float mHorizontalSpace;
	private float mVerticalSpace;
	private float mMaxSeatWidth;
	private float mMaxSeatHeight;
	private float mMinSeatWidth;
	private float mMinSeatHeight;
	
	private float mOffsetLeft;
	private float mOffsetTop;
	private float mOffsetRight;
	private float mOffsetBottom;
	private float mSeatOffsetTop;
	
	private int mDefaultLineColor;

	private float mThumOffsetLeft;
	private float mThumOffsetTop;
	private float mThumWidth;
	private float mThumHeight;
	private int mThumLineColor;
	private float mThumLineWidth;
	private int mThumBackgroundColor;
	private int mThumShowDelay;

	private String mCinemaTitle;
	private float mCinemaTitleSize;
	private float mCinemaTitleHeight;
	private float mCenterLineWidth;
	private float mCenterRegionWidth;
	private int mCinemaTitleColor;
	private int mCenterLineColor;
	private Bitmap mSeatNormalBitmap;
	private Bitmap mSeatLockedBitmap;
	private Bitmap mSeatCheckedBitmap;
	private Bitmap mSeatSoldBitmap;
	private Bitmap mSeatOrderBitmap;
	private Bitmap mTopBackground;
	
	private float mRowLabelLeft;
	private float mRowLabelTop;
	private float mRowLabelRight;
	private float mRowLabelBottom;
	private float mRowLabelWidth;
	private int mRowLabelTextColor;
	private float mRowLabelTextSize;
	private Bitmap mRowLabelBackground;
	
	private boolean mShowScaleMap;
	private float mZoom;
	private float mMoveOffsetLeft;
	private float mMoveOffsetTop;
	private float mStartTouchX,mStartTouchY,mEndTouchX,mEndTouchY;
	private float mStartTouchX2,mStartTouchY2,mEndTouchX2,mEndTouchY2;
	private float mSaveStartTouchX,mSaveStartTouchY;
	
	private float mMarginLeft,mMarginTop,mMarginRight,mMarginBottom;
	
	private boolean mIsZoomAction=false;
	private boolean mIsZoomDown = false;
	private boolean mAnimationLocked = false;
	private float mZoomTotalDelta;
	private float mTranslateFirstDeltaX,mTranslateFirstDeltaY;
	private float mTranslateSecondDeltaX,mTranslateSecondDeltaY;
//	private float mTranslateTotalNum;
	private float mStepX,mStepY;
	private float mArithmeticStepX,mArithmeticStepY;
	private long mAnimationDuration = TRANSLATE_ANIMATION_STEP_TIME;
	private AnimationStyle mAnimationStyle = AnimationStyle.AS_NONE;
	private VelocityTracker mVelocityTracker;
	private ArrayList<ArrayList<SeatStatus>> mSeatConditions = null;
	private ArrayList<SelectedSeatInfo> mSelectedSeats = new ArrayList<SelectedSeatInfo>();
	private OnSeatClickListener mOnSeatClickListener;
	
	public SelectSeatView(Context context){
		super(context);
		initView();
	}
	public SelectSeatView(Context context, AttributeSet attrs){
		super(context, attrs);
		init(context, attrs);
		initView();
	}
	public SelectSeatView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		init(context, attrs);
		initView();
	}
	
	private void init(Context context, AttributeSet attrs){
		int blackColor = context.getResources().getColor(android.R.color.black);
		TOUCH_DELTA = context.getResources().getDisplayMetrics().density*TOUCH_DELTA_DP;
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectSeatView);
		Drawable draw = typedArray.getDrawable(R.styleable.SelectSeatView_seatNormalDrawable);
		if (draw!=null && draw instanceof BitmapDrawable){
			mSeatNormalBitmap = ((BitmapDrawable)draw).getBitmap();
		}
		draw = typedArray.getDrawable(R.styleable.SelectSeatView_seatLockedDrawable);
		if (draw!=null && draw instanceof BitmapDrawable){
			mSeatLockedBitmap = ((BitmapDrawable)draw).getBitmap();
		}
		draw = typedArray.getDrawable(R.styleable.SelectSeatView_seatCheckedDrawable);
		if (draw!=null && draw instanceof BitmapDrawable){
			mSeatCheckedBitmap = ((BitmapDrawable)draw).getBitmap();
		}
		draw = typedArray.getDrawable(R.styleable.SelectSeatView_seatSoldDrawable);
		if (draw!=null && draw instanceof BitmapDrawable){
			mSeatSoldBitmap = ((BitmapDrawable)draw).getBitmap();
		}
		draw = typedArray.getDrawable(R.styleable.SelectSeatView_seatOrderDrawable);
		if (draw!=null && draw instanceof BitmapDrawable){
			mSeatOrderBitmap = ((BitmapDrawable)draw).getBitmap();
		}
		draw = typedArray.getDrawable(R.styleable.SelectSeatView_topBackground);
		if (draw!=null && draw instanceof BitmapDrawable){
			mTopBackground = ((BitmapDrawable)draw).getBitmap();
		}
		mDefaultLineColor = typedArray.getColor(R.styleable.SelectSeatView_defaultLineColor, blackColor);
		mCurrentSeatWidth = typedArray.getDimension(R.styleable.SelectSeatView_seatWidth, 80);
		mCurrentSeatHeight = typedArray.getDimension(R.styleable.SelectSeatView_seatHeight, 80);
		mMinSeatWidth = typedArray.getDimension(R.styleable.SelectSeatView_minSeatWidth, 40);
		mMinSeatHeight = typedArray.getDimension(R.styleable.SelectSeatView_minSeatHeight, 40);
		mMaxSeatWidth = typedArray.getDimension(R.styleable.SelectSeatView_maxSeatWidth, 200);
		mMaxSeatHeight = typedArray.getDimension(R.styleable.SelectSeatView_maxSeatHeight, 200);
		if(mCurrentSeatWidth<mMinSeatWidth)
			mCurrentSeatWidth=mMinSeatWidth;
		if(mCurrentSeatWidth>mMaxSeatWidth)
			mCurrentSeatWidth=mMaxSeatWidth;
		if(mCurrentSeatHeight<mMinSeatHeight)
			mCurrentSeatHeight=mMinSeatHeight;
		if(mCurrentSeatHeight>mMaxSeatHeight)
			mCurrentSeatHeight=mMaxSeatHeight;
		mOffsetLeft = typedArray.getDimension(R.styleable.SelectSeatView_offsetLeft, 0);
		mOffsetTop = typedArray.getDimension(R.styleable.SelectSeatView_offsetTop, 0);
		mOffsetRight = typedArray.getDimension(R.styleable.SelectSeatView_offsetRight, 0);
		mOffsetBottom = typedArray.getDimension(R.styleable.SelectSeatView_offsetBottom, 0);
		mSeatOffsetTop = typedArray.getDimension(R.styleable.SelectSeatView_seatOffsetTop, 0);
		mHorizontalSpace = typedArray.getDimension(R.styleable.SelectSeatView_horizontalSpace, 0);
		mVerticalSpace = typedArray.getDimension(R.styleable.SelectSeatView_verticalSpace, 0);
		mCenterLineWidth = typedArray.getDimension(R.styleable.SelectSeatView_centerLineWidth, 1);
		mCenterRegionWidth = typedArray.getDimension(R.styleable.SelectSeatView_centerRegionWidth, 0); 
		
		mThumWidth = typedArray.getDimension(R.styleable.SelectSeatView_thumWidth, 100);
		mThumHeight = typedArray.getDimension(R.styleable.SelectSeatView_thumHeight, 50);
		mThumBackgroundColor = typedArray.getColor(R.styleable.SelectSeatView_thumBackgroundColor, 0x229932CC);
		mThumLineWidth = typedArray.getDimension(R.styleable.SelectSeatView_thumLineWidth, 2);
		mThumLineColor = typedArray.getColor(R.styleable.SelectSeatView_thumLineColor, blackColor);
		mThumShowDelay = typedArray.getInteger(R.styleable.SelectSeatView_thumShowDelay, 10000);
		mThumOffsetLeft = typedArray.getDimension(R.styleable.SelectSeatView_thumOffsetLeft, 0);
		mThumOffsetTop = typedArray.getDimension(R.styleable.SelectSeatView_thumOffsetTop, 0);
		mCinemaTitle = typedArray.getString(R.styleable.SelectSeatView_cinemaTitle);
		mCinemaTitleSize = typedArray.getDimension(R.styleable.SelectSeatView_cinemaTitleSize, 10);
		mCinemaTitleHeight = typedArray.getDimension(R.styleable.SelectSeatView_cinemaTitleHeight, 20);
		mCinemaTitleColor = typedArray.getColor(R.styleable.SelectSeatView_cinemaTitleColor, blackColor);
		mCenterLineColor = typedArray.getColor(R.styleable.SelectSeatView_centerLineColor, blackColor);
		
		mRowLabelLeft = typedArray.getDimension(R.styleable.SelectSeatView_rowLabelLeft, 0);
		mRowLabelTop = typedArray.getDimension(R.styleable.SelectSeatView_rowLabelTop, 0);
		mRowLabelRight = typedArray.getDimension(R.styleable.SelectSeatView_rowLabelRight, 0);
		mRowLabelBottom = typedArray.getDimension(R.styleable.SelectSeatView_rowLabelBottom, 0);
		mRowLabelWidth = typedArray.getDimension(R.styleable.SelectSeatView_rowLabelWidth, 30);
		draw = typedArray.getDrawable(R.styleable.SelectSeatView_rowLabelBackground);
		if(draw!=null && draw instanceof BitmapDrawable)
			mRowLabelBackground = ((BitmapDrawable)draw).getBitmap();
		mRowLabelTextColor = typedArray.getColor(R.styleable.SelectSeatView_rowLabelTextColor, blackColor);
		mRowLabelTextSize = typedArray.getDimension(R.styleable.SelectSeatView_rowLabelTextSize, 10);
		typedArray.recycle();
	}
	private void initView(){
		ViewTreeObserver observer = getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mViewHeight = getHeight();
				mViewWidth = getWidth();
				
				if(getLayoutParams() instanceof LinearLayout.LayoutParams){
					LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)getLayoutParams();
					mMarginLeft = lp.leftMargin;
					mMarginTop = lp.topMargin;
					mMarginRight = lp.rightMargin;
					mMarginBottom = lp.bottomMargin;
				}
				else if(getLayoutParams() instanceof RelativeLayout.LayoutParams){
					RelativeLayout.LayoutParams lp=(RelativeLayout.LayoutParams)getLayoutParams();
					mMarginLeft = lp.leftMargin;
					mMarginTop = lp.topMargin;
					mMarginRight = lp.rightMargin;
					mMarginBottom = lp.bottomMargin;
				}
				else if(getLayoutParams() instanceof FrameLayout.LayoutParams){
					FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams)getLayoutParams();
					mMarginLeft = lp.leftMargin;
					mMarginTop = lp.topMargin;
					mMarginRight = lp.rightMargin;
					mMarginBottom = lp.bottomMargin;
				}
			}
		});
		mZoom = 1f;
//		mShowScaleMap = true;
	}
	
	public void initData(){
		
	}
	public void initData(int rows, int cols, int selectedMax, ArrayList<ArrayList<SeatStatus>> seats){
		this.mColumns = cols;
		this.mRows = rows;
		mSeatConditions = seats;
		mSelectedMax = selectedMax;
		this.invalidate();
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		canvas.translate(mMoveOffsetLeft*mZoom, mMoveOffsetTop*mZoom);
		canvas.scale(mZoom, mZoom);
		drawCinemaInfo(canvas);
		drawCenterLine(canvas);
		
		Paint paint = new Paint();
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setColor(mDefaultLineColor);
		
		int rows=mSeatConditions!=null?mSeatConditions.size():0;
		for(int i=0;i<rows;i++){
			ArrayList<SeatStatus> rowinfo = mSeatConditions.get(i);
			int cols = rowinfo.size();
			for(int j=0;j<cols;j++){
				SeatStatus status = rowinfo.get(j);
				switch(status){
				case SS_NONE:
					break;
				case SS_NORMAL:
					drawSeatInfo(j, i, mSeatNormalBitmap, canvas, paint);
					break;
				case SS_LOCKED:
					drawSeatInfo(j, i, mSeatLockedBitmap, canvas, paint);
					break;
				case SS_CHECKED:
					drawSeatInfo(j, i, mSeatCheckedBitmap, canvas, paint);
					break;
				case SS_SOLD:
					drawSeatInfo(j, i, mSeatSoldBitmap, canvas, paint);
					break;
				case SS_ORDER:
					drawSeatInfo(j, i, mSeatOrderBitmap, canvas, paint);
					break;
				default:
					break;
				}
			}
		}
		canvas.restore();
		////绘制行号
		drawRowLabel(canvas);
		if(mShowScaleMap){
			drawThumSeats(canvas);
			drawThumRect(canvas);
		}
	}

	//绘制中心虚线
	private void drawCenterLine(Canvas canvas){
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(mCenterLineColor);
		paint.setStrokeWidth(mCenterLineWidth);
		float val = mCenterLineWidth*4;
		PathEffect effects = new DashPathEffect(new float[]{val,val,val,val},mCenterLineWidth);
		paint.setPathEffect(effects);
		Path path = new Path(); 
		
		int mid=mColumns/2;
		float startX =  mOffsetLeft + mid*mCurrentSeatWidth + Math.max(0, mid-1)*mHorizontalSpace + mCenterRegionWidth/2;
        path.moveTo(startX, mOffsetTop + mCinemaTitleHeight);
        path.lineTo(startX, mOffsetTop + mCinemaTitleHeight + mSeatOffsetTop + mRows*mCurrentSeatHeight+Math.max(0, mRows-1)*mVerticalSpace);
		canvas.drawPath(path, paint);
	}

	private void drawSeatInfo(int col, int row, Bitmap bitmap, Canvas canvas, Paint paint) {
		if (bitmap == null) {// 走道
			canvas.drawRect(getSeatRect(col, row), paint);
		} else {
			canvas.drawBitmap(bitmap, null, getSeatRect(col, row), paint);
		}
	}
	private void drawThumSeatInfo(int col, int row, Bitmap bitmap, Canvas canvas, Paint paint) {
		if (bitmap == null) {// 走道
			canvas.drawRect(getThumSeatRect(col, row), paint);
		} else {
			canvas.drawBitmap(bitmap, null, getThumSeatRect(col, row), paint);
		}
	}

	private void drawCinemaInfo(Canvas canvas) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(mCinemaTitleSize);
		paint.setColor(mCinemaTitleColor);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		float cx;
		if(mColumns > 0){
			int mid=mColumns / 2;
			if(mid<1)
				cx = mOffsetLeft + mCurrentSeatWidth/2;
			else
				cx = mOffsetLeft + mid*mCurrentSeatWidth+Math.max(0, mid-1)*mHorizontalSpace+mCenterRegionWidth/2;
		}
		else
			cx = mOffsetLeft + (mViewWidth - mOffsetLeft - mOffsetRight)/2;
		FontMetricsInt fm = paint.getFontMetricsInt();
		float width = paint.measureText(mCinemaTitle);
		float x = (cx - width / 2);
		RectF rect = new RectF((int) (x - width / 10), mOffsetTop, (x + width + width / 10),mOffsetTop+mCinemaTitleHeight);
		if (mTopBackground != null)
			canvas.drawBitmap(mTopBackground, null, rect, paint);
		float centre = mOffsetTop + mCinemaTitleHeight/2;
		float rtop=centre - mCinemaTitleSize/2;
		float rbottom=centre + mCinemaTitleSize/2;
		float y = rtop + (rbottom - rtop - fm.bottom + fm.top) / 2 - fm.top;
		canvas.drawText(mCinemaTitle, x, y, paint);
	}
	private void drawRowLabel(Canvas canvas){
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setTextSize(mRowLabelTextSize);
		paint.setColor(mRowLabelTextColor);
		paint.setTypeface(Typeface.DEFAULT); //设置字体
		RectF rect = new RectF(mRowLabelLeft, mRowLabelTop, mRowLabelLeft+mRowLabelWidth, mViewHeight-mRowLabelBottom);
		if(mRowLabelBackground!=null){
			canvas.drawBitmap(mRowLabelBackground, null, rect, paint);
		}
		if(mRows==0)
			return;
		float y = (mOffsetTop + mCinemaTitleHeight + mSeatOffsetTop + mMoveOffsetTop)*mZoom;
		int from = 0;
		if (y<0){
			from = (int)(-y/((mCurrentSeatHeight+mVerticalSpace)*mZoom));
		}
		y += from*(mCurrentSeatHeight+mVerticalSpace)*mZoom+mCurrentSeatHeight*mZoom/2;
		if(y<0)
			y+=mVerticalSpace*mZoom;
		float cx = mRowLabelLeft+mRowLabelWidth/2;
		for(int i=from;i<mRows;i++){
			if(y>=mOffsetTop && y<=mViewHeight-mRowLabelBottom){
				String label = String.valueOf(i+1);
				FontMetricsInt fm = paint.getFontMetricsInt();
				float width = paint.measureText(label);
				float x = cx - width/2;
				float rtop=y - mRowLabelTextSize/2;
				float rbottom=y + mRowLabelTextSize/2;
				float baseline = rtop + (rbottom - rtop - fm.bottom + fm.top) / 2 - fm.top;
				canvas.drawText(label, x, baseline, paint);
			}
			y += (mCurrentSeatHeight+mVerticalSpace)*mZoom;
		}
	}
	/**
	 * 
	 * @param col 每排的座位号
	 * @param row  排号
	 * @return
	 */
	private RectF getSeatRect(int col, int row) {
		int centre = mColumns/2;
		float cx = mOffsetLeft + (col>=centre?(mCenterRegionWidth-mHorizontalSpace):0);
		float cy = mOffsetTop + mCinemaTitleHeight + mSeatOffsetTop;
		try {
			RectF localRect = new RectF(cx+ col*(mCurrentSeatWidth + mHorizontalSpace),
					cy + row*(mCurrentSeatHeight + mVerticalSpace),
					cx+ col*(mCurrentSeatWidth + mHorizontalSpace)+mCurrentSeatWidth,
					cy+ row*(mCurrentSeatHeight + mVerticalSpace)+mCurrentSeatHeight);
			return localRect;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new RectF();
	}

	private void drawThumSeats(Canvas canvas){
		float acturex=mOffsetLeft+mOffsetRight + mColumns*mCurrentSeatWidth + Math.max(0, mColumns-2)*mHorizontalSpace +(mColumns>1?mCenterRegionWidth:0);
		float acturey=mOffsetTop+mOffsetBottom +mRows*mCurrentSeatHeight+Math.max(0, mRows-1)*mVerticalSpace;
		float zoom = Math.min(mThumWidth/acturex, mThumHeight/acturey);
		Paint paint=new Paint();
		paint.setColor(mThumBackgroundColor);
		canvas.drawRect(new RectF(mThumOffsetLeft,mThumOffsetTop,mThumOffsetLeft+acturex*zoom,mThumOffsetTop+acturey*zoom), paint);
		canvas.save();
		canvas.translate(mThumOffsetLeft, mThumOffsetTop);
		canvas.scale(zoom, zoom);
		
		Paint paint2 = new Paint();
		paint2.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint2.setColor(mDefaultLineColor);
		
		int rows=mSeatConditions!=null?mSeatConditions.size():0;
		for(int i=0;i<rows;i++){
			ArrayList<SeatStatus> rowinfo = mSeatConditions.get(i);
			int cols = rowinfo.size();
			for(int j=0;j<cols;j++){
				SeatStatus status = rowinfo.get(j);
				switch(status){
				case SS_NONE:
					break;
				case SS_NORMAL:
					drawThumSeatInfo(j, i, mSeatNormalBitmap, canvas, paint2);
					break;
				case SS_LOCKED:
					drawThumSeatInfo(j, i, mSeatLockedBitmap, canvas, paint2);
					break;
				case SS_CHECKED:
					drawThumSeatInfo(j, i, mSeatCheckedBitmap, canvas, paint2);
					break;
				case SS_SOLD:
					drawThumSeatInfo(j, i, mSeatSoldBitmap, canvas, paint2);
					break;
				case SS_ORDER:
					drawThumSeatInfo(j, i, mSeatOrderBitmap, canvas, paint2);
					break;
				default:
					break;
				}
			}
		}
		canvas.restore();
	}
	private void drawThumRect(Canvas canvas){
		Paint paint=new Paint();
		paint.setStrokeWidth(mThumLineWidth);
		paint.setColor(mThumLineColor);
		float acturex=mOffsetLeft+mOffsetRight + mColumns*mCurrentSeatWidth + Math.max(0, mColumns-2)*mHorizontalSpace +(mColumns>1?mCenterRegionWidth:0);
		float acturey=mOffsetTop+mOffsetBottom +mRows*mCurrentSeatHeight+Math.max(0, mRows-1)*mVerticalSpace;
		float zoom = Math.min(mThumWidth/acturex, mThumHeight/acturey);
		float x1 = mThumOffsetLeft-Math.min(0, mMoveOffsetLeft)*zoom;
		float x2 = Math.min((Math.min(0, mMoveOffsetLeft)+acturex), mViewWidth/mZoom)*zoom;
		x2+=x1;
		float y1 = Math.min(0, mMoveOffsetTop);
		if(-y1>mOffsetTop)
			y1 = mOffsetTop+Math.max(0, -y1-(mOffsetTop + mCinemaTitleHeight + mSeatOffsetTop));
		else
			y1 = -y1;
		float y2;
		float y = Math.min(0, mMoveOffsetTop);
		if(mViewHeight/mZoom-y>mOffsetTop)
			y2 = mOffsetTop + Math.max(0, mViewHeight/mZoom-y-(mOffsetTop + mCinemaTitleHeight + mSeatOffsetTop));
		else
			y2 = mViewHeight/mZoom-y;
		y2=mThumOffsetTop+Math.min(y2,acturey)*zoom;
		y1=mThumOffsetTop+y1*zoom;
		canvas.drawLines(new float[]{x1,y1,x2,y1,  x2,y1,x2,y2,  x1,y2,x2,y2, x1,y1,x1,y2}, paint);
	}
	private RectF getThumSeatRect(int col, int row) {
		int centre = mColumns/2;
		float cx = mOffsetLeft + (col>=centre?(mCenterRegionWidth-mHorizontalSpace):0);
		float cy = mOffsetTop;// + mCinemaTitleHeight + mSeatOffsetTop;
		try {
			RectF localRect = new RectF(cx+ col*(mCurrentSeatWidth + mHorizontalSpace),
					cy + row*(mCurrentSeatHeight + mVerticalSpace),
					cx+ col*(mCurrentSeatWidth + mHorizontalSpace)+mCurrentSeatWidth,
					cy+ row*(mCurrentSeatHeight + mVerticalSpace)+mCurrentSeatHeight);
			return localRect;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new RectF();
	}
	public SeatStatus clickSeat(int col, int row){
		return changeSeatStatus(col,row);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();
		mVelocityTracker.addMovement(ev);
		float xVelocity,yVelocity;
		boolean twoPoint = false;
		switch(ev.getAction() & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:
			Log.i(TAG, "ACTION_DOWN point:"+ev.getPointerCount());
			mAnimationStyle = AnimationStyle.AS_NONE;
			mIsZoomAction = false;
			mIsZoomDown = false;
			mStartTouchX = ev.getX(0)-mMarginLeft;
			mStartTouchY = ev.getY(0)-mMarginTop;
			mEndTouchX = mStartTouchX;
			mEndTouchY = mStartTouchY;
			mSaveStartTouchX = mStartTouchX;
			mSaveStartTouchY = mStartTouchY;
			if(ev.getPointerCount()>1){
				mStartTouchX2 = ev.getX(1)-mMarginLeft;
				mStartTouchY2 = ev.getY(1)-mMarginTop;
				mEndTouchX2 = mStartTouchX2;
				mEndTouchY2 = mStartTouchY2;
			}
			break;
		case MotionEvent.ACTION_UP:
			Log.i(TAG, "ACTION_UP point:"+ev.getPointerCount());
			if(mIsZoomAction){
//				if(checkMustTranslateAnimation()){
//					startTranslateAnimation(0,0);
//				}
				if(mVelocityTracker!=null){
					mVelocityTracker.recycle();
					mVelocityTracker=null;
				}
				break;
			}
			mVelocityTracker.computeCurrentVelocity(1000);
			xVelocity = mVelocityTracker.getXVelocity(0);
			yVelocity = mVelocityTracker.getYVelocity(0);
//			if (xVelocity>=TOUCH_MIN_VELOCITY || yVelocity>=TOUCH_MIN_VELOCITY){
//				mAnimationStyle = AnimationStyle.AS_MOVE;
//			}
			mEndTouchX = ev.getX(0)-mMarginLeft;
			mEndTouchY = ev.getY(0)-mMarginTop;
			twoPoint = ev.getPointerCount()>1;
			if(twoPoint){
				mEndTouchX2 = ev.getX(1)-mMarginLeft;
				mEndTouchY2 = ev.getY(1)-mMarginTop;
			}
			if(Math.abs(mEndTouchX-mSaveStartTouchX)<TOUCH_DELTA && Math.abs(mEndTouchY-mSaveStartTouchY)<TOUCH_DELTA){
				//on click event
				doClick(mStartTouchX, mStartTouchY);
			}
			else if(twoPoint){
				if(Math.abs(mEndTouchX-mStartTouchX)>=Math.abs(mEndTouchY-mStartTouchY)){
					float x1=mEndTouchX-mStartTouchX;
					float x2=mEndTouchX2-mStartTouchX2;
					if(x1>0 ^ x2>0){
						doScale(x1-x2, false, false);
					}
				}
				else{
					float y1=mEndTouchY-mStartTouchY;
					float y2=mEndTouchY2-mStartTouchY2;
					if(y1>0 ^ y2>0){
						doScale(y1-y2, true, false);
					}
				}
			}
			else{
				doMove(mEndTouchX-mStartTouchX,mEndTouchY-mStartTouchY, true);
//				if(mAnimationStyle==AnimationStyle.AS_MOVE)
				startTranslateAnimation(xVelocity>=TOUCH_MIN_VELOCITY?xVelocity/10:0, yVelocity>=TOUCH_MIN_VELOCITY?yVelocity/10:0);
			}
			if(mVelocityTracker!=null){
				mVelocityTracker.recycle();
				mVelocityTracker=null;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			Log.i(TAG, "ACTION_MOVE point:"+ev.getPointerCount());
			mStartTouchX = mEndTouchX;
			mStartTouchY = mEndTouchY;
			mEndTouchX = ev.getX(0)-mMarginLeft;
			mEndTouchY = ev.getY(0)-mMarginTop;
			twoPoint = ev.getPointerCount()>1;
			if(twoPoint){
				mStartTouchX2 = mEndTouchX2;
				mStartTouchY2 = mEndTouchY2;
				mEndTouchX2 = ev.getX(1)-mMarginLeft;
				mEndTouchY2 = ev.getY(1)-mMarginTop;
			}
			if(twoPoint){
				if(Math.abs(mEndTouchX-mStartTouchX)>=Math.abs(mEndTouchY-mStartTouchY)){
					float x1=mEndTouchX-mStartTouchX;
					float x2=mEndTouchX2-mStartTouchX2;
					if(x1>0 ^ x2>0){
						showThumView(true);
						float xx2=Math.abs(mEndTouchX-mEndTouchX2);
						float xx1=Math.abs(mStartTouchX-mStartTouchX2);
						//xx1>xx2:缩小,xx1<xx2:放大
						doScale(xx2-xx1, false, true);
					}
				}
				else{
					float y1=mEndTouchY-mStartTouchY;
					float y2=mEndTouchY2-mStartTouchY2;
					if(y1>0 ^ y2>0){
						showThumView(true);
						float yy2=Math.abs(mEndTouchY-mEndTouchY2);
						float yy1=Math.abs(mStartTouchY-mStartTouchY2);
						//yy1>yy2:缩小,yy1<yy2:放大
						doScale(yy2-yy1, true, true);
					}
				}
			}
			else{
				if(!mIsZoomAction)
					doMove(mEndTouchX-mStartTouchX,mEndTouchY-mStartTouchY, true);
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			Log.i(TAG, "ACTION_POINTER_DOWN point:"+ev.getPointerCount());
			mIsZoomDown = true;
			mStartTouchX = ev.getX(0)-mMarginLeft;
			mStartTouchY = ev.getY(0)-mMarginTop;
			mEndTouchX = mStartTouchX;
			mEndTouchY = mStartTouchY;
			if(ev.getPointerCount()>1){
				mStartTouchX2 = ev.getX(1)-mMarginLeft;
				mStartTouchY2 = ev.getY(1)-mMarginTop;
				mEndTouchX2 = mStartTouchX2;
				mEndTouchY2 = mStartTouchY2;
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			Log.i(TAG, "ACTION_POINTER_UP point:"+ev.getPointerCount());
			mIsZoomAction = true;
			mEndTouchX = ev.getX(0)-mMarginLeft;
			mEndTouchY = ev.getY(0)-mMarginTop;
			twoPoint = ev.getPointerCount()>1;
			if(twoPoint){
				mEndTouchX2 = ev.getX(1)-mMarginLeft;
				mEndTouchY2 = ev.getY(1)-mMarginTop;
			}
//			if(Math.abs(mEndTouchX-mStartTouchX)<TOUCH_DELTA && Math.abs(mEndTouchY-mStartTouchY)<TOUCH_DELTA){
//				//on click event
//				doClick(mStartTouchX, mStartTouchY);
//			}else 
			if(twoPoint){
				boolean mustAnimation=false;
				if(Math.abs(mEndTouchX-mStartTouchX)>=Math.abs(mEndTouchY-mStartTouchY)){
					float x1=mEndTouchX-mStartTouchX;
					float x2=mEndTouchX2-mStartTouchX2;
					if(x1>0 ^ x2>0){
						showThumView(true);
						float xx2=Math.abs(mEndTouchX-mEndTouchX2);
						float xx1=Math.abs(mStartTouchX-mStartTouchX2);
						//xx1>xx2:缩小,xx1<xx2:放大
						mustAnimation=doScale(xx2-xx1, false, true);
					}
					mustAnimation = mustAnimation || checkMustScaleAnimation(false);
					if(mustAnimation)
						startScaleAnimation(false);
				}
				else{
					float y1=mEndTouchY-mStartTouchY;
					float y2=mEndTouchY2-mStartTouchY2;
					if(y1>0 ^ y2>0){
						showThumView(true);
						float yy2=Math.abs(mEndTouchY-mEndTouchY2);
						float yy1=Math.abs(mStartTouchY-mStartTouchY2);
						//yy1>yy2:缩小,yy1<yy2:放大
						mustAnimation =doScale(yy2-yy1, true, true);
						if(mustAnimation)
							startScaleAnimation(true);
					}
					mustAnimation = mustAnimation || checkMustScaleAnimation(true);
					if(mustAnimation)
						startScaleAnimation(true);
				}
			}
			else{
				doMove(mEndTouchX-mStartTouchX,mEndTouchY-mStartTouchY, true);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.i(TAG, "ACTION_CANCEL point:"+ev.getPointerCount());
			break;
		}
		return true;
	}
	private SelectedSeatInfo checkSeatOfXY(float x,float y){
		int col = -1,row = -1;
		if(x<mOffsetLeft || y<mOffsetTop+mCinemaTitleHeight + mSeatOffsetTop || mColumns==0)
			return null;
		float xx;
		int mid = mColumns/2;
		float dx = mOffsetLeft+mid*(mCurrentSeatWidth + mHorizontalSpace)-(mid>0?mHorizontalSpace:0);
		if(x<=dx){
			col = (int)((x-mOffsetLeft)/(mCurrentSeatWidth + mHorizontalSpace));
			xx = (x-mOffsetLeft)-col*(mCurrentSeatWidth + mHorizontalSpace);
		}
		else{
			col = (int)((x-mOffsetLeft-dx-mCenterRegionWidth)/(mCurrentSeatWidth + mHorizontalSpace));
			xx = (x-mOffsetLeft-dx-mCenterRegionWidth)-col*(mCurrentSeatWidth + mHorizontalSpace);
			col += mid;
		}
		if(xx<0 || xx>mCurrentSeatWidth)
			col=-1;
		float yy = y-(mOffsetTop+mCinemaTitleHeight + mSeatOffsetTop);
		row = (int)(yy/(mCurrentSeatHeight + mVerticalSpace));
		yy = yy - row*(mCurrentSeatHeight + mVerticalSpace);
		if(yy<0 || yy>mCurrentSeatHeight)
			row =-1;
		if(col>=0 && col<mColumns && row>=0 && row<mRows)
			return new SelectedSeatInfo(col,row);
		return null;
	}
	private void doClick(float x, float y){
		SeatStatus seatStatus=SeatStatus.SS_NONE;
		SelectedSeatInfo seatInfo=checkSeatOfXY(x/mZoom-mMoveOffsetLeft,y/mZoom-mMoveOffsetTop);
		if(seatInfo!=null){
			seatStatus=changeSeatStatus(seatInfo.getCol(),seatInfo.getRow());
		}
		if(seatInfo!=null && mOnSeatClickListener!=null)
			mOnSeatClickListener.onSeatClick(seatInfo.getCol(), seatInfo.getRow(),seatStatus);
	}
	private boolean doScale(float delta, boolean vertical, boolean needAnimation){
		boolean mustAnimation = false;
		if(vertical){
			if((mViewHeight+delta)/mViewHeight>1){
				if(mCurrentSeatHeight*mZoom*(mViewHeight+delta)/mViewHeight>mMaxSeatHeight){
					if(needAnimation){
						mZoom = mZoom*(mViewHeight+delta)/mViewHeight;
						mustAnimation = true;
					}
					else
						mZoom = mMaxSeatHeight/mCurrentSeatHeight;
				}
				else
					mZoom = mZoom*(mViewHeight+delta)/mViewHeight;
			}
			else{
				if(mCurrentSeatHeight*mZoom*(mViewHeight+delta)/mViewHeight<mMinSeatHeight){
					if(needAnimation){
						mZoom = mZoom*(mViewHeight+delta)/mViewHeight;
						mustAnimation = true;
					}
					else 
						mZoom = mMinSeatHeight/mCurrentSeatHeight;
				}
				else
					mZoom = mZoom*(mViewHeight+delta)/mViewHeight;
			}
		}
		else{
			if((mViewWidth+delta)/mViewWidth>1){
				if(mCurrentSeatWidth*mZoom*(mViewWidth+delta)/mViewWidth>mMaxSeatWidth){
					if(needAnimation){
						mZoom = mZoom*(mViewWidth+delta)/mViewWidth;
						mustAnimation = true;
					}
					else mZoom = mMaxSeatWidth/mCurrentSeatWidth;
				}
				else
					mZoom = mZoom*(mViewWidth+delta)/mViewWidth;
			}
			else{
				if(mCurrentSeatWidth*mZoom*(mViewWidth+delta)/mViewWidth<mMinSeatWidth){
					if(needAnimation){
						mZoom = mZoom*(mViewWidth+delta)/mViewWidth;
						mustAnimation = true;
					}
					else mZoom = mMinSeatWidth/mCurrentSeatWidth;
				}
				else
					mZoom = mZoom*(mViewWidth+delta)/mViewWidth;
			}
		}
		this.invalidate();
		return mustAnimation;
	}
	private boolean checkMustScaleAnimation(boolean vertical){
		float minZoom,maxZoom;
		if(vertical){
			maxZoom= mMaxSeatHeight/mCurrentSeatHeight;
			minZoom = mMinSeatHeight/mCurrentSeatHeight;
		}
		else {
			maxZoom = mMaxSeatWidth / mCurrentSeatWidth;
			minZoom = mMinSeatWidth / mCurrentSeatWidth;
		}
		return mZoom<minZoom || mZoom>maxZoom;
	}
	private void startScaleAnimation(boolean vertical){
		if(mAnimationLocked) return;
		mAnimationLocked = true;
		float minZoom,maxZoom;
		if(vertical){
			maxZoom= mMaxSeatHeight/mCurrentSeatHeight;
			minZoom = mMinSeatHeight/mCurrentSeatHeight;
		}
		else {
			maxZoom = mMaxSeatWidth / mCurrentSeatWidth;
			minZoom = mMinSeatWidth / mCurrentSeatWidth;
		}
		int n = 0;
		if (mZoom > maxZoom) {
			mZoomTotalDelta = maxZoom - mZoom;
			n = (int)(-mZoomTotalDelta*10);
			n = Math.max(Math.min(n, ZOOM_ANIMATION_MAX_NUM), ZOOM_ANIMATION_MIN_NUM);
			mStepX = mZoomTotalDelta/n;
			mAnimationDuration = ZOOM_ANIMATION_STEP_TIME;
		} else if (mZoom < minZoom) {
			mZoomTotalDelta = minZoom - mZoom;
			n = (int)(mZoomTotalDelta*10);
			n = Math.max(Math.min(n, ZOOM_ANIMATION_MAX_NUM), ZOOM_ANIMATION_MIN_NUM);
			mStepX = mZoomTotalDelta/n;
			mAnimationDuration = ZOOM_ANIMATION_STEP_TIME;
		}
		if(n>0){
			android.os.Message message=new android.os.Message();
			message.what = HANDLE_ZOOM_SEATS;
			message.arg1 = n;
			message.arg2 = 0;
			mHandler.sendMessageDelayed(message, mAnimationDuration);
		}
	}
	private boolean checkMustTranslateAnimation(){
		float acturex=mOffsetLeft+mOffsetRight + mColumns*mCurrentSeatWidth + Math.max(0, mColumns-2)*mHorizontalSpace +(mColumns>1?mCenterRegionWidth:0);
		float acturey=mOffsetTop+mOffsetBottom+mCinemaTitleHeight + mSeatOffsetTop +mRows*mCurrentSeatHeight+Math.max(0, mRows-1)*mVerticalSpace;
		boolean mustAnimation = mMoveOffsetLeft>0 || (mViewWidth/mZoom-mMoveOffsetLeft>acturex);
		return mustAnimation || mMoveOffsetTop>0 || (mViewHeight/mZoom-mMoveOffsetTop>acturey);
	}
	private void startTranslateAnimation(float xVelocity, float yVelocity){
		if(mAnimationLocked)
			return;
		mAnimationLocked = true;
		float multiple = 1f;
		//影院实际长宽
		float acturex=mOffsetLeft+mOffsetRight + mColumns*mCurrentSeatWidth + Math.max(0, mColumns-2)*mHorizontalSpace +(mColumns>1?mCenterRegionWidth:0);
		float acturey=mOffsetTop+mOffsetBottom+mCinemaTitleHeight + mSeatOffsetTop +mRows*mCurrentSeatHeight+Math.max(0, mRows-1)*mVerticalSpace;
		//拖动偏移量 和 速度偏移量叠加计算 动画偏移量
		mTranslateFirstDeltaX = 0;
		if(mMoveOffsetLeft>0){
			mTranslateFirstDeltaX = -mMoveOffsetLeft;
		}
		else if(mViewWidth/mZoom-mMoveOffsetLeft>acturex){
			mTranslateFirstDeltaX = mViewWidth/mZoom-mMoveOffsetLeft-acturex;
		}
		mTranslateSecondDeltaX = 0;
		float xDelta = Math.min(Math.abs(xVelocity)*multiple, mViewWidth/2);
		if(xDelta > 0){
			if(mTranslateFirstDeltaX==0){
				mTranslateFirstDeltaX = xVelocity>0?xDelta:-xDelta;
			}
			else if(-mTranslateFirstDeltaX>0 ^ xVelocity>0){
				if (Math.abs(mTranslateFirstDeltaX) > xDelta){
					mTranslateSecondDeltaX = 0f;
				}
				else{
					if(mTranslateFirstDeltaX<0){
						mTranslateFirstDeltaX -= xDelta/3;
						mTranslateSecondDeltaX = xDelta/3;
					}
					else{
						mTranslateFirstDeltaX += xDelta/3;
						mTranslateSecondDeltaX = -xDelta/3;
					}
				}
			}
			else{
				if(mTranslateFirstDeltaX<0){
					mTranslateSecondDeltaX = mTranslateFirstDeltaX-xDelta/2;
					mTranslateFirstDeltaX = xDelta/2;
				}
				else{
					mTranslateSecondDeltaX = mTranslateFirstDeltaX+xDelta/2;
					mTranslateFirstDeltaX = -xDelta/2;
				}
			}
		}
		float xTotal =Math.abs(mTranslateFirstDeltaX)+Math.abs(mTranslateSecondDeltaX);
		int m = (int)(xTotal/(mViewWidth/15));
		mTranslateFirstDeltaY = 0;
		if(mMoveOffsetTop>0){
			mTranslateFirstDeltaY = -mMoveOffsetTop;
		}
		else if(mViewHeight/mZoom-mMoveOffsetTop>acturey){
			mTranslateFirstDeltaY = mViewHeight/mZoom-mMoveOffsetTop-acturey;
		}
		mTranslateSecondDeltaY = 0;
		float yDelta = Math.min(Math.abs(yVelocity)*multiple, mViewHeight/2);
		if(yDelta>0){
			if(mTranslateFirstDeltaY==0){
				mTranslateFirstDeltaY = yVelocity>0?yDelta:-yDelta;
			}
			else if(-mTranslateFirstDeltaY>0 ^ xVelocity>0){
				if (Math.abs(mTranslateFirstDeltaY) > yDelta){
					mTranslateSecondDeltaY = 0f;
				}
				else{
					if(mTranslateFirstDeltaY<0){
						mTranslateFirstDeltaY -= yDelta/3;
						mTranslateSecondDeltaY = yDelta/3;
					}
					else{
						mTranslateFirstDeltaY += yDelta/3;
						mTranslateSecondDeltaY = -yDelta/3;
					}
				}
			}
			else{
				if(mTranslateFirstDeltaY<0){
					mTranslateSecondDeltaY = mTranslateFirstDeltaY-yDelta/2;
					mTranslateFirstDeltaY = yDelta/2;
				}
				else{
					mTranslateSecondDeltaY = mTranslateFirstDeltaY+yDelta/2;
					mTranslateFirstDeltaY = -yDelta/2;
				}
			}
		}
		float yTotal =Math.abs(mTranslateFirstDeltaY)+Math.abs(mTranslateSecondDeltaY);
		if(xTotal <= 0 && yTotal <= 0)
			return;
		int n = (int)(yTotal/(mViewHeight/15));
		int num = m>n?m:n;
		num = Math.max(Math.min(num, TRANSLATE_ANIMATION_MAX_NUM), TRANSLATE_ANIMATION_MIN_NUM);
		if(num>0){
//			if(mTranslateSecondDeltaX!=0 || mTranslateSecondDeltaY!=0)
//				++num;
//			mTranslateTotalNum = num+(mTranslateSecondDeltaX!=0?1:0)+(mTranslateSecondDeltaY!=0?1:0);
//			mStepX = xTotal/num;
//			mStepY = yTotal/num;
			calculateArithmeticRatio(xTotal,num,TRANSLATE_ARITHMETIC_RATIO,false);
			calculateArithmeticRatio(yTotal,num,TRANSLATE_ARITHMETIC_RATIO,true);
			android.os.Message message=new android.os.Message();
			message.what = HANDLE_TRANSLATE_SEATS;
			message.arg1 = num;
			message.arg2 = 0;
			mHandler.sendMessageDelayed(message, mAnimationDuration);
		}
	}
	private void calculateArithmeticRatio(float totalDelta, int num, float minmaxRatio, boolean vertical){
		//构造等差数列，计算动画递减量
		//a+0x a+1x a+2x a+3x a+4x a+(n-1)x = b
		//(a+(n-1)x)/a = r
		//na+xn(n-1)/2 = b
		if(vertical){
			if(totalDelta>0){
				mStepY = (2*totalDelta)/((minmaxRatio+1)*num);
				mArithmeticStepY = (minmaxRatio-1)*mStepY/(num-1);
			}
			else{
				mStepY=0;mArithmeticStepY=0;
			}
		}
		else{
			if(totalDelta>0){
				mStepX = (2*totalDelta)/((minmaxRatio+1)*num);
				mArithmeticStepX = (minmaxRatio-1)*mStepX/(num-1);
			}
			else{
				mStepX=0;mArithmeticStepX=0;
			}
		}
	}
	private void doMove(float dx, float dy, boolean needAnimation){
		float acturex=mOffsetLeft+mOffsetRight + mColumns*mCurrentSeatWidth + Math.max(0, mColumns-2)*mHorizontalSpace +(mColumns>1?mCenterRegionWidth:0);
		float acturey=mOffsetTop+mOffsetBottom+mCinemaTitleHeight + mSeatOffsetTop +mRows*mCurrentSeatHeight+Math.max(0, mRows-1)*mVerticalSpace;
		mMoveOffsetLeft+=dx/mZoom;
		if(!needAnimation){
			if(acturex>mViewWidth/mZoom){
				mMoveOffsetLeft = Math.max(Math.min(0, mMoveOffsetLeft), mViewWidth/mZoom-acturex);
			}
			else{
				mMoveOffsetLeft = Math.min(Math.max(0, mMoveOffsetLeft), mViewWidth/mZoom-acturex);
			}
		}
		mMoveOffsetTop+=dy/mZoom;
		if(!needAnimation){
			if(acturey>mViewHeight/mZoom){
				mMoveOffsetTop = Math.max(Math.min(0, mMoveOffsetTop), mViewHeight/mZoom-acturey);
			}
			else{
				mMoveOffsetTop = Math.min(Math.max(0, mMoveOffsetTop), mViewHeight/mZoom-acturey);
			}
		}
		this.invalidate();
	}
	private SeatStatus changeSeatStatus(int col, int row){
		if(col<0 || row<0 || mColumns==0 || mRows==0)
			return SeatStatus.SS_NONE;
		SeatStatus seatStatus=selectSeat(col,row);
		float x1 = mOffsetLeft+mMoveOffsetLeft;
		float y1 = mOffsetTop+mCinemaTitleHeight + mSeatOffsetTop+mMoveOffsetTop;
		int mid = mColumns/2;
		if(col<mid){
			x1+=col*(mCurrentSeatWidth + mHorizontalSpace);
		}
		else{
			x1+=col*mCurrentSeatWidth+Math.max(0, mid-1)*mHorizontalSpace+(col-mid)*mHorizontalSpace+mCenterRegionWidth;
		}
		y1+=row*(mCurrentSeatHeight + mVerticalSpace);
		this.invalidate((int)(x1*mZoom), (int)(y1*mZoom), (int)((x1+mCurrentSeatWidth)*mZoom), (int)((y1+mCurrentSeatHeight)*mZoom));
		return seatStatus;
	}
	private SeatStatus selectSeat(int col, int row){
		if(col<0 || row<0 || mColumns==0 || mRows==0)
			return SeatStatus.SS_NONE;
		if(row<mSeatConditions.size()){
			ArrayList<SeatStatus> column=mSeatConditions.get(row);
			if(column!=null && col<column.size()){
				SeatStatus status = column.get(col);
				if(status==SeatStatus.SS_NORMAL || status==SeatStatus.SS_CHECKED){
					int index = findIndexOfColRow(col,row);
					if(status==SeatStatus.SS_NORMAL){
						if(mSelectedSeats.size()>=mSelectedMax){
							return SeatStatus.SS_OVERFLOW;
						}
						column.set(col, SeatStatus.SS_CHECKED);
						if(index<0){
							mSelectedSeats.add(new SelectedSeatInfo(col,row));
						}
						return SeatStatus.SS_CHECKED;
					}
					else{
						column.set(col, SeatStatus.SS_NORMAL);
						if(index>=0)
							mSelectedSeats.remove(index);
						return SeatStatus.SS_NORMAL;
					}
				}
			}
		}
		return SeatStatus.SS_NONE;
	}
	private int findIndexOfColRow(int col,int row){
		boolean success=false;
		int i=0,sz=mSelectedSeats.size();
		while(!success && i<sz){
			SelectedSeatInfo seatInfo=mSelectedSeats.get(i);
			if(seatInfo!=null && seatInfo.getCol()==col && seatInfo.getRow()==row){
				success=true;
			}
			else
				++i;
		}
		return success?i:-1;
	}
	public void clearSelectedSeats(){
		int sz=mSelectedSeats.size();
		for(int i=0;i<sz;i++){
			SelectedSeatInfo seatInfo= mSelectedSeats.get(i);
			if(seatInfo!=null)
				selectSeat(seatInfo.getCol(),seatInfo.getRow());
		}
		mSelectedSeats.clear();
	}
	public ArrayList<SelectedSeatInfo> getSelectedSeats(){
		return this.mSelectedSeats;
	}
	public int getSelectedMax(){
		return this.mSelectedMax;
	}
	public void showThumView(boolean show){
		mHandler.removeMessages(HANDLE_CLOSE_THUM);
		if(show){
			mShowScaleMap = true;
			invalidate();
			android.os.Message msg=new android.os.Message();
			msg.what = HANDLE_CLOSE_THUM;
			mHandler.sendMessageDelayed(msg, this.mThumShowDelay);
		}
		else{
			if(mShowScaleMap){
				mShowScaleMap = false;
				invalidate();
			}
		}
	}
	private final static int HANDLE_CLOSE_THUM = 100;
	private final static int HANDLE_TRANSLATE_SEATS = 101;
	private final static int HANDLE_ZOOM_SEATS = 102;
	private android.os.Handler mHandler=new android.os.Handler(){
		@Override
		public void handleMessage(android.os.Message msg){
			switch(msg.what){
			case HANDLE_CLOSE_THUM:
				mShowScaleMap = false;
				invalidate();
				break;
			case HANDLE_TRANSLATE_SEATS:
				doTranslateNextPosition(msg);
				break;
			case HANDLE_ZOOM_SEATS:
				doZoomNextScale(msg);
				break;
			}
		}
	};
//	private float calclateStep(float translate, float step){
//		float trans = Math.abs(translate);
//		if(trans>step && trans<(step+step))
//			return (float)(trans*0.7);
//		return step;
//	}
	private void doTranslateNextPosition(android.os.Message msg){
		int total = msg.arg1;
		int index = msg.arg2;
		float stepX=mStepX + (total-index)*mArithmeticStepX;
		float delta=0;
		if(mTranslateFirstDeltaX>0){
//			stepX=calclateStep(mTranslateFirstDeltaX, stepX);
			mMoveOffsetLeft += Math.min(stepX, mTranslateFirstDeltaX);
			mTranslateFirstDeltaX = Math.max(0, mTranslateFirstDeltaX-stepX);
			delta = Math.min(0, mTranslateFirstDeltaX-stepX);
		}
		else if(mTranslateFirstDeltaX<0){
//			stepX=calclateStep(mTranslateFirstDeltaX, stepX);
			mMoveOffsetLeft += Math.max(-stepX, mTranslateFirstDeltaX);
			mTranslateFirstDeltaX = Math.min(0, mTranslateFirstDeltaX+stepX);
			delta = Math.max(0, mTranslateFirstDeltaX+stepX);
		}
		if(delta!=0 || mTranslateFirstDeltaX==0){
			stepX = delta!=0?Math.abs(delta):stepX;
			if(mTranslateSecondDeltaX>0){
//				stepX=calclateStep(mTranslateSecondDeltaX, stepX);
				mMoveOffsetLeft += Math.min(stepX, mTranslateSecondDeltaX);
				mTranslateSecondDeltaX = Math.max(0, mTranslateSecondDeltaX-stepX);
			}
			else if(mTranslateSecondDeltaX<0){
//				stepX=calclateStep(mTranslateSecondDeltaX, stepX);
				mMoveOffsetLeft += Math.max(-stepX, mTranslateSecondDeltaX);
				mTranslateSecondDeltaX = Math.min(0, mTranslateSecondDeltaX+stepX);
			}
		}
		delta = 0;
		float stepY=mStepY+(total-index)*mArithmeticStepY;
		if(mTranslateFirstDeltaY>0){
//			stepY=calclateStep(mTranslateFirstDeltaY, stepY);
			mMoveOffsetTop += Math.min(stepY, mTranslateFirstDeltaY);
			mTranslateFirstDeltaY = Math.max(0, mTranslateFirstDeltaY-stepY);
			delta = Math.min(0, mTranslateFirstDeltaY-stepY);
		}
		else if(mTranslateFirstDeltaY<0){
//			stepY=calclateStep(mTranslateFirstDeltaY, stepY);
			mMoveOffsetTop += Math.max(-stepY, mTranslateFirstDeltaY);
			mTranslateFirstDeltaY = Math.min(0, mTranslateFirstDeltaY+stepY);
			delta = Math.max(0, mTranslateFirstDeltaY+stepY);
		}
		if(delta!=0 || mTranslateFirstDeltaY==0){
			stepY = delta!=0?Math.abs(delta):stepY;
			if(mTranslateSecondDeltaY>0){
//				stepY=calclateStep(mTranslateSecondDeltaY, stepY);
				mMoveOffsetTop += Math.min(stepY, mTranslateSecondDeltaY);
				mTranslateSecondDeltaY = Math.max(0, mTranslateSecondDeltaY-stepY);
			}
			else if(mTranslateSecondDeltaY<0){
//				stepY=calclateStep(mTranslateSecondDeltaY, stepY);
				mMoveOffsetTop += Math.max(-stepY, mTranslateSecondDeltaY);
				mTranslateSecondDeltaY = Math.min(0, mTranslateSecondDeltaY+stepY);
			}
		}
		this.invalidate();
		if(index<total){
			android.os.Message message=new android.os.Message();
			message.what = HANDLE_TRANSLATE_SEATS;
			message.arg1 = total;
			message.arg2 = index+1;
			mHandler.sendMessageDelayed(message, mAnimationDuration);
		}
		else{
			mAnimationLocked = false;
		}
	}
	private void doZoomNextScale(android.os.Message msg){
		int total = msg.arg1;
		int index = msg.arg2;
		mZoom = mZoom + mStepX;
		this.invalidate();
		if(index<total){
			android.os.Message message=new android.os.Message();
			message.what = HANDLE_ZOOM_SEATS;
			message.arg1 = total;
			message.arg2 = index+1;
			mHandler.sendMessageDelayed(message, mAnimationDuration);
		}
		else{
			mAnimationLocked = false;
			if(checkMustTranslateAnimation()){
				startTranslateAnimation(0,0);
			}
		}
	}
	public void removeAnimation(){
		mHandler.removeMessages(HANDLE_CLOSE_THUM);
		mHandler.removeMessages(HANDLE_TRANSLATE_SEATS);
		mHandler.removeMessages(HANDLE_ZOOM_SEATS);
	}

	public void setOnSeatClickListener(OnSeatClickListener seatClickListener){
		this.mOnSeatClickListener = seatClickListener;
	}
	public interface OnSeatClickListener{
		public void onSeatClick(int col, int row, SeatStatus seatStatus);
	}
	
}
