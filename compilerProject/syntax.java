package compilerProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

class varDetail{
	String vname;
	String vproc;
	String vtype;
	int vkind;
	int vlev;
	int vadr;
	varDetail() {}
	varDetail(String vname, String vproc, String vtype, int vkind, int vlev, int vadr) {
		super();
		this.vname = vname;
		this.vproc = vproc;
		this.vtype = vtype;
		this.vkind = vkind;
		this.vlev = vlev;
		this.vadr = vadr;
	}
	
}
class procDetail{
	String pname;
	String ptype;
	int plev;
	int fadr;
	int ladr;	
	procDetail() {}
	procDetail(String pname, String ptype, int plev, int fadr, int ladr) {
		this.pname = pname;
		this.ptype = ptype;
		this.plev = plev;
		this.fadr = fadr;
		this.ladr = ladr;
	}
	
}

public class syntax {
	String[] sym=new String[100];	//用来存放二元式中的单词
	String symbol;		//当前单词
	String[] kinds=new String[100];	//用来存放二元式中的种类
	String kind;		//当前种类
	
	int index=0;		//当前单词位置
	int level=0;		//程序的层次
	int line=0;			//当前运行的行数
	int address=0;		//变量在变量表中的位置，address+1为变量的数目
	String last_word;	//上一个单词
	int procFlag=0;		//判断标识符是否为进程，是为1，否为0
	String type;		//变量类型
	int vkind;			//是否是参数
	int proc_number=0; 	//过程的数目
	String err;			//出错信息

	private File varFile;
	private File proFile;
	private File dysFile;
	private File errFile;
	
	varDetail[] varDetails=new varDetail[50];
	procDetail[] procDetails=new procDetail[50];
	
	int errFlag=0;
	void predict(String word,String kind,String err) {
		if (symbol.equals(word)) {
			errFlag=0;
		}else {
			writeErr(err);
			symbol=word;
			this.kind=kind;
			errFlag=1;
		}
	}
	//将*.dyd初始化为单词符号数组和种别数组
	void initial(File file) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, "gbk");
			br = new BufferedReader(isr);
			String readline;
			int i=0;			
			while ((readline = br.readLine()) != null) {
				sym[i]=readline.substring(0,16).trim();
				kinds[i]=readline.substring(17,19);
				i++;
			}
			symbol=sym[0];
			kind=kinds[0];	
			varDetails[0]=new varDetail("","","",0,0,0);
		} catch (FileNotFoundException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		finally {
			try {
				br.close();
				isr.close();
				fis.close();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
	
	//初始化变量名表
	void set_varFile(File file) {
		this.varFile=file;
		FileWriter fw_var=null;
		try {
			fw_var=new FileWriter(varFile,true);
			fw_var.write(String.format("%16s", "vname") + "  ");
			fw_var.write(String.format("%16s", "vproc") + "  ");
			fw_var.write(String.format("%5s", "vkind") + "  ");
			fw_var.write(String.format("%16s", "vtype") + "  ");
			fw_var.write(String.format("%4s", "vlev") + "  ");
			fw_var.write(String.format("%4s", "vadr") + "\n");
			fw_var.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//初始化过程名表
	void set_proFile(File file) {
		this.proFile=file;
		FileWriter fw_pro=null;
		try {
			fw_pro=new FileWriter(proFile,true);
			fw_pro.write(String.format("%16s", "pname") + "  ");
			fw_pro.write(String.format("%16s", "ptype") + "  ");
			fw_pro.write(String.format("%4s", "plev") + "  ");
			fw_pro.write(String.format("%4s", "fadr") + "  ");
			fw_pro.write(String.format("%4s", "ladr") + "\n");
			fw_pro.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//实例化dys & err文件
	void get_dysFile(File file) {
		this.dysFile=file;
	}
	void get_errFile(File file) {
		this.errFile=file;
	}	
	//写.dys文件
	void writeDys(String word,String kind) {
		FileWriter fw_dys=null;
		try {
			fw_dys=new FileWriter(dysFile,true);
			fw_dys.write(String.format("%16s", word) + " ");
			fw_dys.write(kind+"\n");
			fw_dys.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//报错文件
	int err_number=0;
	void writeErr(String errString) {
		FileWriter fw_err=null;
		try {
			fw_err=new FileWriter(errFile,true);
			fw_err.write("***LINE "+line+": "+errString+"\n");
			fw_err.close();
			err_number++;
//			err_number>=5||
			if (errString.indexOf("Fatal")!=-1) {
				System.exit(0);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//指向下一个单词符号
	void advance() {
		last_word=symbol;
			index++;
			symbol=sym[index];
			kind=kinds[index];	
		if (symbol.equals("EOLN")) {
			writeDys(symbol, kind);
			index++;
			symbol=sym[index];
			kind=kinds[index];
			line++;
		}else if (symbol.equals("EOF")) {
			writeDys(symbol, kind);
			symbol="";
			kind="";
		}
	}
	
	//程序->
	void process() {
		line++;
		procDetails[0]=new procDetail();
		procDetails[0].pname="main";
		procDetails[0].plev=level; //level=0
		procDetails[0].ptype="void";
		proc_number++;
		subProcess();
		int fadr=100;
		int ladr=0;
		int tempadr=0;
		for (int n = 0; n < address; n++) {					
			if (varDetails[n].vlev==0) {
				tempadr=varDetails[n].vadr;
				if (tempadr<fadr) {
					procDetails[0].fadr=tempadr;
					fadr=tempadr;
				}
			}
		}
		if (tempadr>ladr) {
			procDetails[0].ladr=tempadr;
			ladr=tempadr;
		}
	}
	//分程序->
	void subProcess() {
		if (symbol.equals("begin")) {
			writeDys(symbol, kind);
			advance();
			declareTable();
			if (last_word.equals(";")) {
				procTable();
				if (symbol.equals("end")) {	
					writeDys(symbol, kind);
					advance();
				}else{
					err="Fatal Error: missing: \"end\"";
					writeErr(err);
				}
			}else{
				err="Fatal Error: missing: \";\"";
				writeErr(err);
			}		
		}else{
			err="Fatal Error: missing \"begin\"";
			writeErr(err);
		}
	}
	//说明语句表-><说明语句><说明语句表*>
	void declareTable() {
		predict("integer", "03", "missing \"integer\"");
		declare();
		declareTable_Ex();
	}
	//说明语句表*-> ；<说明语句><说明语句表*>
	void declareTable_Ex(){
		if (symbol.equals(";")) {
			writeDys(symbol, kind);
			advance();
			declare();
			declareTable_Ex();
		}
	}
	//说明语句->
	int isDeclare=0;	//说明标识符是否正在说明
	void declare() {
		isDeclare=1;
		if (symbol.equals("integer")) {
			if (errFlag==0) {
				writeDys(symbol, kind);
			}else {
				index--;
			}
			errFlag=0;
			type=symbol.toUpperCase();
			advance();
			if (symbol.equals("function")) {
				level++;
				procFlag=1;
				writeDys(symbol, kind);
				advance();
				identifier();
				predict("(","21", err="missing: \"(\"");
				if (symbol.equals("(")) {
					if (errFlag==0) {
						writeDys(symbol, kind);
					}else {
						index--;
					}
					errFlag=0;
					advance();
					vkind=1;
					parameter();
					predict(")","22", err="missing: \")\"");
					if (symbol.equals(")")) {
						if (errFlag==0) {
							writeDys(symbol, kind);
						}else {
							index--;
						}
						errFlag=0;
						advance();
						predict(";","23", err="missing: \";\"");
						if (symbol.equals(";")) {
							if (errFlag==0) {
								writeDys(symbol, kind);
							}else {
								index--;								
							}						
							errFlag=0;
							advance();
							functionBody();
						}
					}
				}
			}else {
				variable();
			}
		}
		isDeclare=0;
	}
	//变量->
	void variable() {
		identifier();
	}
	//参数->
	void parameter() {
		variable();
	}
	//标识符->
	void identifier() {
		if (kind.equals("10")) {
			if (isDeclare!=1) {
				int checkFlag=0;
				for (int n = 0; n < proc_number; n++) {
					if (procDetails[n].pname.equals(symbol)) {
						writeDys(symbol, kind);
						advance();
						checkFlag=1;
					}
				}
				for (int n = 0; n < address; n++) {
					if (varDetails[n].vname.equals(symbol)) {
						writeDys(symbol, kind);
						advance();
						checkFlag=1;
					}
				}
				if (checkFlag==0) {
					err=symbol+ " is not defined";
					writeErr(err);
					advance();
				}
			}
			if (isDeclare==1) {
				if (procFlag==1) {
					int checkFlag=0;
					for (int n = 0; n < proc_number; n++) {
						if (procDetails[n].pname.equals(symbol)) {
							checkFlag=1;
						}
					}
					if (checkFlag==0) {
						procDetails[proc_number]=new procDetail(symbol,type,level,0,0);
						proc_number++;
						writeDys(symbol, kind);
						advance();
					}else {
						err="duplicate local variable "+symbol;
						writeErr(err);
						advance();
					}
					procFlag=0;
				}else {
					int checkFlag=0;
					for (int n = 0; n < proc_number; n++) {
						if (procDetails[n].pname.equals(symbol)) {
							checkFlag=1;
						}
					}
					for (int n = 0; n < address; n++) {
						if (varDetails[n].vname.equals(symbol)&&varDetails[n].vlev==level&&varDetails[n].vkind==vkind) {
							checkFlag=1;
						}
					}
					if (checkFlag==0) {
						varDetails[address]=new varDetail(symbol,"",type,vkind,level,address);
						for (int n = 0; n < proc_number; n++) {
							if (procDetails[n].plev==level) {
								varDetails[address].vproc=procDetails[n].pname;
							}
						}
						address++;
						vkind=0;
						writeDys(symbol, kind);
						advance();
					}else {
						err="duplicate local variable "+symbol;
						writeErr(err);
						advance();
					}
				}
			}
		}
	}
	//函数体->
	void functionBody() {
		if (symbol.equals("begin")) {
			writeDys(symbol, kind);
			advance();
			declareTable();
			if (last_word.equals(";")) {
				procTable();
				if (symbol.equals("end")) {
					writeDys(symbol, kind);
					int fadr=100;
					int ladr=0;
					int tempadr=0;
					for (int n = 0; n < address; n++) {	
						if (varDetails[n].vlev==level) {
							tempadr=varDetails[n].vadr;
//							System.out.println(tempadr);
							if (tempadr<fadr) {
//								System.out.println(level);
//								System.out.println(procDetails[level].pname);
								procDetails[level].fadr=tempadr;
								fadr=tempadr;
							}
						}
					}
					if (tempadr>ladr) {
						procDetails[level].ladr=tempadr;
						ladr=tempadr;
					}
//					fadr=100;
//					ladr=0;
//					tempadr=0;
					advance();
					level--;
				}else{
					err="missing: \"end\"";
					writeErr(err);
				}
			}	
		}else{
			err="Fatal Error: missing \"begin\"";
			writeErr(err);
		}
	}
	//执行语句表
	void procTable() {
		proc();
		procTable_Ex();
	}
	void procTable_Ex() {
		if (symbol.equals(";")) {

			writeDys(symbol, kind);

			advance();
			proc();
			procTable_Ex();
		}
	}
	//执行语句
	void proc() {
		if (symbol.equals("read")) {
			writeDys(symbol, kind);
			advance();
			predict("(","21", err="missing: \"(\"");
			if (symbol.equals("(")) {
				if (errFlag==0) {
					writeDys(symbol, kind);
				}else {
					index--;
				}
				errFlag=0;
				advance();
				variable();
				predict(")","22", err="missing: \")\"");
				if (symbol.equals(")")) {
					if (errFlag==0) {
						writeDys(symbol, kind);
					}else {
						index--;
					}
					errFlag=0;
					advance();
				}
			}
		}else if (symbol.equals("write")) {
			writeDys(symbol, kind);
			advance();
			predict("(","21", err="missing: \"(\"");
			if (symbol.equals("(")) {
				if (errFlag==0) {
					writeDys(symbol, kind);
				}else {
					index--;
				}
				errFlag=0;
				advance();
				vkind=1;
				variable();
				predict(")","22", err="missing: \")\"");
				if (symbol.equals(")")) {
					if (errFlag==0) {
						writeDys(symbol, kind);
					}else {
						index--;
					}
					errFlag=0;
					advance();
				}
			}
		}else if (symbol.equals("if")) {
			writeDys(symbol, kind);
			advance();
			condition();
			predict("then","05", err="missing: \"then\"");
			if (symbol.equals("then")) {
				if (errFlag==0) {
					writeDys(symbol, kind);
				}else {
					index--;
				}
				errFlag=0;
				advance();
				proc();
				predict("else","06", err="missing: \"else\"");
				if (symbol.equals("else")) {
					if (errFlag==0) {
						writeDys(symbol, kind);
					}else {
						index--;
					}
					errFlag=0;
					advance();
					proc();
				}
			}
		}else {
			variable();
			if (symbol.equals(":=")) {
				writeDys(symbol, kind);
				advance();
				arithmetic();
			}
		}
	}
	//算术表达式
	void arithmetic() {
		item();
		arithmetic_Ex();
	}
	void arithmetic_Ex() {
		if (symbol.equals("-")) {
			writeDys(symbol, kind);
			advance();
			item();
			arithmetic_Ex();
		}
	}
	//项
	void item() {
		factor();
		item_Ex();
	}
	void item_Ex() {
		if (symbol.equals("*")) {
			writeDys(symbol, kind);
			advance();
			factor();
			item_Ex();
		}
	}
	//因子
	void factor() {
		if (kind.equals("10")) {
			variable();
			if (symbol.equals("(")) {
				writeDys(symbol, kind);
				advance();
				arithmetic();
				if (symbol.equals(")")) {
					writeDys(symbol, kind);
					advance();
				}
			}
		}else if (kind.equals("11")) {
			writeDys(symbol, kind);
			advance();
		}
	}
	//条件表达式
	void condition() {
		arithmetic();
		relation_op();
		arithmetic();
	}
	//关系运算符
	void relation_op() {
		int num=Integer.parseInt(kind);
		if (num>=12&&num<=17) {
			writeDys(symbol, kind);
			advance();
		}else{
			err="Invalid Symbol.";
			writeErr(err);
		}
	}
	//写变量名表
	void writeVar() {
		FileWriter fw_var=null;
		try {
			fw_var=new FileWriter(varFile,true);
			for (int n = 0; n < address; n++) {
				fw_var.write(String.format("%16s", varDetails[n].vname) + "  ");
				fw_var.write(String.format("%16s", varDetails[n].vproc) + "  ");
				fw_var.write(String.format("%5s", varDetails[n].vkind) + "  ");
				fw_var.write(String.format("%16s", varDetails[n].vtype) + "  ");
				fw_var.write(String.format("%4s", varDetails[n].vlev) + "  ");
				fw_var.write(String.format("%4s", varDetails[n].vadr) + "\n");
			}		
			fw_var.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//写过程名表
	void writeProc() {
		FileWriter fw_pro=null;
		try {
			fw_pro=new FileWriter(proFile,true);
			for (int n = 0; n < proc_number; n++) {
				fw_pro.write(String.format("%16s", procDetails[n].pname) + "  ");
				fw_pro.write(String.format("%16s", procDetails[n].ptype) + "  ");
				fw_pro.write(String.format("%4s", procDetails[n].plev) + "  ");
				fw_pro.write(String.format("%4s", procDetails[n].fadr) + "  ");
				fw_pro.write(String.format("%4s", procDetails[n].ladr) + "\n");
			}
			fw_pro.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}