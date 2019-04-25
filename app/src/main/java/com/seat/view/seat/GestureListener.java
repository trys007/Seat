package com.seat.view.seat;

import java.util.ArrayList;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;


class GestureListener extends GestureDetector.SimpleOnGestureListener {
	private SSView mSsView;

	GestureListener(SSView paramSSView) {
		mSsView = paramSSView;
	}

	public boolean onDoubleTap(MotionEvent paramMotionEvent) {
		return super.onDoubleTap(paramMotionEvent);
	}

	public boolean onDoubleTapEvent(MotionEvent paramMotionEvent) {
		return super.onDoubleTapEvent(paramMotionEvent);
	}

	public boolean onDown(MotionEvent paramMotionEvent) {
		return false;
	}

	public boolean onFling(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
		return false;
	}

	public void onLongPress(MotionEvent paramMotionEvent) {
	}

	
	private float click_buf=0;
	public boolean onScroll(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2, float x_scroll_distance, float y_scroll_distance) {
		//是否可以移动和点击
		if(!SSView.isCanMove(mSsView)){
			return false;
		}
		//显示缩略图
		SSView.setScaleMapStatus(mSsView,true);
		boolean bool1 = true;
		boolean bool2 = true;
		if ((SSView.getSeatViewWidth(mSsView) < mSsView.getMeasuredWidth())
				&& (0.0F == SSView.getXoffSet(mSsView))){
			bool1 = false;
		}
		
		if ((SSView.getSeatViewHeight(mSsView) < mSsView.getMeasuredHeight())
				&& (0.0F == SSView.getYoffSet(mSsView))){
			bool2  = false;
		}
		
		if(bool1){
			int k = Math.round(x_scroll_distance);
			//修改排数x轴的偏移量
			SSView.modifyXoffSet(mSsView, (float)k);
//			Log.i("TAG", SSView.v(mSsView)+"");
			//修改座位距离排数的横向距离
			SSView.modifySpaceRow(mSsView, k);
//			Log.i("TAG", SSView.r(mSsView)+"");
			if (SSView.getSpaceRow(mSsView) < 0) {
				//滑到最左
				SSView.setSpaceRow(mSsView, 0);
				SSView.setXoffSet(mSsView, 0.0F);
			}
			
			if(SSView.getSpaceRow(mSsView) + mSsView.getMeasuredWidth() > SSView.getSeatViewWidth(mSsView)){
				//滑到最右
				SSView.setSpaceRow(mSsView, SSView.getSeatViewWidth(mSsView) - mSsView.getMeasuredWidth());
				SSView.setXoffSet(mSsView, (float)(mSsView.getMeasuredWidth() - SSView.getSeatViewWidth(mSsView)));
				
			}
		}
		
		if(bool2){
			//上负下正- 往下滑则减
			int j = Math.round(y_scroll_distance);
			//修改排数y轴的偏移量
			SSView.modifyYoffSet(mSsView, (float)j);
			//修改可视座位距离顶端的距离
			SSView.modifySpaceTop(mSsView, j);
			Log.i("TAG", SSView.getSpaceTop(mSsView)+"");
			if (SSView.getSpaceTop(mSsView) < 0){
				//滑到顶
				SSView.setSpaceTop(mSsView, 0);
				SSView.setYoffSet(mSsView, 0.0F);
				click_buf=0;
			}
			
			 if (SSView.getSpaceTop(mSsView) + mSsView.getMeasuredHeight() > SSView
						.getSeatViewHeight(mSsView)){
				//滑到底
					SSView.setSpaceTop(mSsView, SSView.getSeatViewHeight(mSsView) - mSsView.getMeasuredHeight());
					float mH = mSsView.getMeasuredHeight();
					float sH = SSView.getSeatViewHeight(mSsView);
					float buf = (float)(mSsView.getMeasuredHeight() - SSView.getSeatViewHeight(mSsView));
					if(buf>0)
					{
						click_buf = buf;
						buf=0;
						
					}else
					{
						click_buf=0;
					}
					SSView.setYoffSet(mSsView, buf);
					
			 }
		}
		
		mSsView.invalidate();
		
//		Log.i("GestureDetector", "onScroll----------------------");
		return false;
	}

	public void onShowPress(MotionEvent paramMotionEvent) {
	}

	public boolean onSingleTapConfirmed(MotionEvent paramMotionEvent) {
		return false;
	}

	public boolean onSingleTapUp(MotionEvent paramMotionEvent) {
//		Log.i("GestureDetector", "onSingleTapUp");
//		if(!SSView.a(mSsView)){
//			return false;
//		}
		//列数
		int i = SSView.getColumnNumber(mSsView, (int)paramMotionEvent.getX());
		//排数
		int j = SSView.getRowNumber(mSsView, (int) (paramMotionEvent.getY()+click_buf));
	
		if((j>=0 && j< SSView.getSeatList(mSsView).size())){
			if(i>=0 && i<((ArrayList<Integer>)(SSView.getSeatList(mSsView).get(j))).size()){
				Log.i("TAG", "排数："+ j + "列数："+i);
				ArrayList<Integer> localArrayList = (ArrayList<Integer>) SSView.getSeatList(mSsView).get(j);
				switch (localArrayList.get(i).intValue()) {
				case 3://已选中
					localArrayList.set(i, Integer.valueOf(1));
					if(SSView.getSeatClickListener(mSsView)!=null){
						SeatInfo info = (SeatInfo)(SSView.getSeatInfosList(mSsView).get(j));
						Seat seat = (Seat)(info.getSeatList().get(i));
						SSView.getSeatClickListener(mSsView).selectSeatCancel(i, j, false,seat);
					}
					
					
					
					break;
				case 1://可选
					if(mSsView.getSelectSeatList().size()<mSsView.max_select)
					{
						localArrayList.set(i, Integer.valueOf(3));
					}
					
					if(SSView.getSeatClickListener(mSsView)!=null){
						
						SeatInfo info = (SeatInfo)(SSView.getSeatInfosList(mSsView).get(j));
						Seat seat = (Seat)(info.getSeatList().get(i));
						SSView.getSeatClickListener(mSsView).selectSeatSure(i, j, false,seat);
					}
					break;
				default:
					break;
				}
				
			}
		}
		
		
		
		
		
		
		
		
		//显示缩略图
		SSView.setScaleMapStatus(mSsView,true);
		mSsView.invalidate();
		return false;
	}
}