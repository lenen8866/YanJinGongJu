package com.read.scriptures.bean;

import java.util.Objects;

public class AnswerClickBean {
    public int selectedAnswerIndex;
    public int rightAnswer;//0未答题 1答对 2答错

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnswerClickBean)) return false;
        AnswerClickBean that = (AnswerClickBean) o;
        return title.equals(that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    public String title;
}
