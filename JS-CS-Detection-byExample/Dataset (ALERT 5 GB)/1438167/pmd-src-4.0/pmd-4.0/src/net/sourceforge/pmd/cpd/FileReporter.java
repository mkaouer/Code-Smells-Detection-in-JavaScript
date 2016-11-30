/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.*;

/**
 * @author Philippe T'Seyen
 */
public class FileReporter {
    private File reportFile;
    private String encoding;

    public FileReporter(File reportFile) {
        this(reportFile, System.getProperty("file.encoding"));
    }

    public FileReporter(File reportFile, String encoding) {
        if (reportFile == null) throw new NullPointerException("reportFile can not be null");
        this.reportFile = reportFile;
        this.encoding = encoding;
    }

    public void report(String content) throws ReportException {
        try {
            Writer writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reportFile), encoding));
                writer.write(content);
            } finally {
                if (writer != null) writer.close();
            }
        } catch (IOException ioe) {
            throw new ReportException(ioe);
        }
    }
}
