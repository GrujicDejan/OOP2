package z01v2;

import java.time.LocalTime;

public class Cas implements Comparable<Cas> {
	
	private String nazivPredmeta, nastavnik, tip, mesto;
	private LocalTime vremePoc, vremeKraj;
	
	public Cas(String nazivPredmeta, String nastavnik, String tip, String mesto, LocalTime vremePoc,
			LocalTime vremeKraj) {
		this.nazivPredmeta = nazivPredmeta;
		this.nastavnik = nastavnik;
		this.tip = tip;
		this.mesto = mesto;
		this.vremePoc = vremePoc;
		this.vremeKraj = vremeKraj;
	}
	
	public String getNazivPredmeta() {
		return nazivPredmeta;
	}

	public String getNastavnik() {
		return nastavnik;
	}

	public String getTip() {
		return tip;
	}

	public String getMesto() {
		return mesto;
	}

	public LocalTime getVremePoc() {
		return vremePoc;
	}

	public LocalTime getVremeKraj() {
		return vremeKraj;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mesto == null) ? 0 : mesto.hashCode());
		result = prime * result + ((nastavnik == null) ? 0 : nastavnik.hashCode());
		result = prime * result + ((nazivPredmeta == null) ? 0 : nazivPredmeta.hashCode());
		result = prime * result + ((tip == null) ? 0 : tip.hashCode());
		result = prime * result + ((vremeKraj == null) ? 0 : vremeKraj.hashCode());
		result = prime * result + ((vremePoc == null) ? 0 : vremePoc.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cas other = (Cas) obj;
		if (mesto == null) {
			if (other.mesto != null)
				return false;
		} else if (!mesto.equals(other.mesto))
			return false;
		if (nastavnik == null) {
			if (other.nastavnik != null)
				return false;
		} else if (!nastavnik.equals(other.nastavnik))
			return false;
		if (nazivPredmeta == null) {
			if (other.nazivPredmeta != null)
				return false;
		} else if (!nazivPredmeta.equals(other.nazivPredmeta))
			return false;
		if (tip == null) {
			if (other.tip != null)
				return false;
		} else if (!tip.equals(other.tip))
			return false;
		if (vremeKraj == null) {
			if (other.vremeKraj != null)
				return false;
		} else if (!vremeKraj.equals(other.vremeKraj))
			return false;
		if (vremePoc == null) {
			if (other.vremePoc != null)
				return false;
		} else if (!vremePoc.equals(other.vremePoc))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(Cas other) {
		int rez = this.vremePoc.getHour() - other.vremePoc.getHour();
		
		if (rez == 0)
			rez = this.getVremePoc().getMinute() - other.vremePoc.getMinute();
		
		return rez;
	}

	@Override
	public String toString() {
		return String.format("%-50s %-5s - %-10s %-30s %-10s %-8s", nazivPredmeta, vremePoc, vremeKraj, nastavnik, tip, mesto);
	}
	
}
