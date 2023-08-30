import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class 총일수이용대출연장기간판별하기 {
	public static void main(String[] args) {
		LocalDate date1 = LocalDate.of(1, 1, 1);
		LocalDate date2 = LocalDate.now();
		long 총일수1 = ChronoUnit.DAYS.between(date1, date2);

		int 현재월 = 3;
		int 현재월일수 = LocalDate.of(2023, 3, 1).lengthOfMonth();

		// 14일 연장 기간 표시
		for (int i = 1; i <= 현재월일수; i++) {
			long 총일수2 = ChronoUnit.DAYS.between(date1, LocalDate.of(2023, 현재월, i));
			if (총일수2 >= 총일수1 && 총일수2 < 총일수1 + 14)
				System.out.println(
						"대출기간:" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.of(2023, 현재월, i)));
		}
	}
}
