package com.tvkdevelopment.subbot.model;

import java.util.Objects;

public class Episode {

    private final int mSeason;
    private final int mNumber;

    public Episode(final int season, final int number) {
        mSeason = season;
        mNumber = number;
    }

    public int getSeason() {
        return mSeason;
    }

    public int getNumber() {
        return mNumber;
    }

    public String getNumberString() {
        return (mNumber < 10 ? "0" : "") + mNumber;
    }

    @Override
    public String toString() {
        return mSeason + "x" + getNumberString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Episode)) {
            return false;
        }
        final Episode other = (Episode) obj;
        return mSeason == other.mSeason && mNumber == other.mNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mSeason, mNumber);
    }

}
