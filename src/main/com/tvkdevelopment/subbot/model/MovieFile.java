package com.tvkdevelopment.subbot.model;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.tvkdevelopment.subbot.FileManager;
import com.tvkdevelopment.subbot.SubtitleCleaner;
import com.tvkdevelopment.subbot.seeking.SubtitlesSeeker.SubtitleSearchResult;
import com.tvkdevelopment.subbot.seeking.SubtitlesSeeker;

public class MovieFile {

    private static String SUBTITLE_DIRECTORY = "Subtitles";
    private static String SUBTITLE_EXTENSION = ".srt";
    private static int SUBTITLE_LIMIT = 10;

    private final File mFile;
    private final String mFilename;
    private final String mTitle;
    private final File mSubtitlesDirectory;
    private final String mSubtitlesFilePath;

    public MovieFile(final File file) {
        mFile = file;
        mFilename = file.getName();

        mTitle = mFilename.substring(0, mFilename.lastIndexOf('.'));

        final String parentPath = mFile.getParentFile().getPath();
        mSubtitlesDirectory = new File(parentPath + File.separator + SUBTITLE_DIRECTORY);
        if (mSubtitlesDirectory.exists()) {
            mSubtitlesFilePath = mSubtitlesDirectory.getPath() + File.separator + mTitle;
        } else {
            mSubtitlesFilePath = parentPath + File.separator + mTitle;
        }
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
            File subtitlesFile = new File(mSubtitlesFilePath + SUBTITLE_EXTENSION);

            while (subtitlesFile.exists()) {
                ++attempt;
                if (attempt == SUBTITLE_LIMIT) {
                    break subtitleLoop;
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

}
