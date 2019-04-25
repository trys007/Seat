package com.seat.bean;

import com.seat.view.seat.SeatDetailBean;

import java.util.ArrayList;

public class SeatBean {

	private String nums_cols;
	private String nums_rows;
	private ArrayList<SeatDetailBean> seats;
	public String getNums_cols() {
		return nums_cols;
	}
	public void setNums_cols(String nums_cols) {
		this.nums_cols = nums_cols;
	}
	public String getNums_rows() {
		return nums_rows;
	}
	public void setNums_rows(String nums_rows) {
		this.nums_rows = nums_rows;
	}
	public ArrayList<SeatDetailBean> getSeats() {
		return seats;
	}
	public void setSeats(ArrayList<SeatDetailBean> seats) {
		this.seats = seats;
	}
	
	
	
}
