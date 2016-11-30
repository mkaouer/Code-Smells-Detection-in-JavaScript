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
package pixelitor.layers;

import pixelitor.Composition;
import pixelitor.filters.comp.Flip;
import pixelitor.history.TranslateEdit;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 */
public class TextLayer extends ContentLayer {
    private static final long serialVersionUID = 2L;

    private String text;
    private int startX = 20;
    private int startY = 70;

    public TextLayer(Composition comp, String name, String text) {
        super(comp, name);
        this.text = text;
    }

    @Override
    public void flip(Flip.Direction direction) {
        // TODO
    }

    @Override
    public void rotate(int angleDegree) {
        // TODO
    }

    @Override
    public Layer duplicate() {
        return new TextLayer(comp, name + " Copy", text);
    }

    @Override
    public boolean notTranslated() {
        return ((translationX == 0) && (translationY == 0));
    }

    @Override
    public void mergeDownOn(Layer bellow) {

    }

    @Override
    public BufferedImage paintLayer(Graphics2D g, boolean firstVisibleLayer, BufferedImage imageSoFar) {
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 42));
        g.setColor(Color.WHITE);
        g.drawString(text, startX + getTranslationX(), startY + getTranslationY());
        return null;
    }

    @Override
    public void enlargeCanvas(int north, int east, int south, int west) {
        // TODO
    }

    @Override
    TranslateEdit createTranslateEdit(int oldTranslationX, int oldTranslationY) {
        return new TranslateEdit(this, null, oldTranslationX, oldTranslationY);
    }
}
