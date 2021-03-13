package z01v2;

public enum Dan {
	
	PONEDELJAK("Pon", "MO"),
	UTORAK("Uto", "TU"),
	SREDA("Sre", "WE"),
	CETVRTAK("Cet", "TH"),
	PETAK("Pet", "FR"),
	SUBOTA("Sub", "SA"),
	NEDELJA("Ned", "SU");
	
	private final String EN;
	private final String SR;
	
	private Dan(String sr, String en) {
		this.SR = sr;
		this.EN = en;
	}
	
	@Override
	public String toString() {
		return SR;
	}
	
	public static Dan fromEn(String en) {
		for (Dan d : Dan.values()) {
			if (d.EN.equalsIgnoreCase(en)) {
				return d;
			}
		}
		return null;
	}
}
