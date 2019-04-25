package com.seat.view.seat;


import java.util.ArrayList;

/**
 * @author Sunny
 *
 */
public class SeatInfo
{
  private String row = null;
  private String desc = null;
  private ArrayList<Seat> mSeatList = null;

  private String c(String paramString)
  {
    if (paramString == null)
      paramString = "";
    return paramString;
  }

  public Seat getSeat(int paramInt)
  {
    if ((paramInt > this.mSeatList.size()) || (paramInt < 0))
      return new Seat();
    return (Seat)this.mSeatList.get(paramInt);
  }

  public String getDesc()
  {
    return c(this.desc);
  }

  public void addSeat(Seat paramSeat)
  {
    this.mSeatList.add(paramSeat);
  }

  public void setRow(String paramString)
  {
    this.row = paramString;
  }

  public ArrayList getSeatList()
  {
    return this.mSeatList;
  }

  public void setSeatList(ArrayList<Seat> seat_list){
	  this.mSeatList = seat_list;
  }
  
  
  public void setDesc(String paramString)
  {
    this.desc = paramString;
  }
}