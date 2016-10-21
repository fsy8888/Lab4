/**
 * @author Kevin Wang
 * Created Dated: 2016-9-13
 */

package com.lab1;

import java.util.List;
import java.util.Map;
import static java.lang.Math.pow;

/**
 * @
 * 建立表达式树，实现化简、求导方法
 */
public class Node {
/**
* 节点内的表达式.
*/
	private List<String> subExpression;

	/**
	 * left，左侧表达式.
	 * right，右侧表达式.
	 */
	private Node left = null, right = null;

	/**
	 * 节点的运算符 u为叶节点.
	 */
	private char sym = 'u';

	/**
	 * 节点是否由纯数字组成.
	 */
	private boolean isDigital = false;

	/**
	 * 变量的代入值.
	 */
	private static Map<String, String> symbolTable;

	/**
	 * 操作符.
	 */
	private static String[] op = {"+", "-", "*"};

	/**
	 * 待求导变量.
	 */
	private static String dsym = "";

	/**
	 * @param s
	 * 构造节点
	 */
	public Node(List<String> s) {
		this.subExpression = s;
	}

	/**
	 * @param table
	 * 设置变量映射表
	 */
	public static void setTable(Map<String, String> table) {
		symbolTable = table;
	}

	/**
	 * @param sym
	 * 设置待求导变量
	 */
	public static void setDsym(String sym) {
		dsym = sym;
	}

	/**
	 * @param exp
	 * @return re.toString()
	 * 将list合并成string， 返回合并结果
	 */
	public static String listToString(List<String> exp) {
		StringBuffer re = new StringBuffer();
		for (String i : exp) {
			re.append(i);
		}
		return re.toString();
	}

	/**
	 * 构造语法树.
	 */
	public void expression() {
		for (String i : op) {
			int p = subExpression.lastIndexOf(i);
			if (p != -1) {
				sym = i.charAt(0);
				left = new Node(subExpression.subList(0, p));
				right = new Node(subExpression.subList(p + 1, subExpression.size()));
				left.expression();
				right.expression();
				return;
			}
		}
		int p = subExpression.lastIndexOf("^");
		if (p != -1) {
			sym = '^';
			left = new Node(subExpression.subList(0, 1));
			right = new Node(subExpression.subList(2, 3));
			return;
		}
	}

	/**
	 * 计算表达式的值 返回结果.
	 * @return s.get(0) or null
	 */
	public String calculate() {
		if (sym == 'u') {
			if (symbolTable.containsKey(subExpression.get(0))) {
				this.isDigital = true;
				return symbolTable.get(subExpression.get(0));
			} else {
				if (subExpression.get(0).matches("\\d+")) {
					this.isDigital = true;
				}
				return subExpression.get(0);
			}
		}
		String reLeft = left.calculate();
		String reRight = right.calculate();
		if (sym == '^') {
			if (left.isDigital && right.isDigital) {
				this.isDigital = true;
				return String.valueOf(((int) pow(Integer.parseInt(reLeft), Integer.parseInt(reRight))));
			} else {
				return reLeft + "^" + reRight;
			}
		} else if (sym == '*') {
			if (left.isDigital && right.isDigital) {
				this.isDigital = true;
				return String.valueOf(Integer.parseInt(reLeft) * Integer.parseInt(reRight));
			} else {
				return new String(reLeft + "*" + reRight);
			}
		} else if (sym == '+') {
			if (left.isDigital && right.isDigital) {
				this.isDigital = true;
				return String.valueOf(Integer.parseInt(reLeft) + Integer.parseInt(reRight));
			} else {
				return reLeft + "+" + reRight;
			}
		} else if (sym == '-') {
			if (left.isDigital && right.isDigital) {
				this.isDigital = true;
				return String.valueOf(Integer.parseInt(reLeft) - Integer.parseInt(reRight));
			} else {
				return reLeft + "-" + reRight;
			}
		}
		return null;
	}

	/**
	 * @return re or "0"
	 * 求导表达式 返回求导结果
	 */
	public String derivative() {
		String re = derivative2();
		if (re.isEmpty()) {
			return "0";
		} else {
			return re;
		}
	}

	/**
	 * @return
	 * 求导表达式 返回求导结果
	 */
	private String derivative2() {
		if (sym == 'u') {
			if (subExpression.get(0).equals(dsym)) {
				return "1";
			}
			return "";
		}
		if (sym == '^') {
			if (subExpression.get(0).equals(dsym)) {
				int index = Integer.parseInt(right.subExpression.get(0));
				if (index > 2) {
					return Integer.toString(index) + "*" + left.subExpression.get(0) + "^" + Integer.toString(index - 1);
				} else if (index == 2) {
					return "2*" + left.subExpression.get(0);
				} else {
					return "1";
				}
			} else {
				return "";
			}
		}
		String reLeft = left.derivative2();
		String reRight = right.derivative2();
		if (sym == '*') {
			String s1, s2;
			s1 = reLeft + "*" + listToString(right.subExpression);
			s2 = listToString(left.subExpression) + "*" + reRight;
			if (reLeft.isEmpty()) {
				s1 = "";
			} else if (reLeft.equals("1")) {
				s1 = listToString(right.subExpression);
			}
			if (reRight.isEmpty()) {
				s2 = "";
			} else if (reRight.equals("1")) {
				s2 = listToString(left.subExpression);
			}
			if (!s1.isEmpty() && !s2.isEmpty()) {
				return s1 + "+" + s2;
			} else {
				return s1 + s2;
			}
		} else if (sym == '+') {
			String s1, s2;
			s1 = reLeft;
			s2 = reRight;
			if (!s1.isEmpty() && !s2.isEmpty()) {
				return s1 + "+" + s2;
			} else {
				return s1 + s2;
			}
		} else if (sym == '-') {
			String s1, s2;
			s1 = reLeft;
			s2 = reRight;
			if (!s1.isEmpty() && !s2.isEmpty()) {
				return s1 + "-" + s2;
			} else {
				return s1 + s2;
			}
		}
		return null;
	}
}
