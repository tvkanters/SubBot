package com.tvkdevelopment.subbot.seeking;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * An interface for finding and downloading subtitles of video files. Intended to be used for communicating with
 * external APIs.
 */
public interface SubtitlesSeeker {

    /**
     * Logs us in to the external service's API.
     * @param user The user to log in as
     * @param password The password used to log in
     * @return True iff logging in was successful
     */
    boolean logIn(String user, String password);

    /**
     * @return True iff we're currently logged in to the external service's API
     */
    boolean isLoggedIn();

    /**
     * Logs us out of the external service's API.
     */
    void logOut();

    /**
     * Searches the external service for subtitles of a specific file.
     *
     * @param file The file to search subtitles for
     * @return A list of subtitles if the search completed successfully or {@link Optional#empty()} otherwise
     */
    Optional<List<SubtitleSearchResult>> search(File file);

    /**
     * Searches the external service for subtitles based on the provide search query.
     *
     * @param query The query to search for - typically a file name
     * @return A list of subtitles if the search completed successfully or {@link Optional#empty()} otherwise
     */
    Optional<List<SubtitleSearchResult>> search(String query);

    /**
     * Downloads a list of subtitles that were returned as search results in the past.
     * @param subtitleSearchResults The subtitles to download
     * @return The downloaded subtitles in the form of strings
     */
    List<String> download(List<SubtitleSearchResult> subtitleSearchResults);

    /**
     * A result wrapper for subtitle searches that can be used to identify the subtitle.
     */
    class SubtitleSearchResult {
        /** The ID to recognise the search result with on the external service */
        public final int id;
        /** The encoding used for the downloaded subtitle */
        public final String encoding;
        /** Whether these subtitles is meant for a hearing impaired audience. */
        public final boolean hearingImpaired;

        public SubtitleSearchResult(final int id, final String encoding, final boolean hearingImpaired) {
            this.id = id;
            this.encoding = encoding;
            this.hearingImpaired = hearingImpaired;
        }
    }
}
