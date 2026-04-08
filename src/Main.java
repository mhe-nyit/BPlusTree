import java.io.*;
import java.util.*;
import java.util.Scanner;

public class Main {

    static BPlusTree bPlusTree = new BPlusTree();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Main mainInstance = new Main();
        mainInstance.loadData();

        while (true) {

            System.out.println("Choose an option:");
            System.out.println("0. Insert New Record");
            System.out.println("1. Insert New Record");
            System.out.println("2. Update Record");
            System.out.println("3. Delete Record");
            System.out.println("4. Print Get Next 10");
            System.out.println("5. Search for Statistics");
            System.out.println("6. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 0:
                    System.out.println("Enter Part ID:");
                    String searchPartID = scanner.nextLine().trim();
                    String searchDescription = bPlusTree.searchNode(searchPartID);
                    System.out.println("Description of " + searchPartID+ " is: " + searchDescription);
                    break;
                case 1:
                    System.out.println("Enter Part ID:");
                    String partID = scanner.nextLine().trim();
                    System.out.println("Enter Part Description:");
                    String description = scanner.nextLine().trim();

                    // Insert new record
                    bPlusTree.insertNode(partID, description);
                    break;

                case 2:
                    System.out.println("Enter Part ID to Update:");
                    String updatePartID = scanner.nextLine().trim();
                    System.out.println("Enter New Part Description:");
                    String newDescription = scanner.nextLine().trim();

                    // Update record
                    bPlusTree.updateNode(updatePartID, newDescription);
                    break;

                case 3:
                    System.out.println("Enter Part ID to Delete:");
                    String deletePartId = scanner.nextLine().trim();

                    // Delete record
                    bPlusTree.deleteNode(deletePartId);
                    break;

                case 4:
                    System.out.println("Enter Part ID to print next 10:");
                    String partId = scanner.nextLine().trim();
                    ArrayList<Product> productList = bPlusTree.getNextTenNode(partId);
                    for(Product product : productList){
                        System.out.println("PartId: " + product.getId() + " Description: " + product.getDescription());
                    }
                    break;

                case 5:
                    // Display statistics
                    bPlusTree.printStaistic();
                    break;

                case 6:
                    // Save changes to file and exit
                    mainInstance.saveData();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a number between 0 and 6.");
            }

            // Ask the user if changes should be saved to the file
            System.out.println("Do you want to save changes to the file? (Y/N)");
            String saveChoice = scanner.nextLine().trim().toLowerCase();
            if (saveChoice.equals("y")) {
                mainInstance.saveData();
                System.out.println("Changes saved to file successfully.");
            }
        }
    }

    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("partfile.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String partId = line.substring(0, 7).trim();
                int lengthOfDescription = 15 + line.substring(15).length();
                String description = line.substring(15, lengthOfDescription).trim();  // Adjust the substring length based on your file format
                bPlusTree.insertNode(partId, description);
            }
        } catch (IOException e) {
            System.out.println("File not fount.");
        }
        System.out.println("Data loaded.");
    }

    private void saveData() {
        try {
            PrintWriter out = new PrintWriter("partfile.txt");
            for (Product Product : bPlusTree.getAllNode()) {
                String formattedLine = String.format("%-7s        %s", Product.getId(), Product.getDescription());
                out.println(formattedLine);
            }
            out.close();
            System.out.println("Data saved.");
        } catch (FileNotFoundException e) {
            System.out.println("Unable to save.");
        }
    }
}