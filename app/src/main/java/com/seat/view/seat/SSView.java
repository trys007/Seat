package com.seat.view.seat;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import com.seat.R;

public class SSView extends View {
	Context mContext;
	int x_offset = 0;
	public  int max_select=5;//最多选中
	private ArrayList<SelectSeat>selectSeatList = new ArrayList<SelectSeat>();
	/** 普通状态 */
	private Bitmap mBitMapSeatNormal = null;
	/** 已锁定 */
	private Bitmap mBitMapSeatLock = null;
	/** 已选中 */
	private Bitmap mBitMapSeatChecked = null;
	
	/************
	 * 已售出
	 */
	private Bitmap mBitMapSeatSold = null ;

	
	private Bitmap topBg = null;
	
	/** 缩略图画布 */
	private Canvas mCanvas = null;
	
	/***序列排画布***/
	private Canvas rCanvas = null;

	/** 是否显示缩略图 */
	private boolean isShowScaleMap = false;

	/** 每个座位的高度 - 57 */
	private int ss_seat_current_height = 57;
	/** 每个座位的宽度 */
	private int ss_seat_current_width = 57;
	/** 座位之间的间距 */
	private int space_L = 3;
	private int space_L_buf=3;
	private double T = 1.0D;

	private double scale = -1.0D;
	private double scale_u = 1.0D;
	/** 是否可缩放 */
	private boolean isZoom = false;

	/** 座位最小高度 */
	private int ss_seat_min_height = 0;
	/** 座位最大高度 */
	private int ss_seat_max_height = 0;
	/** 座位最小宽度 */
	private int ss_seat_min_width = 0;
	/** 座位最大宽度 */
	private int ss_seat_max_width = 0;

	
	private int init_seat_buf=0;
	
	private OnSeatClickListener mOnSeatClickListener = null;

	public static double a = 1.0E-006D;
	private int I = 0;
	private int ss_between_offset = 2;
	private int ss_seat_check_size = 50;
	private SSThumView mSSThumView = null;//缩略图
	private RowView rowView = null;//排序号
	private int ss_seat_thum_size_w = 120;
	private int ss_seat_thum_size_h = 90;
	private int ss_seat_rect_line = 2;
	/** 选座缩略图 */
	private Bitmap mBitMapThumView = null;
	private Bitmap rowBitmap = null;//排号
	private volatile int V = 1500;
	/** 左边距 */
	private int offset_left = 0;
	/** 右边距 */
	private int offset_right = 0;
	/** 上边距 */
	private int offset_top = 30;
	
	private int top_buf=30;
	
	/** 下边距 */
	private int offset_bottom = 70;
	/** 排数x轴偏移量 */
	private float offset_x = 0.0F;
	/** 排数y轴偏移量 */
	private float offset_y = 0.0F;
	/** 座位距离排数的横向距离 */
	private int space_row = 0;
	/** 可视座位距离顶端的距离 */
	private int space_top = 0;
	/** 整个view的宽度 */
	private int seat_view_w = 0;
	/** 整个view的高度 */
	private int seat_view_h = 0;
	/** 能否移动 */
	private boolean isCanMove = true;

	private boolean first_load_bg = true;
	private int tempX;
	private int tempY;
	
	GestureDetector mGestureDetector = new GestureDetector(mContext,
			new GestureListener(this));

	private ArrayList<SeatInfo> mListSeatInfos = null;
	private ArrayList<ArrayList<Integer>> mListSeatConditions = null;
	private int iMaxPay = 0;
	private int totalCountEachRow;
	private int rows;
	private int columns;
	int view_w=0;
	int view_h=0;
	
	private int top_text_buf=50;

	public SSView(Context paramContext, AttributeSet paramAttributeSet) {
		this(paramContext, paramAttributeSet, 0);
	}

	public SSView(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		this.mContext = paramContext;
		
		
		ViewTreeObserver observer = getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				//initParams();
				view_h = getHeight();
				view_w = getWidth();
				
				
			}
		});
	}
	
	
	public void init()
	{
		space_L = 3;
		space_L_buf=3;
		T = 1.0D;
		x_offset=0;
		scale = -1.0D;
		scale_u = 1.0D;
		top_text_buf=50;
		
		
		V = 1500;
		/** 左边距 */
		offset_left = 0;
		/** 右边距 */
		offset_right = 0;
		/** 上边距 */
		offset_top = 30;
		
		top_buf=30;
		
		/** 下边距 */
		offset_bottom = 70;
		/** 排数x轴偏移量 */
		offset_x = 0.0F;
		/** 排数y轴偏移量 */
		offset_y = 0.0F;
		/** 座位距离排数的横向距离 */
		space_row = 0;
		/** 可视座位距离顶端的距离 */
		space_top = 0;
		/** 整个view的宽度 */
		int seat_view_w = 0;
		/** 整个view的高度 */
		seat_view_h = 0;
		
		
		ss_seat_thum_size_w = 120;
		ss_seat_thum_size_h = 90;
		ss_seat_rect_line = 2;
		
		
		double a = 1.0E-006D;
		I = 0;
		ss_between_offset = 2;
		ss_seat_check_size = 50;
		
		
		/** 座位最小高度 */
		ss_seat_min_height = 0;
		/** 座位最大高度 */
		ss_seat_max_height = 0;
		/** 座位最小宽度 */
		ss_seat_min_width = 0;
		/** 座位最大宽度 */
		ss_seat_max_width = 0;
		
		
	}
	
	private void initParams()
	{
		this.ss_seat_thum_size_w = this.mContext.getResources()
				.getDimensionPixelSize(R.dimen.ss_seat_thum_size_w);
		this.ss_seat_thum_size_h = this.mContext.getResources()
				.getDimensionPixelSize(R.dimen.ss_seat_thum_size_h);
		
		this.ss_seat_max_height = this.mContext.getResources()
				.getDimensionPixelSize(R.dimen.seat_max_height);
		this.ss_seat_max_width = this.mContext.getResources()
				.getDimensionPixelSize(R.dimen.seat_max_width);
		this.ss_seat_min_height = this.mContext.getResources()
				.getDimensionPixelSize(R.dimen.seat_min_height);
		this.ss_seat_min_width = this.mContext.getResources()
				.getDimensionPixelSize(R.dimen.seat_min_width);
		this.ss_seat_current_height = this.mContext.getResources()
				.getDimensionPixelSize(R.dimen.seat_init_height);
		this.ss_seat_current_width = this.mContext.getResources()
				.getDimensionPixelSize(R.dimen.seat_init_width);
		
		int screenW = SystemConfigUtil.screen_w;
		int buf = screenW/columns;
		
		if(buf>ss_seat_max_width)
			buf = ss_seat_max_width;
		if(buf<ss_seat_min_width)
			buf = ss_seat_min_width;
		
		
		
		int seatCount = screenW/buf;
		//if(seatCount>columns)
//		offset_x = (seatCount-columns)*buf/2;
//		offset_left = (int)offset_x;
		
		this.ss_seat_current_height = buf;
		
		this.ss_seat_current_width = buf;
		init_seat_buf = ss_seat_current_width;
		//this.space_L = ss_seat_current_height/4;
		
		
		this.ss_seat_check_size = this.mContext.getResources()
				.getDimensionPixelSize(R.dimen.ss_seat_check_size);//30dp
		this.ss_between_offset = this.mContext.getResources()
				.getDimensionPixelSize(R.dimen.ss_between_offset);//5dp
		
		
		space_L = (int)(space_L*SystemConfigUtil.getScale());
		space_L_buf = (int)(space_L_buf*SystemConfigUtil.getScale());
		offset_top = (int)(offset_top*SystemConfigUtil.getScale());
		top_buf = (int)(top_buf*SystemConfigUtil.getScale());
		offset_bottom = (int)(offset_bottom*SystemConfigUtil.getScale());
		top_text_buf = (int)(top_text_buf*SystemConfigUtil.getScale());
		
		
		invalidate();
	}
	

	public void init(int row_count, int rows,
			ArrayList<SeatInfo> list_seatInfos,
			ArrayList<ArrayList<Integer>> list_seat_condtions,
			SSThumView paramSSThumView,RowView rowView,int imaxPay) {
		this.iMaxPay = imaxPay;
		this.mSSThumView = paramSSThumView;
		mSSThumView.setVisibility(View.INVISIBLE);
		this.rowView = rowView;
		this.totalCountEachRow = row_count;
		this.rows = rows;
		columns = row_count;
		this.mListSeatInfos = list_seatInfos;
		this.mListSeatConditions = list_seat_condtions;
		this.mBitMapSeatNormal = getBitmapFromDrawable((BitmapDrawable) this.mContext
				.getResources().getDrawable(R.drawable.seat_normal));
		this.mBitMapSeatLock = getBitmapFromDrawable((BitmapDrawable) this.mContext
				.getResources().getDrawable(R.drawable.seat_lock));
		this.mBitMapSeatChecked = getBitmapFromDrawable((BitmapDrawable) this.mContext
				.getResources().getDrawable(R.drawable.seat_checked));

		this.mBitMapSeatSold = getBitmapFromDrawable((BitmapDrawable) this.mContext
				.getResources().getDrawable(R.drawable.seat_lock));
		topBg = getBitmapFromDrawable((BitmapDrawable) this.mContext
				.getResources().getDrawable(R.drawable.cinema_top));
		
		initParams();
	}

	public static Bitmap getBitmapFromDrawable(
			BitmapDrawable paramBitmapDrawable) {
		return paramBitmapDrawable.getBitmap();
	}

	/**
	 * 
	 * @param seatNum
	 *            每排的座位顺序号
	 * @param rowNum
	 *            排号
	 * @param paramBitmap
	 * @param paramCanvas1
	 * @param paramCanvas2
	 * @param paramPaint
	 */
	private void drawSeatInfo(int seatNum, int rowNum, Bitmap paramBitmap,
			Canvas paramCanvas1, Canvas paramCanvas2, Paint paramPaint) {
		
		
		if (paramBitmap == null) {// 走道
			paramCanvas1.drawRect(getSeatRect(seatNum, rowNum), paramPaint);
			if (this.isShowScaleMap) {
				paramCanvas2.drawRect(getScaleSeatRect(seatNum, rowNum), paramPaint);
			}
		} else {
			paramCanvas1.drawBitmap(paramBitmap, null, getSeatRect(seatNum, rowNum),
					paramPaint);
			if (this.isShowScaleMap) {
				paramCanvas2.drawBitmap(paramBitmap, null, getScaleSeatRect(seatNum, rowNum),
						paramPaint);
			}
		}
		
		
	}

	/**
	 * 
	 * @param seatNum
	 *            每排的座位号
	 * @param rowNum
	 *            排号
	 * @return
	 */
	private Rect getSeatRect(int seatNum, int rowNum) {
		
		
		
		
		try {
			Rect localRect = new Rect(this.offset_left + seatNum * this.ss_seat_current_width + this.space_L,
					this.offset_top + rowNum * this.ss_seat_current_height + this.space_L, this.offset_left + (seatNum + 1)
							* this.ss_seat_current_width - this.space_L, this.offset_top + (rowNum + 1) * this.ss_seat_current_height
							- this.space_L);
			return localRect;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return new Rect();
	}

	private Rect getScaleSeatRect(int seatNum, int rowNum) {
		try {
//			Rect localRect = new Rect(
//					5 + (int) (this.T * (this.offset_left + seatNum * this.ss_seat_current_width + this.space_L)),
//					5 + (int) (this.T * (this.offset_top + rowNum * this.ss_seat_current_height + this.space_L)),
//					5 + (int) (this.T * (this.offset_left + (seatNum + 1) * this.ss_seat_current_width - this.space_L)),
//					5 + (int) (this.T * (this.offset_top + (rowNum + 1) * this.ss_seat_current_height - this.space_L)));
			
			Rect localRect = new Rect(
					5 + (int) (this.T * (seatNum * this.ss_seat_current_width + this.space_L)),
					15 + (int) (this.T * (15+rowNum * this.ss_seat_current_height + this.space_L)),
					5 + (int) (this.T * ((seatNum + 1) * this.ss_seat_current_width - this.space_L)),
					15 + (int) (this.T * (15+(rowNum + 1) * this.ss_seat_current_height - this.space_L)));
			
			return localRect;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return new Rect();
	}

	
	//获取缩略图的范围框
	private Rect getSSThumViewEnv(int paramInt1, int paramInt2)
	{
		int i1;
		int i3;
		try {
			if (getMeasuredWidth() < this.seat_view_w) {
				i1 = getMeasuredWidth();
			} else {
				i1 = this.seat_view_w;
			}
			if (getMeasuredHeight() < this.seat_view_h) {
				i3 = getMeasuredHeight();
			} else {
				i3 = this.seat_view_h;
			}
			
			int left = (int) (5.0D + this.T * paramInt1);
			int top = (int) (5.0D + this.T * paramInt2);
			int right = (int) (5.0D + this.T * paramInt1 + i1 * this.T);
			int bottom = (int) (5.0D + this.T * paramInt2 + i3 * this.T);
			
			
			if(right>tempX)
				right = tempX;
			if(bottom>tempY)
				bottom = tempY;
			
			return new Rect(left,top,right,bottom);
			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new Rect();
		}
	}
	
	
	/*************
	 * 绘制中心虚线
	 * @param canvas
	 */
	private void drawCenterLine(Canvas canvas)
	{
		
		
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);  
		paint.setColor(0xff737373);
		float val = SystemConfigUtil.getScale()*5;
		PathEffect effects = new DashPathEffect(new float[]{val,val,val,val},SystemConfigUtil.getScale());
		paint.setPathEffect(effects);
		Path path = new Path(); 
		
		int startX =  this.offset_left + columns/2 * this.ss_seat_current_width ;
		int startY = top_buf/2;
		int endX = startX;
		int endY = this.offset_top + (rows) * this.ss_seat_current_height;
        path.moveTo(startX, startY);      
        path.lineTo(endX,endY); 
		canvas.drawPath(path, paint);
	}

	
	/********************
	 * 绘制影厅信息
	 */
	private String cinemaInfo="银幕中央";
	private float initTextSize=16f;//初始化标题大小
	private void drawCinemaInfo(Canvas canvas)
	{
		
		float scale = SystemConfigUtil.getScale();
		
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		initTextSize = (float)(initTextSize*scale_u);
		initTextSize = scale*initTextSize;
		if(initTextSize<12)
			initTextSize=12;
		paint.setTextSize(initTextSize);
		
		paint.setColor(0xff737373);
		int x =  this.offset_left + columns/2 * this.ss_seat_current_width ;
		FontMetrics fm = paint.getFontMetrics();  
		float width = paint.measureText(cinemaInfo);
		float height = (float)Math.ceil(fm.descent - fm.ascent); 
		x = (int)(x - width/2);
		Rect rect = new Rect((int)(x-width/10),offset_top*2/3-(int)(height*5/6),(int)(x+width+width/10),(int)(offset_top*2/3+height/8));  
		//paint.getTextBounds(cinemaInfo, 0, cinemaInfo.length(), rect);  
		if(topBg!=null)
			canvas.drawBitmap(topBg, null, rect, paint);
		canvas.drawText(cinemaInfo, x, offset_top*2/3, paint);
		initTextSize = 16f;
		
	}
	
	/**************
	 * 获取标题头的边距
	 * @return
	 */
	public Rect getTopRect()
	{
		Rect rect = new Rect();
		rect.left=this.offset_left*2;
		rect.top=offset_top-top_buf;  //
		rect.right=this.offset_left*2 + this.ss_seat_current_width * this.totalCountEachRow + this.offset_right*2;
		rect.bottom=(int)(scale_u*top_text_buf);
		
		return rect;
	}
	
	
	@Override
	protected void onDraw(Canvas paramCanvas) {
		super.onDraw(paramCanvas);
		
		// Log.i("TAG", "onDraw()...");
		if (this.totalCountEachRow == 0 || this.rows == 0) {
			return;
		}

		if (this.offset_x + this.seat_view_w < 0.0f || this.offset_y + this.seat_view_h < 0.0f) {
			this.offset_x = 0.0f;
			this.offset_y = 0.0f;
			this.space_row = 0;
			this.space_top = 0;
		}
		Paint localPaint2 = new Paint();
		if (this.ss_seat_current_width != 0 && this.ss_seat_current_height != 0) {

			this.mBitMapThumView = Bitmap.createBitmap(this.ss_seat_thum_size_w,
					this.ss_seat_thum_size_h, Bitmap.Config.ARGB_8888);
			this.mCanvas = new Canvas();
			this.mCanvas.setBitmap(this.mBitMapThumView);
			this.mCanvas.save();
			
			 Paint localPaint1 = new Paint();
			 localPaint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			 this.mCanvas.drawPaint(localPaint1);
			 
			 double d1 = (this.ss_seat_thum_size_w - 10.0D)
						/ (this.ss_seat_current_width * this.totalCountEachRow + this.offset_left + this.offset_right); // -
																				// v0/v2
			double d2 = (this.ss_seat_thum_size_h - 10.0D)
						/ (this.ss_seat_current_height * this.rows);
				if (d1 <= d2) {
					this.T = d1;
				} else {
					this.T = d2;
				}
			if(this.isShowScaleMap){
				//-16777216
				localPaint2.setColor(-16777216);
				if(first_load_bg){
					first_load_bg = false;
					tempX = 5+(int) (this.seat_view_w * this.T);
					tempY = 5 + (int) (this.seat_view_h * this.T);
				}
				//Log.e("System.out", "tempX:"+tempX+" tempY:"+tempY);
				this.mCanvas.drawRect(5.0F, 5.0F,  tempX,
						tempY, localPaint2);
			}
		}

		
		//paramCanvas.translate(view_w/2,view_h/2);
		
		//paramCanvas.translate(this.offset_x, this.offset_y);
		paramCanvas.translate(this.offset_x, offset_y);
		
		this.seat_view_w = this.offset_left + this.ss_seat_current_width * this.totalCountEachRow + this.offset_right;
		this.seat_view_h = this.ss_seat_current_height * this.rows+this.offset_bottom;

		//this.offset_left = (int) Math.round(this.ss_seat_current_width / 2.0D);
		
		
		drawCenterLine(paramCanvas);
		drawCinemaInfo(paramCanvas);
		localPaint2.setTextAlign(Paint.Align.CENTER);
		localPaint2.setAntiAlias(true);
		localPaint2.setColor(-16777216);
		for (int i2 = 0; i2 < this.mListSeatConditions.size(); i2++) {
			ArrayList<Integer> localArrayList = (ArrayList<Integer>) this.mListSeatConditions
					.get(i2);

			for (int i3 = 0; i3 < this.mListSeatInfos.get(i2).getSeatList()
					.size(); i3++) {// 2344
				// goto5 - 2344
				Seat localSeat = this.mListSeatInfos.get(i2).getSeat(i3);
				switch (((Integer) localArrayList.get(i3)).intValue()) { // 2373
				case 0: // 2401 - 走道
					localPaint2.setColor(0);
					drawSeatInfo(i3, i2, null, paramCanvas, this.mCanvas, localPaint2);
					localPaint2.setColor(-16777216);
					break;
				case 1:// 可选
					drawSeatInfo(i3, i2, this.mBitMapSeatNormal, paramCanvas,
							this.mCanvas, localPaint2);
					break;
				case 2://
					drawSeatInfo(i3, i2, this.mBitMapSeatLock, paramCanvas,
							this.mCanvas, localPaint2);
					break;
				case 3: // 2500-一已点击的状态
					drawSeatInfo(i3, i2, this.mBitMapSeatChecked, paramCanvas,
							this.mCanvas, localPaint2);
					break;
				case 4://已售
					drawSeatInfo(i3, i2, this.mBitMapSeatSold, paramCanvas,
							this.mCanvas, localPaint2);
				default:
					break;
				}
			}
			// cond_d - 2538
		}

		// 画排数
		localPaint2.setTextSize(0.4F * this.ss_seat_current_height);
		
		rowBitmap = Bitmap.createBitmap(view_w,
				view_h, Bitmap.Config.ARGB_8888);
		rCanvas = new Canvas();
		rCanvas.setBitmap(rowBitmap);
		rCanvas.save();
		rCanvas.translate(0, this.offset_y);
		//rCanvas.drawPaint(localPaint2);
		for (int i1 = 0; i1 < this.mListSeatInfos.size(); i1++) {
//			localPaint2.setColor(-1308622848);
//			paramCanvas.drawRect(new Rect((int) Math.abs(this.offset_x), this.offset_top
//					+ i1 * this.ss_seat_current_height, (int) Math.abs(this.offset_x) + this.ss_seat_current_width / 2,
//					this.offset_top + (i1 + 1) * this.ss_seat_current_height), localPaint2);
			
			
//			paramCanvas.drawRect(new Rect((int) Math.abs(this.offset_x),this.offset_top, (int) Math.abs(this.offset_x)+50, this.offset_top+view_h),localPaint2);
			localPaint2.setColor(-1);
			Rect rectBuf = getSeatRect(0,i1);
			
			
			String row="";
			
			ArrayList<Seat> list = ((SeatInfo) this.mListSeatInfos.get(i1)).getSeatList();
			for(int k=0;k<list.size();k++)
			{
				Seat seat = list.get(k);
				if(seat.getShowRow()!=null&&!"".equals(seat.getShowRow().trim()))
				{
					row = seat.getShowRow();
					break;
				}
			}
			
			rCanvas.drawText(row,SystemConfigUtil.dip2px(mContext, 5), (rectBuf.top+rectBuf.bottom)/2+2, localPaint2);
			
//			rCanvas.drawText(((SeatInfo) this.mListSeatInfos.get(i1))
//					.getDesc(),SystemConfigUtil.dip2px(mContext, 5), (rectBuf.top+rectBuf.bottom)/2+2, localPaint2);
//			paramCanvas.drawText(((SeatInfo) this.mListSeatInfos.get(i1))
//							.getDesc(), (int) Math.abs(this.offset_x) + this.ss_seat_current_width / 2
//							/ 2, this.offset_top + i1 * this.ss_seat_current_height + this.ss_seat_current_height / 2 + this.offset_bottom
//							/ 2, localPaint2);
			
			
			
			
			
		}
		
		rowView.update(rowBitmap);
		//rowView.invalidate();

		if (this.isShowScaleMap) {
			// 画缩略图的黄色框
			localPaint2.setColor(-739328);
			localPaint2.setStyle(Paint.Style.STROKE);
			localPaint2.setStrokeWidth(this.ss_seat_rect_line);
			this.mCanvas.drawRect(
					getSSThumViewEnv((int) Math.abs(this.offset_x), (int) Math.abs(this.offset_y)),
					localPaint2);
			localPaint2.setStyle(Paint.Style.FILL);
			// paramCanvas.restore();
			this.mCanvas.restore();
		}

		if (this.mSSThumView != null) {
			this.mSSThumView.update(mBitMapThumView);
			
			this.mSSThumView.invalidate();
			
		}
		
		

	}

//	public void setXOffset(int x_offset) {
//		this.x_offset = x_offset;
//		//this.x_offset = (float)scale_u*x_offset;
//	}

	/**
	 * 获取两点的直线距离
	 * 
	 * @param paramMotionEvent
	 * @return
	 */
	private float getTwoPoiniterDistance(MotionEvent paramMotionEvent) {
		float f1 = paramMotionEvent.getX(0) - paramMotionEvent.getX(1);
		float f2 = paramMotionEvent.getY(0) - paramMotionEvent.getY(1);
		return (float) Math.sqrt(f1 * f1 + f2 * f2);
	}

	private void scale(MotionEvent paramMotionEvent) {
		double d1 = getTwoPoiniterDistance(paramMotionEvent);
		if (this.scale < 0.0D) {
			this.scale = d1;
		} else {
			try {
				
				double tmpScale =  (d1 / this.scale);
				int tempBuf = (int)Math.round(tmpScale * this.ss_seat_current_width);
				if(tempBuf>ss_seat_max_width||tempBuf<ss_seat_min_width)
					return;
				
				this.scale_u = tmpScale;
				this.scale = d1;
				if ((this.isZoom) && (Math.round(this.scale_u * this.ss_seat_current_width) > 0L)
						&& (Math.round(this.scale_u * this.ss_seat_current_height) > 0L)) {
					this.ss_seat_current_width = (int) Math.round(this.scale_u * this.ss_seat_current_width);
					this.ss_seat_current_height = (int) Math.round(this.scale_u * this.ss_seat_current_height);
					
					//this.offset_left = (int) Math.round(this.offset_left/this.scale_u);
					//this.offset_right = this.offset_left;
//					this.offset_x = (float)(this.offset_x*scale_u);
//					this.offset_y = (float)(this.offset_y*scale_u);
					this.offset_left = (int) Math.round(this.ss_seat_current_width / 2.0D);
					this.offset_right = this.offset_left;
					//this.space_L = (int) Math.round(this.scale_u * this.space_L);
					//this.space_L = ss_seat_current_height/4;
					this.space_L=getSeatSpace();
					Log.e("System.out", "space_L:"+space_L+" ss_seat_current_width:"+ss_seat_current_width);
					if (this.space_L <= 0)
						this.space_L = 1;
				}
				invalidate();
			} catch (Exception localException) {
				localException.printStackTrace();
			}
		}

	}

	/**
	 * new added
	 * 
	 * @return
	 */
	public static int getLeftOffSet(SSView mSsView) {
		return mSsView.offset_left;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @param paramInt
	 * @return
	 */
	public static int m(SSView mSsView, int paramInt) {
		mSsView.V = mSsView.V - paramInt;
		return mSsView.V;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int x(SSView mSsView) {
		return mSsView.V;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 */
	public static void y(SSView mSsView) {
		mSsView.a();
	}

	private void a() {
		// postDelayed(new ag(this), 500L);
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static float getYoffSet(SSView mSsView) {
		return mSsView.offset_y;
	}

	/**
	 * 获取排数x轴偏移量
	 * 
	 * @param mSsView
	 * @return
	 */
	public static float getXoffSet(SSView mSsView) {
		return mSsView.offset_x;
	}

	/**
	 * 获取整个view的高度
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int getSeatViewHeight(SSView mSsView) {
		return mSsView.seat_view_h;
	}

	/**
	 * 获取可视座位距离顶端的距离
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int getSpaceTop(SSView mSsView) {
		return mSsView.space_top;
	}

	/**
	 * 获取整个view的宽度
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int getSeatViewWidth(SSView mSsView) {
		return mSsView.seat_view_w;
	}

	/**
	 * 获取座位距离排数的横向距离
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int getSpaceRow(SSView mSsView) {
		return mSsView.space_row;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int getTopOffSet(SSView mSsView) {
		return mSsView.offset_top;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int p(SSView mSsView) {
		return mSsView.rows;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int getRightOffSet(SSView mSsView) {
		return mSsView.offset_right;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int n(SSView mSsView) {
		return mSsView.totalCountEachRow;
	}

	/**
	 * 修改可见座位距离顶端的距离
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int modifySpaceTop(SSView mSsView, int paramInt) {
		mSsView.space_top = mSsView.space_top + paramInt;
		return mSsView.space_top;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int l(SSView mSsView) {
		return mSsView.ss_seat_current_width;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int k(SSView mSsView) {
		return mSsView.ss_seat_current_width;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @param paramInt
	 * @return
	 */
	public static int modifySpaceRow(SSView mSsView, int paramInt) {
		mSsView.space_row = mSsView.space_row + paramInt;
		return mSsView.space_row;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int getSeatCurrentHeight(SSView mSsView) {
		return mSsView.ss_seat_current_height;
	}

	/**
	 * 设置可视座位距离顶端的距离
	 * 
	 * @param mSsView
	 * @param paramInt
	 * @return
	 */
	public static int setSpaceTop(SSView mSsView, int paramInt) {
		mSsView.space_top = paramInt;
		return mSsView.space_top;
	}

	/**
	 * 设置座位距离排数的横向距离
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int setSpaceRow(SSView mSsView, int paramInt) {
		mSsView.space_row = paramInt;
		return mSsView.space_row;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static boolean isShowScaleMap(SSView mSsView) {
		return mSsView.isShowScaleMap;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int getSeatViewHeight(SSView mSsView, int paramInt) {
		return mSsView.seat_view_h;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int h(SSView mSsView) {
		return mSsView.I + 1;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int getSeatViewWidth(SSView mSsView, int paramInt) {
		return mSsView.seat_view_w;
	}

	/**
	 * 获取最大支付座位数
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int getImaxPay(SSView mSsView) {
		return mSsView.iMaxPay;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @param param
	 * @return
	 */
	public static boolean setScaleMapStatus(SSView mSsView, boolean param) {
		mSsView.isShowScaleMap = param;
//		if (mSsView.mSSThumView != null) {
//			mSsView.mSSThumView.setVisibility(View.VISIBLE);
//			
//		}
		return mSsView.isShowScaleMap;
	}

	/**
	 * 设置排数x轴偏移量
	 * 
	 * @param mSsView
	 * @param param
	 * @return
	 */
	public static float setXoffSet(SSView mSsView, float param) {
		//mSsView.offset_x = param;
		mSsView.offset_x = (float)mSsView.scale_u*param;
		return mSsView.offset_x;
	}

	/**
	 * 计算是第几列
	 * 
	 * @param mSsView
	 * @param param
	 * @return
	 */
	public static int getColumnNumber(SSView mSsView, int param) {
		return mSsView.getColumnNumber(param);
	}

	/**
	 * 计算是第几列
	 * 
	 * @param paramInt
	 * @return
	 */
	private int getColumnNumber(int paramInt) {
		try {
			int i1 = (paramInt + this.space_row - this.offset_left) / this.ss_seat_current_width;
			
			return i1;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return -1;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @param param1
	 * @param param2
	 * @return
	 */
	public static Rect a(SSView mSsView, int param1, int param2) {
		return mSsView.f(param1, param2);
	}

	private Rect f(int paramInt1, int paramInt2) {
		try {
			int v1 = this.ss_seat_current_width * paramInt1 + this.offset_left - this.space_row - this.space_L;
			int v2 = this.ss_seat_current_height * paramInt2 + this.offset_top - this.space_top - this.space_L;
			int v3 = (paramInt1 + 1) * this.ss_seat_current_width + this.offset_left - this.space_row + this.space_L;
			int v4 = (this.offset_top + 1) * this.ss_seat_current_height + this.offset_top - this.space_top + this.space_L;
			return new Rect(v1, v2, v3, v4);
		} catch (Exception e) {
			e.printStackTrace();
			return new Rect();
		}
	}

	/**
	 * 是否可以移动和点击
	 * 
	 * @param mSsView
	 * @return
	 */
	public static boolean isCanMove(SSView mSsView) {
		return mSsView.isCanMove;
	}
	
	private int getSeatSpace() {
		
		double val = space_L_buf*ss_seat_current_width/(init_seat_buf*1.0f);
		int result = (int)Math.ceil(val);
		return result;
		//return (int) Math.round(this.ss_seat_current_width / this.ss_seat_check_size
		//		* this.ss_between_offset);
	}

	/**
	 * 修改排数x轴的
	 * 
	 * @param mSsView
	 * @param param
	 * @return
	 */
	public static float modifyXoffSet(SSView mSsView, float param) {
		//mSsView.offset_x = mSsView.offset_x - param;
		mSsView.offset_x = mSsView.offset_x - (float)mSsView.scale_u*param;
		return mSsView.offset_x;
	}

	/**
	 * 设置每个座位的高度
	 * 
	 * @param mSsView
	 * @param param
	 * @return
	 */
	public static float setSeatHeight(SSView mSsView, int param) {
		mSsView.ss_seat_current_height = param;
		return mSsView.ss_seat_current_height;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static ArrayList getSeatInfosList(SSView mSsView) {
		return mSsView.mListSeatInfos;
	}

	/**
	 * 修改排数y轴的偏移量
	 * 
	 * @param mSsView
	 * @param param
	 * @return
	 */
	public static float modifyYoffSet(SSView mSsView, float param) {
		float old = mSsView.offset_y;
//		mSsView.offset_y = mSsView.offset_y - param);
		mSsView.offset_y = mSsView.offset_y - (float)(param*mSsView.scale_u);
		Log.e("Draw","modify_old:"+old+" now:"+mSsView.offset_y);
		return mSsView.offset_y;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @param param
	 * @return
	 */
	public static int setSeatWidth(SSView mSsView, int param) {
		mSsView.ss_seat_current_width = param;
		return mSsView.ss_seat_current_width;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static OnSeatClickListener getSeatClickListener(SSView mSsView) {
		return mSsView.mOnSeatClickListener;
	}

	/**
	 * 设置排数y轴偏移量
	 * 
	 * @param mSsView
	 * @param param
	 * @return
	 */
	public static float setYoffSet(SSView mSsView, float param) {
		float old = mSsView.offset_y;
//		mSsView.offset_y = param;
		mSsView.offset_y = (float)mSsView.scale_u*param;
		Log.e("Draw","set_old:"+old+" now:"+mSsView.offset_y);
		return mSsView.offset_y;
	}

	/**
	 * 计算是第几排
	 * 
	 * @param mSsView
	 * @param param
	 * @return
	 */
	public static int getRowNumber(SSView mSsView, int param) {
		return mSsView.getRowNumber(param);
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static ArrayList getSeatList(SSView mSsView) {
		return mSsView.mListSeatConditions;
	}

	/**
	 * 计算是第几排
	 * 
	 * @param paramInt
	 * @return
	 */
	private int getRowNumber(int paramInt) {
		try {
			int i1 = (paramInt + this.space_top - this.offset_top) / this.ss_seat_current_height;
			return i1;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return -1;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @param param
	 * @return
	 */
	public static int setLeftoffSet(SSView mSsView, int param) {
		mSsView.offset_left = param;
		return mSsView.offset_left;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int e(SSView mSsView) {
		mSsView.I--;
		return mSsView.I;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @return
	 */
	public static int f(SSView mSsView) {
		return mSsView.I;
	}

	/**
	 * new added
	 * 
	 * @param mSsView
	 * @param param
	 * @return
	 */
	public static int setRightOffSet(SSView mSsView, int param) {
		mSsView.offset_right = param;
		return mSsView.offset_right;
	}

	/**
	 * 设置按钮点击事件
	 * 
	 * @param paramOnSeatClickLinstener
	 */
	public void setOnSeatClickListener(
			OnSeatClickListener paramOnSeatClickLinstener) {
		this.mOnSeatClickListener = paramOnSeatClickLinstener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getPointerCount() == 1) {
			if (this.isZoom) {
				this.isZoom = false;
				this.isCanMove = false;
				this.scale = -1.0D;
				this.scale_u = 1.0D;
				
				
				
			}else{
				this.isCanMove = true;
			}
			hideSSThumView();
			// Toast.makeText(mContext, "单点触控", Toast.LENGTH_SHORT).show();
			while (this.ss_seat_current_width < this.ss_seat_min_width || this.ss_seat_current_height < this.ss_seat_min_height) {
				this.ss_seat_current_width++;
				this.ss_seat_current_height++;
				this.offset_left = (int) Math.round(this.ss_seat_current_width / 2.0D);
				this.offset_right = this.offset_left;
				this.space_L = getSeatSpace();
				// 滑到最左和最上
				SSView.setSpaceRow(this, 0);
				SSView.setXoffSet(this, 0.0F);
				SSView.setSpaceTop(this, 0);
				SSView.setYoffSet(this, 0.0F);
				invalidate();
			}
			while ((this.ss_seat_current_width > this.ss_seat_max_width) || (this.ss_seat_current_height > this.ss_seat_max_height)) {
				this.ss_seat_current_width--;
				this.ss_seat_current_height--;
				this.offset_left = (int) Math.round(this.ss_seat_current_width / 2.0D);
				this.offset_right = this.offset_left;
				 this.space_L = getSeatSpace();
				invalidate();
			}

			// 移动功能-点击事件
			this.mGestureDetector.onTouchEvent(event);
		} else {
			// Toast.makeText(mContext, "多点触控", Toast.LENGTH_SHORT).show();
			this.isZoom = true;
			
			if (mSSThumView != null) {
				mSSThumView.setVisibility(View.VISIBLE);
				
			}
			//Log.e("System.out", "ss_seat_current_width:"+ss_seat_current_width+" ss_seat_min_width:"+ss_seat_min_width);
//			if(ss_seat_current_width<ss_seat_min_width)
//			{
//				ss_seat_current_width = ss_seat_min_width;
//				ss_seat_current_height = ss_seat_min_height;
//				return true;
//			}
//			
//			if(ss_seat_current_width>ss_seat_max_width)
//			{
//				ss_seat_current_width = ss_seat_max_width;
//				ss_seat_current_height = ss_seat_max_height;
//				return true;
//			}
			
			
			scale(event);

		}

		return true;
	}

	
	public ArrayList<SelectSeat> getSelectSeatList()
	{
		return selectSeatList;
	}
	
	private void hideSSThumView()
	{
		handler.removeCallbacks(r);
		handler.postDelayed(r, 3000);
	}
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			
			
			if (mSSThumView != null) {
				mSSThumView.setVisibility(View.INVISIBLE);
				
			}
			
		};
	};
	private  Runnable r = new Runnable() {
	    public void run() {
	   
//	    	try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	    	
	    	handler.sendEmptyMessage(0);
	    	
	    }
	}; 
	
	
	
}
