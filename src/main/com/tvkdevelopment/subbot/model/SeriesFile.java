package com.tvkdevelopment.subbot.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import com.tvkdevelopment.subbot.FileManager;
import com.tvkdevelopment.subbot.SubtitleCleaner;
import com.tvkdevelopment.subbot.exceptions.FilenameFormatException;
import com.tvkdevelopment.subbot.exceptions.UnknownEpisodeException;
import com.tvkdevelopment.subbot.exceptions.UnknownSeriesTitleException;
import com.tvkdevelopment.subbot.parsing.EpisodeParser;
import com.tvkdevelopment.subbot.parsing.SeriesTitleParser;
import com.tvkdevelopment.subbot.seeking.EpisodeTitleSeeker;
import com.tvkdevelopment.subbot.seeking.SubtitlesSeeker.SubtitleSearchResult;
import com.tvkdevelopment.subbot.seeking.SubtitlesSeeker;
import com.tvkdevelopment.subbot.seeking.TvMazeSeeker;

public class SeriesFile {

    private static final String SUBTITLE_DIRECTORY = "Subtitles";
    private static final String SUBTITLE_EXTENSION = ".srt";
    private static final int SUBTITLE_LIMIT = 5;

    private final File mFile;
    private final String mFilename;
    private final EpisodeTitleSeeker mTitleSeeker;
    private final Episode mEpisode;
    private final Optional<Episode> mConjoinedEpisode;
    private final String mTitle;
    private final String mExtension;
    private final File mSubtitlesDirectory;
    private final String mSubtitlesFilePath;

    public SeriesFile(final File file) throws FilenameFormatException, UnknownSeriesTitleException, UnknownEpisodeException {
        this(file, new TvMazeSeeker(getSeriesTitle(file)));
    }

    public SeriesFile(final File file, final EpisodeTitleSeeker titleSeeker) throws FilenameFormatException, UnknownSeriesTitleException, UnknownEpisodeException {
        mFile = file;
        mFilename = file.getName();
        mTitleSeeker = titleSeeker;

        final EpisodeParser.Result parsedEpisodes = EpisodeParser.parseFilename(mFilename);
        mEpisode = parsedEpisodes.getEpisode();
        mConjoinedEpisode = parsedEpisodes.getConjoinedEpisode();

        String titlePrefix = mEpisode.toString();
        String title = titleSeeker.seekEpisodeTitle(mEpisode);
        if (mConjoinedEpisode.isPresent()) {
            final Episode conjoinedEpisode = mConjoinedEpisode.get();
            titlePrefix += "-" + conjoinedEpisode.getNumberString();
            title += " - " + titleSeeker.seekEpisodeTitle(conjoinedEpisode);
        }
        mTitle = titlePrefix + " - " + title;
        mExtension = mFilename.substring(mFilename.lastIndexOf('.') + 1);
        mSubtitlesDirectory = new File(mFile.getParentFile().getPath() + File.separator + SUBTITLE_DIRECTORY);
        mSubtitlesFilePath = mSubtitlesDirectory.getPath() + File.separator + FileManager.cleanFilename(mTitle);
    }

    private static String getSeriesTitle(final File file) {
        String seriesTitle;
        final File parentFile = file.getParentFile();
        if (parentFile.getName().startsWith("Season ")) {
            seriesTitle = parentFile.getParentFile().getName();

        } else {
            int searchEnd = file.getName().lastIndexOf('.');
            try {
                final EpisodeParser.Result parsedEpisodes = EpisodeParser.parseFilename(file.getName());
                searchEnd = parsedEpisodes.getMatchStart();
            } catch (final FilenameFormatException ex) {
                // Ignore
            }

            try {
                seriesTitle = SeriesTitleParser.parseFilename(file.getName().substring(0, searchEnd));
            } catch (final FilenameFormatException ex) {
                ex.printStackTrace();
                seriesTitle = parentFile.getName();
            }
        }
        return seriesTitle;
    }

    public EpisodeTitleSeeker getTitleSeeker() {
        return mTitleSeeker;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getExtension() {
        return mExtension;
    }

    public void installSubtitles(final SubtitlesSeeker seeker) {
        Optional<List<SubtitleSearchResult>> subtitleResults = seeker.search(mFile);
        if (!subtitleResults.isPresent() || subtitleResults.get().isEmpty()) {

            subtitleResults = seeker.search(mFile.getName());
            if (!subtitleResults.isPresent() || subtitleResults.get().isEmpty()) {

                subtitleResults = seeker.search(mTitleSeeker.getSeriesTitle() + " - " + mTitle);
                if (!subtitleResults.isPresent() || subtitleResults.get().isEmpty()) {
                    return;
                }
            }
        }
        final List<SubtitleSearchResult> subtitleSearchResultsTrimmed = subtitleResults.get().subList(0, Math.min(subtitleResults.get().size(), SUBTITLE_LIMIT));

        if (!mSubtitlesDirectory.exists()) {
            mSubtitlesDirectory.mkdir();
        }

        final List<String> subtitles = seeker.download(subtitleSearchResultsTrimmed);
        subtitleLoop:
            for (int i = 0; i < subtitles.size(); ++i) {
            File subtitlesFile = new File(mSubtitlesFilePath + SUBTITLE_EXTENSION);

            int attempt = 0;
            while (subtitlesFile.exists()) {
                ++attempt;
                if (attempt == SUBTITLE_LIMIT) {
                    continue subtitleLoop;
                }
                subtitlesFile = new File(mSubtitlesFilePath + "–" + attempt + SUBTITLE_EXTENSION);
            }

            final String cleanedSubtitle = SubtitleCleaner.clean(subtitles.get(i));
            FileManager.saveFile(subtitlesFile, cleanedSubtitle, subtitleSearchResultsTrimmed.get(i).encoding);
        }
    }

    public boolean hasSubtitles() {
        return new File(mSubtitlesFilePath + SUBTITLE_EXTENSION).exists();
    }

    public void rename() {
        System.gc();
        try {
            Files.move(mFile.toPath(), new File(mFile.getParent() + File.separator + FileManager.cleanFilename(mTitle) + "." + mExtension).toPath());
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

}
