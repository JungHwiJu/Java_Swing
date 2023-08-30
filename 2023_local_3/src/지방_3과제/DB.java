package 지방_3과제;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class DB {
	static Connection con;
	static Statement stmt;

	String cascade = " on update cascade on delete cascade ";

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=Asia/Seoul&allowLoadLocalInfile=true", "root", "1234");
			stmt = con.createStatement();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "셋팅 실패", "경고", 0);
			e.printStackTrace();
		}
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void createT(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './datafiles/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists 2023지방_3");
		execute("drop user if exists user@localhost");
		execute("create database 2023지방_3");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, insert, delete, update on 2023지방_3.* to user@localhost");
		execute("set global local_infile = 1");
		execute("use 2023지방_3");

		createT("continent", "c_no int primary key not null auto_increment, c_name varchar(50), x int , y int");
		createT("user",
				"u_no int primary key not null auto_increment, id varchar(20), pw varchar(20), name1 varchar(20), name2 varchar(20), birth date, mileage int");
		createT("nation",
				"n_no int primary key not null auto_increment, c_no int, code varchar(3), n_name varchar(20), x int, y int, foreign key(c_no) references continent(c_no)"
						+ cascade);
		createT("schedule",
				"s_no int primary key not null auto_increment, date date, depart int, arrival int, departTime time, timeTaken time, price int, foreign key(depart) references nation(n_no)"
						+ cascade + ", foreign key(arrival) references nation(n_no)" + cascade);
		createT("reservation",
				"r_no int primary key not null auto_increment, u_no int, s_no int, income int, expense int, foreign key(u_no) references user(u_no)"
						+ cascade + ", foreign key(s_no) references schedule(s_no)" + cascade);
		createT("boarding",
				"b_no int primary key not null auto_increment, r_no int, agegroup int, 	b_name varchar(50), birth date, seat varchar(3), foreign key(r_no) references reservation(r_no)"
						+ cascade);
		JOptionPane.showMessageDialog(null, "셋팅 성공", "정보", 1);
	}

	public static void main(String[] args) {
		new DB();
	}
}
