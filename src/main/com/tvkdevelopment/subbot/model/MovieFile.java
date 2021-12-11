package com.tvkdevelopment.subbot.model;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.tvkdevelopment.subbot.FileManager;
import com.tvkdevelopment.subbot.SubtitleCleaner;
import com.tvkdevelopment.subbot.seeking.SubtitlesSeeker.SubtitleSearchResult;
import com.tvkdevelopment.subbot.seeking.SubtitlesSeeker;

public class MovieFile {

    private static final String SUBTITLE_DIRECTORY = "Subtitles";
    private static final String SUBTITLE_DIRECTORY_HEARING_IMPAIRED = "Subtitles HI";
    private static final String SUBTITLE_EXTENSION = ".srt";
    private static final int SUBTITLE_LIMIT = 10;

    private final File mFile;
    private final String mTitle;
    private final String mSubtitlesFilePath;

    public MovieFile(final File file) {
        mFile = file;

        final String filename = file.getName();
        mTitle = filename.substring(0, filename.lastIndexOf('.'));

        final File parentFile = mFile.getParentFile();
        final String parentPath = parentFile.getPath();
        final File subtitlesDirectory = getSubtitlesDirectory(parentPath, SUBTITLE_DIRECTORY);
        final File subtitlesDirectoryHearingImpaired = getSubtitlesDirectory(parentPath, SUBTITLE_DIRECTORY_HEARING_IMPAIRED);

        if (subtitlesDirectory.exists() || subtitlesDirectoryHearingImpaired.exists()) {
            subtitlesDirectory.mkdir();
            subtitlesDirectoryHearingImpaired.mkdir();
            mSubtitlesFilePath = getSubtitlesFilePath(subtitlesDirectory);
        } else {
            mSubtitlesFilePath = getSubtitlesFilePath(parentFile);
        }
    }

    private File getSubtitlesDirectory(final String parentPath, final String subtitleDirectory) {
        return new File(parentPath + File.separator + subtitleDirectory);
    }

    private String getSubtitlesFilePath(final File subtitlesDirectory) {
        return subtitlesDirectory.getPath() + File.separator + FileManager.cleanFilename(mTitle);
    }

    public void installSubtitles(final SubtitlesSeeker seeker) {
        Optional<List<SubtitleSearchResult>> subtitleResults = seeker.search(mFile);
        if (!subtitleResults.isPresent() || subtitleResults.get().isEmpty()) {
            subtitleResults = seeker.search(mFile.getName());
            if (!subtitleResults.isPresent() || subtitleResults.get().isEmpty()) {
                return;
            }
        }

        final List<SubtitleSearchResult> subtitleSearchResultsTrimmed = subtitleResults.get().subList(0,
                Math.min(subtitleResults.get().size(), SUBTITLE_LIMIT));

        final List<String> subtitles = seeker.download(subtitleSearchResultsTrimmed);
        int attempt = 0;
        subtitleLoop:
        for (int i = 0; i < subtitles.size(); ++i) {
            final SubtitleSearchResult subtitle = subtitleSearchResultsTrimmed.get(i);
            final String suffix = getSubtitleSuffix(subtitle);
            File subtitlesFile = new File(mSubtitlesFilePath + suffix);

            while (subtitlesFile.exists()) {
                ++attempt;
                if (attempt == SUBTITLE_LIMIT) {
                    break subtitleLoop;
                }
                subtitlesFile = new File(mSubtitlesFilePath + "–" + attempt + suffix);
            }

            final String cleanedSubtitle = SubtitleCleaner.clean(subtitles.get(i));
            FileManager.saveFile(subtitlesFile, cleanedSubtitle, subtitle.encoding);
        }
    }

    private String getSubtitleSuffix(final SubtitleSearchResult subtitle) {
        final StringBuilder suffix = new StringBuilder();
        if (subtitle.hearingImpaired) {
            suffix.append(".sdh");
        }
        suffix.append(SUBTITLE_EXTENSION);
        return suffix.toString();
    }

}
