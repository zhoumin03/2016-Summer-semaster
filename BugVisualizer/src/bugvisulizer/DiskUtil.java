package bugvisulizer;

import java.io.File;

public class DiskUtil {
	public static interface PathVisitor {
		public boolean visit(File path);
	}
	
	public static void traverse(String root, PathVisitor visitor) {
		traverse(new File(root), visitor);
	}
	
	public static void traverse(File file, PathVisitor visitor) {
		if (file.exists() && visitor.visit(file) && file.isDirectory()) {
			for (File sub : file.listFiles())
				traverse(sub, visitor);
		}
	}
}
