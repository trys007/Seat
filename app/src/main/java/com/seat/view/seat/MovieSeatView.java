package com.seat.view.seat;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetricsInt;
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
import com.seat.R;

public class MovieSeatView extends FrameLayout {
	private final static String TAG="MovieSeatView";
	public enum SeatStatus{
		SS_NONE, SS_NORMAL, SS_LOCKED, SS_CHECKED, SS_SOLD, SS_ORDER, SS_OVERFLOW
	}
	public enum AnimationStyle{
		AS_NONE, AS_MOVE, AS_ZOOM
	}
	public enum RowLabelStyle{
		RLS_NUMBER, RLS_UPPER, RLS_LOWER
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
	private final static float TOUCH_DELTA_DP = 8f;
	private static float TOUCH_DELTA = 10f;
	private final static float TOUCH_MIN_VELOCITY = 200f;
	private final static long TRANSLATE_ANIMATION_STEP_TIME = 300;
	private final static long ZOOM_ANIMATION_STEP_TIME = 150;
	private final static float TRANSLATE_ANIMATION_MIN_DELTA = 10;
	private final static int TRANSLATE_ANIMATION_MAX_NUM = 13;//移动动画最多次数
	private final static int TRANSLATE_ANIMATION_MIN_NUM = 3;//移动动画最小次数
	private final static int ZOOM_ANIMATION_MAX_NUM = 6;//缩放动画最多次数
	private final static int ZOOM_ANIMATION_MIN_NUM = 2;//缩放动画最小次数
//	private final static float TRANSLATE_ARITHMETIC_RATIO = 6;//等差数列最小与最大的比值
	private final static float ZOOM_MIN_ZOOM = 0.2f;
	private final static float ZOOM_MAX_ZOOM = 10f;
	private final static int MAX_POINTER_COUNT = 5;
	private int mViewWidth;
	private int mViewHeight;
//	private int mInterViewWidth,mInterViewHeight;
	private float mActureWidth;
	private float mActureHeight;
	private float mActureNoTitleHeight;
	private int mColumns;
	private int mRows;
	private ArrayList<String> mRowLabels;
//	private int mFirstRowIndex;
//	private int mFirstRowLabelIndex;
//	private RowLabelStyle mRowLabelStyle;
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
	private int mThumTitleColor;
	private int mThumCenterLineColor;
	private int mThumLineColor;
	private float mThumLineWidth;
	private int mThumBackgroundColor;
	private int mThumShowDelay;
	private float mThumZoom = 1;

	private String mCinemaTitle;
	private float mCinemaTitleSize;
	private float mCinemaTitleHeight;
	private float mCenterLineWidth;
	private float mCenterRegionWidth;
	private float mMaxBackTranslationX,mMaxBackTranslationY;
	private float mInitialTranslationX,mInitialTranslationY;
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
	private float mScaleTranslationX,mScaleTranslationY;
	private float mStartTouchX,mStartTouchY,mEndTouchX,mEndTouchY;
	private float mStartTouchX2,mStartTouchY2,mEndTouchX2,mEndTouchY2;
	private float mSaveStartTouchX,mSaveStartTouchY;
	private int mSaveTouchIndex1,mSaveTouchIndex2;
	private float mSavePreTouchsX[] = new float[MAX_POINTER_COUNT];
	private float mSavePreTouchsY[] = new float[MAX_POINTER_COUNT];
	
//	private float mMarginLeft,mMarginTop,mMarginRight,mMarginBottom;
	private boolean mThumTitleShow;
//	private boolean mIsZoomAction=false;
	private boolean mAnimationLocked = false;
//	private boolean mAnimationCanceled = false;
	private float mZoomTotalDelta;
//	private float mTranslateFirstDeltaX,mTranslateFirstDeltaY;
//	private float mTranslateSecondDeltaX,mTranslateSecondDeltaY;
//	private float mTranslateTotalNum;
//	private float mStepX,mStepY;
//	private float mArithmeticStepX,mArithmeticStepY;
//	private long mAnimationDuration = TRANSLATE_ANIMATION_STEP_TIME;
//	private AnimationStyle mAnimationStyle = AnimationStyle.AS_NONE;
	private VelocityTracker mVelocityTracker;
	private ArrayList<ArrayList<SeatStatus>> mSeatConditions = null;
	private ArrayList<SelectedSeatInfo> mSelectedSeats = new ArrayList<SelectedSeatInfo>();
	private OnSeatClickListener mOnSeatClickListener;
	private InterSeatView mInterSeatView;
	private InterRowLabelView mInterRowLabelView;
	private InterThumSeatView mInterThumSeatView;
	private InterThumRectView mInterThumRectView;
//	private InterSeatTranslateAnimation mSeatTranslateAnimation;
	private ObjectAnimator mTranslationXAnimator,mTranslationYAnimator,mScaleXAnimator,mScaleYAnimator;
	private AnimatorSet mTranslationAnimatorSet, mScaleAnimatorSet;
	
	public MovieSeatView(Context context){
		super(context);
		initView();
	}
	public MovieSeatView(Context context, AttributeSet attrs){
		super(context,attrs);
		init(context, attrs);
		initView();
	}
	public MovieSeatView(Context context, AttributeSet attrs, int defStyle){
		super(context,attrs,defStyle);
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
		mMaxBackTranslationX = typedArray.getFraction(R.styleable.SelectSeatView_maxBackTranslationX, 100,100,20);
		mMaxBackTranslationY = typedArray.getFraction(R.styleable.SelectSeatView_maxBackTranslationY, 100,100,20);
		mInitialTranslationX = typedArray.getFraction(R.styleable.SelectSeatView_initialTranslationX, 100,100,0);
		mInitialTranslationY = typedArray.getFraction(R.styleable.SelectSeatView_initialTranslationY, 100,100,0);
		
		mThumWidth = typedArray.getDimension(R.styleable.SelectSeatView_thumWidth, 100);
		mThumHeight = typedArray.getDimension(R.styleable.SelectSeatView_thumHeight, 50);
		mThumBackgroundColor = typedArray.getColor(R.styleable.SelectSeatView_thumBackgroundColor, 0x229932CC);
		mThumLineWidth = typedArray.getDimension(R.styleable.SelectSeatView_thumLineWidth, 2);
		mThumLineColor = typedArray.getColor(R.styleable.SelectSeatView_thumLineColor, blackColor);
		mThumTitleColor = typedArray.getColor(R.styleable.SelectSeatView_thumTitleColor, (~mThumBackgroundColor));
		mThumCenterLineColor = typedArray.getColor(R.styleable.SelectSeatView_thumCenterLineColor, (~mThumBackgroundColor));
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
		mThumTitleShow = typedArray.getBoolean(R.styleable.SelectSeatView_thumTitleShow, false);
		typedArray.recycle();
		addViews(context);
	}
	private void initView(){
		ViewTreeObserver observer = getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mViewHeight = getHeight();
				mViewWidth = getWidth();
//				mInterViewWidth = Math.min(mViewWidth, mInterSeatView.getWidth());
//				mInterViewHeight = Math.min(mViewHeight, mInterSeatView.getHeight());
			}
		});
		mZoom = 1f;
//		mShowScaleMap = true;
	}
	private void addViews(Context context){
		mInterSeatView =new InterSeatView(context);
		LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mInterSeatView.setLayoutParams(lp);
		this.addView(mInterSeatView);
		mInterRowLabelView =new InterRowLabelView(context);
		lp = new LayoutParams((int)mRowLabelWidth, LayoutParams.MATCH_PARENT);
		lp.leftMargin = (int)mRowLabelLeft;
		lp.topMargin = (int)mRowLabelTop;
		lp.bottomMargin = (int)mRowLabelBottom;
		mInterRowLabelView.setLayoutParams(lp);
		this.addView(mInterRowLabelView);
		mInterThumSeatView = new InterThumSeatView(context);
		lp= new LayoutParams((int)mThumWidth, (int)mThumHeight);
		mInterThumSeatView.setLayoutParams(lp);
		this.addView(mInterThumSeatView);
		mInterThumRectView = new InterThumRectView(context);
		lp= new LayoutParams((int)mThumWidth, (int)mThumHeight);
		mInterThumRectView.setLayoutParams(lp);
		this.addView(mInterThumRectView);
		mInterThumSeatView.setVisibility(mShowScaleMap?View.VISIBLE:View.INVISIBLE);
		mInterThumRectView.setVisibility(mShowScaleMap?View.VISIBLE:View.INVISIBLE);
	}
	
	public void initData(){
		
	}
	public void initData(int rows, int cols, int selectedMax, ArrayList<String> rowLabels, ArrayList<ArrayList<SeatStatus>> seats){
		this.mColumns = cols;
		this.mRows = rows;
		mSeatConditions = seats;
		mSelectedMax = selectedMax;
		mRowLabels = rowLabels;
//		mFirstRowIndex = firstRowIndex;
//		mRowLabelStyle = RowLabelStyle.RLS_NUMBER;
//		mFirstRowLabelIndex = 0;
//		if(rowLabel!=null && rowLabel.length()>0){
//			int n = rowLabel.length();
//			char c= rowLabel.charAt(n-1);
//			if(c>='a' && c<='z'){
//				mRowLabelStyle = RowLabelStyle.RLS_LOWER;
//				mFirstRowLabelIndex = (int)(c-'a');
//			}
//			else if(c>='A' && c<='Z'){
//				mRowLabelStyle = RowLabelStyle.RLS_UPPER;
//				mFirstRowLabelIndex = (int)(c-'A');
//			}
//			else if(c>='0' && c<='9'){
//				mRowLabelStyle = RowLabelStyle.RLS_NUMBER;
//				mFirstRowLabelIndex = (int)(c-'0');
//			}
//		}
//		String s = getchar(1);
//		s = getchar(26);
//		s = getchar(29);
//		s = getchar(59);
//		s = getchar(3);		
		mActureWidth = mOffsetLeft+mOffsetRight + mColumns*mCurrentSeatWidth + Math.max(0, mColumns-2)*mHorizontalSpace +(mColumns>1?mCenterRegionWidth:0);
		mActureHeight = mOffsetTop+mOffsetBottom+mCinemaTitleHeight + mSeatOffsetTop +mRows*mCurrentSeatHeight+Math.max(0, mRows-1)*mVerticalSpace;
		if(mThumTitleShow)
			mActureNoTitleHeight = mActureHeight;
		else
			mActureNoTitleHeight = mActureHeight - mCinemaTitleHeight - mSeatOffsetTop;
		setSeatSize(mMoveOffsetLeft, mMoveOffsetTop, 0, 0);
		mMoveOffsetLeft = mViewWidth*mInitialTranslationX/(mZoom*100) - mActureWidth/2;
		mMoveOffsetTop = mViewHeight*mInitialTranslationY/(mZoom*100) - mActureHeight/2;
		mScaleTranslationX = mInterSeatView.getWidth()*(1-mZoom)/(mZoom+mZoom);
		mScaleTranslationY = mInterSeatView.getHeight()*(1-mZoom)/(mZoom+mZoom);
		setSeatSize(mZoom, mMoveOffsetLeft, mMoveOffsetTop);
		mInterRowLabelView.setPosition(mZoom, mMoveOffsetTop);
		mThumZoom = Math.min(mThumWidth/mActureWidth, mThumHeight/mActureNoTitleHeight);
		mThumWidth = mThumZoom*mActureWidth;
		mThumHeight = mThumZoom*mActureNoTitleHeight;
		LayoutParams lp = (LayoutParams)mInterThumSeatView.getLayoutParams();
		lp.width = (int)mThumWidth + 1;
		lp.height = (int)mThumHeight + 1;
		lp.leftMargin = (int)mThumOffsetLeft;
		lp.topMargin = (int)mThumOffsetTop;
		mInterThumSeatView.setLayoutParams(lp);
		mInterThumSeatView.invalidate();
		lp = (LayoutParams)mInterThumRectView.getLayoutParams();
		lp.width = (int)mThumWidth + 1;
		lp.height = (int)mThumHeight + 1;
		lp.leftMargin = (int)mThumOffsetLeft;
		lp.topMargin = (int)mThumOffsetTop;
		mInterThumRectView.setLayoutParams(lp);
		mInterThumRectView.invalidate();
	}
//	private String getchar(int index){
//		int n = (mFirstRowLabelIndex + index - mFirstRowIndex - 1)/26;
//		String label = ""+(char)('a'+(mFirstRowLabelIndex + index - mFirstRowIndex - 1) % 26);
//		if(n>0)
//			label = ""+(char)('a'+(n-1))+label;
//		return label;
//	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	//绘制中心虚线
	private void drawCenterLine(Canvas canvas,boolean drawToThum){
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		if(!drawToThum)
			paint.setColor(mCenterLineColor);
		else
			paint.setColor(mThumCenterLineColor);
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

	private void drawCinemaInfo(Canvas canvas, boolean drawToThum) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(mCinemaTitleSize);
		if(!drawToThum)
			paint.setColor(mCinemaTitleColor);
		else{
			paint.setColor(mThumTitleColor);
		}
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
	private void drawRowLabel(Canvas canvas, float zoom, float offsetTop){
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setTextSize(mRowLabelTextSize);
		paint.setColor(mRowLabelTextColor);
		paint.setTypeface(Typeface.DEFAULT); //设置字体
		RectF rect = new RectF(0, 0, mRowLabelWidth, mViewHeight-mRowLabelTop-mRowLabelBottom);
		if(mRowLabelBackground!=null){
			canvas.drawBitmap(mRowLabelBackground, null, rect, paint);
		}
		if(mRows==0)
			return;
		float y = (mOffsetTop + mCinemaTitleHeight + mSeatOffsetTop + offsetTop)*zoom;
		int from = 0;
		if (y<0){
			from = (int)(-y/((mCurrentSeatHeight+mVerticalSpace)*zoom));
		}
		y += from*(mCurrentSeatHeight+mVerticalSpace)*zoom+mCurrentSeatHeight*zoom/2;
		if(y<0)
			y+=mVerticalSpace*zoom;
		int rowsz = mRowLabels!=null ? mRowLabels.size():0;
		if(rowsz<=0) return;
		float cx = mRowLabelWidth/2;
		for(int i=from;i<mRows;i++){
			if(y>=mRowLabelTop && y<=mViewHeight-mRowLabelBottom){
				String label = i<rowsz?mRowLabels.get(i):null;
				if(rowsz>0 && label!=null && !"".equals(label)){
//					String label="";
//					switch(mRowLabelStyle){
//					case RLS_NUMBER:
//						label = String.valueOf(mFirstRowLabelIndex + i - mFirstRowIndex);
//						break;
//					case RLS_LOWER:{
//						int n = (mFirstRowLabelIndex + i - mFirstRowIndex)/26;
//						label = ""+(char)('a'+(mFirstRowLabelIndex + i - mFirstRowIndex) % 26);
//						if(n>0)
//							label = ""+(char)('a'+(n-1))+label;
//						break;
//					}
//					case RLS_UPPER:
//						int n = (mFirstRowLabelIndex + i - mFirstRowIndex)/26;
//						label = ""+(char)('A'+(mFirstRowLabelIndex + i - mFirstRowIndex) % 26);
//						if(n>0)
//							label = ""+(char)('A'+(n-1))+label;
//						break;
//					}
					
					FontMetricsInt fm = paint.getFontMetricsInt();
					float width = paint.measureText(label);
					float x = cx - width/2;
					float rtop=y-mRowLabelTop - mRowLabelTextSize/2;
					float rbottom=y-mRowLabelTop + mRowLabelTextSize/2;
					float baseline = rtop + (rbottom - rtop - fm.bottom + fm.top) / 2 - fm.top;
					canvas.drawText(label, x, baseline, paint);
				}
			}
			y += (mCurrentSeatHeight+mVerticalSpace)*zoom;
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

	private void showThumView(boolean show){
		if(mShowScaleMap ^ show){
			mShowScaleMap = !mShowScaleMap;
			mInterThumSeatView.setVisibility(mShowScaleMap?View.VISIBLE:View.INVISIBLE);
			mInterThumRectView.setVisibility(mShowScaleMap?View.VISIBLE:View.INVISIBLE);
			if(mShowScaleMap){
				mInterThumSeatView.invalidate();
				mInterThumRectView.invalidate();
			}
		}
		else{
			if(mShowScaleMap){
				mInterThumRectView.invalidate();
			}
		}
	}
	private void drawThumSeats(Canvas canvas){
//		float zoom = Math.min(mThumWidth/mActureWidth, mThumHeight/mActureHeight);
		Paint paint=new Paint();
		paint.setColor(mThumBackgroundColor);
		canvas.drawRect(new RectF(0,0,mThumWidth,mThumHeight), paint);
		canvas.save();
		canvas.translate(mThumOffsetLeft, mThumOffsetTop);
		canvas.scale(mThumZoom, mThumZoom);
		
		if(mThumTitleShow){
			drawCinemaInfo(canvas,true);
			drawCenterLine(canvas,true);
		}
		
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
		float translationLeft = mMoveOffsetLeft/mZoom + mScaleTranslationX;
		float translationTop = mMoveOffsetTop/mZoom + mScaleTranslationY;
		Paint paint=new Paint();
		paint.setStrokeWidth(mThumLineWidth);
		paint.setColor(mThumLineColor);
//		float zoom = Math.min(mThumWidth/mActureWidth, mThumHeight/mActureHeight);
		float x1 = -Math.min(0, translationLeft)*mThumZoom;
		float x2 = Math.min(mViewWidth/mZoom - translationLeft, mActureWidth)*mThumZoom;
//		float x2 = Math.min((Math.max(0, translationLeft)+mActureWidth), mViewWidth/mZoom)*mThumZoom;//-(translationLeft>0?translationLeft:0)
//		x2+=x1;
		float y1,y2;
		if(mThumTitleShow){
			y1 = -Math.min(0, translationTop)*mThumZoom;
			y2 = Math.min(mViewHeight/mZoom - translationTop, mActureHeight)*mThumZoom;
		}
		else{
			y1 = Math.min(0, translationTop);
			if(-y1>mOffsetTop)
				y1 = mOffsetTop+Math.max(0, -y1-(mOffsetTop + mCinemaTitleHeight + mSeatOffsetTop));
			else
				y1 = -y1;
			float y = translationTop;//Math.min(0, translationTop);
			if(mViewHeight/mZoom-y>mOffsetTop)
				y2 = mOffsetTop + Math.max(0, mViewHeight/mZoom-y-(mOffsetTop + mCinemaTitleHeight + mSeatOffsetTop));
			else
				y2 = mViewHeight/mZoom-y;
//			if(translationTop>0)
//				y2 -= translationTop;
			y2=Math.min(y2,mActureNoTitleHeight)*mThumZoom;
			y1=y1*mThumZoom;
		}
		canvas.drawLines(new float[]{x1,y1,x2,y1,  x2,y1,x2,y2,  x1,y2,x2,y2, x1,y1,x1,y2}, paint);
	}
	private RectF getThumSeatRect(int col, int row) {
		int centre = mColumns/2;
		float cx = mOffsetLeft + (col>=centre?(mCenterRegionWidth-mHorizontalSpace):0);
		float cy = mOffsetTop + (mThumTitleShow ? (mCinemaTitleHeight + mSeatOffsetTop) : 0);
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
	private void test(){
		Log.i(TAG, "0height="+mInterSeatView.getHeight()+",top="+mInterSeatView.getTop()+",bottom="+mInterSeatView.getBottom()+"1translatey="+mInterSeatView.getTranslationY()+",scaley="+mInterSeatView.getScaleY());
		mInterSeatView.setScaleY(2);
		mInterSeatView.setScaleX(2);
		Log.i(TAG, "1height="+mInterSeatView.getHeight()+",top="+mInterSeatView.getTop()+",bottom="+mInterSeatView.getBottom()+"1translatey="+mInterSeatView.getTranslationY()+",scaley="+mInterSeatView.getScaleY());
		mInterSeatView.setTranslationX(mInterSeatView.getWidth()/2);
		mInterSeatView.setTranslationY(mInterSeatView.getHeight()/2);
		Log.i(TAG, "2height="+mInterSeatView.getHeight()+",top="+mInterSeatView.getTop()+",bottom="+mInterSeatView.getBottom()+"1translatey="+mInterSeatView.getTranslationY()+",scaley="+mInterSeatView.getScaleY());
//		mInterSeatView.setScaleY(3);
		Log.i(TAG, "3height="+mInterSeatView.getHeight()+",top="+mInterSeatView.getTop()+",bottom="+mInterSeatView.getBottom()+"1translatey="+mInterSeatView.getTranslationY()+",scaley="+mInterSeatView.getScaleY());
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();
		mVelocityTracker.addMovement(ev);
		int index1,index2;
		float xVelocity,yVelocity;
		boolean twoPoint = false;
		switch(ev.getAction() & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:
//			mAnimationStyle = AnimationStyle.AS_NONE;
//			mIsZoomAction = false;
			mSaveTouchIndex1 = ev.getPointerId(0);
			mStartTouchX = ev.getX(0);//-mMarginLeft;
			mStartTouchY = ev.getY(0);//-mMarginTop;
			mEndTouchX = mStartTouchX;
			mEndTouchY = mStartTouchY;
			mSaveStartTouchX = mStartTouchX;
			mSaveStartTouchY = mStartTouchY;
			Log.i(TAG, "down point="+ev.getPointerCount()+" "+ev.getPointerId(0)+" x="+mEndTouchX+",y="+mEndTouchY);
			if(ev.getPointerCount()>1){
				mStartTouchX2 = ev.getX(1);//-mMarginLeft;
				mStartTouchY2 = ev.getY(1);//-mMarginTop;
				mEndTouchX2 = mStartTouchX2;
				mEndTouchY2 = mStartTouchY2;
				mSaveTouchIndex2 = ev.getPointerId(1);
			}
			else
				mSaveTouchIndex2 = -1;
			savePointers(ev);
			break;
		case MotionEvent.ACTION_UP:
			Log.i(TAG, "UP point="+ev.getPointerCount()+" "+ev.getPointerId(0)+" x="+ev.getX(0)+",y="+ev.getY(0));
			showThumView(false);
//			test();
//			if(!mTwoPointerCanMove && mIsZoomAction){
////				if(checkMustTranslateAnimation()){
////					startTranslateAnimation(0,0);
////				}
//				if(mVelocityTracker!=null){
//					mVelocityTracker.recycle();
//					mVelocityTracker=null;
//				}
//				break;
//			}
			mVelocityTracker.computeCurrentVelocity(1000);
			xVelocity = mVelocityTracker.getXVelocity(0);
			yVelocity = mVelocityTracker.getYVelocity(0);
//			if (xVelocity>=TOUCH_MIN_VELOCITY || yVelocity>=TOUCH_MIN_VELOCITY){
//				mAnimationStyle = AnimationStyle.AS_MOVE;
//			}
			index1= ev.getPointerId(0);
			if(index1!=mSaveTouchIndex1 && index1<MAX_POINTER_COUNT){
				mStartTouchX = mSavePreTouchsX[index1];
				mStartTouchY = mSavePreTouchsY[index1];
			}
			else{
				mStartTouchX = mEndTouchX;
				mStartTouchY = mEndTouchY;
			}
			mEndTouchX = ev.getX(0);//-mMarginLeft;
			mEndTouchY = ev.getY(0);//-mMarginTop;
			twoPoint = ev.getPointerCount()>1;
			if(twoPoint){
				index2 = ev.getPointerId(1);
				if(index2!=mSaveTouchIndex2){
					mStartTouchX2 = mSavePreTouchsX[index2];
					mStartTouchY2 = mSavePreTouchsY[index2];
				}
				else{
					mStartTouchX2 = mEndTouchX2;
					mStartTouchY2 = mEndTouchY2;
				}
				mEndTouchX2 = ev.getX(1);//-mMarginLeft;
				mEndTouchY2 = ev.getY(1);//-mMarginTop;
				Log.i(TAG, "sx="+mStartTouchX+",sy="+mStartTouchY+",sx2="+mStartTouchX2+",sy2="+mStartTouchY2);
			}
			else
				Log.i(TAG, "sx="+mStartTouchX+",sy="+mStartTouchY);
			if(Math.abs(mEndTouchX-mSaveStartTouchX)<TOUCH_DELTA && Math.abs(mEndTouchY-mSaveStartTouchY)<TOUCH_DELTA){
				//on click event
				doClick(mSaveStartTouchX, mSaveStartTouchY);
				startSeatsAnimation(false, 0, 0);
			}
			else if(twoPoint){
				if(Math.abs(mEndTouchX-mStartTouchX)>=Math.abs(mEndTouchY-mStartTouchY)){
					float x1=mEndTouchX-mStartTouchX;
					float x2=mEndTouchX2-mStartTouchX2;
					if(x1>0 ^ x2>0){
						doScale(x1-x2, false, false);
					}
					startSeatsAnimation(false, 0, 0);
				}
				else{
					float y1=mEndTouchY-mStartTouchY;
					float y2=mEndTouchY2-mStartTouchY2;
					if(y1>0 ^ y2>0){
						doScale(y1-y2, true, false);
					}
					startSeatsAnimation(true, 0, 0);
				}
			}
			else{
				doMove(mEndTouchX-mStartTouchX,mEndTouchY-mStartTouchY, true);
//				if(mAnimationStyle==AnimationStyle.AS_MOVE)
				startSeatsAnimation(false, Math.abs(xVelocity)>=TOUCH_MIN_VELOCITY?xVelocity/8:0, Math.abs(yVelocity)>=TOUCH_MIN_VELOCITY?yVelocity/8:0);
//				startTranslateAnimation(Math.abs(xVelocity)>=TOUCH_MIN_VELOCITY?xVelocity/8:0, Math.abs(yVelocity)>=TOUCH_MIN_VELOCITY?yVelocity/8:0);
			}
			if(mVelocityTracker!=null){
				mVelocityTracker.recycle();
				mVelocityTracker=null;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			Log.i(TAG, "move point="+ev.getPointerCount()+" "+ev.getPointerId(0)+" x="+ev.getX(0)+",y="+ev.getY(0)+(ev.getPointerCount()>1?(","+ev.getPointerId(1)+" x2="+ev.getX(1)+",y2="+ev.getY(1)):""));
			if(Math.abs(ev.getX(0)-mSaveStartTouchX)<TOUCH_DELTA && Math.abs(ev.getY(0)-mSaveStartTouchY)<TOUCH_DELTA)
				break;
			doMove(ev,false);
			savePointers(ev);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			index1 = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)>>MotionEvent.ACTION_POINTER_INDEX_SHIFT;
//			if(index1>1)
//				break;
			index1 = ev.getPointerId(index1);
			Log.i(TAG, "POINTER_DOWN point:"+ev.getPointerCount()+" "+ev.getPointerId(0)+" "+index1+" x="+ev.getX(0)+",y="+ev.getY(0)+(ev.getPointerCount()>1?(","+ev.getPointerId(1)+" x2="+ev.getX(1)+",y2="+ev.getY(1)):""));
			doMove(ev, true);
//			mStartTouchX = ev.getX(0);//-mMarginLeft;
//			mStartTouchY = ev.getY(0);//-mMarginTop;
//			mEndTouchX = mStartTouchX;
//			mEndTouchY = mStartTouchY;
//			if(ev.getPointerCount()>1){
//				mStartTouchX2 = ev.getX(1);//-mMarginLeft;
//				mStartTouchY2 = ev.getY(1);//-mMarginTop;
//				mEndTouchX2 = mStartTouchX2;
//				mEndTouchY2 = mStartTouchY2;
//			}
			savePointers(ev);
			break;
		case MotionEvent.ACTION_POINTER_UP:
			doMove(ev, false);
			index1 = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)>>MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			index1 = ev.getPointerId(index1);
			savePointers(ev);
			mSavePreTouchsX[index1] = 0;
			mSavePreTouchsY[index1] = 0;
			Log.i(TAG, "POINTER_UP point:"+ev.getPointerCount()+" "+ev.getPointerId(0)+" "+index1+" x="+ev.getX(0)+",y="+ev.getY(0)+(ev.getPointerCount()>1?(","+ev.getPointerId(1)+" x2="+ev.getX(1)+",y2="+ev.getY(1)):""));
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.i(TAG, "ACTION_CANCEL point:"+ev.getPointerCount());
			break;
		}
		
		return true;
	}
	private void savePointers(MotionEvent ev){
		for(int i=0;i<ev.getPointerCount();i++){
			int index = ev.getPointerId(i);
			if(index<MAX_POINTER_COUNT){
				mSavePreTouchsX[index] = ev.getX(i);
				mSavePreTouchsY[index] = ev.getY(i);
			}
		}
	}
	private void doMove(MotionEvent ev, boolean pointerDown){
		int index1= ev.getPointerId(0);
		if(index1!=mSaveTouchIndex1 && index1<MAX_POINTER_COUNT){
			mStartTouchX = mSavePreTouchsX[index1];
			mStartTouchY = mSavePreTouchsY[index1];
		}
		else{
			mStartTouchX = mEndTouchX;
			mStartTouchY = mEndTouchY;
		}
		mEndTouchX = ev.getX(0);//-mMarginLeft;
		mEndTouchY = ev.getY(0);//-mMarginTop;
		boolean twoPoint = ev.getPointerCount()>1;
		
		if(twoPoint){
			int index2 = ev.getPointerId(1);
			if(index2!=mSaveTouchIndex2 && index2<MAX_POINTER_COUNT){
				mStartTouchX2 = mSavePreTouchsX[index2];
				mStartTouchY2 = mSavePreTouchsY[index2];
			}
			else{
				mStartTouchX2 = mEndTouchX2;
				mStartTouchY2 = mEndTouchY2;
			}
			mEndTouchX2 = ev.getX(1);//-mMarginLeft;
			mEndTouchY2 = ev.getY(1);//-mMarginTop;
			Log.i(TAG, "sx="+mStartTouchX+",sy="+mStartTouchY+",sx2="+mStartTouchX2+",sy2="+mStartTouchY2+",i1="+mSaveTouchIndex1+index1+",i2="+mSaveTouchIndex2+index2);
			mSaveTouchIndex2 = index2;
		}
		else
			Log.i(TAG, "sx="+mStartTouchX+",sy="+mStartTouchY+",i1="+mSaveTouchIndex1+index1+",savex="+mSavePreTouchsX[index1]);
		mSaveTouchIndex1 = index1;
		if (pointerDown){
			index1 = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)>>MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			twoPoint = twoPoint && index1 > 1;
//			index1 = ev.getPointerId(index1);
		}
		if(Math.abs(mEndTouchX-mSaveStartTouchX)>=TOUCH_DELTA || Math.abs(mEndTouchY-mSaveStartTouchY)>=TOUCH_DELTA)
			showThumView(true);
		if(twoPoint){
			float x1=mEndTouchX-mStartTouchX;
			float x2=mEndTouchX2-mStartTouchX2;
			float y1=mEndTouchY-mStartTouchY;
			float y2=mEndTouchY2-mStartTouchY2;
			boolean one = Math.abs(Math.abs(x1)-Math.abs(y1))>Math.abs(Math.abs(x2)-Math.abs(y2));
			if (one?(Math.abs(x1)>=Math.abs(y1)):(Math.abs(x2)>=Math.abs(y2))){
				if(x1>0 ^ x2>0){
					//xx1>xx2:缩小,xx1<xx2:放大
					float xx2=Math.abs(mEndTouchX-mEndTouchX2);
					float xx1=Math.abs(mStartTouchX-mStartTouchX2);
					doScale(xx2-xx1, false, true);
				}
				else {
					if(one)
						doMove(x1, y1, true);
					else
						doMove(x2, y2, true);
					if(one)
						Log.i(TAG, "1startx="+mStartTouchX+",endx="+mEndTouchX+",dx="+x1+",starty="+mStartTouchY+",endy="+mEndTouchY+",dy="+y1);
					else
						Log.i(TAG, "2startx="+mStartTouchX2+",endx="+mEndTouchX2+",dx="+x2+",starty="+mStartTouchY2+",endy="+mEndTouchY2+",dy="+y2);
				}
			}
			else{
				if(y1>0 ^ y2>0){
					float yy2=Math.abs(mEndTouchY-mEndTouchY2);
					float yy1=Math.abs(mStartTouchY-mStartTouchY2);
					//yy1>yy2:缩小,yy1<yy2:放大
					doScale(yy2-yy1, true, true);
				}
				else {
					if(one)
						doMove(x1, y1, true);
					else
						doMove(x2, y2, true);
					if(one)
						Log.i(TAG, "1startx="+mStartTouchX+",endx="+mEndTouchX+",dx="+x1+",starty="+mStartTouchY+",endy="+mEndTouchY+",dy="+y1);
					else
						Log.i(TAG, "2startx="+mStartTouchX2+",endx="+mEndTouchX2+",dx="+x2+",starty="+mStartTouchY2+",endy="+mEndTouchY2+",dy="+y2);
				}
			}
			
//			if(Math.abs(mEndTouchX-mStartTouchX)>=Math.abs(mEndTouchY-mStartTouchY)){
//				float x1=mEndTouchX-mStartTouchX;
//				float x2=mEndTouchX2-mStartTouchX2;
//				if(x1>0 ^ x2>0){
////					showThumView(true);
//					float xx2=Math.abs(mEndTouchX-mEndTouchX2);
//					float xx1=Math.abs(mStartTouchX-mStartTouchX2);
//					//xx1>xx2:缩小,xx1<xx2:放大
//					doScale(xx2-xx1, false, true);
//				}
//			}
//			else{
//				float y1=mEndTouchY-mStartTouchY;
//				float y2=mEndTouchY2-mStartTouchY2;
//				if(y1>0 ^ y2>0){
////					showThumView(true);
//					float yy2=Math.abs(mEndTouchY-mEndTouchY2);
//					float yy1=Math.abs(mStartTouchY-mStartTouchY2);
//					//yy1>yy2:缩小,yy1<yy2:放大
//					doScale(yy2-yy1, true, true);
//				}
//			}
		}
		else{
			if(pointerDown && index1==0)
				doMove(mEndTouchX2-mStartTouchX2,mEndTouchY2-mStartTouchY2, true);
			else
//			if(mTwoPointerCanMove || !mIsZoomAction)
				doMove(mEndTouchX-mStartTouchX,mEndTouchY-mStartTouchY, true);
			if(pointerDown && index1==0)
			Log.i(TAG, "1startx="+mStartTouchX2+",endx="+mEndTouchX2+",dx="+(mEndTouchX2-mStartTouchX2)+",starty="+mStartTouchY2+",endy="+mEndTouchY2+",dy="+(mEndTouchY2-mStartTouchY2));
			else
				Log.i(TAG, "1startx="+mStartTouchX+",endx="+mEndTouchX+",dx="+(mEndTouchX-mStartTouchX)+",starty="+mStartTouchY+",endy="+mEndTouchY+",dy="+(mEndTouchY-mStartTouchY));
		}
	}
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		return super.onInterceptTouchEvent(ev);
//	}
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
		resetAnimation();
//		float scaleX = mInterSeatView.getScaleX();
//		float scaleY = mInterSeatView.getScaleY();
		SeatStatus seatStatus=SeatStatus.SS_NONE;
		SelectedSeatInfo seatInfo=checkSeatOfXY(x/mZoom-mMoveOffsetLeft/mZoom-mScaleTranslationX, y/mZoom-mMoveOffsetTop/mZoom-mScaleTranslationY);
		if(seatInfo!=null){
			seatStatus=changeSeatStatus(seatInfo.getCol(),seatInfo.getRow());
		}
		if(seatInfo!=null && mOnSeatClickListener!=null)
			mOnSeatClickListener.onSeatClick(seatInfo.getCol(), seatInfo.getRow(),seatStatus);
	}
	private boolean doScale(float delta, boolean vertical, boolean needAnimation){
		delta *= 2;
		resetAnimation();
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
		if(mZoom < ZOOM_MIN_ZOOM)
			mZoom = ZOOM_MIN_ZOOM;
		else if(mZoom > ZOOM_MAX_ZOOM)
			mZoom = ZOOM_MAX_ZOOM;
//		float offsetX = mActureWidth<mViewWidth/mZoom?Math.min(0,mZoom*mMoveOffsetLeft):(mZoom*mMoveOffsetLeft);
//		float offsetY = mActureHeight<mViewHeight/mZoom? Math.min(0,mZoom*mMoveOffsetTop):(mZoom*mMoveOffsetTop);
//		setSeatSize(mZoom, mZoom*mMoveOffsetLeft, mZoom*mMoveOffsetTop);
		setSeatSize(mZoom, mMoveOffsetLeft, mMoveOffsetTop);
//		mInterRowLabelView.setPosition(mZoom, mMoveOffsetTop);
//		mInterRowLabelView.invalidate();
//		this.invalidate();
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
	private void resetAnimation(){
		if(mAnimationLocked){
			mAnimationLocked = false;
			
			if(mTranslationXAnimator!=null)
				mTranslationXAnimator.cancel();
			if(mTranslationYAnimator!=null)
				mTranslationYAnimator.cancel();
			
			if(mScaleAnimatorSet!=null)
				mScaleAnimatorSet.cancel();
			
			if(mTranslationAnimatorSet!=null)
				mTranslationAnimatorSet.cancel();
//			mZoom = mSaveZoom;
//			mMoveOffsetLeft = mSaveMoveOffsetLeft;
//			mMoveOffsetTop = mSaveMoveOffsetTop;
//			setSeatSize(mZoom, mMoveOffsetLeft, mMoveOffsetTop);
//			mInterRowLabelView.setPosition(mZoom, mMoveOffsetTop);
//			mInterRowLabelView.invalidate();
		}
	}
	private void startScaleAnimation(boolean vertical){
		if(mAnimationLocked){
//			mAnimationCanceled = true;
			if(mScaleXAnimator!=null)
				mScaleXAnimator.cancel();
			if(mScaleYAnimator!=null)
				mScaleYAnimator.cancel();
		}
//		resetAnimation();
		float minZoom,maxZoom;
		if(vertical){
			maxZoom= mMaxSeatHeight/mCurrentSeatHeight;
			minZoom = mMinSeatHeight/mCurrentSeatHeight;
		}
		else {
			maxZoom = mMaxSeatWidth / mCurrentSeatWidth;
			minZoom = mMinSeatWidth / mCurrentSeatWidth;
		}
		boolean mustzoom = false;
		int n = 0;
		if (mZoom > maxZoom) {
			mustzoom = true;
			mZoomTotalDelta = maxZoom - mZoom;
			n = (int)(-mZoomTotalDelta*10);
			n = Math.max(Math.min(n, ZOOM_ANIMATION_MAX_NUM), ZOOM_ANIMATION_MIN_NUM);
//			mStepX = mZoomTotalDelta/n;
//			mAnimationDuration = ZOOM_ANIMATION_STEP_TIME;
		} else if (mZoom < minZoom) {
			mustzoom = true;
			mZoomTotalDelta = minZoom - mZoom;
			n = (int)(mZoomTotalDelta*10);
			n = Math.max(Math.min(n, ZOOM_ANIMATION_MAX_NUM), ZOOM_ANIMATION_MIN_NUM);
//			mStepX = mZoomTotalDelta/n;
//			mAnimationDuration = ZOOM_ANIMATION_STEP_TIME;
		}
		if(!mustzoom)
			return;
//		float zoom = (mZoom+mZoomTotalDelta)/mZoom;
//		mSaveZoom = mZoom + mZoomTotalDelta;
//		mSaveMoveOffsetLeft = mMoveOffsetLeft;
//		mSaveMoveOffsetTop = mMoveOffsetTop;
		mAnimationLocked = true;
		mScaleXAnimator = ObjectAnimator.ofFloat(mInterSeatView, "scaleX", mInterSeatView.getScaleX(), mZoom+mZoomTotalDelta);
		mScaleYAnimator = ObjectAnimator.ofFloat(mInterSeatView, "scaleY", mInterSeatView.getScaleY(), mZoom+mZoomTotalDelta);
		mScaleAnimatorSet = new AnimatorSet();
		mScaleAnimatorSet.setDuration(n*ZOOM_ANIMATION_STEP_TIME);
		mScaleAnimatorSet.addListener(new Animator.AnimatorListener(){
			public void onAnimationStart(Animator animation) {}
			public void onAnimationEnd(Animator animation) {
				mAnimationLocked = false;
				mScaleXAnimator = null;
				mScaleYAnimator = null;
				mScaleAnimatorSet = null;
//				Log.i(TAG, "2scalex=" + mInterSeatView.getScaleX() + ",scaley="	+ mInterSeatView.getScaleY()+",translatey="+mInterSeatView.getTranslationY()+",offsettop="+mMoveOffsetTop);
				mZoom = mInterSeatView.getScaleX();
				mMoveOffsetLeft = mInterSeatView.getTranslationX();///mZoom;
				mMoveOffsetTop = mInterSeatView.getTranslationY();///mZoom;
//				mZoom += mZoomTotalDelta;
				setSeatSize(mZoom, mMoveOffsetLeft, mMoveOffsetTop);
//				setSeatSize(mZoom, mZoom * mMoveOffsetLeft, mZoom * mMoveOffsetTop);
//				Log.i(TAG, "2scalex=" + mInterSeatView.getScaleX() + ",scaley="	+ mInterSeatView.getScaleY());
			}
			public void onAnimationCancel(Animator animation) {
				mAnimationLocked = false;
				mScaleXAnimator = null;
				mScaleYAnimator = null;
				mScaleAnimatorSet = null;
				mZoom = mInterSeatView.getScaleX();
			}
			public void onAnimationRepeat(Animator animation){}
		});
		if(mTranslationXAnimator==null){
			if(mTranslationYAnimator==null)
				mScaleAnimatorSet.playTogether(mScaleXAnimator,mScaleYAnimator);
			else
				mScaleAnimatorSet.playTogether(mScaleXAnimator,mScaleYAnimator,mTranslationYAnimator);
		}
		else{
			if(mTranslationYAnimator==null)
				mScaleAnimatorSet.playTogether(mScaleXAnimator,mScaleYAnimator,mTranslationXAnimator);
			else
				mScaleAnimatorSet.playTogether(mScaleXAnimator,mScaleYAnimator,mTranslationXAnimator,mTranslationYAnimator);
		}
		mScaleAnimatorSet.start();
//		InterSeatScaleAnimation scalseAnimation=new InterSeatScaleAnimation(1, zoom, 1, zoom);
//		scalseAnimation.setDuration(6000);
//		scalseAnimation.setInterpolator(new DecelerateInterpolator(4));
//		scalseAnimation.setAnimationListener(new Animation.AnimationListener(){
//			public void onAnimationStart(Animation animation){}
//			public void onAnimationEnd(Animation animation){
//				mAnimationLocked = false;
//				mInterSeatView.clearAnimation();
//				mZoom += mZoomTotalDelta;
//				setSeatSize(mZoom,mMoveOffsetLeft,mMoveOffsetTop);
//				mInterRowLabelView.setPosition(mZoom, mMoveOffsetTop);
//				mInterRowLabelView.invalidate();
//			}
//			public void onAnimationRepeat(Animation animation){}
//		});
//		mInterSeatView.startAnimation(scalseAnimation);
	}

	private void startSeatsAnimation(boolean vertical, float xVelocity, float yVelocity){
		float zoom = createScaleAnimation(vertical);
		createTranslateAnimation(zoom==0?mZoom:zoom, xVelocity, yVelocity);
		int n = (mTranslationXAnimator!=null?1:0) + (mTranslationYAnimator!=null?1:0) + (mScaleXAnimator!=null?2:0);
		if(n<1)	return ;
		mAnimationLocked = true;
		mScaleAnimatorSet = new AnimatorSet();
		mScaleAnimatorSet.addListener(new Animator.AnimatorListener(){
			public void onAnimationStart(Animator animation) {}
			public void onAnimationEnd(Animator animation) {
				mAnimationLocked = false;
				mScaleXAnimator = null;
				mScaleYAnimator = null;
				mTranslationXAnimator = null;
				mTranslationYAnimator = null;
				mScaleAnimatorSet = null;
//				Log.i(TAG, "2scalex=" + mInterSeatView.getScaleX() + ",scaley="	+ mInterSeatView.getScaleY()+",translatey="+mInterSeatView.getTranslationY()+",offsettop="+mMoveOffsetTop);
				mZoom = mInterSeatView.getScaleX();
				mMoveOffsetLeft = mInterSeatView.getTranslationX();///mZoom;
				mMoveOffsetTop = mInterSeatView.getTranslationY();///mZoom;
//				mZoom += mZoomTotalDelta;
				setSeatSize(mZoom, mMoveOffsetLeft, mMoveOffsetTop);
//				setSeatSize(mZoom, mZoom * mMoveOffsetLeft, mZoom * mMoveOffsetTop);
//				Log.i(TAG, "2scalex=" + mInterSeatView.getScaleX() + ",scaley="	+ mInterSeatView.getScaleY());
			}
			public void onAnimationCancel(Animator animation) {
				mAnimationLocked = false;
				mScaleXAnimator = null;
				mScaleYAnimator = null;
				mScaleAnimatorSet = null;
				mZoom = mInterSeatView.getScaleX();
			}
			public void onAnimationRepeat(Animator animation){}
		});
		
		ObjectAnimator[] animatorlist= new ObjectAnimator[n];
		int i = 0;
		if (mTranslationXAnimator!=null){
			animatorlist[i++] = mTranslationXAnimator;
		}
		if (mTranslationYAnimator!=null){
			animatorlist[i++] = mTranslationYAnimator;
		}
		if(mScaleXAnimator!=null){
			animatorlist[i++] = mScaleXAnimator;
			animatorlist[i++] = mScaleYAnimator;
		}
		mScaleAnimatorSet.playTogether(animatorlist);
		mScaleAnimatorSet.start();
	}
	private float createScaleAnimation(boolean vertical){
		float minZoom,maxZoom;
		if(vertical){
			maxZoom= mMaxSeatHeight/mCurrentSeatHeight;
			minZoom = mMinSeatHeight/mCurrentSeatHeight;
		}
		else {
			maxZoom = mMaxSeatWidth / mCurrentSeatWidth;
			minZoom = mMinSeatWidth / mCurrentSeatWidth;
		}
		boolean mustzoom = false;
		int n = 0;
		if (mZoom > maxZoom) {
			mustzoom = true;
			mZoomTotalDelta = maxZoom - mZoom;
			n = (int)(-mZoomTotalDelta*10);
			n = Math.max(Math.min(n, ZOOM_ANIMATION_MAX_NUM), ZOOM_ANIMATION_MIN_NUM);
//			mStepX = mZoomTotalDelta/n;
//			mAnimationDuration = ZOOM_ANIMATION_STEP_TIME;
		} else if (mZoom < minZoom) {
			mustzoom = true;
			mZoomTotalDelta = minZoom - mZoom;
			n = (int)(mZoomTotalDelta*10);
			n = Math.max(Math.min(n, ZOOM_ANIMATION_MAX_NUM), ZOOM_ANIMATION_MIN_NUM);
//			mStepX = mZoomTotalDelta/n;
//			mAnimationDuration = ZOOM_ANIMATION_STEP_TIME;
		}
		if(!mustzoom)
			return 0;
		mScaleXAnimator = ObjectAnimator.ofFloat(mInterSeatView, "scaleX", mInterSeatView.getScaleX(), mZoom+mZoomTotalDelta);
		mScaleYAnimator = ObjectAnimator.ofFloat(mInterSeatView, "scaleY", mInterSeatView.getScaleY(), mZoom+mZoomTotalDelta);
		mScaleXAnimator.setDuration(n*ZOOM_ANIMATION_STEP_TIME);
		mScaleYAnimator.setDuration(n*ZOOM_ANIMATION_STEP_TIME);
//		mScaleXAnimator.addListener(new Animator.AnimatorListener(){
//			public void onAnimationStart(Animator animation) {}
//			public void onAnimationEnd(Animator animation) {
//				mScaleXAnimator = null;
//				mZoom = mInterSeatView.getScaleX();
//			}
//			public void onAnimationCancel(Animator animation){}
//			public void onAnimationRepeat(Animator animation){}
//		});
//		mScaleXAnimator.addListener(new Animator.AnimatorListener(){
//			public void onAnimationStart(Animator animation) {}
//			public void onAnimationEnd(Animator animation) {
//				mScaleYAnimator = null;
//			}
//			public void onAnimationCancel(Animator animation){}
//			public void onAnimationRepeat(Animator animation){}
//		});
		return mZoom+mZoomTotalDelta;
	}
	private void createTranslateAnimation(float zoom, float xVelocity, float yVelocity){
		float tensionX=1,factorX=4,tensionY=1,factorY=4;
		float toTranslateX=0,toTranslateY=0;
		float multiple = 1f;
		int translationTimesX=5, translationTimesY=5;

		float maxBackTranslationX = mActureWidth*mMaxBackTranslationX/100;
		float maxBackTranslationY = mActureHeight*mMaxBackTranslationY/100;
		float scaleTranslationX = mInterSeatView.getWidth()*(1-zoom)/(zoom+zoom);
		float scaleTranslationY = mInterSeatView.getHeight()*(1-zoom)/(zoom+zoom);
		float translateLeft = mMoveOffsetLeft/zoom+scaleTranslationX;
		float xDelta = Math.min(Math.abs(xVelocity)*multiple, mViewWidth/2);
		float translatex = xVelocity>0?xDelta:-xDelta;
//		boolean xNeedTranslate = (translateLeft>0 || mViewWidth/mZoom-translateLeft>mActureWidth || translatex!=0);//(translateLeft<-TRANSLATE_ANIMATION_MIN_DELTA || mActureWidth > mViewWidth/mZoom-translateLeft) &&
		boolean xNeedTranslate = maxBackTranslationX>0 && (translateLeft<-maxBackTranslationX || mViewWidth/zoom-translateLeft<mActureWidth-maxBackTranslationX || translatex!=0);
		if(translateLeft<-maxBackTranslationX){
			toTranslateX = -translateLeft-maxBackTranslationX;
			translationTimesX = 3;//Math.min(4, (int)(Math.abs(toTranslateX*tensionX)/(mViewWidth/12)));
		}
		else if(mViewWidth/zoom-translateLeft<mActureWidth-maxBackTranslationX){
			toTranslateX = mViewWidth/zoom-translateLeft-mActureWidth+maxBackTranslationX;
			translationTimesX = 3;//Math.min(4,(int)(Math.abs(toTranslateX*tensionX)/(mViewWidth/12)));
		}
		else if(translatex!=0){
			if(translatex<0){
				toTranslateX = Math.max(-maxBackTranslationX, translateLeft+translatex)-translateLeft;
			}
			else{
				toTranslateX = Math.min(translatex, mViewWidth/zoom-translateLeft-mActureWidth+maxBackTranslationX);
			}
			translationTimesX = 5;//Math.min(6,(int)(Math.abs(toTranslateX*tensionX)/(mViewWidth/12)));
		}
		float translateTop = mMoveOffsetTop/zoom+scaleTranslationY;
		float yDelta = Math.min(Math.abs(yVelocity)*multiple, mViewHeight/2);
		float translatey = yVelocity>0?yDelta:-yDelta;
//		boolean yNeedTranslate = (translateTop>0 || mViewHeight/mZoom-translateTop>mActureHeight || translatey!=0);//(translateTop<-TRANSLATE_ANIMATION_MIN_DELTA || mActureHeight > mViewHeight/mZoom-translateTop) &&
		boolean yNeedTranslate = maxBackTranslationY>0 && (translateTop<-maxBackTranslationY || mViewHeight/zoom-translateTop<mActureHeight-maxBackTranslationY || translatey!=0);
		if(translateTop<-maxBackTranslationY){
			toTranslateY = -translateTop-maxBackTranslationY;
			translationTimesY = 3;//Math.min(4,(int)(Math.abs(toTranslateY*tensionY)/(mViewHeight/12)));
		}
		else if(mViewHeight/zoom-translateTop<mActureHeight-maxBackTranslationY){
			toTranslateY = mViewHeight/zoom-translateTop-mActureHeight+maxBackTranslationY;
			translationTimesY = 3;//Math.min(4,(int)(Math.abs(toTranslateY*tensionY)/(mViewHeight/12)));
		}
		else if(translatey!=0){
			if(translatey<0){
				toTranslateY = Math.max(-maxBackTranslationY, translateTop+translatey)-translateTop;
			}
			else{
				toTranslateY = Math.min(translatey, mViewHeight/zoom-translateTop-mActureHeight+maxBackTranslationY);
			}
			translationTimesY = 5;
		}
		if(xNeedTranslate){
			mTranslationXAnimator = ObjectAnimator.ofFloat(mInterSeatView, "translationX", mInterSeatView.getTranslationX(),(mMoveOffsetLeft+toTranslateX*zoom));
			mTranslationXAnimator.setInterpolator(new SeatOvershootInterpolator(tensionX, factorX));
			mTranslationXAnimator.setDuration(TRANSLATE_ANIMATION_STEP_TIME*translationTimesX);
//			mTranslationXAnimator.addListener(new Animator.AnimatorListener(){
//				public void onAnimationStart(Animator animation){}
//				public void onAnimationEnd(Animator animation){
//					mTranslationXAnimator = null;
//					mMoveOffsetLeft = mInterSeatView.getTranslationX();///mZoom;
////					mMoveOffsetLeft += toTranslateX;
//					Log.i(TAG, "3left="+mInterSeatView.getLeft()+",translatex="+mInterSeatView.getTranslationX()+",top="+mInterSeatView.getTop()+",translatey="+mInterSeatView.getTranslationY());
////					setSeatSize(mZoom, mMoveOffsetLeft, mMoveOffsetTop);
////					setSeatSize(mZoom, mZoom*mMoveOffsetLeft,mZoom*mMoveOffsetTop);
//				}
//				public void onAnimationCancel(Animator animation){
//					mAnimationLocked = false;
//					mTranslationXAnimator = null;
//					mMoveOffsetLeft = mInterSeatView.getTranslationX();///mZoom;
//				}
//				public void onAnimationRepeat(Animator animation){}
//			});
		}
		if(yNeedTranslate){
			mTranslationYAnimator = ObjectAnimator.ofFloat(mInterSeatView, "translationY", mInterSeatView.getTranslationY(),(mMoveOffsetTop+toTranslateY*zoom));
			mTranslationYAnimator.setInterpolator(new SeatOvershootInterpolator(tensionY, factorY));
			mTranslationYAnimator.setDuration(TRANSLATE_ANIMATION_STEP_TIME*translationTimesY);
//			mTranslationYAnimator.addListener(new Animator.AnimatorListener(){
//				public void onAnimationStart(Animator animation){}
//				public void onAnimationEnd(Animator animation){
//					mTranslationYAnimator = null;
//					mMoveOffsetTop = mInterSeatView.getTranslationY();///mZoom;
////					mMoveOffsetTop += toTranslateY;
//					Log.i(TAG, "4left="+mInterSeatView.getLeft()+",translatex="+mInterSeatView.getTranslationX()+",top="+mInterSeatView.getTop()+",translatey="+mInterSeatView.getTranslationY());
////					setSeatSize(mZoom, mMoveOffsetLeft, mMoveOffsetTop);
////					setSeatSize(mZoom, mZoom*mMoveOffsetLeft,mZoom*mMoveOffsetTop);
//				}
//				public void onAnimationCancel(Animator animation){
//					mAnimationLocked = false;
//					mTranslationYAnimator = null;
//					mMoveOffsetTop = mInterSeatView.getTranslationY();///mZoom;
//				}
//				public void onAnimationRepeat(Animator animation){}
//			});
		}
	}
	private void startTranslateAnimation(float xVelocity, float yVelocity){
		if(mAnimationLocked){
//			mAnimationCanceled = true;
			if(mTranslationXAnimator!=null)
				mTranslationXAnimator.cancel();
			if(mTranslationYAnimator!=null)
				mTranslationYAnimator.cancel();
		}
//		resetAnimation();
		float tensionX=1,factorX=4,tensionY=1,factorY=4;
		float toTranslateX=0,toTranslateY=0;
		float multiple = 1f;
		int translationTimesX=5, translationTimesY=5;
		//拖动偏移量 和 速度偏移量叠加计算 动画偏移量
		
		///(1)
//		mTranslateFirstDeltaX = 0;
//		if(mMoveOffsetLeft>0){
//			mTranslateFirstDeltaX = -mMoveOffsetLeft;
//		}
//		else if(mViewWidth/mZoom-mMoveOffsetLeft>mActureWidth){
//			mTranslateFirstDeltaX = mViewWidth/mZoom-mMoveOffsetLeft-mActureWidth;
//		}
//		mTranslateSecondDeltaX = 0;
		float maxBackTranslationX = mActureWidth*mMaxBackTranslationX/100;
		float maxBackTranslationY = mActureHeight*mMaxBackTranslationY/100;
		float translateLeft = mMoveOffsetLeft/mZoom+mScaleTranslationX;
		float xDelta = Math.min(Math.abs(xVelocity)*multiple, mViewWidth/2);
		float translatex = xVelocity>0?xDelta:-xDelta;
//		boolean xNeedTranslate = (translateLeft>0 || mViewWidth/mZoom-translateLeft>mActureWidth || translatex!=0);//(translateLeft<-TRANSLATE_ANIMATION_MIN_DELTA || mActureWidth > mViewWidth/mZoom-translateLeft) &&
		boolean xNeedTranslate = maxBackTranslationX>0 && (translateLeft<-maxBackTranslationX || mViewWidth/mZoom-translateLeft<mActureWidth-maxBackTranslationX || translatex!=0);
		if(translateLeft<-maxBackTranslationX){
			toTranslateX = -translateLeft-maxBackTranslationX;
			translationTimesX = 4;//Math.min(4, (int)(Math.abs(toTranslateX*tensionX)/(mViewWidth/12)));
		}
		else if(mViewWidth/mZoom-translateLeft<mActureWidth-maxBackTranslationX){
			toTranslateX = mViewWidth/mZoom-translateLeft-mActureWidth+maxBackTranslationX;
			translationTimesX = 4;//Math.min(4,(int)(Math.abs(toTranslateX*tensionX)/(mViewWidth/12)));
		}
		else if(translatex!=0){
			if(translatex<0){
				toTranslateX = Math.max(-maxBackTranslationX, translateLeft+translatex)-translateLeft;
			}
			else{
				toTranslateX = Math.min(translatex, mViewWidth/mZoom-translateLeft-mActureWidth+maxBackTranslationX);
			}
			translationTimesX = 8;//Math.min(6,(int)(Math.abs(toTranslateX*tensionX)/(mViewWidth/12)));
		}
		///(2)
//		if(translateLeft>0){
//			toTranslateX = -translateLeft;
//			if(mMoveOffsetLeft+translatex<0)
//				tensionX = 2*(translateLeft+translatex)/-translateLeft+1;
//			else
//				tensionX = 1;
//			translationTimesX = (int)(Math.abs(toTranslateX*tensionX)/(mViewWidth/12));
//		}
//		else if(mViewWidth/mZoom-translateLeft>mActureWidth){
//			toTranslateX = mViewWidth/mZoom-translateLeft-mActureWidth;
//			if(mViewWidth/mZoom-translateLeft-translatex<mActureWidth)
//				tensionX = 2*(translatex-toTranslateX)/translatex+1;
//			else
//				tensionX = 1;
//			translationTimesX = (int)(Math.abs(toTranslateX*tensionX)/(mViewWidth/12));
//		}
//		else{
//			if(translatex!=0){
//				if(translatex<0)
//					toTranslateX = Math.max(mViewWidth/mZoom-translateLeft-mActureWidth, translatex);
//				else
//					toTranslateX = Math.min(-mViewWidth/mZoom+translateLeft+mActureWidth, translatex);
//				toTranslateX *= 0.7f;
//				tensionX = 1;
//				translationTimesX = (int)(Math.abs(toTranslateX*tensionX)/(mViewWidth/20));
//				factorX =2;
//			}
//		}

		///(1)
//		float xTotal =Math.abs(mTranslateFirstDeltaX)+Math.abs(mTranslateSecondDeltaX);
//		int m = (int)(xTotal/(mViewWidth/15));
//		mTranslateFirstDeltaY = 0;
//		if(mMoveOffsetTop>0){
//			mTranslateFirstDeltaY = -mMoveOffsetTop;
//		}
//		else if(mViewHeight/mZoom-mMoveOffsetTop>mActureHeight){
//			mTranslateFirstDeltaY = mViewHeight/mZoom-mMoveOffsetTop-mActureHeight;
//		}
//		mTranslateSecondDeltaY = 0;
		float translateTop = mMoveOffsetTop/mZoom+mScaleTranslationY;
		float yDelta = Math.min(Math.abs(yVelocity)*multiple, mViewHeight/2);
		float translatey = yVelocity>0?yDelta:-yDelta;
//		boolean yNeedTranslate = (translateTop>0 || mViewHeight/mZoom-translateTop>mActureHeight || translatey!=0);//(translateTop<-TRANSLATE_ANIMATION_MIN_DELTA || mActureHeight > mViewHeight/mZoom-translateTop) &&
		boolean yNeedTranslate = maxBackTranslationY>0 && (translateTop<-maxBackTranslationY || mViewHeight/mZoom-translateTop<mActureHeight-maxBackTranslationY || translatey!=0);
		if(translateTop<-maxBackTranslationY){
			toTranslateY = -translateTop-maxBackTranslationY;
			translationTimesY = 4;//Math.min(4,(int)(Math.abs(toTranslateY*tensionY)/(mViewHeight/12)));
		}
		else if(mViewHeight/mZoom-translateTop<mActureHeight-maxBackTranslationY){
			toTranslateY = mViewHeight/mZoom-translateTop-mActureHeight+maxBackTranslationY;
			translationTimesY = 4;//Math.min(4,(int)(Math.abs(toTranslateY*tensionY)/(mViewHeight/12)));
		}
		else if(translatey!=0){
			if(translatey<0){
				toTranslateY = Math.max(-maxBackTranslationY, translateTop+translatey)-translateTop;
			}
			else{
				toTranslateY = Math.min(translatey, mViewHeight/mZoom-translateTop-mActureHeight+maxBackTranslationY);
			}
			translationTimesY = 8;
		}
		///(2)
//		if(translateTop>0){
//			toTranslateY = -translateTop;
//			if(translateTop+translatey<0)
//				tensionY = 2*(translateTop+translatey)/-translateTop+1;
//			else
//				tensionY = 1;
//			translationTimesY = (int)(Math.abs(toTranslateY*tensionY)/(mViewHeight/12));
//		}
//		else if(mViewHeight/mZoom-translateTop>mActureHeight){
//			toTranslateY = mViewHeight/mZoom-translateTop-mActureHeight;
//			if(mViewHeight/mZoom-translateTop-translatey < mActureHeight)
//				tensionY = 2*(translatey-toTranslateY)/translatey+1;
//			else
//				tensionY = 1;
//			translationTimesY = (int)(Math.abs(toTranslateY*tensionY)/(mViewHeight/12));
//		}
//		else{
//			if(translatey!=0){
//				if(translatey<0)
//					toTranslateY = Math.max(mViewHeight/mZoom-translateTop-mActureHeight, translatey);
//				else
//					toTranslateY = Math.min(-mViewHeight/mZoom+translateTop+mActureHeight, translatey);
//				toTranslateY *= 0.7f;
//				tensionY = 1;
//				translationTimesY = (int)(Math.abs(toTranslateY*tensionY)/(mViewHeight/20));
//				factorY =2;
//			}
//		}

		if(!xNeedTranslate && !yNeedTranslate)
			return;
//		float yTotal =Math.abs(mTranslateFirstDeltaY)+Math.abs(mTranslateSecondDeltaY);
//		if(xTotal <= 0 && yTotal <= 0)
//			return;
//		float startx = mAnimationLocked?(mSaveMoveOffsetLeft - mCurrentMoveOffsetLeft):0;
//		float starty = mAnimationLocked?(mSaveMoveOffsetTop - mCurrentMoveOffsetTop):0;
//		mSaveZoom = mZoom;
//		mSaveMoveOffsetLeft = mMoveOffsetLeft+mTranslateFirstDeltaX;
//		mSaveMoveOffsetTop = mMoveOffsetTop+mTranslateFirstDeltaY;
		mAnimationLocked = true;
//		Log.i(TAG, "left="+mInterSeatView.getLeft()+",translatex="+mInterSeatView.getTranslationX()+",top="+mInterSeatView.getTop()+",translatey="+mInterSeatView.getTranslationY());
		if(xNeedTranslate){
			mTranslationXAnimator = ObjectAnimator.ofFloat(mInterSeatView, "translationX", mInterSeatView.getTranslationX(),(mMoveOffsetLeft+toTranslateX*mZoom));
			mTranslationXAnimator.setInterpolator(new SeatOvershootInterpolator(tensionX, factorX));
			mTranslationXAnimator.setDuration(TRANSLATE_ANIMATION_STEP_TIME*translationTimesX);
		}
		if(yNeedTranslate){
			mTranslationYAnimator = ObjectAnimator.ofFloat(mInterSeatView, "translationY", mInterSeatView.getTranslationY(),(mMoveOffsetTop+toTranslateY*mZoom));
			mTranslationYAnimator.setInterpolator(new SeatOvershootInterpolator(tensionY, factorY));
			mTranslationYAnimator.setDuration(TRANSLATE_ANIMATION_STEP_TIME*translationTimesY);
		}
		if(xNeedTranslate && yNeedTranslate){
			mTranslationAnimatorSet = new AnimatorSet();
			mTranslationAnimatorSet.addListener(new Animator.AnimatorListener(){
				public void onAnimationStart(Animator animation){}
				public void onAnimationEnd(Animator animation){
					mAnimationLocked = false;
					mTranslationXAnimator = null;
					mTranslationYAnimator = null;
					mTranslationAnimatorSet = null;
//					Log.i(TAG, "2left="+mInterSeatView.getLeft()+",translatex="+mInterSeatView.getTranslationX()+",top="+mInterSeatView.getTop()+",translatey="+mInterSeatView.getTranslationY());
//					mMoveOffsetLeft += toTranslateX;
//					mMoveOffsetTop += toTranslateY;
					mMoveOffsetLeft = mInterSeatView.getTranslationX();///mZoom;
					mMoveOffsetTop = mInterSeatView.getTranslationY();///mZoom;
					setSeatSize(mZoom, mMoveOffsetLeft, mMoveOffsetTop);
//					setSeatSize(mZoom, mZoom*mMoveOffsetLeft,mZoom*mMoveOffsetTop);
//					Log.i(TAG, "2left="+mInterSeatView.getLeft()+",translatex="+mInterSeatView.getTranslationX()+",top="+mInterSeatView.getTop()+",translatey="+mInterSeatView.getTranslationY());
				}
				public void onAnimationCancel(Animator animation){
					mAnimationLocked = false;
					mTranslationXAnimator = null;
					mTranslationYAnimator = null;
					mTranslationAnimatorSet = null;
					mMoveOffsetLeft = mInterSeatView.getTranslationX();///mZoom;
					mMoveOffsetTop = mInterSeatView.getTranslationY();///mZoom;
				}
				public void onAnimationRepeat(Animator animation){}
			});
//			mTranslationAnimatorSet.setDuration(6000);
			mTranslationAnimatorSet.playTogether(mTranslationXAnimator, mTranslationYAnimator);
			mTranslationAnimatorSet.start();
			mTranslationXAnimator =null;
			mTranslationYAnimator = null;
		}
		else{
			if(xNeedTranslate){
//				mTranslationXAnimator.setDuration(6000);
				mTranslationXAnimator.addListener(new Animator.AnimatorListener(){
					public void onAnimationStart(Animator animation){}
					public void onAnimationEnd(Animator animation){
						mAnimationLocked = false;
						mTranslationXAnimator = null;
						mMoveOffsetLeft = mInterSeatView.getTranslationX();///mZoom;
//						mMoveOffsetLeft += toTranslateX;
//						Log.i(TAG, "3left="+mInterSeatView.getLeft()+",translatex="+mInterSeatView.getTranslationX()+",top="+mInterSeatView.getTop()+",translatey="+mInterSeatView.getTranslationY());
						setSeatSize(mZoom, mMoveOffsetLeft, mMoveOffsetTop);
//						setSeatSize(mZoom, mZoom*mMoveOffsetLeft,mZoom*mMoveOffsetTop);
//						Log.i(TAG, "3left="+mInterSeatView.getLeft()+",translatex="+mInterSeatView.getTranslationX()+",top="+mInterSeatView.getTop()+",translatey="+mInterSeatView.getTranslationY());
					}
					public void onAnimationCancel(Animator animation){
						mAnimationLocked = false;
						mTranslationXAnimator = null;
						mMoveOffsetLeft = mInterSeatView.getTranslationX();///mZoom;
					}
					public void onAnimationRepeat(Animator animation){}
				});
				mTranslationXAnimator.start();
			}
			else{
//				mTranslationYAnimator.setDuration(6000);
				mTranslationYAnimator.addListener(new Animator.AnimatorListener(){
					public void onAnimationStart(Animator animation){}
					public void onAnimationEnd(Animator animation){
						mAnimationLocked = false;
						mTranslationYAnimator = null;
						mMoveOffsetTop = mInterSeatView.getTranslationY();///mZoom;
//						mMoveOffsetTop += toTranslateY;
//						Log.i(TAG, "4left="+mInterSeatView.getLeft()+",translatex="+mInterSeatView.getTranslationX()+",top="+mInterSeatView.getTop()+",translatey="+mInterSeatView.getTranslationY());
						setSeatSize(mZoom, mMoveOffsetLeft, mMoveOffsetTop);
//						setSeatSize(mZoom, mZoom*mMoveOffsetLeft,mZoom*mMoveOffsetTop);
//						Log.i(TAG, "4left="+mInterSeatView.getLeft()+",translatex="+mInterSeatView.getTranslationX()+",top="+mInterSeatView.getTop()+",translatey="+mInterSeatView.getTranslationY());
					}
					public void onAnimationCancel(Animator animation){
						mAnimationLocked = false;
						mTranslationYAnimator = null;
						mMoveOffsetTop = mInterSeatView.getTranslationY();///mZoom;
					}
					public void onAnimationRepeat(Animator animation){}
				});
				mTranslationYAnimator.start();
			}
		}
//		mSeatTranslateAnimation = new InterSeatTranslateAnimation(mZoom*startx,mZoom*toTranslateX,
//				mZoom*starty,mZoom*toTranslateY, tensionX, 1f, tensionY, 2f);
//		mSeatTranslateAnimation.setDuration(6000);
////		translateAnimation.setFillAfter(true);
////		mSeatTranslateAnimation.setInterpolator(new OvershootInterpolator(2));
////		mSeatTranslateAnimation.cancel();
////		DecelerateInterpolator();
////		AccelerateInterpolator()
////		new AnticipateInterpolator();
////		new OvershootInterpolator();
//		mSeatTranslateAnimation.setAnimationListener(new Animation.AnimationListener(){
//			public void onAnimationStart(Animation animation){}
//			public void onAnimationEnd(Animation animation){
//				if(mAnimationCanceled){
//					mAnimationCanceled = false;
//				}
//				mAnimationLocked = false;
//				mInterSeatView.clearAnimation();
//				mMoveOffsetLeft += toTranslateX;
//				mMoveOffsetTop += toTranslateY;
//				setSeatSize(mMoveOffsetLeft,mMoveOffsetTop,0,0);
//				mInterRowLabelView.setPosition(mZoom, mMoveOffsetTop);
//				mInterRowLabelView.invalidate();
//			}
//			public void onAnimationRepeat(Animation animation){}
//		});
//		this.mInterSeatView.startAnimation(mSeatTranslateAnimation);
		
	}
//	private void calculateArithmeticRatio(float totalDelta, int num, float minmaxRatio, boolean vertical){
//		//构造等差数列，计算动画递减量
//		//a+0x a+1x a+2x a+3x a+4x a+(n-1)x = b
//		//(a+(n-1)x)/a = r
//		//na+xn(n-1)/2 = b
//		if(vertical){
//			if(totalDelta>0){
//				mStepY = (2*totalDelta)/((minmaxRatio+1)*num);
//				mArithmeticStepY = (minmaxRatio-1)*mStepY/(num-1);
//			}
//			else{
//				mStepY=0;mArithmeticStepY=0;
//			}
//		}
//		else{
//			if(totalDelta>0){
//				mStepX = (2*totalDelta)/((minmaxRatio+1)*num);
//				mArithmeticStepX = (minmaxRatio-1)*mStepX/(num-1);
//			}
//			else{
//				mStepX=0;mArithmeticStepX=0;
//			}
//		}
//	}
	private void doMove(float dx, float dy, boolean needAnimation){
		resetAnimation();
		float offsetLeft = mMoveOffsetLeft+dx;///mZoom;
		if(!needAnimation){
			if(mActureWidth>mViewWidth/mZoom){
				offsetLeft = Math.max(Math.min(0, offsetLeft), mViewWidth/mZoom-mActureWidth);
			}
			else{
				offsetLeft = Math.min(Math.max(0, offsetLeft), mViewWidth/mZoom-mActureWidth);
			}
		}
		float offsetTop = mMoveOffsetTop+dy;///mZoom;
		if(!needAnimation){
			if(mActureHeight>mViewHeight/mZoom){
				offsetTop = Math.max(Math.min(0, offsetTop), mViewHeight/mZoom-mActureHeight);
			}
			else{
				offsetTop = Math.min(Math.max(0, offsetTop), mViewHeight/mZoom-mActureHeight);
			}
		}
//		if(mMaxTranslationX>0)
//			mMoveOffsetLeft = (offsetLeft+mScaleTranslationX>0 ? Math.min(offsetLeft+mScaleTranslationX, mMaxTranslationX): Math.max(offsetLeft+mScaleTranslationX, -mMaxTranslationX))-mScaleTranslationX;
//		else
		mMoveOffsetLeft = offsetLeft;
//		if(mActureWidth > mViewWidth/mZoom)
//			mMoveOffsetLeft = offsetLeft;
//		if(mMaxTranslationY>0)
//			mMoveOffsetTop = (offsetTop+mScaleTranslationY>0 ? Math.min(offsetTop+mScaleTranslationY, mMaxTranslationY): Math.max(offsetTop+mScaleTranslationY, -mMaxTranslationY))-mScaleTranslationY;
//		else
		mMoveOffsetTop = offsetTop;
//		if(mActureHeight > mViewHeight/mZoom)
//			mMoveOffsetTop = offsetTop;
//		mMoveOffsetLeft = mActureWidth<mViewWidth/mZoom?Math.min(0, offsetLeft):offsetLeft;
//		mMoveOffsetTop = mActureHeight<mViewHeight/mZoom?Math.min(0, offsetTop):offsetTop;
		setSeatSize(mZoom, mMoveOffsetLeft, mMoveOffsetTop);
//		setSeatSize(mZoom, mMoveOffsetLeft*mZoom, mMoveOffsetTop*mZoom);
//		mInterRowLabelView.setPosition(mZoom, mMoveOffsetTop);
//		mInterRowLabelView.invalidate();
//		this.invalidate();
	}
	private void setSeatSize(float zoom, float translateX, float translateY){
		mInterSeatView.setTranslationX(translateX);
		mInterSeatView.setTranslationY(translateY);
//		Log.i(TAG, "scalex="+mInterSeatView.getScaleX());
		if(zoom>0 && zoom!=mInterSeatView.getScaleY()){
			mInterSeatView.setScaleX(zoom);
			mInterSeatView.setScaleY(zoom);
			mScaleTranslationX = mInterSeatView.getWidth()*(1-zoom)/(zoom+zoom);
			mScaleTranslationY = mInterSeatView.getHeight()*(1-zoom)/(zoom+zoom);
		}
//		Log.i(TAG, "scalex="+mInterSeatView.getScaleX());
//		FrameLayout.LayoutParams lp =(FrameLayout.LayoutParams)this.mInterSeatView.getLayoutParams();
//		lp.width=(int)(zoom*Math.max(mActureWidth,mViewWidth));
//		lp.height=(int)(zoom*Math.max(mActureHeight,mViewHeight));
//		lp.leftMargin=(int)translateX;
//		lp.topMargin=(int)translateY;
//		mInterSeatView.setLayoutParams(lp);
//		mInterSeatView.setZoom(zoom);
//		mInterSeatView.invalidate();
	}
	private void setSeatSize(float translateX, float translateY, float width, float height){
		LayoutParams lp =(LayoutParams)this.mInterSeatView.getLayoutParams();
		if(width==0 && height == 0){
			lp.width = Math.max((int)(mActureWidth*mZoom), this.getWidth());
			lp.height = Math.max((int)(mActureHeight*mZoom), this.getHeight());
		}
		else{
			lp.width = (int)width;
			lp.height = (int)height;
		}
		lp.leftMargin=(int)translateX;
		lp.topMargin=(int)translateY;
		mInterSeatView.setLayoutParams(lp);
		mInterSeatView.setZoom(mZoom);
		mInterSeatView.invalidate();
	}
	private SeatStatus changeSeatStatus(int col, int row){
		if(col<0 || row<0 || mColumns==0 || mRows==0)
			return SeatStatus.SS_NONE;
		SeatStatus seatStatus=selectSeat(col,row);
//		float x1 = mOffsetLeft+mMoveOffsetLeft/mZoom+mScaleTranslationX;
//		float y1 = mOffsetTop+mCinemaTitleHeight + mSeatOffsetTop+mMoveOffsetTop/mZoom+mScaleTranslationY;
//		int mid = mColumns/2;
//		if(col<mid){
//			x1+=col*(mCurrentSeatWidth + mHorizontalSpace);
//		}
//		else{
//			x1+=col*mCurrentSeatWidth+Math.max(0, mid-1)*mHorizontalSpace+(col-mid)*mHorizontalSpace+mCenterRegionWidth;
//		}
//		y1+=row*(mCurrentSeatHeight + mVerticalSpace);
		mInterSeatView.invalidate();
//		mInterSeatView.invalidate((int)(x1*mZoom), (int)(y1*mZoom), (int)((x1+mCurrentSeatWidth)*mZoom), (int)((y1+mCurrentSeatHeight)*mZoom));
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
//	public void showThumView(boolean show){
//		mHandler.removeMessages(HANDLE_CLOSE_THUM);
//		if(show){
//			mShowScaleMap = true;
//			invalidate();
//			android.os.Message msg=new android.os.Message();
//			msg.what = HANDLE_CLOSE_THUM;
//			mHandler.sendMessageDelayed(msg, this.mThumShowDelay);
//		}
//		else{
//			if(mShowScaleMap){
//				mShowScaleMap = false;
//				invalidate();
//			}
//		}
//	}
//	private final static int HANDLE_CLOSE_THUM = 100;
//	private final static int HANDLE_TRANSLATE_SEATS = 101;
//	private final static int HANDLE_ZOOM_SEATS = 102;
//	private android.os.Handler mHandler=new android.os.Handler(){
//		@Override
//		public void handleMessage(android.os.Message msg){
//			switch(msg.what){
//			case HANDLE_CLOSE_THUM:
//				mShowScaleMap = false;
//				invalidate();
//				break;
//			case HANDLE_TRANSLATE_SEATS:
//				doTranslateNextPosition(msg);
//				break;
//			case HANDLE_ZOOM_SEATS:
//				doZoomNextScale(msg);
//				break;
//			}
//		}
//	};


	public void setOnSeatClickListener(OnSeatClickListener seatClickListener){
		this.mOnSeatClickListener = seatClickListener;
	}
	public interface OnSeatClickListener{
		public void onSeatClick(int col, int row, SeatStatus seatStatus);
	}
	
	private class InterSeatView extends View{
		private float mZoom;
//		private float mStartTouchX,mStartTouchY;
//		private float mEndTouchX,mEndTouchY;
		public InterSeatView(Context context){
			super(context);
			mZoom =1;
		}
		public InterSeatView(Context context, AttributeSet attrs){
			super(context,attrs);
			mZoom =1;
		}
		public InterSeatView(Context context, AttributeSet attrs, int defStyle){
			super(context,attrs,defStyle);
			mZoom =1;
		}
		public void setZoom(float zoom){
			mZoom = zoom;
			this.invalidate();
		}
		@Override
		public void setTranslationY(float translationY){
			super.setTranslationY(translationY);
			float scaleY = this.getScaleY();
			mScaleTranslationX = mInterSeatView.getWidth()*(1-scaleY)/(scaleY+scaleY);
			mScaleTranslationY = mInterSeatView.getHeight()*(1-scaleY)/(scaleY+scaleY);
			mInterRowLabelView.setPosition(scaleY, translationY/scaleY+mScaleTranslationY);
		}
		@Override
		public void setScaleY(float scaleY){
//			Log.i(TAG, "1anilock="+(mAnimationLocked?"true":"false")+",translatey="+getTranslationY()+",scaley="+scaleY+",scrolly="+getScrollY());
			super.setScaleY(scaleY);
			mScaleTranslationX = mInterSeatView.getWidth()*(1-scaleY)/(scaleY+scaleY);
			mScaleTranslationY = mInterSeatView.getHeight()*(1-scaleY)/(scaleY+scaleY);
			float translatey = this.getTranslationY();
			mInterRowLabelView.setPosition(scaleY, translatey/scaleY+mScaleTranslationY);
//			if(!mAnimationLocked)
//			Log.i(TAG, "2anilock="+(mAnimationLocked?"true":"false")+",translatey="+getTranslationY()+",scaley="+scaleY+",scrolly="+getScrollY());
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.save();
			canvas.translate(0, 0);
			canvas.scale(mZoom, mZoom);
			drawCinemaInfo(canvas,false);
			drawCenterLine(canvas,false);
			
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
		}
	}
	private class InterThumSeatView extends View{
		public InterThumSeatView(Context context){
			super(context);
		}
		public InterThumSeatView(Context context, AttributeSet attrs){
			super(context,attrs);
		}
		public InterThumSeatView(Context context, AttributeSet attrs, int defStyle){
			super(context,attrs,defStyle);
		}
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if(mShowScaleMap){
				drawThumSeats(canvas);
//				drawThumRect(canvas);
			}
		}
	}
	private class InterThumRectView extends View{
		public InterThumRectView(Context context){
			super(context);
		}
		public InterThumRectView(Context context, AttributeSet attrs){
			super(context,attrs);
		}
		public InterThumRectView(Context context, AttributeSet attrs, int defStyle){
			super(context,attrs,defStyle);
		}
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if(mShowScaleMap){
				drawThumRect(canvas);
			}
		}
	}
	private class InterRowLabelView extends View{
		private float mZoom;
		private float mMoveOffsetTop;
		public InterRowLabelView(Context context){
			super(context);
		}
		public InterRowLabelView(Context context, AttributeSet attrs){
			super(context,attrs);
		}
		public InterRowLabelView(Context context, AttributeSet attrs, int defStyle){
			super(context,attrs,defStyle);
		}
		public void setPosition(float zoom, float offsetTop){
			mZoom = zoom;
			mMoveOffsetTop = offsetTop;
			this.invalidate();
		}
		public void setZoom(float zoom){
			mZoom = zoom;
			this.invalidate();
		}
		public void setOffsetTop(float offsetTop){
			mMoveOffsetTop = offsetTop*mZoom;
			this.invalidate();
		}
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			////绘制行号
			drawRowLabel(canvas, mZoom, mMoveOffsetTop);
		}
	}

	private class SeatOvershootInterpolator implements TimeInterpolator{
		private float mTension;
		private float mFactor;
		public SeatOvershootInterpolator(float tension, float factor){
			this.mTension = tension;
			this.mFactor = factor;
		}
		public float getInterpolation(float input){
			float polation;
			if(mFactor == 1.0f)
				polation = (1.0f - (1.0f - input) * (1.0f - input))*mTension;
			else
				polation = (1.0f - (float)Math.pow((1.0f - input), 2 * mFactor))*mTension;
			if(polation < 1+(mTension-1)/2)
				return polation;
			return 1+mTension-polation;
		}
	}
}
