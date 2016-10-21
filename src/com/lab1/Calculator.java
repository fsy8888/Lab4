/* Author: Kevin Wang
 * Created Dated: 2016-9-13
 */

package com.lab1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.*;

/**
 * @author Kevin Wang
 *
 */
public class Calculator {
/**
*
*/
	private static Pattern p = Pattern.compile("\\+|\\-|\\*|\\^|[a-zA-Z]+|\\d+|\\(|\\)");
	/**
	 *
	 */
	private static Pattern pnum = Pattern.compile("\\d+");

//	private static Pattern pmul = Pattern.compile("\\*");
	/**
	 *
	 */
	private static Pattern palph = Pattern.compile("[a-zA-Z]+");

	/**
	 *
	 */
	private static Pattern pop = Pattern.compile("\\+|\\-|\\*|\\^");

	/**
	 *
	 */
	private static Pattern pil = Pattern.compile("[^\\+\\-\\*\\^a-zA-Z0-9\\(\\)\\s]");

	/**
	 * @param str
	 * @param indexNeg
	 * @return List<String> split
	 * 按+/-分解表达式， 在index_neg中记录符号的位置， 返回分解表达式
	 */
	public static List<String> split(String str, List<Boolean> indexNeg) {
		StringBuffer s = new StringBuffer(str);
		List<String> re = new LinkedList<String>();
		int plus = s.indexOf("+");
		int minus = s.indexOf("-");
		while (plus != -1 || minus != -1) {
			if (plus != -1 && minus != -1) {
				if (plus < minus) {
					re.add(new String(s.substring(0, plus)));
					s.delete(0, plus + 1);
					indexNeg.add(false);
				} else {
					re.add(new String(s.substring(0, minus)));
					s.delete(0, minus + 1);
					indexNeg.add(true);
				}
			} else if (plus != -1) {
				re.add(new String(s.substring(0, plus)));
				s.delete(0, plus + 1);
				indexNeg.add(false);
			} else {
				re.add(new String(s.substring(0, minus)));
				s.delete(0, minus + 1);
				indexNeg.add(true);
			}
			plus = s.indexOf("+");
			minus = s.indexOf("-");
		}
		re.add(new String(s));
		return re;
	}

	/**
	 * @param exp
	 * @return
	 * 将表达式重构，包括将数字放置左侧，合并同类项，返回重构结果.
	 */
	public static String refactor(String exp) {
		String re = "";
		List<Boolean> indexNeg = new ArrayList<Boolean>();
		List<String> exps = split(exp, indexNeg);
		int index = 0;
		for (String e : exps) {
			String[] items = e.split("\\*");
			String num = "";
			Map<String, Integer> sym = new HashMap<String, Integer>();
			for (String item : items) {
				if (pnum.matcher(item).matches()) {
					if (num.isEmpty()) {
						num = item;
					} else {
						num += "*" + item;
					}
				} else if (!palph.matcher(item).matches()) {
					String[] tmp = item.split("\\^");
					if (sym.containsKey(tmp[0])) {
						sym.put(tmp[0], sym.get(tmp[0]) + Integer.parseInt(tmp[1]));
					} else {
						sym.put(tmp[0], Integer.parseInt(tmp[1]));
					}
				} else {
					if (sym.containsKey(item)) {
						sym.put(item, sym.get(item) + 1);
					} else {
						sym.put(item, 1);
					}
				}
			}
			String i = num;
			if (!sym.isEmpty()) {
				for (String k : sym.keySet()) {
					if (!i.isEmpty()) {
						if (sym.get(k) != 1) {
							i += "*" + k + "^" + sym.get(k);
						} else {
							i += "*" + k;
						}
					} else {
						if (sym.get(k) != 1) {
							i = k + "^" + sym.get(k);
						} else {
							i = k;
						}
					}
				}
			}
			if (re.isEmpty()) {
				re = i;
			} else if (indexNeg.get(index)) {
				re += "-" + i;
				index += 1;
			} else {
				re += "+" + i;
				index += 1;
			}
		}
		return re;
	}

	/**
	 * @param str
	 * @return
	 * 表达式合法性检查，并补充省略的乘法符号 返回补充结果
	 */
	public static String generateItems(String str) {
		Matcher m = p.matcher(str);
		Matcher mil = pil.matcher(str);
		List<String> exp = new LinkedList<String>();

		if (mil.find()) {
			System.out.println("illegal charaters");
			System.exit(1);
		}
		while (m.find()) {
			exp.add(m.group());
		}
		for (int i = 0; i < exp.size() - 1; i += 1) {
			if ((pnum.matcher(exp.get(i)).matches()
					&& (palph.matcher(exp.get(i + 1)).matches() || exp.get(i + 1).equals("(")))
					|| (palph.matcher(exp.get(i)).matches()
							&& (pnum.matcher(exp.get(i + 1)).matches() || exp.get(i + 1).equals("(")))) {
				exp.add(i + 1, new String("*"));
			}
			if ((exp.get(i).equals("^") && !pnum.matcher(exp.get(i + 1)).matches())
					|| (pop.matcher(exp.get(i)).matches()
							&& (pop.matcher(exp.get(i + 1)).matches() || exp.get(i + 1).equals(")")))
					|| (exp.get(i).equals("(") && pop.matcher(exp.get(i)).matches()) || exp.get(0) == "*"
					|| exp.get(0) == "^") {
				System.out.println("invalid expression");
				System.exit(1);
			}
		}
		StringBuffer s = new StringBuffer();
		for (String e : exp) {
			s.append(e);
		}
		return s.toString();
	}

	/**
	 * @param str
	 * @return
	 * 返回表达式中全部变量
	 */
	public static List<String> allSym(String str) {
		Matcher m = palph.matcher(str);
		List<String> sym = new ArrayList<String>();
		while (m.find()) {
			sym.add(m.group());
		}
		return sym;
	}

	/**
	 * @param str
	 * @return
	 * 拆分表达式并返回结果
	 */
	public static List<String> toList(String str) {
		Matcher m = p.matcher(str);
		List<String> exp = new LinkedList<String>();
		while (m.find()) {
			exp.add(m.group());
		}
		return exp;
	}

	/**
	 * @param args
	 * 主函数负责对输入操作的判断与执行
	 */
	public static void main(String[] args) {
		String str = "3+2*x+x*y*3*x*z";
		System.out.println("Enter 'q' to quit\n");
		Scanner in = new Scanner(System.in);
		Node tree = null;
		Map<String, String> symbolTable = new HashMap<String, String>();
		List<String> allSym = new ArrayList<String>();
		while (true) {
			str = in.nextLine();
			if (str.equals("q")) {
				break;
			} else if (str.startsWith("!simplify")) {
				String[] str2 = str.split(" ");
				for (int i = 1; i < str2.length; i += 1) {
					String[] sim = str2[i].split("=");
					symbolTable.put(sim[0], sim[1]);
				}
				String r1 = tree.calculate();
				// System.out.println(r1);
				Node simTree = new Node(toList(refactor(generateItems(r1))));
				simTree.expression();
				String r2 = simTree.calculate();
				System.out.println(r2);
				symbolTable.clear();
			} else if (str.startsWith("!d/d")) {
				String dsym = str.substring(4, str.length());
				if (!allSym.contains(dsym)) {
					System.out.println("Error, no variable");
				} else {
					Node.setDsym(dsym);
					String r1 = tree.derivative();
					Node simTree = new Node(toList(refactor(generateItems(r1))));
					simTree.expression();
					String r2 = simTree.calculate();
					System.out.println(r2);
				}
			} else {
				allSym = allSym(str);
				tree = new Node(toList(refactor(generateItems(str))));
				Node.setTable(symbolTable);
				tree.expression();
			}
		}
	}
}
