package compilerProject;

public class lexical {

	int link(String letter) {

		switch (letter) {
		case " ":
		case "\r":
		case "\n":
			return -1;
		case "\t":
			return 0;
		case "a":
		case "b":
		case "c":
		case "d":
		case "e":
		case "f":
		case "g":
		case "h":
		case "i":
		case "j":
		case "k":
		case "l":
		case "m":
		case "n":
		case "o":
		case "p":
		case "q":
		case "r":
		case "s":
		case "t":
		case "u":
		case "v":
		case "w":
		case "x":
		case "y":
		case "z":
			return 1; // 接收到字母
		case "0":
		case "1":
		case "2":
		case "3":
		case "4":
		case "5":
		case "6":
		case "7":
		case "8":
		case "9":
			return 2; // 接收到数字
		case "=":
			return 3;
		case "-":
			return 4;
		case "*":
			return 4;
		case "(":
			return 4;
		case ")":
			return 4;
		case "<":
			return 5;
		case ">":
			return 6;
		case ":":
			return 7;
		case ";":
			return 4;
		default:
			return 8;
		}

	}

	String symbol_to_kind(String string) {
		switch (string) {
		case "begin":
			return "01";
		case "end":
			return "02";
		case "integer":
			return "03";
		case "if":
			return "04";
		case "then":
			return "05";
		case "else":
			return "06";
		case "function":
			return "07";
		case "read":
			return "08";
		case "write":
			return "09";
		case "=":
			return "12";
		case "<>":
			return "13";
		case "<=":
			return "14";
		case "<":
			return "15";
		case ">=":
			return "16";
		case ">":
			return "17";
		case "-":
			return "18";
		case "*":
			return "19";
		case ":=":
			return "20";
		case "(":
			return "21";
		case ")":
			return "22";
		case ";":
			return "23";
		default:
			if (string.matches("[0-9]+")) {
				return "11";
			} else {
				return "10";
			}
		}

	}
}
