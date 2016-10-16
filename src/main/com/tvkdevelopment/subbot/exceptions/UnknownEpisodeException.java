package com.tvkdevelopment.subbot.exceptions;

import com.tvkdevelopment.subbot.model.Episode;

public class UnknownEpisodeException extends SeriesInterpretationException {

    private static final long serialVersionUID = 455757714424379947L;

    public UnknownEpisodeException(final Episode episode) {
        super("Couldn't find series episode: " + episode);
    }

    public UnknownEpisodeException(final Episode episode, final Throwable throwable) {
        super("Couldn't find series episode: " + episode, throwable);
    }

}