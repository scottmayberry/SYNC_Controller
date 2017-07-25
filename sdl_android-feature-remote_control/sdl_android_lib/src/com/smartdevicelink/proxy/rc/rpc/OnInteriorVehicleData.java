package com.smartdevicelink.proxy.rc.rpc;

import java.util.Hashtable;

import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.proxy.RPCNotification;
import com.smartdevicelink.proxy.rc.datatypes.ModuleData;
import com.smartdevicelink.proxy.rpc.enums.TriggerSource;
import com.smartdevicelink.util.DebugTool;

public class OnInteriorVehicleData extends RPCNotification {

	public static final String KEY_MODULE_DATA = "moduleData";

	/**
	*Constructs a newly allocated OnInteriorVehicleData object
	*/ 	
    public OnInteriorVehicleData() {
        super(FunctionID.ON_INTERIOR_VEHICLE_DATA.toString());
    }
    
    /**
    *<p>Constructs a newly allocated OnInteriorVehicleData object indicated by the Hashtable parameter</p>
    *@param hash The Hashtable to use
    */    
    public OnInteriorVehicleData(Hashtable<String, Object> hash) {
        super(hash);
    }
    
    	
	@SuppressWarnings("unchecked")
	public ModuleData getModuleData(){
		 Object obj = parameters.get(KEY_MODULE_DATA);
	        if (obj instanceof TriggerSource) {
	            return (ModuleData) obj;
	        } else if (obj instanceof Hashtable) {
	            return new ModuleData((Hashtable<String, Object>) obj);
	        }
	        return null;
	}

	public void setModuleData(ModuleData data){
		if (data!=null) {
			parameters.put(KEY_MODULE_DATA, data);
		} else {
			parameters.remove(KEY_MODULE_DATA);
		}
	}
    
}
