package core;

import java.io.*;

public class FileIO {

	private FileIO() {
	}

	public static int load(File file, BPlusTree tree) throws IOException {
		int loaded = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.length() < 7)
					continue;
				String partId = line.substring(0, 7).trim();
				String description = "";
				if (line.length() > 15) {
					int end = Math.min(line.length(), 80);
					description = line.substring(15, end).trim();
				}
				if (!partId.isEmpty()) {
					tree.insertNode(partId, description);
					loaded++;
				}
			}
		}
		return loaded;
	}

	public static void save(File file, BPlusTree tree) throws FileNotFoundException {
		try (PrintWriter out = new PrintWriter(file)) {
			for (Product p : tree.getAllNode()) {
				String desc = p.getDescription();
				if (desc.length() > 65)
					desc = desc.substring(0, 65);
				String line = String.format("%-7s        %-65s", p.getId(), desc);
				out.println(line);
			}
		}
	}
}
