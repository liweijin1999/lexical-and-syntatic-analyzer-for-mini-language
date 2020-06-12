package compilerProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class syntactic_analyzer {

	public static void main(String[] args) {
		File file_dyd = new File("src.dyd");
		File file_err = new File("src.err");
		File file_dys = new File("src.dys");
		
		File file_var = new File("src.var");
		File file_pro = new File("src.pro");
		
		FileWriter fw_var = null;
		FileWriter fw_pro = null;
		FileWriter fw_dys=null;

		try {
			if (file_dys.exists() && file_dys.isFile());
			else {
				try {
					file_dys.createNewFile();
				} catch (IOException e) {
					System.err.println("文件创建失败");
				}
			}
			if (file_err.exists() && file_err.isFile());
			else {
				try {
					file_err.createNewFile();
				} catch (IOException e) {
					System.err.println("文件创建失败");
				}
			}
			if (file_var.exists() && file_var.isFile());
			else {
				try {
					file_var.createNewFile();
				} catch (IOException e) {
					System.err.println("文件创建失败");
				}
			}
			if (file_pro.exists() && file_pro.isFile());
			else {
				try {
					file_pro.createNewFile();
				} catch (IOException e) {
					System.err.println("文件创建失败");
				}
			}
			fw_var = new FileWriter(file_var);
			fw_var.write("");
			fw_pro = new FileWriter(file_pro);
			fw_pro.write("");
			fw_dys=new FileWriter(file_dys);
			fw_dys.write("");
			
//			FileWriter fw_err = new FileWriter(file_err);
//			fw_err.write("");
			
			syntax syn=new syntax();
			syn.set_varFile(file_var);
			syn.set_proFile(file_pro);
			syn.get_dysFile(file_dys);
			syn.get_errFile(file_err);
			syn.initial(file_dyd);
			syn.process();
			syn.writeVar();
			syn.writeProc();
			fw_dys.close();
//			fw_err.close();
			fw_pro.close();
			fw_var.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
