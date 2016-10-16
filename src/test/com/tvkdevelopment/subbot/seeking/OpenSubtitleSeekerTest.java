package com.tvkdevelopment.subbot.seeking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.tvkdevelopment.subbot.seeking.SubtitlesSeeker.SubtitleSearchResult;

public class OpenSubtitleSeekerTest {

    /** Update as needed */
    private static final File SAMPLE_FILE = new File(
            "D:\\Series\\Seen\\Ray Donovan\\Season 1\\Ray.Donovan.S01E03.720p.HDTV.x264-IMMERSE.mkv");

    OpenSubtitlesSeeker mSeeker;

    @Before
    public void setup() {
        mSeeker = new OpenSubtitlesSeeker();
    }

    @Test
    public void testLogin() {
        final boolean success = mSeeker.logIn("", "");
        assertTrue(success);
        assertTrue(mSeeker.isLoggedIn());
        mSeeker.logOut();
        assertFalse(mSeeker.isLoggedIn());
    }

    @Test
    public void testHash() {
        final String hash = mSeeker.hashVideoFile(SAMPLE_FILE);
        assertEquals("99a3188343a6d529", hash); // Update as needed
    }

    @Test
    public void testSearchFile() {
        mSeeker.logIn("", "");
        final Optional<List<SubtitleSearchResult>> subtitles = mSeeker.search(SAMPLE_FILE);
        mSeeker.logOut();
        assertTrue(subtitles.isPresent());
        assertEquals(2, subtitles.get().size());
    }

    @Test
    public void testSearchQuery() {
        mSeeker.logIn("", "");
        final Optional<List<SubtitleSearchResult>> subtitles = mSeeker.search(SAMPLE_FILE.getName());
        mSeeker.logOut();
        assertTrue(subtitles.isPresent());
        assertEquals(3, subtitles.get().size());
    }

}
