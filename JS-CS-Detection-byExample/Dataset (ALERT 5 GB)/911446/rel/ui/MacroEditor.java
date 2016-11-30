/* Merchant of Venice - technical analysis software for the stock market.
   Copyright (C) 2002 Andrew Leppard (aleppard@picknowl.com.au)
   This portion of code Copyright (C) 2004 Dan Makovec (venice@makovec.net)
  
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

package nz.org.venice.ui;

import javax.swing.JInternalFrame;
import nz.org.venice.macro.StoredMacro;
import nz.org.venice.util.Locale;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.CardLayout;
import javax.swing.JScrollPane;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;

/**
 * @author Dan Makovec venice@makovec.net
 *
 * TODO This is the XXXX
 */
public class MacroEditor extends JInternalFrame 
						 implements java.awt.event.ActionListener {

    /** Did the user click ok? */
    private boolean is_ok;
    
	private JPanel jPanel = null;
	private JPanel jPanel2 = null;
	private JPanel jPanel3 = null;
	private JTextField name_txt = null;
	private JTextArea macro_txt = null;
	private JButton ok_btn = null;
	private JButton cancel_btn = null;
	private JScrollPane jScrollPane = null;
	private JTextField file_txt = null;
	private JPanel jPanel5 = null;
	private JPanel jPanel1 = null;

	
	/** The stored macro being edited */
	StoredMacro macro;
	
	/**
	 * This method initializes 
	 * 
	 */
    public MacroEditor(StoredMacro m) {
        super();
        this.macro = m;
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
	    this.setSize(350, 249);
	    this.setMaximizable(true);
	    this.setContentPane(getJPanel());
	    this.is_ok = false;
        setResizable(true);
        setClosable(true);
        setTitle(Locale.getString("EDITMACRO")+" "+this.macro.getName());
        name_txt.setText(this.macro.getName());
        file_txt.setText(this.macro.getFilename());
        macro_txt.setText(this.macro.getCode());
        setVisible(true);
        DesktopManager.getDesktop().add(this);
        if (this.macro.getCode().length() > 0)
            jScrollPane.setPreferredSize(macro_txt.getPreferredScrollableViewportSize());
	    pack();
	    setLocation(DesktopManager.getDesktop().getWidth()/2 - this.getWidth()/2,
            	DesktopManager.getDesktop().getHeight()/2 - this.getHeight()/2);
        moveToFront();
	}


	public boolean isOk_clicked() {
	    return this.is_ok;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.add(getJPanel1(), java.awt.BorderLayout.NORTH);
			jPanel.add(getJPanel2(), java.awt.BorderLayout.CENTER);
			jPanel.add(getJPanel3(), java.awt.BorderLayout.SOUTH);
		}
		return jPanel;
	}
	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setLayout(new CardLayout());
			jPanel2.add(getJScrollPane(), getJScrollPane().getName());
		}
		return jPanel2;
	}
	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.add(getJButton(), null);
			jPanel3.add(getJButton1(), null);
		}
		return jPanel3;
	}
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJTextField() {
		if (name_txt == null) {
			name_txt = new JTextField();
			name_txt.addCaretListener(new javax.swing.event.CaretListener() { 
				public void caretUpdate(javax.swing.event.CaretEvent e) {    
					if (macro.getFilename().length() == 0 && name_txt.getText().length() > 0)
					    file_txt.setText(((JTextField)e.getSource()).getText()+".py");
					else if (macro.getFilename().length() == 0)
					    file_txt.setText("");
		        setTitle(Locale.getString("EDITMACRO")+" "+name_txt.getText());
				}
			});
		}
		return name_txt;
	}
	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getJTextArea() {
		if (macro_txt == null) {
			macro_txt = new JTextArea();
		}
		return macro_txt;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (ok_btn == null) {
			ok_btn = new JButton();
			ok_btn.setText(Locale.getString("OK"));
			ok_btn.addActionListener(this);
		}
		return ok_btn;
	}
	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton1() {
		if (cancel_btn == null) {
			cancel_btn = new JButton();
			cancel_btn.setText(Locale.getString("CANCEL"));
			cancel_btn.addActionListener(this);
		}
		return cancel_btn;
	}
	public void actionPerformed(java.awt.event.ActionEvent e) {
	    if (e.getSource().equals(ok_btn)) {
	        this.macro.setName(name_txt.getText());
	        this.macro.setFilename(file_txt.getText());
	        this.macro.setCode(macro_txt.getText());
	        this.macro.save();
	        this.is_ok = true;
	    }
	    try {
	        this.setClosed(true);
	    } catch (Exception ex) {}
	}
	    
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setName("jScrollPane");
			jScrollPane.setViewportView(getJTextArea());
			jScrollPane.setPreferredSize(new java.awt.Dimension(200,80));
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJTextField2() {
		if (file_txt == null) {
			file_txt = new JTextField();
		}
		return file_txt;
	}
		
	/** 	
	 * @return javax.swing.JPanel	
	 */    
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			java.awt.GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			java.awt.GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			java.awt.GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			java.awt.GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			javax.swing.JLabel jLabel1 = new JLabel();
			javax.swing.JLabel jLabel = new JLabel();
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jLabel.setText(Locale.getString("MACRONAME"));
			jLabel1.setText(Locale.getString("FILE"));
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.insets = new java.awt.Insets(9,3,9,2);
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.insets = new java.awt.Insets(5,3,5,2);
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.insets = new java.awt.Insets(9,3,9,2);
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.CENTER;
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.insets = new java.awt.Insets(5,3,5,2);
			jPanel1.add(jLabel, gridBagConstraints1);
			jPanel1.add(getJTextField(), gridBagConstraints2);
			jPanel1.add(jLabel1, gridBagConstraints3);
			jPanel1.add(getJTextField2(), gridBagConstraints4);
		}
		return jPanel1;
	}
 }