package com.smartdevicelink.proxy.rc.rpc;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.proxy.RPCResponse;
import com.smartdevicelink.proxy.rc.datatypes.ModuleDescription;
import com.smartdevicelink.proxy.rpc.ButtonCapabilities;

public class GetInteriorVehicleDataCapabilitiesResponse extends RPCResponse {

	public static final String KEY_INTERIOR_VEHICLE_CAPABILITIES = "interiorVehicleDataCapabilities";

	
	/**
	*Constructs a newly allocated GetInteriorVehicleDataCapabilities object
	*/ 	
    public GetInteriorVehicleDataCapabilitiesResponse() {
        super(FunctionID.GET_INTERIOR_VEHICLE_DATA_CAPABILITIES.toString());
    }
    /**
    *<p>Constructs a newly allocated GetInteriorVehicleDataCapabilities object indicated by the Hashtable parameter</p>
    *@param hash The Hashtable to use
    */    
    public GetInteriorVehicleDataCapabilitiesResponse(Hashtable<String, Object> hash) {
        super(hash);
    }
    	
	@SuppressWarnings("unchecked")
	public List<ModuleDescription> getInteriorCapabilities(){
        if (parameters.get(KEY_INTERIOR_VEHICLE_CAPABILITIES) instanceof List<?>) {
        	List<?> list = (List<?>)parameters.get(KEY_INTERIOR_VEHICLE_CAPABILITIES);
        	if (list != null && list.size() > 0) {
        		Object obj = list.get(0);
        		if (obj instanceof ModuleDescription) {
                	return (List<ModuleDescription>) list;
        		} else if (obj instanceof Hashtable) {
	            	List<ModuleDescription> newList = new ArrayList<ModuleDescription>();
	                for (Object hashObj : list) {
	                    newList.add(new ModuleDescription((Hashtable<String, Object>)hashObj));
	                }
	                return newList;
	            }
        	}
        }
        return null;
	}

	public void setInteriorCapabilities(List<ModuleDescription> zoneCapabilities){
		if (zoneCapabilities!=null) {
			parameters.put(KEY_INTERIOR_VEHICLE_CAPABILITIES, zoneCapabilities);
		} else {
			parameters.remove(KEY_INTERIOR_VEHICLE_CAPABILITIES);
		}
	}  
    
}
