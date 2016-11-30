/*
 * Copyright 2010 László Balázs-Csíki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor.  If not, see <http://www.gnu.org/licenses/>.
 */
package pixelitor;

import org.jdesktop.swingx.JXTipOfTheDay;
import org.jdesktop.swingx.tips.TipLoader;
import org.jdesktop.swingx.tips.TipOfTheDayModel;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.GUIUtils;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.prefs.Preferences;

/**
 *
 */
public class TipsOfTheDay {
    private static Preferences tipPrefs = AppPreferences.getWindowUserNode();

    private static int nextTip = -1;

    private static final String NEXT_TIP_NR_KEY = "next_tip_nr";

    public static void showTips(JFrame parent, boolean force) {
        try {
            if(nextTip == -1) {
                nextTip = tipPrefs.getInt(NEXT_TIP_NR_KEY, 0);
            }

            TipOfTheDayModel tipOfTheDayModel = loadTipsModel();
            int tipCount = tipOfTheDayModel.getTipCount();
            if(nextTip < 0) {
                nextTip = 0;
            }
            if(nextTip > tipCount - 1) {
                nextTip = tipCount - 1;
            }

            JXTipOfTheDay tipOfTheDay = new JXTipOfTheDay(tipOfTheDayModel);
            tipOfTheDay.setCurrentTip(nextTip);
            tipOfTheDay.showDialog(parent, tipPrefs, force);  // this stops until the user hits close

            int lastTipIndex = tipOfTheDay.getCurrentTip();
            if(lastTipIndex < tipCount - 1) {
                nextTip = lastTipIndex + 1;
            } else {
                nextTip = 0;
            }
        } catch (IOException ex) {
            GUIUtils.showExceptionDialog(ex);
        }
    }

    private static TipOfTheDayModel loadTipsModel() throws IOException {
        Properties properties = new Properties();
        InputStream propertiesIn = PixelitorWindow.class.getResourceAsStream("tips.properties");
        properties.load(propertiesIn);
        return TipLoader.load(properties);
    }

    public static void saveNextTipNr() {
        tipPrefs.putInt(NEXT_TIP_NR_KEY, nextTip);
    }
}
