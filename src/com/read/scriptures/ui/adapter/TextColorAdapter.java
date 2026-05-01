package com.read.scriptures.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.read.scriptures.R;
import com.read.scriptures.util.SharedPreferencesUtils;

import java.util.List;


/**
 * Created by May on 2017/9/26.
 * RecommendAdapter for listView
 */

public class TextColorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<String> mNewsArray;

    private final LayoutInflater inflater;

    public TextColorAdapter(Context ctx, List<String> newsArray) {//, OnRecyclerItemClickonRecyclerItemClick,OnRecyclerItemLongClick onRecyclerItemLongClick
        mContext = ctx;
        mNewsArray = newsArray;
//        mOnRecyclerItemClickListener = onRecyclerItemClick;
//        mOnRecyclerItemLongClickListener = onRecyclerItemLongClick;
        inflater = LayoutInflater.from(mContext);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_text_color, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).setData(position);
    }

    @Override
    public int getItemCount() {
        return mNewsArray.size() ;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {//implements View.OnClickListener
        private ImageView mImageView;

        ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_text_color);
            //itemView.setOnClickListener(this);
        }

        public void setData(final int position) {
            switch (position+1) {
                case 1:
                    mImageView.setImageResource(R.color.text_1);
                    break;
                case 2:
                    mImageView.setImageResource(R.color.text_2);
                    break;
                case 3:
                    mImageView.setImageResource(R.color.text_3);
                    break;
                case 4:
                    mImageView.setImageResource(R.color.text_4);
                    break;
                case 5:
                    mImageView.setImageResource(R.color.text_5);
                    break;
                case 6:
                    mImageView.setImageResource(R.color.text_6);
                    break;
                case 7:
                    mImageView.setImageResource(R.color.text_7);
                    break;
                case 8:
                    mImageView.setImageResource(R.color.text_8);
                    break;
                case 9:
                    mImageView.setImageResource(R.color.text_9);
                    break;
                case 10:
                    mImageView.setImageResource(R.color.text_10);
                    break;
                case 11:
                    mImageView.setImageResource(R.color.text_11);
                    break;
                case 12:
                    mImageView.setImageResource(R.color.text_12);
                    break;
                case 13:
                    mImageView.setImageResource(R.color.text_13);
                    break;
                case 14:
                    mImageView.setImageResource(R.color.text_14);
                    break;
                case 15:
                    mImageView.setImageResource(R.color.text_15);
                    break;
                case 16:
                    mImageView.setImageResource(R.color.text_16);
                    break;
                case 17:
                    mImageView.setImageResource(R.color.text_17);
                    break;
                case 18:
                    mImageView.setImageResource(R.color.text_18);
                    break;
                case 19:
                    mImageView.setImageResource(R.color.text_19);
                    break;
                case 20:
                    mImageView.setImageResource(R.color.text_20);
                    break;
                case 21:
                    mImageView.setImageResource(R.color.text_21);
                    break;
                case 22:
                    mImageView.setImageResource(R.color.text_22);
                    break;
                case 23:
                    mImageView.setImageResource(R.color.text_23);
                    break;
                case 24:
                    mImageView.setImageResource(R.color.text_24);
                    break;
                case 25:
                    mImageView.setImageResource(R.color.text_25);
                    break;
            }
            if (position == SharedPreferencesUtils.getTextNumber(mContext)){
                mImageView.setBackground(mContext.getResources().getDrawable(R.drawable.btn_red_border_pressed));
            }else {
                mImageView.setBackground(mContext.getResources().getDrawable(R.drawable.btn_red_border_normal));
            }
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(view,position);
                    SharedPreferencesUtils.saveTextNumber(mContext,position);
                    notifyDataSetChanged();
                }
            });
        }
//        @Override
//        public void onClick(View v) {
//            if (mOnRecyclerItemClickListener != null) {
//                int itemPosition = getAdapterPosition();
//                mOnRecyclerItemClickListener.onItemClick(v, itemPosition);
//            } else {
//                Log.e("NewsAdapter", "没有点击监听");
//            }
//        }
    }


    private OnItemOnClickListener listener;

    public void setOnItemClickListener(OnItemOnClickListener listener)
    {
        this.listener = listener;
    }

    public interface OnItemOnClickListener
    {
        void onItemClick(View v, int position);
    }


}
