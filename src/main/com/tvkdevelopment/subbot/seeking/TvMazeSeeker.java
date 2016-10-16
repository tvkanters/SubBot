package com.tvkdevelopment.subbot.seeking;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.tvkdevelopment.subbot.exceptions.UnknownEpisodeException;
import com.tvkdevelopment.subbot.exceptions.UnknownSeriesTitleException;
import com.tvkdevelopment.subbot.model.Episode;
import com.tvkdevelopment.subbot.network.NetworkHelper;

/**
 * Used for finding the names of series episodes by querying TvMaze.
 */
public class TvMazeSeeker implements EpisodeTitleSeeker {

    private static final String ENCODING = "UTF-8";
    private static final String BASE_URL = "http://api.tvmaze.com/";

    /*package*/ final int mSeriesId;
    private final String mSeriesTitle;

    public TvMazeSeeker(final String seriesTitle) throws UnknownSeriesTitleException {
        mSeriesId = getSeriesId(seriesTitle);
        mSeriesTitle = seriesTitle;
    }

    private int getSeriesId(final String seriesTitle) throws UnknownSeriesTitleException {
        final String response;
        try {
            response = NetworkHelper.get(BASE_URL + "singlesearch/shows?q=" + URLEncoder.encode(seriesTitle, ENCODING));
        } catch (final UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }

        if (response == null || response.isEmpty()) {
            throw new UnknownSeriesTitleException(seriesTitle);
        }

        try {
            return new JSONObject(response).getInt("id");
        } catch (final JSONException ex) {
            throw new UnknownSeriesTitleException(seriesTitle, ex);
        }
    }

    @Override
    public String seekEpisodeTitle(final Episode episode) throws UnknownEpisodeException {
        final String response = NetworkHelper.get(BASE_URL + "shows/" + mSeriesId + "/episodebynumber?season=" + episode.getSeason() + "&number=" + episode.getNumber());

        if (response == null || response.isEmpty()) {
            throw new UnknownEpisodeException(episode);
        }

        try {
            return new JSONObject(response).getString("name");
        } catch (final JSONException ex) {
            throw new UnknownEpisodeException(episode, ex);
        }
    }

    @Override
    public String getSeriesTitle() {
        return mSeriesTitle;
    }

}
