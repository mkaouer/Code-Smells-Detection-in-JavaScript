/* Merchant of Venice - technical analysis software for the stock market.
   Copyright (C) 2002 Andrew Leppard (aleppard@picknowl.com.au)

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   
   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
*/

package org.mov.util;

import java.text.ParseException;

import java.text.NumberFormat;

/** 
 * An immutable representation of Money. This class was created to reference
 * sums of money. Due to rounding issues, the use of <code>float</code> and to a lesser
 * extent <code>double</code> are not recommended to represent money. Therefore 
 * money should be represented by an integer type, which would require conversion
 * from the more natural floating point type. This class was created to handle
 * all this conversion work and provide a nice simple abstraction.
 */
public class Money implements Cloneable, Comparable {

    // WARNING: Do not convert doubles to long by:
    // amountLong = (long)(amountDouble * 100.0D)
    // because it performs a floor operation. Round by:
    // amountLong = Math.round(amountDouble * 100.0D)

    // Converter to display currency. Create a single instance so we don't
    // have to keep instantiating it.
    private static NumberFormat format = NumberFormat.getCurrencyInstance();

    /** A representation of no money, e.g. $0. */
    public static Money ZERO = new Money();

    // We use a long to store the money because float and doubles aren't
    // accurate enough. We do not use an int because it would only allow
    // ranges between -20 and 20 million. While this seems fine for now,
    // it doesn't make much sense limiting the application in this way.
    private long amount;

    // This is private because if the user wants to create an empty money
    // value they should use the ZERO instance.
    private Money() {
        amount = 0;
    }

    private Money(long amount) {
        this.amount = amount;
    }

    /**
     * Create a new <code>Money</code> instance from the given amount.
     * 
     * @param amount the amount of money.
     */
    public Money(double amount) {
        this.amount = Math.round(amount * 100.0D);
    }

    /**
     * Create a new <code>Money</code> instance by parsing the given string.
     * 
     * @param string the string to parse.
     * @exception MoneyFormatException if there was an error parsing the string.
     */
    public Money(String string) 
        throws MoneyFormatException {

        try {
            // Try parsing currency format, e.g. $32.00
            Number number = format.parse(string);
            this.amount = Math.round(number.doubleValue() * 100.0D);
        }
        catch(ParseException e2) {
            // If that doesn't work try parsing it as a simple double
            try {
                double doubleValue = Double.parseDouble(string);
                this.amount = Math.round(doubleValue * 100.0D);
            }
            catch(NumberFormatException e) {
                throw new MoneyFormatException(string);
            }
        }
    }

    /**
     * Add the given money to this money.
     * 
     * @param money the money to add.
     * @return the resultant <code>Money</code>.
     */
    public Money add(Money money) {
        return new Money(amount + money.longValue());
    }

    /**
     * Add the given money to this money.
     * 
     * @param money the money to add.
     * @return the resultant <code>Money</code>.
     */
    public Money add(double money) {
        return new Money(amount + Math.round(money * 100.0D));
    }

    /**
     * Subtract the given money from this money.
     * 
     * @param money the money to subtract.
     * @return the resultant <code>Money</code>.
     */
    public Money subtract(Money money) {
        return new Money(amount - money.longValue());
    }

    /**
     * Divide this money by the given number.
     * 
     * @param number the number to divide by.
     * @return the resultant <code>Money</code>.
     */
    public Money divide(int number) {
        return new Money(amount / number);
    }

    /**
     * Multiply this money by the given number.
     * 
     * @param number the number to multiply by.
     * @return the resultant <code>Money</code>.
     */
    public Money multiply(int number) {
        return new Money(amount * number);
    }

    public Object clone() {
        return new Money(amount);
    }

    /**
     * Return whether this money is less than the given money.
     *
     * @param money the money to compare with
     * @return <code>true</code> if this money is less than the given money.
     */
    public boolean isLessThan(Money money) {
        return (amount < money.longValue());
    }

    /**
     * Return whether this money is less than or equal to the given money.
     *
     * @param money the money to compare with
     * @return <code>true</code> if this money is less than or equal to the given money.
     */
    public boolean isLessThanEqual(Money money) {
        return (amount <= money.longValue());
    }

    /**
     * Return whether this money is greater than to the given money.
     *
     * @param money the money to compare with
     * @return <code>true</code> if this money is greater than the given money.
     */
    public boolean isGreaterThan(Money money) {
        return (amount > money.longValue());
    }

    /**
     * Return whether this money is greater than or equal to the given money.
     *
     * @param money the money to compare with
     * @return <code>true</code> if this money is greater than or equal to the given money.
     */
    public boolean isGreaterThanEqual(Money money) {
        return (amount >= money.longValue());
    }

    public int compareTo(Object object) {
        Money money = (Money)object;

        if(amount < money.longValue())
            return -1;
        if(amount > money.longValue())
            return 1;
        return 0;
    }

    public boolean equals(Object object) {
        Money money = (Money)object;

        return (amount == money.longValue());
    }

    public int hashCode() {
        return (int)amount;
    }

    /**
     * Returns a <code>String</code> representation of the given money value.
     *
     * @param amount the amount of money
     * @return the string representation.
     */
    public static String toString(double amount) {
        return format.format(amount);
    }

    /**
     * Returns a <code>String</code> object representing this <code>Money</code>'s value.
     *
     * @return the string representation.
     */
    public String toString() {
        return format.format(doubleValue());
    }

    /** 
     * Return the value of this <code>Money</code> as a <code>double</code>.
     *
     * @return the numeric value represented by this object after 
     *         conversion to type <code>double</code>.
     */
    public double doubleValue() {
        double cents = (double)amount;
        return cents / 100.0D;
    }

    /**
     * Return the value of this <code>Money</code> as a <code>long</code>.
     * This conversion does not involve rounding as it represents the number
     * of cents in the money rather than the dollar amount.
     *
     * @return the long representation.
     */
    public long longValue() {
        return amount;
    }


}
