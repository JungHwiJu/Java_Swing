//package db;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//
//import javax.swing.JOptionPane;
//
//public class DB {
//	public static Connection con;
//	public static PreparedStatement ps;
//	public static Statement stmt;
//	
//	static {
//		try {
//			con = DriverManager.getConnection(
//					"jdbc:mysql://localhost?serverTimezone=UTC&allowPublicKeyRetreival=true&allowLoadLocalInfile=true",
//					"root", "1234");
//			stmt = con.createStatement();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	
//	
//	static public void execute(String sql, Object...obj){
//		try {
//			var pst = con.prepareStatement(sql);
//			if(obj != null) {
//				for(int i = 0; i < obj.length; i++) {
//					pst.setObject(i+1, obj[i]);
//				}
//			}
//			System.out.println(pst);
//			pst.execute();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	void createT(String t, String c) {
//		execute("create table `" + t + "`(" + c + ")");
//		execute("load data local infile './Datafiles/" + t + ".txt' into table `" + t + "` ignore 1 lines");
//	}
//
//	public DB() {
//		execute("drop database if exists 2023지방_2");
//		execute("create database 2023지방_2 default character set utf8");
//		execute("drop user if exists user@localhost");
//		execute("create user user@localhost identified by '1234'");
//		execute("grant select, update, insert, delete on libray. * to user@localhost");
//		execute("set global local_infile=1");
//		execute("SET session sql_mode = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION'");
//		execute("use 2023지방_2");
//
//		createT("user", "u_no int primary key not null auto_increment, u_name varchar(5), u_id varchar(10), u_pw varchar(10)");
//		createT("division", "d_no int primary key not null auto_increment, d_name varchar(50)");
//		createT("book", "b_no int primary key not null auto_increment, b_name varchar(50), d_no int, b_author varchar(50), b_count int, b_page int, b_exp varchar(500), b_img longblob, foreign key(d_no) references division(d_no)");
//		createT("likebook", "l_no int primary key not null auto_increment, u_no int, b_no int, foreign key(u_no) references user(u_no), foreign key(b_no) references book(b_no)");
//		createT("rental", "r_no int primary key not null auto_increment, u_no int, b_no int, r_date date, r_count int, r_reading int, r_returnday date, foreign key(u_no) references user(u_no), foreign key(b_no) references book(b_no)");
//		
//		for(var rs : getRows("select b_no from book")) {
//			try {
//				execute("update book set b_img = ? where b_no = ?", new FileInputStream(new File("./datafiles/book/" + rs.get(0) + ".jpg")), rs.get(0));
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//		}
//		JOptionPane.showMessageDialog(null, "성공", "정보", 1);
//	}
//	
//	public static ArrayList<ArrayList<Object>> getRows(String sql, Object...obj){	//??????그래서 이게 뭐임?
//		var row = new ArrayList<ArrayList<Object>>();
//		try {
//			var pst = con.prepareStatement(sql);
//			if(obj != null) {
//				for(int i = 0; i < obj.length; i++) {
//					pst.setObject(i+1, obj[i]);
//				}
//			}
//			var rs = pst.executeQuery();
//			var m = rs.getMetaData();
//			System.out.println(pst);
//			while(rs.next()) {
//				var rows = new ArrayList<Object>();
//				for(int i = 0; i < m.getColumnCount(); i++) { 
//					rows.add(rs.getObject(i+1));
//				}
//				row.add(rows);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return row;
//	}
//	
//	public static ResultSet rs(String sql) {
//		try {
//			var rs = stmt.executeQuery(sql);
//			return rs;
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return null;
//	}
//	public static String getOneRs(String sql) {
//		try {
//			var rs = stmt.executeQuery(sql);
//			if (rs.next()) {
//				return rs.getString(1);
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return "";
//	}
//	public static void main(String[] args) {
//		new DB();
//	}
//}
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class DB {
	public static Connection con;
	public static Statement stmt;

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowPublicKeyRetreival=true&allowLoadLocalInfile=true&zeroDateTimeBehavior=convertToNull","root", "1234");
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static ArrayList<ArrayList<Object>> getRows(String sql, Object... obj) {
		var row = new ArrayList<ArrayList<Object>>();
		try {
			var pst = con.prepareStatement(sql);
			if (obj != null) {
				for (int i = 0; i < obj.length; i++) {
					pst.setObject(i + 1, obj[i]);
				}
			}
			var rs = pst.executeQuery();
			var m = rs.getMetaData();
			System.out.println(pst);
			while (rs.next()) {
				var rows = new ArrayList<Object>();
				for (int i = 0; i < m.getColumnCount(); i++) {
					rows.add(rs.getObject(i + 1));
				}
				row.add(rows);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return row;
	}

	static public void execute(String sql, Object... obj) {
		try {
			var pst = con.prepareStatement(sql);
			if (obj != null) {
				for (int i = 0; i < obj.length; i++) {
					pst.setObject(i + 1, obj[i]);
				}
			}
			pst.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void createT(String t, String c) {
		execute("create table `" + t + "`(" + c + ")");
		execute("load data local infile './Datafiles/" + t + ".txt' into table `" + t + "` ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists 2023지방_2");
		execute("create database 2023지방_2 default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, delete on libray. * to user@localhost");
		execute("set global local_infile=1");
		execute("use 2023지방_2");
		execute("set GLOBAL sql_mode = ''");
		execute("SET session sql_mode = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION'");

		createT("user",
				"u_no int primary key not null auto_increment, u_name varchar(5), u_id varchar(10), u_pw varchar(10)");
		createT("division", "d_no int primary key not null auto_increment, d_name varchar(50)");
		createT("book",
				"b_no int primary key not null auto_increment, b_name varchar(50), d_no int, b_author varchar(50), b_count int, b_page int, b_exp varchar(500), b_img longblob, foreign key(d_no) references division(d_no)");
		createT("likebook",
				"l_no int primary key not null auto_increment, u_no int, b_no int, foreign key(u_no) references user(u_no), foreign key(b_no) references book(b_no)");
		createT("rental",
				"r_no int primary key not null auto_increment, u_no int, b_no int, r_date date, r_count int, r_reading int, r_returnday date, foreign key(u_no) references user(u_no), foreign key(b_no) references book(b_no)");

		for (var rs : getRows("select b_no from book")) {
			try {
				execute("update book set b_img = ? where b_no = ?",
						new FileInputStream(new File("./datafiles/book/" + rs.get(0) + ".jpg")), rs.get(0));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// due_Date 만든 뷰
		execute("CREATE VIEW `test1` AS SELECT *, (r_date + INTERVAL (14 + r_count) DAY) AS due_date FROM rental");
		// 0 : 반납완료, 1: 연체 중, 2: 반납 중
		execute("CREATE VIEW rentalstate AS SELECT *, if(r_returnday = '0000-00-00' or r_returnday is null, if(now() < due_date,'2','1'), '0')as state FROM 2023지방_2.test1;");
		JOptionPane.showMessageDialog(null, "성공", "정보", 1);
	}
	
	public static void main(String[] args) {
		new DB();
	}
}
