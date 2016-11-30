package net.sourceforge.pmd.util.viewer.gui;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_3;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.TargetJDK1_6;
import net.sourceforge.pmd.TargetJDK1_7;
import net.sourceforge.pmd.TargetJDKVersion;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.model.ViewerModelEvent;
import net.sourceforge.pmd.util.viewer.model.ViewerModelListener;
import net.sourceforge.pmd.util.viewer.util.NLS;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * viewer's main frame
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id: MainFrame.java 5874 2008-03-07 01:59:30Z xlv $
 */

public class MainFrame
        extends JFrame
        implements ActionListener, ActionCommands, ViewerModelListener {
    private ViewerModel model;
    private SourceCodePanel sourcePanel;
    private XPathPanel xPathPanel;
    private JButton evalBtn;
    private JLabel statusLbl;
    private JRadioButtonMenuItem jdk13MenuItem;
    private JRadioButtonMenuItem jdk14MenuItem;
    private JRadioButtonMenuItem jdk15MenuItem;	//NOPMD
    private JRadioButtonMenuItem jdk16MenuItem;
    private JRadioButtonMenuItem jdk17MenuItem;

    /**
     * constructs and shows the frame
     */
    public MainFrame() {
        super(NLS.nls("MAIN.FRAME.TITLE") + " (v " + PMD.VERSION + ')');
        init();
    }

    private void init() {
        model = new ViewerModel();
        model.addViewerModelListener(this);
        sourcePanel = new SourceCodePanel(model);
        ASTPanel astPanel = new ASTPanel(model);
        xPathPanel = new XPathPanel(model);
        getContentPane().setLayout(new BorderLayout());
        JSplitPane editingPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sourcePanel, astPanel);
        editingPane.setResizeWeight(0.5d);
        JPanel interactionsPane = new JPanel(new BorderLayout());
        interactionsPane.add(xPathPanel, BorderLayout.SOUTH);
        interactionsPane.add(editingPane, BorderLayout.CENTER);
        getContentPane().add(interactionsPane, BorderLayout.CENTER);
        JButton compileBtn = new JButton(NLS.nls("MAIN.FRAME.COMPILE_BUTTON.TITLE"));
        compileBtn.setActionCommand(COMPILE_ACTION);
        compileBtn.addActionListener(this);
        evalBtn = new JButton(NLS.nls("MAIN.FRAME.EVALUATE_BUTTON.TITLE"));
        evalBtn.setActionCommand(EVALUATE_ACTION);
        evalBtn.addActionListener(this);
        evalBtn.setEnabled(false);
        statusLbl = new JLabel();
        statusLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPane.add(compileBtn);
        btnPane.add(evalBtn);
        btnPane.add(statusLbl);
        getContentPane().add(btnPane, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("JDK");
        ButtonGroup group = new ButtonGroup();
        jdk13MenuItem = new JRadioButtonMenuItem("JDK 1.3");
        jdk13MenuItem.setSelected(false);
        group.add(jdk13MenuItem);
        menu.add(jdk13MenuItem);
        jdk14MenuItem = new JRadioButtonMenuItem("JDK 1.4");
        jdk14MenuItem.setSelected(true);
        group.add(jdk14MenuItem);
        menu.add(jdk14MenuItem);
        jdk15MenuItem = new JRadioButtonMenuItem("JDK 1.5");
        jdk15MenuItem.setSelected(false);
        group.add(jdk15MenuItem);
        menu.add(jdk15MenuItem);
        jdk16MenuItem = new JRadioButtonMenuItem("JDK 1.6");
        jdk16MenuItem.setSelected(false);
        group.add(jdk16MenuItem);
        menu.add(jdk16MenuItem);
        jdk17MenuItem = new JRadioButtonMenuItem("JDK 1.7");
        jdk17MenuItem.setSelected(false);
        group.add(jdk17MenuItem);
        menu.add(jdk17MenuItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setSize(800, 600);
        setVisible(true);
    }

    private TargetJDKVersion createJDKVersion() {
        if (jdk14MenuItem.isSelected()) {
            return new TargetJDK1_4();
        } else if (jdk13MenuItem.isSelected()) {
            return new TargetJDK1_3();
        } else if (jdk16MenuItem.isSelected()) {
            return new TargetJDK1_6();
        } else if (jdk17MenuItem.isSelected()) {
            return new TargetJDK1_7();
        }
        return new TargetJDK1_5();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        long t0, t1;
        if (command.equals(COMPILE_ACTION)) {
            try {
                t0 = System.currentTimeMillis();
                model.commitSource(sourcePanel.getSourceCode(), createJDKVersion());
                t1 = System.currentTimeMillis();
                setStatus(NLS.nls("MAIN.FRAME.COMPILATION.TOOK") + " " + (t1 - t0) + " ms");
            } catch (ParseException exc) {
                setStatus(NLS.nls("MAIN.FRAME.COMPILATION.PROBLEM") + " " + exc.toString());
                new ParseExceptionHandler(this, exc);
            }
        } else if (command.equals(EVALUATE_ACTION)) {
            try {
                t0 = System.currentTimeMillis();
                model.evaluateXPathExpression(xPathPanel.getXPathExpression(), this);
                t1 = System.currentTimeMillis();
                setStatus(NLS.nls("MAIN.FRAME.EVALUATION.TOOK") + " " + (t1 - t0) + " ms");
            } catch (Exception exc) {
                setStatus(NLS.nls("MAIN.FRAME.EVALUATION.PROBLEM") + " " + exc.toString());
                new ParseExceptionHandler(this, exc);
            }
        }
    }

    /**
     * Sets the status bar message
     *
     * @param string the new status, the empty string will be set if the value is <code>null</code>
     */
    private void setStatus(String string) {
        statusLbl.setText(string == null ? "" : string);
    }

    /**
     * @see ViewerModelListener#viewerModelChanged(ViewerModelEvent)
     */
    public void viewerModelChanged(ViewerModelEvent e) {
        evalBtn.setEnabled(model.hasCompiledTree());
    }
}
