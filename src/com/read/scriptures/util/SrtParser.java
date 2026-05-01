package com.read.scriptures.util;

import android.text.TextUtils;

import com.read.scriptures.view.lrc.EditLrcBean;
import com.read.scriptures.view.lrc.Lrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SrtParser {

    /**
     * 解析SRT字幕文件
     * 字幕路径
     */
    public static ArrayList<Lrc> parseSrt(File file) {
        ArrayList<Lrc> srtList = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file.getAbsolutePath());
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                if (!line.equals("")) {
                    sb.append(line).append("@");
                    continue;
                }
                String[] parseStrs = sb.toString().split("@");
                // 该if为了适应一开始就有空行以及其他不符格式的空行情况
                if (parseStrs.length < 3) {
                    sb.delete(0, sb.length());// 清空，否则影响下一个字幕元素的解析</i>
                    continue;
                }
                // 解析开始和结束时间
                String timeTotime = parseStrs[1];
                int begin_hour = Integer.parseInt(timeTotime.substring(0, 2));
                int begin_mintue = Integer.parseInt(timeTotime.substring(3, 5));
                int begin_scend = Integer.parseInt(timeTotime.substring(6, 8));
                int beginTime = (begin_hour * 3600 + begin_mintue * 60 + begin_scend) * 1000;

                Lrc srt = new Lrc();
                srt.setText(parseStrs[2]);
                srt.setTime(beginTime);
                srtList.add(srt);
                sb.delete(0, sb.length());// 清空，否则影响下一个字幕元素的解析
            }
            br.close();
        } catch (Exception e) {
        }
        return srtList;
    }


    /**
     * 解析SRT字幕文件
     * 字幕路径
     * 编辑字幕专用
     */
    public static ArrayList<EditLrcBean> parseEditSrt(String filePath) {
        ArrayList<EditLrcBean> srtList = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                if (!line.equals("")) {
                    sb.append(line).append("@");
                    continue;
                }
                String[] parseStrs = sb.toString().split("@");
                // 该if为了适应一开始就有空行以及其他不符格式的空行情况
                if (parseStrs.length < 3) {
                    sb.delete(0, sb.length());// 清空，否则影响下一个字幕元素的解析</i>
                    continue;
                }
                EditLrcBean srt = new EditLrcBean();
                srt.index = parseStrs[0];
                srt.timeStr = parseStrs[1];
                srt.title = parseStrs[2];

                // 解析开始和结束时间
                String timeTotime = parseStrs[1];
                int begin_hour = Integer.parseInt(timeTotime.substring(0, 2));
                int begin_mintue = Integer.parseInt(timeTotime.substring(3, 5));
                int begin_scend = Integer.parseInt(timeTotime.substring(6, 8));
                int beginTime = (begin_hour * 3600 + begin_mintue * 60 + begin_scend) * 1000;

                srt.time = beginTime;
                srtList.add(srt);
                sb.delete(0, sb.length());// 清空，否则影响下一个字幕元素的解析
            }
            br.close();
        } catch (Exception e) {
        }
        return srtList;
    }

    public static ArrayList<EditLrcBean> parseEditLrc(File file) {
        ArrayList<EditLrcBean> lrcs = new ArrayList<>();
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(new FileInputStream(file), "utf-8");
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                List<EditLrcBean> lrcList = parseLrc(line);
                if (lrcList != null && lrcList.size() != 0) {
                    lrcs.addAll(lrcList);
                }
            }
            return lrcs;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return lrcs;
    }

    private static final String LINE_REGEX = "((\\[\\d{2}:\\d{2}\\.\\d{2}])+)(.*)";
    private static final String TIME_REGEX = "\\[(\\d{2}):(\\d{2})\\.(\\d{2})]";

    private static List<EditLrcBean> parseLrc(String lrcLine) {
        if (lrcLine.trim().isEmpty()) {
            return null;
        }
        List<EditLrcBean> lrcs = new ArrayList<>();
        Matcher matcher = Pattern.compile(LINE_REGEX).matcher(lrcLine);
        if (!matcher.matches()) {
            return null;
        }
        String time = matcher.group(1);
        String content = matcher.group(3);
        if (content == null || TextUtils.isEmpty(content.trim())) {//不添加空白字幕的时间
            return null;
        }
        Matcher timeMatcher = Pattern.compile(TIME_REGEX).matcher(time);
        while (timeMatcher.find()) {
            String min = timeMatcher.group(1);
            String sec = timeMatcher.group(2);
            String mil = timeMatcher.group(3);
            EditLrcBean lrc = new EditLrcBean();
            lrc.timeStr = time;
            lrc.title = content;
            lrc.time = Long.parseLong(min) * 60 * 1000 + Long.parseLong(sec) * 1000 + Long.parseLong(mil) * 10;
            lrcs.add(lrc);
        }
        return lrcs;
    }

}
