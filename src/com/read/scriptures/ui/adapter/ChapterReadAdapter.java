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
    // 阅读字段段间距
    private int mTextMagin = 0;
    // 阅读左右间距
    private int mTextAround = 10;
    // 阅读上下间距
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

    private boolean isFontFace = true;
    private int textColor = -1;
    private int defaultColor = Color.BLACK;
    private int HUAI_ZHU_CHAPTER_HAS_ZW = 0;
    private int keyWordColor = Color.parseColor(SystemConfig.DEFAULT_READ_TEXT_COLOR_KEY_WORD);
    private int remarkTextBackgroundColor = Color.parseColor(SystemConfig.DEFAULT_READ_REMARK_TEXT_BACKGROUND_COLOR_KEY_WORD);
    // 简繁模式
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
            this.mChapterContent = mChapterContent.replace("\u3000", "")
                    .replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "");
            if (this.mChapterContent.indexOf("\u3016") > 0) {
                this.mChapterContent = this.mChapterContent.substring(this.mChapterContent.indexOf("\u3016"), this.mChapterContent.length());
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

    public void setSeechIndex(int seechIndex) {
        this.mSeechIndex = seechIndex;
    }

    public void setSpeechModel(boolean mSpeechModel) {
        this.mSpeechModel = mSpeechModel;
    }

    public void setRemarkText(String mRemarkText) {
        LogUtil.error("sssssssss", " mRemarkText = " + mRemarkText);
        if (mRemarkText != null) {
            mRemarkText = mRemarkText.replace("<", "<font color=\"#7b2f2f\" style=\"background-color:#7b2f2f\">&lt;");
            mRemarkText = mRemarkText.replace(">", "&gt;</font>");
            this.mRemarkText = mRemarkText.trim().replace("\u3000", "");
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

        String content = getItem(position).replace("\u3000", "");
        if (content.contains("\u5927\u536b\u56de\u8036\u8def\u6492\u51b7")) {
            content = "<h3>\u5927\u536b\u56de\u8036\u8def\u6492\u51b7</h3>";
        }
        String head = CharUtils.match("[\\u4e00-\\u9fa5]{1,2}\\d+:\\d+", content);
        boolean isLuoJi = isLuoJiShengJing(content);
        boolean isSJmode = isShengJing || isLuoJi;
        if (isSJmode) {
            viewHolder.tvItem.setVisibility(View.VISIBLE);
            viewHolder.tvVersion.setVisibility(View.VISIBLE);
            if (isLuoJi) {
                int startIndex = head.lastIndexOf(":") + 1;
                viewHolder.tvItem.setText(head.substring(startIndex));
            } else {
                viewHolder.tvItem.setText("");
                if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) {
                    viewHolder.tvItem.setVisibility(View.GONE);
                }
            }
        } else {
            viewHolder.tvItem.setVisibility(View.GONE);
            viewHolder.tvVersion.setVisibility(View.GONE);
        }

        if (content.indexOf("\u3016") > 0) {
            content = content.substring(content.indexOf("\u3016"), content.length());
        }

        // 是否是搜索结果里点击的那一行：只高亮该行，同章其他行不高亮
        boolean isSearchContent = isClickedSearchContent(position);

        String title = null;
        if (content.contains("<b>")) {
            isTitle = true;
            title = content.substring(content.indexOf("<b>") + "<b>".length(), content.indexOf("</b>"));
        } else if (content.contains("<h3>")) {
            isTitle = true;
            int index = content.indexOf("</h3>");
            if (index != -1) {
                title = content.substring(content.indexOf("<h3>") + "<h3>".length(), index);
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
            if (content.contains("\u3016\u548c\u5408\u672c\u3017") && content.contains("\u3016/\u548c\u5408\u672c\u3017")) {
                verTag = "\u548c\u5408\u672c"; versionColor = mTextColor;
            } else if (content.contains("\u3016\u5415\u632f\u4e2d\u3017") && content.contains("\u3016/\u5415\u632f\u4e2d\u3017")) {
                verTag = "\u5415\u632f\u4e2d"; versionColor = Color.parseColor("#3f51b5");
            } else if (content.contains("\u3016\u601d\u9ad8\u672c\u3017") && content.contains("\u3016/\u601d\u9ad8\u672c\u3017")) {
                verTag = "\u601d\u9ad8\u672c"; versionColor = Color.parseColor("#666666");
            } else if (content.contains("\u3016\u73b0\u4ee3\u672c\u3017") && content.contains("\u3016/\u73b0\u4ee3\u672c\u3017")) {
                verTag = "\u73b0\u4ee3\u672c"; versionColor = Color.parseColor("#009688");
            } else if (content.contains("\u3016\u65b0\u8bd1\u672c\u3017") && content.contains("\u3016/\u65b0\u8bd1\u672c\u3017")) {
                verTag = "\u65b0\u8bd1\u672c"; versionColor = Color.parseColor("#9c27b0");
            } else if (content.contains("\u3016\u5f53\u4ee3\u7248\u3017") && content.contains("\u3016/\u5f53\u4ee3\u7248\u3017")) {
                verTag = "\u5f53\u4ee3\u7248"; versionColor = Color.parseColor("#ef9a9a");
            } else if (content.contains("\u3016KJV\u3017") && content.contains("\u3016/KJV\u3017")) {
                verTag = "KJV"; versionColor = Color.parseColor("#e53935");
            } else if (content.contains("\u3016NIV\u3017") && content.contains("\u3016/NIV\u3017")) {
                verTag = "NIV"; versionColor = Color.parseColor("#4caf50");
            } else if (content.contains("\u3016BBE\u3017") && content.contains("\u3016/BBE\u3017")) {
                verTag = "BBE"; versionColor = Color.parseColor("#673ab7");
            } else if (content.contains("\u3016ASV\u3017") && content.contains("\u3016/ASV\u3017")) {
                verTag = "ASV"; versionColor = Color.parseColor("#5677FC");
            } else if (content.contains("\u3016\u4e2d\u6587\u3017") && content.contains("\u3016/\u4e2d\u6587\u3017")) {
                verTag = "\u4e2d\u6587"; versionColor = mTextColor;
            } else if (content.contains("\u3016\u82f1\u6587\u3017") && content.contains("\u3016/\u82f1\u6587\u3017")) {
                verTag = "\u82f1\u6587"; versionColor = Color.parseColor("#3f51b5");
            } else if (content.contains("\u3016\u5442\u632f\u4e2d") && content.contains("\u3016/\u5442\u632f\u4e2d\u3017")) {
                verTag = "\u5442\u632f\u4e2d"; versionColor = Color.parseColor("#3f51b5");
            } else if (content.contains("\u3016\u73fe\u4ee3\u672c\u3017") && content.contains("\u3016/\u73fe\u4ee3\u672c\u3017")) {
                verTag = "\u73fe\u4ee3\u672c"; versionColor = Color.parseColor("#009688");
            } else if (content.contains("\u3016\u65b0\u8b6f\u672c\u3017") && content.contains("\u3016/\u65b0\u8b6f\u672c\u3017")) {
                verTag = "\u65b0\u8b6f\u672c"; versionColor = Color.parseColor("#9c27b0");
            } else if (content.contains("\u3016\u7576\u4ee3\u7248\u3017") && content.contains("\u3016/\u7576\u4ee3\u7248\u3017")) {
                verTag = "\u7576\u4ee3\u7248"; versionColor = Color.parseColor("#ef9a9a");
            } else {
                versionColor = mTextColor;
            }
        }
        if (!TextUtils.isEmpty(verTag)) {
            viewHolder.tvVersion.setVisibility(View.VISIBLE);
            viewHolder.tvVersion.setText(verTag);
            viewHolder.tvVersion.setTextColor(versionColor);
            if ("\u4e2d\u6587".equals(verTag) || "\u82f1\u6587".equals(verTag)) {
                viewHolder.tvVersion.setVisibility(View.GONE);
            }
        } else {
            viewHolder.tvVersion.setText("");
        }

        content = content.replaceAll("\u3016.*?\u3017", "");
        String htmlContent = null;
        if (isSearchContent && !TextUtils.isEmpty(mTipsKeyword)) {
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
            if (!TextUtils.isEmpty(head)) {
                char c = head.charAt(0);
                if (content.indexOf(c) == 0) {
                    if (!isSJmode) {
                        content = content.replace(head, "");
                        content = "<font color=\"#989898\" style=\"background-color:#7b2f2f\"><small>" + head + "</small></font>" + "&ensp;" + content;
                        viewHolder.tvName.setText(Html.fromHtml(content));
                    } else {
                        defaultColor = TextUtil.getVersionColor(content, mTextColor);
                        viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                    }
                } else {
                    String[] tips = mTipsKeyword.split(" ");
                    for (String string : tips) {
                        content = content.replace("<font color='#ff0000'>" + string + "</font>", string);
                    }
                    String squre = StringUtil.match("(?=<font.*?>&lt;)([\\s\\S]*)(?=<\\/font>)", content);
                    String paren = StringUtil.match("(?=<font.*?>\\uff08)(.*?)(?=<\\/font>)", content);
                    String b = StringUtil.match("(?=<b.*?>)(.*?)(?=<\\/b>)", content);
                    if (!isSJmode) {
                        content = TextUtil.parentheseSetHtmlColor(content);
                        viewHolder.tvName.setText(Html.fromHtml(content));
                    } else {
                        defaultColor = TextUtil.getVersionColor(content, mTextColor);
                        viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                    }
                    if (!isSJmode) {
                        SpannableStringBuilder span = new SpannableStringBuilder("\u7f29\u8fdb" + viewHolder.tvName.getText());
                        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        if (squre != null && squre.length() != 0) {
                            squre = squre.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                    .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "\uff08").replaceAll("\\)", "\uff09");
                            span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)),
                                    2 + viewHolder.tvName.getText().toString().indexOf(squre),
                                    2 + viewHolder.tvName.getText().toString().indexOf(squre) + squre.length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        if (paren != null && paren.length() != 0) {
                            paren = paren.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                    .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "\uff08").replaceAll("\\)", "\uff09");
                            span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)),
                                    2 + viewHolder.tvName.getText().toString().indexOf(paren),
                                    2 + viewHolder.tvName.getText().toString().indexOf(paren) + paren.length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        if (b != null && b.length() != 0) {
                            b = b.replaceAll("<b.*?>", "").replaceAll("<\\/b>", "");
                            span.setSpan(new StyleSpan(BOLD),
                                    2 + viewHolder.tvName.getText().toString().indexOf(b),
                                    2 + viewHolder.tvName.getText().toString().indexOf(b) + b.length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        for (String string : tips) {
                            for (int i = -1; i <= 2 + viewHolder.tvName.getText().toString().lastIndexOf(string); ++i) {
                                i = 2 + viewHolder.tvName.getText().toString().indexOf(string, i);
                                span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.tip_text)), i,
                                        i + string.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                        }
                        viewHolder.tvName.setText(span);
                    }
                }
            } else {
                if (content.contains("&lt;h3&gt;") && content.contains("&lt;/h3&gt;")) {
                    content = content.replaceAll("&lt;h3&gt;", "").replaceAll("&lt;/h3&gt;", "");
                    viewHolder.tvName.setGravity(Gravity.CENTER);
                    viewHolder.tvName.getPaint().setFakeBoldText(true);
                }
                String squre = StringUtil.match("(?=<font.*?>&lt;)([\\s\\S]*)(?=<\\/font>)", content);
                String paren = StringUtil.match("(?=<font.*?>\\uff08)(.*?)(?=<\\/font>)", content);
                if (!isSJmode) {
                    content = TextUtil.parentheseSetHtmlColor(content);
                    viewHolder.tvName.setText(Html.fromHtml(content));
                } else {
                    defaultColor = TextUtil.getVersionColor(content, mTextColor);
                    viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                }
                if (!isSJmode) {
                    SpannableStringBuilder span = new SpannableStringBuilder("\u7f29\u8fdb" + viewHolder.tvName.getText());
                    span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    if (squre != null && squre.length() != 0) {
                        squre = squre.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "\uff08").replaceAll("\\)", "\uff09").replaceAll("</b>", "");
                        span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)),
                                2 + viewHolder.tvName.getText().toString().indexOf(squre),
                                2 + viewHolder.tvName.getText().toString().indexOf(squre) + squre.length(),
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                    if (paren != null && paren.length() != 0) {
                        paren = paren.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "\uff08").replaceAll("\\)", "\uff09");
                        span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)),
                                2 + viewHolder.tvName.getText().toString().indexOf(paren),
                                2 + viewHolder.tvName.getText().toString().indexOf(paren) + paren.length(),
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                    viewHolder.tvName.setText(span);
                }
            }
        } else {
            if (mSpeechModel && mSeechIndex == position && !TextUtils.isEmpty(mRemarkText)) {
                content = content.replace(mRemarkText, "<font color=\"#000000\" style=\"background-color:#7b2f2f\">" + mRemarkText + "</font>");
                if (!TextUtils.isEmpty(head)) {
                    char c = head.charAt(0);
                    if (content.indexOf(c) == 0) {
                        if (!isSJmode) {
                            content = content.replace(head, "");
                            content = "<font color=\"#989898\" style=\"background-color:#7b2f2f\"><small>" + head + "</small></font>" + "&ensp;" + content;
                            viewHolder.tvName.setText(Html.fromHtml(content));
                        } else {
                            content = content.replace(head, "");
                            defaultColor = TextUtil.getVersionColor(content, mTextColor);
                            viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                            setBackground(isSJmode, viewHolder);
                        }
                        viewHolder.tvName.setGravity(Gravity.START);
                        viewHolder.tvName.getPaint().setFakeBoldText(false);
                    } else {
                        if (!isSJmode) {
                            content = TextUtil.parentheseSetHtmlColor(content);
                            viewHolder.tvName.setText(Html.fromHtml(content));
                        } else {
                            defaultColor = TextUtil.getVersionColor(content, mTextColor);
                            viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                        }
                        viewHolder.tvName.setGravity(Gravity.START);
                        viewHolder.tvName.getPaint().setFakeBoldText(false);
                        if (!isSJmode) {
                            SpannableStringBuilder span = new SpannableStringBuilder("\u7f29\u8fdb" + viewHolder.tvName.getText());
                            mRemarkText = mRemarkText.replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                    .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "\uff08").replaceAll("\\)", "\uff09");
                            viewHolder.tvHide.setText(Html.fromHtml(mRemarkText));
                            String b = StringUtil.match("<.*?>", viewHolder.tvName.getText().toString());
                            span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            span.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.yellow_fff59d)), 2, viewHolder.tvName.getText().toString().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            if (b != null && b.length() != 0) {
                                span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)),
                                        2 + viewHolder.tvName.getText().toString().indexOf(b),
                                        2 + viewHolder.tvName.getText().toString().indexOf(b) + b.length(),
                                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                            viewHolder.tvName.setGravity(Gravity.START);
                            viewHolder.tvName.setText(span);
                        } else {
                            setBackground(isSJmode, viewHolder);
                        }
                    }
                } else {
                    if (content.contains("&lt;h3&gt;") && content.contains("&lt;/h3&gt;")) {
                        content = content.replaceAll("&lt;h3&gt;", "").replaceAll("&lt;/h3&gt;", "");
                        viewHolder.tvName.setGravity(Gravity.CENTER);
                        viewHolder.tvName.getPaint().setFakeBoldText(true);
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
                        SpannableStringBuilder span = new SpannableStringBuilder("\u7f29\u8fdb" + viewHolder.tvName.getText());
                        mRemarkText = mRemarkText.replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "\uff08").replaceAll("\\)", "\uff09")
                                .replaceAll("<b>", "").replace("</b>", "");
                        viewHolder.tvHide.setText(Html.fromHtml(mRemarkText));
                        String remark = StringUtil.match("<.*?>", viewHolder.tvName.getText().toString());
                        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        int hideIdx = viewHolder.tvName.getText().toString().indexOf(viewHolder.tvHide.getText().toString());
                        span.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.yellow_fff59d)),
                                2 + hideIdx, 2 + hideIdx + viewHolder.tvHide.getText().toString().length(),
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        if (remark != null && remark.length() != 0) {
                            span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)),
                                    2 + viewHolder.tvName.getText().toString().indexOf(remark),
                                    2 + viewHolder.tvName.getText().toString().indexOf(remark) + remark.length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        if (b != null && b.length() != 0) {
                            b = b.replaceAll("<b.*?>", "").replaceAll("<\\/b>", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "");
                            span.setSpan(new StyleSpan(BOLD),
                                    2 + viewHolder.tvName.getText().toString().indexOf(b),
                                    2 + viewHolder.tvName.getText().toString().indexOf(b) + b.length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        viewHolder.tvName.setText(span);
                    } else {
                        setBackground(isSJmode, viewHolder);
                    }
                }
            } else {
                if (!TextUtils.isEmpty(head)) {
                    char c = head.charAt(0);
                    if (content.indexOf(c) == 0) {
                        content = content.replace(head, "");
                        if (!isSJmode) {
                            content = "<font color=\"#989898\" style=\"background-color:#7b2f2f\"><small>" + head + "</small></font>" + "&ensp;" + content;
                            viewHolder.tvName.setText(Html.fromHtml(content));
                        } else {
                            defaultColor = TextUtil.getVersionColor(content, mTextColor);
                            viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                        }
                        viewHolder.tvName.setGravity(Gravity.START);
                        viewHolder.tvName.getPaint().setFakeBoldText(false);
                    } else {
                        String squre = StringUtil.match("(?=<font.*?>&lt;)([\\s\\S]*)(?=<\\/font>)", content);
                        String paren = StringUtil.match("(?=<font.*?>\\uff08)(.*?)(?=<\\/font>)", content);
                        String b = StringUtil.match("(?=<b.*?>)(.*?)(?=<\\/b>)", content);
                        if (!isSJmode) {
                            content = TextUtil.parentheseSetHtmlColor(content);
                            viewHolder.tvName.setText(Html.fromHtml(content));
                        } else {
                            defaultColor = TextUtil.getVersionColor(content, mTextColor);
                            viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                        }
                        viewHolder.tvName.setGravity(Gravity.START);
                        viewHolder.tvName.getPaint().setFakeBoldText(false);
                        if (!isSJmode) {
                            SpannableStringBuilder span = new SpannableStringBuilder("\u7f29\u8fdb" + viewHolder.tvName.getText());
                            span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            if (squre != null && squre.length() != 0) {
                                squre = squre.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                        .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "\uff08").replaceAll("\\)", "\uff09");
                                span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)),
                                        2 + viewHolder.tvName.getText().toString().indexOf(squre),
                                        2 + viewHolder.tvName.getText().toString().indexOf(squre) + squre.length(),
                                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                            if (paren != null && paren.length() != 0) {
                                paren = paren.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                        .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "\uff08").replaceAll("\\)", "\uff09");
                                span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)),
                                        2 + viewHolder.tvName.getText().toString().indexOf(paren),
                                        2 + viewHolder.tvName.getText().toString().indexOf(paren) + paren.length(),
                                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                            if (b != null && b.length() != 0) {
                                b = b.replaceAll("<b.*?>", "").replaceAll("<\\/b>", "");
                                span.setSpan(new StyleSpan(BOLD),
                                        2 + viewHolder.tvName.getText().toString().indexOf(b),
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
                        viewHolder.tvName.getPaint().setFakeBoldText(true);
                        viewHolder.tvName.setText(content);
                    }
                    String squre = StringUtil.match("(?=<font.*?>&lt;)([\\s\\S]*)(?=<\\/font>)", content);
                    String paren = StringUtil.match("(?=<font.*?>\\uff08)(.*?)(?=<\\/font>)", content);
                    String b = StringUtil.match("(?=<b.*?>)(.*?)(?=<\\/b>)", content);
                    if (!isSJmode) {
                        content = TextUtil.parentheseSetHtmlColor(content);
                        viewHolder.tvName.setText(Html.fromHtml(content));
                    } else {
                        defaultColor = TextUtil.getVersionColor(content, mTextColor);
                        viewHolder.tvName.setText(Html.fromHtml(TextUtil.parseSetHtmlColor(content)));
                    }
                    if (!isSJmode) {
                        SpannableStringBuilder span = new SpannableStringBuilder("\u7f29\u8fdb" + viewHolder.tvName.getText());
                        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        if (squre != null && squre.length() != 0) {
                            squre = squre.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                    .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "\uff08").replaceAll("\\)", "\uff09");
                            span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)),
                                    2 + viewHolder.tvName.getText().toString().indexOf(squre),
                                    2 + viewHolder.tvName.getText().toString().indexOf(squre) + squre.length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        if (paren != null && paren.length() != 0) {
                            paren = paren.replace("", "").replaceAll("<font.*?>", "").replaceAll("<\\/font.*?>", "")
                                    .replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("\\(", "\uff08").replaceAll("\\)", "\uff09");
                            span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.remark_text)),
                                    2 + viewHolder.tvName.getText().toString().indexOf(paren),
                                    2 + viewHolder.tvName.getText().toString().indexOf(paren) + paren.length(),
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        if (b != null && b.length() != 0) {
                            b = b.replaceAll("<b.*?>", "").replaceAll("<\\/b>", "");
                            span.setSpan(new StyleSpan(BOLD),
                                    2 + viewHolder.tvName.getText().toString().indexOf(b),
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
            viewHolder.tvName.setPadding(mTextAround * 3, mTextMagin * 3, mTextAround * 3, mTextMagin * 3);
        }
        viewHolder.tvName.setLineSpacing(mLineMargin * 3, 1);
        viewHolder.tvName.setTextColor(mTextColor);

        if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) {
            int newDis1 = (int) (mTextAround * 2.5);
            int newDis2 = (int) (mTextAround * 1.2);
            viewHolder.tvName.setPadding(newDis1, mTextMagin * 3, newDis2, mTextMagin * 3);
            if (HuDongApplication.mVersions_HZ.size() == 2) {
                if (position % 2 == 1) {
                    viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.indigo));
                }
            } else if (HuDongApplication.mVersions_HZ.size() == 1 && HuDongApplication.mVersions_HZ.contains("\u82f1\u6587")) {
                viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.indigo));
            }
        }

        viewHolder.tvName.setTextSize(mTextSize);
        if (mSearchType == 2) {
            if (isTitle && !TextUtils.isEmpty(title) && isSearchContent) {
                matchOnlyTitle(viewHolder.tvName, title, versionColor);
            }
        } else {
            match(viewHolder.tvName, isSearchContent, versionColor);
        }

        setBackgroudColor(viewHolder, position);
        return convertView;
    }

    private void setBackground(boolean isSJmode, ViewHolder viewHolder) {
        if (isSJmode) {
            String tvname = viewHolder.tvName.getText().toString();
            String tvhide = viewHolder.tvHide.getText().toString();
            tvhide = tvhide.replaceAll("</h3>", "").replaceAll("<h3>", "");
            if (tvname.indexOf(tvhide) != -1) {
                SpannableStringBuilder span = new SpannableStringBuilder(viewHolder.tvName.getText());
                span.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.yellow_fff59d)),
                        tvname.indexOf(tvhide), tvname.indexOf(tvhide) + tvhide.length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                viewHolder.tvName.setText(span);
            }
        }
    }

    /**
     * 判断当前行是否是用户从搜索结果点击进来的那一行。
     * 以位置（mTipsPostion）精确匹配，确保同章节中只有该行高亮。
     */
    private boolean isClickedSearchContent(int position) {
        if (TextUtils.isEmpty(mTipsKeyword)) {
            return false;
        }
        return position == mTipsPostion;
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

    private void setBackgroudColor(ViewHolder viewHolder, int itemIndex) {
        if (isSelectModel() && isCheckedKey(itemIndex)) {
            viewHolder.convertView.setBackgroundColor(mBackgroudColor);
        } else {
            viewHolder.convertView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }
    }

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
        SpannableString sStr;
        if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) {
            String txt = textView.getText().toString();
            if (!txt.startsWith("\u7f29\u8fdb")) {
                sStr = new SpannableString("\u7f29\u8fdb" + textView.getText());
            } else {
                sStr = new SpannableString(textView.getText());
            }
        } else {
            sStr = new SpannableString(textView.getText());
        }
        sStr.setSpan(new ForegroundColorSpan(versionColor), 0, sStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (sStr.toString().startsWith("\u7f29\u8fdb")) {
            sStr.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        // 匹配弯引号 \u201c...\u201d 内的文字设置字体样式
        String p = "\u201c([^\u201d]*)\u201d";
        Pattern P = Pattern.compile(p);
        Matcher matcher = P.matcher(sStr);
        while (matcher.find()) {
            sStr.setSpan(new ForegroundColorSpan(textColor == -1 ? defaultColor : textColor),
                    matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            sStr.setSpan(FontsUtil.getInstance(textView.getContext()).getMyNumTypefaceSpan(),
                    matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        // 非搜索命中行：只处理朗读高亮，不做关键词高亮
        if (!isSearchContent) {
            if (!TextUtils.isEmpty(mRemarkText) && sStr.toString().indexOf(mRemarkText) >= 0) {
                sStr.setSpan(new BackgroundColorSpan(remarkTextBackgroundColor),
                        sStr.toString().indexOf(mRemarkText),
                        sStr.toString().indexOf(mRemarkText) + mRemarkText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textView.setText(sStr);
            return;
        }

        // 搜索命中行：关键词高亮
        if (!TextUtils.isEmpty(mTipsKeyword) && !TextUtils.isEmpty(SearchTextUtil.textMacth(sStr.toString(), mTipsKeyword))) {
            String[] keyWords = mTipsKeyword.split(" ");
            for (String keyWord : keyWords) {
                if (TextUtils.isEmpty(keyWord)) continue;
                Pattern P1 = Pattern.compile(Pattern.quote(keyWord));
                Matcher m1 = P1.matcher(sStr);
                while (m1.find()) {
                    sStr.setSpan(new ForegroundColorSpan(keyWordColor), m1.start(), m1.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sStr.setSpan(new StyleSpan(BOLD), m1.start(), m1.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        // 朗读高亮
        if (!TextUtils.isEmpty(mRemarkText) && sStr.toString().indexOf(mRemarkText) >= 0) {
            sStr.setSpan(new BackgroundColorSpan(remarkTextBackgroundColor),
                    sStr.toString().indexOf(mRemarkText),
                    sStr.toString().indexOf(mRemarkText) + mRemarkText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(sStr);
    }

    public void matchOnlyTitle(TextView textView, String title, int versionColor) {
        if (!isFontFace) {
            return;
        }
        String content = textView.getText().toString();
        textView.setText(content);

        SpannableString sStr;
        if (HUAI_ZHU_CHAPTER_HAS_ZW == 1) {
            if (!textView.getText().toString().startsWith("\u7f29\u8fdb")) {
                sStr = new SpannableString("\u7f29\u8fdb" + textView.getText());
            } else {
                sStr = new SpannableString(textView.getText());
            }
        } else {
            sStr = new SpannableString(textView.getText());
        }
        sStr.setSpan(new ForegroundColorSpan(versionColor), 0, sStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (sStr.toString().startsWith("\u7f29\u8fdb")) {
            sStr.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        // 匹配弯引号 \u201c...\u201d 内的文字设置字体样式
        String p = "\u201c([^\u201d]*)\u201d";
        Pattern P = Pattern.compile(p);
        Matcher matcher = P.matcher(content);
        while (matcher.find()) {
            sStr.setSpan(new ForegroundColorSpan(textColor == -1 ? defaultColor : textColor),
                    matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            sStr.setSpan(FontsUtil.getInstance(textView.getContext()).getMyNumTypefaceSpan(),
                    matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        sStr.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 2, sStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        if (sStr.toString().lastIndexOf("<") >= 0) {
            sStr.setSpan(new ForegroundColorSpan(Color.parseColor("#7B2F2F")),
                    sStr.toString().lastIndexOf("<"), sStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // 关键词高亮（标题搜索，只高亮标题范围内的关键词）
        if (!TextUtils.isEmpty(mTipsKeyword) && !TextUtils.isEmpty(SearchTextUtil.textMacth(sStr.toString(), mTipsKeyword))) {
            String[] keyWords = mTipsKeyword.split(" ");
            for (String keyWord : keyWords) {
                if (TextUtils.isEmpty(keyWord)) continue;
                Pattern P1 = Pattern.compile(Pattern.quote(keyWord));
                Matcher m1 = P1.matcher(sStr);
                while (m1.find()) {
                    if (m1.start() >= content.indexOf(title) && m1.end() <= (content.indexOf(title) + title.length())) {
                        sStr.setSpan(new ForegroundColorSpan(keyWordColor), m1.start(), m1.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
            Pattern P1 = Pattern.compile(Pattern.quote(title));
            Matcher mTitle = P1.matcher(sStr);
            while (mTitle.find()) {
                sStr.setSpan(new StyleSpan(BOLD), mTitle.start(), mTitle.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
