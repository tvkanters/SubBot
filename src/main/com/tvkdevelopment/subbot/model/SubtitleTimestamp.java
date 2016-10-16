package com.tvkdevelopment.subbot.model;

public class SubtitleTimestamp {

    private final int mHours;
    private final int mMinutes;
    private final int mSeconds;
    private final int mMilliseconds;

    public SubtitleTimestamp(final String hours, final String minutes, final String seconds, final String milliseconds) {
        mHours = Integer.parseInt(hours);
        mMinutes = Integer.parseInt(minutes);
        mSeconds = Integer.parseInt(seconds);
        mMilliseconds = Integer.parseInt(milliseconds);
    }

    @Override
    public String toString() {
        return prefix(mHours, 2) + ":" + prefix(mMinutes, 2) + ":" + prefix(mSeconds, 2) + "," + prefix(mMilliseconds, 3);
    }

    private String prefix(final int number, final int length) {
        String string = String.valueOf(number);
        while(string.length() < length) {
            string = "0" + string;
        }
        return string;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof SubtitleTimestamp)) {
            return false;
        }
        final SubtitleTimestamp other = (SubtitleTimestamp) obj;
        return mHours == other.mHours && mMinutes == other.mMinutes && mSeconds == other.mSeconds && mMilliseconds == other.mMilliseconds;
    }

}
