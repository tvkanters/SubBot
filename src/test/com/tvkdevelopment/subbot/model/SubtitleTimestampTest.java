package com.tvkdevelopment.subbot.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tvkdevelopment.subbot.exceptions.FilenameFormatException;

public class SubtitleTimestampTest {

    @Test
    public void testToString() throws FilenameFormatException {
        final SubtitleTimestamp timestamp = new SubtitleTimestamp("1", "0", "6", "4");

        assertEquals("01:00:06,004", timestamp.toString());
    }

}
