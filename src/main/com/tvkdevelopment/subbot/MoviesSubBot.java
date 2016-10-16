package com.tvkdevelopment.subbot;

import java.io.File;

import com.tvkdevelopment.subbot.model.MovieFile;
import com.tvkdevelopment.subbot.seeking.OpenSubtitlesSeeker;
import com.tvkdevelopment.subbot.seeking.SubtitlesSeeker;

public class MoviesSubBot {

    private static final boolean INSTALL_SUBTITLES = true;

    @SuppressWarnings("unused")
    public static void main(final String[] args) {
        if (args.length != 1) {
            System.out.println("Incorrect arguments length");
            return;
        }

        final MovieFile movieFile = new MovieFile(new File(args[0]));

        final SubtitlesSeeker subtitleSeeker = new OpenSubtitlesSeeker();
        subtitleSeeker.logIn("", "");

        if (INSTALL_SUBTITLES) {
            System.out.println("Installing subtitles");
            movieFile.installSubtitles(subtitleSeeker);
        }

        subtitleSeeker.logOut();
    }

}
