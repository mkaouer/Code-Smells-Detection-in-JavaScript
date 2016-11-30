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

/**
 *
 * A dialog

 */

package nz.org.venice.alert;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.net.URL;

import nz.org.venice.util.Locale;
import nz.org.venice.main.CommandManager;

/**
 * A dialog which displays notifications of alerts which have been triggered.
 *
 * @author Mark Hummel
 * @see AlertDialog
 */

public class AlertTriggeredDialog  {


    public static final int OK = 0;
    public static final int OPEN_TABLE = 1;
    public static final int OPEN_CHART = 2;
    public static final int OPEN_ALERT = 3;
    

    private AlertTriggeredDialog() {
	
    }

    public static int show(JDesktopPane desktop, Alert alert, String triggerValue) {
	
	String okButton = Locale.getString("OK");
	String openTableButton = Locale.getString("ALERT_SHOW_TABLE");
	String openChartButton = Locale.getString("ALERT_SHOW_CHART");
	String openAlertsButton = Locale.getString("ALERT_SHOW_ALERTS");

	Object[] buttons = {okButton, openTableButton, 
			     openChartButton, openAlertsButton};
	
	ImageIcon aboutIcon;
	String about = "resources/info.png";

	URL aboutIconURL = ClassLoader.getSystemResource(about);
	aboutIcon = new ImageIcon(aboutIconURL);
	
	String message = generateMessage(alert, triggerValue);

	int optionSelected = JOptionPane.showOptionDialog(desktop,
							  message,
							  "Alert",
							  JOptionPane.
							  INFORMATION_MESSAGE,
							  JOptionPane.
							  YES_NO_CANCEL_OPTION,
							  aboutIcon,
							  buttons,
							  buttons[0]);
	

	return optionSelected;
    }
    
    private static String generateMessage(Alert alert, String triggerValue) {
	String message = "";

	if (alert instanceof OHLCVAlert) {
	    message += Alert.boundTypeToString(alert.getBoundType())  + " bound  on " + alert.getField() + 
		Locale.getString("ALERT_TRIGGER_MESG_OHLCV",
				 alert.getSymbol().toString(),
				 alert.getField(),
				 triggerValue);
	} else {
	    message += Locale.getString("ALERT_TRIGGER_MESG_EXP", alert.getSymbol().toString());
	}

	message += Locale.getString("ALERT_TRIGGER_MESG_DATE", 
				    alert.getDateSet().toString());
	return message;
    }
}