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
            System.out.println("0. Search for a Part");
            System.out.println("1. Insert New Record");
            System.out.println("2. Update Record");
            System.out.println("3. Delete Record");
            System.out.println("4. Display Next 10");
            System.out.println("5. Print Statistics");
            System.out.println("6. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            System.out.println();
            switch (choice) {
                case 0:
                    System.out.println("Enter Part ID:");
                    String searchPartID = scanner.nextLine().trim();
                    String searchDescription = bPlusTree.searchNode(searchPartID);
                    
                    if (searchDescription != null) {
                        System.out.println("Description of " + searchPartID + " is: " + searchDescription);
                    } else {
                        System.out.println("Part ID " + searchPartID + " not found.");
                    }
                    
                    break;
                case 1:
                    System.out.println("Enter Part ID:");
                    String partID = scanner.nextLine().trim();
                    System.out.println("Enter Part Description:");
                    String description = scanner.nextLine().trim();

                    // Insert new record
                    bPlusTree.insertNode(partID, description);
                    System.out.println("Record inserted.");
                    break;

                case 2:
                    System.out.println("Enter Part ID to Update:");
                    String updatePartID = scanner.nextLine().trim();
                    System.out.println("Enter New Part Description:");
                    String newDescription = scanner.nextLine().trim();

                    // Update record
                    bPlusTree.updateNode(updatePartID, newDescription);
                    boolean updated = bPlusTree.updateNode(updatePartID, newDescription);
                    System.out.println(updated ? "Record updated." : "Part ID not found.");
                    break;

                case 3:
                    System.out.println("Enter Part ID to Delete:");
                    String deletePartId = scanner.nextLine().trim();

                    // Delete record
                    bPlusTree.deleteNode(deletePartId);
                    boolean deleted = bPlusTree.deleteNode(deletePartId);
                    System.out.println(deleted ? "Record deleted." : "Part ID not found.");
                    break;

                case 4:
                    System.out.println("Enter Part ID to print next 10:");
                    String partId = scanner.nextLine().trim();
                    ArrayList<Product> productList = bPlusTree.getNextTenNode(partId);
                    
                    if (productList == null || productList.isEmpty()) {
                        System.out.println("No records found after " + partId);
                    } else {
	                    for(Product product : productList){
	                        System.out.println("PartId: " + product.getId() + " Description: " + product.getDescription());
	                    }
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
            	if (line.length() < 7) { 
            		continue;
            	}
            	// Specification: description starts at col 16 (index 15), ends at col 80 (index 80).
                
            	String partId = line.substring(0, 7).trim();
                String description = "";
                if (line.length() > 15) {
                    int end = Math.min(line.length(), 80);
                    description = line.substring(15, end).trim();
                }
                if (!partId.isEmpty()) {
                    bPlusTree.insertNode(partId, description);
                }
            }
        } catch (IOException e) {
            System.out.println("File not found.");
        }
        System.out.println("Data loaded.");
    }

    private void saveData() {
        try {
            PrintWriter out = new PrintWriter("partfile.txt");
            for (Product product : bPlusTree.getAllNode()) {
                // Fixed-width format: Part ID cols 1-7, Description cols 16-80
                String formattedLine = String.format("%-7s        %-65s",
                        product.getId(),
                        product.getDescription().length() > 65
                                ? product.getDescription().substring(0, 65)
                                : product.getDescription());
                out.println(formattedLine);
            }
            out.close();
            System.out.println("Data saved.");
        } catch (FileNotFoundException e) {
            System.out.println("Unable to save.");
        }
    }
}