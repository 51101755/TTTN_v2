import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.mysql.jdbc.Connection;

/**
 * 
 */

/**
 * @author 51101_000
 * 
 */
public class Main {

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException
	 */
	static String SC = "";

	public static void main(String[] args) throws NoSuchAlgorithmException, ClassNotFoundException, SQLException {
	
		java.sql.Connection conn=GetConnection();
		Statement statement=conn.createStatement();
		System.out.println("type 1 to register account,  type 2 to authenticate account");
		Scanner x=new Scanner(System.in);
		//System.out.println(x.nextInt());
		if(x.nextInt()==1)
		{
			System.out.println("type username to register account");
			Scanner name=new Scanner(System.in);
			RegisterAcc reg=new RegisterAcc();
			String username=name.next();
			//statement.executeUpdate("INSERT INTO user(username) values ('"+username+"')");
			ArrayList<Point> ChaffPoint=reg.ChaffPoint;
			for(int i=0;i<ChaffPoint.size();i++)
			{
				statement.executeUpdate("insert into fuzzyvault(xCood,yCood,username) value('"+ChaffPoint.get(i).x+"','"+ChaffPoint.get(i).y+"','"+username+"')");
			}
			
		}
		else if(x.nextInt()==2)
		{
			try {
				AuthenticationAcc auth=new AuthenticationAcc();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	
	}
	public static java.sql.Connection GetConnection() throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/tttn", "root", "");
	}

	
}
