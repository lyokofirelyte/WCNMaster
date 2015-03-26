package com.github.lyokofirelyte.Platform.Rounds;

public enum Round {

	ZERO("0"),
	ONE("1"),
	TWO("2"),
	THREE("3"),
	FOUR("4"),
	FIVE("5"),
	SIX("6"),
	SEVEN("7"),
	EIGHT("8"),
	NINE("9"),
	TEN("10"),
	ELEVEN("11");
	
	String round;
	
	Round(String r){
		round = r;
	}
	
	@Override
	public String toString(){
		return round;
	}
}