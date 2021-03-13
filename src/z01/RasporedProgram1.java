/*
 * U ulaznom fajlu 'raspored.ics' je dat raspored casova druge godine IT smera
 * u iCalendar formatu.
 *
 * Kalendar pocinje linijom 'BEGIN:VCALENDAR' a zavrsava se linijom
 * 'END:VCALENDAR'. Izmedju ove dve linije se nalaze podaci o svim dogadjajima
 * ukljucujuci i podatke o casovima predavanja i vezbi.
 *
 * Svaki cas predavanja i vezbi je predstavljen zasebnim dogadjajem. Svaki
 * dogadjaj pocinje linijom 'BEGIN:VEVENT' a zavrsava sa 'END:VEVENT'. Izmedju
 * ovih linija se nalaze podaci za konkretan dogadjaj, odnosno cas.
 *
 * Podaci o casovima se nalaze u osobinama odgovarajucih dogadjaja i zapisani
 * su na sledeci nacin, svaki u svojoj liniji:
 * OSOBINA;ATRIBUT1=Vrednost1;ATRIBUT2=Vrednost2:VrednostOsobine
 * Dugacke osobine se mogu razdvojiti u vise linija. Pri tome prva linija
 * pocinje normalno dok je svaka sledeca uvucena jednom prazninom. Cela osobina
 * se dobija konkatenacijom linija, naravno ne ukljucujuci vodecu prazninu.
 *
 * Osobine od znacaja za raspored casova su:
 *
 * 1) Vreme pocetka i kraja casa (DTSTART i DTEND) cija vrednost sadrzi datum i
 *    vreme u formatu YYYYMMDDTHHMMSS. Prvih 8 znakova predstavlja datum i moze
 *    se slobodno ignorisati. Poslednjih 6 znakova predstavlja vreme i sadrzi
 *    po dve cifre za sat, minut i sekindu pocetka, odnosno kraja casa. Takodje,
 *    osobina moze sadrzati i atribut sa podatkom o vremenskoj zoni koji se
 *    moze slobodno ignorisati.
 *
 * 2) Pravilo ponavljanja (RRULE) sadrzi, izmedju ostalih podataka, dan u
 *    nedelji kada se cas odrzava. Iako je ovaj podatak moguce izracunati na
 *    osnovu datuma u osobini (DTSTART) lakse je koristiti ovu osobinu. Dan u
 *    nedelji je zapisan kao engleska dvoslovna srkacenica (MO, TU, WE...) kao
 *    vrednost komponente 'BYDAY'.
 *
 * 3) Predmet, nastavnik, tip, sala (SUMMARY) se nalaze zajedno odvojeni
 *    zarezom ispred kojeg se nalazi obrnuta kosa crta.
 *
 * Vise podataka o iCalendar formatu se moze naci na sledecem linku:
 * https://en.wikipedia.org/wiki/ICalendar
 *
 * Napisati aplikaciju koja ucitava podatke iz fajla, i najpre proverava da li
 * su svi podaci u fajlu dobro zadati. Ukoliko za neki od casova ne postoji
 * potrebna osobina ili format osobine nije zapisan na gore naveden nacin,
 * program na ekran ispisuje koja osobina je u pitanju i u kom redu fajla se
 * nalazi.
 *
 * Zatim, program od korisnika u ucitava naziv predmeta i ispisuje na ekran dan
 * u nedelji i vremena pocetka i kraja casa predavanja zadatog predmeta (tip
 * casa predavanja je 'P') kao i salu u kojoj se odrzava (ili online ako se
 * odrzava online), odnosno odgovarajucu poruku ukoliko predmet ne postoji u fajlu.
 *
 * Program ne treba da razlikuje mala i velika slova ni u kom delu svoje
 * funkcionalnosti.
 *
 * Jedan primer casa zapisanog kao dogadjaj u kalendaru:
 *
 * ...
 * BEGIN:VEVENT
 * ...
 * DTSTART;TZID=Europe/Belgrade:20210225T090000
 * DTEND;TZID=Europe/Belgrade:20210225T110000
 * ...
 * RRULE:FREQ=WEEKLY;BYDAY=TH
 * ...
 * SUMMARY:Objektno - orijentisano programiranje 2\, M. Radovanovic\, (P)\, online
 * ...
 * END:VEVENT
 * ...
 * 
 * Ovaj dogadjaj predstavlja predavanja iz predmeta Objektno-orijentisano
 * programiranje 2, koji se odrzava cetvrtkom od 9 do 11h online.
 */

package z01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RasporedProgram1 {

	public static void main(String[] args) throws IOException {

		String[] lines = readFile();
		check(lines);

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Unesite naziv predmeta: ");
		String unos = in.readLine();
		print(lines, unos);

	}

	private static String[] readFile() throws IOException {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(
				RasporedProgram1.class.getResourceAsStream("raspored.ics")))) {
			List<String> lines = new ArrayList<>();
			String line;
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
			return lines.toArray(new String[lines.size()]);
		}
	}

	// Vraca ime osobine u datoj liniji, tj.
	// podstring od pocetka do prvog znaka ':' ili ';'
	private static String getPropertyName(String line) {
		int index = line.indexOf(':');
		if (index == -1) {
			return null;
		}
		int indexAlt = line.indexOf(';');
		if (indexAlt != -1 && indexAlt < index) {
			index = indexAlt;
		}
		return line.substring(0, index);
	}

	// Vraca vrednost osobine u datoj liniji, tj.
	// podstring prvog znaka ':' do kraja linije
	private static String getPropertyValue(String line) {
		int index = line.indexOf(':');
		if (index == -1) {
			return null;
		}
		return line.substring(index + 1);
	}

	// Proverava ceo fajl
	private static void check(String[] lines) {
		
		// Proveravamo samo osobine unutar dogadjaja
		// i pomocu ove promenljive znamo da li smo
		// izmedju 'BEGIN:VEVENT' i 'END:VEVENT'
		boolean inEvent = false;
		
		// Idemo po svim linijama
		for (int i = 0; i < lines.length; i++) {
			
			// Podelimo liniju i izvucemo ime i vrednost osobine
			// dok atribute ignorisemo
			String propertyName = getPropertyName(lines[i]);
			String propertyValue = getPropertyValue(lines[i]);

			// Postavljamo kontrolnu promenljivu da znamo da li
			// smo unutar ili izvan dogadjaja
			if ("BEGIN".equalsIgnoreCase(propertyName) && "VEVENT".equalsIgnoreCase(propertyValue)) {
				inEvent = true;
			} else if ("END".equalsIgnoreCase(propertyName) && "VEVENT".equalsIgnoreCase(propertyValue)) {
				inEvent = false;
			}

			// Ostale osobine ignorisemo ako su van dogadjaja
			if (!inEvent) {
				continue;
			}

			if ("DTSTART".equalsIgnoreCase(propertyName) && !checkTime(propertyValue)) {
				reportError("Vreme pocetka nije dobro", i, lines[i]);
			} else if ("DTEND".equalsIgnoreCase(propertyName) && !checkTime(propertyValue)) {
				reportError("Vreme kraja nije dobro", i, lines[i]);
			} else if ("RRULE".equalsIgnoreCase(propertyName) && !checkDay(propertyValue)) {
				reportError("Dan u nedelji nije dobar", i, lines[i]);
			} else if ("SUMMARY".equalsIgnoreCase(propertyName) && !checkData(propertyValue)) {
				reportError("Nepotpuni podaci o predmetu", i, lines[i]);
			}

		}
	}

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmmss");
	private static boolean checkTime(String value) {
		try {
			if (value.charAt(8) != 'T') {
				return false;
			}
			StringBuilder builder = new StringBuilder(value);
			builder.setCharAt(8, ' ');
			format.parse(builder.toString());
			return true;
		} catch (IndexOutOfBoundsException e) {
			// Ako je string kraci pa charAt() i setCharAt() bace izuzetak
			return false;
		} catch (ParseException e) {
			// Ako SimpleDateFormat ne uspe da prepozna string
			return false;
		}
	}

	private static boolean checkDay(String value) {
		// 'BYDAY' moze biti bilo gde u liniji, ne samo na kraju,
		// tako da moramo da proverimo sve delove
		String byDay = null;
		String[] values = value.split(";");
		for (String v : values) {
			if (v.toUpperCase().startsWith("BYDAY=")) {
				byDay = v.substring(6);
			}
		}
		return Dan.fromEn(byDay) != null;
	}

	private static boolean checkData(String value) {
		// Linija mora da sadrzi 4 podatka:
		// Naziv predmeta, ime nastavnika, tip i salu.
		// Izuzetak su dogadjaji kao npr. 'Aprilski rok' koji
		// ne sadrze zareze
		String[] values = value.split("\\\\,", -1);
		return values.length == 4 || values.length == 1;
	}

	private static void reportError(String message, int lineNumber, String line) {
		// Ispis greske u nesto lepsem formatu
		System.err.printf("%s, linija %d: %s%n", message, lineNumber, line);
	}

	private static void print(String[] lines, String name) {
		
		// Podaci koje trazimo
		String casPoc = null;
		String casKraj = null;
		Dan casDan = null;
		String casPredmet = null;
		String casSala = null;
		
		// Idemo redom po svim linijama.
		// Kad naidjemo na novi dogadjaj, tj. 'BEGIN:VEVENT',
		// brisemo sve podatke (posto se odnose na prethodni dogjadjaj).
		// Kad naidjemo na neku osobinu koja nas zanima, izvlacimo potrebne podatke
		// Kad naidjemo na kraj dogadjaja, tj. 'END:VEVENT',
		// gledamo da li je to cas predavanja koji trazimo i stampamo ga ako jeste
		for (String line : lines) {
			String propertyName = getPropertyName(line);
			String propertyValue = getPropertyValue(line);

			// Brisanje podataka kad se naidje na novi dogadjaj
			if ("BEGIN".equalsIgnoreCase(propertyName) && "VEVENT".equalsIgnoreCase(propertyValue)) {
				casPoc = null;
				casKraj = null;
				casDan = null;
				casPredmet = null;
				casSala = null;

			// Stampanje casa, ali samo ako su podaci potpuni
			} else if ("END".equalsIgnoreCase(propertyName) && "VEVENT".equalsIgnoreCase(propertyValue)) {
				if (casDan != null && casSala != null) {
					System.out.printf("%s %s-%s: %s (%s)%n", casDan, casPoc, casKraj, casPredmet, casSala);
				}

			// Izvlacimo vreme pocetka ali samo ako je linija prosla proveru i sve je ok
			} else if ("DTSTART".equalsIgnoreCase(propertyName) && checkTime(propertyValue)) {
				int n = propertyValue.length();
				String sat = propertyValue.substring(n - 6, n - 4);
				String min = propertyValue.substring(n - 4, n - 2);
				StringBuilder builder = new StringBuilder(5);
				builder.append(sat);
				builder.append('.');
				builder.append(min);
				casPoc = builder.toString();
				
			// Izvlacimo vreme kraja ako je linija prosla proveru
			} else if ("DTEND".equalsIgnoreCase(propertyName) && checkTime(propertyValue)) {
				int n = propertyValue.length();
				String sat = propertyValue.substring(n - 6, n - 4);
				String min = propertyValue.substring(n - 4, n - 2);
				StringBuilder builder = new StringBuilder(5);
				builder.append(sat);
				builder.append('.');
				builder.append(min);
				casKraj = builder.toString();

			// Izvlacimo dan u nedelji samo ako je linija ok
			} else if ("RRULE".equalsIgnoreCase(propertyName) && checkDay(propertyValue)) {
				String[] values = propertyValue.split(";");
				for (String v : values) {
					if (v.startsWith("BYDAY=")) {
						casDan = Dan.fromEn(v.substring(6));
					}
				}

			// Izvlacimo salu ali samo ako su u pitanju predavanja iz zeljenog predmeta
			} else if ("SUMMARY".equalsIgnoreCase(propertyName) && checkData(propertyValue)) {
				String[] values = propertyValue.split("\\\\,", -1);
				if (values.length == 4 && values[0].equalsIgnoreCase(name) && values[2].trim().equalsIgnoreCase("(P)")) {
					casPredmet = values[0].trim();
					casSala = values[3].trim();
				}
			}
		}
	}
}
