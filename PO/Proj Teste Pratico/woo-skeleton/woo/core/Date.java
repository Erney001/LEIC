package woo.core;

import java.io.Serializable;

public class Date implements Serializable {

	private int _currentDate;

	public Date() {
		_currentDate = 0;
	}

	/**
	* @param daysToAdvace
	*/
	void advanceDate(int daysToAdvance) {
		_currentDate += daysToAdvance;
	}

	public int getDate() {
		return _currentDate;
	}
}