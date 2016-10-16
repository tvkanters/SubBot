package com.tvkdevelopment.subbot.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

/**
 * A base class for OS-specific shell functionality.
 */
@SuppressWarnings("StaticInitializerReferencesSubClass")
public abstract class Shell {

    /** The OS specific shell */
    private final static Shell sInstance;

    static {
        // Instantiate an OS specific implementation
        final String OS = System.getProperty("os.name", "generic").toLowerCase();
        if ((OS.contains("mac")) || (OS.contains("darwin"))) {
            throw new RuntimeException("Mac not supported");
        } else if (OS.contains("win")) {
            sInstance = new WindowsShell();
        } else if (OS.contains("nux")) {
            throw new RuntimeException("Linux not supported");
        } else {
            sInstance = null;
        }

        if (sInstance == null) {
            throw new UnsupportedOperationException("Your operating system is not supported");
        }
    }

    /**
     * @return The singleton instance of the OS specific shell
     */
    public static Shell getInstance() {
        return sInstance;
    }

    /**
     * Executes the shell command given and waits for it to finish so that it may return the result. Lines are separated
     * by \n character. The returned value does not end with a \n char.
     *
     * @param command
     *            The command to execute.
     *
     * @return The output of the shell command
     */
    public String executeCommandForResult(final String command) {
        try {
            final ProcessBuilder builder = getProcessBuilder(command);
            builder.redirectErrorStream(true);
            final Process process = builder.start();
            final BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

            final StringBuilder output = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
            in.close();

            return output.toString().trim();

        } catch (final IOException | InterruptedException ex) {
            throw new RuntimeException("Couldn't execute command", ex);
        }
    }

    /**
     * Executes a command as administrator.
     *
     * @param command
     *            The command to execute
     * @param args
     *            The arguments to pass to the command
     *
     * @return True iff the command could be executed as administrator
     */
    public abstract boolean executeAsAdministrator(final String command, final String args);

    /**
     * Executes the shell command given and waits for it to finish.
     *
     * @param command
     *            The command to execute.
     */
    public void executeCommand(final String command) {
        try {
            getProcessBuilder(command).start().waitFor();
        } catch (final IOException | InterruptedException ex) {
            throw new RuntimeException("Couldn't execute command", ex);
        }
    }

    /**
     * Creates a process builder for the given command.
     *
     * @param command
     *            The command that can be executed within a console
     *
     * @return The process builder to execute the command
     */
    protected abstract ProcessBuilder getProcessBuilder(String command);

    /**
     * Creates a right click action for a file with a certain extension.
     *
     * @param extension
     *            The extension to register the right click action for
     * @param name
     *            The name for the right click action
     * @param command
     *            The command to execute when the user clicks the right click action
     */
    public abstract void registerRightClickCommandForExtension(String extension, String name, String command);

    /**
     * Creates a right click action for folders.
     *
     * @param name
     *            The name for the right click action
     * @param command
     *            The command to execute when the user clicks the right click action
     */
    public abstract void registerRightClickCommandForDirectory(String name, String command);

    /**
     * Removes a right click action for a file with a certain extension.
     *
     * @param extension
     *            The extension to remove the right click action for
     * @param name
     *            The name for the right click action
     */
    public abstract void unregisterRightClickCommandForExtension(String extension, String name);

    /**
     * Removes a right click action for folders.
     *
     * @param name
     *            The name for the right click action
     */
    public abstract void unregisterRightClickCommandForDirectory(String name);

}
