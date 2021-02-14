package com.tvkdevelopment.subbot.seeking;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import com.tvkdevelopment.subbot.Config;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * Used for finding and downloading subtitles of movies and series episodes from OpenSubtitles.
 */
public class OpenSubtitlesSeeker implements SubtitlesSeeker {

    private static final String API_URL = "http://api.opensubtitles.org/xml-rpc";
    private static final String USER_AGENT = "JSubBot";
    private static final String LOGIN_LANGUAGE = "en";
    private static final String SUBTITLE_LANGUAGE = "eng";
    private static final String STATUS_OK = "200 OK";
    private static final int HASH_CHUNK_SIZE = 64 * 1024;

    private static final boolean PRIORITISE_HEARING_IMPAIRED = Config.HEARING_IMPAIRED_MODE;

    private final XmlRpcClient mClient;
    private String mToken = null;

    public OpenSubtitlesSeeker() {
        try {
            final XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(API_URL));
            mClient = new XmlRpcClient();
            mClient.setConfig(config);
        } catch (final MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean logIn(final String user, final String password) {
        if (isLoggedIn()) {
            throw new IllegalStateException("Already logged in");
        }

        try {
            final Object[] params = new Object[] { user, password, LOGIN_LANGUAGE, USER_AGENT };
            final HashMap<?, ?> response = (HashMap<?, ?>) mClient.execute("LogIn", params);
            final String status = (String) response.get("status");
            if (status.equals(STATUS_OK)) {
                mToken = (String) response.get("token");
                System.out.println("Logged in - Token: " + mToken);
                return true;
            } else {
                System.out.println("Logged in failed");
                return false;
            }
        } catch (final XmlRpcException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isLoggedIn() {
        return (mToken != null);
    }

    @Override
    public void logOut() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Not logged in");
        }
        try {
            final Object[] params = new Object[] { mToken };
            mClient.execute("LogOut", params);
            mToken = null;
            System.out.println("Logged out");
        } catch (final XmlRpcException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Converts a video file to the hash that serves as an identifier. Hashing works by getting the first and last
     * number of bytes of the file and pasting them together.
     *
     * @param file The video file to hash
     * @return The video file's hash
     */
    /*package for testing*/ String hashVideoFile(final File file) {
        final FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (final FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        FileChannel channel = null;
        try {
            channel = inputStream.getChannel();
            final long channelSize = channel.size();

            // Files that are too small can't be hashed
            if (channelSize < HASH_CHUNK_SIZE) {
                return String.format("%016x", 0);
            }

            final int channelChunkSize = (int) Math.min(HASH_CHUNK_SIZE, channelSize);
            final long head = getHashChunk(channel, 0, channelChunkSize);
            final long tail = getHashChunk(channel, channelSize - HASH_CHUNK_SIZE, channelChunkSize);

            final String hash = String.format("%016x", channelSize + head + tail);
            System.out.println("Hashed " + file.getName() + " as: " + hash);
            return hash;

        } catch (final IOException ex) {
            throw new RuntimeException(ex);

        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (final IOException ex) {
                    System.err.println("FileInputStream couldn't be closed");
                }
            }
            try {
                inputStream.close();
            } catch (final IOException ex) {
                System.err.println("FileInputStream couldn't be closed");
            }
        }
    }

    private long getHashChunk(final FileChannel channel, final long startPosition, final int chunkSize)
            throws IOException {
        final MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, startPosition, chunkSize);
        final LongBuffer longBuffer = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer();

        long hash = 0;
        while (longBuffer.hasRemaining()) {
            hash += longBuffer.get();
        }
        return hash;
    }

    @Override
    public Optional<List<SubtitleSearchResult>> search(final File file) {
        System.out.println("Searching subtitles for " + file);
        final Map<String, String> movieInfo = new HashMap<>();
        movieInfo.put("moviehash", hashVideoFile(file));
        movieInfo.put("moviebytesize", Long.toString(file.length()));
        movieInfo.put("sublanguageid", SUBTITLE_LANGUAGE);
        return performSearch(movieInfo);
    }

    @Override
    public Optional<List<SubtitleSearchResult>> search(final String query) {
        System.out.println("Searching subtitles for " + query);
        final Map<String, String> movieInfo = new HashMap<>();
        movieInfo.put("query", query);
        movieInfo.put("sublanguageid", SUBTITLE_LANGUAGE);
        return performSearch(movieInfo);
    }

    private Optional<List<SubtitleSearchResult>> performSearch(final Map<String, String> movieInfo) {
        if (!isLoggedIn()) {
            throw new RuntimeException("Not logged in");
        }
        try {
            final Object[] params = new Object[] { mToken, new Object[] { movieInfo } };
            final Map<?, ?> searchResult = (Map<?, ?>) mClient.execute("SearchSubtitles", params);
            return Optional.of(parseSearchResult(searchResult));
        } catch (final XmlRpcException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    private List<SubtitleSearchResult> parseSearchResult(final Map<?, ?> searchResult) {
        final List<SubtitleSearchResult> resultsNonHearingImpaired = new ArrayList<>();
        final List<SubtitleSearchResult> resultsHearingImpaired = new ArrayList<>();
        final Object[] data = (Object[]) searchResult.get("data");
        for (final Object entry : data) {
            final Map<?, ?> subtitle = (Map<?, ?>) entry;
            final int subtitleId = Integer.parseInt((String) subtitle.get("IDSubtitleFile"));
            final boolean hearingImpaired = "1".equals(subtitle.get("SubHearingImpaired"));
            final String encoding = (String) subtitle.get("SubEncoding");

            System.out.println("Adding subtitle " + subtitleId + " with encoding " + encoding + " (" + (hearingImpaired ? "" : "not ") + "hearing impaired)");
            final SubtitleSearchResult result = new SubtitleSearchResult(subtitleId, encoding, hearingImpaired);
            if (hearingImpaired) {
                resultsHearingImpaired.add(result);
            } else {
                resultsNonHearingImpaired.add(result);
            }
        }

        final List<SubtitleSearchResult> results = new ArrayList<>();
        if (PRIORITISE_HEARING_IMPAIRED) {
            results.addAll(resultsHearingImpaired);
            results.addAll(resultsNonHearingImpaired);
        } else {
            results.addAll(resultsNonHearingImpaired);
            results.addAll(resultsHearingImpaired);
        }
        return results;
    }

    @Override
    public List<String> download(final List<SubtitleSearchResult> subtitleSearchResults) {
        final List<Integer> subtitleIds = subtitleSearchResults.stream().map(s -> s.id).collect(Collectors.toList());
        final List<String> subtitles = new ArrayList<>();
        try {
            final Object[] params = new Object[] { mToken, subtitleIds };
            final Map<?, ?> result = (Map<?, ?>) mClient.execute("DownloadSubtitles", params);
            final Object[] data = ((Object[]) result.get("data"));
            for (final Object entry : data) {
                final Map<?, ?> entryMap = ((Map<?, ?>) entry);
                final String encodedCompressedSubtitle = (String) entryMap.get("data");
                final int subtitleId = Integer.parseInt((String) entryMap.get("idsubtitlefile"));

                try {
                    final byte[] decodedCompressedSubtitle = Base64.getDecoder().decode(encodedCompressedSubtitle);
                    final GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(decodedCompressedSubtitle));
                    InputStreamReader inputStreamReader = null;
                    final Optional<SubtitleSearchResult> subtitle = subtitleSearchResults.stream().filter(s -> s.id == subtitleId).findFirst();
                    if (subtitle.isPresent()) {
                        try {
                            inputStreamReader = new InputStreamReader(gzipInputStream, subtitle.get().encoding);
                        } catch (final UnsupportedEncodingException ex) {
                            // Use the default encoding
                        }
                    }
                    if (inputStreamReader == null) {
                        inputStreamReader = new InputStreamReader(gzipInputStream, "UTF-8");
                    }
                    final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    final StringBuilder subtitleBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        subtitleBuilder.append(line).append("\n");
                    }

                    subtitles.add(subtitleBuilder.toString());
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }

            return subtitles;
        } catch (final XmlRpcException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
