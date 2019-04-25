package com.seat.view.seat;

public class Seat {
	/** 序号，当为走道时 为"Z" */
	private String n = null;
	/** 损坏标签 */
	private String damagedFlg = null;
	/** 情侣座 */
	private String loveInd = null;

	private String seatCode;// 座位实际编码

	private String showCol;// 展示列号
	private String showRow;// 展示行号
	private int mCol;
	private int mRow;

	public String getShowCol() {
		return showCol;
	}

	public void setShowCol(String showCol) {
		this.showCol = showCol;
	}

	public String getShowRow() {
		return showRow;
	}

	public void setShowRow(String showRow) {
		this.showRow = showRow;
	}

	public String getSeatCode() {
		return seatCode;
	}

	public void setSeatCode(String seatCode) {
		this.seatCode = seatCode;
	}

	public void setN(String paramString) {
		this.n = paramString;
	}

	public boolean a() {
		return ("1".equals(this.loveInd)) || ("2".equals(this.loveInd));
	}

	public String getN() {
		return this.n;
	}

	public void setDamagedFlg(String paramString) {
		this.damagedFlg = paramString;
	}

	public String getDamagedFlg() {
		return this.damagedFlg;
	}

	public void setLoveInd(String paramString) {
		this.loveInd = paramString;
	}

	public String getLoveInd() {
		return this.loveInd;
	}

	public int getCol() {
		return mCol;
	}

	public void setCol(int col) {
		this.mCol = col;
	}

	public int getRow() {
		return mRow;
	}

	public void setRow(int row) {
		this.mRow = row;
	}
}