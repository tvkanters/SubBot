package com.tvkdevelopment.subbot;

import java.io.File;

import com.tvkdevelopment.subbot.exceptions.FilenameFormatException;
import com.tvkdevelopment.subbot.exceptions.UnknownEpisodeException;
import com.tvkdevelopment.subbot.exceptions.UnknownSeriesTitleException;
import com.tvkdevelopment.subbot.model.SeriesFile;
import com.tvkdevelopment.subbot.seeking.EpisodeTitleSeeker;
import com.tvkdevelopment.subbot.seeking.OpenSubtitlesSeeker;
import com.tvkdevelopment.subbot.seeking.SubtitlesSeeker;

public class SeriesSubBot {

    private static final boolean INSTALL_SUBTITLES = true;
    private static final boolean RENAME = true;

    @SuppressWarnings("unused")
    public static void main(final String[] args) {
        if (args.length != 1) {
            System.out.println("Incorrect arguments length");
            return;
        }

        final File directory = new File(args[0]);
        final File[] videoFiles = FileManager.getVideoFiles(directory);

        if (videoFiles.length == 0) {
            System.out.println("Empty directory");
            return;
        }

        final SubtitlesSeeker subtitleSeeker = new OpenSubtitlesSeeker();
        final boolean loggedIn = subtitleSeeker.logIn("", "");
        if (!loggedIn) {
            throw new IllegalStateException("Couldn't log in");
        }

        EpisodeTitleSeeker titleSeeker = null;
        for (final File file : videoFiles) {
            final SeriesFile seriesFile;
            try {
                if (titleSeeker == null) {
                    seriesFile = new SeriesFile(file);
                    titleSeeker = seriesFile.getTitleSeeker();
                } else {
                    seriesFile = new SeriesFile(file, titleSeeker);
                }
            } catch (final FilenameFormatException | UnknownSeriesTitleException | UnknownEpisodeException ex) {
                ex.printStackTrace();
                continue;
            }

            if (INSTALL_SUBTITLES && !seriesFile.hasSubtitles()) {
                seriesFile.installSubtitles(subtitleSeeker);
            }

            if (RENAME) {
                seriesFile.rename();
            }

            System.out.println(file.getName() + " -> " + seriesFile.getTitle() + "." + seriesFile.getExtension());
        }

        subtitleSeeker.logOut();
    }

}
