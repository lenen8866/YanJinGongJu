package com.read.scriptures.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 登录用户
 * 
 */
public class User implements Serializable, Parcelable {

	private static final long serialVersionUID = 1L;

	// 0无效用户、1正常用户、2未审核、3该账户或则密码不存在
	public static final String STATUS_INVALIDATE = "0";
	public static final String STATUS_NORMAL = "1";
	public static final String STATUS_VERIFY = "2";
	public static final String STATUS_INEXISTENCE = "3";
	
	private String id;
	private String username;
	private String password;
	private String name;
	private String nickname;
	private String phone;
	private String age;
	private int sex;
	private String birthday;
	private String address;
	private String avatar;
	private String email;
	private String signature;
	private int state;
	private String longitude;
	private String latitude;
	private String registerTime;
	private String lastLoginTime;
	private String remark;
	
	private int vipType;
	
	public static final Creator<User> CREATOR = new Creator<User>() {
		@Override
		public User createFromParcel(Parcel source) {
			// 序列化user对象
			User user = new User();
			user.setId(source.readString());
			user.setUsername(source.readString());
			user.setNickname(source.readString());
			user.setAge(source.readString());
			user.setSex(source.readInt());
			return user;
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(username);
		dest.writeString(nickname);
		dest.writeString(age);
		dest.writeInt(sex);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}

	public String getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getVipType() {
		return vipType;
	}

	public void setVipType(int vipType) {
		this.vipType = vipType;
	}
	
}
