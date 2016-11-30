package test.net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.Report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.JUnit4TestAdapter;

@RunWith(Parameterized.class)
public class ReadableDurationTest {

    private Integer value;
    private String expected;
    public ReadableDurationTest(String expected, Integer value) {
        this.value = value;
        this.expected = expected;
    }

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
                {"0s", 35},
                {"25s", (25 * 1000)},
                {"5m 0s", (60 * 1000 * 5)},
                {"2h 0m 0s", (60 * 1000 * 120)}
        });
    }

    @Test
    public void test() {
        assertEquals(expected, new Report.ReadableDuration(value).getTime());
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ReadableDurationTest.class);
    }
}
