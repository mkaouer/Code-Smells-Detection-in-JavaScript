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

package org.mov.portfolio;

import java.io.OutputStream;
import java.util.Iterator;

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

/**
 * This class writes portfolios in XML format.
 *
 * @author Andrew Leppard
 * @see Portfolio
 * @see PortfolioReader
 */
public class PortfolioWriter {

    private PortfolioWriter() {
        // Nothing to do
    }

    /**
     * Write the portfolio to the output stream in XML format.
     *
     * @param portfolio the portfolio to write
     * @param stream    the output stream to write the portfolio.
     */
    public static void write(Portfolio portfolio, OutputStream stream) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.newDocument();
            
            Element portfolioElement = (Element)document.createElement("portfolio"); 
            portfolioElement.setAttribute("name", portfolio.getName());
            portfolioElement.setAttribute("currency", portfolio.getCurrency().getCurrencyCode());

            document.appendChild(portfolioElement);

            Element accountsElement = (Element)document.createElement("accounts");

            portfolioElement.appendChild(accountsElement);
            for(Iterator iterator = portfolio.getAccounts().iterator(); iterator.hasNext();) {
                Account account = (Account)iterator.next();
                Element accountElement =
                    (Element)document.createElement((account.getType() == Account.CASH_ACCOUNT?
                                                     "cash" : "share"));
                accountElement.setAttribute("name", account.getName());
                accountElement.setAttribute("currency", account.getCurrency().getCurrencyCode());
                accountsElement.appendChild(accountElement);
            }

            Element transactionsElement = (Element)document.createElement("transactions");
            portfolioElement.appendChild(transactionsElement);

            for(Iterator iterator = portfolio.getTransactions().iterator(); iterator.hasNext();) {
                Transaction transaction = (Transaction)iterator.next();
                Element transactionElement = null;

                switch(transaction.getType()) {
                case Transaction.WITHDRAWAL:
                    transactionElement = (Element)document.createElement("withdrawal");
                    transactionElement.setAttribute("cash_account",
                                                    transaction.getCashAccount().getName());
                    transactionElement.setAttribute("amount",  
                                                    transaction.getAmount().toString());
                    break;
                case Transaction.DEPOSIT:
                    transactionElement = (Element)document.createElement("deposit");
                    transactionElement.setAttribute("cash_account",
                                                    transaction.getCashAccount().getName());
                    transactionElement.setAttribute("amount",  
                                                    transaction.getAmount().toString());
                    break;
                case Transaction.INTEREST:
                    transactionElement = (Element)document.createElement("interest");
                    transactionElement.setAttribute("cash_account",
                                                    transaction.getCashAccount().getName());
                    transactionElement.setAttribute("amount",  
                                                    transaction.getAmount().toString());
                    break;
                case Transaction.FEE:
                    transactionElement = (Element)document.createElement("fee");
                    transactionElement.setAttribute("cash_account",
                                                    transaction.getCashAccount().getName());
                    transactionElement.setAttribute("amount",  
                                                    transaction.getAmount().toString());
                    break;
                case Transaction.ACCUMULATE:
                    transactionElement = (Element)document.createElement("accumulate");
                    transactionElement.setAttribute("amount",  
                                                    transaction.getAmount().toString());
                    transactionElement.setAttribute("symbol",  
                                                    transaction.getSymbol().toString());
                    transactionElement.setAttribute("shares",  
                                                    Integer.toString(transaction.getShares()));
                    transactionElement.setAttribute("trade_cost",  
                                                    transaction.getTradeCost().toString());
                    transactionElement.setAttribute("cash_account",
                                                    transaction.getCashAccount().getName());
                    transactionElement.setAttribute("share_account",
                                                    transaction.getShareAccount().getName());
                    break;
                case Transaction.REDUCE:
                    transactionElement = (Element)document.createElement("reduce");
                    transactionElement.setAttribute("amount",  
                                                    transaction.getAmount().toString());
                    transactionElement.setAttribute("symbol",  
                                                    transaction.getSymbol().toString());
                    transactionElement.setAttribute("shares",  
                                                    Integer.toString(transaction.getShares()));
                    transactionElement.setAttribute("trade_cost",  
                                                    transaction.getTradeCost().toString());
                    transactionElement.setAttribute("cash_account",
                                                    transaction.getCashAccount().getName());
                    transactionElement.setAttribute("share_account",
                                                    transaction.getShareAccount().getName());
                    break;
                case Transaction.DIVIDEND:
                    transactionElement = (Element)document.createElement("dividend");
                    transactionElement.setAttribute("amount",  
                                                    transaction.getAmount().toString());
                    transactionElement.setAttribute("symbol",  
                                                    transaction.getSymbol().toString());
                    transactionElement.setAttribute("cash_account",
                                                    transaction.getCashAccount().getName());
                    transactionElement.setAttribute("share_account",
                                                    transaction.getShareAccount().getName());
                    break;
                case Transaction.DIVIDEND_DRP:
                    transactionElement = (Element)document.createElement("dividend_drp");
                    transactionElement.setAttribute("symbol",  
                                                    transaction.getSymbol().toString());
                    transactionElement.setAttribute("shares",  
                                                    Integer.toString(transaction.getShares()));
                    transactionElement.setAttribute("share_account",
                                                    transaction.getShareAccount().getName());
                    break;
                case Transaction.TRANSFER:
                    transactionElement = (Element)document.createElement("transfer");
                    transactionElement.setAttribute("source_cash_account",
                                                    transaction.getCashAccount().getName());
                    transactionElement.setAttribute("destination_cash_account",
                                                    transaction.getCashAccount2().getName());
                    transactionElement.setAttribute("amount",  
                                                    transaction.getAmount().toString());
                    break;
                default:
                    // Unknown transaction type
                    assert false;
                }

                // All transaction elements have a date
                transactionElement.setAttribute("date", transaction.getDate().toString());
                transactionsElement.appendChild(transactionElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(stream);
            transformer.transform(source, result);
        }
        catch(ParserConfigurationException e) {
            // This should not occur
            assert false;
        }
        catch(TransformerException e) {
            // This should not occur
            assert false;
        }
    }
}