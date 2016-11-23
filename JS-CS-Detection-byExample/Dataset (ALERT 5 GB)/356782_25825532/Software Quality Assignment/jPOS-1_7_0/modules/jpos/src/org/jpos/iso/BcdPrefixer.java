/*
 * jPOS Project [http://jpos.org]
 * Copyright (C) 2000-2010 Alejandro P. Revilla
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jpos.iso;

/**
 * BcdPrefixer constructs a prefix storing the length in BCD.
 * 
 * @author joconnor
 * @version $Revision$ $Date$
 */
public class BcdPrefixer implements Prefixer
{
    /**
     * A length prefixer for upto 9 chars. The length is encoded with 1 ASCII
     * char representing 1 decimal digit.
     */
    public static final BcdPrefixer L = new BcdPrefixer(1);
    /**
	 * A length prefixer for upto 99 chars. The length is encoded with 2 ASCII
	 * chars representing 2 decimal digits.
	 */
    public static final BcdPrefixer LL = new BcdPrefixer(2);
    /**
	 * A length prefixer for upto 999 chars. The length is encoded with 3 ASCII
	 * chars representing 3 decimal digits.
	 */
    public static final BcdPrefixer LLL = new BcdPrefixer(3);
    /**
	 * A length prefixer for upto 9999 chars. The length is encoded with 4
	 * ASCII chars representing 4 decimal digits.
	 */
    public static final BcdPrefixer LLLL = new BcdPrefixer(4);
    /**
     * A length prefixer for upto 99999 chars. The length is encoded with 5
     * ASCII chars representing 5 decimal digits.
     */
    public static final BcdPrefixer LLLLL = new BcdPrefixer(5);

    /** The number of digits allowed to express the length */
    private int nDigits;

    public BcdPrefixer(int nDigits)
    {
        this.nDigits = nDigits;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.iso.Prefixer#encodeLength(int, byte[])
	 */
    public void encodeLength(int length, byte[] b)
    {
        for (int i = getPackedLength() - 1; i >= 0; i--) {
            int twoDigits = length % 100;
            length /= 100;
            b[i] = (byte)(((twoDigits / 10) << 4) + twoDigits % 10);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.iso.Prefixer#decodeLength(byte[], int)
	 */
    public int decodeLength(byte[] b, int offset)
    {
        int len = 0;
        for (int i = 0; i < (nDigits + 1) / 2; i++)
        {
            len = 100 * len + ((b[offset + i] & 0xF0) >> 4) * 10 + ((b[offset + i] & 0x0F));
        }
        return len;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.iso.Prefixer#getLengthInBytes()
	 */
    public int getPackedLength()
    {
        return (nDigits + 1) / 2;
    }
}