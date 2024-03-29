package com.tvkdevelopment.subbot;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class SubtitleCleanerTest {

    private static final String NEW_LINE = System.getProperty("line.separator");

    @Before
    public void setUp() {
        SubtitleCleaner.STRIP_SPAM = true;
        SubtitleCleaner.STRIP_CREDITS = true;
        SubtitleCleaner.STRIP_HEARING_IMPAIRED = true;
        SubtitleCleaner.REPLACEMENTS = true;
    }

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
                "Watch all episodes for free on\nwww.FlixTor.to",
                "<font color=#0000FF>ENGLISH SUBTITLE BY :\nFRIDODIDO</font>",
                "primewire.ag is back!\nStream any Movie or TV show",
                "sync:fisherchen\nRe-edit & ProofRead by SooN (MSN: ts_leo@hotmail.com)\nwww.ydy.com",
                "Transcript: ydy.com - Synchro: jh26",
                "Synchro: K!r!lleXXI\n[WEB-DL 720p LinkinPark]",
                "<font color=#38B0DE>-=www.ydy.com/bbs=-Proudly Presents</font>",
                "<font color=#FFFF00>www. ydy. com proudly presents</font>",
                "–=www.ydy.com/bbs=-Proudly Presents",
                "Host: www.CsSubs.org - Thanks Guys",
                "<i>Transcript: swsub.com</i>",
                "Watch free HD Movies and TV Shows at\nStreamingSites.com",
                "Subtitles downloaded from Podnapisi.NET",
                "Visit bird-hd.info for more m720p Movies Encoded By BiRD",
                "iSubDB.com - fast, modern, simple\nSubtitles search by drag & drop",
                "Captioned by So and So",
                "Subtitle translation by: Qianni Lu",
                "Provided by explosiveskull\nhttps://twitter.com/kaboomskull"
        };
        
        final String expected =
                "919" + NEW_LINE +
                "00:53:46,869 --> 00:53:47,970" + NEW_LINE +
                "♪ <i>Twerk</i> ♪" + NEW_LINE +
                NEW_LINE;

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
                "919" + NEW_LINE +
                "00:53:46,869 --> 00:53:47,970" + NEW_LINE +
                "♪ <i>Twerk</i> ♪" + NEW_LINE +
                NEW_LINE;

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
                "<i>¶ ¶" + NEW_LINE + "♪ ♫</i>",
                "<i>[I Monster's" + NEW_LINE + "\"Daydream In Blue\" playing]</i>",
                "(!)",
                "- _"
        };

        final String firstBlock =
                "919" + NEW_LINE +
                "00:53:46,869 --> 00:53:47,970" + NEW_LINE +
                "♪ <i>Twerk</i> ♪" + NEW_LINE +
                NEW_LINE;
        final String expectedBlocks =
                firstBlock +
                "920" + NEW_LINE +
                "00:54:46,869 --> 00:54:47,970" + NEW_LINE +
                "Boop" + NEW_LINE +
                "Bop" + NEW_LINE +
                NEW_LINE;

        for (final String stringToRemove : stringsToRemove) {
            assertEquals(expectedBlocks, SubtitleCleaner.clean(firstBlock + wrapHearingImpaired(stringToRemove)));
        }

        final String expectedBlocksEnd =
                firstBlock +
                "920" + NEW_LINE +
                "00:54:46,869 --> 00:54:47,970" + NEW_LINE +
                "Boop" + NEW_LINE +
                NEW_LINE;
        assertEquals(expectedBlocksEnd, SubtitleCleaner.clean(firstBlock + wrapSubtitle("Boop" + NEW_LINE + "[music]")));

        final String expectedBlocksStart =
                firstBlock +
                "920" + NEW_LINE +
                "00:54:46,869 --> 00:54:47,970" + NEW_LINE +
                "Bop" + NEW_LINE +
                NEW_LINE;
        assertEquals(expectedBlocksStart, SubtitleCleaner.clean(firstBlock + wrapSubtitle("[music]" + NEW_LINE + "Bop")));
    }

    private String wrapSubtitle(final String subtitle) {
        return "920" + NEW_LINE +
                "00:54:46,869 --> 00:54:47,970" + NEW_LINE +
                subtitle + NEW_LINE +
                NEW_LINE;
    }

    private String wrapHearingImpaired(final String hearingImpairedPiece) {
        return wrapSubtitle("Boop" + NEW_LINE +
                hearingImpairedPiece + NEW_LINE +
                "Bop");
    }

}
