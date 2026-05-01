package com.read.scriptures.ui.adapter;

import android.content.Context;


import com.read.scriptures.EIUtils.EIBaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lim
 * @Description: 支持选择的Adapter
 * @mail lgmshare@gmail.com
 * @date 2014年7月7日  上午11:04:44
 */
public abstract class CustomSelectableAdapter<K, T> extends EIBaseAdapter<T> {

	private boolean broadcasting = false;

	protected final Map<K, T> mSelectMap = new HashMap<K, T>();

	private SelectInterceptor<T> mSelectInterceptor;

	public CustomSelectableAdapter(Context context) {
		super(context);
	}

	public boolean setChecked(int position, boolean isChecked) {
		T t = getItem(position);
		return innerSetChecked(position, t, isChecked);
	}

	/**
	 * 设置Item被选中
	 * 
	 * @param t
	 * @param isChecked
	 */
	public boolean setCheckedById(T t, boolean isChecked) {
		int position = mList.indexOf(t);
		if (position < 0) {
			return false;
		}
		return innerSetChecked(position, t, isChecked);
	}

	private boolean innerSetChecked(int position, T t, boolean isChecked) {
		if (mSelectInterceptor != null && mSelectInterceptor.interceptSelect(t)) {
			return false;
		}
		if (t != null) {
			if (isChecked) {
				mSelectMap.put(getItemCheckRecordKey(t,position), t);
			} else {
				mSelectMap.remove(getItemCheckRecordKey(t,position));
			}
			if (broadcasting) {
				return true;
			}
			broadcasting = true;
			onSelectChanged(position, isChecked);

			broadcasting = false;
		}
		return true;
	}

	/**
	 * 选中所有
	 */
	public void checkAll() {
		if (mList != null) {
			int size = mList.size();
			for (int i = 0; i < size; i++) {
				T t = mList.get(i);
				if (t != null) {
					mSelectMap.put(getItemCheckRecordKey(t,i), t);
					onSelectChanged(i, true);
				}
			}
		}
		notifyDataSetChanged();
	}

	/**
	 * 取消所有选中状态
	 * 
	 * @param notifySelectHolder
	 */
	public void unCheckAll(boolean notifySelectHolder) {
		if (notifySelectHolder && mList != null) {
			int size = mList.size();
			for (int i = 0; i < size; i++) {
				T t = mList.get(i);
				if (t != null) {
					onSelectChanged(i, false);
				}
			}
		}
		mSelectMap.clear();
		notifyDataSetChanged();
	}

	public List<T> getChecked() {
		if (mList == null || mList.size() <= 0 || mSelectMap.size() <= 0) {
			return null;
		}
		List<T> result = new ArrayList<T>();
		for (T t : mList) {
			if (mSelectMap.containsValue(t)) {
				result.add(t);
			}
		}
		return result;
	}

	public int getCheckedCount() {
		return mSelectMap.size();
	}

	/**
	 * 是否被选中
	 * 
	 * @param key
	 * @return
	 */
	public boolean isCheckedKey(K key) {
		return mSelectMap.get(key) != null;
	}

	protected void onSelectChanged(int position, boolean isSelected) {
	}

	private boolean mSelectModel = false;
	private boolean mBroadcasting = false;

	public void setSelectModel(boolean selectMode) {
		if (mSelectModel == selectMode) {
			return;
		}
		this.mSelectModel = selectMode;
		if (!mBroadcasting) {
			mBroadcasting = true;
			onSelectModelChanged(selectMode);
			mBroadcasting = false;
		}
		if (!mSelectModel) {
			mSelectMap.clear();
		}
	}

	public boolean isSelectModel() {
		return mSelectModel;
	}

	/**
	 * 被选中的Item会被放到一个Map<Key,ItemData>,该方法返回之作为这个map中的key
	 *
	 * @param t
	 * @return
	 */
	protected abstract K getItemCheckRecordKey(T t,int position);

	protected abstract void onSelectModelChanged(boolean selectModel);


	public interface SelectInterceptor<D> {
		public boolean interceptSelect(D data);
	}

	public void setSelectInterceptor(SelectInterceptor<T> interceptor) {
		mSelectInterceptor = interceptor;
	}

}
