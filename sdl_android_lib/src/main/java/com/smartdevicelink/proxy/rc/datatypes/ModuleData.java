package com.smartdevicelink.proxy.rc.datatypes;

import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

import com.smartdevicelink.proxy.RPCStruct;
import com.smartdevicelink.proxy.rc.enums.ModuleType;
import com.smartdevicelink.util.DebugTool;

/**
 * The interiorDataType indicates which type of data should be changed and identifies which data object exists in this struct. <p>For example, if the interiorDataType is CLIMATE then a "climateControlData" should exist
 * 
 * <P> The correct way to use this class is to check the data type first, and then use the corresponding method to find
 * @author Joey Grover
 *
 */
public class ModuleData extends RPCStruct {
	
	public static final String KEY_MODULE_TYPE		= "moduleType";
	public static final String KEY_MODULE_ZONE		= "moduleZone";
	public static final String KEY_RADIO_DATA		= "radioControlData";
	public static final String KEY_CLIMATE_DATA		= "climateControlData";
	
	/**
	 * Constructs a newly allocated InteriorData object
	 */
	public ModuleData() { }

	/**
	 * Constructs a newly allocated InteriorData object
	 */
	public ModuleData(ModuleType type, InteriorZone zone, ControlData data) { 
		this.setModuleType(type);
		this.setModuleZone(zone);
		this.setControlData(data);
	}

	/**
	 * Constructs a newly allocated InteriorZone object indicated by the Hashtable parameter
	 * @param hash The Hashtable to use
	 */      
	public ModuleData(Hashtable<String, Object> hash) {
		super(hash);
	}
	
	@SuppressWarnings("unchecked")
	public ModuleType getModuleType(){
		 Object obj = store.get(KEY_MODULE_TYPE);
		 if (obj instanceof ModuleType) {
	            return (ModuleType) obj;
	        } else if (obj instanceof String) {
	        	ModuleType type = null;
	            try {
	                type = ModuleType.valueOf((String) obj);
	            } catch (Exception e) {
	                DebugTool.logError("Failed to parse " + getClass().getSimpleName() + "." + KEY_MODULE_TYPE, e);
	            }
	            return type;
	        }
		
		 return null;
	}

	public void setModuleType(ModuleType type){
		if (type!=null) {
			store.put(KEY_MODULE_TYPE, type);
		} else {
			store.remove(KEY_MODULE_TYPE);
		}
	}
	
	public InteriorZone getZone(){
		 Object obj = store.get(KEY_MODULE_ZONE);
		 if (obj instanceof InteriorZone) {
		 return (InteriorZone)obj;
		 }else if(obj instanceof Hashtable){
			 return new InteriorZone((Hashtable<String,Object>)obj);
		 }
		 return null;
	}

	public void setModuleZone(InteriorZone zone){
		if (zone!=null) {
			store.put(KEY_MODULE_ZONE, zone);
		} else {
			store.remove(KEY_MODULE_ZONE);
		}
	}
	
	public ControlData getControlData(){
		ModuleType type = getModuleType();
		if(type== null){
			return null;
		}
		switch(type){
			case RADIO:
				return getRadioData();
			case CLIMATE:
				return getClimateData();
			//Anymore cases can be added here as we add more types
			default:
				return null;
		}
		
	}
	
	private RadioControlData getRadioData(){
		 Object obj = store.get(KEY_RADIO_DATA);
		if (obj instanceof RadioControlData) {
			return (RadioControlData)obj;
		}else if(obj instanceof Hashtable){
			return new RadioControlData((Hashtable<String,Object>)obj);
		}
		return null;
			
	}
	
	private ClimateControlData getClimateData(){
		 Object obj = store.get(KEY_CLIMATE_DATA);
		if (obj instanceof ClimateControlData) {
			return (ClimateControlData)obj;
		}else if(obj instanceof Hashtable){
			return new ClimateControlData((Hashtable<String,Object>)obj);
		}
		return null;
			
	}
	public void setControlData(ControlData data){
		
		ModuleType type = getModuleType();
		if(type== null){
			return;
		}
		switch(type){
			case RADIO:
				addToStore(KEY_RADIO_DATA, data); 
				break;
			case CLIMATE:
				addToStore(KEY_CLIMATE_DATA, data);
				break;
			//Anymore cases can be added here as we add more types
			default:
				return;
		}
	}
	
	private void addToStore(String key, ControlData data){
		if (data!=null) {
			store.put(key, data);
		} else {
			store.remove(key);
		}
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		Set<Entry<String, Object>> set = store.entrySet();
		for(Entry<String, Object> entry: set){
			builder.append(entry.getKey());
			builder.append(" - ");
			builder.append(entry.getValue());
			builder.append("\n");
		}
		return builder.toString();
		
	}
	
}
