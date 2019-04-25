package com.seat.view.seat;


public abstract interface OnSeatClickListener
{
  

  public abstract boolean selectSeatCancel(int paramInt1, int paramInt2, boolean paramBoolean, Seat seat);

  public abstract boolean selectSeatSure(int paramInt1, int paramInt2, boolean paramBoolean, Seat seat);
  
  
}