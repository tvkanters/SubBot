package com.tvkdevelopment.subbot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.regex.Pattern;

public class FileManager {

    /** The location of the currently running executable files (also works for JARs) */
    public static final File EXE_FILE = new File(InstallerSubBot.class.getProtectionDomain().getCodeSource().getLocation().getPath());

    public static final String[] VIDEO_FILE_EXTENSIONS = { "avi", "mp4", "mkv", "wmv", "mov", "qt", "mpg", "mp2", "mpeg", "mpe", "mpv", "m2v" };

    /** A regular expression that matches file names of video files */
    private static final Pattern sVideoFilePattern;

    static {
        final StringBuilder videoFilePatternBuilder = new StringBuilder();
        videoFilePatternBuilder.append("\\.(");
        videoFilePatternBuilder.append(VIDEO_FILE_EXTENSIONS[0]);
        for (int i = 1; i < VIDEO_FILE_EXTENSIONS.length; ++i) {
            videoFilePatternBuilder.append('|').append(VIDEO_FILE_EXTENSIONS[i]);
        }
        videoFilePatternBuilder.append(")$");
        sVideoFilePattern = Pattern.compile(videoFilePatternBuilder.toString(), Pattern.CASE_INSENSITIVE);
    }

    public static File[] getVideoFiles(final File directory) {
        return directory.listFiles(filename -> sVideoFilePattern.matcher(filename.getName()).find());
    }

    public static void saveFile(final File file, final String contents, final String characterEncoding) {
        Writer fileWriter = null;
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(file);

            Writer outputStreamWriter;
            try {
                outputStreamWriter = new OutputStreamWriter(fileOutputStream, characterEncoding);
            } catch (final UnsupportedEncodingException ex) {
                outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            }

            fileWriter = new BufferedWriter(outputStreamWriter);
            fileWriter.write(contents);
        } catch (final IOException ex) {
            ex.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (final IOException ignored) {}
            }
        }
    }

    public static String cleanFilename(final String name) {
        return name.replaceAll("[\u0001-\u001f<>:\"/\\\\|?*\u007f]+", "").trim();
    }

}
