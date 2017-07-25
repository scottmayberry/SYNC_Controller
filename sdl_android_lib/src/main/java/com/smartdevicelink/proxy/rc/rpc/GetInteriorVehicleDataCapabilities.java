package com.smartdevicelink.proxy.rc.rpc;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rc.datatypes.InteriorZone;
import com.smartdevicelink.proxy.rc.enums.ModuleType;

/**
 * Called to retrieve the available zones and supported control types
 * @author Joey Grover
 *
 */
public class GetInteriorVehicleDataCapabilities extends RPCRequest {
	
	public static final String KEY_ZONE			= "zone";
	public static final String KEY_MODULE_TYPES	= "moduleTypes";
	
	
	
	/**
	*Constructs a newly allocated GetInteriorVehicleDataCapabilities object
	*/ 	
    public GetInteriorVehicleDataCapabilities() {
        super(FunctionID.GET_INTERIOR_VEHICLE_DATA_CAPABILITIES.toString());
    }
    
	/**
	 * Constructs a newly allocated GetInteriorVehicleDataCapabilities object using the supplied params
	 */
	public GetInteriorVehicleDataCapabilities(InteriorZone zone,ModuleType dataType) {
		super(FunctionID.GET_INTERIOR_VEHICLE_DATA_CAPABILITIES.toString());
		setZone(zone);
		setDataTypes(Arrays.asList(dataType)); //TODO check this works
	}
	
	/**
	 * Constructs a newly allocated GetInteriorVehicleDataCapabilities object using the supplied params
	 */
	public GetInteriorVehicleDataCapabilities(InteriorZone zone,List<ModuleType> dataTypes) {
		super(FunctionID.GET_INTERIOR_VEHICLE_DATA_CAPABILITIES.toString());
		setZone(zone);
		setDataTypes(dataTypes);
	}
	
    /**
    *<p>Constructs a newly allocated GetInteriorVehicleDataCapabilities object indicated by the Hashtable parameter</p>
    *@param hash The Hashtable to use
    */    
    public GetInteriorVehicleDataCapabilities(Hashtable<String, Object> hash) {
        super(hash);
    }
    
	public InteriorZone getZone(){
		return (InteriorZone) parameters.get(KEY_ZONE);
	}

	public void setZone(InteriorZone zone){
		if (zone!=null) {
			parameters.put(KEY_ZONE, zone);
		} else {
			parameters.remove(KEY_ZONE);
		}
	}
	
	//TODO make sure this is still how we are doing it as an array
	
	@SuppressWarnings("unchecked")
	public List<ModuleType> getDataTypes(){
        if (parameters.get(KEY_MODULE_TYPES) instanceof List<?>) {
        	List<?> list = (List<?>)parameters.get( KEY_MODULE_TYPES);
        	if (list != null && list.size() > 0) {
        		Object obj = list.get(0);
        		if (obj instanceof ModuleType) {
                	return (List<ModuleType>) list;
        		}//FIXME
        	}
        }
        return null;
	}

	public void setDataTypes(List<ModuleType> dataTypes){
		if (dataTypes!=null) {
			parameters.put(KEY_MODULE_TYPES, dataTypes);
		} else {
			parameters.remove(KEY_MODULE_TYPES);
		}
	}
}
