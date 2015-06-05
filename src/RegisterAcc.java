import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * 
 */

/**
 * @author 51101_000
 * 
 */
public class RegisterAcc {

	/**
	 * 
	 */
	private static String SC = "";
	private BigInteger[] coefPoly = new BigInteger[9];
	public ArrayList<Point> ChaffPoint = new ArrayList();
	private ArrayList<Point> temPoint = new ArrayList();
	private ArrayList<Point> GeniPoint = new ArrayList();

	public RegisterAcc() throws NoSuchAlgorithmException {
		// TODO Auto-generated constructor stub
		try {

			SC = SC();

			coefPoly = coefPolynomial(SC);
			PointAfProject(temPoint);
			ChaffPoint = ChaffPoint();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String SC() throws NoSuchAlgorithmException {

		KeyGenerator generator = KeyGenerator.getInstance("AES");
		SecretKey key = generator.generateKey();

		byte[] bytecode = key.getEncoded();

		for (byte b : bytecode) {
			String sb = (Integer.toBinaryString((b + 256) % 256));

			while (sb.length() < 8) {
				sb = "0" + sb;

			}
			SC = SC + sb;

		}
		String tempStr=SC;
		while(tempStr.length()<144)
			tempStr=tempStr+"0";
		String sc = checkCRC(tempStr, "10001000000100001");
		System.out.println(sc);
		SC = SC + sc;

		try {
			FileWriter fw = new FileWriter("crc.txt", true);
			fw.append(SC);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("SC" + SC);
		return SC;
	}

	public static String checkCRC(String a, String b) {
		int count = 0;
		String[] chr = a.split("", 0);
		String[] chr2 = b.split("", 0);
		String str = "";
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

			for (int l = 0; l < chr.length; l++) {
				sum = sum + Integer.parseInt(chr[l]);
				System.out.println(sum + "sum");
				str = str + chr[l];
			}
			System.out.println(str);
		}
		String sc = str.substring(144 - 16, 144);
		return sc;
	}

	private BigInteger[] coefPolynomial(String secret) {
		// = new BigInteger[9];
		int count = 0;
		int i = 0;
		while (count <= secret.length() - 16) {
			// coefPoly[i]=BigInteger.valueOf(0);

			String ab = secret.substring(count, count + 16);
			count = count + 16;
			BigInteger c = new BigInteger(ab, 2);
			System.out.println(c);
			coefPoly[i] = c;
			i++;
		}
		return coefPoly;
	}

	private BigInteger ConcateXY(BigInteger x, BigInteger y) {

		String xX = x.toString(2);
		while (xX.length() < 8) {
			xX = "0" + xX;
		}
		String yY = y.toString(2);
		while (yY.length() < 8) {
			yY = "0" + yY;
		}
		String concateXY=xX.concat(yY);
		System.out.println("XX "+xX+"x "+x+"  y  "+y+"  concateXY  " +concateXY+" concateXY  "+new BigInteger(concateXY, 2));
		
		return new BigInteger(concateXY, 2);
	}

	public BigInteger powBig(BigInteger x, int l) { // BigInteger
		// a=BigInteger.valueOf(0);
		if (l == 0)
			return BigInteger.valueOf(1);
		else {
			;
			return powBig(x, l - 1).multiply(x);
		}
	}

	private ArrayList<Point> PointAfProject(ArrayList<Point> AfterProject)
			throws IOException {
		ArrayList<Point> arr = new ArrayList();
		BufferedReader bfr = new BufferedReader(new FileReader("point.txt"));
		// System.out.println("Lam");
		String xyCood = "";

		while ((xyCood = bfr.readLine()) != null) {

			// System.out.println(xyCood);
			BigInteger a = new BigInteger(xyCood.split(" ")[0]);
			BigInteger b = new BigInteger(xyCood.split(" ")[1]);
			Point p = new Point(a, b);

			arr.add(p);
			// System.out.println("Lam");
		}

		for (int i = 0; i < arr.size(); i++) {

			// BigInteger[] coef = coefPolynomial(SC);
			BigInteger XY = ConcateXY(arr.get(i).x, arr.get(i).y);
			BigInteger yY = BigInteger.valueOf(0);
			for (int j = 0; j < coefPoly.length; j++) {
				yY = (yY.add(powBig(XY, coefPoly.length - j - 1).multiply(
						coefPoly[coefPoly.length - j - 1])));
			}
			// System.out.println(XY+" "+yY);
			Point point = new Point(XY, yY);
			AfterProject.add(point);
		}

		return AfterProject;
	}

	public BigInteger findMaxX(ArrayList<Point> array) {
		BigInteger in = BigInteger.valueOf(0);
		for (int i = 0; i < array.size(); i++) {
			if ((array.get(i).x).compareTo(in) > 0)
				in = array.get(i).x;
		}
		return in;
	}

	public BigInteger findMinX(ArrayList<Point> arrayX) {
		BigInteger in = arrayX.get(0).x;
		for (int i = 0; i < arrayX.size(); i++) {
			if (in.compareTo(arrayX.get(i).x) > 0)
				in = arrayX.get(i).x;
		}
		return in;
	}

	public BigInteger findMaxY(ArrayList<Point> arrayY) {
		BigInteger in = BigInteger.valueOf(0);
		for (int i = 0; i < arrayY.size(); i++) {
			if (arrayY.get(i).y.compareTo(in) > 0) {
				in = arrayY.get(i).y;
			}
		}
		return in;
	}

	public BigInteger findMinY(ArrayList<Point> arrayY) {
		BigInteger in = arrayY.get(0).y;
		for (int i = 0; i < arrayY.size(); i++) {
			if (in.compareTo(arrayY.get(i).y) > 0)
				in = arrayY.get(i).y;
		}
		return in;
	}

	public boolean compare(BigInteger x, BigInteger y, ArrayList<Point> arrList) {
		BigInteger yY = BigInteger.valueOf(0);
		for (int j = 0; j < coefPoly.length; j++) {
			yY = yY.add(powBig(x, coefPoly.length - j - 1));
		}
		for (int i = 0; i < arrList.size(); i++) {
			if (x.equals(arrList.get(i).x) == true) {
				//System.out.println("LamHama   " + x);
				return false;
			}
		}
		if (yY.equals(y) == true) {
			return false;
		}
		return true;
	}

	private ArrayList<Point> ChaffPoint() throws IOException {
		ArrayList<Point> chaff = new ArrayList();
		// ArrayList<Point> pointPro=PointAfProject();
		
		for (int i = 0; i < temPoint.size(); i++) {
			System.out.println(temPoint.get(i).y);
			chaff.add(temPoint.get(i));

		}

		int count = 0;
		while (count <= 200) {
			Random rdn = new Random();
			Random rnY=new Random();
			int bitLen=rnY.nextInt(findMaxY(temPoint).bitLength()-findMinY(temPoint).bitLength())+findMinY(temPoint).bitLength();
			BigInteger y = new BigInteger(bitLen, rdn);
			//System.out.println("bit"+bitLen);
			Random rn = new Random();
//			BigInteger x = new BigInteger(findMaxX(temPoint).bitLength(), rn);
			int maxX=Integer.parseInt(findMaxX(temPoint).toString());
			int minX=Integer.parseInt(findMinX(temPoint).toString());
			int xInt=rn.nextInt(maxX-minX+1)+minX;
			BigInteger x = BigInteger.valueOf(xInt);
			if (compare(x, y, chaff) == true) {
				chaff.add(new Point(x, y));
				// fChaff.append(x + " " + y + "\n");
				// System.out.println("chieuvui" +count);
				count++;
			}

		}
		// random chaff ;
		// increasing List
		for (int j = 0; j < chaff.size(); j++) {
			for (int k = 0; k < chaff.size(); k++) {
				if ((chaff.get(j).x).compareTo(chaff.get(k).x) >= 0) {
					BigInteger xTemp = chaff.get(j).x;
					BigInteger yTemp = chaff.get(j).y;
					chaff.get(j).x = chaff.get(k).x;
					chaff.get(j).y = chaff.get(k).y;
					chaff.get(k).x = xTemp;
					chaff.get(k).y = yTemp;

				}
			}
		}

		
		return chaff;
	}
}
