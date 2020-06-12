package compilerProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class lexical_analyzer {

	public static void main(String[] args) {
		File file_pas = new File("src.pas");
		File file_err = new File("src.err");
		File file_dyd = new File("src.dyd");
		Reader reader = null;
		FileWriter fw_dyd = null;
		FileWriter fw_err = null;
		StringBuffer buffer = new StringBuffer();
		lexical links = new lexical();
		String s;
		int state = 0;
		int ex_state = 0;
		int line = 1;

		try {
			reader = new InputStreamReader(new FileInputStream(file_pas));
			int tempchar;
			if (file_pas.exists() && file_pas.isFile());
			else {
				try {
					file_pas.createNewFile();
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
			if (file_dyd.exists() && file_dyd.isFile());
			else {
				try {
					file_dyd.createNewFile();
				} catch (IOException e) {
					System.err.println("文件创建失败");
				}
			}
			fw_dyd = new FileWriter(file_dyd);
			fw_dyd.write("");
			fw_err = new FileWriter(file_err);
			fw_err.write("");
			try {
				while ((tempchar = reader.read()) != -1
						|| ((tempchar = reader.read()) == -1 && (ex_state == 1 || ex_state == 2))) {
					if (((char) tempchar) == '\n') {
						line++;
						try {
							fw_dyd = new FileWriter(file_dyd, true);
							fw_dyd.write(String.format("%16s", "EOLN") + " 24\n");
							fw_dyd.close();
							buffer.delete(0, buffer.length());
						} catch (IOException e) {
							e.printStackTrace();
						}

					} else {
						s = String.valueOf((char) tempchar);
						state = links.link(s);

						// 接收到空格
						if (state == 0) {
							ex_state = 0;
							continue;
						}

						// 接收到标识符，如果字符是字母数字则写入
						if (ex_state == 1 || (state == 1 && ex_state == 0)) {
							if (state == 1 || state == 2) {
								buffer.append((char) tempchar);
								ex_state = 1;
								continue;
							} else {
								String word = buffer.toString();
								if (word.length() > 16) {
									try {
										fw_err = new FileWriter(file_err, true);
										String str = "***LINE" + line + ":  Identifier is too long.\n";
										fw_err.write(str);
										fw_err.close();
										buffer.delete(0, buffer.length());
									} catch (IOException e) {
										e.printStackTrace();
									}
								} else {
									try {
										fw_dyd = new FileWriter(file_dyd, true);
										fw_dyd.write(
												String.format("%16s", word) + " " + links.symbol_to_kind(word) + "\n");
										fw_dyd.close();
										buffer.delete(0, buffer.length());
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								ex_state = 0;
							}
						}
						// 接收:=，只有：时报错
						if (ex_state == 7 || state == 7) {
							if (ex_state == 0 && state == 7) {
								buffer.append((char) tempchar);
								ex_state = 7;
								continue;
							} else if (ex_state == 7 && state == 3) {
								buffer.append((char) tempchar);
								String word = buffer.toString();
								try {
									fw_dyd = new FileWriter(file_dyd, true);
									fw_dyd.write(String.format("%16s", word) + " " + links.symbol_to_kind(word) + "\n");
									fw_dyd.close();
									buffer.delete(0, buffer.length());
								} catch (IOException e) {
									e.printStackTrace();
								}
								buffer.delete(0, buffer.length());
								ex_state = 0;
								continue;
							} else {
								buffer.delete(0, buffer.length());
								try {
									fw_err = new FileWriter(file_err, true);
									String str = "***LINE" + line + ":  Missing \"=\" after \":\".\n";
									fw_err.write(str);
									fw_err.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
								ex_state = 0;
							}
						}
						// 接收到数字串
						if (ex_state == 2 || state == 2) {
							if (state == 2) {
								buffer.append((char) tempchar);
								ex_state = 2;
								continue;
							} else {
								String word = buffer.toString();
								try {
									fw_dyd = new FileWriter(file_dyd, true);
									fw_dyd.write(String.format("%16s", word) + " " + links.symbol_to_kind(word) + "\n");
									fw_dyd.close();
									buffer.delete(0, buffer.length());
								} catch (IOException e) {
									e.printStackTrace();
								}
								buffer.delete(0, buffer.length());
								ex_state = 0;
							}
						}

						// 接收到等号
						if (ex_state == 0 && state == 3) {
							buffer.append((char) tempchar);
							String ssString = buffer.toString();
							System.out.print(String.format("%16s", ssString));
							System.out.println(" " + links.symbol_to_kind(ssString));
							buffer.delete(0, buffer.length());
							ex_state = 0;
						}

						// 接收到-，*，（，）,;
						if (ex_state == 0 && state == 4) {
							buffer.append((char) tempchar);
							String word = buffer.toString();
							try {
								fw_dyd = new FileWriter(file_dyd, true);
								fw_dyd.write(String.format("%16s", word) + " " + links.symbol_to_kind(word) + "\n");
								fw_dyd.close();
								buffer.delete(0, buffer.length());
							} catch (IOException e) {
								e.printStackTrace();
							}
							buffer.delete(0, buffer.length());
							ex_state = 0;
						}

						// 接收到<=,<>,<
						if (ex_state == 5 || state == 5) {
							if (ex_state == 0 && state == 5) {
								buffer.append((char) tempchar);
								ex_state = 5;
								continue;

							} else if (ex_state == 5 && (state == 3 || state == 6)) {
								buffer.append((char) tempchar);
								String word = buffer.toString();
								try {
									fw_dyd = new FileWriter(file_dyd, true);
									fw_dyd.write(String.format("%16s", word) + " " + links.symbol_to_kind(word) + "\n");
									fw_dyd.close();
									buffer.delete(0, buffer.length());
								} catch (IOException e) {
									e.printStackTrace();
								}
								buffer.delete(0, buffer.length());
								ex_state = 0;
								continue;
							} else { // 把小于号输出，回退一个字符
								String word = buffer.toString();
								try {
									fw_dyd = new FileWriter(file_dyd, true);
									fw_dyd.write(String.format("%16s", word) + " " + links.symbol_to_kind(word) + "\n");
									fw_dyd.close();
									buffer.delete(0, buffer.length());
								} catch (IOException e) {
									e.printStackTrace();
								}
								buffer.delete(0, buffer.length());
								ex_state = 0;
							}
						}

						// 接受>=,>
						if (ex_state == 6 || state == 6) {
							if (ex_state == 0 && state == 6) {
								buffer.append((char) tempchar);
								ex_state = 6;
								continue;

							} else if (ex_state == 6 && state == 3) {
								buffer.append((char) tempchar);
								String word = buffer.toString();
								try {
									fw_dyd = new FileWriter(file_dyd, true);
									fw_dyd.write(String.format("%16s", word) + " " + links.symbol_to_kind(word) + "\n");
									fw_dyd.close();
									buffer.delete(0, buffer.length());
								} catch (IOException e) {
									e.printStackTrace();
								}
								buffer.delete(0, buffer.length());
								ex_state = 0;
								continue;
							} else {
								String word = buffer.toString();
								try {
									fw_dyd = new FileWriter(file_dyd, true);
									fw_dyd.write(String.format("%16s", word) + " " + links.symbol_to_kind(word) + "\n");
									fw_dyd.close();
									buffer.delete(0, buffer.length());
								} catch (IOException e) {
									e.printStackTrace();
								}
								buffer.delete(0, buffer.length());
								ex_state = 0;
							}
						}
						

						if (state == 8 && tempchar != -1) {
							try {
								fw_err = new FileWriter(file_err, true);
								String str = "***LINE" + line + ":  Invalid Symbol.\n";
								fw_err.write(str);// 将字符串写入到指定的路径下的文件中
								fw_err.close();

							} catch (IOException e) {
								e.printStackTrace();
							}
							ex_state = 0;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				reader.close();
				try {
					fw_dyd = new FileWriter(file_dyd, true);
					fw_dyd.write(String.format("%16s", "EOF") + " 25\n");
					fw_dyd.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
