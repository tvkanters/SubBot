package com.tvkdevelopment.subbot;

import java.util.List;
import java.util.regex.Pattern;

import com.tvkdevelopment.subbot.model.SubtitleBlock;
import com.tvkdevelopment.subbot.parsing.SubtitleParser;
import org.intellij.lang.annotations.Language;

public class SubtitleCleaner {

    static boolean STRIP_SPAM = true;
    static boolean STRIP_CREDITS = true;
    static boolean STRIP_HEARING_IMPAIRED = !Config.HEARING_IMPAIRED_MODE;
    static boolean REPLACEMENTS = true;

    private static final String NEW_LINE = "(?:\\r?\\n)";

    private static Pattern regex(@Language("RegExp") final String regex) {
        return regex(regex, false, false);
    }

    private static Pattern regex(@Language("RegExp") final String regex, final boolean caseSensitive, final boolean multiLine) {
        final int flags = (caseSensitive ? 0 : Pattern.CASE_INSENSITIVE)
                | (multiLine ? Pattern.MULTILINE : 0);
        return Pattern.compile(regex, flags);
    }

    private static final Pattern[] sSpamPatterns = {
            regex("Original Air Date"),
            regex("rate this subtitle"),
            regex("osdb\\.link"),
            regex("HumanGuardians.com"),
            regex("LookLive"),
            regex("recast\\.ai"),
            regex("bitninja"),
            regex("^[\\s*_-]+$"),
            regex("GOM\\s*Player"),
            regex("StreamBox"),
            regex("gts-translation\\.com"),
            regex("AmericasCardroom"),
            regex("FlixTor"),
            regex("psagmeno"),
            regex("primewire"),
            regex("CsSubs"),
            regex("ydy[^\\w]*com"),
            regex("piratebay"),
            regex("(?<!a-z)kat\\."),
            regex("swsub.com"),
            regex("StreamingSites"),
            regex("watch.*\\.(?:com|org)"),
            regex("Podnapisi\\.NET"),
            regex("Subtitles? downloaded"),
            regex("bird-hd")
    };

    private static final Pattern[] sCreditsPatterns = {
            regex("\\s+by\\s*(?:</font>\\s*)?<font color="),
            regex("elderman"),
            regex("OpenSubtitles"),
            regex("Subscene"),
            regex("addic7ed"),
            regex("honeybunny"),
            regex("tvsubtitles"),
            regex("Ripped By"),
            regex("Sync(?:ed)?[^a-z]+correct(?:ed)?"),
            regex("subtit[^ ]+? by"),
            regex("sync:"),
            regex("Synchro:"),
            regex("Transcript:")
    };

    private static final Pattern[] sHearingImpairedPatterns = {
            regex("(?:<i>)?(?:- ?)?\\[[^\\]]*\\](?:</i>)?" + NEW_LINE + "?", false, true),
            regex("(?:<i>)?(?:- ?)?\\([^)]*\\)(?:</i>)?" + NEW_LINE + "?", false, true),
            regex("^(?:<i>)?[¶♪♫ \\n\\r-]*(?:</i>)?(?:" + NEW_LINE + "|$)", false, true)
    };

    private static final Pattern[] sReplacementPatterns = {
            regex("\\. \\. \\."), // Ellipsis
            regex("\\(!\\)"), // Singular exclamation
            regex("(\\s|^)([cC])os(\\s|$)"), // cos -> 'cause
            regex(" ([?!])"), // Spaces before exclamation
            regex("([\\n\\r]+|^)-?\\s*_(?=([\\n\\r]+|$))") // Just underscores
    };

    private static final String[] sReplacementResults = {
            "...",
            "",
            "$1'$2ause$3",
            "$1",
            ""
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
