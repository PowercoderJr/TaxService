package TaxService;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomHelper
{
	public enum Gender
	{
		MALE, FEMALE
	}

	;

	public static final long earlyDate = 10957; //Epoch day of 01.01.2000
	private static Random rnd = new Random();
	private static List<String> companies;
	private static List<String> namesF;
	private static List<String> namesM;
	private static List<String> patronymics;
	private static List<String> streets;
	private static List<String> surnames;

	static
	{
		try (InputStream companiesSrc = RandomHelper.class.getClassLoader().getResourceAsStream("nomenclature/companies.txt");
			 InputStream namesFSrc = RandomHelper.class.getClassLoader().getResourceAsStream("nomenclature/names_f.txt");
			 InputStream namesMSrc = RandomHelper.class.getClassLoader().getResourceAsStream("nomenclature/names_m.txt");
			 InputStream patronymicsSrc = RandomHelper.class.getClassLoader().getResourceAsStream("nomenclature/patronymics.txt");
			 InputStream streetsSrc = RandomHelper.class.getClassLoader().getResourceAsStream("nomenclature/streets.txt");
			 InputStream surnamesSrc = RandomHelper.class.getClassLoader().getResourceAsStream("nomenclature/surnames.txt"))
		{
			companies = new BufferedReader(new InputStreamReader(companiesSrc, StandardCharsets.UTF_8)).lines()
					.collect(Collectors.toList());
			namesF = new BufferedReader(new InputStreamReader(namesFSrc, StandardCharsets.UTF_8)).lines()
					.collect(Collectors.toList());
			namesM = new BufferedReader(new InputStreamReader(namesMSrc, StandardCharsets.UTF_8)).lines()
					.collect(Collectors.toList());
			patronymics = new BufferedReader(new InputStreamReader(patronymicsSrc, StandardCharsets.UTF_8)).lines()
					.collect(Collectors.toList());
			streets = new BufferedReader(new InputStreamReader(streetsSrc, StandardCharsets.UTF_8)).lines()
					.collect(Collectors.toList());
			surnames = new BufferedReader(new InputStreamReader(surnamesSrc, StandardCharsets.UTF_8)).lines()
					.collect(Collectors.toList());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static Date getRandomDateBetween(LocalDate first, LocalDate last)
	{
		long epochFirst = first.toEpochDay();
		long epochLast = last.toEpochDay();
		return Date.valueOf(LocalDate.ofEpochDay(epochFirst + rnd.nextLong() % (epochLast - epochFirst)));
	}

	public static String getRandomSurname(Gender gender)
	{
		return surnames.get(rnd.nextInt(surnames.size())) + (gender == Gender.FEMALE ? "а" : "");
	}

	public static String getRandomName(Gender gender)
	{
		List<String> src = gender == Gender.MALE ? namesM : namesF;
		return src.get(rnd.nextInt(src.size()));
	}

	public static String getRandomPatronymic(Gender gender)
	{
		return patronymics.get(rnd.nextInt(patronymics.size())) + (gender == Gender.FEMALE ? "на" : "ич");
	}

	public static String getRandomCompany()
	{
		return companies.get(rnd.nextInt(companies.size()));
	}

	public static String getRandomPhone()
	{
		return "+38(0" + (10 + rnd.nextInt(89)) + ")" + (100 + rnd.nextInt(899)) + "-" +
				(10 + rnd.nextInt(89)) + "-" + (10 + rnd.nextInt(89));
	}

	public static String getRandomStreet()
	{
		return streets.get(rnd.nextInt(streets.size()));
	}

	public static String getRandomHouse()
	{
		String house = String.valueOf(1 + rnd.nextInt(200));
		if (rnd.nextDouble() < 0.1)
			house += (char) ('а' + rnd.nextInt(6));
		return house;
	}

	private RandomHelper()
	{
		;
	}
}
