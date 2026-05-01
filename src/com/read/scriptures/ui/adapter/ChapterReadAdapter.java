package com.read.scriptures.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.read.scriptures.R;
import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.config.PreferenceConfig;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.util.CharUtils;
import com.read.scriptures.util.DisplayUtil;
import com.read.scriptures.util.FontsUtil;
import com.read.scriptures.util.LogUtil;
import com.read.scriptures.util.SearchTextUtil;
import com.read.scriptures.util.SharedUtil;
import com.read.scriptures.util.StringUtil;
import com.read.scriptures.util.TextUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.graphics.Typeface.BOLD;

/**
 * Created by LGM. Datetime: 2015/7/5. Email: lgmshare@mgail.com
 */
public class ChapterReadAdapter extends CustomSelectableAdapter<Integer, String> {

    private Context mContext;

    // 文字大小
    private int mTextSize = 28;
    // 字体颜色
    private int mTextColor = Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_DEFAULT);
    // item背景色
    private int mBackgroudColor;
    //阅读字段段间距
    private int mTextMagin = 0;
    //阅读左右间距
    private int mTextAround = 10;
    //阅读上下间距
    private int mTopAndBottomMargin = 10;
    //阅读上下间距
    private int mLineMargin = 10;
    private int mTipsPostion = 0;
    private String mTipsKeyword;

    private int mSeechIndex;
    private boolean mSpeechModel = false;
    private String mRemarkText = "";
    private String mTipsContent = "";

    private int mSearchType;
    private String mChapterName;
    private String mChapterContent;
    private String mChapterNameKeyWord;

    private boolean isFontFace = true;//是否显示楷体
    private int textColor = -1;//黑色
    private int defaultColor = Color.BLACK;//黑色
    private int HUAI_ZHU_CHAPTER_HAS_ZW = 0;// 1
    private int keyWordColor = Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_KEY_WORD);//关键字颜色
    private int remarkTextBackgroundColor = Color.parseColor(SystemConfig.DEFAULT_READ_REMARK_TEXT_BACKGROUND_COLOR_KEY_WORD);//朗读背景颜色
    //简繁模式
    private int mTextModel = 0;

    public ChapterReadAdapter(Context context) {
        super(context);
        mContext = context;
        isFontFace = SharedUtil.getBoolean(PreferenceConfig.Preference_font_face_setting, true);
        textColor = SharedUtil.getInt(PreferenceConfig.Preference_text_color_setting, -1);
    }

    public void setFlag(int flag) {
        this.HUAI_ZHU_CHAPTER_HAS_ZW = flag;
    }

    public void setTipsPostion(int tipsPostion) {
        this.mTipsPostion = tipsPostion;
    }

    public void setTipsKeyword(String tipsKeyword) {
        this.mTipsKeyword = tipsKeyword;
    }

    public void setSearchType(int mSearchType) {
        this.mSearchType = mSearchType;
    }

    public void setChapterName(String mChapterName) {
        this.mChapterName = mChapterName;
    }

    public void setChapterContent(String mChapterContent) {
        if (!TextUtils.isEmpty(mChapterContent)) {
            this.mChapterContent = mChapterContent.replace("　", "")
                    .replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "");
            if (this.mChapterContent.indexOf("〖") > 0) {
                //移除出处
                this.mChapterContent = this.mChapterContent.substring(this.mChapterContent.indexOf("〖"), this.mChapterContent.length());
            }
        }
    }

    public void setChapterNameKeyWord(String mChapterNameKeyWord) {
        this.mChapterNameKeyWord = mChapterNameKeyWord;
    }

    public void setReadModel(int readModel) {
        if (readModel == SystemConfig.READ_MODEL_NIGHT) {
            mTextColor = Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_NIGHT);
        } else {
            mTextColor = Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_DEFAULT);
        }

        // 阅读模式-选中item背景颜色
        if (readModel == SystemConfig.READ_MODEL_NIGHT) {
            mBackgroudColor = Color.parseColor("#616161");
        } else {
            mBackgroudColor = Color.parseColor("#BDBDBD");
        }
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    public void setTextMargin(int textMargin) {
        this.mTextMagin = textMargin;
    }

    public void setLineMargin(int mLineMargin) {
        this.mLineMargin = mLineMargin;
    }

    public void setTextAroundMargin(int textMargin) {
        this.mTextAround = textMargin;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    public void setTopAndBottomMargin(int mTopAndBottomMargin) {
        this.mTopAndBottomMargin = mTopAndBottomMargin;
    }

    public void setSeechIndex(int seechIndex) {
        this.mSeechIndex = seechIndex;
    }

    public void setSpeechModel(boolean mSpeechModel) {
        this.mSpeechModel = mSpeechModel;
    }

    public void setRemarkText(String mRemarkText) {
        LogUtil.error("sssssssss", " mRemarkText = " + mRemarkText);
        if (mRemarkText != null) {
            mRemarkText = mRemarkText.replace("<", "<font color=\"#7b2f2f\" " +
                    "style=\"background-color:#7b2f2f\">&lt;");
            mRemarkText = mRemarkText.replace(">", "&gt;</font>");
            this.mRemarkText = mRemarkText.trim().replace("　", "");
        } else {
            this.mRemarkText = mRemarkText;
        }

        notifyDataSetChanged();
    }

    public boolean isShengJing() {
        return isShengJing;
    }

    public ChapterReadAdapter setShengJing(boolean shengJing) {
        isShengJing = shengJing;
        return this;
    }

    private boolean isShengJing;

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        boolean isTitle = false;
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_chapter_read_item_sj, null);
            viewHolder.convertView = convertView;
            viewHolder.tvName = convertView.findViewById(R.id.tv_title);
            viewHolder.tvHide = convertView.findViewById(R.id.tv_hide);
            viewHolder.tvItem = convertView.findViewById(R.id.tv_item);
            viewHolder.tvVersion = convertView.findViewById(R.id.tv_version);
            viewHolder.tvItem.setVisibility(View.VISIBLE);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String content = getItem(position).replace("　", "");
        if (content.contains("大卫回耶路撒冷")) {
            content = "<h3>大卫回耶路撒冷</h3>";
        }
        //圣经增加数字下标
        String head = CharUtils.match("[\\u4e00-\\u9fa5]{1,2}\\d+:\\d+", content);
        // PERF: isLuoJiShengJing 内部做正则匹配，原来在同一个 getView 里被调用两次
        // 改为只计算一次，结果复用
        boolean isLuoJi = isLuoJiShengJing(content);
        boolean isSJmode = isShengJing || isLuoJi;
        if (isSJmode) {
            viewHolder.tvItem.setVisibility(View.VISIBLE);
            viewHolder.tvVersion.setVisibility(View.VISIBLE);
            if (isLuoJi) {
                int startIndex = head.lastIndexOf(":") + 1;
                viewHolder.tvItem.setText(head.substring(startIndex));
            } else {
                //老板说的不显示
                viewHolder.tvItem.setText("");
                if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) {
                    viewHolder.tvItem.setVisibility(View.GONE);
                }
            }
        } else {
            viewHolder.tvItem.setVisibility(View.GONE);
            viewHolder.tvVersion.setVisibility(View.GONE);
        }

        //移除出处
        if (content.indexOf("〖") > 0) {
            content = content.substring(content.indexOf("〖"), content.length());
        }
        //是否是搜索内容列表的内容
        boolean isSearchContent = false;
        if (!TextUtils.isEmpty(mChapterContent) && mChapterContent.equals(content)) {
            isSearchContent = true;
        }
        if (!TextUtils.isEmpty(mChapterContent) && !isSearchContent && content.indexOf("〖") > 0 && content.contains(mChapterContent)) {
            //有一些前面带有出处
            isSearchContent = true;
        }
        if (!isSearchContent && !TextUtils.isEmpty(mChapterContent) && content.length() - mChapterContent.length() < 2 && content.contains(mChapterContent)) {
            //繁体情况下content会比mChapterContent的length多1
            isSearchContent = true;
        }

        String title = null;
        if (content.contains("<b>")) {
            isTitle = true;
            title = content.substring(content.indexOf("<b>") + "<b>".length(), content.indexOf("</b>"));
        } else if (content.contains("<h3>")) {
            isTitle = true;
            int index = content.indexOf("</h3>");
            if (index != -1) {
                title = content.substring(content.indexOf("<h3>") + "<h3>".length(), index != -1 ? index : 0);
            }
        } else if (content.contains("<h6>")) {
            isTitle = true;
            title = content.substring(content.indexOf("<h6>") + "<h6>".length(), content.indexOf("</h6>"));
        }
        String[] tags = {"a", "b", "font", "h", ""};
        if (content.contains("<h3>")) {
            content = content.substring(content.indexOf("<h3>"), content.length());
        }
        content = StringUtil.tagsReplace(content, tags);
        String verTag = "";
        int versionColor = mTextColor;
        if (content != null) {
            if (content.contains("〖和合本〗") && content.contains("〖/和合本〗")) {
                verTag = "和合本";
                versionColor = mTextColor;
            } else if (content.contains("〖吕振中〗") && content.contains("〖/吕振中〗")) {
                verTag = "吕振中";
                versionColor = Color.parseColor("#3f51b5");
            } else if (content.contains("〖思高本〗") && content.contains("〖/思高本〗")) {
                verTag = "思高本";
                versionColor = Color.parseColor("#666666");
            } else if (content.contains("〖现代本〗") && content.contains("〖/现代本〗")) {
                verTag = "现代本";
                versionColor = Color.parseColor("#009688");
            } else if (content.contains("〖新译本〗") && content.contains("〖/新译本〗")) {
                verTag = "新译本";
                versionColor = Color.parseColor("#9c27b0");
            } else if (content.contains("〖当代版〗") && content.contains("〖/当代版〗")) {
                verTag = "当代版";
                versionColor = Color.parseColor("#ef9a9a");
            } else if (content.contains("〖KJV〗") && content.contains("〖/KJV〗")) {
                verTag = "KJV";
                versionColor = Color.parseColor("#e53935");
            } else if (content.contains("〖NIV〗") && content.contains("〖/NIV〗")) {
                verTag = "NIV";
                versionColor = Color.parseColor("#4caf50");
            } else if (content.contains("〖BBE〗") && content.contains("〖/BBE〗")) {
                verTag = "BBE";
                versionColor = Color.parseColor("#673ab7");
            } else if (content.contains("〖ASV〗") && content.contains("〖/ASV〗")) {
                verTag = "ASV";
                versionColor = Color.parseColor("#5677FC");
            } else if (content.contains("〖中文〗") && content.contains("〖/中文〗")) {
                verTag = "中文";
                versionColor = mTextColor;
            } else if (content.contains("〖英文〗") && content.contains("〖/英文〗")) {
                verTag = "英文";
                versionColor = Color.parseColor("#3f51b5");
            } else if (content.contains("〖呂振中") && content.contains("〖/呂振中〗")) {
                verTag = "呂振中";
                versionColor = Color.parseColor("#3f51b5");
            } else if (content.contains("〖現代本〗") && content.contains("〖/現代本〗")) {
                verTag = "現代本";
                versionColor = Color.parseColor("#009688");
            } else if (content.contains("〖新譯本〗") && content.contains("〖/新譯本〗")) {
                verTag = "新譯本";
                versionColor = Color.parseColor("#9c27b0");
            } else if (content.contains("〖當代版〗") && content.contains("〖/當代版〗")) {
                verTag = "當代版";
                versionColor = Color.parseColor("#ef9a9a");
            } else {
                versionColor = mTextColor;
            }
        }
        if (!TextUtils.isEmpty(verTag)) {
            viewHolder.tvVersion.setVisibility(View.VISIBLE);
            viewHolder.tvVersion.setText(verTag);
            viewHolder.tvVersion.setTextColor(versionColor);
            if ("中文".equals(verTag) || "英文".equals(verTag)) {
                viewHolder.tvVersion.setVisibility(View.GONE);
            }
        } else {
            viewHolder.tvVersion.setText("");
        }

        content = content.replaceAll("〖.*?〗", "");
        String htmlContent = null;
        if ((position == mTipsPostion && !TextUtils.isEmpty(mTipsKeyword)) || (!TextUtils.isEmpty(mTipsKeyword) && !TextUtils.isEmpty(mTipsContent) && getItem(position).replace("　", "").contains(mTipsContent.replace("　", "")))) {
            htmlContent = SearchTextUtil.textMacth(content, mTipsKeyword);
        }

        if (!TextUtils.isEmpty(mRemarkText)) {
            viewHolder.tvHide.setText(Html.fromHtml(mRemarkText));
        }

        if (!TextUtils.isEmpty(htmlContent)) {
            content = htmlContent;
            if (mSpeechModel && mSeechIndex == position && !TextUtils.isEmpty(mRemarkText)) {
                content = content.replace(mRemarkText, "<font color=\"#0099FF\" style=\"background-color:#7b2f2f\">" + mRemarkText + "</font>");
            }
            if (!TextUtils.isEmpty(head)) {//如果有head
                char c = head.charAt(0);
                if (content.indexOf(c) == 0) {//说明是第一个
                    if (!isSJmode) {
                        content = content.replace(head, "");//去掉head
                        content = "<font color=\"#989898\" style=\"background-color:#7b2f2f\"><small>" + head + "</small></font>" + "&ensp;" + content;//重新组合head
                        viewHolder.tvName.setText(Html.fromHtml(content));
                    } else {
                        defaultColor = TextUtil.getVersionColor(content, mTextColor);
                        viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                    }
                } else {
                    String tips[] = mTipsKeyword.split(" ");
                    for (String string : tips) {
                        content = content.replace("<font color='#ff0000'>" + string + "</font>", string);
                    }
                    String squre = StringUtil.match("(?=<font.*?>&lt;)([\\s\\S]*)(?=<\\/font>)", content);
                    String paren = StringUtil.match("(?=<font.*?>\\（)(.*?)(?=<\\/font>)", content);
                    String b = StringUtil.match("(?=<b.*?>)(.*?)(?=<\\/b>)", content);
                    if (!isSJmode) {
                        content = TextUtil.parentheseSetHtmlColor(content);
                        viewHolder.tvName.setText(Html.fromHtml(content));
                    } else {
                        defaultColor = TextUtil.getVersionColor(content, mTextColor);
                        viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                    }
                    if (!isSJmode) {
                        SpannableStringBuilder span = new SpannableStringBuilder("缩进" + viewHolder.tvName.getText());
                        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        if (squre != null && squre.length() != 0) {
                            squre = squre.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                    .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "（").replaceAll("\\)", "）");
                            span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)), 2 + viewHolder.tvName.getText().toString().indexOf(squre),
                                    2 + viewHolder.tvName.getText().toString().indexOf(squre) + squre.length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        if (paren != null && paren.length() != 0) {
                            paren = paren.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                    .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "（").replaceAll("\\)", "）");
                            span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)), 2 + viewHolder.tvName.getText().toString().indexOf(paren),
                                    2 + viewHolder.tvName.getText().toString().indexOf(paren) + paren.length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        if (b != null && b.length() != 0) {
                            b = b.replaceAll("<b.*?>", "").replaceAll("<\\/b>", "");
                            span.setSpan(new StyleSpan(BOLD), 2 + viewHolder.tvName.getText().toString().indexOf(b),
                                    2 + viewHolder.tvName.getText().toString().indexOf(b) + b.length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        for (String string : tips) {
                            for (int i = -1; i <= 2 + viewHolder.tvName.getText().toString().lastIndexOf(string); ++i) {
                                i = 2 + viewHolder.tvName.getText().toString().indexOf(string, i);
                                span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.tip_text)), i,
                                        i + string.length(),
                                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                        }
                        viewHolder.tvName.setText(span);
                    }
                }


            } else {
                if (content.contains("&lt;h3&gt;") && content.contains("&lt;/h3&gt;")) {//小标题
                    content = content.replaceAll("&lt;h3&gt;", "").replaceAll("&lt;/h3&gt;", "");
                    viewHolder.tvName.setGravity(Gravity.CENTER);
                    TextPaint tp = viewHolder.tvName.getPaint();
                    tp.setFakeBoldText(true);
                }
                String squre = StringUtil.match("(?=<font.*?>&lt;)([\\s\\S]*)(?=<\\/font>)", content);
                String paren = StringUtil.match("(?=<font.*?>\\（)(.*?)(?=<\\/font>)", content);

                if (!isSJmode) {
                    content = TextUtil.parentheseSetHtmlColor(content);
                    viewHolder.tvName.setText(Html.fromHtml(content));
                } else {
                    defaultColor = TextUtil.getVersionColor(content, mTextColor);
                    viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                }
                if (!isSJmode) {
                    SpannableStringBuilder span = new SpannableStringBuilder("缩进" + viewHolder.tvName.getText());
                    span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    if (squre != null && squre.length() != 0) {
                        squre = squre.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "（")
                                .replaceAll("\\)", "）").replaceAll("</b>", "");
                        span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)), 2 + viewHolder.tvName.getText().toString().indexOf(squre),
                                2 + viewHolder.tvName.getText().toString().indexOf(squre) + squre.length(),
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                    if (paren != null && paren.length() != 0) {
                        paren = paren.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "（").replaceAll("\\)", "）");
                        span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)), 2 + viewHolder.tvName.getText().toString().indexOf(paren),
                                2 + viewHolder.tvName.getText().toString().indexOf(paren) + paren.length(),
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                    viewHolder.tvName.setText(span);
                }
            }
        } else {
            if (mSpeechModel && mSeechIndex == position && !TextUtils.isEmpty(mRemarkText)) {
                content = content.replace(mRemarkText, "<font color=\"#000000\" style=\"background-color:#7b2f2f\">" + mRemarkText + "</font>");
                if (!TextUtils.isEmpty(head)) {//如果有head
                    char c = head.charAt(0);
                    if (content.indexOf(c) == 0) {//说明是第一个
                        if (!isSJmode) {
                            content = content.replace(head, "");//去掉head
                            content = "<font color=\"#989898\" style=\"background-color:#7b2f2f\"><small>" +
                                    head + "</small></font>" + "&ensp;" + content;//重新组合head
                            viewHolder.tvName.setText(Html.fromHtml(content));
                        } else {
                            content = content.replace(head, "");//去掉headr
                            defaultColor = TextUtil.getVersionColor(content, mTextColor);
                            viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                            //添加背景颜色
                            setBackground(isSJmode, viewHolder);
                        }
                        viewHolder.tvName.setGravity(Gravity.START);
                        TextPaint tp = viewHolder.tvName.getPaint();
                        tp.setFakeBoldText(false);
                    } else {
                        if (!isSJmode) {
                            content = TextUtil.parentheseSetHtmlColor(content);
                            viewHolder.tvName.setText(Html.fromHtml(content));
                        } else {
                            defaultColor = TextUtil.getVersionColor(content, mTextColor);
                            viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                        }
                        viewHolder.tvName.setGravity(Gravity.START);
                        TextPaint tp = viewHolder.tvName.getPaint();
                        tp.setFakeBoldText(false);
                        if (!isSJmode) {
                            SpannableStringBuilder span = new SpannableStringBuilder("缩进" + viewHolder.tvName.getText());
                            mRemarkText = mRemarkText.replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                    .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "（").replaceAll("\\)", "）");
                            viewHolder.tvHide.setText(Html.fromHtml(mRemarkText));
                            String b = StringUtil.match("<.*?>", viewHolder.tvName.getText().toString());
                            span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            span.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.yellow_fff59d)), 2, viewHolder.tvName.getText().toString().length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            if (b != null && b.length() != 0) {
                                span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)), 2 + viewHolder.tvName.getText().toString().indexOf(b),
                                        2 + viewHolder.tvName.getText().toString().indexOf(b) + b.length(),
                                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                            viewHolder.tvName.setGravity(Gravity.START);
                            viewHolder.tvName.setText(span);
                        } else {
                            //添加背景颜色
                            setBackground(isSJmode, viewHolder);
                        }
                    }
                } else {

                    if (content.contains("&lt;h3&gt;") && content.contains("&lt;/h3&gt;")) {//小标题
                        content = content.replaceAll("&lt;h3&gt;", "").replaceAll("&lt;/h3&gt;", "");
                        viewHolder.tvName.setGravity(Gravity.CENTER);
                        TextPaint tp = viewHolder.tvName.getPaint();
                        tp.setFakeBoldText(true);
                    }
                    if (!isSJmode) {
                        content = TextUtil.parentheseSetHtmlColor(content);
                        viewHolder.tvName.setText(Html.fromHtml(content));
                    } else {
                        defaultColor = TextUtil.getVersionColor(content, mTextColor);
                        viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                    }
                    String b = StringUtil.match("(?=<b.*?>)(.*?)(?=<\\/b>)", content);
                    if (!isSJmode) {
                        SpannableStringBuilder span = new SpannableStringBuilder("缩进" + viewHolder.tvName.getText());
                        mRemarkText = mRemarkText.replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "（").replaceAll("\\)", "）")
                                .replaceAll("<b>", "").replace("</b>", "");
                        viewHolder.tvHide.setText(Html.fromHtml(mRemarkText));
                        String remark = StringUtil.match("<.*?>", viewHolder.tvName.getText().toString());
                        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        span.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.yellow_fff59d)), 2 + viewHolder.tvName.getText().toString().indexOf(viewHolder.tvHide.getText().toString()), 2 + viewHolder.tvName.getText().toString().indexOf(viewHolder.tvHide.getText().toString()) + viewHolder.tvHide.getText().toString().length(),
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        if (remark != null && remark.length() != 0) {
                            span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)), 2 + viewHolder.tvName.getText().toString().indexOf(remark),
                                    2 + viewHolder.tvName.getText().toString().indexOf(remark) + remark.length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        if (b != null && b.length() != 0) {
                            b = b.replaceAll("<b.*?>", "").replaceAll("<\\/b>", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "");

                            span.setSpan(new StyleSpan(BOLD), 2 + viewHolder.tvName.getText().toString().indexOf(b),
                                    2 + viewHolder.tvName.getText().toString().indexOf(b) + b.length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        viewHolder.tvName.setText(span);
                    } else {
                        //添加背景颜色
                        setBackground(isSJmode, viewHolder);
                    }
                }

            } else {

                if (!TextUtils.isEmpty(head)) {//如果有head
                    char c = head.charAt(0);
                    if (content.indexOf(c) == 0) {//说明是第一个
                        content = content.replace(head, "");//去掉head
                        if (!isSJmode) {
                            content = "<font color=\"#989898\" style=\"background-color:#7b2f2f\"><small>" + head + "</small></font>" + "&ensp;" + content;//重新组合head
                            viewHolder.tvName.setText(Html.fromHtml(content));

                        } else {
                            defaultColor = TextUtil.getVersionColor(content, mTextColor);
                            viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                        }
                        viewHolder.tvName.setGravity(Gravity.START);
                        TextPaint tp = viewHolder.tvName.getPaint();
                        tp.setFakeBoldText(false);
                    } else {
                        String squre = StringUtil.match("(?=<font.*?>&lt;)([\\s\\S]*)(?=<\\/font>)", content);
                        String paren = StringUtil.match("(?=<font.*?>\\（)(.*?)(?=<\\/font>)", content);
                        String b = StringUtil.match("(?=<b.*?>)(.*?)(?=<\\/b>)", content);
                        if (!isSJmode) {
                            content = TextUtil.parentheseSetHtmlColor(content);
                            viewHolder.tvName.setText(Html.fromHtml(content));
                        } else {
                            defaultColor = TextUtil.getVersionColor(content, mTextColor);
                            viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                        }
                        viewHolder.tvName.setGravity(Gravity.START);
                        TextPaint tp = viewHolder.tvName.getPaint();
                        tp.setFakeBoldText(false);
                        if (!isSJmode) {
                            SpannableStringBuilder span = new SpannableStringBuilder("缩进" + viewHolder.tvName.getText());
                            span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            if (squre != null && squre.length() != 0) {
                                squre = squre.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                        .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "（").replaceAll("\\)", "）");
                                span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)), 2 + viewHolder.tvName.getText().toString().indexOf(squre),
                                        2 + viewHolder.tvName.getText().toString().indexOf(squre) + squre.length(),
                                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                            if (paren != null && paren.length() != 0) {
                                paren = paren.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                        .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "（").replaceAll("\\)", "）");
                                span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)), 2 + viewHolder.tvName.getText().toString().indexOf(paren),
                                        2 + viewHolder.tvName.getText().toString().indexOf(paren) + paren.length(),
                                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                            if (b != null && b.length() != 0) {
                                b = b.replaceAll("<b.*?>", "").replaceAll("<\\/b>", "");
                                span.setSpan(new StyleSpan(BOLD), 2 + viewHolder.tvName.getText().toString().indexOf(b),
                                        2 + viewHolder.tvName.getText().toString().indexOf(b) + b.length(),
                                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                            viewHolder.tvName.setText(span);
                        }
                    }
                } else {
                    if (content.contains("&lt;h3&gt;") && content.contains("&lt;/h3&gt;")) {
                        content = content.replaceAll("&lt;h3&gt;", "").replaceAll("&lt;/h3&gt;", "");
                        viewHolder.tvName.setGravity(Gravity.CENTER);
                        TextPaint tp = viewHolder.tvName.getPaint();
                        tp.setFakeBoldText(true);
                        viewHolder.tvName.setText(content);
                    }
                    String squre = StringUtil.match("(?=<font.*?>&lt;)([\\s\\S]*)(?=<\\/font>)", content);
                    String paren = StringUtil.match("(?=<font.*?>\\（)(.*?)(?=<\\/font>)", content);
                    String b = StringUtil.match("(?=<b.*?>)(.*?)(?=<\\/b>)", content);
                    if (!isSJmode) {
                        content = TextUtil.parentheseSetHtmlColor(content);
                        viewHolder.tvName.setText(Html.fromHtml(content));
                    } else {
                        if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) {
                            defaultColor = TextUtil.getVersionColor(content, mTextColor);
                            viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                        } else {
                            defaultColor = TextUtil.getVersionColor(content, mTextColor);
                            viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                        }
                    }
                    if (!isSJmode) {
                        SpannableStringBuilder span = new SpannableStringBuilder("缩进" + viewHolder.tvName.getText());
                        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        if (squre != null && squre.length() != 0) {
                            squre = squre.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                    .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "（").replaceAll("\\)", "）");
                            span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)), 2 + viewHolder.tvName.getText().toString().indexOf(squre),
                                    2 + viewHolder.tvName.getText().toString().indexOf(squre) + squre.length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        if (paren != null && paren.length() != 0) {
                            paren = paren.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                    .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "（").replaceAll("\\)", "）");
                            span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)), 2 + viewHolder.tvName.getText().toString().indexOf(paren),
                                    2 + viewHolder.tvName.getText().toString().indexOf(paren) + paren.length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        if (b != null && b.length() != 0) {
                            b = b.replaceAll("<b.*?>", "").replaceAll("<\\/b>", "");
                            span.setSpan(new StyleSpan(BOLD), 2 + viewHolder.tvName.getText().toString().indexOf(b),
                                    2 + viewHolder.tvName.getText().toString().indexOf(b) + b.length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        viewHolder.tvName.setGravity(Gravity.START);
                        viewHolder.tvName.setText(span);
                    }
                }

            }
        }
        viewHolder.tvName.setTextSize(DisplayUtil.pxTosp(mContext, 32));

        if (!isSJmode) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                viewHolder.tvName.setPadding(mTextAround * 3, mTextMagin * 3, mTextAround * 3, mTextMagin * 3);
            } else {
                viewHolder.tvName.setPadding(mTextAround * 3, mTextMagin * 3, mTextAround * 3, mTextMagin * 3);
            }
        }
        viewHolder.tvName.setLineSpacing(mLineMargin * 3, 1);

        viewHolder.tvName.setTextColor(mTextColor);
        if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) {
            int newDis1 = new Double(mTextAround * 2.5).intValue();
            int newDis2 = new Double(mTextAround * 1.2).intValue();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                viewHolder.tvName.setPadding(newDis1, mTextMagin * 3, newDis2, mTextMagin * 3);
            } else {
                viewHolder.tvName.setPadding(newDis1, mTextMagin * 3, newDis2, mTextMagin * 3);
            }
            if (HuDongApplication.mVersions_HZ.size() == 2) {
                if (position % 2 == 1) {
                    viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.indigo));
                }
            } else if (HuDongApplication.mVersions_HZ.size() == 1 && HuDongApplication.mVersions_HZ.contains("英文")) {
                viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.indigo));
            }
        }

        // 字体大小
        viewHolder.tvName.setTextSize(mTextSize);
        if (mSearchType == 2) {
            //在标题搜索的情况下并且是标题的情况下才设置字体样式
            if (isTitle && !TextUtils.isEmpty(title)) {
                matchOnlyTitle(viewHolder.tvName, title, versionColor);
            }
        } else {
            match(viewHolder.tvName, isSearchContent, versionColor);//huaizhu有缩进的字情况
        }

        // 选择模式
        setBackgroudColor(viewHolder, position);
        return convertView;
    }

    private void setBackground(boolean isSJmode, ViewHolder viewHolder) {
        if (isSJmode) {
            if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) {
                String tvname = viewHolder.tvName.getText().toString();
                String tvhide = viewHolder.tvHide.getText().toString();
                if (tvname.indexOf(tvhide) != -1) {
                    SpannableStringBuilder span = new SpannableStringBuilder(viewHolder.tvName.getText());
                    span.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.yellow_fff59d)), tvname.indexOf(tvhide), tvname.indexOf(tvhide) + tvhide.length(),
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    viewHolder.tvName.setText(span);
                }
            } else {
                String tvname = viewHolder.tvName.getText().toString();
                String tvhide = viewHolder.tvHide.getText().toString();

                tvhide = tvhide.replaceAll("</h3>", "").replaceAll("<h3>", "");
                if (tvname.indexOf(tvhide) != -1) {
                    SpannableStringBuilder span = new SpannableStringBuilder(viewHolder.tvName.getText());
                    span.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.yellow_fff59d)), tvname.indexOf(tvhide), tvname.indexOf(tvhide) + tvhide.length(),
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    viewHolder.tvName.setText(span);
                }
            }
        }
    }

    public boolean isLuoJiShengJing(String content) {
        if (!TextUtils.isEmpty(content) && content.length() > 12) {
            String preTxt = content.substring(0, 12);
            String shengJingTxt = CharUtils.match("[\\u4e00-\\u9fa5]{1,2}\\d+:\\d+", preTxt);
            return !TextUtils.isEmpty(shengJingTxt) && content.replace(" ", "").startsWith(shengJingTxt);
        }
        return false;
    }

    @Override
    protected Integer getItemCheckRecordKey(String s, int position) {
        return position;
    }

    @Override
    protected void onSelectModelChanged(boolean selectModel) {
    }

    public int getBackgroudColor() {
        return mBackgroudColor;
    }

    public void setTipsContent(String tipsContent) {
        this.mTipsContent = tipsContent;
    }

    public void setTextModel(int textModel) {
        this.mTextModel = textModel;
    }

    private class ViewHolder {
        View convertView;
        TextView tvName;
        TextView tvHide;
        TextView tvItem;
        TextView tvVersion;
    }

    public void updateView(AdapterView<?> adapterView, int itemIndex) {
        int firstVisiblePosition = adapterView.getFirstVisiblePosition();
        View view = adapterView.getChildAt(itemIndex - firstVisiblePosition);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder != null) {
            setBackgroudColor(viewHolder, itemIndex);
        }
    }

    /**
     * 根据模式设置背景色
     *
     * @param viewHolder
     * @param itemIndex
     */
    private void setBackgroudColor(ViewHolder viewHolder, int itemIndex) {
        if (isSelectModel() && isCheckedKey(itemIndex)) {
            viewHolder.convertView.setBackgroundColor(mBackgroudColor);
        } else {
            viewHolder.convertView.setBackgroundColor(mContext.getResources().getColor(R.color
                    .transparent));
        }
    }

    /**
     * 反选
     */
    public void uncheck() {
        if (mList != null) {
            int size = mList.size();
            for (int i = 0; i < size; i++) {
                String t = mList.get(i);
                if (t != null) {
                    if (isCheckedKey(i)) {
                        setChecked(i, false);
                    } else {
                        setChecked(i, true);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void match(TextView textView, boolean isSearchContent, int versionColor) {
        if (!isFontFace) {
            return;
        }
        SpannableString sStr = null;
        if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) {
            if (textView.getText().toString() != null && textView.getText().toString().indexOf("缩进") != 0) {
                sStr = new SpannableString("缩进" + textView.getText());
            } else {
                if (!"缩进".equals(textView.getText().toString().substring(0, 2))) {
                    sStr = new SpannableString("缩进" + textView.getText());
                } else {
                    sStr = new SpannableString(textView.getText());
                }
            }
            sStr.setSpan(new ForegroundColorSpan(versionColor), 0, sStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            sStr = new SpannableString(textView.getText());
            sStr.setSpan(new ForegroundColorSpan(versionColor), 0, sStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (sStr.toString().startsWith("缩进")) {
            sStr.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        String p = "\\“([^\\”]*)\\”";
        Pattern P = Pattern.compile(p);
        Matcher matcher = P.matcher(sStr);
        while (matcher.find()) {
            int i = 0;
            sStr.setSpan(new ForegroundColorSpan(textColor == -1 ? defaultColor : textColor), matcher.start() + i, matcher.end() + i, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            sStr.setSpan(FontsUtil.getInstance(textView.getContext()).getMyNumTypefaceSpan(), matcher.start() + i, matcher.end() + i, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        if (mSearchType == 4 && !isSearchContent) {
            //阅读高亮
            if (!TextUtils.isEmpty(mRemarkText)) {
                if (sStr.toString().indexOf(mRemarkText) >= 0) {
                    sStr.setSpan(new BackgroundColorSpan(remarkTextBackgroundColor), sStr.toString().indexOf(mRemarkText), sStr.toString().indexOf(mRemarkText) + mRemarkText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            textView.setText(sStr);
            return;
        }
        //关键字高亮
        if (!TextUtils.isEmpty(mTipsKeyword) && !TextUtils.isEmpty(sStr) && !TextUtils.isEmpty(SearchTextUtil.textMacth(sStr.toString(), mTipsKeyword))) {
            String[] keyWords = mTipsKeyword.split(" ");
            for (String keyWord : keyWords) {
                Pattern P1 = Pattern.compile(keyWord);
                Matcher matcherKeyWord = P1.matcher(sStr);
                while (matcherKeyWord.find()) {
                    if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) {
                        sStr.setSpan(new ForegroundColorSpan(keyWordColor), matcherKeyWord.start(), matcherKeyWord.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        sStr.setSpan(new StyleSpan(BOLD), matcherKeyWord.start(), matcherKeyWord.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        sStr.setSpan(new ForegroundColorSpan(keyWordColor), matcherKeyWord.start(), matcherKeyWord.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        sStr.setSpan(new StyleSpan(BOLD), matcherKeyWord.start(), matcherKeyWord.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }
        //阅读高亮
        if (!TextUtils.isEmpty(mRemarkText)) {
            if (sStr.toString().indexOf(mRemarkText) >= 0) {
                sStr.setSpan(new BackgroundColorSpan(remarkTextBackgroundColor), sStr.toString().indexOf(mRemarkText), sStr.toString().indexOf(mRemarkText) + mRemarkText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        textView.setText(sStr);

    }


    public void matchOnlyTitle(TextView textView, String title, int versionColor) {
        if (!isFontFace) {
            return;
        }
        SpannableString sStr = null;
        String content = textView.getText().toString();

        textView.setText(content);
        if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) {
            if (textView.getText().toString() != null && textView.getText().toString().indexOf("缩进") != 0) {
                sStr = new SpannableString("缩进" + textView.getText());
            } else {
                if (!"缩进".equals(textView.getText().toString().substring(0, 2))) {
                    sStr = new SpannableString("缩进" + textView.getText());
                } else {
                    sStr = new SpannableString(textView.getText());
                }
            }
            sStr.setSpan(new ForegroundColorSpan(versionColor), 0, sStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            sStr = new SpannableString(textView.getText());
            sStr.setSpan(new ForegroundColorSpan(versionColor), 0, sStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (sStr.toString().startsWith("缩进")) {
            sStr.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        String p = "\\“([^\\”]*)\\”";
        Pattern P = Pattern.compile(p);
        Matcher matcher = P.matcher(content);
        while (matcher.find()) {
            sStr.setSpan(new ForegroundColorSpan(textColor == -1 ? defaultColor : textColor), matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            sStr.setSpan(FontsUtil.getInstance(textView.getContext()).getMyNumTypefaceSpan(), matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        sStr.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 2, sStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        if (sStr.toString().lastIndexOf("<") >= 0) {
            sStr.setSpan(new ForegroundColorSpan(Color.parseColor("#7B2F2F")), sStr.toString().lastIndexOf("<"), sStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //关键字高亮
        if (!TextUtils.isEmpty(mTipsKeyword) && !TextUtils.isEmpty(sStr) && !TextUtils.isEmpty(SearchTextUtil.textMacth(sStr.toString(), mTipsKeyword))) {
            String[] keyWords = mTipsKeyword.split(" ");
            for (String keyWord : keyWords) {
                Pattern P1 = Pattern.compile(keyWord);
                Matcher matcherKeyWord = P1.matcher(sStr);
                while (matcherKeyWord.find()) {
                    if (matcherKeyWord.start() >= content.indexOf(title) && matcherKeyWord.end() <= (content.indexOf(title) + title.length())) {
                        if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) {
                            sStr.setSpan(new ForegroundColorSpan(keyWordColor), matcherKeyWord.start(), matcherKeyWord.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else {
                            sStr.setSpan(new ForegroundColorSpan(keyWordColor), matcherKeyWord.start(), matcherKeyWord.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }

            }

            Pattern P1 = Pattern.compile(title);
            Matcher matcherTitle = P1.matcher(sStr);
            while (matcherTitle.find()) {
                sStr.setSpan(new StyleSpan(BOLD), matcherTitle.start(), matcherTitle.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }

        textView.setText(sStr);

    }

    public int getmSeechIndex() {
        return mSeechIndex;
    }

    public void setmSeechIndex(int mSeechIndex) {
        this.mSeechIndex = mSeechIndex;
    }

    public String getmRemarkText() {
        return mRemarkText;
    }

}