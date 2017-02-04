package com.tvkdevelopment.subbot;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class CleanerSubBot {

    private static final String[] CHARACTER_ENCODING = {
            StandardCharsets.UTF_8.name(),
            StandardCharsets.ISO_8859_1.name()
    };

    @SuppressWarnings("unused")
    public static void main(final String[] args) {
        if (args.length != 1) {
            System.out.println("Incorrect arguments length");
            return;
        }

        for (final String characterEncoding : CHARACTER_ENCODING) {
            final String contents = FileManager.readFile(args[0], characterEncoding);
            if (contents == null) {
                System.out.println("Encoding " + characterEncoding + " invalid");
                continue;
            }
            final String cleanedContents = SubtitleCleaner.clean(contents);
            FileManager.saveFile(new File(args[0]), cleanedContents, characterEncoding);
        }
    }

}
