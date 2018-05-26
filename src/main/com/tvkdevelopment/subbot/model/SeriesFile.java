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
    private static final String SUBTITLE_DIRECTORY_HEARING_IMPAIRED = "Subtitles HI";
    private static final String SUBTITLE_EXTENSION = ".srt";
    private static final int SUBTITLE_LIMIT = 5;

    private final File mFile;
    private final EpisodeTitleSeeker mTitleSeeker;
    private final String mTitle;
    private final String mExtension;
    private final File mSubtitlesDirectory;
    private final File mSubtitlesDirectoryHearingImpaired;
    private final String mSubtitlesFilePath;
    private final String mSubtitlesFilePathHearingImpaired;

    public SeriesFile(final File file) throws FilenameFormatException, UnknownSeriesTitleException, UnknownEpisodeException {
        this(file, new TvMazeSeeker(getSeriesTitle(file)));
    }

    public SeriesFile(final File file, final EpisodeTitleSeeker titleSeeker) throws FilenameFormatException, UnknownSeriesTitleException, UnknownEpisodeException {
        mFile = file;
        mTitleSeeker = titleSeeker;

        final String filename = file.getName();
        final EpisodeParser.Result parsedEpisodes = EpisodeParser.parseFilename(filename);
        final Episode episode = parsedEpisodes.getEpisode();
        final Optional<Episode> optionalConjoinedEpisode = parsedEpisodes.getConjoinedEpisode();

        String titlePrefix = episode.toString();
        String title = titleSeeker.seekEpisodeTitle(episode);
        if (optionalConjoinedEpisode.isPresent()) {
            final Episode conjoinedEpisode = optionalConjoinedEpisode.get();
            titlePrefix += "-" + conjoinedEpisode.getNumberString();
            title += " - " + titleSeeker.seekEpisodeTitle(conjoinedEpisode);
        }
        mTitle = titlePrefix + " - " + title;
        mExtension = filename.substring(filename.lastIndexOf('.') + 1);
        mSubtitlesDirectory = getSubtitlesDirectory(SUBTITLE_DIRECTORY);
        mSubtitlesDirectoryHearingImpaired = getSubtitlesDirectory(SUBTITLE_DIRECTORY_HEARING_IMPAIRED);
        mSubtitlesFilePath = getSubtitlesFilePath(mSubtitlesDirectory);
        mSubtitlesFilePathHearingImpaired = getSubtitlesFilePath(mSubtitlesDirectoryHearingImpaired);
    }

    private File getSubtitlesDirectory(final String subtitleDirectory) {
        return new File(mFile.getParentFile().getPath() + File.separator + subtitleDirectory);
    }

    private String getSubtitlesFilePath(final File subtitlesDirectory) {
        return subtitlesDirectory.getPath() + File.separator + FileManager.cleanFilename(mTitle);
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

        mSubtitlesDirectory.mkdir();
        mSubtitlesDirectoryHearingImpaired.mkdir();

        final List<String> subtitles = seeker.download(subtitleSearchResultsTrimmed);
        subtitleLoop:
        for (int i = 0; i < subtitles.size(); ++i) {
            final SubtitleSearchResult subtitle = subtitleSearchResultsTrimmed.get(i);
            final String baseFilePath = getFilePathForSubtitle(subtitle);
            File subtitlesFile = new File(baseFilePath + SUBTITLE_EXTENSION);

            int attempt = 0;
            while (subtitlesFile.exists()) {
                ++attempt;
                if (attempt == SUBTITLE_LIMIT) {
                    continue subtitleLoop;
                }
                subtitlesFile = new File(baseFilePath + "–" + attempt + SUBTITLE_EXTENSION);
            }

            final String cleanedSubtitle = SubtitleCleaner.clean(subtitles.get(i));
            FileManager.saveFile(subtitlesFile, cleanedSubtitle, subtitle.encoding);
        }
    }

    private String getFilePathForSubtitle(final SubtitleSearchResult subtitle) {
        if (subtitle.hearingImpaired) {
            return mSubtitlesFilePathHearingImpaired;
        } else {
            return mSubtitlesFilePath;
        }
    }

    public boolean hasSubtitles() {
        return new File(mSubtitlesFilePath + SUBTITLE_EXTENSION).exists()
                || new File(mSubtitlesFilePathHearingImpaired + SUBTITLE_EXTENSION).exists();
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
