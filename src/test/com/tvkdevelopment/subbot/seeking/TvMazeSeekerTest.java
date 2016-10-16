package com.tvkdevelopment.subbot.seeking;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.tvkdevelopment.subbot.exceptions.UnknownEpisodeException;
import com.tvkdevelopment.subbot.exceptions.UnknownSeriesTitleException;
import com.tvkdevelopment.subbot.model.Episode;
import com.tvkdevelopment.subbot.seeking.TvMazeSeeker;

public class TvMazeSeekerTest  {

    @Test
    public void testSeekSeriesId() throws UnknownSeriesTitleException {
        final TvMazeSeeker tvMazeSeeker = new TvMazeSeeker("Ray Donovan");
        assertEquals(152, tvMazeSeeker.mSeriesId);
    }

    @Test
    public void testSeekSeriesIdNoResult() {
        final TvMazeSeeker tvMazeSeeker;
        try {
            tvMazeSeeker = new TvMazeSeeker("Ray Donovich");
        } catch (final UnknownSeriesTitleException ex) {
            // Successful
            return;
        }
        fail("" + tvMazeSeeker.mSeriesId);
    }

    @Test
    public void testSeekEpisodeTitle() throws UnknownSeriesTitleException, UnknownEpisodeException {
        final TvMazeSeeker tvMazeSeeker = new TvMazeSeeker("Ray Donovan");
        final String episodeTitle = tvMazeSeeker.seekEpisodeTitle(new Episode(1, 3));
        assertEquals("Twerk", episodeTitle);
    }

    @Test
    public void testSeriesIdSeekerNoResult() throws UnknownSeriesTitleException {
        final TvMazeSeeker tvMazeSeeker = new TvMazeSeeker("Ray Donovan");
        final String episodeTitle;
        try {
            episodeTitle = tvMazeSeeker.seekEpisodeTitle(new Episode(11, 13));
        } catch (final UnknownEpisodeException ex) {
            // Successful
            return;
        }
        fail(episodeTitle);
    }

}
