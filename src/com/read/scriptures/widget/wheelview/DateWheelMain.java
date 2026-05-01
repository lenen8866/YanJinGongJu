package com.read.scriptures.widget.wheelview;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.read.scriptures.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class DateWheelMain {

	private static int startYear = 1942, endYear = 2049;

	private View view;
	private WheelView wv_year;
	private WheelView wv_month;
	private WheelView wv_day;
	private WheelView wv_week;
	public int screenheight;

	private boolean isHideYear;//是否显示年份
	private boolean isLinter;//显示农历

	private int msYear;
	private int msMouth;
	private int msDay;
	
	private TextView mTvWeek;
	
	private ChangeYearViewListener mChangeYearViewListener;

	public DateWheelMain(View view) {
		initView(view);
	}
	
	public void setView(View view) {
		initView(view);
	}

	public static int getStartYear() {
		return startYear;
	}

	public static void setStartYear(int startYear) {
		DateWheelMain.startYear = startYear;
	}

	public static int getEndYear() {
		return endYear;
	}

	public static void setEndYear(int endYear) {
		DateWheelMain.endYear = endYear;
	}

	public ChangeYearViewListener getChangeYearViewListener() {
		return mChangeYearViewListener;
	}

	public void setChangeYearViewListener(ChangeYearViewListener listener) {
		this.mChangeYearViewListener = listener;
	}

	public void initView(View view) {
		this.view = view;
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_month = (WheelView) view.findViewById(R.id.month);
		wv_day = (WheelView) view.findViewById(R.id.day);
		wv_week = (WheelView) view.findViewById(R.id.week);
	}

	/**
	 * 弹出阳历日期时间选择器
	 */
	public void initDateTimePicker(int year, int month, int day) {
		if (isHideYear) {
			wv_year.setVisibility(View.GONE);
		}else{
			wv_year.setVisibility(View.VISIBLE);
		}
		wv_month.setVisibility(View.VISIBLE);
		wv_day.setVisibility(View.VISIBLE);
		// int year = calendar.get(Calendar.YEAR);
		// int month = calendar.get(Calendar.MONTH);
		// int day = calendar.get(Calendar.DATE);

		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// 年
		wv_year.setLabel("年");// 添加文字
		wv_year.setAdapter(new NumericWheelAdapter(startYear, endYear));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setCurrentItem(year - startYear);// 初始化时显示的数据
		wv_year.setVisibleItems(3);
		// 月
		wv_month.setLabel("月");
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		wv_month.setCurrentItem(month);
		wv_month.setVisibleItems(3);

		// 日
		wv_day.setLabel("日");
		wv_day.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (isLinter) {
			if (list_big.contains(String.valueOf(month + 1))) {
				wv_day.setAdapter(new NumericWheelAdapter(1, 31));
			} else if (list_little.contains(String.valueOf(month + 1))) {
				wv_day.setAdapter(new NumericWheelAdapter(1, 30));
			} else {
				// 闰年
				if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
					wv_day.setAdapter(new NumericWheelAdapter(1, 29));
				else
					wv_day.setAdapter(new NumericWheelAdapter(1, 28));
			}
		}
		wv_day.setCurrentItem(day - 1);
		wv_day.setVisibleItems(3);

		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + startYear;
				if (isLinter) {
					msYear = year_num;
					// 判断大小月及是否闰年,用来确定"日"的数据
					if (list_big.contains(String.valueOf(wv_month
							.getCurrentItem() + 1))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 31));
					} else if (list_little.contains(String.valueOf(wv_month
							.getCurrentItem() + 1))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 30));
					} else {
						if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0)
							wv_day.setAdapter(new NumericWheelAdapter(1, 29));
						else
							wv_day.setAdapter(new NumericWheelAdapter(1, 28));
					}
				}else{
					setLunarToGl(year_num, wv_month.getCurrentItem()+1, wv_day.getCurrentItem()+1);
					wv_month.setAdapter(new ArrayWheelAdapter<String>(toArray(DateUtil.runMonth(DateUtil.leapMonth(year_num)))));
					wv_month.getAdapter().getItem(wv_month.getCurrentItem());
					if (getNum(wv_month.getAdapter().getItem(wv_month.getCurrentItem())) == 0) {
						if (DateUtil.leapDays(year_num) == 29) {
							wv_day.setAdapter(new ArrayWheelAdapter<String>(chineseNumber2));
						} else {
							wv_day.setAdapter(new ArrayWheelAdapter<String>(chineseNumber1));
						}
					} else {
						if (DateUtil.monthDays(year_num, getNum(wv_month.getAdapter().getItem(wv_month.getCurrentItem()))) == 29) {
							wv_day.setAdapter(new ArrayWheelAdapter<String>(chineseNumber2));
						} else {
							wv_day.setAdapter(new ArrayWheelAdapter<String>(chineseNumber1));
						}
					}
				}
				updateWeek();
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				if (isLinter) {
					msMouth = newValue;
					// 判断大小月及是否闰年,用来确定"日"的数据
					if (list_big.contains(String.valueOf(month_num))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 31));
					} else if (list_little.contains(String.valueOf(month_num))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 30));
					} else {
						if (((wv_year.getCurrentItem() + startYear) % 4 == 0 && (wv_year
								.getCurrentItem() + startYear) % 100 != 0)
								|| (wv_year.getCurrentItem() + startYear) % 400 == 0)
							wv_day.setAdapter(new NumericWheelAdapter(1, 29));
						else
							wv_day.setAdapter(new NumericWheelAdapter(1, 28));
					}
				}else{
					setLunarToGl(wv_year.getCurrentItem() + startYear, month_num, wv_day.getCurrentItem()+1);
					if (getNum(wv_month.getAdapter().getItem(wv_month.getCurrentItem())) == 0) {
						if (DateUtil.leapDays(wv_year.getCurrentItem() + startYear) == 29) {
							wv_day.setAdapter(new ArrayWheelAdapter<String>(chineseNumber2));
						} else {
							wv_day.setAdapter(new ArrayWheelAdapter<String>(chineseNumber1));
						}
					} else {
						if (DateUtil.monthDays(wv_year.getCurrentItem() + startYear,
								getNum(wv_month.getAdapter().getItem(wv_month.getCurrentItem()))) == 29) {
							wv_day.setAdapter(new ArrayWheelAdapter<String>(chineseNumber2));
						} else {
							wv_day.setAdapter(new ArrayWheelAdapter<String>(chineseNumber1));
						}
					}
				}
				updateWeek();
			}
		};
		// 添加"日"监听
		OnWheelChangedListener wheelListener_day = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int day_num = newValue + 1;
				if (isLinter) {
					msDay = day_num;
				}else{
					setLunarToGl(wv_year.getCurrentItem() + startYear, wv_month.getCurrentItem()+1,day_num);
				}
				updateWeek();
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);
		wv_day.addChangingListener(wheelListener_day);
		setTextSize();

	}
	final String chineseNumber1[] = { "初一", "初二", "初三", "初四", "初五", "初六",
			"初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六",
			"十七", "十八", "十九", "廿十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六",
			"廿七", "廿八", "廿九", "卅十" };
	final String chineseNumber2[] = { "初一", "初二", "初三", "初四", "初五", "初六",
			"初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六",
			"十七", "十八", "十九", "廿十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六",
			"廿七", "廿八", "廿九" };
	/***
	 * 
	 * 弹出农历日期时间选择器
	 * 
	 */
	private void showlunarTimePicker() {
		if (isHideYear) {
			wv_year.setVisibility(View.GONE);
		}else{
			wv_year.setVisibility(View.VISIBLE);
		}
		wv_month.setVisibility(View.VISIBLE);
		wv_day.setVisibility(View.VISIBLE);
		String year[] = DateUtil.getYera();

		Calendar calendar = Calendar.getInstance();
		calendar.set(msYear, msMouth, msDay);
		Lunar lunar = new Lunar(calendar.getTime());
//		final String monthOfAlmanac[] = { "正月", "二月", "三月", "四月", "五月", "六月",
//				"七月", "八月", "九月", "十月", "冬月", "腊月" };
		final String monthOfAlmanac[] = toArray(DateUtil.runMonth(DateUtil.leapMonth(lunar.getLunarYear())));
//		final String daysOfAlmanac[] = { "初一", "初二", "初三", "初四", "初五", "初六",
//				"初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六",
//				"十七", "十八", "十九", "二十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六",
//				"廿七", "廿八", "廿九", "三十" }; // 农历的天数
		String daysOfAlmanac[] = null;
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (getNum(wv_month.getAdapter().getItem(wv_month.getCurrentItem())) == 0) {
			if (DateUtil.leapDays(msYear) == 29) {
				daysOfAlmanac = chineseNumber2;
			} else {
				daysOfAlmanac = chineseNumber1;
			}
		} else {
			if (DateUtil.monthDays(msYear, getNum(wv_month.getAdapter().getItem(wv_month.getCurrentItem()))) == 29) {
				daysOfAlmanac = chineseNumber2;
			} else {
				daysOfAlmanac = chineseNumber1;
			}
		}
		
		wv_year.setLabel("年");
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setVisibleItems(3);
		wv_year.setAdapter(new ArrayWheelAdapter<String>(year));// 设置"年"的显示数据
		wv_year.setCurrentItem(lunar.getLunarYear() - startYear);
		
		// 月
		wv_month.setLabel("月");
		wv_month.setCyclic(true);
		wv_month.setVisibleItems(3);
		wv_month.setAdapter(new ArrayWheelAdapter<String>(monthOfAlmanac));
		if (DateUtil.leapMonth(lunar.getLunarYear())> 0&&DateUtil.leapMonth(lunar.getLunarYear()) <= lunar.getLunarMonth()) {
//		if(lunar.isLeap()){
			wv_month.setCurrentItem(lunar.getLunarMonth());
		}else{
			wv_month.setCurrentItem(lunar.getLunarMonth()-1);
		}
		// 日
		wv_day.setLabel("日");
		wv_day.setCyclic(true);
		wv_day.setVisibleItems(3);
		wv_day.setAdapter(new ArrayWheelAdapter<String>(daysOfAlmanac));
		wv_day.setCurrentItem(lunar.getLunarDay()-1);
	}
	
	/**
	 * 初始化时间
	 * @param year
	 * @param month
	 * @param day
	 */
	public void setTime(final int year, final int month, final int day) {
		setTextSize();
		isLinter = true;
		msYear = year;
		msMouth = month;
		msDay = day;
		initDateTimePicker(year, month, day);
		updateWeek();
		final CheckBox btn_year = (CheckBox) view.findViewById(R.id.cb_none_year);
		final CheckBox btn_lunar = (CheckBox) view.findViewById(R.id.cb_lunar);
		btn_year.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isHideYear = true;
					wv_year.setVisibility(View.GONE);
				}else{
					isHideYear = false;
					wv_year.setVisibility(View.VISIBLE);
				}
				if (mChangeYearViewListener != null) {
					mChangeYearViewListener.onChange(isHideYear);
				}
			}
		});
		btn_lunar.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isLinter = false;
					showlunarTimePicker();
				}else{
					isLinter = true;
					initDateTimePicker(msYear, msMouth, msDay);
				}
			}
		});

	}
	
	/**
	 * 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
	 */
	private void setTextSize(){
		int textSize = 0;
		textSize = (screenheight / 160) * 4;
		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;
		wv_week.TEXT_SIZE = textSize;
	}
	
	/**
	 * 更新星期
	 */
	private void updateWeek(){
		// 日
		String[] week = {DateUtil.getWeekStr(getTime()),"",""};
		wv_week.setLabel("");
		wv_week.setCyclic(false);
		wv_week.setVisibleItems(3);
		wv_week.setAdapter(new ArrayWheelAdapter<String>(week));
		wv_week.setCurrentItem(0,false);
		wv_week.setCanScroll(true);
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
		return (wv_year.getCurrentItem() + startYear);
	}

	public int getMonth() {
		return (wv_month.getCurrentItem() + 1);
	}

	public int getDay() {
		return (wv_day.getCurrentItem() + 1);
	}
	
	private void setLunarToGl(int year, int mouth, int day){
		boolean isLeapMonth = DateUtil.leapMonth(year) == wv_month.getCurrentItem();
			if (DateUtil.leapMonth(year)> 0&&DateUtil.leapMonth(year) <= mouth) {
				mouth--;
			}
		int[] nums = LunarUtil.lunarToSolar(year, mouth, day, isLeapMonth);
		msYear = nums[0];
		msMouth = nums[1]-1;
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
		if (n.equals("正")) {
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
	
	public interface ChangeYearViewListener{
		public void onChange(boolean isShow);
	}
}
