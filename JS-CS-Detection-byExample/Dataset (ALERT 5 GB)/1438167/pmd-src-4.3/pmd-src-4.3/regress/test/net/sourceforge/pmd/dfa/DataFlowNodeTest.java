package test.net.sourceforge.pmd.dfa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.dfa.DataFlowNode;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.NodeType;
import net.sourceforge.pmd.dfa.StartOrEndDataFlowNode;

import org.junit.Test;

import java.util.LinkedList;

public class DataFlowNodeTest {

    @Test
    public void testAddPathToChild() {
        DataFlowNode parent = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 10, false);
        IDataFlowNode child = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 12, false);
        parent.addPathToChild(child);
        assertEquals(parent.getChildren().size(), 1);
        assertTrue(child.getParents().contains(parent));
        assertTrue(parent.getChildren().contains(child));
    }

    @Test
    public void testRemovePathToChild() {
        DataFlowNode parent = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 10, false);
        IDataFlowNode child = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 12, false);
        parent.addPathToChild(child);

        assertTrue(parent.removePathToChild(child));
        assertFalse(child.getParents().contains(parent));
        assertFalse(parent.getChildren().contains(child));
    }

    @Test
    public void testRemovePathWithNonChild() {
        DataFlowNode parent = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 10, false);
        IDataFlowNode child = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 12, false);
        assertFalse(parent.removePathToChild(child));
    }

    @Test
    public void testReverseParentPathsTo() {
        DataFlowNode parent1 = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 10, false);
        DataFlowNode parent2 = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 12, false);
        IDataFlowNode child1 = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 13, false);
        IDataFlowNode child2 = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 13, false);
        parent1.addPathToChild(child1);
        parent2.addPathToChild(child1);
        assertTrue(parent1.getChildren().contains(child1));

        child1.reverseParentPathsTo(child2);
        assertTrue(parent1.getChildren().contains(child2));
        assertFalse(parent1.getChildren().contains(child1));
        assertTrue(parent2.getChildren().contains(child2));
        assertFalse(parent2.getChildren().contains(child1));

        assertEquals(0, child1.getParents().size());
        assertEquals(2, child2.getParents().size());
    }

    @Test
    public void testSetType() {
        DataFlowNode node = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 10, false);
        node.setType(NodeType.BREAK_STATEMENT);
        assertTrue(node.isType(NodeType.BREAK_STATEMENT));
        assertFalse(node.isType(NodeType.CASE_LAST_STATEMENT));
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(DataFlowNodeTest.class);
    }
}
