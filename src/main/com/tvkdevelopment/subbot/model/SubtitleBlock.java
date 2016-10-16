package com.tvkdevelopment.subbot.model;

public class SubtitleBlock {

    private final int mId;
    private final SubtitleTimestamp mStart;
    private final SubtitleTimestamp mEnd;
    private String mText;

    public SubtitleBlock(final int id, final SubtitleTimestamp start, final SubtitleTimestamp end, final String text) {
        mId = id;
        mStart = start;
        mEnd = end;
        mText = text;
    }

    public int getId() {
        return mId;
    }

    public SubtitleTimestamp getStart() {
        return mStart;
    }

    public SubtitleTimestamp getEnd() {
        return mEnd;
    }

    public String getText() {
        return mText;
    }

    public String setText(final String text) {
        return mText = text;
    }

    @Override
    public String toString() {
        return mId + System.getProperty("line.separator") +
               mStart + " --> " + mEnd + System.getProperty("line.separator") +
               mText + System.getProperty("line.separator") + System.getProperty("line.separator");
    }

}
