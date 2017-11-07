package com.deans.data.generator.common;

public class IOUtils {

    public static void closeAll(AutoCloseable... closeables) {
        if (closeables == null || closeables.length == 0) {
            return;
        }
        for (AutoCloseable closeable : closeables) {
            closeQuietly(closeable);
        }
    }

    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
                // ignored
            }
        }
    }
}
