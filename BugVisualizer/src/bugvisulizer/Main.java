package bugvisulizer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;

import bugvisulizer.DiskUtil.PathVisitor;
import cn.edu.thu.tsmart.tool.bd.report.FaultResult;
import cn.edu.thu.tsmart.tool.bd.report.Report;
import cn.edu.thu.tsmart.tool.bd.report.section.AbstractSection;
import cn.edu.thu.tsmart.tool.bd.report.section.Location;
import cn.edu.thu.tsmart.tool.bd.report.section.TransferRelation;
import cn.edu.thu.tsmart.tool.bd.report.util.ResultUtil;

public class Main {
	public static String projectPath = "home";    
	public static String reportPath = "data/test/test-report-real-01/report/result.xml";
	public static String escape(String src) {  
        int i;  
        char j;  
        StringBuffer tmp = new StringBuffer();  
        tmp.ensureCapacity(src.length() * 6);  
        for (i = 0; i < src.length(); i++) {  
            j = src.charAt(i);  
            if (Character.isDigit(j) || Character.isLowerCase(j)  
                    || Character.isUpperCase(j))  
                tmp.append(j);  
            else if (j < 256) {  
                tmp.append("%");  
                if (j < 16)  
                    tmp.append("0");  
                tmp.append(Integer.toString(j, 16));  
            } else {  
                tmp.append("%u");  
                tmp.append(Integer.toString(j, 16));  
            }  
        }  
        return tmp.toString();  
    }
	public static void main(String[] args) throws Exception {
		final List<File> files = new ArrayList<>();
		if(!projectPath.endsWith("/")){
			projectPath = projectPath + "/";					//文件夹的结尾默认加上'/'
		}
		DiskUtil.traverse(projectPath, new PathVisitor() {
			@Override
			public boolean visit(File path) {
				//if (path.isFile()) {
					files.add(path);
				//}
				return true;
			}
		});
		String htmlPart = Joiner.on(" , ").join(FluentIterable.from(files).transform(new Function<File, String>() {
			@Override
			public String apply(File f) {
				if(f.isDirectory()){
					return "\"" + f.getPath().replace('\\', '/') + '/' + "\"";
				}else{
					return "\"" + f.getPath().replace('\\', '/')+ "\"";
				}
			}
		}));
		
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(htmlPart);
		sb.append("]");
		
		String htmlPart2 = Joiner.on(" , ").join(FluentIterable.from(files).transform(new Function<File, String>() {
			@Override
			public String apply(File f) {
				if(f.isFile()){
					String fileName=f.getName();
					String prefix=fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
					if(!prefix.equals("class")&&!prefix.equals("jar") ){
						try {
							FileReader fr = new FileReader(f);
							int fileLen = (int)f.length();
							char[] chars = new char[fileLen];
							fr.read(chars);
							StringBuilder sb_temp = new StringBuilder();
							sb_temp.append("\"");
							sb_temp.append(f.getPath().replace('\\', '/'));
							sb_temp.append("\":\"");
							String str = new String(chars);
							sb_temp.append(escape(str));
							sb_temp.append("\"");
							return sb_temp.toString();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return "\"\":\"\"";
			}
		}));
		StringBuilder sb2 = new StringBuilder();
		sb2.append("{");
		sb2.append(htmlPart2);
		sb2.append("}");
		
		String htmlpart3 = new String();
		String htmlpart4 = new String();
		try {
			Report report = ResultUtil.readFromFile(reportPath);
			final List<String> strs = new ArrayList();
			final List<String> strs2 = new ArrayList();
			final List<String> strs3 = new ArrayList();
			for (FaultResult fault : report.getFaultResults()) {
				StringBuilder sb_temp = new StringBuilder();
				StringBuilder sb_temp2 = new StringBuilder();
				sb_temp.append("{id:");
				sb_temp.append(fault.getId());
				sb_temp.append(",severity:\"");
				sb_temp.append(fault.getSeverity());
				sb_temp.append("\",confidence:\"");
				sb_temp.append(fault.getConfidence());
				sb_temp.append("\",weakness:\"");
				sb_temp.append(fault.getWeakness());
				sb_temp.append("\"}");
				strs.add(sb_temp.toString());
				sb_temp2.append("\"");
				sb_temp2.append(fault.getId());
				sb_temp2.append("\":");
				
				List<AbstractSection> section =  fault.getWitness();
				for(AbstractSection s : section){
					if(s.getOrientation().getFile().equals("<none>")){
						continue;
					}
					if (s instanceof Location) {
						//Location loc = (Location) s;
						continue;
					} else if (s instanceof TransferRelation) {
						StringBuilder node = new StringBuilder();
						node.append("{startline:");
						TransferRelation transfer = (TransferRelation) s;
						node.append(transfer.getOrientation().getStartCoordinate().getLineNumber());
						node.append(", endline:");
						node.append(transfer.getOrientation().getEndCoordinate().getLineNumber());
						node.append(", filePath:\"");
						node.append(transfer.getOrientation().getFile());
						node.append("\", supplementation:\"");
						node.append(transfer.getSupplementation());
						node.append("\"}");
						strs2.add(node.toString());
					}
				}
				StringBuilder nodeArray = new StringBuilder();
				nodeArray.append("[");
				nodeArray.append(Joiner.on(" , ").join(strs2));
				nodeArray.append("]");
				sb_temp2.append(nodeArray.toString());
				strs3.add(sb_temp2.toString());
				
			}
			htmlpart3 = Joiner.on(" , ").join(strs);
			htmlpart4 = Joiner.on(" , ").join(strs3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		StringBuilder sb3 = new StringBuilder();
		sb3.append("[");
		sb3.append(htmlpart3);
		sb3.append("]");
		
		StringBuilder sb4 = new StringBuilder();
		sb4.append("{");
		sb4.append(htmlpart4);
		sb4.append("}");
		
		FileWriter writer = new FileWriter("data/template/data/data.js");
		writer.write("var protoDic = " + sb.toString());
		writer.write(";\nvar projectPath = \"" + projectPath + "\"");
		writer.write(";\nvar _SOURCE_CODE_SET = " + sb2.toString());
		writer.write(";\nvar _FAULTS_SET = " + sb3.toString());
		writer.write(";\nvar faultID_Path_Dic = " + sb4.toString() + ";");
		writer.close();
		System.out.println("done");
	}
}
