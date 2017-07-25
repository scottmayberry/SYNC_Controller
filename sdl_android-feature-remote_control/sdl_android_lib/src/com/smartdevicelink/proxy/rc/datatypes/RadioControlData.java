package com.smartdevicelink.proxy.rc.datatypes;

import java.util.Hashtable;

import com.smartdevicelink.proxy.rc.enums.ModuleType;
import com.smartdevicelink.proxy.rc.enums.RadioBand;
import com.smartdevicelink.proxy.rc.enums.RadioState;
import com.smartdevicelink.util.DebugTool;

public class RadioControlData extends ControlData{


	public static final String KEY_FREQUENCY_INTEGER 		= "frequencyInteger";
	public static final String KEY_FREQUENCY_FRACTION 		= "frequencyFraction";
	public static final String KEY_BAND 					= "band";
	public static final String KEY_RDS_DATA					= "rdsData";
	public static final String KEY_AVAILABLE_HDS			= "availableHDs";
	public static final String KEY_HD_CHANNEL				= "hdChannel";
	public static final String KEY_SIGNAL_STRENGTH			= "signalStrength";
	public static final String KEY_SIGNAL_CHANGE_THRESHOLD	= "signalChangeThreshold";
	public static final String KEY_RADIO_ENABLED			= "radioEnable";
	public static final String KEY_STATE 					= "state";
	
	

	/**
	 * Constructs a newly allocated RadioControlData object
	 */
	public RadioControlData() { 
		this.setInteriorDataType(ModuleType.RADIO);
	}

	/**
	 * Constructs a newly allocated RadioControlData object indicated by the Hashtable parameter
	 * @param hash The Hashtable to use
	 */      
	public RadioControlData(Hashtable<String, Object> hash) {
		super(hash);
		this.setInteriorDataType(ModuleType.RADIO);
	}

	/**
	 * The integer part of the frequency ie for 101.7 this value should be 101
	 * @return
	 */
	public Integer getFrequencyInteger(){
		return (Integer) store.get(KEY_FREQUENCY_INTEGER);
	}

	public void setFrequencyInteger(Integer freqInt){
		if (freqInt!=null) {
			store.put(KEY_FREQUENCY_INTEGER, freqInt);
		} else {
			store.remove(KEY_FREQUENCY_INTEGER);
		}
	}
	
	/**
	 * The fractional part of the frequency for 101.7 is 7
	 * @return
	 */
	public Integer getFrequencyFraction(){
		return (Integer) store.get(KEY_FREQUENCY_FRACTION);
	}

	public void setFrequencyFraction(Integer freqFrac){
		if (freqFrac!=null) {
			store.put(KEY_FREQUENCY_FRACTION, freqFrac);
		} else {
			store.remove(KEY_FREQUENCY_FRACTION);
		}
	}
	//TODO add a get/set frequency with float
	
	public RadioBand getBand(){
		 Object obj = store.get(KEY_BAND);
		 if (obj instanceof RadioBand) {
	            return (RadioBand) obj;
	        } else if (obj instanceof String) {
	        	RadioBand band = null;
	            try {
	                band = RadioBand.valueOf((String) obj);
	            } catch (Exception e) {
	                DebugTool.logError("Failed to parse " + getClass().getSimpleName() + "." + KEY_BAND, e);
	            }
	            return band;
	        }
		 return null;
	}

	public void setBand(RadioBand band){
		if (band!=null) {
			store.put(KEY_BAND, band);
		} else {
			store.remove(KEY_BAND);
		}
	}
	
	public RDSData getRDSData(){
		return (RDSData) store.get(KEY_RDS_DATA);
	}

	public void setRDSData(RDSData rdsData){
		if (rdsData!=null) {
			store.put(KEY_RDS_DATA, rdsData);
		} else {
			store.remove(KEY_RDS_DATA);
		}
	}
	
	/**
	 * Number of HD sub-channels if available
	 * @return
	 */
	public Integer getAvailableHDs(){
		return (Integer) store.get(KEY_AVAILABLE_HDS);
	}

	public void setAvailableHDs(Integer hds){
		if (hds!=null) {
			store.put(KEY_AVAILABLE_HDS, hds);
		} else {
			store.remove(KEY_AVAILABLE_HDS);
		}
	}
	
	/**
	 * Current HD sub-channel if available
	 * @return
	 */
	public Integer getHDChannel(){
		return (Integer) store.get(KEY_HD_CHANNEL);
	}

	public void setHDChannel(Integer channel){
		if (channel!=null) {
			store.put(KEY_HD_CHANNEL, channel);
		} else {
			store.remove(KEY_HD_CHANNEL);
		}
	}
	
	public Integer getSignalStrength(){
		return (Integer) store.get(KEY_SIGNAL_STRENGTH);
	}

	public void setSignalStrength(Integer signalStrength){
		if (signalStrength!=null) {
			store.put(KEY_SIGNAL_STRENGTH, signalStrength);
		} else {
			store.remove(KEY_SIGNAL_STRENGTH);
		}
	}
	
	/**
	 * If the signal strength falls below the set value for this parameter, the radio will tune to an alternative frequency
	 * @return
	 */
	public Integer getSignalChangeThreshold(){
		return (Integer) store.get(KEY_SIGNAL_CHANGE_THRESHOLD);
	}

	public void setSignalChangeThreshold(Integer signalChangeThreshold){
		if (signalChangeThreshold!=null) {
			store.put(KEY_SIGNAL_CHANGE_THRESHOLD, signalChangeThreshold);
		} else {
			store.remove(KEY_SIGNAL_CHANGE_THRESHOLD);
		}
	}
	
	/**
	 * True if the radio is on, false is the radio is off
	 * @return
	 */
	public Boolean getRadioEnabled(){
		return (Boolean) store.get(KEY_RADIO_ENABLED);
	}

	public void setRadioEnabled(Boolean enabled){
		if (enabled!=null) {
			store.put(KEY_RADIO_ENABLED, enabled);
		} else {
			store.remove(KEY_RADIO_ENABLED);
		}
	}
	
	public RadioState getRadioState(){
		 Object obj = store.get(KEY_STATE);
		 if (obj instanceof RadioState) {
	            return (RadioState) obj;
	        } else if (obj instanceof String) {
	        	RadioState state = null;
	            try {
	                state = RadioState.valueOf((String) obj);
	            } catch (Exception e) {
	                DebugTool.logError("Failed to parse " + getClass().getSimpleName() + "." + KEY_STATE, e);
	            }
	            return state;
	        }
		
		 return null;
	}

	public void setRadioState(RadioState radioState){
		if (radioState!=null) {
			store.put(KEY_STATE, radioState);
		} else {
			store.remove(KEY_STATE);
		}
	}
}
