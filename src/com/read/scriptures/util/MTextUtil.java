package com.read.scriptures.util;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MTextUtil {

  public final static String pattern = "[\u4e00-\u9fa5]+[0-9]+[:][0-9]+[\u4e00-\u9fa5]+[0-9]+[:][0-9]+"; //创：2创3这种情况
  public final static String pattern1 = "\\<[^\\>]*\\>"; //<h><b>...
  public final static String regx = "(?=\\〖).*?(?<=\\〗)"; //.〖〗
    /**
     * 获取textview一行最大能显示几个字(需要在TextView测量完成之后)
     *
     * @param text     文本内容
     * @param paint    textview.getPaint()
     * @param maxWidth textview.getMaxWidth()/或者是指定的数值,如200dp
     */
    public static int getLineMaxNumber(String text, TextPaint paint, int maxWidth) {
        if (null == text || "".equals(text)) {
            return 0;
        }
        StaticLayout staticLayout = new StaticLayout(text, paint, maxWidth, Layout.Alignment.ALIGN_NORMAL
                , 1.0f, 0, false);
        //获取第一行最后显示的字符下标
        return staticLayout.getLineEnd(0);
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static String changeEletter(String str) {
        if(str == null || "".equals(str)){
            return "";
        }
        String e = str.trim();
        if (str != null && str.length() > 0) {
            char endE = str.charAt(str.length() - 1);
            if ('E' == endE) {
                e = str.replace(str.charAt(str.length() - 1) + "", "");
                if (!e.contains("E")) {
                    return e;
                }
                changeEletter(e);
            }
        }
        return e;
    }

    // 判断一个 TextView 显示的内容是否被缩略了
    public static boolean isTextEllipse(TextView textView) {
        try {
            CharSequence rawText = textView.getText();
            CharSequence displayText = textView.getLayout().getText();
            return !TextUtils.equals(rawText, displayText);
        } catch (Exception ignored) {
            // getLayout() 可能返回 null
            return false;
        }
    }
    /**
     * 获取textview一行最大能显示几个字(需要在TextView测量完成之后)
     *
     * @param text     文本内容
     * @param paint     textview.getPaint()
     * @param maxWidth  textview.getWidth()/或者是指定的数值,如200dp
     * @return
     */
    private int getLineMaxNumber(String text, TextPaint paint, float maxWidth) {
        if (null == text || "".equals(text)) {
            return 0;
        }
        //得到文本内容总体长度
        float textWidth = paint.measureText(text);
        // textWidth
        float width = textWidth / text.length();
        return (int) (maxWidth / width);
    }

    //去掉【】【/】的这种情况 里面的文字和符号 〖/和合本〗
    public  static  String takeOutSymbol(String orignal){

//        String  regx = "\\【.*?】";
        if("".equals(orignal)){
            return "";
        }
//        if(orignal.contains("〖序言】")){
//            return orignal.replace("〖中文】","").replace("〖/中文〗","");
//        }
        return orignal.replaceAll(regx,"");
    }

    //
    public static String match(String regex, String input) {

        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                input = match(regex,input.replaceAll(regex,"").trim());
            }
            return input;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
