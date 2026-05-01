package com.read.scriptures.model;


import java.text.Collator;
import java.util.Comparator;

@SuppressWarnings("unchecked")
public class ChineseCharComp implements Comparator<String> {

    public int compare(String o1, String o2) {
        Collator myCollator = Collator.getInstance(java.util.Locale.CHINA);
        if (myCollator.compare(o1, o2) < 0)
            return -1;
        else if (myCollator.compare(o1, o2) > 0)
            return 1;
        else
            return 0;
    }

}
