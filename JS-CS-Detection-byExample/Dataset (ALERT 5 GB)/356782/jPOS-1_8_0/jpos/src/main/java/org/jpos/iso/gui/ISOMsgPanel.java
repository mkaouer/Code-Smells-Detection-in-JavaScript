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

package org.jpos.iso.gui;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.Vector;

/**
 * Called from ISOChannelPanel when you click on it's ISOMeter.
 * It enable field and header visualization by means of visual
 * components such as JTable
 *
 * @see ISOChannelPanel
 * @see ISORequestListenerPanel
 */
public class ISOMsgPanel extends JPanel {

    private static final long serialVersionUID = 7779880613544725704L;
    /**
     * @serial
     */
    ISOMsg m;
    /**
     * @serial
     */
    Vector validFields;
    public ISOMsgPanel(ISOMsg m, boolean withDump) {
        super();
        this.m = m;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createRaisedBevelBorder());
        setValidFields();
        add(createISOMsgTable(), BorderLayout.CENTER);
        if (withDump)
            add(createISOMsgDumpPanel(), BorderLayout.SOUTH);
    }
    public ISOMsgPanel(ISOMsg m) {
        this(m, false);
    }
    private void setValidFields() {
        validFields = new Vector();
        for (int i=0; i<=m.getMaxField(); i++)
            if (m.hasField(i))
                validFields.addElement(new Integer(i));
    }
    private JComponent createISOMsgTable() {
        TableModel dataModel = new AbstractTableModel() {

            private static final long serialVersionUID = 8917029825751856951L;
            public int getColumnCount() {
                return 3;
            }
            public int getRowCount() {
                return validFields.size();
            }
            public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0 :
                        return "#";
                    case 1 :
                        return "Content";
                    case 2 :
                        return "Description";
                    default:
                        return "";
                }
            }
            public Object getValueAt(int row, int col) {
                switch (col) {
                    case 0 :
                        return ((Integer)validFields.elementAt(row));
                    case 1 :
                        try {
                            int index =
                            ((Integer)validFields.elementAt(row)).intValue();

                            Object obj = m.getValue(index);
                            if (obj instanceof String) 
                                return obj.toString();
                            else if (obj instanceof byte[])
                                return ISOUtil.hexString((byte[]) obj);
                            else if (obj instanceof ISOMsg)
                                return "<ISOMsg>";
                        } catch (ISOException e) {
                            e.printStackTrace();
                        }   
                        break;
                    case 2 :
                        int i=((Integer)validFields.elementAt(row)).intValue();
                        ISOPackager p = m.getPackager();
                        return p.getFieldDescription(m,i);
                }
                return "<???>";
            }
        };
        JTable table = new JTable(dataModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(10);
        table.setPreferredScrollableViewportSize(
            new Dimension (500,table.getRowCount()*table.getRowHeight()));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ListSelectionModel rowSM = table.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting())
                    return;

                ListSelectionModel lsm =
                    (ListSelectionModel)e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    int selectedRow = lsm.getMinSelectionIndex();
                    try {
                        int index = ((Integer)
                            validFields.elementAt(selectedRow)).intValue();

                        Object obj = m.getValue(index);
                        if (obj instanceof ISOMsg) {
                            ISOMsg sm = (ISOMsg) obj;
                            JFrame f = new JFrame("ISOMsg field "+index);
                            ISOMsgPanel p = new ISOMsgPanel(sm, false);
                            f.getContentPane().add(p);
                            f.pack();
                            f.show();
                        }
                    } catch (ISOException ex) {
                        ex.printStackTrace();
                    }   
                }
            }
        });

        return new JScrollPane(table);
    }
    JComponent createISOMsgDumpPanel() {
        JPanel p = new JPanel();
        JTextArea t = new JTextArea(3,20);

        p.setLayout(new BorderLayout());
        p.setBackground(Color.white);
        p.setBorder(BorderFactory.createLoweredBevelBorder());
        p.add(new JLabel("Dump", SwingConstants.LEFT),
            BorderLayout.NORTH);
        t.setFont(new Font ("Helvetica", Font.PLAIN, 8));
        t.setLineWrap(true);
        try {
            StringBuffer buf = new StringBuffer();
            if (m.getHeader() != null) 
                buf.append("--[Header]--\n" 
                    +ISOUtil.hexString(m.getHeader()) + "\n--[Msg]--\n");
            byte[] b = m.pack();
            buf.append (ISOUtil.hexString(b));
            t.setText(buf.toString());
        } catch (ISOException e) {
            t.setText(e.toString());
            t.setForeground(Color.red);
        }
        p.add(t, BorderLayout.CENTER);
        return p;
    }
}
