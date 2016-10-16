package com.tvkdevelopment.subbot.shell;

import com.sun.jna.Shell32X;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;

/**
 * A class for Windows-specific shell functionality.
 */
/*package*/ class WindowsShell extends Shell {

    /*package*/ WindowsShell() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean executeAsAdministrator(final String command, final String args) {
        final Shell32X.SHELLEXECUTEINFO execInfo = new Shell32X.SHELLEXECUTEINFO();
        execInfo.lpFile = new WString(command);
        if (args != null) execInfo.lpParameters = new WString(args);
        execInfo.nShow = Shell32X.SW_HIDE;
        execInfo.fMask = Shell32X.SEE_MASK_NOCLOSEPROCESS;
        execInfo.lpVerb = new WString("runas");
        final boolean success = Shell32X.INSTANCE.ShellExecuteEx(execInfo);

        if (!success) {
            final int lastError = Kernel32.INSTANCE.GetLastError();
            final String errorMessage = Kernel32Util.formatMessageFromLastErrorCode(lastError);
            System.err.println("Error performing elevation: " + lastError + ": " + errorMessage + " (apperror="
                    + execInfo.hInstApp + ")");
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ProcessBuilder getProcessBuilder(final String command) {
        return new ProcessBuilder("cmd", "/C", command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRightClickCommandForExtension(final String extension, final String name, final String command) {
        final String path = getExtensionShellPath(extension) + "\\" + name + "\\command";

        Registry.addKeysRecursive(path);
        Registry.addDefaultString(path, command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRightClickCommandForDirectory(final String name, final String command) {
        final String path = "HKEY_CLASSES_ROOT\\Directory\\shell\\" + name + "\\command";

        Registry.addKeysRecursive(path);
        Registry.addDefaultString(path, command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterRightClickCommandForExtension(final String extension, final String name) {
        final String path = getExtensionShellPath(extension) + "\\" + name;
        Registry.delete(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterRightClickCommandForDirectory(final String name) {
        final String path = "HKEY_CLASSES_ROOT\\Directory\\shell\\" + name;
        Registry.delete(path);
    }

    private String getExtensionShellPath(final String extension) {
        final String basePath = "HKEY_CLASSES_ROOT";
        final String keyExtension = (extension.charAt(0) != '.' ? "." : "") + extension;
        String path = basePath + "\\" + keyExtension;

        // TODO: Find a better way to determine the ACTUAL location
        String resolvedExtension = Registry.query(path, "Winamp_Back");
        if (resolvedExtension != null && (!resolvedExtension.startsWith("(") || !resolvedExtension.endsWith(")"))) {
            path = basePath + "\\" + resolvedExtension;
        } else {
            resolvedExtension = Registry.query(path, "");
            if (resolvedExtension != null && (!resolvedExtension.startsWith("(") || !resolvedExtension.endsWith(")"))) {
                path = basePath + "\\" + resolvedExtension;
            }
        }

        path += "\\shell";

        return path;
    }

}
