package com.seat.bean;

import java.util.List;

/**
 * Class Description:
 *
 * @author xuzhou
 */
public class SeatResultBean {

	private String code;
	private String message;

	private TheAuditoriumSeatsListBean datas;
	
	public TheAuditoriumSeatsListBean getDatas() {
		return datas;
	}

	public void setDatas(TheAuditoriumSeatsListBean datas) {
		this.datas = datas;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static class TheAuditoriumSeatsListBean {
		private int seat_row;
		private int seat_column;
		private List<TheAuditoriumSeatsBean> seats;
		private TheAuditoriumFilmBaseBean filmbase;
		private TheAuditoriumSeatStatusBean seatstatus;
		
		public int getSeat_row() {
			return seat_row;
		}
		public void setSeat_row(int seat_row) {
			this.seat_row = seat_row;
		}
		public int getSeat_column() {
			return seat_column;
		}
		public void setSeat_column(int seat_column) {
			this.seat_column = seat_column;
		}
		public List<TheAuditoriumSeatsBean> getSeats() {
			return seats;
		}
		public void setSeats(List<TheAuditoriumSeatsBean> seats) {
			this.seats = seats;
		}
		public TheAuditoriumFilmBaseBean getFilmbase() {
			return filmbase;
		}
		public void setFilmbase(TheAuditoriumFilmBaseBean filmbase) {
			this.filmbase = filmbase;
		}
		public TheAuditoriumSeatStatusBean getSeatstatus() {
			return seatstatus;
		}
		public void setSeatstatus(TheAuditoriumSeatStatusBean seatstatus) {
			this.seatstatus = seatstatus;
		}
		
	}

	public static class TheAuditoriumSeatsBean {
		
		private int coord_x;
		private int coord_y;
		private String row;
		private String column;
		private String third_seat_code;
		private String seat_code;
		private int love;
		private int status;
		private TheAuditoriumUserMainsBean usermains;
		
		public int getCoord_x() {
			return coord_x;
		}
		public void setCoord_x(int coord_x) {
			this.coord_x = coord_x;
		}
		public int getCoord_y() {
			return coord_y;
		}
		public void setCoord_y(int coord_y) {
			this.coord_y = coord_y;
		}
		public String getRow() {
			return row;
		}
		public void setRow(String row) {
			this.row = row;
		}
		public String getColumn() {
			return column;
		}
		public void setColumn(String column) {
			this.column = column;
		}
		public String getThird_seat_code() {
			return third_seat_code;
		}
		public void setThird_seat_code(String third_seat_code) {
			this.third_seat_code = third_seat_code;
		}
		public String getSeat_code() {
			return seat_code;
		}
		public void setSeat_code(String seat_code) {
			this.seat_code = seat_code;
		}
		public int getLove() {
			return love;
		}
		public void setLove(int love) {
			this.love = love;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public TheAuditoriumUserMainsBean getUsermains() {
			return usermains;
		}
		public void setUsermains(TheAuditoriumUserMainsBean usermains) {
			this.usermains = usermains;
		}
		
	}
	
	public static class TheAuditoriumFilmBaseBean {
		
		private String filmId;
		private String filmName;
		private String filmType;
		private String filmLanguage;
		private String filmPoster;
		private String showDateTime;
		private String cinemaName;
		private String screenName;
		private String cinemaId;
		private String screenId;
		
		public String getFilmId() {
			return filmId;
		}
		public void setFilmId(String filmId) {
			this.filmId = filmId;
		}
		public String getFilmName() {
			return filmName;
		}
		public void setFilmName(String filmName) {
			this.filmName = filmName;
		}
		public String getFilmType() {
			return filmType;
		}
		public void setFilmType(String filmType) {
			this.filmType = filmType;
		}
		public String getFilmLanguage() {
			return filmLanguage;
		}
		public void setFilmLanguage(String filmLanguage) {
			this.filmLanguage = filmLanguage;
		}
		public String getFilmPoster() {
			return filmPoster;
		}
		public void setFilmPoster(String filmPoster) {
			this.filmPoster = filmPoster;
		}
		public String getShowDateTime() {
			return showDateTime;
		}
		public void setShowDateTime(String showDateTime) {
			this.showDateTime = showDateTime;
		}
		public String getCinemaName() {
			return cinemaName;
		}
		public void setCinemaName(String cinemaName) {
			this.cinemaName = cinemaName;
		}
		public String getScreenName() {
			return screenName;
		}
		public void setScreenName(String screenName) {
			this.screenName = screenName;
		}
		public String getCinemaId() {
			return cinemaId;
		}
		public void setCinemaId(String cinemaId) {
			this.cinemaId = cinemaId;
		}
		public String getScreenId() {
			return screenId;
		}
		public void setScreenId(String screenId) {
			this.screenId = screenId;
		}
		
	}
	
	public static class TheAuditoriumUserMainsBean {
		
		private String companyName;
		private String sex;
		private String customerName;
		private String mobile;
		private String couponId;
		private String seatNumber;
		private String amount;
		
		public String getCompanyName() {
			return companyName;
		}
		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}
		public String getSex() {
			return sex;
		}
		public void setSex(String sex) {
			this.sex = sex;
		}
		public String getCustomerName() {
			return customerName;
		}
		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		public String getCouponId() {
			return couponId;
		}
		public void setCouponId(String couponId) {
			this.couponId = couponId;
		}
		public String getSeatNumber() {
			return seatNumber;
		}
		public void setSeatNumber(String seatNumber) {
			this.seatNumber = seatNumber;
		}
		public String getAmount() {
			return amount;
		}
		public void setAmount(String amount) {
			this.amount = amount;
		}
		
	}
	
	public static class TheAuditoriumSeatStatusBean {
		private int sold;
		private int nosold;
		
		public int getSold() {
			return sold;
		}
		public void setSold(int sold) {
			this.sold = sold;
		}
		public int getNosold() {
			return nosold;
		}
		public void setNosold(int nosold) {
			this.nosold = nosold;
		}
		
	}
	
}
