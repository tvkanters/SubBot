package com.tvkdevelopment.subbot;

import java.util.List;
import java.util.regex.Pattern;

import com.tvkdevelopment.subbot.model.SubtitleBlock;
import com.tvkdevelopment.subbot.parsing.SubtitleParser;

public class SubtitleCleaner {

    private static final boolean STRIP_SPAM = true;
    private static final boolean STRIP_CREDITS = true;
    private static final boolean STRIP_HEARING_IMPAIRED = true;
    private static final boolean REPLACEMENTS = true;

    private static final String NEW_LINE = "(?:\\r?\\n)";

    private static final Pattern[] sSpamPatterns = {
            Pattern.compile("Original Air Date", Pattern.CASE_INSENSITIVE),
            Pattern.compile("rate this subtitle", Pattern.CASE_INSENSITIVE),
            Pattern.compile("osdb\\.link", Pattern.CASE_INSENSITIVE),
            Pattern.compile("HumanGuardians.com", Pattern.CASE_INSENSITIVE),
            Pattern.compile("LookLive", Pattern.CASE_INSENSITIVE),
            Pattern.compile("recast\\.ai", Pattern.CASE_INSENSITIVE),
            Pattern.compile("bitninja", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^[ _-]+$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("GOM\\s*Player", Pattern.CASE_INSENSITIVE)
    };

    private static final Pattern[] sCreditsPatterns = {
            Pattern.compile("\\s+by\\s*(?:</font>\\s*)?<font color=", Pattern.CASE_INSENSITIVE),
            Pattern.compile("elderman", Pattern.CASE_INSENSITIVE),
            Pattern.compile("OpenSubtitles", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Subscene", Pattern.CASE_INSENSITIVE),
            Pattern.compile("addic7ed", Pattern.CASE_INSENSITIVE),
            Pattern.compile("honeybunny", Pattern.CASE_INSENSITIVE),
            Pattern.compile("tvsubtitles", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Ripped By", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Sync(?:ed)?[^a-z]+correct(?:ed)?", Pattern.CASE_INSENSITIVE),
            Pattern.compile("piratebay", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?<!a-z)kat\\.", Pattern.CASE_INSENSITIVE)
    };

    private static final Pattern[] sHearingImpairedPatterns = {
            Pattern.compile("(?:<i>)?(?:- ?)?\\[[^\\]]*\\](?:</i>)?" + NEW_LINE + "?", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
            Pattern.compile("(?:<i>)?(?:- ?)?\\([^)]*\\)(?:</i>)?" + NEW_LINE + "?", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
            Pattern.compile("^(?:<i>)?[¶♪♫ \\n\\r-]*(?:</i>)?(?:" + NEW_LINE + "|$)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
    };

    private static final Pattern[] sReplacementPatterns = {
            Pattern.compile("\\. \\. \\."),
            Pattern.compile(" ([?!])", Pattern.CASE_INSENSITIVE)
    };

    private static final String[] sReplacementResults = {
            "...",
            "$1"
    };

    public static String clean(final String subtitle) {
        final List<SubtitleBlock> subtitleBlocks = SubtitleParser.parseSubtitle(subtitle);
        final StringBuilder cleanedSubtitle = new StringBuilder();

        for (final SubtitleBlock subtitleBlock : subtitleBlocks) {
            if (shouldSkip(subtitleBlock)) {
                continue;
            }

            performReplacements(subtitleBlock);

            if (subtitleBlock.getText().isEmpty()) {
                continue;
            }

            cleanedSubtitle.append(subtitleBlock.toString());
        }

        return cleanedSubtitle.toString();
    }

    private static boolean shouldSkip(final SubtitleBlock subtitleBlock) {
        if (STRIP_SPAM && containsPattern(subtitleBlock, sSpamPatterns)) {
            return true;
        }

        if (STRIP_CREDITS && containsPattern(subtitleBlock, sCreditsPatterns)) {
            return true;
        }

        return false;
    }

    private static boolean containsPattern(final SubtitleBlock subtitleBlock, final Pattern[] patterns) {
        for (final Pattern pattern : patterns) {
            if (pattern.matcher(subtitleBlock.getText()).find()) {
                return true;
            }
        }
        return false;
    }

    private static boolean performReplacements(final SubtitleBlock subtitleBlock) {
        if (STRIP_HEARING_IMPAIRED) {
            String strippedText = subtitleBlock.getText();
            for (final Pattern pattern : sHearingImpairedPatterns) {
                strippedText = pattern.matcher(strippedText).replaceAll("").trim();
            }
            subtitleBlock.setText(strippedText);
        }

        if (REPLACEMENTS) {
            String replacedText = subtitleBlock.getText();
            for (int i = 0; i < sReplacementPatterns.length; ++i) {
                replacedText = sReplacementPatterns[i].matcher(replacedText).replaceAll(sReplacementResults[i]);
            }
            subtitleBlock.setText(replacedText);
        }
        return false;
    }

}
