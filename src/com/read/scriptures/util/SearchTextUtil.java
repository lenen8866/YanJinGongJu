package com.read.scriptures.util;

import android.content.Context;
import android.text.LoginFilter;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ProgressBar;

import com.read.scriptures.app.HuDongApplication;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.model.Baike;
import com.read.scriptures.model.Bookmark;
import com.read.scriptures.model.Category;
import com.read.scriptures.model.Chapter;
import com.read.scriptures.model.SearchTemp;
import com.read.scriptures.model.Spirituality;
import com.read.scriptures.model.Volume;
import com.read.scriptures.util.ThreadUtil.ProgressThread;
import com.zxl.common.db.sqlite.DbException;
import com.zxl.common.db.sqlite.Selector;
import com.zxl.common.db.sqlite.WhereBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import taobe.tec.jcc.JChineseConvertor;

/**
 * Created by LGM. Datetime: 2015/7/4. Email: lgmshare@mgail.com
 */
public class SearchTextUtil {

    /**
     * 搜索目录关键字
     *
     * @param volumeList
     * @param keyword
     * @return
     */
    public static List<Bookmark> searchVolumeByKeyword(List<Volume> volumeList, String keyword,
                                                       ProgressBar progressBar) {
        int length = volumeList.size();
        progressBar.setMax(length);
        progressBar.setProgress(0);
        List<Bookmark> bookmarkList = new ArrayList<Bookmark>();
        for (int i = 0; i < length; i++) {
            Volume volume = volumeList.get(i);
            String macthLine = textMacth(volume.getVolName().replaceAll("^\\d{1,}-", ""), keyword);
            if (macthLine != null) {
                Bookmark mark = new Bookmark();
                mark.setCategroyId(String.valueOf(volume.getCategoryId()));
                mark.setVolumeId(volume.getId());
                mark.setVolumeName(volume.getVolName().replaceAll("^\\d{1,}-", ""));
                mark.setChapterCount(volume.getChpCount());
                mark.setIndex(0);
                mark.setType(0);
                mark.setContent(macthLine);
                mark.setCategroyId(volume.getCategoryId() + "");
                bookmarkList.add(mark);
            }
            progressBar.setProgress(i);
        }
        return bookmarkList;
    }

    /**
     * 搜索章节关键字
     *
     * @param chapterList
     * @param keyword
     * @return
     */
    public static List<Bookmark> searchChapterByKeyword(List<Chapter> chapterList, String keyword, ProgressBar progressBar) {
        int length = chapterList.size();
        progressBar.setMax(length);
        progressBar.setProgress(0);
        List<Bookmark> bookmarkList = new ArrayList<Bookmark>();
        for (int i = 0; i < length; i++) {
            Chapter chapter = chapterList.get(i);
            String macthLine = textMacth(chapter.getShowName(), keyword);
            if (macthLine != null) {
                Bookmark bookmark = new Bookmark();
                bookmark.setVolumeId(chapter.getVolumeId());
                bookmark.setVolumeName(chapter.getShowVolumeName());
                bookmark.setChapterName(chapter.getShowName());
                bookmark.setChapterIndexId(chapter.getIndexId());
                bookmark.setChapterCount(chapter.getChapterCount());
                bookmark.setIndex(0);
                bookmark.setType(0);
                bookmark.setContent(macthLine);
                bookmark.setCategroyId(chapter.getCategoryId() + "");
                bookmarkList.add(bookmark);
            }
            progressBar.setProgress(i);
        }
        return bookmarkList;
    }

    /**
     * 搜索关键字标题
     *
     * @param keyword
     * @param progressBar
     * @param searchMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Bookmark> searchContentByKeyword(String keyword, ProgressBar progressBar, Map<String, Object> searchMap) {
        List<Volume> volumeList = (List<Volume>) searchMap.get("volumeList");
        Volume volume = (Volume) searchMap.get("volume");
        Category category = (Category) searchMap.get("category");
        int rootId = (Integer) searchMap.get("rootId");
        long start = System.currentTimeMillis();
        boolean searchTitle = (Boolean) searchMap.get("searchTitle");
        int searchLength = volumeList.size();
        progressBar.setMax(searchLength * 100);
        progressBar.setProgress(0);
        List<Bookmark> bookmarkResultList = new ArrayList<>();
        List<SearchTemp> list = null;
        try {
            if (volume != null) {
                list = HuDongApplication.getInstance().getDbUtils().findAll(Selector.from(Chapter.class)
                        .where(WhereBuilder.getInstance("volumeId", "=", volume.getId())).toString(), SearchTemp.class);
            } else if (category != null) {
                list = HuDongApplication.getInstance().getDbUtils()
                        .findAll(Selector.from(Chapter.class)
                                .where(WhereBuilder.getInstance("categoryId", "=", category.getId())).toString(), SearchTemp.class);
            } else {
                list = HuDongApplication.getInstance().getDbUtils().findAll(Selector.from(Chapter.class)
                        .where(WhereBuilder.getInstance("parentId", "=", rootId)).toString(), SearchTemp.class);
            }
            LogUtil.test("搜索关键字搜索耗时：" + (System.currentTimeMillis() - start));
            List<String> have = new ArrayList<String>();
            if (list != null) {
                int progress = 0;
                progressBar.setMax(list.size());
                for (SearchTemp chapter : list) {
                    try {
                        int position = 0;
                        String content = chapter.getContent();
                        if (content == null) {
                            continue;
                        }
                        String[] keywords = keyword.split(" ");
                        boolean isContains = true;
                        for (int i = 0; i < keywords.length; i++) {
                            if (!content.contains(keywords[i])) {
                                isContains = false;
                                break;
                            }
                        }
                        if (isContains) {
                            String volName = "";
                            for (Volume volumetemp : volumeList) {
                                if (chapter.getVolumeId() == volumetemp.getId()) {
                                    volName = volumetemp.getVolName().replaceAll("^\\d{1,}-", "");
                                }
                            }
                            List<String> contentList = new ArrayList<String>(Arrays.asList
                                    (content.split("\n")));
                            int remove = 0;
                            for (int j = 0; j < contentList.size(); j++) {
                                String line = contentList.get(j);
                                if (line.trim().equals("\n") || line.trim().equals("\n\r")
                                        || StringUtil.isEmpty(line.trim())) {
                                    remove++;
                                    continue;
                                }

                                boolean isBreak = false;
                                isContains = false;
                                for (int i = 0; i < keywords.length; i++) {
                                    if (isBreak) {
                                        break;
                                    }
                                    if (line.contains(keywords[i])) {
                                        if (searchTitle) {
                                            if (line.contains("<b")) {
                                                Document document = Jsoup.parse(line);
                                                Elements bs = document.getElementsByTag("b");
                                                for (Element element : bs) {
                                                    if (element.text().contains(keywords[i])) {
                                                        isContains = true;
                                                        isBreak = true;
                                                    }
                                                }
                                            } else if (line.contains("<h3")) {
                                                isContains = true;
                                                isBreak = true;
                                            } else if (line.contains("<h6")) {
                                                isContains = true;
                                                isBreak = true;
                                            }
                                        } else {
                                            if (line.contains("<b")) {
                                                isContains = false;
                                                isBreak = true;
                                            } else {
                                                isContains = true;
                                                isBreak = true;
                                            }
                                        }
                                    }
                                }
                                if (isContains) {
                                    position = j - remove + 1;
                                    Bookmark point = new Bookmark();
                                    point.setVolumeId(chapter.getVolumeId());
                                    point.setVolumeName(volName);
                                    point.setChapterName(chapter.getName());
                                    point.setChapterIndexId(chapter.getIndexId());
                                    point.setChapterCount(chapter.getChapterCount());
                                    point.setIndex(position);
                                    point.setCategroyId(chapter.getCategoryId() + "");
                                    point.setType(0);
                                    String pointContent = textMacth(line, keyword);
                                    if (StringUtil.isEmpty(pointContent)) {
                                        continue;
                                    }
                                    point.setContent(pointContent);
                                    boolean isHave = false;
                                    for (Bookmark bookmark : bookmarkResultList) {
                                        if (bookmark.getContent().equals(pointContent) && bookmark.getChapterIndexId() == chapter.getIndexId()
                                                && bookmark.getVolumeId() == chapter.getVolumeId()) {
                                            isHave = true;
                                        }
                                    }
                                    if (!isHave)
                                        bookmarkResultList.add(point);
                                }
                            }
                            if (have.indexOf(chapter.getName()) >= 0) {
                                continue;
                            }
                            have.add(chapter.getName());
                        }
                    } catch (Exception e) {
                        LogUtil.error("Exception", e);
                    } finally {
                        progress++;
                        progressBar.setProgress(progress);
                    }

                }
            }
        } catch (DbException e) {
            LogUtil.error("DbException", e);
        }
        LogUtil.test("搜索关键字搜索耗时：" + (System.currentTimeMillis() - start));
        return bookmarkResultList;
    }


    /**
     * 搜索关键字内容
     *
     * @param keyword
     * @param progressBar
     * @param searchMap
     * @return
     */
    public static boolean searchByKeywordLoadFinish = false;
    public static int searchLimit = 50;


    @SuppressWarnings("unchecked")
    public static long searchCountByKeyword(String keyword, Map<String, Object> searchMap) {
        Volume volume = (Volume) searchMap.get("volume");
        Category category = (Category) searchMap.get("category");
        int rootId = (Integer) searchMap.get("rootId");

        String instr = getSplitKeywordInstr(keyword, rootId);
        String sql = null;
//        SELECT id,indexId,name,volumeId,content,categoryId,parentId FROM chapter WHERE
        if (volume != null) {
            sql = "SELECT COUNT(1) FROM chapter WHERE volumeId = " + volume.getId() + " and INSTR(content,'" + keyword + "')>0";
        } else if (category != null) {
            sql = "SELECT COUNT(1) FROM chapter WHERE categoryId = " + category.getId() + " and INSTR(content,'" + keyword + "')>0";
        } else {
            sql = "SELECT COUNT(1) FROM chapter WHERE parentId = " + rootId + instr;
        }
        try {
            return HuDongApplication.getInstance().getDbUtils().findAllCount(sql);
        } catch (DbException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @param volumeList   书籍列表
     * @param nodeCategory 选中的分类
     * @param volume       选中的书籍
     * @param keyword      搜索关键字
     * @param cateId       搜索的分类
     * @param offset       偏移量
     * @return
     */
    public static List<Bookmark> searchContent(List<Volume> volumeList, Category nodeCategory, Volume volume, String keyword, int cateId, int offset, int count) {
        String[] keys = keyword.trim().split(" ");
        StringBuilder stringBuilder = new StringBuilder();

        if (volume != null) {
            stringBuilder.append("select * from chapter where categoryId = " + volume.getCategoryId());
        } else if (nodeCategory != null) {
            stringBuilder.append("select * from chapter where categoryId = " + nodeCategory.getId());
        } else {
            stringBuilder.append("select * from chapter where parentId =" + cateId);
        }
        for (String key : keys) {
            if (!TextUtils.isEmpty(key)) {
                stringBuilder.append(" and content like '%" + key + "%' ");
            }
        }
        stringBuilder.append(" and content NOT like '%【%' and content NOT like '%】%'  and  name  NOT like '%【%' and content NOT like '%】%' and  name  NOT like '%jieshao%'  limit " + offset + ",50");
        List<Bookmark> bookmarkResultList = new ArrayList<>();
        List<String> versions = new ArrayList<>();
        if (cateId == 1) {
            //圣经 要筛选和合本、吕振中、思高本等版本
            versions = HuDongApplication.mVersions;
        } else if (cateId == 2) {
            //怀著 要筛选中英文
            versions = HuDongApplication.mVersions_HZ;
        }
        try {
            List<SearchTemp> all = HuDongApplication.getInstance().getDbUtils().find1(stringBuilder.toString(), SearchTemp.class);
            searchByKeywordLoadFinish = all == null || all.size() < searchLimit;
            for (int i = 0; i < all.size(); i++) {
                SearchTemp chapter = all.get(i);
                List<String> contentList = new ArrayList<>(Arrays.asList(chapter.getContent().split("\n")));
                for (int j = 0; j < contentList.size(); j++) {
                    String str = contentList.get(j);
                    boolean isContainsVersion = true;
                    boolean isMatched = false;
                    if (str.contains("〖") || str.contains("〗")) {
                        a:
                        for (String version : versions) {
                            String p = "〖(" + version + ")〗.*?〖(/" + version + ")〗";
                            Pattern P = Pattern.compile(p);
                            Matcher matcher = P.matcher(str);
                            isMatched = matcher.find();
                            if (isMatched) break a;
                        }
                    } else {
                        isContainsVersion = false;
                        isMatched = true;
                    }
                    if (isMatched) {
                        String pointContent = textMacth(str, keyword);
                        if (!StringUtil.isEmpty(pointContent)) {
                            String volName = "";
                            for (Volume volumetemp : volumeList) {
                                if (chapter.getVolumeId() == volumetemp.getId()) {
                                    volName = volumetemp.getVolName().replaceAll("^\\d{1,}-", "");
                                }
                            }
                            Bookmark point = new Bookmark();
                            point.setVolumeId(chapter.getVolumeId());
                            point.setVolumeName(volName);
                            point.setChapterName(chapter.getName());
                            point.setChapterIndexId(chapter.getIndexId());
                            point.setChapterCount(chapter.getChapterCount());
                            point.setCategroyId(chapter.getCategoryId() + "");
                            int size = versions.size();
                            if (size != 0) {
                                if (!isContainsVersion) {
                                    point.setIndex(j + 1);
                                } else {
                                    point.setIndex(j / 2 + 1);
                                }
                            }
                            point.setType(0);
                            point.setContent(pointContent);
                            bookmarkResultList.add(point);
                        }
                    }
                }
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
        return bookmarkResultList;
    }

    @SuppressWarnings("unchecked")
    public static List<Bookmark> searchContentByKeyword(String keyword, ProgressBar progressBar, Map<String, Object> searchMap, long now, int offset) {
        now = System.currentTimeMillis();
        List<Volume> volumeList = (List<Volume>) searchMap.get("volumeList");
        Volume volume = (Volume) searchMap.get("volume");
        Category category = (Category) searchMap.get("category");
        int rootId = (Integer) searchMap.get("rootId");
        long start = System.currentTimeMillis();
        boolean searchTitle = (Boolean) searchMap.get("searchTitle");
        List<Bookmark> bookmarkResultList = new ArrayList<Bookmark>();
        List<SearchTemp> list = null;

        String instr = getSplitKeywordInstr(keyword, rootId);

        try {
            if (volume != null) {
                String sql = "SELECT id,indexId,name,volumeId,content,categoryId,parentId FROM chapter WHERE volumeId = " + volume.getId() + " and INSTR(content,'" + keyword + "')>0 LIMIT " + searchLimit + " OFFSET " + offset;
                list = HuDongApplication.getInstance().getDbUtils().findAll(sql, SearchTemp.class);
                searchByKeywordLoadFinish = list == null || list.size() < searchLimit;
            } else if (category != null) {
                String sql = "SELECT id,indexId,name,volumeId,content,categoryId,parentId FROM chapter WHERE categoryId = " + category.getId() + " and INSTR(content,'" + keyword + "')>0 LIMIT " + searchLimit + " OFFSET " + offset;
                list = HuDongApplication.getInstance().getDbUtils().findAll(sql, SearchTemp.class);
                searchByKeywordLoadFinish = list == null || list.size() < searchLimit;
            } else {
                String sql = "SELECT id,indexId,name,volumeId,content,categoryId,parentId FROM chapter WHERE parentId = " + rootId + instr + " LIMIT " + searchLimit + " OFFSET " + offset;
                list = HuDongApplication.getInstance().getDbUtils().findAll(sql, SearchTemp.class);
                searchByKeywordLoadFinish = list == null || list.size() < searchLimit;
            }
            now = System.currentTimeMillis();
            LogUtil.test("搜索关键字搜索耗时：" + (System.currentTimeMillis() - start));
            List<String> have = new ArrayList<String>();
            if (list != null) {
                int progress = 0;
                progressBar.setMax(list.size());
                for (SearchTemp chapter : list) {
                    try {
                        int position = 0;
                        String content = chapter.getContent();
                        if (content == null) {
                            continue;
                        }
                        String[] keywords = keyword.split(" ");
                        List<String> strList = Arrays.asList(keywords);
                        List arrList = new ArrayList(strList);
                        Iterator<String> stringIterator = arrList.iterator();
                        while (stringIterator.hasNext()) {
                            String string = stringIterator.next();
                            if (TextUtils.isEmpty(string)) {
                                stringIterator.remove();
                            }
                        }

                        keywords = new String[arrList.size()];
                        arrList.toArray(keywords);
                        boolean isContains = true;
                        for (int i = 0; i < keywords.length; i++) {
                            if (!content.contains(keywords[i])) {
                                isContains = false;
                                break;
                            }
                        }
                        if (isContains) {
                            String volName = "";
                            for (Volume volumetemp : volumeList) {
                                if (chapter.getVolumeId() == volumetemp.getId()) {
                                    volName = volumetemp.getVolName().replaceAll("^\\d{1,}-", "");
                                }
                            }
                            List<String> contentList = new ArrayList<>(Arrays.asList(content.split("\n")));
                            //移除未选中的版本对照
                            List<String> removeContents = new ArrayList<>();
                            String location = "";
                            for (int j = 0; j < contentList.size(); j++) {
                                String line = contentList.get(j);
                                if (line.indexOf("〖") >= 0 && !TextUtils.isEmpty(line.substring(0, line.indexOf("〖"))) && !TextUtils.isEmpty(line.substring(0, line.indexOf("〖")).replaceAll("\t\t\t", ""))) {
                                    //截取和合本前的章节出处
                                    location = line.substring(0, line.indexOf("〖"));
                                } else {
                                    if (!TextUtils.isEmpty(location)) {
                                        contentList.set(j, location + line.replace("\t\t\t", ""));
                                    }
                                }
                                boolean isReMove = true;
                                if (line.indexOf("〖") >= 0) {
                                    List<String> versions = new ArrayList<>();
                                    if (rootId == 1) {
                                        //圣经 要筛选和合本、吕振中、思高本等版本
                                        versions = HuDongApplication.mVersions;
                                    } else if (rootId == 2) {
                                        //怀著 要筛选中英文
                                        versions = HuDongApplication.mVersions_HZ;
                                    }
                                    for (String version : versions) {
                                        if (line.contains("〖" + version + "〗") && line.contains("〖/" + version + "〗")) {
                                            isReMove = false;
                                        }
                                    }
                                } else {
                                    isReMove = false;
                                }
                                if (isReMove) {
                                    removeContents.add(contentList.get(j));
                                }
                            }
                            contentList.removeAll(removeContents);

                            int remove = 0;
                            for (int j = 0; j < contentList.size(); j++) {
                                String line = contentList.get(j);
                                if (line.trim().equals("\n") || line.trim().equals("\n\r") || StringUtil.isEmpty(line.trim())) {
                                    remove++;
                                    continue;
                                }

                                boolean isBreak = false;
                                isContains = false;
                                for (int i = 0; i < keywords.length; i++) {
                                    if (isBreak) {
                                        break;
                                    }
                                    if (line.contains(keywords[i])) {
                                        if (searchTitle) {
                                            if (line.contains("<b")) {
                                                Document document = Jsoup.parse(line);
                                                Elements bs = document.getElementsByTag("b");
                                                for (Element element : bs) {
                                                    if (element.text().contains(keywords[i])) {
                                                        isContains = true;
                                                        isBreak = true;
                                                    }
                                                }
                                            }
                                        } else {
                                            if (line.contains("<b")) {
                                                isContains = false;
                                                isBreak = true;
                                            } else {
                                                isContains = true;
                                                isBreak = true;
                                            }
                                        }
                                    }
                                }
                                if (isContains) {
                                    position = j - remove + 1;
                                    Bookmark point = new Bookmark();
                                    point.setVolumeId(chapter.getVolumeId());
                                    point.setVolumeName(volName);
                                    point.setChapterName(chapter.getName());
                                    point.setChapterIndexId(chapter.getIndexId());
                                    point.setChapterCount(chapter.getChapterCount());
                                    point.setCategroyId(chapter.getCategoryId() + "");
                                    point.setIndex(position);
                                    point.setType(0);
                                    String pointContent = textMacth(line, keyword);
                                    if (StringUtil.isEmpty(pointContent)) {
                                        continue;
                                    }
                                    point.setContent(pointContent);
                                    bookmarkResultList.add(point);
                                }
                            }
                            if (have.indexOf(chapter.getName()) >= 0) {
                                continue;
                            }
                            have.add(chapter.getName());
                        }
                    } catch (Exception e) {
                        LogUtil.error("Exception", e);
                    } finally {
                        progress++;
                        progressBar.setProgress(progress);
                    }

                }
                now = System.currentTimeMillis();
            }
        } catch (DbException e) {
            LogUtil.error("DbException", e);
        }
        LogUtil.test("搜索关键字搜索耗时：" + (System.currentTimeMillis() - start));
        return bookmarkResultList;
    }

    private static String getSplitKeywordInstr(String keyword, int rootId) {
        String[] keywords = keyword.trim().split(" ");
        List arrList = new ArrayList();
        for (int i = 0; i < keywords.length; i++) {
            if (!TextUtils.isEmpty(keywords[i].trim())) {
                arrList.add(keywords[i].trim());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arrList.size(); i++) {
            sb.append(" and INSTR(content,'" + arrList.get(i) + "')>0 ");
        }

        List<String> versions = new ArrayList<>();
        if (rootId == 1) {
            //圣经 要筛选和合本、吕振中、思高本等版本
            versions = HuDongApplication.mVersions;
        } else if (rootId == 2) {
            //怀著 要筛选中英文
            versions = HuDongApplication.mVersions_HZ;
        }
        if (versions.size() > 0) {
            sb.append(" and (");
            int i = 0;
            for (String version : versions) {
                if (i == 0) {
                    sb.append(" INSTR(content,'〖" + version + "〗')>0 ");
                } else {
                    sb.append(" or INSTR(content,'〖" + version + "〗')>0 ");
                }
                i++;
            }
            sb.append(" )");
        }
        sb.append(" and INSTR(content,'jieshao') <= 0 ");
        sb.append(" and INSTR(content,'注释') <= 0 ");
        return sb.toString();
    }

    /**
     * 搜索关键字内容
     *
     * @param keyword
     * @param progressBar
     * @param volume
     * @return
     */
    public static List<Bookmark> searchContentByKeyword(String keyword, ProgressBar progressBar,
                                                        Volume volume) {
        long start = System.currentTimeMillis();
        progressBar.setMax(1000);
        progressBar.setProgress(0);
        List<Bookmark> bookmarkResultList = new ArrayList<Bookmark>();
        List<SearchTemp> list = null;
        ProgressThread progressThread = new ProgressThread(1000, progressBar);
        ThreadUtil.execute(progressThread);
        try {
            list = HuDongApplication.getInstance().getDbUtils().findAll(Selector.from(Chapter.class)
                            .where(WhereBuilder.getInstance("volumeId", "=", volume.getId()))
                            .toString(),
                    SearchTemp.class);
            LogUtil.test("搜索关键字搜索耗时：" + (System.currentTimeMillis() - start));
            List<String> have = new ArrayList<String>();
            if (list != null) {
                for (SearchTemp chapter : list) {
                    try {
                        int position = 0;
                        String content = chapter.getContent();
                        if (content != null && content.contains(keyword)) {
                            String volName = volume.getVolName().replaceAll("^\\d{1,}-", "");
                            List<String> contentList = new ArrayList<String>(Arrays.asList(content.split("\n")));
                            int remove = 0;
                            for (int j = 0; j < contentList.size(); j++) {
                                String line = contentList.get(j);
                                if (line.trim().equals("\n") || line.trim().equals("\n\r")
                                        || StringUtil.isEmpty(line.trim())) {
                                    remove++;
                                    continue;
                                }
                                if (line.contains(keyword)) {
                                    position = j - remove + 1;
                                    Bookmark point = new Bookmark();
                                    // point.setCategroyId(bookmark.getCategroyId());
                                    point.setVolumeId(chapter.getVolumeId());
                                    point.setVolumeName(volName);
                                    point.setChapterName(chapter.getName());
                                    point.setChapterIndexId(chapter.getIndexId());
                                    point.setChapterCount(chapter.getChapterCount());
                                    point.setCategroyId(chapter.getCategoryId() + "");
                                    point.setIndex(position);
                                    point.setType(0);
                                    String matchLine = textMacth(line, keyword);
                                    if (StringUtil.isEmpty(matchLine)) {
                                        continue;
                                    }
                                    point.setContent(matchLine);
                                    bookmarkResultList.add(point);
                                }
                            }
                            if (have.indexOf(chapter.getName()) >= 0) {
                                continue;
                            }
                            have.add(chapter.getName());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String content = chapter.getContent();
                        if (content != null) {
                            String[] lines = content.split("\n");
                        }
                    }
                }
            }
        } catch (DbException e1) {
            LogUtil.error("", e1);
        }
        progressThread.setStop(true);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            LogUtil.error("InterruptedException", e);
        }
        LogUtil.test("搜索关键字搜索耗时：" + (System.currentTimeMillis() - start));
        return bookmarkResultList;
    }

    /**
     * 搜索内容关键字(灵修)
     *
     * @param context
     * @param spirituality
     * @param keyword
     * @return
     */
    public static List<Bookmark> searchSpiritualityContentByKeyword(Context context, Spirituality
            spirituality,
                                                                    String keyword) {
        List<Bookmark> points = new ArrayList<Bookmark>();
        try {
            int position = 0;
            Spirituality selectSpirituality = HuDongApplication.getInstance().getDbUtils()
                    .findById(spirituality.getClass(), spirituality.getId());
            String content = selectSpirituality.getContent();
            List<String> contentList = new ArrayList<String>(Arrays.asList(content.split("\n")));
            for (String line : contentList) {
                if (!TextUtils.isEmpty(line)) {
                    position++;
                    String macthLine = textMacth(line, keyword);
                    if (macthLine != null) {
                        Bookmark point = new Bookmark();
                        point.setChapterName(spirituality.getDaytime());
                        point.setVolumeId(spirituality.getId());
                        point.setVolumeName(spirituality.getShowName());
                        point.setCategroyName(spirituality.getPatrent());
                        point.setVolumeName(spirituality.getBook());
                        point.setIndex(position);
                        point.setContent(macthLine);
                        point.setType(1);
                        points.add(point);
                    }
                }

            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return points;
    }

    /**
     * 搜索百科关键字
     *
     * @param baikes
     * @param keyword
     * @param progressBar
     * @return
     */
    public static List<Bookmark> searchBaikeByKeyword(List<Baike> baikes, String keyword,
                                                      ProgressBar progressBar) {
        int length = baikes.size();
        progressBar.setMax(length);
        progressBar.setProgress(0);
        List<Bookmark> bookmarkList = new ArrayList<Bookmark>();
        for (int i = 0; i < length; i++) {
            Baike baike = baikes.get(i);
            int position = 0;
            List<String> contentList = new ArrayList<String>(Arrays.asList(baike.getContent()
                    .split("\n")));
            for (String line : contentList) {
                if (!line.trim().equals("\n") && !line.trim().equals("\n\r") &&
                        StringUtil.isNotEmpty(line.trim())) {
                    position++;
                    String macthLine = textMacth(line, keyword);
                    if (macthLine != null) {
                        Bookmark point = new Bookmark();
                        point.setChapterName(baike.getShowName());
                        point.setVolumeId(baike.getCategoryId());
                        point.setVolumeName(baike.getShowCateName());
                        point.setIndex(position);
                        point.setId(baike.getId());
                        point.setContent(macthLine);
                        point.setType(1);
                        bookmarkList.add(point);
                    }
                }

            }
            progressBar.setProgress(i);
        }
        return bookmarkList;
    }

    /**
     * 搜索百科标题关键字
     *
     * @param baikes
     * @param keyword
     * @param progressBar
     * @return
     */
    public static List<Bookmark> searchBaikeTitleByKeyword(List<Baike> baikes, String keyword,
                                                           ProgressBar progressBar) {
        int length = baikes.size();
        progressBar.setMax(length);
        progressBar.setProgress(0);
        List<Bookmark> bookmarkList = new ArrayList<Bookmark>();
        for (int i = 0; i < length; i++) {
            Baike baike = baikes.get(i);
            int position = 0;
            List<String> contentList = new ArrayList<String>(Arrays.asList(baike.getName()));
            for (String line : contentList) {
                if (line.contains(keyword)) {
                    String macthLine = textMacth(line, keyword);
                    Bookmark point = new Bookmark();
                    point.setChapterName(macthLine);
                    point.setVolumeId(baike.getCategoryId());
                    point.setVolumeName(baike.getShowCateName());
                    point.setIndex(position);
                    point.setId(baike.getId());
                    point.setContent(baike.getContent());
                    point.setType(1);
                    bookmarkList.add(point);
                }
            }
            progressBar.setProgress(i);
        }
        return bookmarkList;
    }

    public static boolean isLuoJiShengJing(List<String> contents) {
        if (contents != null && !contents.isEmpty()) {
            int count = 0;

            for (String content : contents) {
                if (!TextUtils.isEmpty(content) && content.length() > 12) {
                    String preTxt = content.substring(0, 12);
                    String shengJingTxt = CharUtils.match("[\\u4e00-\\u9fa5]{1,2}\\d+:\\d+", preTxt);
                    if (!TextUtils.isEmpty(shengJingTxt) && content.replace(" ", "").startsWith(shengJingTxt)) {
                        count++;
                    }
                }
            }

            if (count > 2) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取章节内容
     *
     * @param context
     * @param chapter
     * @return
     */
    public static List<String> queryChaptreContent(Context context, Chapter chapter, int textModel) {
        String contents = chapter.getContent();
        List<String> content = new ArrayList<>(Arrays.asList(contents.split("\n")));
        boolean isSheingJ = isLuoJiShengJing(content);

        // int remove = 0;
        for (int i = 0; i < content.size(); i++) {
            String string = content.get(i);

            if (string.trim().equals("\n") || string.trim().equals("\n\r") || StringUtil.isEmpty(string.trim())) {
                content.remove(i);
                i--;
                continue;
            }
            if (textModel == SystemConfig.TEXT_MODEL_FANTI) {
                content.set(i, jian2fan(string + "\n"));
            }

        }
        if (isSheingJ) {
            List<String> rContent = new ArrayList<>();
            if (content != null && !content.isEmpty()) {
                String saveHead = "";
                for (String con : content) {
                    List<String> allTags = HuDongApplication.baseVersions;
                    List<String> tags = HuDongApplication.mVersions;
                    String head = CharUtils.match("[\\u4e00-\\u9fa5]{1,2}\\d+:\\d+", con);
                    boolean isNoContains = false;
                    // 剔除没有选中的
                    for (String tagName : allTags) {
                        if (!tags.contains(tagName)) {
                            if (con.contains("〖" + tagName + "〗") && con.contains("〖/" + tagName + "〗")) {
                                isNoContains = true;
                            }
                        }
                    }
                    if (!isNoContains) {
                        if (!TextUtils.isEmpty(head)) {
                            if (!con.contains(head)) {
                                rContent.add(head + con);
                            } else {
                                rContent.add(con);
                            }
                        } else if (!TextUtils.isEmpty(saveHead)) {
                            if (!con.contains(saveHead)) {
                                rContent.add(saveHead + con);
                            } else {
                                rContent.add(con);
                            }
                            saveHead = "";
                        } else {
                            rContent.add(con);
                        }
                    } else {
                        if (!TextUtils.isEmpty(head)) {
                            saveHead = head;
                        }
                    }
                }
            }
            return rContent;
        } else {
            List<String> rContent = new ArrayList<String>();
            List<String> allTags = HuDongApplication.HZ_baseVersions;
            List<String> tags = HuDongApplication.mVersions_HZ;
            if (tags.size() != allTags.size()) {
                if (tags.get(0).contains("中文")) {
                    for (String entity : content) {
                        if (entity.contains("中文")) {
                            rContent.add(entity);
                        }
                    }
                } else { //英文
                    for (String entity : content) {
                        if (entity.contains("英文")) {
                            rContent.add(entity);
                        }
                    }
                }
                if (rContent.size() > 0) {
                    return rContent;
                }
            }

            return content;
        }
    }


    /**
     * 获取章节内容
     *
     * @param context
     */
    public static List<String> queryChaptreContentByContent(Context context, String contents, int
            textModel) {
        List<String> content = new ArrayList<String>();
        content = new ArrayList<>(Arrays.asList(contents.split("\n")));
        // int remove = 0;
        for (int i = 0; i < content.size(); i++) {
            String string = content.get(i);
            if (string.trim().equals("\n") || string.trim().equals("\n\r") || StringUtil.isEmpty
                    (string.trim())) {
                content.remove(i);
                i--;
                continue;
            }
            if (textModel == SystemConfig.TEXT_MODEL_FANTI) {
                content.set(i, jian2fan(string + "\n"));
            }
        }
        return content;
    }


    /**
     * 获取每日灵修内容
     *
     * @param context
     * @param spirituality
     * @return
     */
    public static List<String> querySpiritualityContent(Context context, Spirituality
            spirituality, int textModel) {
        List<String> content = new ArrayList<String>();
        LogUtil.log("spirituality:" + spirituality.getId() + "-" + spirituality.toString());
        try {
            Spirituality selectSpirituality = HuDongApplication.getInstance().getDbUtils()
                    .findById(spirituality.getClass(), spirituality.getId());
            if (selectSpirituality != null) {
                String selectContent = selectSpirituality.getContent();
                if (selectContent == null) {
                    return content;
                }
                if (textModel == SystemConfig.TEXT_MODEL_FANTI) {
                    selectContent = jian2fan(selectContent);
                }
                String[] arrays = selectContent.split("\n");
                if (arrays.length == 0) {
                    content.add(new String(""));
                    return content;
                }
                for (int i = 0; i < arrays.length; i++) {
                    if (StringUtil.isNotEmpty(arrays[i])) {
                        content.add(arrays[i]);
                    }
                }
            }
        } catch (DbException e) {
            Writer result = new StringWriter();
            PrintWriter printWriter = new PrintWriter(result);
            e.printStackTrace(printWriter);
            LogUtil.log("querySpiritualityContent exception:" + result.toString());
            LogUtil.error("querySpiritualityContent", e);
        }

        return content;
    }


    /**
     * 获取百科内容
     *
     * @param context
     * @param baike
     * @param textModel
     * @return
     */
    public static List<String> queryBaikeContent(Context context, Baike baike, int textModel) {
        List<String> content = new ArrayList<String>();
        try {
            Baike selectBaike = HuDongApplication.getInstance().getDbUtils()
                    .findById(baike.getClass(), baike.getId());
            if (selectBaike != null) {
                String selectContent = selectBaike.getContent();
                if (selectContent == null) {
                    return content;
                }
                if (textModel == SystemConfig.TEXT_MODEL_FANTI) {
                    selectContent = jian2fan(selectContent);
                }
                String[] arrays = selectContent.split("\n");
                for (int i = 0; i < arrays.length; i++) {
                    if (arrays[i].trim().equals("\n") || arrays[i].trim().equals("\n\r") ||
                            StringUtil.isEmpty(arrays[i].trim())) {
                        continue;
                    } else {
                        content.add(arrays[i]);
                    }
                }
            }
        } catch (DbException e) {
            Writer result = new StringWriter();
            PrintWriter printWriter = new PrintWriter(result);
            e.printStackTrace(printWriter);
            LogUtil.log("querySpiritualityContent exception:" + result.toString());
            LogUtil.error("querySpiritualityContent", e);
        }
        return content;
    }

    /**
     * 关键字匹配
     *
     * @param content
     * @param keyword
     * @return
     */

    public static String textMacth(String content, String keyword) {
        return textMacth(content, keyword, false);
    }


    /**
     * 关键字匹配
     *
     * @param content
     * @param keyword
     * @return
     */

    public static String textMacth(String content, String keyword, boolean searchTitle) {
        String[] keyWords = keyword.split(" ");
        List<String> strList = Arrays.asList(keyWords);
        List arrList = new ArrayList(strList);
        Iterator<String> stringIterator = arrList.iterator();
        while (stringIterator.hasNext()) {
            String string = stringIterator.next();
            if (TextUtils.isEmpty(string)) {
                stringIterator.remove();
            }
        }
        keyWords = new String[arrList.size()];
        arrList.toArray(keyWords);
        String result = content;
        if (searchTitle) {
            Document document = Jsoup.parse(content);
            Elements elements = document.getElementsByTag("b");
            for (Element element : elements) {
                int j = keyWords.length;
                for (int i = 0; i < j; i++) {
                    String kd = keyWords[i];
                    if (element.text().contains(kd)) {
                        result = result.replace(element.text(),
                                element.text().replace(kd, "<font color='#ff0000'>" + kd +
                                        "</font>"));
                    }
                }
            }
            return result;
        }
        int j = keyWords.length;
        for (int i = 0; i < j; i++) {
            String kd = keyWords[i];
            if (content.contains(kd)) {
                result = result.replace(kd, "<font color='#ff0000'>" + kd + "</font>");
            } else if (content.toLowerCase().contains(kd)) {//内容小写才有适配
                result = result.replace(kd.toUpperCase(), "<font color='#ff0000'>" + kd.toUpperCase() + "</font>");
            } else if (content.toUpperCase().contains(kd)) {
                result = result.replace(kd.toLowerCase(), "<font color='#ff0000'>" + kd.toLowerCase() + "</font>");
            } else {
                result = null;
                break;
            }
        }
        return result;
    }


    /**
     * 简体转繁体
     *
     * @param content
     * @return
     */
    public static String jian2fan(String content) {
        if (TextUtils.isEmpty(content)) return "";
        String changeText = null;
        try {
            JChineseConvertor jChineseConvertor = JChineseConvertor.getInstance();
            changeText = jChineseConvertor.s2t(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return changeText;
    }

    public static String replaceTag(String str1, String str2) {
        String str = str2.replace("", "");
        return Pattern.compile(str1).matcher(str).replaceAll("");
    }

    public static String getChapterTxtName(String volumeId, String indexId) {
        return "chapters/" + volumeId + "_" + indexId + ".txt";
    }

    public static String getSpiritualityTxtName(int id) {
        return "spirituality/" + id + ".txt";
    }

    /**
     * @param mCcontext
     * @param chapter
     * @param mTextModel
     * @param flag1      1是怀著带中文
     * @return
     */
    public static List<String> queryChaptreContent(Context mCcontext, Chapter chapter, int mTextModel, int flag1) {
        List<String> content = new ArrayList<String>();
        String contents = chapter.getContent();

        content = new ArrayList<String>(Arrays.asList(contents.split("\n")));

        // int remove = 0;
        for (int i = 0; i < content.size(); i++) {
            String string = content.get(i);
            if (string.trim().equals("\n") || string.trim().equals("\n\r") || StringUtil.isEmpty
                    (string.trim())) {
                content.remove(i);
                i--;
                continue;
            }
            if (mTextModel == SystemConfig.TEXT_MODEL_FANTI) {
                content.set(i, jian2fan(string + "\n"));
            }
        }

        LogUtil.debug("queryChaptreContent", "  content =  " + content);

        List<String> rContent = new ArrayList<String>();
        List<String> allTags = HuDongApplication.HZ_baseVersions;
        List<String> tags = HuDongApplication.mVersions_HZ;
        if (tags.size() != allTags.size()) {
            if (tags.get(0).contains("中文")) {
                for (String entity : content) {
                    if (entity.contains("中文")) {
                        rContent.add(entity);
                    }
                }
            } else { //英文
                for (String entity : content) {
                    if (entity.contains("英文")) {
                        rContent.add(entity);
                    }
                }
            }
            if(rContent.size() ==0){
                return content;
            }
            return rContent;
        }

        return content;
    }
}
