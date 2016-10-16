package com.tvkdevelopment.subbot.parsing;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tvkdevelopment.subbot.exceptions.FilenameFormatException;
import com.tvkdevelopment.subbot.model.Episode;

public class EpisodeParser {

    private static final Pattern[] sEpisodePatterns = {
            regex("S(\\d?\\d)E(\\d?\\d)(?:-?E?(\\d\\d))?"),
            regex("(\\d?\\d) Episode (\\d?\\d)(?:-(\\d?\\d))?"),
            regex("(\\d?\\d)x(\\d?\\d)(?:-?(\\d\\d))?"),
            regex("(\\d)(\\d\\d)")
    };

    private static Pattern regex(final String string) {
        return Pattern.compile(string, Pattern.CASE_INSENSITIVE);
    }

    private static final int GROUP_SEASON = 1;
    private static final int GROUP_NUMBER = 2;
    private static final int GROUP_CONJOINED_NUMBER = 3;

    public static Result parseFilename(final String filename) throws FilenameFormatException {
        Episode episode = null;
        Optional<Episode> conjoinedEpisode = null;
        int matchStart = -1;
        int matchEnd = -1;

        boolean found = false;
        for (final Pattern episodePattern : sEpisodePatterns) {
            final Matcher matcher = episodePattern.matcher(filename);
            if (!matcher.find()) {
                continue;
            }

            try {
                final int season = Integer.parseInt(matcher.group(GROUP_SEASON));
                final int episodeNumber = Integer.parseInt(matcher.group(GROUP_NUMBER));
                episode = new Episode(season, episodeNumber);

                conjoinedEpisode = Optional.empty();
                if (matcher.groupCount() > 2) {
                    final String conjoinedNumberGroup = matcher.group(GROUP_CONJOINED_NUMBER);
                    if (conjoinedNumberGroup != null) {
                        final int conjoinedNumber = Integer.parseInt(conjoinedNumberGroup);
                        conjoinedEpisode = Optional.of(new Episode(season, conjoinedNumber));
                    }
                }
            } catch (final NumberFormatException ex) {
                continue;
            }

            matchStart = matcher.start();
            matchEnd = matcher.end();
            found = true;
            break;
        }

        if (!found) {
            throw new FilenameFormatException(filename);
        }

        return new Result(episode, conjoinedEpisode, matchStart, matchEnd);
    }

    public static class Result {

        private final Episode mEpisode;
        private final Optional<Episode> mConjoinedEpisode;
        private final int mMatchEnd;
        private final int mMatchStart;

        public Result(final Episode episode, final Optional<Episode> conjoinedEpisode, final int matchStart, final int matchEnd) {
            mEpisode = episode;
            mConjoinedEpisode = conjoinedEpisode;
            mMatchStart = matchStart;
            mMatchEnd = matchEnd;
        }

        public Episode getEpisode() {
            return mEpisode;
        }

        public Optional<Episode> getConjoinedEpisode() {
            return mConjoinedEpisode;
        }

        public int getMatchStart() {
            return mMatchStart;
        }

        public int getMatchEnd() {
            return mMatchEnd;
        }

        @Override
        public String toString() {
            return mEpisode + " - " + (mConjoinedEpisode.isPresent() ? mConjoinedEpisode.get() : "None") + " - " + mMatchEnd;
        }

    }

}
