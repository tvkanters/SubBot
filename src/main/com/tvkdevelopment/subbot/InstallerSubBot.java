package com.tvkdevelopment.subbot;

import com.tvkdevelopment.subbot.shell.Registry;
import com.tvkdevelopment.subbot.shell.Shell;

/**
 * The installer for SubBot. Works by registering right click actions for the current location of the running executable
 * (or JAR) file.
 *
 * Due to the chaotic way the registry works with extensions (and my limited understanding of it), this may not always
 * work.
 */
public class InstallerSubBot {

    public static final String MOVIE_COMMAND_NAME = "Download movie subtitles";
    public static final String SERIES_COMMAND_NAME = "Download series subtitles";
    public static final String CLEAN_COMMAND_NAME = "Clean subtitles";

    public static void main(final String[] args) {
        final Shell shell = Shell.getInstance();

        final String movieCommand = getJavaLocation() + " -cp \"" + FileManager.EXE_FILE + "\" " + MoviesSubBot.class.getCanonicalName() + " \"%1\"";
        for (final String extension : FileManager.VIDEO_FILE_EXTENSIONS) {
            shell.registerRightClickCommandForExtension(extension, MOVIE_COMMAND_NAME, movieCommand);
        }

        final String seriesCommand = getJavaLocation() + " -cp \"" + FileManager.EXE_FILE + "\" " + SeriesSubBot.class.getCanonicalName() + " \"%1\"";
        shell.registerRightClickCommandForDirectory(SERIES_COMMAND_NAME, seriesCommand);

        final String cleanCommand = getJavaLocation() + " -cp \"" + FileManager.EXE_FILE + "\" " + CleanerSubBot.class.getCanonicalName() + " \"%1\"";
        shell.registerRightClickCommandForExtension("srt", CLEAN_COMMAND_NAME, cleanCommand);
    }

    private static String getJavaLocation() {
        final String version = Registry.query("HKLM\\Software\\JavaSoft\\Java Runtime Environment", "CurrentVersion");
        final String javaHome = Registry.query("HKLM\\Software\\JavaSoft\\Java Runtime Environment\\" + version, "JavaHome");
        return "\"" + javaHome + "\\bin\\javaw.exe\"";
    }

}
