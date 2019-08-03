package com.wemall.foundation.domain;

public class Register {
	//id
		private int id;
		//企业名称
		private String PurchasersName;
		//姓名
		private String attention;
		//电话
		private String phone;
		//省
		private String province;
		//市
				private String city;
				//区
				private String area;
				//详细地址
				private String betterAddress;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getPurchasersName() {
			return PurchasersName;
		}
		public void setPurchasersName(String purchasersName) {
			PurchasersName = purchasersName;
		}
		public String getAttention() {
			return attention;
		}
		public void setAttention(String attention) {
			this.attention = attention;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getProvince() {
			return province;
		}
		public void setProvince(String province) {
			this.province = province;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getArea() {
			return area;
		}
		public void setArea(String area) {
			this.area = area;
		}
		public String getBetterAddress() {
			return betterAddress;
		}
		public void setBetterAddress(String betterAddress) {
			this.betterAddress = betterAddress;
		}
		@Override
		public String toString() {
			return "Register [id=" + id + ", PurchasersName=" + PurchasersName + ", attention=" + attention + ", phone=" + phone
					+ ", province=" + province + ", city=" + city + ", area=" + area + ", betterAddress="
					+ betterAddress + "]";
		}
}
