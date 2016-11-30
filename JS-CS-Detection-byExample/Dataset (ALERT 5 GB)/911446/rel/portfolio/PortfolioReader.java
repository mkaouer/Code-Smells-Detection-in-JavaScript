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

package nz.org.venice.portfolio;

import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.SymbolFormatException;
import nz.org.venice.util.Currency;
import nz.org.venice.util.Locale;
import nz.org.venice.util.Money;
import nz.org.venice.util.MoneyFormatException;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.TradingDateFormatException;
import nz.org.venice.util.UnknownCurrencyCodeException;

import java.io.InputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult; 

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 * This class parses portfolios written in XML format.
 *
 * @author Andrew Leppard
 * @see Portfolio
 * @see PortfolioWriter
 */
public class PortfolioReader {

    /**
     * This class cannot be instantiated.
     */
    private PortfolioReader() {
        // Nothing to do
    }

    /**
     * Read and parse the portfolio in XML format from the input stream and return
     * the portfolio object.
     *
     * @param stream the input stream containing the portfolio in XML format
     * @return the portfolio
     * @exception IOException if there was an I/O error reading from the stream.
     * @exception PortfolioParserException if there was an error parsing the portfolio.
     */
    public static Portfolio read(InputStream stream) throws IOException, PortfolioParserException {
        Portfolio portfolio = null;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(stream);
            Element portfolioElement = (Element)document.getDocumentElement();
            NamedNodeMap portfolioAttributes = portfolioElement.getAttributes();
            Node portfolioNameNode = portfolioAttributes.getNamedItem("name");
            Node portfolioCurrencyNode = portfolioAttributes.getNamedItem("currency");

            if(portfolioNameNode == null)
                throw new PortfolioParserException(Locale.getString("MISSING_PORTFOLIO_NAME_ATTRIBUTE"));
            if(portfolioCurrencyNode == null)
                throw new PortfolioParserException(Locale.getString("MISSING_PORTFOLIO_CURRENCY_ATTRIBUTE"));
            String portfolioName = portfolioNameNode.getNodeValue();
            String currencyName = portfolioCurrencyNode.getNodeValue();

            Currency currency = new Currency(currencyName);
            portfolio = new Portfolio(portfolioName, currency);

            NodeList childNodes = portfolioElement.getChildNodes();

            if (childNodes.getLength() != 2) {
                throw new PortfolioParserException(Locale.getString("PORTFOLIO_TOP_LEVEL_ERROR"));
            }

            if(childNodes.item(0).getNodeName().equals("accounts") &&
               childNodes.item(1).getNodeName().equals("transactions")) {
                readAccounts(portfolio, childNodes.item(0));
                readTransactions(portfolio, childNodes.item(1));
            }
            else if(childNodes.item(1).getNodeName().equals("accounts") &&
                    childNodes.item(0).getNodeName().equals("transactions")) {
                readAccounts(portfolio, childNodes.item(0));
                readTransactions(portfolio, childNodes.item(1));
            }
            else
                throw new PortfolioParserException(Locale.getString("PORTFOLIO_TOP_LEVEL_ERROR"));

        } catch (SAXException e) {
            throw new PortfolioParserException(e.getMessage());
        } catch(ParserConfigurationException e) {
            throw new PortfolioParserException(e.getMessage());
        } catch(UnknownCurrencyCodeException e) {
            throw new PortfolioParserException(Locale.getString("UNKNOWN_CURRENCY_CODE",
                                                                e.getReason()));
        }

        return portfolio;
    }

    /**
     * Read and parse the accounts node and add each account to the portfolio.
     *
     * @param portfolio    the portfolio being created
     * @param accountsNode the node containing the list of accounts.
     * @exception PortfolioParserException if there was an error parsing the portfolio.
     */
    private static void readAccounts(Portfolio portfolio, Node accountsNode)
        throws PortfolioParserException {
        NodeList accountNodeList = accountsNode.getChildNodes();

        for(int i = 0; i < accountNodeList.getLength(); i++) {
            Node accountNode = (Node)accountNodeList.item(i);
            NamedNodeMap accountAttributes = accountNode.getAttributes();
            Node accountNameNode = accountAttributes.getNamedItem("name");
            Node accountCurrencyNode = accountAttributes.getNamedItem("currency");

            if(accountNameNode == null)
                throw new PortfolioParserException(Locale.getString("MISSING_ACCOUNT_NAME_ATTRIBUTE"));
            if(accountCurrencyNode == null)
                throw new PortfolioParserException(Locale.getString("MISSING_ACCOUNT_CURRENCY_ATTRIBUTE"));

            String accountName = accountNameNode.getNodeValue();
            Currency accountCurrency = null;

            try {
                accountCurrency = new Currency(accountCurrencyNode.getNodeValue());
            } catch(UnknownCurrencyCodeException e) {
                throw new PortfolioParserException(Locale.getString("UNKNOWN_CURRENCY_CODE",
                                                                    e.getReason()));
            }

            if(accountNode.getNodeName().equals("cash"))
                portfolio.addAccount(new CashAccount(accountName, accountCurrency)); 
            else if(accountNode.getNodeName().equals("share"))
                portfolio.addAccount(new ShareAccount(accountName, accountCurrency)); 
            else
                throw new PortfolioParserException(Locale.getString("UKNOWN_ACCOUNT_TYPE",
                                                                    accountNode.getNodeName()));
        }
    }

    /**
     * Read and parse the transactions node and add each transaction to the portfolio.
     *
     * @param portfolio the portfolio being created.
     * @param transactionNode the node containing the list of transactions.
     * @exception PortfolioParserException if there was an error parsing the portfolio.
     */
    private static void readTransactions(Portfolio portfolio, Node transactionsNode) 
        throws PortfolioParserException {
        NodeList transactionNodeList = transactionsNode.getChildNodes();

        for(int i = 0; i < transactionNodeList.getLength(); i++) {
            Transaction transaction = null;
            Node transactionNode = (Node)transactionNodeList.item(i);
            String transactionType = transactionNode.getNodeName();
            NamedNodeMap transactionAttributes = transactionNode.getAttributes();
            Node dateNode = transactionAttributes.getNamedItem("date");
            TradingDate date = null;

            if(dateNode == null)
                throw new PortfolioParserException(Locale.getString("MISSING_TRANSACTION_DATE_ATTRIBUTE"));
            try {
                date = new TradingDate(dateNode.getNodeValue(), TradingDate.BRITISH);
            }
            catch(TradingDateFormatException e) {
                throw new PortfolioParserException(e.getMessage());
            }

            if(transactionType.equals("withdrawal")) {
                CashAccount account = readCashAccount(portfolio, transactionAttributes,
                                                      "cash_account");
                Money amount = readMoney(account.getCurrency(), transactionAttributes, "amount");
                transaction = Transaction.newWithdrawal(date, amount, account);
            }
            else if(transactionType.equals("deposit")) {
                CashAccount account = readCashAccount(portfolio, transactionAttributes,
                                                      "cash_account");
                Money amount = readMoney(account.getCurrency(), transactionAttributes, "amount");
                transaction = Transaction.newDeposit(date, amount, account);
                
            }
            else if(transactionType.equals("interest")) {
                CashAccount account = readCashAccount(portfolio, transactionAttributes,
                                                      "cash_account");
                Money amount = readMoney(account.getCurrency(), transactionAttributes, "amount");
                transaction = Transaction.newInterest(date, amount, account);
            }
            else if(transactionType.equals("fee")) {
                CashAccount account = readCashAccount(portfolio, transactionAttributes,
                                                      "cash_account");
                Money amount = readMoney(account.getCurrency(), transactionAttributes, "amount");
                transaction = Transaction.newFee(date, amount, account);
            }
            else if(transactionType.equals("accumulate")) {
                Symbol symbol = readSymbol(transactionAttributes, "symbol");
                int shares = readInt(transactionAttributes, "shares");
                CashAccount cashAccount = readCashAccount(portfolio, transactionAttributes,
                                                          "cash_account");
                Money amount = readMoney(cashAccount.getCurrency(), transactionAttributes, "amount");
                Money tradeCost = readMoney(cashAccount.getCurrency(), transactionAttributes,
                                            "trade_cost");
                ShareAccount shareAccount = readShareAccount(portfolio, transactionAttributes,
                                                            "share_account");
                transaction = Transaction.newAccumulate(date, amount, symbol, shares, tradeCost,
                                                        cashAccount, shareAccount);
            }
            else if(transactionType.equals("reduce")) {
                Symbol symbol = readSymbol(transactionAttributes, "symbol");
                int shares = readInt(transactionAttributes, "shares");
                CashAccount cashAccount = readCashAccount(portfolio, transactionAttributes,
                                                          "cash_account");
                ShareAccount shareAccount = readShareAccount(portfolio, transactionAttributes,
                                                             "share_account");
                Money amount = readMoney(cashAccount.getCurrency(), transactionAttributes,
                                         "amount");
                Money tradeCost = readMoney(cashAccount.getCurrency(), transactionAttributes,
                                            "trade_cost");
                transaction = Transaction.newReduce(date, amount, symbol, shares, tradeCost,
                                                    cashAccount, shareAccount);
            }
            else if(transactionType.equals("dividend")) {
                Symbol symbol = readSymbol(transactionAttributes, "symbol");
                CashAccount cashAccount = readCashAccount(portfolio, transactionAttributes,
                                                          "cash_account");
                ShareAccount shareAccount = readShareAccount(portfolio, transactionAttributes,
                                                             "share_account");
                Money amount = readMoney(cashAccount.getCurrency(), transactionAttributes,
                                         "amount");
                transaction = Transaction.newDividend(date, amount, symbol, cashAccount,
                                                      shareAccount);
            }
            else if(transactionType.equals("dividend_drp")) {
                Symbol symbol = readSymbol(transactionAttributes, "symbol");
                int shares = readInt(transactionAttributes, "shares");
                ShareAccount shareAccount = readShareAccount(portfolio,
                                                             transactionAttributes,
                                                             "share_account");
                transaction = Transaction.newDividendDRP(date, symbol, shares, shareAccount);
            }
            else if(transactionType.equals("transfer")) {
                CashAccount sourceAccount = readCashAccount(portfolio, transactionAttributes,
                                                            "source_cash_account");
                CashAccount destinationAccount = readCashAccount(portfolio, transactionAttributes,
                                                                 "destination_cash_account");
                Money amount = readMoney(sourceAccount.getCurrency(), transactionAttributes,
                                         "amount");
                transaction = Transaction.newTransfer(date, amount, sourceAccount,
                                                      destinationAccount);
            }
            else
                throw new PortfolioParserException(Locale.getString("UNKNOWN_TRANSACTION_TYPE",
                                                                    transactionType));

            portfolio.addTransaction(transaction);
        }
    }

    /**
     * Read and parse a transaction's attribute that should reference a cash account.
     *
     * @param portfolio  the portfolio being creqated
     * @param attributes the transaction's atttributes
     * @param name       the name of the attribute which should reference a cash account.
     * @return the cash account referenced.
     * @exception PortfolioParserException if there was an error parsing the portfolio such as
     *            the cash account being unknown.
     */
    private static CashAccount readCashAccount(Portfolio portfolio, NamedNodeMap attributes,
                                               String name)
        throws PortfolioParserException {
        Node node = attributes.getNamedItem(name);
        String accountName = node.getNodeValue();

        if(accountName == null) {
            String error = Locale.getString("MISSING_TRANSACTION_ATTRIBUTE", name);
            throw new PortfolioParserException(error);
        }

        Account account = portfolio.findAccountByName(accountName);

        if(account == null)
            throw new PortfolioParserException(Locale.getString("UNKNOWN_ACCOUNT",
                                                                accountName));
        try {
            return (CashAccount)account;
        }
        catch(ClassCastException e) {
            throw new PortfolioParserException(Locale.getString("EXPECTING_CASH_ACCOUNT",
                                                                accountName));
        }
    }

    /**
     * Read and parse a transaction's attribute that should reference a share account.
     *
     * @param portfolio  the portfolio being creqated
     * @param attributes the transaction's atttributes
     * @param name       the name of the attribute which should reference a share account.
     * @return the share account referenced.
     * @exception PortfolioParserException if there was an error parsing the portfolio such as
     *            the share account being unknown.
     */
    private static ShareAccount readShareAccount(Portfolio portfolio, NamedNodeMap attributes,
                                                 String name)
        throws PortfolioParserException {
        Node node = attributes.getNamedItem(name);
        String accountName = node.getNodeValue();

        if(accountName == null) {
            String error = Locale.getString("MISSING_TRANSACTION_ATTRIBUTE", name);
            throw new PortfolioParserException(error);
        }

        Account account = portfolio.findAccountByName(accountName);

        if(account == null)
            throw new PortfolioParserException(Locale.getString("UNKNOWN_ACCOUNT",
                                                                accountName));
        try {
            return (ShareAccount)account;
        }
        catch(ClassCastException e) {
            throw new PortfolioParserException(Locale.getString("EXPECTING_SHARE_ACCOUNT",
                                                                accountName));
        }
    }

    /**
     * Read and parse a transaction's attribute that should contain an integer.
     *
     * @param attributes the transaction's atttributes
     * @param name       the name of the attribute which should contain an integer.
     * @return the number.
     * @exception PortfolioParserException if there was an error parsing the portfolio.
     */
    private static int readInt(NamedNodeMap attributes, String name)
        throws PortfolioParserException {
        Node node = attributes.getNamedItem(name);

        if(node == null) {
            String error = Locale.getString("MISSING_TRANSACTION_ATTRIBUTE", name);
            throw new PortfolioParserException(error);
        }

        try {
            return Integer.parseInt(node.getNodeValue());
        }
        catch(NumberFormatException e) {
            throw new PortfolioParserException(Locale.getString("ERROR_PARSING_NUMBER",
                                                                e.getMessage()));
        }
    }

    /**
     * Read and parse a transaction's attribute that should contain money.
     *
     * @param attributes the transaction's atttributes
     * @param name       the name of the attribute which should contain money.
     * @return the money.
     * @exception PortfolioParserException if there was an error parsing the portfolio.
     */
    private static Money readMoney(Currency currency, NamedNodeMap attributes, String name)
        throws PortfolioParserException {
        Node node = attributes.getNamedItem(name);

        if(node == null) {
            String error = Locale.getString("MISSING_TRANSACTION_ATTRIBUTE", name);
            throw new PortfolioParserException(error);
        }

        try {
            return new Money(currency, node.getNodeValue());
        }
        catch(MoneyFormatException e) {
            throw new PortfolioParserException(Locale.getString("ERROR_PARSING_MONEY",
                                                                e.getReason()));
        }
    }
    
    /**
     * Read and parse a transaction's attribute that should contain a stock symbol.
     *
     * @param attributes the transaction's atttributes
     * @param name       the name of the attribute which should contain a stock symbol.
     * @return the stock symbol.
     * @exception PortfolioParserException if there was an error parsing the portfolio.
     */
    private static Symbol readSymbol(NamedNodeMap attributes, String name)
        throws PortfolioParserException {
        Node node = attributes.getNamedItem(name);

        if(node == null) {
            String error = Locale.getString("MISSING_TRANSACTION_ATTRIBUTE", name);
            throw new PortfolioParserException(error);
        }

        try {
            return Symbol.find(node.getNodeValue());
        }
        catch(SymbolFormatException e) {
            throw new PortfolioParserException(e.getMessage());
        }
    }
}
