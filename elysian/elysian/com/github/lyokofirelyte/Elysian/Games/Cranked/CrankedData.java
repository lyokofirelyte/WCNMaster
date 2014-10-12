package com.github.lyokofirelyte.Elysian.Games.Cranked;

import com.github.lyokofirelyte.Elysian.Elysian;

public class CrankedData {

	Cranked root;
	Elysian main;

	CrankedData(Cranked i){
		root = i;
		main = root.main;
	}
}
