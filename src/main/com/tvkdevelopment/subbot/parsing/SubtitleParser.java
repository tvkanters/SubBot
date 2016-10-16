package com.tvkdevelopment.subbot.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tvkdevelopment.subbot.model.SubtitleBlock;
import com.tvkdevelopment.subbot.model.SubtitleTimestamp;

public class SubtitleParser {

    private static final String NEW_LINE = "(?:\\r?\\n)";
    private static final String SPACES = "[ \\t]*";
    private static final Pattern sBlockPattern = Pattern.compile(
                        "(?<id>\\d+)" + SPACES + NEW_LINE
                        + "(?<startHours>\\d+):(?<startMinutes>\\d{1,2}):(?<startSeconds>\\d{1,2}),(?<startMilliseconds>\\d{1,3})"
                        + SPACES + "-->" + SPACES
                        + "(?<endHours>\\d+):(?<endMinutes>\\d\\d):(?<endSeconds>\\d\\d),(?<endMilliseconds>\\d\\d\\d)"
                        + SPACES + "(X1:\\d.*?)??" + NEW_LINE + "(?<text>(.|[\\r\\n])*?)"
                        + "(" + NEW_LINE + NEW_LINE + "|" + NEW_LINE + "*$)");

    public static List<SubtitleBlock> parseSubtitle(final String subtitle) {
        final List<SubtitleBlock> subtitleBlocks = new ArrayList<>();

        final Matcher matcher = sBlockPattern.matcher(subtitle);
        while (matcher.find()) {
            final int id = Integer.parseInt(matcher.group("id"));
            final SubtitleTimestamp start = new SubtitleTimestamp(matcher.group("startHours"), matcher.group("startMinutes"), matcher.group("startSeconds"), matcher.group("startMilliseconds"));
            final SubtitleTimestamp end = new SubtitleTimestamp(matcher.group("endHours"), matcher.group("endMinutes"), matcher.group("endSeconds"), matcher.group("endMilliseconds"));
            final String text = matcher.group("text");

            subtitleBlocks.add(new SubtitleBlock(id, start, end, text));
        }

        return subtitleBlocks;
    }

}
