/*
 * Copyright 2009 László Balázs-Csíki
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

package pixelitor.levels;

import java.awt.Color;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pixelitor.filters.LinearIntParam;
import pixelitor.utils.SliderSpinner;

public class LevelsAdjustments extends OneChannelLevelsPanel implements ChangeListener {
    private static final int BLACK_DEFAULT = 0;
    private static final int WHITE_DEFAULT = 255;

    private static final Color DARK_CYAN = new Color(0, 128, 128);
    private static final Color LIGHT_PINK = new Color(255, 128, 128);

    private static final Color DARK_PURPLE = new Color(128, 0, 128);
    private static final Color LIGHT_GREEN = new Color(128, 255, 128);

    private static final Color DARK_YELLOW_GREEN = new Color(128, 128, 0);
    private static final Color LIGHT_BLUE = new Color(128, 128, 255);

    private static final Color DARK_BLUE = new Color(0, 0, 128);
    private static final Color LIGHT_YELLOW = new Color(255, 255, 128);

    private static final Color DARK_GREEN = new Color(0, 128, 0);
    private static final Color LIGHT_PURPLE = new Color(255, 128, 255);

    private static final Color DARK_RED = new Color(128, 0, 0);
    private static final Color LIGHT_CYAN = new Color(128, 255, 128);


    enum Type {
        RGB {
            @Override
            public String getName() {
                return "red, green, blue";
            }
            @Override
            Color getBackColor() {
                return Color.BLACK;
            }
            @Override
            Color getWhiteColor() {
                return Color.WHITE;
            }
        }, R {
            @Override
            public String getName() {
                return "red";
            }
            @Override
            Color getBackColor() {
                return DARK_CYAN;
            }
            @Override
            Color getWhiteColor() {
                return LIGHT_PINK;
            }
        }, G {
            @Override
            public String getName() {
                return "green";
            }
            @Override
            Color getBackColor() {
                return DARK_PURPLE;
            }
            @Override
            Color getWhiteColor() {
                return LIGHT_GREEN;
            }
        }, B {
            @Override
            public String getName() {
                return "blue";
            }
            @Override
            Color getBackColor() {
                return DARK_YELLOW_GREEN;
            }
            @Override
            Color getWhiteColor() {
                return LIGHT_BLUE;
            }
        }, RG {
            @Override
            public String getName() {
                return "red, green";
            }
            @Override
            Color getBackColor() {
                return DARK_BLUE;
            }
            @Override
            Color getWhiteColor() {
                return LIGHT_YELLOW;
            }
        }, RB {
            @Override
            public String getName() {
                return "red, blue";
            }
            @Override
            Color getBackColor() {
                return DARK_GREEN;
            }
            @Override
            Color getWhiteColor() {
                return LIGHT_PURPLE;
            }
        }, GB {
            @Override
            public String getName() {
                return "green, blue";
            }
            @Override
            Color getBackColor() {
                return DARK_RED;
            }
            @Override
            Color getWhiteColor() {
                return LIGHT_CYAN;
            }
        };

        abstract String getName();
        abstract Color getBackColor();
        abstract Color getWhiteColor();
    }

    private int inputBlackValue = BLACK_DEFAULT;
    private int inputWhiteValue = WHITE_DEFAULT;
    private int outputBlackValue = BLACK_DEFAULT;
    private int outputWhiteValue = WHITE_DEFAULT;

    private SliderSpinner inputBlackSlider;
    private SliderSpinner inputWhiteSlider;
    private SliderSpinner outputBlackSlider;
    private SliderSpinner outputWhiteSlider;
    private final GrayScaleAdjustmentChangeListener grayScaleAdjustmentChangeListener;

    public LevelsAdjustments(Type type, GrayScaleAdjustmentChangeListener grayScaleAdjustmentChangeListener) {
        super(type.getName());
        this.grayScaleAdjustmentChangeListener = grayScaleAdjustmentChangeListener;


        LinearIntParam inputBlackParam = new LinearIntParam("input black", 0, 255, BLACK_DEFAULT);
        inputBlackSlider = new SliderSpinner(Color.GRAY, type.getBackColor(),  inputBlackParam, this);

        LinearIntParam inputWhiteParam = new LinearIntParam("input white", 0, 255, WHITE_DEFAULT);
        inputWhiteSlider = new SliderSpinner(type.getWhiteColor(), Color.GRAY, inputWhiteParam, this);

        LinearIntParam outputBlackParam = new LinearIntParam("output black", 0, 255, BLACK_DEFAULT);
        outputBlackSlider = new SliderSpinner(Color.GRAY, type.getWhiteColor(), outputBlackParam, this);

        LinearIntParam outputWhiteParam = new LinearIntParam("output white", 0, 255, WHITE_DEFAULT);
        outputWhiteSlider = new SliderSpinner(type.getBackColor(), Color.GRAY, outputWhiteParam, this);

        addSliderSpinner(inputBlackSlider);
        addSliderSpinner(inputWhiteSlider);
        addSliderSpinner(outputBlackSlider);
        addSliderSpinner(outputWhiteSlider);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        SliderSpinner source = (SliderSpinner) e.getSource();
        if (source == inputBlackSlider) {
            inputBlackValue = source.getCurrentValue();
        } else if (source == inputWhiteSlider) {
            inputWhiteValue = source.getCurrentValue();
        } else if (source == outputBlackSlider) {
            outputBlackValue = source.getCurrentValue();
        } else if (source == outputWhiteSlider) {
            outputWhiteValue = source.getCurrentValue();
        }

        adjustment = new GrayScaleLookup(inputBlackValue, inputWhiteValue, outputBlackValue, outputWhiteValue);
        grayScaleAdjustmentChangeListener.grayScaleAdjustmentHasChanged();
    }

}
