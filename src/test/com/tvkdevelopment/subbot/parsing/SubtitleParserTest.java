package com.tvkdevelopment.subbot.parsing;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.tvkdevelopment.subbot.exceptions.FilenameFormatException;
import com.tvkdevelopment.subbot.model.SubtitleBlock;
import com.tvkdevelopment.subbot.model.SubtitleTimestamp;

public class SubtitleParserTest {

    @Test
    public void testProperFormat() throws FilenameFormatException {
        final List<SubtitleBlock> result = SubtitleParser.parseSubtitle(
                "1\n" +
                "00:00:05,913 --> 00:00:06,744\n" +
                "<i>He got out.</i>\n" +
                "\n" +
                "2\n" +
                "00:01:06,745 --> 00:01:07,710\n" +
                "What are you talking about?\n" +
                "\n" +
                "3\n" +
                "01:00:07,745 --> 01:00:09,573\n" +
                "The old man made parole.\n" +
                "\n");

        final List<SubtitleBlock> expected = new ArrayList<>();
        expected.add(new SubtitleBlock(1,
                new SubtitleTimestamp("00", "00", "05", "913"),
                new SubtitleTimestamp("00", "00", "06", "744"),
                "<i>He got out.</i>"));
        expected.add(new SubtitleBlock(2,
                new SubtitleTimestamp("00", "01", "06", "745"),
                new SubtitleTimestamp("00", "01", "07", "710"),
                "What are you talking about?"));
        expected.add(new SubtitleBlock(3,
                new SubtitleTimestamp("01", "00", "07", "745"),
                new SubtitleTimestamp("01", "00", "09", "573"),
                "The old man made parole."));

        assertBlocksEqual(expected, result);
    }

    @Test
    public void testSuddenEnd() throws FilenameFormatException {
        final List<SubtitleBlock> result = SubtitleParser.parseSubtitle(
                "1\n" +
                "00:00:05,913 --> 00:00:06,744\n" +
                "<i>He got out.</i>\n" +
                "\n" +
                "2\n" +
                "00:01:06,745 --> 00:01:07,710\n" +
                "What are you talking about?\n" +
                "\n" +
                "3\n" +
                "01:00:07,745 --> 01:00:09,573\n" +
                "The old man made parole.");

        final List<SubtitleBlock> expected = new ArrayList<>();
        expected.add(new SubtitleBlock(1,
                new SubtitleTimestamp("00", "00", "05", "913"),
                new SubtitleTimestamp("00", "00", "06", "744"),
                "<i>He got out.</i>"));
        expected.add(new SubtitleBlock(2,
                new SubtitleTimestamp("00", "01", "06", "745"),
                new SubtitleTimestamp("00", "01", "07", "710"),
                "What are you talking about?"));
        expected.add(new SubtitleBlock(3,
                new SubtitleTimestamp("01", "00", "07", "745"),
                new SubtitleTimestamp("01", "00", "09", "573"),
                "The old man made parole."));

        assertBlocksEqual(expected, result);
    }

    @Test
    public void testShortNumbersEnd() throws FilenameFormatException {
        final List<SubtitleBlock> result = SubtitleParser.parseSubtitle(
                "1\n" +
                "0:0:5,3 --> 00:00:06,744\n" +
                "<i>He got out.</i>");

        final List<SubtitleBlock> expected = new ArrayList<>();
        expected.add(new SubtitleBlock(1,
                new SubtitleTimestamp("00", "00", "05", "003"),
                new SubtitleTimestamp("00", "00", "06", "744"),
                "<i>He got out.</i>"));

        assertBlocksEqual(expected, result);
    }

    @Test
    public void testNewline() throws FilenameFormatException {
        final List<SubtitleBlock> result = SubtitleParser.parseSubtitle(
                "1\n" +
                "00:00:05,913 --> 00:00:06,744\n" +
                "<i>He got out.</i>\n" +
                "\n" +
                "2\n" +
                "00:01:06,745 --> 00:01:07,710\n" +
                "What are you\n" +
                "talking about?\n" +
                "\n" +
                "3\n" +
                "01:00:07,745 --> 01:00:09,573\n" +
                "The old man made parole.\n" +
                "\n");

        final List<SubtitleBlock> expected = new ArrayList<>();
        expected.add(new SubtitleBlock(1,
                new SubtitleTimestamp("00", "00", "05", "913"),
                new SubtitleTimestamp("00", "00", "06", "744"),
                "<i>He got out.</i>"));
        expected.add(new SubtitleBlock(2,
                new SubtitleTimestamp("00", "01", "06", "745"),
                new SubtitleTimestamp("00", "01", "07", "710"),
                "What are you\n" +
                "talking about?"));
        expected.add(new SubtitleBlock(3,
                new SubtitleTimestamp("01", "00", "07", "745"),
                new SubtitleTimestamp("01", "00", "09", "573"),
                "The old man made parole."));

        assertBlocksEqual(expected, result);
    }

    private static void assertBlocksEqual(final List<SubtitleBlock> expected, final List<SubtitleBlock> result) {
        assertEquals("Amount of blocks", expected.size(), result.size());

        for (int i = 0; i < expected.size(); ++i) {
            final SubtitleBlock expectedBlock = expected.get(i);
            final SubtitleBlock resultBlock = result.get(i);
            assertEquals("Block " + i + " ID", expectedBlock.getId(), resultBlock.getId());
            assertEquals("Block " + i + " start", expectedBlock.getStart(), resultBlock.getStart());
            assertEquals("Block " + i + " end", expectedBlock.getEnd(), resultBlock.getEnd());
            assertEquals("Block " + i + " text", expectedBlock.getText(), resultBlock.getText());
        }
    }

}
