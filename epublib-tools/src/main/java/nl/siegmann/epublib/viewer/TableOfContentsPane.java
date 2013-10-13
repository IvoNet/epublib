package nl.siegmann.epublib.viewer;

import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a JTree for navigating a Book via its Table of Contents.
 *
 * @author paul
 *
 */
public class TableOfContentsPane extends JPanel implements NavigationEventListener {

    private static final long serialVersionUID = 2277717264176049700L;

    private final Map<String, Collection<DefaultMutableTreeNode>> href2treeNode = new HashMap<String,
            Collection<DefaultMutableTreeNode>>();
    private final JScrollPane scrollPane;
    private final Navigator navigator;
    private JTree tree;

    /**
     * Creates a JTree that displays all the items in the table of contents from the book in SectionWalker.
     * Also sets up a selectionListener that updates the SectionWalker when an item in the tree is selected.
     *
     * @param navigator
     */
    public TableOfContentsPane(final Navigator navigator) {
        super(new GridLayout(1, 0));
        this.navigator = navigator;
        navigator.addNavigationEventListener(this);

        this.scrollPane = new JScrollPane();
        add(this.scrollPane);
        initBook(navigator.getBook());
    }

    /**
     * Wrapper around a TOCReference that gives the TOCReference's title when toString() is called
     * .createTableOfContentsTree
     * @author paul
     *
     */
    private static class TOCItem {
        private final TOCReference tocReference;

        public TOCItem(final TOCReference tocReference) {
            this.tocReference = tocReference;
        }

        public TOCReference getTOCReference() {
            return this.tocReference;
        }

        public String toString() {
            return this.tocReference.getTitle();
        }
    }

    private void addToHref2TreeNode(final Resource resource, final DefaultMutableTreeNode treeNode) {
        if ((resource == null) || StringUtils.isBlank(resource.getHref())) {
            return;
        }
        Collection<DefaultMutableTreeNode> treeNodes = this.href2treeNode.get(resource.getHref());
        if (treeNodes == null) {
            treeNodes = new ArrayList<DefaultMutableTreeNode>();
            this.href2treeNode.put(resource.getHref(), treeNodes);
        }
        treeNodes.add(treeNode);
    }

    private DefaultMutableTreeNode createTree(final Book book) {
        final TOCItem rootTOCItem = new TOCItem(new TOCReference(book.getTitle(), book.getCoverPage()));
        final DefaultMutableTreeNode top = new DefaultMutableTreeNode(rootTOCItem);
        addToHref2TreeNode(book.getCoverPage(), top);
        createNodes(top, book);
        return top;
    }

    private void createNodes(final DefaultMutableTreeNode top, final Book book) {
        addNodesToParent(top, book.getTableOfContents().getTocReferences());
    }

    private void addNodesToParent(final DefaultMutableTreeNode parent, final List<TOCReference> tocReferences) {
        if (tocReferences == null) {
            return;
        }
        for (final TOCReference tocReference : tocReferences) {
            final TOCItem tocItem = new TOCItem(tocReference);
            final DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(tocItem);
            addToHref2TreeNode(tocReference.getResource(), treeNode);
            addNodesToParent(treeNode, tocReference.getChildren());
            parent.add(treeNode);
        }
    }


    @Override
    public void navigationPerformed(final NavigationEvent navigationEvent) {
        if (this == navigationEvent.getSource()) {
            return;
        }
        if (navigationEvent.isBookChanged()) {
            initBook(navigationEvent.getCurrentBook());
            return;
        }
        if (this.tree == null) {
            return;
        }
        if (navigationEvent.getCurrentResource() == null) {
            return;
        }
        final Collection<DefaultMutableTreeNode> treeNodes = this.href2treeNode
                .get(navigationEvent.getCurrentResource().getHref());
        if ((treeNodes == null) || treeNodes.isEmpty()) {
            if (navigationEvent.getCurrentSpinePos() == (navigationEvent.getOldSpinePos() + 1)) {
                return;
            }
            this.tree.setSelectionPath(null);
            return;
        }
        for (final DefaultMutableTreeNode treeNode : treeNodes) {
            final TreeNode[] path = treeNode.getPath();
            final TreePath treePath = new TreePath(path);
            this.tree.setSelectionPath(treePath);
        }
    }

    private void initBook(final Book book) {
        if (book == null) {
            return;
        }
        this.tree = new JTree(createTree(book));
        this.tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(final MouseEvent me) {
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) TableOfContentsPane.this.tree
                        .getLastSelectedPathComponent();
                final TOCItem tocItem = (TOCItem) node.getUserObject();
                TableOfContentsPane.this.navigator
                        .gotoResource(tocItem.getTOCReference().getResource(), tocItem.getTOCReference()
                                .getFragmentId(), TableOfContentsPane.this);
            }
        });

        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
//		tree.setRootVisible(false);
        this.tree.setSelectionRow(0);
        this.scrollPane.getViewport().removeAll();
        this.scrollPane.getViewport().add(this.tree);
    }
}
