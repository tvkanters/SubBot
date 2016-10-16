package com.tvkdevelopment.subbot.exceptions;

public class FilenameFormatException extends SeriesInterpretationException {

    private static final long serialVersionUID = -726242675626258306L;

    public FilenameFormatException(final String filename) {
        super("Couldn't format filename: " + filename);
    }

}