package net.sourceforge.pmd.util.viewer.gui;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.model.ViewerModelEvent;
import net.sourceforge.pmd.util.viewer.model.ViewerModelListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.util.Vector;

/**
 * A panel showing XPath expression evaluation results
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id: EvaluationResultsPanel.java 4217 2006-02-10 14:15:31Z tomcopeland $
 */
public class EvaluationResultsPanel extends JPanel implements ViewerModelListener {
    private ViewerModel model;
    private JList list;

    /**
     * constructs the panel
     *
     * @param model model to refer to
     */
    public EvaluationResultsPanel(ViewerModel model) {
        super(new BorderLayout());

        this.model = model;

        init();
    }

    private void init() {
        model.addViewerModelListener(this);

        list = new JList();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (list.getSelectedValue() != null) {
                    model.selectNode((SimpleNode) list.getSelectedValue(), EvaluationResultsPanel.this);
                }
            }
        });

        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    /**
     * @see ViewerModelListener#viewerModelChanged(ViewerModelEvent)
     */
    public void viewerModelChanged(ViewerModelEvent e) {
        switch (e.getReason()) {
            case ViewerModelEvent.PATH_EXPRESSION_EVALUATED:

                if (e.getSource() != this) {
                    list.setListData(new Vector(model.getLastEvaluationResults()));
                }

                break;

            case ViewerModelEvent.CODE_RECOMPILED:
                list.setListData(new Vector(0));

                break;
        }
    }
}
