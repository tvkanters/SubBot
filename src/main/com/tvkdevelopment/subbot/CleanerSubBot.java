package com.tvkdevelopment.subbot;

import java.io.File;

public class CleanerSubBot {

    private static final String CHARACTER_ENCODING = "UTF-8";

    @SuppressWarnings("unused")
    public static void main(final String[] args) {
        if (args.length != 1) {
            System.out.println("Incorrect arguments length");
            return;
        }

        final String contents = FileManager.readFile(args[0], CHARACTER_ENCODING);
        final String cleanedContents = SubtitleCleaner.clean(contents);
        FileManager.saveFile(new File(args[0]), cleanedContents, CHARACTER_ENCODING);
    }

}
