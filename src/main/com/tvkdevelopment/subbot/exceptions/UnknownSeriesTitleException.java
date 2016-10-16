package com.tvkdevelopment.subbot.exceptions;

public class UnknownSeriesTitleException extends SeriesInterpretationException {

    private static final long serialVersionUID = 7857474405439664955L;

    public UnknownSeriesTitleException(final String seriesTitle) {
        super("Couldn't find series title: " + seriesTitle);
    }

    public UnknownSeriesTitleException(final String seriesTitle, final Throwable throwable) {
        super("Couldn't find series title: " + seriesTitle, throwable);
    }

}