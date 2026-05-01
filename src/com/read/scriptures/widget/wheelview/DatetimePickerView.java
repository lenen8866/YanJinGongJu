package com.read.scriptures.widget.wheelview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import com.read.scriptures.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DatetimePickerView extends LinearLayout{

	private int startYear = 1942, endYear = 2049;
	
	private View mView;
	
	private WheelView mWheelViewYear;
	private WheelView mWheelViewMonth;
	private WheelView mWheelViewDay;

	private WheelView mWheelViewWeek;
	
	private CheckBox btn_year;
	private CheckBox btn_lunar;
	
	private int mScreenheight;
	private boolean isHideYear = false;//是否显示年份
	private boolean isLosar = true;//显示农历

	private int CalendarMode = 0;
	
	private int msYear;
	private int msMouth;
	private int msDay;
	private int msHours;
	private int msMins;
	
	final String chineseNumber1[] = { "初一", "初二", "初三", "初四", "初五", "初六",
			"初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六",
			"十七", "十八", "十九", "廿十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六",
			"廿七", "廿八", "廿九", "卅十" };
	final String chineseNumber2[] = { "初一", "初二", "初三", "初四", "初五", "初六",
			"初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六",
			"十七", "十八", "十九", "廿十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六",
			"廿七", "廿八", "廿九" };
	
	// 添加大小月月份并将其转换为list,方便之后的判断
	final String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
	final String[] months_little = { "4", "6", "9", "11" };
	
	final List<String> list_big = Arrays.asList(months_big);
	final List<String> list_little = Arrays.asList(months_little);
	
	private DatePickerChangeListener mDatePickerChangeListener;
	
	public DatetimePickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DatetimePickerView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		mView = LayoutInflater.from(getContext()).inflate(R.layout.datepicker_layout, this);
		
		mWheelViewYear = (WheelView) mView.findViewById(R.id.year);
		mWheelViewMonth = (WheelView) mView.findViewById(R.id.month);
		mWheelViewDay = (WheelView) mView.findViewById(R.id.day);
		mWheelViewWeek = (WheelView) mView.findViewById(R.id.week);

		btn_year = (CheckBox) mView.findViewById(R.id.cb_none_year);
		btn_lunar = (CheckBox) mView.findViewById(R.id.cb_lunar);
		btn_year.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isHideYear = true;
					mWheelViewYear.setVisibility(View.GONE);
				}else{
					isHideYear = false;
					mWheelViewYear.setVisibility(View.VISIBLE);
				}
			}
		});
		btn_lunar.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isLosar = false;
					if (CalendarMode == 1) {
						initDateTimePicker(msYear, msMouth, msDay, msHours, msMins);
					}else{
						initDateTimePicker(msYear, msMouth, msDay);
					}
				}else{
					isLosar = true;
					if (CalendarMode == 1) {
						initDateTimePicker(msYear, msMouth, msDay, msHours, msMins);
					}else{
						initDateTimePicker(msYear, msMouth, msDay);
					}
				}
			}
		});
		
		Calendar calendar = Calendar.getInstance();
		msYear = calendar.get(Calendar.YEAR);
		msMouth = calendar.get(Calendar.MONTH);
		msDay = calendar.get(Calendar.DATE);
		
	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}
	
	public int getScreenheight() {
		return mScreenheight;
	}

	public void setScreenheight(int screenheight) {
		this.mScreenheight = screenheight;
	}

	public DatePickerChangeListener getDatePickerChangeListener() {
		return mDatePickerChangeListener;
	}

	public void setDatePickerChangeListener(
			DatePickerChangeListener datePickerChangeListener) {
		this.mDatePickerChangeListener = datePickerChangeListener;
	}

	private void setYear(int year){
		mWheelViewYear.setLabel("年");// 添加文字
		mWheelViewYear.setAdapter(new NumericWheelAdapter(startYear, endYear));// 设置"年"的显示数据
		mWheelViewYear.setCyclic(true);// 可循环滚动
		mWheelViewYear.setCurrentItem(year - startYear);// 初始化时显示的数据
		mWheelViewYear.setVisibleItems(3);
		mWheelViewYear.addChangingListener(wheelListener_year);
	}
	
	private void setLunarYear(Lunar lunar) {
		String year[] = DateUtil.getYera();
		mWheelViewYear.setLabel("年");
		mWheelViewYear.setCyclic(true);// 可循环滚动
		mWheelViewYear.setVisibleItems(3);
		mWheelViewYear.setAdapter(new ArrayWheelAdapter<String>(year));// 设置"年"的显示数据
		mWheelViewYear.setCurrentItem(lunar.getLunarYear() - startYear);
		mWheelViewYear.addChangingListener(wheelListener_year);
	}
	
	/**
	 * 设置月
	 * @param month
	 */
	private void setMonth(int month){
		mWheelViewMonth.setLabel("月");
		mWheelViewMonth.setAdapter(new NumericWheelAdapter(1, 12));
		mWheelViewMonth.setCyclic(true);
		mWheelViewMonth.setCurrentItem(month);
		mWheelViewMonth.setVisibleItems(3);
		mWheelViewMonth.addChangingListener(wheelListener_month);
	}
	
	private void setLunarMonth(Lunar lunar){
		final String monthOfAlmanac[] = toArray(DateUtil.runMonth(DateUtil.leapMonth(lunar.getLunarYear())));
		mWheelViewMonth.setLabel("月");
		mWheelViewMonth.setCyclic(true);
		mWheelViewMonth.setVisibleItems(3);
		mWheelViewMonth.setAdapter(new ArrayWheelAdapter<String>(monthOfAlmanac));
		if (DateUtil.leapMonth(lunar.getLunarYear())> 0&&DateUtil.leapMonth(lunar.getLunarYear()) <= lunar.getLunarMonth()) {
			mWheelViewMonth.setCurrentItem(lunar.getLunarMonth());
		}else{
			mWheelViewMonth.setCurrentItem(lunar.getLunarMonth()-1);
		}
		mWheelViewMonth.addChangingListener(wheelListener_month);
	}
	
	private void setDay(int year, int month, int day){
		mWheelViewDay.setLabel("日");
		mWheelViewDay.setVisibleItems(3);
		mWheelViewDay.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (isLosar) {
			if (list_big.contains(String.valueOf(month + 1))) {
				mWheelViewDay.setAdapter(new NumericWheelAdapter(1, 31));
			} else if (list_little.contains(String.valueOf(month + 1))) {
				mWheelViewDay.setAdapter(new NumericWheelAdapter(1, 30));
			} else {
				// 闰年
				if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
					mWheelViewDay.setAdapter(new NumericWheelAdapter(1, 29));
				else
					mWheelViewDay.setAdapter(new NumericWheelAdapter(1, 28));
			}
		}
		mWheelViewDay.setCurrentItem(day - 1);
		mWheelViewDay.addChangingListener(wheelListener_day);
	}
	
	private void setLunarDay(Lunar lunar){
		String daysOfAlmanac[] = null;
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (getNum(mWheelViewMonth.getAdapter().getItem(mWheelViewMonth.getCurrentItem())) == 0) {
			if (DateUtil.leapDays(msYear) == 29) {
				daysOfAlmanac = chineseNumber2;
			} else {
				daysOfAlmanac = chineseNumber1;
			}
		} else {
			if (DateUtil.monthDays(msYear, getNum(mWheelViewMonth.getAdapter().getItem(mWheelViewMonth.getCurrentItem()))) == 29) {
				daysOfAlmanac = chineseNumber2;
			} else {
				daysOfAlmanac = chineseNumber1;
			}
		}
		mWheelViewDay.setLabel("日");
		mWheelViewDay.setCyclic(true);
		mWheelViewDay.setVisibleItems(3);
		mWheelViewDay.setAdapter(new ArrayWheelAdapter<String>(daysOfAlmanac));
		mWheelViewDay.setCurrentItem(lunar.getLunarDay()-1);
		mWheelViewDay.addChangingListener(wheelListener_day);
	}
	
	// 添加"年"监听
	OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			int year_num = newValue + startYear;
			if (isLosar) {
				msYear = year_num;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(mWheelViewMonth.getCurrentItem() + 1))) {
					mWheelViewDay.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(mWheelViewMonth.getCurrentItem() + 1))) {
					mWheelViewDay.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0)
						mWheelViewDay.setAdapter(new NumericWheelAdapter(1, 29));
					else
						mWheelViewDay.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}else{
				mWheelViewMonth.setAdapter(new ArrayWheelAdapter<String>(toArray(DateUtil.runMonth(DateUtil.leapMonth(year_num)))));
				if (mWheelViewMonth.getCurrentItem() > 11) {
					mWheelViewMonth.setCurrentItem(11);
				}else{
					mWheelViewMonth.setCurrentItem(mWheelViewMonth.getCurrentItem());
				}
				if (getNum(mWheelViewMonth.getAdapter().getItem(mWheelViewMonth.getCurrentItem())) == 0) {
					if (DateUtil.leapDays(year_num) == 29) {
						mWheelViewDay.setAdapter(new ArrayWheelAdapter<String>(chineseNumber2));
						if (mWheelViewDay.getCurrentItem() > 28) {
							mWheelViewDay.setCurrentItem(28);
						}
					} else {
						mWheelViewDay.setAdapter(new ArrayWheelAdapter<String>(chineseNumber1));
					}
				} else {
					if (DateUtil.monthDays(year_num, getNum(mWheelViewMonth.getAdapter().getItem(mWheelViewMonth.getCurrentItem()))) == 29) {
						mWheelViewDay.setAdapter(new ArrayWheelAdapter<String>(chineseNumber2));
						if (mWheelViewDay.getCurrentItem() > 28) {
							mWheelViewDay.setCurrentItem(28);
						}
					} else {
						mWheelViewDay.setAdapter(new ArrayWheelAdapter<String>(chineseNumber1));
					}
				}
				setLunarToGl(year_num, mWheelViewMonth.getCurrentItem()+1, mWheelViewDay.getCurrentItem()+1);
			}
			updateWeek();
			if (mDatePickerChangeListener != null) {
				mDatePickerChangeListener.onChanged();
			}
		}
	};
	// 添加"月"监听
	OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			int month_num = newValue + 1;
			if (isLosar) {
				msMouth = newValue;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					mWheelViewDay.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					mWheelViewDay.setAdapter(new NumericWheelAdapter(1, 30));
					if (mWheelViewDay.getCurrentItem() > 29) {
						mWheelViewDay.setCurrentItem(29);
					}
				} else {
					if (((mWheelViewYear.getCurrentItem() + startYear) % 4 == 0 && (mWheelViewYear
							.getCurrentItem() + startYear) % 100 != 0)
							|| (mWheelViewYear.getCurrentItem() + startYear) % 400 == 0){
						mWheelViewDay.setAdapter(new NumericWheelAdapter(1, 29));
						if (mWheelViewDay.getCurrentItem() > 28) {
							mWheelViewDay.setCurrentItem(28);
						}
					} else{
						mWheelViewDay.setAdapter(new NumericWheelAdapter(1, 28));
						if (mWheelViewDay.getCurrentItem() > 27) {
							mWheelViewDay.setCurrentItem(27);
						}
					}
				}
			}else{
				if (getNum(mWheelViewMonth.getAdapter().getItem(mWheelViewMonth.getCurrentItem())) == 0) {
					if (DateUtil.leapDays(mWheelViewYear.getCurrentItem() + startYear) == 29) {
						mWheelViewDay.setAdapter(new ArrayWheelAdapter<String>(chineseNumber2));
						if (mWheelViewDay.getCurrentItem() > 28) {
							mWheelViewDay.setCurrentItem(28);
						}
					} else {
						mWheelViewDay.setAdapter(new ArrayWheelAdapter<String>(chineseNumber1));
					}
				} else {
					if (DateUtil.monthDays(mWheelViewYear.getCurrentItem() + startYear,
							getNum(mWheelViewMonth.getAdapter().getItem(mWheelViewMonth.getCurrentItem()))) == 29) {
						mWheelViewDay.setAdapter(new ArrayWheelAdapter<String>(chineseNumber2));
						if (mWheelViewDay.getCurrentItem() > 28) {
							mWheelViewDay.setCurrentItem(28);
						}
					} else {
						mWheelViewDay.setAdapter(new ArrayWheelAdapter<String>(chineseNumber1));
					}
				}
				setLunarToGl(mWheelViewYear.getCurrentItem() + startYear, month_num, mWheelViewDay.getCurrentItem()+1);
			}
			updateWeek();
			if (mDatePickerChangeListener != null) {
				mDatePickerChangeListener.onChanged();
			}
		}
	};
	// 添加"日"监听
	OnWheelChangedListener wheelListener_day = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			int day_num = newValue + 1;
			if (isLosar) {
				msDay = day_num;
			}else{
				setLunarToGl(mWheelViewYear.getCurrentItem() + startYear, mWheelViewMonth.getCurrentItem()+1,day_num);
			}
			updateWeek();
			if (mDatePickerChangeListener != null) {
				mDatePickerChangeListener.onChanged();
			}
		}
	};
	
	// 添加"日"监听
	OnWheelChangedListener wheelListener_hours = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (mDatePickerChangeListener != null) {
				mDatePickerChangeListener.onChanged();
			}
		}
	};
	
	// 添加"日"监听
	OnWheelChangedListener wheelListener_mins = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (mDatePickerChangeListener != null) {
				mDatePickerChangeListener.onChanged();
			}
		}
	};
	
	public void initDateTimePicker( final int hours, final int mins){
		msHours = hours;
		msMins = mins;
		mWheelViewYear.setVisibility(View.GONE);
		mWheelViewMonth.setVisibility(View.GONE);
		mWheelViewDay.setVisibility(View.GONE);
		mWheelViewWeek.setVisibility(View.GONE);

		btn_year.setVisibility(View.GONE);
		btn_lunar.setVisibility(View.GONE);
		
		updateWeek();
		setTextSize();
	}
	
	public void initDateTimePicker(final int year, final int month, final int day){
		msYear = year;
		msMouth = month;
		msDay = day;
		
		//是否显示年
		if (isHideYear) {
			mWheelViewYear.setVisibility(View.GONE);
		}else{
			mWheelViewYear.setVisibility(View.VISIBLE);
		}
		mWheelViewMonth.setVisibility(View.VISIBLE);
		mWheelViewDay.setVisibility(View.VISIBLE);
		mWheelViewWeek.setVisibility(View.VISIBLE);

		if (isLosar) {
			setYear(year);
			setMonth(month);
			setDay(year, month, day);
		}else{
			Calendar calendar = Calendar.getInstance();
			calendar.set(msYear, msMouth, msDay);
			Lunar lunar = new Lunar(calendar.getTime());
			setLunarYear(lunar);
			setLunarMonth(lunar);
			setLunarDay(lunar);
		}
		
		updateWeek();
		setTextSize();
	}
	
	public void initDateTimePicker(final int year, final int month, final int day, final int hours, final int mins){
		msYear = year;
		msMouth = month;
		msDay = day;
		msHours = hours;
		msMins = mins;
		
		CalendarMode = 1;
		mWheelViewYear.setVisibility(View.GONE);
		mWheelViewMonth.setVisibility(View.VISIBLE);
		mWheelViewDay.setVisibility(View.VISIBLE);
		mWheelViewWeek.setVisibility(View.GONE);

		btn_year.setVisibility(View.GONE);
		
		if (!isLosar) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(msYear, msMouth, msDay);
			Lunar lunar = new Lunar(calendar.getTime());
			setLunarYear(lunar);
			setLunarMonth(lunar);
			setLunarDay(lunar);
		}else{
			setYear(year);
			setMonth(month);
			setDay(year, month, day);
		}
		
		updateWeek();
		setTextSize();
	}
	
	/**
	 * 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
	 */
	private void setTextSize(){
		int textSize = 0;
		textSize = (mScreenheight / 120) * 3;
		mWheelViewDay.TEXT_SIZE = textSize;
		mWheelViewMonth.TEXT_SIZE = textSize;
		mWheelViewYear.TEXT_SIZE = textSize;
		mWheelViewWeek.TEXT_SIZE = textSize;

	}
	
	/**
	 * 更新星期
	 */
	private void updateWeek(){
		String[] week = {DateUtil.getWeekStr(getTime()),"",""};
		mWheelViewWeek.setLabel("");
		mWheelViewWeek.setCyclic(false);
		mWheelViewWeek.setVisibleItems(3);
		mWheelViewWeek.setAdapter(new ArrayWheelAdapter<String>(week));
		mWheelViewWeek.setCurrentItem(0,false);
		mWheelViewWeek.setCanScroll(true);
	}

	/**
	 * 获取选中日期
	 * @return
	 */
	public String getTime() {
		StringBuffer sb = new StringBuffer();
		sb.append(msYear).append("-")
				.append(msMouth + 1).append("-")
				.append(msDay);
		return sb.toString();
	}

	public int getYear() {
		return msYear;
	}

	public int getMonth() {
		return (msMouth + 1);
	}

	public int getDay() {
		return msDay;
	}
	
	private void setLunarToGl(int year, int month, int day) {
		boolean isLeapMonth = DateUtil.leapMonth(year) == mWheelViewMonth.getCurrentItem();
		if (DateUtil.leapMonth(year) > 0 && DateUtil.leapMonth(year) <= month) {
			month--;
		}
		Log.e("", year + "--"+ month +"--"+ day);
		if (day > 30) {
			return;
		}
		if (month > 12) {
			return;
		}
		int[] nums = LunarUtil.lunarToSolar(year, month, day, isLeapMonth);
		msYear = nums[0];
		msMouth = nums[1] - 1;
		msDay = nums[2];
	}
	
	private String[] toArray(List<String> l) {
		String[] r = new String[l.size()];
		for (int i = 0; i < l.size(); i++) {
			r[i] = l.get(i);
		}
		return r;
	}
	
	private int getNum(String n) {
		int i = 0;
		if (TextUtils.isEmpty(n)) {
			i = 1;
		}else if (n.equals("正")) {
			i = 1;
		} else if (n.equals("二")) {
			i = 2;
		} else if (n.equals("三")) {
			i = 3;
		} else if (n.equals("四")) {
			i = 4;
		} else if (n.equals("五")) {
			i = 5;
		} else if (n.equals("六")) {
			i = 6;
		} else if (n.equals("七")) {
			i = 7;
		} else if (n.equals("八")) {
			i = 8;
		} else if (n.equals("九")) {
			i = 9;
		} else if (n.equals("十")) {
			i = 10;
		} else if (n.equals("冬")) {
			i = 11;
		} else if (n.equals("腊")) {
			i = 12;
		} else if (n.equals("闰")) {
			i = 0;
		}
		return i;
	}
	
	public interface DatePickerChangeListener{
		public void onChanged();
	}
}
