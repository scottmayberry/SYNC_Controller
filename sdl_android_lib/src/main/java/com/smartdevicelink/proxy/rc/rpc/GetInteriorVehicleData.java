package com.smartdevicelink.proxy.rc.rpc;

import java.util.Hashtable;
import java.util.List;

import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rc.datatypes.ModuleDescription;

public class GetInteriorVehicleData extends RPCRequest {

	
	public static final String KEY_MODULE_DESCRIPTION 	= "moduleDescription";
	public static final String KEY_SUBSCRIBE 			= "subscribe";

	
	/**
	*Constructs a newly allocated GetInteriorVehicleData object
	*/ 	
    public GetInteriorVehicleData() {
        super(FunctionID.GET_INTERIOR_VEHICLE_DATA.toString());
    }
    /**
    *<p>Constructs a newly allocated GetInteriorVehicleData object indicated by the Hashtable parameter</p>
    *@param hash The Hashtable to use
    */    
    public GetInteriorVehicleData(Hashtable<String, Object> hash) {
        super(hash);
    }

	
	public ModuleDescription getModuleDescription(){
		return (ModuleDescription)parameters.get(KEY_MODULE_DESCRIPTION);
	}

	public void setModuleDescription(ModuleDescription moduleDescription){
		if (moduleDescription!=null) {
			parameters.put(KEY_MODULE_DESCRIPTION, moduleDescription);
		} else {
			parameters.remove(KEY_MODULE_DESCRIPTION);
		}
	}
	
	public Boolean getSubscribed(){
		return (Boolean) parameters.get(KEY_SUBSCRIBE);
	}

	public void setSubscribed(Boolean subscribe){
		if (subscribe!=null) {
			parameters.put(KEY_SUBSCRIBE, subscribe);
		} else {
			parameters.remove(KEY_SUBSCRIBE);
		}
	}
    
}
