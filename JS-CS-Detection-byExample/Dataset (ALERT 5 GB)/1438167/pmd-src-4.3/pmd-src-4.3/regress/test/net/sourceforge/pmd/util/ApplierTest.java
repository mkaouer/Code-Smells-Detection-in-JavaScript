/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.util;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.util.Applier;
import net.sourceforge.pmd.util.UnaryFunction;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ApplierTest {

    private static class MyFunction implements UnaryFunction<Object> {
        private boolean gotCallback;

        public void applyTo(Object o) {
            this.gotCallback = true;
        }

        public boolean gotCallback() {
            return this.gotCallback;
        }
    }

    @Test
    public void testSimple() {
        MyFunction f = new MyFunction();
        List<Object> l = new ArrayList<Object>();
        l.add(new Object());
        Applier.apply(f, l.iterator());
        assertTrue(f.gotCallback());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ApplierTest.class);
    }
}
