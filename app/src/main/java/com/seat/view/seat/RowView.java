package com.seat.view.seat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class RowView extends View {

	private Bitmap rowBitmap;
	public RowView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		
		
		super.onDraw(canvas);
		
		if(rowBitmap!=null)
			canvas.drawBitmap(rowBitmap,0,0,null);
	}
	
	
	public void update(Bitmap bitmap)
	{
		rowBitmap = bitmap;
		invalidate();
	}
	
}
