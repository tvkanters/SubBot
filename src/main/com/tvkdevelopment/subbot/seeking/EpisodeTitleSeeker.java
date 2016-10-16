package com.tvkdevelopment.subbot.seeking;

import com.tvkdevelopment.subbot.exceptions.UnknownEpisodeException;
import com.tvkdevelopment.subbot.model.Episode;

/**
 * An interface for finding the names of series episodes.
 */
public interface EpisodeTitleSeeker {

    /**
     * Searches for the title of a specific episode.
     *
     * @param episode The episode to find the title for
     * @return The episode's title
     * @throws UnknownEpisodeException Thrown when the episode wasn't recognised
     */
    String seekEpisodeTitle(final Episode episode) throws UnknownEpisodeException;

    /**
     * @return The title for the series that the seeker is currently searching for
     */
    String getSeriesTitle();

}
