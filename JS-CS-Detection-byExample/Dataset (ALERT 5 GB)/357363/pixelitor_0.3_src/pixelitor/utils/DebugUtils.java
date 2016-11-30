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

package pixelitor.utils;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public final class DebugUtils {
    // this is a utility class with static methods, it should not be instantiated
    private DebugUtils() {
    }

    public static String getBufferedImageTypeDescription(int type) {
        String retVal = "";
        switch (type) {
            case BufferedImage.TYPE_3BYTE_BGR:
                retVal = "TYPE_3BYTE_BGR";
                break;
            case BufferedImage.TYPE_4BYTE_ABGR:
                retVal = "TYPE_4BYTE_ABGR";
                break;
            case BufferedImage.TYPE_4BYTE_ABGR_PRE:
                retVal = "TYPE_4BYTE_ABGR_PRE";
                break;
            case BufferedImage.TYPE_BYTE_BINARY:
                retVal = "TYPE_BYTE_BINARY";
                break;
            case BufferedImage.TYPE_BYTE_GRAY:
                retVal = "TYPE_BYTE_GRAY";
                break;
            case BufferedImage.TYPE_BYTE_INDEXED:
                retVal = "TYPE_BYTE_INDEXED";
                break;
            case BufferedImage.TYPE_CUSTOM:
                retVal = "TYPE_CUSTOM";
                break;
            case BufferedImage.TYPE_INT_ARGB:
                retVal = "TYPE_INT_ARGB";
                break;
            case BufferedImage.TYPE_INT_ARGB_PRE:
                retVal = "TYPE_INT_ARGB_PRE";
                break;
            case BufferedImage.TYPE_INT_BGR:
                retVal = "TYPE_INT_BGR";
                break;
            case BufferedImage.TYPE_INT_RGB:
                retVal = "TYPE_INT_RGB";
                break;
            case BufferedImage.TYPE_USHORT_555_RGB:
                retVal = "TYPE_USHORT_555_RGB";
                break;
            case BufferedImage.TYPE_USHORT_565_RGB:
                retVal = "TYPE_USHORT_565_RGB";
                break;
            case BufferedImage.TYPE_USHORT_GRAY:
                retVal = "TYPE_USHORT_GRAY";
                break;
            default:
                retVal = "unrecognized (program error)";
                break;
        }
        return retVal;
    }

    public static String getBufferedImageDescription(BufferedImage image) {
        if (image == null) {
            return "null";
        }
        String retVal = "{\n    type = ";
        retVal += getBufferedImageTypeDescription(image.getType());
        int width = image.getWidth();
        retVal += ("\n    width = " + width);
        int height = image.getHeight();
        retVal += ("\n    height = " + height);
        boolean alphaPremultiplied = image.isAlphaPremultiplied();
        retVal += ("\n    alphaPremultiplied = " + alphaPremultiplied);

        ColorModel colorModel = image.getColorModel();
        WritableRaster raster = image.getRaster();
        retVal += ("\n    raster = " + getWritebleRasterDescription(raster));
        retVal += ("\n    colorModel = " + getColorModelDescription(colorModel, 2));
        retVal += "\n  }";
        return retVal;
    }


    public static String getColorSpaceTypeDescription(int type) {
        switch(type) {
            case ColorSpace.TYPE_2CLR: return "TYPE_2CLR";
            case ColorSpace.TYPE_3CLR: return "TYPE_3CLR";
            case ColorSpace.TYPE_4CLR: return "TYPE_4CLR";
            case ColorSpace.TYPE_5CLR: return "TYPE_5CLR";
            case ColorSpace.TYPE_6CLR: return "TYPE_6CLR";
            case ColorSpace.TYPE_7CLR: return "TYPE_7CLR";
            case ColorSpace.TYPE_8CLR: return "TYPE_8CLR";
            case ColorSpace.TYPE_9CLR: return "TYPE_9CLR";
            case ColorSpace.TYPE_ACLR: return "TYPE_ACLR";
            case ColorSpace.TYPE_BCLR: return "TYPE_BCLR";
            case ColorSpace.TYPE_CCLR: return "TYPE_CCLR";
            case ColorSpace.TYPE_CMY: return "TYPE_CMY";
            case ColorSpace.TYPE_CMYK: return "TYPE_CMYK";
            case ColorSpace.TYPE_DCLR: return "TYPE_DCLR";
            case ColorSpace.TYPE_ECLR: return "TYPE_ECLR";
            case ColorSpace.TYPE_FCLR: return "TYPE_FCLR";
            case ColorSpace.TYPE_GRAY: return "TYPE_GRAY";
            case ColorSpace.TYPE_HLS: return "TYPE_HLS";
            case ColorSpace.TYPE_HSV: return "TYPE_HSV";
            case ColorSpace.TYPE_Lab: return "TYPE_Lab";
            case ColorSpace.TYPE_Luv: return "TYPE_Luv";
            case ColorSpace.TYPE_RGB: return "TYPE_RGB";
            case ColorSpace.TYPE_XYZ: return "TYPE_XYZ";
            case ColorSpace.TYPE_YCbCr: return "TYPE_YCbCr";
            case ColorSpace.TYPE_Yxy: return "TYPE_Yxy";
        }
        return "UNKNOWN";
    }

    public static String getTransparencyDescription(int transparency) {
        if(transparency == Transparency.OPAQUE) {
            return "OPAQUE";
        } else if(transparency == Transparency.BITMASK) {
            return "BITMASK";
        } else if(transparency == Transparency.TRANSLUCENT) {
            return "TRANSLUCENT";
        }
        return "UNKNOWN";
    }

    public static String getTransferTypeDescription(int transferType) {
        switch (transferType) {
            case DataBuffer.TYPE_BYTE:
                return "TYPE_BYTE";
            case DataBuffer.TYPE_USHORT:
                return "TYPE_USHORT";
            case DataBuffer.TYPE_INT:
                return "TYPE_INT";
        }
        return "UNKNOWN";
    }

    public static String getColorModelDescription(ColorModel colorModel, int indentLevel) {

        StringBuilder sb = new StringBuilder(" {");
        addIndent(sb, indentLevel + 1);
        sb.append("class = ");
        sb.append(colorModel.getClass().getName());

        String colorSpaceDescription = getColorSpaceDescription(colorModel.getColorSpace(), indentLevel + 1);
        addIndent(sb, indentLevel + 1);
        sb.append("color space = ").append(colorSpaceDescription);

        int numColorComponents = colorModel.getNumColorComponents();
        addIndent(sb, indentLevel + 1);
        sb.append("numColorComponents = ").append(numColorComponents);

        int numComponents = colorModel.getNumComponents();
        addIndent(sb, indentLevel + 1);
        sb.append("numComponents = ").append(numComponents);

        boolean hasAlpha = colorModel.hasAlpha();
        addIndent(sb, indentLevel + 1);
        sb.append("hasAlpha = ").append(hasAlpha);

        int pixelSize = colorModel.getPixelSize();
        addIndent(sb, indentLevel + 1);
        sb.append("pixelSize = ").append(pixelSize);

        int transferType = colorModel.getTransferType();
        addIndent(sb, indentLevel + 1);
        sb.append("transferType = ").append(getTransferTypeDescription(transferType));

        int transparency = colorModel.getTransparency();
        addIndent(sb, indentLevel + 1);
        sb.append("transparency  = ").append(getTransparencyDescription(transparency));

        addIndent(sb, indentLevel);
        sb.append("}");
        return sb.toString();
    }


    public static String getColorSpaceDescription(ColorSpace colorSpace, int indentLevel) {
        StringBuilder sb = new StringBuilder(" {");
        addIndent(sb, indentLevel + 1);
        sb.append("class = ");
        sb.append(colorSpace.getClass().getName());

        int numComponents = colorSpace.getNumComponents();
        int type = colorSpace.getType();
        boolean is_sRGB = colorSpace.isCS_sRGB();

        addIndent(sb, indentLevel + 1);
        sb.append("numComponents = ").append(numComponents);

        addIndent(sb, indentLevel + 1);
        sb.append("type = ").append(getColorSpaceTypeDescription(type));

        addIndent(sb, indentLevel + 1);
        sb.append("is_sRGB = ").append(is_sRGB);

        addIndent(sb, indentLevel);
        sb.append("}");

        return sb.toString();
    }

    private static void addIndent(StringBuilder sb, int indentLevel) {
        sb.append("\n");
        for(int i = 0; i < indentLevel; i++) {
            sb.append("  ");
        }
    }

    private static String getWritebleRasterDescription(WritableRaster raster) {
        if (raster == null) {
            return "null";
        }
        String retVal = "{";
        retVal += ("\n      class = " + raster.getClass().getName());
        SampleModel sampleModel = raster.getSampleModel();
        retVal += ("\n      sampleModel = " + getSampleModelDescription(sampleModel));
        DataBuffer dataBuffer = raster.getDataBuffer();
        retVal += ("\n      dataBuffer = " + getDataBufferDescription(dataBuffer));
        retVal += "\n    }";
        return retVal;
    }

    private static String getDataBufferDescription(DataBuffer dataBuffer) {
        if (dataBuffer == null) {
            return "null";
        }
        String retVal = "{";
        retVal += ("\n        class = " + dataBuffer.getClass().getName());
        int numBanks = dataBuffer.getNumBanks();
        retVal += ("\n        numBanks = " + numBanks);
        int type = dataBuffer.getDataType();
        retVal += ("\n        type = " + getDataBufferTypeDescription(type));
        int size = dataBuffer.getSize();
        retVal += ("\n        size = " + size);
        retVal += "\n      }";
        return retVal;
    }

    private static String getDataBufferTypeDescription(int type) {
        // The DataBuffer lists the additional types TYPE_SHORT, TYPE_FLOAT, TYPE_DOUBLE, TYPE_UNDEFINED
        // but those are not in use
        return getTransferTypeDescription(type);
    }

    private static String getSampleModelDescription(SampleModel sampleModel) {
        if (sampleModel == null) {
            return "null";
        }
        // TODO: there must be other properties
        String retVal = "{";
        retVal += ("class = " + sampleModel.getClass().getName());
        retVal += "}";
        return retVal;
    }
}
