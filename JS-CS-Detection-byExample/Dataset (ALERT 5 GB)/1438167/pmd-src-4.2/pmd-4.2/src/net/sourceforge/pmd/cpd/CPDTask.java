/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * CPDTask
 * <p/>
 * Runs the CPD utility via ant. The ant task looks like this:
 * <p/>
 * <project name="CPDProj" default="main" basedir=".">
 * <taskdef name="cpd" classname="net.sourceforge.pmd.cpd.CPDTask" />
 * <target name="main">
 * <cpd encoding="UTF-16LE" language="java" ignoreIdentifiers="true" ignoreLiterals="true" minimumTokenCount="100" outputFile="c:\cpdrun.txt">
 * <fileset dir="/path/to/my/src">
 * <include name="*.java"/>
 * </fileset>
 * </cpd>
 * </target>
 * </project>
 * <p/>
 * Required: minimumTokenCount, outputFile, and at least one file
 */
public class CPDTask extends Task {

    private static final String TEXT_FORMAT = "text";
    private static final String XML_FORMAT = "xml";
    private static final String CSV_FORMAT = "csv";

    private String format = TEXT_FORMAT;
    private String language = "java";
    private int minimumTokenCount;
    private boolean ignoreLiterals;
    private boolean ignoreIdentifiers;
    private File outputFile;
    private String encoding = System.getProperty("file.encoding");
    private List<FileSet> filesets = new ArrayList<FileSet>();

    public void execute() throws BuildException {
        try {
            validateFields();

            log("Starting run, minimumTokenCount is " + minimumTokenCount, Project.MSG_INFO);

            log("Tokenizing files", Project.MSG_INFO);
            CPD cpd = new CPD(minimumTokenCount, createLanguage());
            cpd.setEncoding(encoding);
            tokenizeFiles(cpd);

            log("Starting to analyze code", Project.MSG_INFO);
            long timeTaken = analyzeCode(cpd);
            log("Done analyzing code; that took " + timeTaken + " milliseconds");

            log("Generating report", Project.MSG_INFO);
            report(cpd);
        } catch (IOException ioe) {
            log(ioe.toString(), Project.MSG_ERR);
            throw new BuildException("IOException during task execution", ioe);
        } catch (ReportException re) {
            re.printStackTrace();
            log(re.toString(), Project.MSG_ERR);
            throw new BuildException("ReportException during task execution", re);
        }
    }

    private Language createLanguage() {
        Properties p = new Properties();
        if (ignoreLiterals) {
            p.setProperty(JavaTokenizer.IGNORE_LITERALS, "true");
        }
        if (ignoreIdentifiers) {
            p.setProperty(JavaTokenizer.IGNORE_IDENTIFIERS, "true");
        }
        return new LanguageFactory().createLanguage(language, p);
    }

    private void report(CPD cpd) throws ReportException {
        if (!cpd.getMatches().hasNext()) {
            log("No duplicates over " + minimumTokenCount + " tokens found", Project.MSG_INFO);
        }
        Renderer renderer = createRenderer();
        FileReporter reporter;
        if (outputFile == null) {
        	reporter = new FileReporter(encoding);
        } else if (outputFile.isAbsolute()) {
            reporter = new FileReporter(outputFile, encoding);
        } else {
            reporter = new FileReporter(new File(getProject().getBaseDir(), outputFile.toString()), encoding);
        }
        reporter.report(renderer.render(cpd.getMatches()));
    }

    private void tokenizeFiles(CPD cpd) throws IOException {
        for (FileSet fileSet: filesets) {
            DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(getProject());
            String[] includedFiles = directoryScanner.getIncludedFiles();
            for (int i = 0; i < includedFiles.length; i++) {
                File file = new File(directoryScanner.getBasedir() + System.getProperty("file.separator") + includedFiles[i]);
                log("Tokenizing " + file.getAbsolutePath(), Project.MSG_VERBOSE);
                cpd.add(file);
            }
        }
    }

    private long analyzeCode(CPD cpd) {
        long start = System.currentTimeMillis();
        cpd.go();
        long stop = System.currentTimeMillis();
        return stop - start;
    }

    private Renderer createRenderer() {
        if (format.equals(TEXT_FORMAT)) {
            return new SimpleRenderer();
        } else if (format.equals(CSV_FORMAT)) {
            return new CSVRenderer();
        }
        return new XMLRenderer(encoding);
    }

    private void validateFields() throws BuildException {
        if (minimumTokenCount == 0) {
            throw new BuildException("minimumTokenCount is required and must be greater than zero");
        } else if (filesets.isEmpty()) {
            throw new BuildException("Must include at least one FileSet");
        }
    }

    public void addFileset(FileSet set) {
        filesets.add(set);
    }

    public void setMinimumTokenCount(int minimumTokenCount) {
        this.minimumTokenCount = minimumTokenCount;
    }

    public void setIgnoreLiterals(boolean value) {
        this.ignoreLiterals = value;
    }

    public void setIgnoreIdentifiers(boolean value) {
        this.ignoreIdentifiers = value;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public void setFormat(FormatAttribute formatAttribute) {
        format = formatAttribute.getValue();
    }

    public void setLanguage(LanguageAttribute languageAttribute) {
        language = languageAttribute.getValue();
    }

    public void setEncoding(String encodingValue) {
        encoding = encodingValue;
    }

    public static class FormatAttribute extends EnumeratedAttribute {
        private static final String[] FORMATS = new String[]{XML_FORMAT, TEXT_FORMAT, CSV_FORMAT};
        public String[] getValues() {
            return FORMATS;
        }
    }

    /*
     * FIXME Can't we do something cleaner and
     * more dynamic ? Maybe externalise to a properties files that will
     * be generated when building pmd ? This will not have to add manually
     * new language here ?
    */
    public static class LanguageAttribute extends EnumeratedAttribute {
        private static final String[] LANGUAGES = new String[]{"java","jsp","cpp", "c","php", "ruby", "fortran"};
        public String[] getValues() {
            return LANGUAGES;
        }
    }
}
