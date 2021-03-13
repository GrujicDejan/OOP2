package z01v2

/*
 * Napisati program koji ispisuje raspored casova.
 *
 * 1) Napraviti klasu "Cas" koja predstavlja jedan cas. Ova klasa od osobina
 *    treba da ima bar naziv predmeta, ime predavaca, tip casa, vreme i mesto
 *    odrzavanja. Takodje, ova klasa implementira interfejs Comparable kao i
 *    metode equals(), hashCode() i toString().
 *
 * 2) Napraviti nabrojivi tip "Dan" koji predstavlja dane u nedelji.
 *
 * 3) Napraviti klasu "Raspored" koja sadrzi mapu ciji su kljucevi dani u
 *    nedelji a vrednosti liste casova koji se odrzavaju tog dana. Ove liste
 *    su sortirane po vremenu pocetka casa. Takodje implementirati i potrebne
 *    metode za pristup i izmenu podataka.
 *
 * 4) Pitati korisnika za koju godinu IT smera zeli raspored. Identifikatori
 *    Google kalendara za odgovarajuce godine su:
 *        1 godina: g3khre7jrsih1idp5b5ahgm1f8
 *        2 godina: hu93vkklcv692mikqvm17scnv4
 *        3 godina: 6ovsf0fqb19q10s271b9e59ttc
 *        4 godina: a730slcmbr9c6dii94j9pbdir4
 *
 * 5) Ucitati odgovarajuci raspored casova, izvuci potrebne podatke u instance
 *    klase "Cas" i smestiti ih u jednu instancu klase "Raspored".
 *
 * 6) Pitati korisnika za koji dan u nedelji zeli raspored i ispisati casove
 *    koji se odrzavaju tog dana.
 *
 * Metod za ucitavanje rasporeda sa interneta je vec dat, kao i kako treba da
 * izgleda URL za Google kalendar sa datim identifikatorom. 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RasporedProgram2 {
	
	private static Raspored raspored = new Raspored();
	private static final LocalDate dat1 = LocalDate.of(2021, 2, 17);

	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		String id = setId(scanner);
		int dan = setDan(scanner);
		scanner.close();
		URL url = new URL("https://calendar.google.com/calendar/ical/" + id + "%40group.calendar.google.com/public/basic.ics");
		String text = readURL(url);
		parseTextToRaspored(text);
		ispisiRaspored(dan);
	}

	private static int setDan(Scanner scanner) {
		System.out.println("Meni:\n1 - Ponedeljak\n2 - Utorak\n3 - Sreda\n"
				+ "4 - Cetvrtak\n5 - Petak\n6 - Subota\n7 - Nedelja");
		
		int input;
		do {
			System.out.println("Odaberite dan u nedelji [1-7] za prikaz casova:");
			input = scanner.nextInt();
			System.out.println();
		} while (input < 1 || input > 7);
		
		return input;
	}

	private static String setId(Scanner scanner) {
		System.out.println("Meni:\n1 - Prva godina\n2 - Druga godina\n3 - Treca godina\n4 - Cetvrta godina");
		
		int input;
		do {
			System.out.println("Odaberite za koju godinu [1-4] zelite prikaz rasporeda:");
			input = scanner.nextInt();
			System.out.println();
		} while (input < 1 || input > 4);
		
		if (input == 1)
			return "g3khre7jrsih1idp5b5ahgm1f8";
		else if (input == 2)
			return "hu93vkklcv692mikqvm17scnv4";
		else if (input == 3)
			return "6ovsf0fqb19q10s271b9e59ttc";
		else
			return "a730slcmbr9c6dii94j9pbdir4";
	}

	private static String readURL(URL url) throws IOException {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
			StringBuilder text = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
			return text.toString();
		}
	}

	private static void parseTextToRaspored(String text) {    
		
		Pattern eventPattern = Pattern.compile("(?sm)^BEGIN:VEVENT$(?<sadrzaj>.*?)^END:VEVENT$");
		Pattern dtstartPettern = Pattern.compile("(?sm)^DTSTART(?:;.*?)*:(?<godina>\\d{4})(?<mesec>\\d{2})(?<dan>\\d{2})T(?<sat>\\d{2})(?<minut>\\d{2})\\d{2}$");
		Pattern dtendPettern = Pattern.compile("(?sm)^DTEND(?:;.*?)*:.*?T(?<sat>\\d{2})(?<minut>\\d{2})\\d{2}$");
		Pattern prulePettern = Pattern.compile("(?sm)^RRULE:(?:.*?;)*BYDAY=(?<dan>.*?)$");
		Pattern summaryPattern = Pattern.compile("(?sm)^SUMMARY:\\s*(?<predmet>.*?)\\s*\\\\,\\s*(?<nastavnik>.*?)\\s*\\\\,\\s*\\((?<tip>.*?)\\)\\s*\\\\,\\s*(?<mesto>.*?)\\s*$");
		Matcher eventMatcher = eventPattern.matcher(text);
		while (eventMatcher.find()) {
			String event = eventMatcher.group("sadrzaj");
			
			LocalTime vremePoc = null;
			LocalTime vremeKraj = null;
			LocalDate datum = null;
			String predmet = null;
			String tip = null;
			String nastavnik = null;
			String mesto = null;
			Dan dan = null;
			
			Matcher dtstartMatcher = dtstartPettern.matcher(event);
			if (dtstartMatcher.find()) {
				vremePoc = LocalTime.of(Integer.parseInt(dtstartMatcher.group("sat")), Integer.parseInt(dtstartMatcher.group("minut")));
				datum = LocalDate.of(Integer.parseInt(dtstartMatcher.group("godina")), Integer.parseInt(dtstartMatcher.group("mesec")), Integer.parseInt(dtstartMatcher.group("dan")));
			}
			Matcher dtendMatcher = dtendPettern.matcher(event);
			if (dtendMatcher.find()) {
				vremeKraj = LocalTime.of(Integer.parseInt(dtendMatcher.group("sat")), Integer.parseInt(dtendMatcher.group("minut")));
			}
			Matcher pruleMatcher = prulePettern.matcher(event);
			if (pruleMatcher.find()) {
				dan = Dan.fromEn(pruleMatcher.group("dan"));
			}
			Matcher summaryMatcher = summaryPattern.matcher(event);
			if (summaryMatcher.find()) {
				predmet = summaryMatcher.group("predmet");
				nastavnik = summaryMatcher.group("nastavnik");
				tip = summaryMatcher.group("tip");
				mesto = summaryMatcher.group("mesto");
			}
			
			if (predmet == null || nastavnik == null || tip == null || mesto == null) {
				reportError("Nepotpuni podaci o predmetu", eventMatcher);
			} else if (vremePoc == null) {
				reportError("Vreme pocetka nije dobro", eventMatcher);
			} else if (vremeKraj == null) {
				reportError("Vreme kraja nije dobro", eventMatcher);
			} else if (dan == null) {
				reportError("Dan u nedelji nije dobar", eventMatcher);
			} else {
				if (datum.compareTo(dat1) > 0) {
					Cas cas = new Cas(predmet, nastavnik, tip, mesto, vremePoc, vremeKraj);
					raspored.dodajURaspored(dan, cas);
				}
			}
			
		}
	}
	
	private static void reportError(String message, Matcher matcher) {
		Pattern idPattern = Pattern.compile("(?sm)^UID:\\s*(?<id>.*?)\\s*$");
		Matcher idMatcher = idPattern.matcher(matcher.group(1));
		String id = idMatcher.find() ? idMatcher.group("id") : "???";
		Pattern infoPattern = Pattern.compile("(?sm)^SUMMARY:\\s*(?<info>.*?)\\s*$");
		Matcher infoMatcher = infoPattern.matcher(matcher.group(1));
		String info = infoMatcher.find() ? infoMatcher.group("info") : "???";
		System.err.printf("%s, dogadjaj %s: %s%n", message, id, info);
	}
	
	private static void ispisiRaspored(int input) {
		
		switch(input) {
			case 1: raspored.ispisiRaspored(Dan.PONEDELJAK); break;
			case 2: raspored.ispisiRaspored(Dan.UTORAK); break;
			case 3: raspored.ispisiRaspored(Dan.SREDA); break;
			case 4: raspored.ispisiRaspored(Dan.CETVRTAK); break;
			case 5: raspored.ispisiRaspored(Dan.PETAK); break;
			case 6: raspored.ispisiRaspored(Dan.SUBOTA); break;
			case 7: raspored.ispisiRaspored(Dan.NEDELJA); break;
		}
		
	}
	
	
}
