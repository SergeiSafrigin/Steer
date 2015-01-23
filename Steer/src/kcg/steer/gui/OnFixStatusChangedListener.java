package kcg.steer.gui;

import kcg.steer.logic.StaticValues;

public interface OnFixStatusChangedListener {
	public void onChanged(StaticValues.GPS_STATUS status);
}
