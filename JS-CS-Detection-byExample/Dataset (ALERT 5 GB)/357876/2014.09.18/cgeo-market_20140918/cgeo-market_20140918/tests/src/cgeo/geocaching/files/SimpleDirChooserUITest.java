package cgeo.geocaching.files;

import static org.assertj.core.api.Assertions.assertThat;

import cgeo.geocaching.Intents;

import com.robotium.solo.Solo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Suppress;
import android.widget.CheckBox;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.FROYO)
@Suppress
public class SimpleDirChooserUITest extends ActivityInstrumentationTestCase2<SimpleDirChooser> {

    private Solo solo;

    public SimpleDirChooserUITest() {
        super(SimpleDirChooser.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        setActivityIntent(new Intent().putExtra(Intents.EXTRA_START_DIR, "").putExtra(SimpleDirChooser.EXTRA_CHOOSE_FOR_WRITING, false));
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public ArrayList<CheckBox> getCurrentCheckBoxes() {
        return solo.getCurrentViews(CheckBox.class);
    }

    public void testSingleSelection() throws InterruptedException {
        // normally our activity should be ready, but we already had Jenkins report no checkboxes right here at the beginning
        solo.waitForActivity(solo.getCurrentActivity().getClass().getSimpleName(), 2000);

        assertChecked("Newly opened activity", 0);
        solo.scrollToBottom();
        pause();
        // according to the documentation, automatic pauses only happen in the clickXYZ() methods.
        // Therefore lets introduce a manual pause after the scrolling methods.

        final int lastIndex = getCurrentCheckBoxes().size() - 1;

        solo.clickOnCheckBox(lastIndex);
        assertThat(solo.isCheckBoxChecked(lastIndex)).isTrue();
        assertThat(solo.isCheckBoxChecked(0)).isFalse();
        assertChecked("Clicked last checkbox", 1);

        solo.scrollUp();
        pause();
        solo.scrollToBottom();
        pause();
        assertChecked("Refreshing last checkbox", 1);

        solo.scrollToTop();
        pause();
        solo.clickOnCheckBox(0);
        assertChecked("Clicked first checkbox", 1);
        assertThat(solo.isCheckBoxChecked(0)).isTrue();
        solo.clickOnCheckBox(1);
        assertChecked("Clicked second checkbox", 1);
        assertThat(solo.isCheckBoxChecked(1)).isTrue();
    }

    private static void pause() throws InterruptedException {
        Thread.sleep(100);
    }

    private void assertChecked(String message, int expectedChecked) {
        final ArrayList<CheckBox> boxes = getCurrentCheckBoxes();
        assertThat(boxes).as("Checkboxes").isNotNull();
        assertThat(boxes.size()).as("number of checkboxes").isGreaterThan(1);
        int checked = 0;
        for (int i = 0; i < boxes.size(); i++) {
            if (solo.isCheckBoxChecked(i)) {
                checked++;
            }
        }
        assertEquals(message, expectedChecked, checked);
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }
}
