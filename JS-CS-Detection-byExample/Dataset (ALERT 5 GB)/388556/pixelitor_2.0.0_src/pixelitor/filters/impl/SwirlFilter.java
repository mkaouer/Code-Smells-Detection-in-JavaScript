package pixelitor.filters.impl;

import com.jhlabs.image.TransformFilter;

import java.awt.image.BufferedImage;

/**
 * Based on http://stackoverflow.com/questions/225548/resources-for-image-distortion-algorithms
 */
public class SwirlFilter extends TransformFilter {
    private float amount;
    private float radius2;
    private float centerX;
    private float centerY;
    private float divideFactor;
    private int cx;
    private int cy;

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setRadius(float radius) {
        this.radius2 = radius * radius;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        cx = (int) (centerX * src.getWidth());
        cy = (int) (centerY * src.getHeight());

        return super.filter(src, dst);
    }

    @Override
    protected void transformInverse(int x, int y, float[] out) {
        float u, v;
        int dx = x - cx;
        int dy = y - cy;
        float angle = (float) (amount * Math.exp(-(dx * dx + dy * dy) / (radius2)));

        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        u = (float) (cos * dx + sin * dy);
        v = (float) (-sin * dx + cos * dy);
        out[0] = (u + cx) * divideFactor;
        out[1] = (v + cy )* divideFactor;
    }

    public void setDivideFactor(float divideFactor) {
        this.divideFactor = divideFactor;
    }
}
