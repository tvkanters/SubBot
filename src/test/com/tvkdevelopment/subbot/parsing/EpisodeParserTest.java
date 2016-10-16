package com.tvkdevelopment.subbot.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.tvkdevelopment.subbot.exceptions.FilenameFormatException;
import com.tvkdevelopment.subbot.model.Episode;
import com.tvkdevelopment.subbot.parsing.EpisodeParser;
import com.tvkdevelopment.subbot.parsing.EpisodeParser.Result;

public class EpisodeParserTest {

    // S01E03

    @Test
    public void testSeFormat() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.S01E03.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertFalse(result.getConjoinedEpisode().isPresent());
        assertEquals(18, result.getMatchEnd());
    }

    @Test
    public void testSeFormatDoubleDigits() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.S11E13.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(11, 13), result.getEpisode());
        assertFalse(result.getConjoinedEpisode().isPresent());
        assertEquals(18, result.getMatchEnd());
    }

    @Test
    public void testSeFormatShort() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.S1E3.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertFalse(result.getConjoinedEpisode().isPresent());
        assertEquals(16, result.getMatchEnd());
    }

    @Test
    public void testSeFormatCase() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.s01e03.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertFalse(result.getConjoinedEpisode().isPresent());
        assertEquals(18, result.getMatchEnd());
    }

    @Test
    public void testSeFormatConjoinedEpisode() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.S01E0304.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertTrue(result.getConjoinedEpisode().isPresent());
        assertEquals(new Episode(1, 4), result.getConjoinedEpisode().get());
        assertEquals(20, result.getMatchEnd());
    }

    @Test
    public void testSeFormatConjoinedEpisodeDash() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.S01E03-04.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertTrue(result.getConjoinedEpisode().isPresent());
        assertEquals(new Episode(1, 4), result.getConjoinedEpisode().get());
        assertEquals(21, result.getMatchEnd());
    }

    @Test
    public void testSeFormatConjoinedEpisodeE() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.S01E03E04.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertTrue(result.getConjoinedEpisode().isPresent());
        assertEquals(new Episode(1, 4), result.getConjoinedEpisode().get());
        assertEquals(21, result.getMatchEnd());
    }

    @Test
    public void testSeFormatConjoinedEpisodeDashE() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.S01E03-E04.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertTrue(result.getConjoinedEpisode().isPresent());
        assertEquals(new Episode(1, 4), result.getConjoinedEpisode().get());
        assertEquals(22, result.getMatchEnd());
    }

    // 1 Episode 03

    @Test
    public void testEpisodeFormat() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.1 Episode 03.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertFalse(result.getConjoinedEpisode().isPresent());
        assertEquals(24, result.getMatchEnd());
    }

    @Test
    public void testEpisodeFormatDoubleDigits() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.11 Episode 13.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(11, 13), result.getEpisode());
        assertFalse(result.getConjoinedEpisode().isPresent());
        assertEquals(25, result.getMatchEnd());
    }

    @Test
    public void testEpisodeFormatShort() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.1 Episode 3.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertFalse(result.getConjoinedEpisode().isPresent());
        assertEquals(23, result.getMatchEnd());
    }

    @Test
    public void testEpisodeFormatCase() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.1 episode 03.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertFalse(result.getConjoinedEpisode().isPresent());
        assertEquals(24, result.getMatchEnd());
    }

    @Test
    public void testEpisodeFormatConjoinedEpisode() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.1 Episode 03-04.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertTrue(result.getConjoinedEpisode().isPresent());
        assertEquals(new Episode(1, 4), result.getConjoinedEpisode().get());
        assertEquals(27, result.getMatchEnd());
    }

    @Test
    public void testEpisodeFormatShortConjoined() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.1 Episode 3-4.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertTrue(result.getConjoinedEpisode().isPresent());
        assertEquals(new Episode(1, 4), result.getConjoinedEpisode().get());
        assertEquals(25, result.getMatchEnd());
    }

    // 1x03

    @Test
    public void testXFormat() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.1x03.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertFalse(result.getConjoinedEpisode().isPresent());
        assertEquals(16, result.getMatchEnd());
    }

    @Test
    public void testXFormatDoubleDigits() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.11x13.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(11, 13), result.getEpisode());
        assertFalse(result.getConjoinedEpisode().isPresent());
        assertEquals(17, result.getMatchEnd());
    }

    @Test
    public void testXFormatShort() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.1x3.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertFalse(result.getConjoinedEpisode().isPresent());
        assertEquals(15, result.getMatchEnd());
    }

    @Test
    public void testXFormatCase() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.1X03.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertFalse(result.getConjoinedEpisode().isPresent());
        assertEquals(16, result.getMatchEnd());
    }

    @Test
    public void testXFormatConjoinedEpisode() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.1x0304.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertTrue(result.getConjoinedEpisode().isPresent());
        assertEquals(new Episode(1, 4), result.getConjoinedEpisode().get());
        assertEquals(18, result.getMatchEnd());
    }

    @Test
    public void testXFormatConjoinedEpisodeDash() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.1x03-04.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertTrue(result.getConjoinedEpisode().isPresent());
        assertEquals(new Episode(1, 4), result.getConjoinedEpisode().get());
        assertEquals(19, result.getMatchEnd());
    }

    // 103

    @Test
    public void testNumFormat() throws FilenameFormatException {
        final Result result = EpisodeParser.parseFilename("Ray.Donovan.103.720p.HDTV.x264-EVOLVE");
        assertEquals(new Episode(1, 3), result.getEpisode());
        assertFalse(result.getConjoinedEpisode().isPresent());
        assertEquals(15, result.getMatchEnd());
    }

    // None

    @Test
    public void testNone() throws FilenameFormatException {
        final Result result;
        try {
            result = EpisodeParser.parseFilename("Ray.Donovan.1.HDTV.EVOLVE");
        } catch (final FilenameFormatException ex) {
            // Successful
            return;
        }
        fail(result.toString());
    }

}
