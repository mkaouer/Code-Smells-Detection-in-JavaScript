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

import java.text.NumberFormat;
import java.text.ParseException;

/** 
 * An immutable representation of Money. This class was created to reference
 * sums of money. Due to rounding issues, the use of <code>float</code> and to a lesser
 * extent <code>double</code> are not recommended to represent money. Therefore 
 * money should be represented by an integer type, which would require conversion
 * from the more natural floating point type. This class was created to handle
 * all this conversion work and provide a nice simple abstraction.
 *
 * @author Andrew Leppard
 * @see Currency
 */
public class Money implements Cloneable, Comparable {

    /** Immutable representation of zero money in the default currency. */
    public final static Money ZERO = new Money(0.0D);

    // WARNING: Do not convert doubles to long by:
    // amountLong = (long)(amountDouble * 100.0D)
    // because it performs a floor operation. Round by:
    // amountLong = Math.round(amountDouble * 100.0D)

    // The currency of this money
    private Currency currency;

    // This number is the amount we need to multiply the double
    // representation of money to store it as a long. We store
    // and manipulate financial calculations using the long
    // type to ensure that calculations are exact and we do not
    // loose precision when rounding. For currencies containing
    // 100 cents, the conversion rate will be 100.0.
    private double conversion;

    // We use a long to store the money because float and doubles aren't
    // accurate enough. We do not use an int because it would only allow
    // ranges between -20 and 20 million. While this seems fine for now,
    // it doesn't make much sense limiting the application in this way.
    private long amount;

    /**
     * Create a new <code>Money</code> instance from the given amount using
     * the given currency and conversion value.
     *
     * @param currency   the currency of the money.
     * @param conversion the number to multiply the money to convert
     *                   from a double to a long representation.
     * @param amount     the amount of money.
     */
    private Money(Currency currency, double conversion, long amount) {
        this.currency = currency;
        this.conversion = conversion;
        this.amount = amount;
    }

    /**
     * Create a new <code>Money</code> instance from the given amount using the
     * given currency.
     * 
     * @param currency the currency of the money.
     * @param amount   the amount of money.
     */
    public Money(Currency currency, double amount) {
        this.currency = currency;
        this.conversion = calculateConversion(currency);
        this.amount = toLong(amount, conversion);
    }

    /**
     * Create a new <code>Money</code> instance from the given amount using the
     * default currency.
     * 
     * @param amount the amount of money.
     */
    public Money(double amount) {
        this(Currency.getDefaultCurrency(), amount);
    }

    /**
     * Create a new <code>Money</code> instance by parsing the given string.
     * 
     * @param string the string to parse.
     * @exception MoneyFormatException if there was an error parsing the string.
     */
    public Money(Currency currency, String string) 
        throws MoneyFormatException {

        this.currency = currency;
        this.conversion = calculateConversion(currency);
        
        try {
            // Try parsing currency format, e.g. $32.00
            NumberFormat format = currency.getNumberFormat();
            Number number = format.parse(string);
            this.amount = toLong(number.doubleValue(), conversion);
        }
        catch(ParseException e2) {
            // If that doesn't work try parsing it as a simple double
            try {
                double doubleValue = Double.parseDouble(string);
                this.amount = toLong(doubleValue, conversion);
            }
            catch(NumberFormatException e) {
                throw new MoneyFormatException(string);
            }
        }
    }

    /**
     * Return the currency of this money.
     *
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Add the given money to this money.
     * 
     * @param money the money to add.
     * @return the resultant <code>Money</code>.
     */
    public Money add(Money money) {
        assert currency.equals(money.currency);

        return new Money(currency, conversion, amount + money.amount);
    }

    /**
     * Add the given money to this money.
     * 
     * @param money the money to add.
     * @return the resultant <code>Money</code>.
     */
    public Money add(double money) {
        return new Money(currency, conversion, amount + toLong(money, conversion));
    }

    /**
     * Subtract the given money from this money.
     * 
     * @param money the money to subtract.
     * @return the resultant <code>Money</code>.
     */
    public Money subtract(Money money) {
        assert currency.equals(money.currency);

        return new Money(currency, conversion, amount - money.amount);
    }

    /**
     * Divide this money by the given number.
     * 
     * @param number the number to divide by.
     * @return the resultant <code>Money</code>.
     */
    public Money divide(int number) {
        return new Money(currency, conversion, amount / number);
    }

    /**
     * Multiply this money by the given number.
     * 
     * @param number the number to multiply by.
     * @return the resultant <code>Money</code>.
     */
    public Money multiply(int number) {
        return new Money(currency, conversion, amount * number);
    }

    /**
     * Return a clone of this object. Since <code>Money</code> is immutable,
     * this function simply returns this object.
     *
     * @return this object
     */
    public Object clone() {
        return this;
    }

    /**
     * Return whether this money is less than the given money.
     *
     * @param money the money to compare with
     * @return <code>true</code> if this money is less than the given money.
     */
    public boolean isLessThan(Money money) {
        assert currency.equals(money.currency);

        return (amount < money.amount);
    }

    /**
     * Return whether this money is less than or equal to the given money.
     *
     * @param money the money to compare with
     * @return <code>true</code> if this money is less than or equal to the given money.
     */
    public boolean isLessThanEqual(Money money) {
        assert currency.equals(money.currency);

        return (amount <= money.amount);
    }

    /**
     * Return whether this money is greater than to the given money.
     *
     * @param money the money to compare with
     * @return <code>true</code> if this money is greater than the given money.
     */
    public boolean isGreaterThan(Money money) {
        assert currency.equals(money.currency);

        return (amount > money.amount);
    }

    /**
     * Return whether this money is greater than or equal to the given money.
     *
     * @param money the money to compare with
     * @return <code>true</code> if this money is greater than or equal to the given money.
     */
    public boolean isGreaterThanEqual(Money money) {
        assert currency.equals(money.currency);

        return (amount >= money.amount);
    }

    /**
     * Compare this money to the specified money.
     *
     * @param object the money to compare
     * @return the value <code>0</code> if the monies are equal;
     * <code>1</code> if this money is more than the specified money or
     * <code>-1</code> if this money is less than the specified money.
     */
    public int compareTo(Object object) {
        Money money = (Money)object;

        assert currency.equals(money.currency);

        if(amount < money.amount)
            return -1;
        if(amount > money.amount)
            return 1;
        return 0;
    }

    /**
     * Compare this money for equality with the specified money.
     *
     * @param object the money to compare
     * @return <code>true</code> iff the monies are equal.
     */
    public boolean equals(Object object) {
        Money money = (Money)object;

        assert currency.equals(money.currency);

        return (amount == money.amount);
    }

    /**
     * Return a hash code representation of this money.
     *
     * @return hash code representation.
     */
    public int hashCode() {
        return (int)amount;
    }

    /**
     * Exchange this money for another currency at the given exchange rate.
     * This function returns a new money object which contains the exchanged
     * money in the given currency.
     *
     * @param currency     the currency to exchange this money for
     * @param exchangeRate the exchange rate
     * @return the new money in the given currency
     */
    public Money exchange(Currency currency, double exchangeRate) {
        double newAmount = toDouble(amount, conversion) * exchangeRate;

        return new Money(currency, newAmount);
    }

    /**
     * Returns a <code>String</code> object representing this <code>Money</code>'s value.
     *
     * @return the string representation.
     */
    public String toString() {
        NumberFormat format = currency.getNumberFormat();
        
        return format.format(doubleValue());
    }
    
    /**
     * Return a <code>String</code> representation of the given amount of money
     * in the default currency.
     *
     * @param value the value of the money
     * @return the string representation.
     */
    public static String toString(double value) {
        NumberFormat format = Currency.getDefaultCurrency().getNumberFormat();

        return format.format(value);
    }

    /** 
     * Return the value of this <code>Money</code> as a <code>double</code>.
     *
     * @return the numeric value represented by this object after 
     *         conversion to type <code>double</code>.
     */
    public double doubleValue() {
        return toDouble(amount, conversion);
    }

    /**
     * Create the number we need to multiple the currency to convert it
     * to a long. E.g. for Euro, with two decimal digits, if we
     * multiply it by 100, we can convert it to a long. We store
     * the value internally as an integer to avoid rounding issues.
     *
     * @param currency the currency
     * @return the conversion value
     */
    private static double calculateConversion(Currency currency) {
        return Math.pow(10.0D, (double)currency.getDefaultFractionDigits());
    }

    /**
     * Convert a <code>long</code> representation of money to a
     * <code>double</code> representation of money. The former representation
     * is used for calculations as it performs no rounding and so does not
     * loose precision. The latter is displayed to the user.
     *
     * @param value      the <code>long</code> value to convert from
     * @param conversion the conversion value
     * @return the resultant <code>double</code> value
     */
    private static double toDouble(long value, double conversion) {
        return ((double)value) / conversion;
    }

    /**
     * Convert a <code>double</code> representation of money to a
     * <code>long</code> representation of money. The latter representation
     * is used for calculations as it performs no rounding and so does not
     * loose precision. The former is displayed to the user.
     *
     * @param value      the <code>double</code> value to convert from
     * @param conversion the conversion value
     * @return the resultant <code>long</code> value
     */
    private static long toLong(double value, double conversion) {
        return Math.round(value * conversion);
    }
}
