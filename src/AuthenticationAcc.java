import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

/**
 * 
 */

/**
 * @author 51101_000
 * 
 */
public class AuthenticationAcc {

	/**
	 * 
	 */
	ArrayList<Point> CanPoint = new ArrayList();
	ArrayList<Point> CombiSet = new ArrayList();
	ArrayList<Point> FuzzyPoint = new ArrayList();
	BigDecimal[] CoefPoly = new BigDecimal[9];
	BigInteger[] CoefficientsSet = new BigInteger[9];
	static boolean flag = false;
	static boolean sign = false;
	int[] Com = new int[50];
	String SC = "";

	public static java.sql.Connection GetConnection()
			throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/tttn",
				"root", "");
	}

	public AuthenticationAcc() throws IOException {
		// TODO Auto-generated constructor stub
		for (int i = 0; i < 9; i++) {
			CombiSet.add(new Point(BigInteger.valueOf(0), BigInteger.valueOf(0)));
		}

		getFuzzyVault();
		CandPoint();
		CombiSet(1, 9);
	}

	public ArrayList<Point> getFuzzyVault() {

		Scanner u_name = new Scanner(System.in);
		String username = u_name.next();

		try {
			java.sql.Connection conn = GetConnection();
			java.sql.Statement stmt = conn.createStatement();
			ResultSet setVault = stmt
					.executeQuery("Select * from fuzzyvault where username='"
							+ username + "'");
			if (setVault == null)
				System.out.println("Username doesn't exist");
			else {
				// FuzzyPoint.add(new Point(new
				// BigInteger(setVault.f.getObject(1).toString()),new
				// BigInteger(setVault.getObject(2).toString())));
				if (setVault.next()) {
					FuzzyPoint.add(new Point(new BigInteger(setVault
							.getObject(1).toString()), new BigInteger(setVault
							.getObject(2).toString())));
				}
				while (setVault.next()) {
					int col = setVault.getRow();
					BigInteger x = new BigInteger(setVault.getObject(1)
							.toString());
					BigInteger y = new BigInteger(setVault.getObject(2)
							.toString());
					System.out.println(setVault.getObject(1).toString() + " "
							+ setVault.getObject(2));
					FuzzyPoint.add(new Point(x, y));
				}
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return FuzzyPoint;
	}

	public ArrayList<Point> queryMinutiae() {
		ArrayList<Point> QueryMinu = new ArrayList();
		try {
			BufferedReader bfread = new BufferedReader(new FileReader(
					"point.txt"));
			String str = "";
			while ((str = bfread.readLine()) != null) {
				// System.out.println(str);
				BigInteger x = new BigInteger(str.split(" ")[0]);
				BigInteger y = new BigInteger(str.split(" ")[1]);
				QueryMinu.add(new Point(x, y));

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return QueryMinu;
	}

	public ArrayList<Point> CandPoint() {
		ArrayList<Point> queryMinu = queryMinutiae();

		for (int i = 0; i < queryMinu.size(); i++) {
			int count = 0;
			float x = 100;
			// System.out.println("ad"+queryMinu.get(i).x+" "+queryMinu.get(i).y);
			for (int j = 0; j < FuzzyPoint.size(); j++)

			{
				int xFuzzy = Integer.parseInt(((FuzzyPoint.get(j).x)
						.shiftRight(8)).toString());
				int yFuzzy = Integer.parseInt((FuzzyPoint.get(j).x).and(
						BigInteger.valueOf(255)).toString());

				int xQuery = Integer.parseInt((queryMinu.get(i).x).toString());
				int yQuery = Integer.parseInt((queryMinu.get(i).y).toString());
				int xx = (int) (Math.pow((xQuery - xFuzzy), 2) + Math.pow(
						yQuery - yFuzzy, 2));
				float xLast = (float) Math.sqrt(xx);
//				System.out.println(i + " " + FuzzyPoint.get(j).x + " i  xLast "
//						+ xLast);
				if (xLast < x) {
					x = xLast;
					count = j;

				}

			}
			System.out
					.println("s"
							+ Integer.parseInt(((FuzzyPoint.get(count).x)
									.shiftRight(8)).toString())
							+ "    "
							+ Integer.parseInt((FuzzyPoint.get(count).x).and(
									BigInteger.valueOf(255)).toString()));
			CanPoint.add(FuzzyPoint.get(count));

			// System.out.println(FuzzyPoint.get(count).x);
			FuzzyPoint.remove(count);
		}
		return CanPoint;
	}

	public void CombiSet(int i, int k) throws IOException {
		// System.out.println("CanPoint"+CanPoint.size());
		Com[0] = 0;
		BufferedWriter bfw = new BufferedWriter(new FileWriter(("sc.txt")));
		for (int j = Com[i - 1] + 1; j <= CanPoint.size() - k + i; j++) {
			if (flag == true)
				break;
			Com[i] = j;
			if (i == k) {
				Lagrange(k);
				try {
					// bfw.newLine();

					bfw.write(SC + "\n");

					// bfw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				CombiSet(i + 1, k);
		}

	}

	private String Lagrange(int k) {
		// System.out.println("LAM");
		BigDecimal[] coefDeci = new BigDecimal[9];

		int count = 0;
		while (count < 9) {
			CoefficientsSet[count] = BigInteger.valueOf(0);
			coefDeci[count] = BigDecimal.valueOf(0);
			count++;
		}
		for (int i = 1; i <= k; i++) {

			CombiSet.get(i - 1).x = (CanPoint.get(Com[i] - 1).x);
			CombiSet.get(i - 1).y = CanPoint.get(Com[i] - 1).y;

		}
		for (int kj = 0; kj < CombiSet.size(); kj++) {
			
			BigInteger XX = CombiSet.get(kj).x;

			BigInteger YY = CombiSet.get(kj).y;
			System.out.println( " xx " + XX);
			BigDecimal[] c = new BigDecimal[1];
			c[0] = BigDecimal.valueOf(1);
			BigInteger xMau = BigInteger.valueOf(1);

			for (int jk = 0; jk < CombiSet.size(); jk++) {
				if (kj != jk) {
					BigInteger[] a = { BigInteger.valueOf(1),
							BigInteger.valueOf(0).subtract(CombiSet.get(jk).x) };
					c = multiPolynomial(c, a);
					xMau = xMau.multiply(XX.subtract(CombiSet.get(jk).x));
				}
			}
			BigDecimal yFinal = new BigDecimal(YY);
			BigDecimal xFinal = new BigDecimal(xMau);
			BigDecimal ceofFinal = yFinal.divide(xFinal, 15,
					RoundingMode.HALF_UP);
			c = multiPolyNum(c, ceofFinal);
			coefDeci = addPoly(coefDeci, c);
		}
		SC = "";
		for (int la = 0; la < coefDeci.length; la++) {
			System.out.println("coef" + coefDeci[la]);
			coefDeci[la] = coefDeci[la].setScale(0, RoundingMode.HALF_UP);
			CoefficientsSet[la] = coefDeci[la].toBigInteger();
			if (coefDeci[la].toBigInteger().compareTo(BigInteger.valueOf(0)) == -1
					|| (coefDeci[la].toBigInteger().compareTo(
							BigInteger.valueOf(65535)) == 1)) {
				System.out.println("oooo");
				SC = "1";
				return SC;
			}
			// if (coefDeci[la].compareTo(BigDecimal.valueOf(0)) == -1)
			// sign = true;
			System.out.println("coef" + coefDeci[la]);

			String Coef = coefDeci[la].toBigInteger().toString(2);

			while (Coef.length() < 16) {
				Coef = "0" + Coef;

				// for (int i = 0; i < SC.substring(0, SC.length()-16).length();
				// i++) {

				// System.out.println(j);
			}
			SC = Coef + SC;
		}
		System.out.println(SC);

		checkCRC(SC, "10001000000100001");
		// checkCRC("101110","1001");

		return SC;

	}

	public BigDecimal[] addPoly(BigDecimal[] a, BigDecimal[] bigIntegers) {
		for (int i = 0; i < a.length; i++) {
			// BigInteger aA = BigInteger.);
			a[i] = a[i].add(bigIntegers[i]);
		}
		return a;
	}

	public BigDecimal[] multiPolyNum(BigDecimal[] c2, BigDecimal bigInteger) {
		// BigDecimal c[] = new BigDecimal[c2.length];

		for (int i = 0; i < c2.length; i++) {
			c2[i] = c2[i].multiply(bigInteger);
			// System.out.println("c2	"+c2[i]);
		}
		return c2;
	}

	public BigDecimal[] multiPolynomial(BigDecimal[] c2, BigInteger[] a) {
		BigDecimal c[] = new BigDecimal[c2.length + a.length - 1];
		for (int k = 0; k < c2.length + a.length - 1; k++)
			c[k] = BigDecimal.valueOf(0);
		for (int i = 0; i < c2.length; i++)
			for (int j = 0; j < a.length; j++) {
				BigDecimal aA = new BigDecimal(a[j]);
				c[i + j] = c[i + j].add(c2[i].multiply(aA));
			}
		return c;
	}

	public static boolean checkCRC(String a, String b) {
		int count = 0;
		String[] chr = a.split("", 0);
		String[] chr2 = b.split("", 0);
		{
			while (count <= (a.length() - b.length())) {

				if (chr[0] == "0") {
					int ab = Integer.parseInt(chr[count]);
					while (ab == 0) {
						count++;

						if (count == (a.length() - 1))
							break;
						ab = Integer.parseInt(chr[count]);
					}

					for (int i = count; i < b.length() + count - 1; i++) {
						int x = 0;

						x = Integer.parseInt(chr[i])
								^ Integer.parseInt(chr2[i - count]);

						// System.out.println(x);
						chr[i] = Integer.toString(x);
						// j=count;

					}
				} else {
					for (int j = count; j < b.length() + count; j++) {
						int x = 0;

						x = Integer.parseInt(chr[j])
								^ Integer.parseInt(chr2[j - count]);

						// System.out.println(x);
						chr[j] = Integer.toString(x);
						// j=count;

					}
					int ab = Integer.parseInt(chr[count]);
					while (ab == 0) {
						count++;

						if (count == (a.length() - 1))
							break;
						ab = Integer.parseInt(chr[count]);
					}
				}
				// System.out.println(j);

			}
			int sum = 0;
			String ag = "";
			for (int l = 0; l < chr.length; l++) {
				sum = sum + Integer.parseInt(chr[l]);
				// System.out.println(sum + "sum");
				ag = ag + chr[l];
			}
			if (sum == 0) {
				flag = true;
				System.out.println(flag);
			}

		}
		return flag;
	}

}
