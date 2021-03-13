package oop2.p02.raspored;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Raspored {
	
	private Map<Dan, ArrayList<Cas>> mapaRaspored = new HashMap<>();
	
	public void dodajURaspored(Dan dan, Cas cas) {
		if (mapaRaspored.get(dan) == null) {
			mapaRaspored.put(dan, new ArrayList<Cas>());
		}
		
		mapaRaspored.get(dan).add(cas);
		Collections.sort(mapaRaspored.get(dan));
	}
	
	public void ispisiRaspored(Dan dan) {
		if (mapaRaspored.get(dan) != null) {
			for (Cas cas : mapaRaspored.get(dan)) {
				System.out.println(cas);
			}
		} else {
			System.out.println("Odabrani dan (" + dan + ") nema casova u rasporedu");
		}
	}
	
}
