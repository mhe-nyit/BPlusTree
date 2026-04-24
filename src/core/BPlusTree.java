package core;

import java.util.*;

public class BPlusTree {
    private BPlusIndexNode root;
    private BPlusLeafNode leafHead;
    private int totalSplits;
    private int parentSplits;
    private int totalFusions;
    private int parentFusions;
    private int treeDepth;

    public BPlusTree() {
        root = null;
        leafHead = null;
        totalSplits = 0;
        totalFusions = 0;
        parentFusions = 0;
        parentSplits = 0;
        treeDepth = 0;
    }

    public ArrayList<Product> getAllNode() {
        ArrayList<Product> entries = new ArrayList<>();

        BPlusLeafNode leafNode = leafHead;

        while (leafNode != null) {
            for (Product Product : leafNode.products) {
                if (Product != null) entries.add(Product);
            }
            leafNode = leafNode.rightSibling;
        }

        return entries;
    }

    public boolean isEmpty() {
        return leafHead == null;
    }

    public ArrayList<Product> getNextTenNode(String id) {
        if (isEmpty()) { return null; }

        BPlusLeafNode leafNode;
        if (root == null) leafNode = leafHead;
        else leafNode = getPossibleLeafNode(id);

        Product[] products = leafNode.products;
        int index = binarySearch(products, leafNode.numberOfEntry, id);

        if (index < 0) {
            return null;
        } else {
            ArrayList<Product> result = new ArrayList<>();

            while (result.size() != 10) {
                index++;
                if (index < leafNode.numberOfEntry) {
                    if (products[index] != null) {
                        result.add(products[index]);
                    }
                } else {
                    leafNode = leafNode.rightSibling;
                    if (leafNode == null) {
                        break;
                    } else {
                        products = leafNode.products;
                        index = -1;
                    }
                }
            }

            return result;
        }
    }

    public String searchNode(String id) {
        if (isEmpty()) {
            return null;
        }
        BPlusLeafNode leafNode;
        if (root == null) leafNode = leafHead;
        else leafNode = getPossibleLeafNode(id);

        Product[] products = leafNode.products;
        int index = binarySearch(products, leafNode.numberOfEntry, id);
        return index < 0 ? null : products[index].description;
    }

    public void insertNode(String id, String description) {
        if (searchNode(id) != null) {
            return;
        }
        if (isEmpty()) {
            leafHead = new BPlusLeafNode(new Product(id, description));
            treeDepth = 1;
        } else {
            BPlusLeafNode leafNode;
            if (root == null) leafNode = leafHead;
            else leafNode = getPossibleLeafNode(id);

            boolean success = leafNode.insertProduct(new Product(id, description));

            if (!success) {
                leafNode.products[leafNode.numberOfEntry] = new Product(id, description);
                leafNode.numberOfEntry++;

                sortEntries(leafNode.products);

                int mid = leafNode.products.length / 2;
                Product[] half = splitEntries(leafNode, mid);

                totalSplits++;

                if (leafNode.parent != null) {
                    String newParentId = half[0].partId;
                    leafNode.parent.productIDs[leafNode.parent.numberOfEntry - 1] = newParentId;
                    Arrays.sort(leafNode.parent.productIDs, 0, leafNode.parent.numberOfEntry);
                } else {
                    String[] parentIds = new String[BPlusIndexNode.MAX_ENTRY_KEY];
                    parentIds[0] = half[0].partId;
                    BPlusIndexNode parent = new BPlusIndexNode(parentIds);
                    leafNode.parent = parent;
                    parent.addIndexPointer(leafNode);
                }

                BPlusLeafNode newLeafNode = new BPlusLeafNode(half, leafNode.parent);

                int pointerIndex = leafNode.parent.getIndexOfPointer(leafNode) + 1;
                leafNode.parent.insertPointer(newLeafNode, pointerIndex);

                newLeafNode.rightSibling = leafNode.rightSibling;
                if (newLeafNode.rightSibling != null) {
                    newLeafNode.rightSibling.leftSibling = newLeafNode;
                }
                leafNode.rightSibling = newLeafNode;
                newLeafNode.leftSibling = leafNode;

                if (root != null) {
                    BPlusIndexNode in = leafNode.parent;
                    while (in != null) {
                        if (in.isFull()) {
                            splitIndexNode(in);
                        } else {
                            break;
                        }
                        in = in.parent;
                    }
                } else {
                    root = leafNode.parent;
                    treeDepth++;
                }
            }
        }
    }

    public boolean deleteNode(String id) {
        if (isEmpty()) {
            return false;
        } else {
            BPlusLeafNode leafNode;
            if (root == null) leafNode = leafHead;
            else leafNode = getPossibleLeafNode(id);

            int index = binarySearch(leafNode.products, leafNode.numberOfEntry, id);

            if (index < 0) {
                return false;
            } else {
                leafNode.deleteProduct(index);

                if (leafNode.hasLowerThenMinimum()) {
                    BPlusLeafNode sibling;
                    BPlusIndexNode parent = leafNode.parent;

                    if (leafNode.leftSibling != null &&
                            leafNode.leftSibling.canLend() &&
                            leafNode.leftSibling.parent == leafNode.parent) {

                        sibling = leafNode.leftSibling;
                        Product temp = sibling.products[sibling.numberOfEntry - 1];

                        leafNode.insertProduct(temp);
                        sortEntries(leafNode.products);
                        sibling.deleteProduct(sibling.numberOfEntry - 1);

                        int pointerIndex = getIndexOfPointer(parent.indexPointers, leafNode);
                        parent.productIDs[pointerIndex - 1] = leafNode.products[0].partId;

                    } else if (leafNode.rightSibling != null &&
                            leafNode.rightSibling.canLend() &&
                            leafNode.rightSibling.parent == leafNode.parent) {

                        sibling = leafNode.rightSibling;
                        Product temp = sibling.products[0];

                        leafNode.insertProduct(temp);
                        sibling.deleteProduct(0);
                        sortEntries(sibling.products);

                        int pointerIndex = getIndexOfPointer(parent.indexPointers, leafNode);
                        parent.productIDs[pointerIndex] = sibling.products[0].partId;

                    } else if (leafNode.leftSibling != null &&
                            leafNode.leftSibling.canMerge() &&
                            leafNode.leftSibling.parent == leafNode.parent) {

                        sibling = leafNode.leftSibling;
                        int pointerIndex = getIndexOfPointer(parent.indexPointers, leafNode);

                        // Move leafNode's remaining products into the left sibling
                        for (Product p : leafNode.products) {
                            if (p != null) sibling.insertProduct(p);
                        }

                        // Remove separator key at (pointerIndex - 1) with left-shift
                        for (int j = pointerIndex - 1; j < parent.numberOfEntry - 2; j++) {
                            parent.productIDs[j] = parent.productIDs[j + 1];
                        }
                        parent.productIDs[parent.numberOfEntry - 2] = null;

                        // Remove pointer to leafNode at pointerIndex with left-shift
                        for (int j = pointerIndex; j < parent.numberOfEntry - 1; j++) {
                            parent.indexPointers[j] = parent.indexPointers[j + 1];
                        }
                        parent.indexPointers[parent.numberOfEntry - 1] = null;
                        parent.numberOfEntry--;

                        sibling.rightSibling = leafNode.rightSibling;
                        if (leafNode.rightSibling != null) {
                            leafNode.rightSibling.leftSibling = sibling;
                        }

                        totalFusions++;
                        if (parent.parent != null) parentFusions++;

                        if (parent.hasLowerThenMinimum()) {
                            fixDeficiency(parent);
                        }

                    } else if (leafNode.rightSibling != null &&
                            leafNode.rightSibling.canMerge() &&
                            leafNode.rightSibling.parent == leafNode.parent) {

                        sibling = leafNode.rightSibling;
                        int pointerIndex = getIndexOfPointer(parent.indexPointers, leafNode);

                        // Move leafNode's remaining products into the right sibling
                        for (Product p : leafNode.products) {
                            if (p != null) sibling.insertProduct(p);
                        }

                        // Remove separator key at pointerIndex with left-shift
                        for (int j = pointerIndex; j < parent.numberOfEntry - 2; j++) {
                            parent.productIDs[j] = parent.productIDs[j + 1];
                        }
                        parent.productIDs[parent.numberOfEntry - 2] = null;

                        // Remove pointer to leafNode at pointerIndex with left-shift
                        for (int j = pointerIndex; j < parent.numberOfEntry - 1; j++) {
                            parent.indexPointers[j] = parent.indexPointers[j + 1];
                        }
                        parent.indexPointers[parent.numberOfEntry - 1] = null;
                        parent.numberOfEntry--;

                        sibling.leftSibling = leafNode.leftSibling;
                        if (sibling.leftSibling == null) {
                            leafHead = sibling;
                        } else {
                            sibling.leftSibling.rightSibling = sibling;
                        }

                        totalFusions++;

                        if (parent.parent != null) parentFusions++;

                        if (parent.hasLowerThenMinimum()) {
                            fixDeficiency(parent);
                        }
                    } else {
                        sortEntries(leafNode.products);
                    }

                } else if (this.root == null && this.leafHead.numberOfEntry == 0) {
                    this.leafHead = null;
                } else {
                    sortEntries(leafNode.products);
                }

                return true;
            }
        }
    }

    public boolean updateNode(String id, String description) {
        if (isEmpty()) {
            return false;
        }

        BPlusLeafNode leafNode;
        if (root == null) leafNode = leafHead;
        else leafNode = getPossibleLeafNode(id);
        Product[] products = leafNode.products;
        int index = binarySearch(products, leafNode.numberOfEntry, id);

        if (index < 0) {
            return false;
        } else {
            products[index].description = description;
            return true;
        }
    }

    private int binarySearch(Product[] products, int numPairs, String t) {
        Comparator<Product> c = Comparator.comparing(o -> o.partId);
        return Arrays.binarySearch(products, 0, numPairs, new Product(t, ""), c);
    }

    private BPlusLeafNode getPossibleLeafNode(String partId) {
        String[] keys = this.root.productIDs;
        int i;

        for (i = 0; i < this.root.numberOfEntry - 1; i++) {
            if (partId.compareTo(keys[i]) < 0) {
                break;
            }
        }

        BPlusNode child = this.root.indexPointers[i];
        if (child instanceof BPlusLeafNode) {
            return (BPlusLeafNode) child;
        } else {
            return getPossibleLeafNode((BPlusIndexNode) child, partId);
        }
    }

    private BPlusLeafNode getPossibleLeafNode(BPlusIndexNode node, String partId) {
        String[] keys = node.productIDs;
        int i;

        for (i = 0; i < node.numberOfEntry - 1; i++) {
            if (partId.compareTo(keys[i]) < 0) {
                break;
            }
        }

        BPlusNode childBPlusNode = node.indexPointers[i];
        if (childBPlusNode instanceof BPlusLeafNode) {
            return (BPlusLeafNode) childBPlusNode;
        } else {
            return getPossibleLeafNode((BPlusIndexNode) node.indexPointers[i], partId);
        }
    }

    private int getIndexOfPointer(BPlusNode[] pointers, BPlusLeafNode node) {
        for (int i = 0; i < pointers.length; i++) {
            if (pointers[i] == node) {
                return i;
            }
        }
        return -1;
    }

    private int getMidPoint() {
        return (int) Math.ceil((BPlusIndexNode.MAX_ENTRY_KEY + 1) / 2.0) - 1;
    }

    private void fixDeficiency(BPlusIndexNode in) {
        BPlusIndexNode sibling;
        BPlusIndexNode parent = in.parent;

        if (this.root == in) {
            for (int i = 0; i < in.indexPointers.length; i++) {
                if (in.indexPointers[i] != null) {
                    if (in.indexPointers[i] instanceof BPlusIndexNode) {
                        this.root = (BPlusIndexNode) in.indexPointers[i];
                        this.root.parent = null;
                        treeDepth--;
                    } else if (in.indexPointers[i] instanceof BPlusLeafNode) {
                        this.root = null;
                        ((BPlusLeafNode) in.indexPointers[i]).parent = null;
                        treeDepth--;
                    }
                    break;
                }
            }
        } else if (in.leftSibling != null && in.leftSibling.canLend()) {
            sibling = in.leftSibling;

            String borrowedKey = sibling.productIDs[sibling.numberOfEntry - 1];
            BPlusNode borrowedPointer = sibling.indexPointers[sibling.numberOfEntry];

            for (int i = in.numberOfEntry - 1; i >= 0; i--) {
                in.productIDs[i + 1] = in.productIDs[i];
            }
            for (int i = in.numberOfEntry; i >= 0; i--) {
                in.indexPointers[i + 1] = in.indexPointers[i];
            }

            int parentIndex = parent.getIndexOfPointer(in);
            in.productIDs[0] = parent.productIDs[parentIndex - 1];
            in.indexPointers[0] = borrowedPointer;
            if (borrowedPointer != null) borrowedPointer.parent = in;
            in.numberOfEntry++;

            parent.productIDs[parentIndex - 1] = borrowedKey;

            sibling.productIDs[sibling.numberOfEntry - 1] = null;
            sibling.indexPointers[sibling.numberOfEntry] = null;
            sibling.numberOfEntry--;

        } else if (in.rightSibling != null && in.rightSibling.canLend()) {
            sibling = in.rightSibling;

            int parentIndex = parent.getIndexOfPointer(in);
            String siblingFirstId = sibling.productIDs[0];
            BPlusNode pointer = sibling.indexPointers[0];

            in.productIDs[in.numberOfEntry - 1] = parent.productIDs[parentIndex];
            in.indexPointers[in.numberOfEntry] = pointer;

            if (pointer != null) {
                pointer.parent = in;
            }
            in.numberOfEntry++;

            parent.productIDs[parentIndex] = siblingFirstId;

            for (int i = 0; i < sibling.numberOfEntry - 1; i++) {
                sibling.productIDs[i] = sibling.productIDs[i + 1];
            }
            for (int i = 0; i < sibling.numberOfEntry; i++) {
                sibling.indexPointers[i] = sibling.indexPointers[i + 1];
            }
            sibling.productIDs[sibling.numberOfEntry - 1] = null;
            sibling.indexPointers[sibling.numberOfEntry] = null;
            sibling.numberOfEntry--;

        } else if (in.leftSibling != null && in.leftSibling.canMerge()) {
            sibling = in.leftSibling;

            int parentIndex = parent.getIndexOfPointer(sibling);
            String separatorKey = parent.productIDs[parentIndex];

            int sibKeyCount = sibling.numberOfEntry - 1;
            int inKeyCount  = in.numberOfEntry - 1;

            // Append separator key after sibling's last key, then in's keys
            sibling.productIDs[sibKeyCount] = separatorKey;
            for (int i = 0; i < inKeyCount; i++) {
                sibling.productIDs[sibKeyCount + 1 + i] = in.productIDs[i];
            }

            // Append in's pointers after sibling's last pointer, updating their parent
            for (int i = 0; i < in.numberOfEntry; i++) {
                sibling.indexPointers[sibling.numberOfEntry + i] = in.indexPointers[i];
                if (in.indexPointers[i] != null) {
                    in.indexPointers[i].parent = sibling;
                }
            }

            sibling.numberOfEntry += in.numberOfEntry;

            // Remove separator key at parentIndex from parent with left-shift
            for (int i = parentIndex; i < parent.numberOfEntry - 2; i++) {
                parent.productIDs[i] = parent.productIDs[i + 1];
            }
            parent.productIDs[parent.numberOfEntry - 2] = null;

            // Remove pointer to 'in' at parentIndex + 1 from parent with left-shift
            for (int i = parentIndex + 1; i < parent.numberOfEntry - 1; i++) {
                parent.indexPointers[i] = parent.indexPointers[i + 1];
            }
            parent.indexPointers[parent.numberOfEntry - 1] = null;
            parent.numberOfEntry--;

            sibling.rightSibling = in.rightSibling;
            if (in.rightSibling != null) {
                in.rightSibling.leftSibling = sibling;
            }

            totalFusions++;
            if (in.parent != null) {
                parentFusions++;
            }
        } else if (in.rightSibling != null && in.rightSibling.canMerge()) {
            sibling = in.rightSibling;

            int parentIndex = parent.getIndexOfPointer(in);
            String separatorKey = parent.productIDs[parentIndex];

            int inKeyCount  = in.numberOfEntry - 1;
            int sibKeyCount = sibling.numberOfEntry - 1;

            // Shift sibling's existing keys right to make room for in's keys + separator
            for (int i = sibKeyCount - 1; i >= 0; i--) {
                sibling.productIDs[i + inKeyCount + 1] = sibling.productIDs[i];
                sibling.productIDs[i] = null;
            }
            // Shift sibling's existing pointers right to make room for in's pointers
            for (int i = sibling.numberOfEntry - 1; i >= 0; i--) {
                sibling.indexPointers[i + in.numberOfEntry] = sibling.indexPointers[i];
                sibling.indexPointers[i] = null;
            }

            // Copy in's keys into the front of sibling, then add separator
            for (int i = 0; i < inKeyCount; i++) {
                sibling.productIDs[i] = in.productIDs[i];
            }
            sibling.productIDs[inKeyCount] = separatorKey;

            // Copy in's pointers into the front of sibling, updating their parent
            for (int i = 0; i < in.numberOfEntry; i++) {
                sibling.indexPointers[i] = in.indexPointers[i];
                if (in.indexPointers[i] != null) {
                    in.indexPointers[i].parent = sibling;
                }
            }

            sibling.numberOfEntry += in.numberOfEntry;

            // Remove separator key at parentIndex from parent with left-shift
            for (int i = parentIndex; i < parent.numberOfEntry - 2; i++) {
                parent.productIDs[i] = parent.productIDs[i + 1];
            }
            parent.productIDs[parent.numberOfEntry - 2] = null;

            // Remove pointer to 'in' at parentIndex from parent with left-shift
            for (int i = parentIndex; i < parent.numberOfEntry - 1; i++) {
                parent.indexPointers[i] = parent.indexPointers[i + 1];
            }
            parent.indexPointers[parent.numberOfEntry - 1] = null;
            parent.numberOfEntry--;

            sibling.leftSibling = in.leftSibling;
            if (in.leftSibling != null) {
                in.leftSibling.rightSibling = sibling;
            }

            totalFusions++;
            if (in.parent != null) {
                parentFusions++;
            }
        }

        if (parent != null && parent.hasLowerThenMinimum()) {
            fixDeficiency(parent);
        }
    }

    private int searchEmpty(BPlusNode[] pointers) {
        for (int i = 0; i < pointers.length; i++) {
            if (pointers[i] == null) {
                return i;
            }
        }
        return -1;
    }

    private void sortEntries(Product[] dictionary) {
        Arrays.sort(dictionary, (node1, node2) -> {
            if (node1 == null && node2 == null) return 0;
            if (node1 == null) return 1;
            if (node2 == null) return -1;
            return node1.compareTo(node2);
        });
    }

    private BPlusNode[] splitChildPointers(BPlusIndexNode in, int split) {
        BPlusNode[] pointers = in.indexPointers;
        BPlusNode[] halfPointers = new BPlusNode[pointers.length];

        for (int i = split + 1; i < pointers.length; i++) {
            halfPointers[i - split - 1] = pointers[i];
            in.removePointer(i);
        }

        return halfPointers;
    }

    private Product[] splitEntries(BPlusLeafNode leafNode, int split) {
        Product[] dictionary = leafNode.products;
        Product[] half = new Product[dictionary.length];

        for (int i = split; i < dictionary.length; i++) {
            half[i - split] = dictionary[i];
            leafNode.deleteProduct(i);
        }

        return half;
    }

    private void splitIndexNode(BPlusIndexNode indexNode) {
        BPlusIndexNode parent = indexNode.parent;

        int mid = getMidPoint();
        String newParentKey = indexNode.productIDs[mid];
        String[] halfKeys = splitIds(indexNode.productIDs, mid);
        BPlusNode[] halfPointers = splitChildPointers(indexNode, mid);

        indexNode.numberOfEntry = searchEmpty(indexNode.indexPointers);

        BPlusIndexNode sibling = new BPlusIndexNode(halfKeys, halfPointers);
        for (BPlusNode pointer : halfPointers) {
            if (pointer != null) {
                pointer.parent = sibling;
            }
        }

        sibling.rightSibling = indexNode.rightSibling;
        if (sibling.rightSibling != null) {
            sibling.rightSibling.leftSibling = sibling;
        }
        indexNode.rightSibling = sibling;
        sibling.leftSibling = indexNode;

        if (parent == null) {
            String[] keys = new String[BPlusIndexNode.MAX_ENTRY_KEY];
            keys[0] = newParentKey;
            BPlusIndexNode newRoot = new BPlusIndexNode(keys);
            newRoot.addIndexPointer(indexNode);
            newRoot.addIndexPointer(sibling);
            this.root = newRoot;

            indexNode.parent = newRoot;
            sibling.parent = newRoot;
            totalSplits++;
            treeDepth++;
        } else {
            parent.productIDs[parent.numberOfEntry - 1] = newParentKey;
            Arrays.sort(parent.productIDs, 0, parent.numberOfEntry);

            int pointerIndex = parent.getIndexOfPointer(indexNode) + 1;
            parent.insertPointer(sibling, pointerIndex);
            sibling.parent = parent;

            totalSplits++;

            if (parent.parent != null) {
                parentSplits++;
            }
        }
    }

    private String[] splitIds(String[] ids, int split) {
        String[] half = new String[BPlusIndexNode.MAX_ENTRY_KEY];
        ids[split] = null;

        for (int i = split + 1; i < ids.length; i++) {
            half[i - split - 1] = ids[i];
            ids[i] = null;
        }

        return half;
    }

    public void printStatistics() {
        System.out.println("Total Splits:        " + totalSplits);
        System.out.println("Total Parent Splits: " + parentSplits);
        System.out.println("Total Fusions:       " + totalFusions);
        System.out.println("Total Parent Fusions:" + parentFusions);
        System.out.println("Tree Depth:          " + treeDepth);
    }

    public String getStatistics() {
        return "<html>" +
               "<b>B+ Tree Statistics</b><br><br>" +
               "Total Splits:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + totalSplits   + "<br>" +
               "Parent Splits:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + parentSplits  + "<br>" +
               "Total Fusions:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + totalFusions  + "<br>" +
               "Parent Fusions:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + parentFusions + "<br>" +
               "Tree Depth:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + treeDepth     +
               "</html>";
    }
}
