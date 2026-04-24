package core;

public class Product implements Comparable<Product> {
    public String partId;
    public String description;

    public Product(String partId, String description) {
        this.partId = partId;
        this.description = description;
    }

    public String getId() {
        return partId;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(Product o) {
        return partId.compareTo(o.partId);
    }

    @Override
    public String toString() {
        return "[" + partId + " : " + description + ']';
    }
}
