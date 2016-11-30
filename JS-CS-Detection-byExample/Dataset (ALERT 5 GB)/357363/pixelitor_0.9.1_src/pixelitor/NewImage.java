/*
 * Copyright 2009-2010 László Balázs-Csíki
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

import pixelitor.utils.GridBagHelper;
import pixelitor.utils.IntTextField;
import pixelitor.utils.OKCancelDialog;

import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

public final class NewImage extends JPanel {
    private static int untitledCount = 1;

    public enum BgFill {
        WHITE {
            @Override
            public String toString() {
                return "White";
            }
            @Override
            public Color getColor() {
                return Color.WHITE;
            }
        }, BLACK {
            @Override
            public String toString() {
                return "Black";
            }
            @Override
            public Color getColor() {
                return Color.BLACK;
            }
        }, TRANSPARENT {
            @Override
            public String toString() {
                return "Transparent";
            }
            @Override
            public Color getColor() {
                return null;
            }
        }, FOREGROUND {
            @Override
            public String toString() {
                return "Foreground Color";
            }
            @Override
            public Color getColor() {
                return FgBgColorSelector.getFG();
            }
        }, BACKGROUND {
            @Override
            public String toString() {
                return "Background Color";
            }
            @Override
            public Color getColor() {
                return FgBgColorSelector.getBG();
            }
        };

        public abstract Color getColor();
    }

    private NewImage() {
    }

    public static void addNewImage(BgFill bg, int width, int height, String title) {
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
        fillImage(newImage, bg);
        PixelitorWindow.getInstance().addNewImage(newImage, title);
    }

    private static void fillImage(BufferedImage img, BgFill bg) {
        if (bg == BgFill.TRANSPARENT) {
            return;
        }
        Color c = bg.getColor();
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        Graphics2D g = img.createGraphics();
        g.setColor(c);
        g.fillRect(0, 0, imgWidth, imgHeight);
        g.dispose();
    }

    private static void showInDialog() {
        final NewImagePanel p = new NewImagePanel();
        new OKCancelDialog(p, PixelitorWindow.getInstance(), "New Image", true) {
            @Override
            public void dialogAccepted() {
                int selectedWidth = p.getSelectedWidth();
                int selectedHeight = p.getSelectedHeight();
                BgFill bg = p.getSelectedBackground();

                String title = "Untitled" + untitledCount;
                addNewImage(bg, selectedWidth, selectedHeight, title);
                untitledCount++;

                dispose();
            }

            @Override
            public void dialogCancelled() {
                dispose();
            }
        };
    }

    public static Action getAction() {
        return new AbstractAction("New Image...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInDialog();
            }
        };
    }

    private static class NewImagePanel extends JPanel {
        private final JTextField widthTextField;
        private final JTextField heightTextField;

        private static final int WIDGET_DISTANCE = 5;
        private JComboBox backgroundSelector;

        private NewImagePanel() {
//            setLayout(new GridLayout(3, 2, WIDGET_DISTANCE, WIDGET_DISTANCE));
            setLayout(new GridBagLayout());

            setBorder(BorderFactory.createEmptyBorder(WIDGET_DISTANCE, WIDGET_DISTANCE, WIDGET_DISTANCE, WIDGET_DISTANCE));

            GridBagHelper.addLabel(this, "Width:", 0, 0);
            widthTextField = new IntTextField("600");
            GridBagHelper.addControl(this, widthTextField);

            GridBagHelper.addLabel(this, "Height:", 0, 1);
            heightTextField = new IntTextField("400");
            GridBagHelper.addControl(this, heightTextField);

            GridBagHelper.addLabel(this, "Fill:", 0, 2);
            backgroundSelector = new JComboBox(NewImage.BgFill.values());
            GridBagHelper.addControl(this, backgroundSelector);
        }

        private int getSelectedWidth() {
            return Integer.parseInt(widthTextField.getText());
        }

        private int getSelectedHeight() {
            return Integer.parseInt(heightTextField.getText());
        }

        private NewImage.BgFill getSelectedBackground() {
            return (NewImage.BgFill) backgroundSelector.getSelectedItem();
        }
    }
}

