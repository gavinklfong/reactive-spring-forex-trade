package space.gavinklfong.forex.services;

public enum RateTier {

	TIER1(0.025),
	TIER2(0.05),
	TIER3(0.1),
	TIER4(0.5);
	
	
	public final double rate;
	
	private RateTier(double rate) {
		this.rate = rate;
	}
	
	
}
