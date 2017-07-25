package com.smartdevicelink.proxy.rc.datatypes;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import com.smartdevicelink.proxy.RPCStruct;
import com.smartdevicelink.proxy.rc.enums.ModuleType;
import com.smartdevicelink.proxy.rpc.enums.VehicleDataEventStatus;
import com.smartdevicelink.util.DebugTool;

public class ModuleDescription extends RPCStruct{

	public static final String KEY_ZONE			= "moduleZone";
	public static final String KEY_MODULE_TYPE	= "moduleType";
	
	
	/**
	 * Constructs a newly allocated InteriorZoneDescription object
	 */
	public ModuleDescription() { }

	/**
	 * Constructs a newly allocated InteriorZoneDescription object using the supplied params
	 */
	public ModuleDescription(InteriorZone zone,ModuleType moduleType) { 
		setZone(zone);
		setModuleType(moduleType); //TODO check this works
	}
	/**
	 * Constructs a newly allocated InteriorZoneDescription object indicated by the Hashtable parameter
	 * @param hash The Hashtable to use
	 */      
	public ModuleDescription(Hashtable<String, Object> hash) {
		super(hash);
	}
	
	
	public InteriorZone getZone(){
		 Object obj = store.get(KEY_ZONE);
		 if (obj instanceof InteriorZone) {
		 return (InteriorZone)obj;
		 }else if(obj instanceof Hashtable){
			 return new InteriorZone((Hashtable<String,Object>)obj);
		 }
		 return null;
	}

	public void setZone(InteriorZone zone){
		if (zone!=null) {
			store.put(KEY_ZONE, zone);
		} else {
			store.remove(KEY_ZONE);
		}
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

	public void setModuleType(ModuleType moduleType){
		if (moduleType!=null) {
			store.put(KEY_MODULE_TYPE, moduleType);
		} else {
			store.remove(KEY_MODULE_TYPE);
		}
	}
}
