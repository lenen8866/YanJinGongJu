//package com.read.scriptures.widget;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import com.read.scriptures.R;
//import com.read.scriptures.model.Category;
//import com.read.scriptures.model.Volume;
//import com.read.scriptures.ui.adapter.CategoryOneGridAdapter;
//import com.read.scriptures.ui.adapter.CategoryTwoGridAdapter;
//import com.read.scriptures.ui.adapter.SearchCategoryAdapter;
//import com.read.scriptures.ui.adapter.VolumeGridAdapter;
//
//import android.content.Context;
//import android.graphics.drawable.BitmapDrawable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.AdapterView;
//import android.widget.GridView;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//
//
//public class SearchContentOptionPopupWindow extends PopupWindow {
//
//    private Context mContext;
//    private View mRootView;
//
//    private List<Category> mRootCategory;
//    private Map<Category, List<Category>> mCategoryMaps;
//
//    private SearchCategoryAdapter mSearchCategoryAdapter;
//    private CategoryTwoGridAdapter mCategoryTwoGridAdapter;
//
//    private GridView mCategoryTwoGridView;
//    private ListView mCategorySectionListView;
//
//    private int mIndexCategory = 0;
//
//    private Category mNodeCategory;
//    private Volume mVolume;
//
//    private OnSelectChangeListener onSelectChangeListener;
//
//    public SearchContentOptionPopupWindow(Context context, List<Category> rootCategory, Map<Category, List<Category>> categoryMaps) {
//        super(context);
//        mContext = context;
//        mRootCategory = rootCategory;
//        mCategoryMaps = categoryMaps;
//        mRootView = LayoutInflater.from(context).inflate(R.layout.popup_search_content_options, null);
//        initView(mRootView);
//        this.setContentView(mRootView);
////        this.setBackgroundDrawable(new ColorDrawable(0x00000000));
//        this.setBackgroundDrawable(new BitmapDrawable());
//        this.setWidth(LayoutParams.MATCH_PARENT);
//        this.setHeight(LayoutParams.MATCH_PARENT);
//        this.setFocusable(true);
//        this.setAnimationStyle(R.style.PopupAnimationNone);
//    }
//
//    private void initView(View view) {
//        CategoryOneGridAdapter adapter = new CategoryOneGridAdapter(mContext, mRootCategory);
//        adapter.setIndex(mIndexCategory);
//        GridView gridView = (GridView) view.findViewById(R.id.gridview_1);
//        gridView.setNumColumns(mRootCategory.size());
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (mIndexCategory == position) {
//                    return;
//                }
//                mIndexCategory = position;
//                mNodeCategory = null;
//                mVolume = null;
//                if (onSelectChangeListener != null){
//                    onSelectChangeListener.onSelectChange();
//                }
//                CategoryOneGridAdapter adapter = (CategoryOneGridAdapter) parent.getAdapter();
//                adapter.setIndex(parent, position);
//                mCategoryTwoGridAdapter.setList(mCategoryMaps.get(mRootCategory.get(mIndexCategory)));
//                mCategoryTwoGridAdapter.setIndex(mCategoryTwoGridView, 0);
//                mCategoryTwoGridAdapter.notifyDataSetChanged();
//
//                mSearchCategoryAdapter.notifyDataSetChanged();
//                mSearchCategoryAdapter.setNodeCategorys(mCategoryMaps.get(mRootCategory.get(mIndexCategory)));
//                mCategorySectionListView.setSelection(0);
//            }
//        });
//        gridView.setAdapter(adapter);
//
//        mCategoryTwoGridAdapter = new CategoryTwoGridAdapter(mContext);
//        mCategoryTwoGridAdapter.setList(mCategoryMaps.get(mRootCategory.get(mIndexCategory)));
//        mCategoryTwoGridView = (GridView) view.findViewById(R.id.gridview_2);
//        mCategoryTwoGridView.setNumColumns(6);
//        mCategoryTwoGridView.setAdapter(mCategoryTwoGridAdapter);
//        mCategoryTwoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mCategoryTwoGridAdapter.setIndex(mCategoryTwoGridView, position);
//                if (position == 0) {
//                    mNodeCategory = null;
//                    mVolume = null;
//                    mSearchCategoryAdapter.setNodeCategorys(mCategoryMaps.get(mRootCategory.get(mIndexCategory)));
//                    mSearchCategoryAdapter.notifyDataSetChanged();
//                    mCategorySectionListView.setSelection(0);
//                } else {
//                    ArrayList<Category> categories = new ArrayList<Category>();
//                    categories.add(mCategoryTwoGridAdapter.getItem(position));
//                    mNodeCategory = mCategoryTwoGridAdapter.getItem(position);
//                    mVolume = null;
//                    mSearchCategoryAdapter.setNodeCategorys(categories);
//                    mSearchCategoryAdapter.notifyDataSetChanged();
//                    mCategorySectionListView.setSelection(0);
//                }
//                if (onSelectChangeListener != null){
//                    onSelectChangeListener.onSelectChange();
//                }
//            }
//        });
//        mSearchCategoryAdapter = new SearchCategoryAdapter(mContext);
//        mSearchCategoryAdapter.setNodeCategorys(mCategoryMaps.get(mRootCategory.get(mIndexCategory)));
//        mSearchCategoryAdapter.setItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                VolumeGridAdapter adapter = (VolumeGridAdapter) parent.getAdapter();
//                int index  = (Integer)parent.getTag();
//                mNodeCategory = mSearchCategoryAdapter.getItem(index);
//                mVolume = adapter.getItem(position);
//                if (onSelectChangeListener != null){
//                    onSelectChangeListener.onSelectChange();
//                }
//                dismiss();
//            }
//        });
//        mCategorySectionListView = (ListView) view.findViewById(R.id.listview);
//        mCategorySectionListView.setAdapter(mSearchCategoryAdapter);
//    }
//
//    public Category getRootCategory() {
//        return mRootCategory.get(mIndexCategory);
//    }
//
//    public Category getNodeCategory() {
//        return mNodeCategory;
//    }
//
//    public Volume getVolume() {
//        return mVolume;
//    }
//
//    public interface OnSelectChangeListener{
//        public void onSelectChange();
//    }
//
//    public void setOnSelectChangeListener(OnSelectChangeListener onSelectChangeListener) {
//        this.onSelectChangeListener = onSelectChangeListener;
//    }
//}
