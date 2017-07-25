package com.smartdevicelink.proxy.rc.datatypes;

import java.util.Hashtable;

import com.smartdevicelink.proxy.rc.enums.DefrostZone;
import com.smartdevicelink.proxy.rc.enums.ModuleType;
import com.smartdevicelink.proxy.rc.enums.TemperatureUnit;
import com.smartdevicelink.util.DebugTool;

public class ClimateControlData extends ControlData {

	public static final String KEY_FAN_SPEED				= "fanSpeed";
	public static final String KEY_CURRENT_TEMP				= "currentTemp";
	public static final String KEY_DESIRED_TEMP				= "desiredTemp";
	public static final String KEY_TEMP_UNIT				= "temperatureUnit";
	public static final String KEY_AC_ENABLED				= "acEnable";
	public static final String KEY_RECIRCULATE_AIR_ENABLED	= "recirculateAirEnable";
	public static final String KEY_AUTO_MODE_ENABLED		= "autoModeEnable";
	public static final String KEY_DEFROST_ZONE				= "defrostZone";
	public static final String KEY_DUAL_MODE_ENABLED		= "dualModeEnable";

	
	
	/**
	 * Constructs a newly allocated ClimateControlData object
	 */
	public ClimateControlData() { 
		this.setInteriorDataType(ModuleType.CLIMATE);
	}

	/**
	 * Constructs a newly allocated ClimateControlData object indicated by the Hashtable parameter
	 * @param hash The Hashtable to use
	 */      
	public ClimateControlData(Hashtable<String, Object> hash) {
		super(hash);
		this.setInteriorDataType(ModuleType.CLIMATE);
	}
	
	/**
	 * 
	 * @return A value between 0-100 or null if not included
	 */
	public Integer getFanSpeed(){
		return (Integer) store.get(KEY_FAN_SPEED);
	}

	/**
	 * 
	 * @param fanSpeed Values 0-100 accepted
	 */
	public void setFanSpeed(Integer fanSpeed){
		if (fanSpeed!=null) {
			store.put(KEY_FAN_SPEED, fanSpeed);
		} else {
			store.remove(KEY_FAN_SPEED);
		}
	}
	
	/**
	 * 
	 * @return A value between 0-100 or null if not included
	 */
	public Integer getCurrentTemp(){
		return (Integer) store.get(KEY_CURRENT_TEMP);
	}

	/**
	 * 
	 * @param currentTemp Values 0-100 accepted
	 */
	public void setCurrentTemp(Integer currentTemp){
		if (currentTemp!=null) {
			store.put(KEY_CURRENT_TEMP, currentTemp);
		} else {
			store.remove(KEY_CURRENT_TEMP);
		}
	}
	
	/**
	 * 
	 * @return A value between 0-100 or null if not included
	 */
	public Integer getDesiredTemp(){
		return (Integer) store.get(KEY_DESIRED_TEMP);
	}

	/**
	 * 
	 * @param desiredTemp Values 0-100 accepted
	 */
	public void setDesiredTemp(Integer desiredTemp){
		if (desiredTemp!=null) {
			store.put(KEY_DESIRED_TEMP, desiredTemp);
		} else {
			store.remove(KEY_DESIRED_TEMP);
		}
	}
	
	/**
	 * 
	 * @return a TemperatureUnit enum, or null if not included
	 */
	public TemperatureUnit getTemperatureUnit(){
		 Object obj = store.get(KEY_TEMP_UNIT);
		 if (obj instanceof TemperatureUnit) {
	            return (TemperatureUnit) obj;
	        } else if (obj instanceof String) {
	        	TemperatureUnit unit = null;
	            try {
	                unit = TemperatureUnit.valueOf((String) obj);
	            } catch (Exception e) {
	                DebugTool.logError("Failed to parse " + getClass().getSimpleName() + "." + KEY_TEMP_UNIT, e);
	            }
	            return unit;
	        }
		
		 return null;
	}

	/**
	 * 
	 * @param temperatureUnit Values of TemperatureUnit accepted. Null will remove key/value pair.
	 */
	public void setTemperatureUnit(TemperatureUnit temperatureUnit){
		if (temperatureUnit!=null) {
			store.put(KEY_TEMP_UNIT, temperatureUnit);
		} else {
			store.remove(KEY_TEMP_UNIT);
		}
	}
	
	/**
	 * 
	 * @return True, False, or null if not included
	 */
	public Boolean getACEnabled(){
		return (Boolean) store.get(KEY_AC_ENABLED);
	}

	/**
	 * 
	 * @param acEnable Values True/False accepted. Null will remove key/value pair.
	 */
	public void setACEnabled(Boolean acEnable){
		if (acEnable!=null) {
			store.put(KEY_AC_ENABLED, acEnable);
		} else {
			store.remove(KEY_AC_ENABLED);
		}
	}
	
	/**
	 * 
	 * @return True, False, or null if not included
	 */
	public Boolean getRecirculateEnabled(){
		return (Boolean) store.get(KEY_RECIRCULATE_AIR_ENABLED);
	}

	/**
	 * 
	 * @param recirculateAirEnable Values True/False accepted. Null will remove key/value pair.
	 */
	public void setRecirculateEnabled(Boolean recirculateAirEnable){
		if (recirculateAirEnable!=null) {
			store.put(KEY_RECIRCULATE_AIR_ENABLED, recirculateAirEnable);
		} else {
			store.remove(KEY_RECIRCULATE_AIR_ENABLED);
		}
	}
	
	/**
	 * 
	 * @return True, False, or null if not included
	 */
	public Boolean getAutoModeEnabled(){
		return (Boolean) store.get(KEY_AUTO_MODE_ENABLED);
	}

	/**
	 * 
	 * @param autoModeEnable Values True/False accepted. Null will remove key/value pair.
	 */
	public void setAutoModeEnabled(Boolean autoModeEnable){
		if (autoModeEnable!=null) {
			store.put(KEY_AUTO_MODE_ENABLED, autoModeEnable);
		} else {
			store.remove(KEY_AUTO_MODE_ENABLED);
		}
	}
	
	/**
	 * 
	 * @return True, False, or null if not included
	 */
	public DefrostZone getDefrostZone(){
		 Object obj = store.get(KEY_DEFROST_ZONE);
		 if (obj instanceof DefrostZone) {
	            return (DefrostZone) obj;
	        } else if (obj instanceof String) {
	        	DefrostZone zone = null;
	            try {
	                zone = DefrostZone.valueOf((String) obj);
	            } catch (Exception e) {
	                DebugTool.logError("Failed to parse " + getClass().getSimpleName() + "." + KEY_DEFROST_ZONE, e);
	            }
	            return zone;
	        }
		
		 return null;
	}

	/**
	 * 
	 * @param defrostZone Values True/False accepted. Null will remove key/value pair.
	 */
	public void setDefrostZone(DefrostZone defrostZone){
		if (defrostZone!=null) {
			store.put(KEY_DEFROST_ZONE, defrostZone);
		} else {
			store.remove(KEY_DEFROST_ZONE);
		}
	}
	
	/**
	 * 
	 * @return True, False, or null if not included
	 */
	public Boolean getDualModeEnabled(){
		return (Boolean) store.get(KEY_DUAL_MODE_ENABLED);
	}

	/**
	 * 
	 * @param dualModeEnable Values True/False accepted. Null will remove key/value pair.
	 */
	public void setDualModeEnabled(Boolean dualModeEnable){
		if (dualModeEnable!=null) {
			store.put(KEY_DUAL_MODE_ENABLED, dualModeEnable);
		} else {
			store.remove(KEY_DUAL_MODE_ENABLED);
		}
	}
	
}
