package com.smartdevicelink.proxy.rc.datatypes;

import java.util.Hashtable;

import com.smartdevicelink.proxy.RPCStruct;
import com.smartdevicelink.proxy.rc.enums.ModuleType;

public class ControlData extends RPCStruct{

	private ModuleType interiorDataType = null;

	public ControlData(){ }
	
	public ControlData(Hashtable<String, Object> hash) {
		super(hash);
	}
	
	
	public ModuleType getInteriorDataType() {
		return interiorDataType;
	}

	public void setInteriorDataType(ModuleType interiorDataType) {
		this.interiorDataType = interiorDataType;
	}
	
}
