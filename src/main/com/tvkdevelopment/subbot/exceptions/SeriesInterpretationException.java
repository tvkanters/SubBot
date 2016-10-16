package com.tvkdevelopment.subbot.exceptions;

public class SeriesInterpretationException extends Exception {

    private static final long serialVersionUID = -8016594893857249324L;

    public SeriesInterpretationException(final String message) {
        super(message);
    }

    public SeriesInterpretationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}