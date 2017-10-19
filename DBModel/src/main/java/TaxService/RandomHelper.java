package TaxService;

import java.sql.Date;
import java.time.LocalDate;

public class RandomHelper
{
	public static final long earlyDate = 10957; //Epoch day of 01.01.2000

	public static Date getRandomDateBetween(LocalDate first, LocalDate last)
	{
		long epochFirst = first.toEpochDay();
		long epochLast = last.toEpochDay();
		return Date.valueOf(LocalDate.ofEpochDay((long) (epochFirst + Math.random() * (epochLast - epochFirst))));
	}

	private RandomHelper()
	{
		;
	}
}
