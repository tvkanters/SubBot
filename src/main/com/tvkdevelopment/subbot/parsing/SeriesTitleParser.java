package com.tvkdevelopment.subbot.parsing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tvkdevelopment.subbot.exceptions.FilenameFormatException;

public class SeriesTitleParser {

    private static final Pattern sTitlePattern =
            Pattern.compile(
                    "\\s*[-.]?\\s*" +                   // Leading space

                    "((?=[^ ]*$)" +                   // If title does not contain a space
                        "(?:" +
                            "[^.]" +                        // Title segment (non-dot)
                        "|" +
                            "\\." +                             // Title segment (prefixed by dot)
                            "(?![A-Z0-9]*\\.)" +                // Title segment (dot not followed by capitalised alphanumerics)
                            "(?!\\d+p)" +                       // Title segment (dot not followed by 720p)
                            "(?![a-z0-9]{4,})" +                // Title segment (dot not followed by lower-case non-article)
                        ")+" +
                    "|" +                               // If title contains a space
                        "(?:" +
                            "[^.]" +                        // Title (non-dot)
                        ")+" +
                    ")"
            );

    private static final Pattern sStripDotsPattern =
            Pattern.compile(
                    "([^.\\s])\\.([^.\\s])"
            );
    private static final String sStripDotsReplacement = "$1 $2";

    private static final int GROUP_TITLE = 1;

    public static String parseFilename(final String filename) throws FilenameFormatException {
        final Matcher matcher = sTitlePattern.matcher(filename);

        if (!matcher.find()) {
            throw new FilenameFormatException(filename);
        }

        final String rawTitle = matcher.group(GROUP_TITLE);
        final String cleanedTitle = sStripDotsPattern.matcher(rawTitle).replaceAll(sStripDotsReplacement).trim();

        return cleanedTitle;
    }

}
