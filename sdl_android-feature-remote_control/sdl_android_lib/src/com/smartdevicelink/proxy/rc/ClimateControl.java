package com.smartdevicelink.proxy.rc;

import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rc.datatypes.ClimateControlData;
import com.smartdevicelink.proxy.rc.datatypes.InteriorZone;
import com.smartdevicelink.proxy.rc.enums.ModuleType;
import com.smartdevicelink.proxy.rc.enums.TemperatureUnit;
import com.smartdevicelink.proxy.rc.rpc.SetInteriorVehicleData;

public class ClimateControl extends Module {

	
	public ClimateControl(InteriorZone zone) {
		super(ModuleType.CLIMATE,zone);
	}
	
	public RPCRequest setFanSpeed(int fanSpeed){
		ClimateControlData climate = new ClimateControlData();
		climate.setFanSpeed(fanSpeed);
		return new SetInteriorVehicleData(getBaseModuleData(climate));
	}
	
	/**
	 * Set the desired temperature for this zone
	 * @param desiredTemp
	 * @param temperatureUnit
	 * @return
	 */
	public RPCRequest setTemp(int desiredTemp, TemperatureUnit temperatureUnit){
		ClimateControlData climate = new ClimateControlData();
		climate.setDesiredTemp(desiredTemp);
		climate.setTemperatureUnit(temperatureUnit);
		return new SetInteriorVehicleData(getBaseModuleData(climate));

	}
	
	public RPCRequest enalbeAC(boolean enable){
		ClimateControlData climate = new ClimateControlData();
		climate.setACEnabled(enable);
		return new SetInteriorVehicleData(getBaseModuleData(climate));
	}
}
