package com.tvkdevelopment.subbot;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SubtitleCleanerTest {

    @Test
    public void testIllegalTexts() {
        final String[] illegalStrings = {
                "== sync, corrected by <font color=#00FF00>elderman</font> ==\n<font color=#00FFFF>@elder_man</font>",
                "Support us and become VIP member\nto remove all ads from www.OpenSubtitles.org",
                "<font color=\"#40bfff\">Sync & corrections by</font> <font color=\"#FFA500\">wilson0804</font>",
                "<font color=\"#40bfff\">Addic7ed</font>",
                "<font color=\"#ffff00\" size=14>www.tvsubtitles.net</font>",
                "Sync by: honeybunny and Kerensky",
                "Please rate this subtitle at www.osdb.link/5jstt",
                "Stop terrorism! Boycott the source.\nJoin HumanGuardians.com. It’s free.",
                "<b><font color=\"#004F8C\">Ripped By mstoll</font></b>",
                "Shop this shows fashion, download the\n\"LookLive\" app in iTunes",
                "Synced & corrected by\nGhostedNet",
                "_",
                "- _\n\r- _",
                "© Anosomething\nhttp://thepiratebay.se/something",
                "Downloaded from kat.cr",
                "Want sharper video quality? Want\nclearer audio? Try GOM Player now!",
                "StreamBox Pro - Best box for movies & shows on your TV!\nCheck out the reviews and order on GRATISSTREAMEN.NL",
                "Professional Translation Services\nwww.gts-translation.com",
                "Watch all episodes for free on\nwww.FlixTor.to"
        };
        
        final String expected =
                "919" + System.getProperty("line.separator") +
                "00:53:46,869 --> 00:53:47,970" + System.getProperty("line.separator") +
                "♪ <i>Twerk</i> ♪" + System.getProperty("line.separator") +
                System.getProperty("line.separator");

        for (final String illegalString : illegalStrings) {
            final String result = SubtitleCleaner.clean(expected + wrapSubtitle(illegalString));
            assertEquals(expected, result);
        }
    }

    @Test
    public void testStrippingTextFully() {
        final String[] stringsToRemove = {
                "[music]",
                "- [music]",
                "<i>[music]</i>",
                "<I>- [MUSIC]</I>",
                "- ¶ ¶ ♪ ♫",
                "<i>¶ ¶ ♪ ♫</i>",
                "[blaring continues] <i>¶ ¶</i>",
                "(\"It's The End Of The World As We Know lt\"\nby REM)",
        };
        
        final String expectedSingleBlock =
                "919" + System.getProperty("line.separator") +
                "00:53:46,869 --> 00:53:47,970" + System.getProperty("line.separator") +
                "♪ <i>Twerk</i> ♪" + System.getProperty("line.separator") +
                System.getProperty("line.separator");

        for (final String stringToRemove : stringsToRemove) {
            assertEquals(expectedSingleBlock, SubtitleCleaner.clean(expectedSingleBlock + wrapSubtitle(stringToRemove)));
        }
    }

    @Test
    public void testStrippingTextPartially() {
        final String[] stringsToRemove = {
                "[music]",
                "- [music]",
                "<i>[music]</i>",
                "<I>- [MUSIC]</I>",
                "- ¶ ¶ ♪ ♫",
                "<i>¶ ¶" + System.getProperty("line.separator") + "♪ ♫</i>",
                "<i>[I Monster's" + System.getProperty("line.separator") + "\"Daydream In Blue\" playing]</i>",
                "(!)",
                "- _"
        };

        final String firstBlock =
                "919" + System.getProperty("line.separator") +
                "00:53:46,869 --> 00:53:47,970" + System.getProperty("line.separator") +
                "♪ <i>Twerk</i> ♪" + System.getProperty("line.separator") +
                System.getProperty("line.separator");
        final String expectedBlocks =
                firstBlock +
                "920" + System.getProperty("line.separator") +
                "00:54:46,869 --> 00:54:47,970" + System.getProperty("line.separator") +
                "Boop" + System.getProperty("line.separator") +
                "Bop" + System.getProperty("line.separator") +
                System.getProperty("line.separator");

        for (final String stringToRemove : stringsToRemove) {
            assertEquals(expectedBlocks, SubtitleCleaner.clean(firstBlock + wrapHearingImpaired(stringToRemove)));
        }

        final String expectedBlocksEnd =
                firstBlock +
                "920" + System.getProperty("line.separator") +
                "00:54:46,869 --> 00:54:47,970" + System.getProperty("line.separator") +
                "Boop" + System.getProperty("line.separator") +
                System.getProperty("line.separator");
        assertEquals(expectedBlocksEnd, SubtitleCleaner.clean(firstBlock + wrapSubtitle("Boop" + System.getProperty("line.separator") + "[music]")));

        final String expectedBlocksStart =
                firstBlock +
                "920" + System.getProperty("line.separator") +
                "00:54:46,869 --> 00:54:47,970" + System.getProperty("line.separator") +
                "Bop" + System.getProperty("line.separator") +
                System.getProperty("line.separator");
        assertEquals(expectedBlocksStart, SubtitleCleaner.clean(firstBlock + wrapSubtitle("[music]" + System.getProperty("line.separator") + "Bop")));
    }

    private String wrapSubtitle(final String subtitle) {
        return "920" + System.getProperty("line.separator") +
                "00:54:46,869 --> 00:54:47,970" + System.getProperty("line.separator") +
                subtitle + System.getProperty("line.separator") +
                System.getProperty("line.separator");
    }

    private String wrapHearingImpaired(final String hearingImpairedPiece) {
        return wrapSubtitle("Boop" + System.getProperty("line.separator") +
                hearingImpairedPiece + System.getProperty("line.separator") +
                "Bop");
    }

}
