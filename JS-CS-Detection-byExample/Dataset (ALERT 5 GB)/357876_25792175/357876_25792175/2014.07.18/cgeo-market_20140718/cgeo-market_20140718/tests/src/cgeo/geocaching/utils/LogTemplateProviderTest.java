package cgeo.geocaching.utils;

import static org.assertj.core.api.Assertions.assertThat;

import cgeo.geocaching.utils.LogTemplateProvider.LogContext;

import java.util.Calendar;

import junit.framework.TestCase;

public class LogTemplateProviderTest extends TestCase {

    public static void testApplyTemplates() {
        final String noTemplates = " no templates ";
        assertEquals(noTemplates, LogTemplateProvider.applyTemplates(noTemplates, new LogContext(null, null, true)));

        // This test can occasionally fail if the current year changes right after the next line.
        final String currentYear = Integer.toString(Calendar.YEAR);
        assertThat(LogTemplateProvider.applyTemplates("[DATE]", new LogContext(null, null, true)).contains(currentYear)).isTrue();
    }

}
