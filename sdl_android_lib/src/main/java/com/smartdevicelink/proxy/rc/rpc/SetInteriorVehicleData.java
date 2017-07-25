package com.smartdevicelink.proxy.rc.rpc;

import java.util.Hashtable;

import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rc.datatypes.ModuleData;

public class SetInteriorVehicleData extends RPCRequest {

	public static final String KEY_MODULE_DATA = "moduleData";

	/**
	*Constructs a newly allocated SetInteriorVehicleData object
	*/ 	
    public SetInteriorVehicleData() {
        super(FunctionID.SET_INTERIOR_VEHICLE_DATA.toString());
    }
    
	/**
	*Constructs a newly allocated SetInteriorVehicleData object
	*@param moduleData The instance of module data to be sent
	*/ 	
    public SetInteriorVehicleData(ModuleData moduleData) {
        super(FunctionID.SET_INTERIOR_VEHICLE_DATA.toString());
        this.setModuleData(moduleData);
    }
    /**
    /**
    *<p>Constructs a newly allocated SetInteriorVehicleData object indicated by the Hashtable parameter</p>
    *@param hash The Hashtable to use
    */    
    public SetInteriorVehicleData(Hashtable<String, Object> hash) {
        super(hash);
    }
    
	public ModuleData getModuleData(){
		return (ModuleData) store.get(KEY_MODULE_DATA);
	}

	public void setModuleData(ModuleData moduleData){
		if (moduleData!=null) {
			parameters.put(KEY_MODULE_DATA, moduleData);
		} else {
			parameters.remove(KEY_MODULE_DATA);
		}
	}
	
}
