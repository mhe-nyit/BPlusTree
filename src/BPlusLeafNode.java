import java.util.*;

public class BPlusLeafNode extends BPlusNode{
    public static final int MAX_LEAF_NODE = 16;
    public static final int MIN_LEAF_NODE = MAX_LEAF_NODE / 2;


    public int numberOfEntry;
    public BPlusLeafNode leftSibling;
    public BPlusLeafNode rightSibling;
    public Product[] products;

    public BPlusLeafNode(Product product) {
        super();
        this.products = new Product[MAX_LEAF_NODE + 1];
        this.numberOfEntry = 0;
        this.insertProduct(product);
    }

    public BPlusLeafNode(Product[] products, BPlusIndexNode parent) {
        super();
        this.products = products;
        this.numberOfEntry = searchEmpty(products);
        this.parent = parent;
    }

    public void deleteProduct(int index) {
        //System.out.println(Arrays.toString(products));
        products[index] = null;
        //System.out.println(Arrays.toString(products));
        numberOfEntry--;
    }

    public boolean insertProduct(Product product) {
        if (this.isFull()) {
            return false;
        } else {
            int insertPos = 0;
            while (insertPos < numberOfEntry && products[insertPos].compareTo(product) < 0) {
                insertPos++;
            }
            for (int i = numberOfEntry; i > insertPos; i--) {
                products[i] = products[i - 1];
            }
            products[insertPos] = product;
            numberOfEntry++;
            return true;
        }
    }

    public boolean hasLowerThenMinimum() {
        return numberOfEntry < MIN_LEAF_NODE;
    }

    public boolean isFull() {
        return numberOfEntry == MAX_LEAF_NODE;
    }

    public boolean canLend() {
        return numberOfEntry > MIN_LEAF_NODE;
    }

    public boolean canMerge() {
        return numberOfEntry == MIN_LEAF_NODE;
    }

    private int searchEmpty(Product[] products) {
        for (int i = 0; i < products.length; i++) {
            if (products[i] == null) {
                return i;
            }
        }
        return -1;
    }
}
