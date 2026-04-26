# BPlusTree

CSCI 651: Algorithm Concepts - Spring 2026

## Latest Release 🔗

<a href="https://github.com/mhe-nyit/BPlusTree/releases/download/prod-release/partscatalog.jar">
  <img src="https://img.shields.io/badge/Download-partscatalog.jar-darkblue?style=for-the-badge" />
</a>

## How to Run

### Requirements
- Java 17+ or higher

### Steps

1. Clone the repository
2. Open terminal and navigate to the project directory
3. Compile source files:

   **Windows**
   ```
   javac -d out src\core\*.java src\ui\*.java src\Main.java
   ```

   **Mac / Linux**
   ```
   find src -name "*.java" | xargs javac -d out
   ```

4. Run the application:
   ```
   java -cp out Main
   ```
5. Click **Import a file** or drag `partfile.txt` into the drop zone
6. Use the search bar to look up a Part ID, or use the **Insert / Delete / Update / Next 10 Results** buttons to manage records
7. On close, you will be prompted to save changes back to the file

### Notes
- Statistics (total splits, fusions, tree depth) are printed to the console on exit
