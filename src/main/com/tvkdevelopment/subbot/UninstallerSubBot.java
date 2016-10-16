package com.tvkdevelopment.subbot;

import com.tvkdevelopment.subbot.shell.Shell;

public class UninstallerSubBot {

    public static void main(final String[] args) {
        final Shell shell = Shell.getInstance();

        for (final String extension : FileManager.VIDEO_FILE_EXTENSIONS) {
            shell.unregisterRightClickCommandForExtension(extension, InstallerSubBot.MOVIE_COMMAND_NAME);
        }

        shell.unregisterRightClickCommandForDirectory(InstallerSubBot.SERIES_COMMAND_NAME);
    }

}
