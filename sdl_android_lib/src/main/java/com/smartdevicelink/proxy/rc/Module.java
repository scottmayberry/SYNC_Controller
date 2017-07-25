package com.smartdevicelink.proxy.rc;

import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rc.datatypes.ControlData;
import com.smartdevicelink.proxy.rc.datatypes.InteriorZone;
import com.smartdevicelink.proxy.rc.datatypes.ModuleData;
import com.smartdevicelink.proxy.rc.datatypes.ModuleDescription;
import com.smartdevicelink.proxy.rc.enums.ModuleType;
import com.smartdevicelink.proxy.rc.rpc.ButtonPress;
import com.smartdevicelink.proxy.rc.rpc.GetInteriorVehicleData;
import com.smartdevicelink.proxy.rc.rpc.SetInteriorVehicleData;
import com.smartdevicelink.proxy.rpc.enums.ButtonName;
import com.smartdevicelink.proxy.rpc.enums.ButtonPressMode;

public abstract class Module {

	protected ModuleType type;
	protected InteriorZone zone;
	protected ModuleListener listener;
	
	public Module(ModuleType type,InteriorZone zone ){
		this.type = type;
		this.zone = zone;
	}
	
	public Module(ModuleType type,InteriorZone zone,ModuleListener listener){
		this.type = type;
		this.zone = zone;
		this.listener = listener;
	}
	
	public void setModuleListener(ModuleListener listener){
		this.listener = listener;
	}
	
	public ModuleType getType() {
		return type;
	}

	public InteriorZone getZone() {
		return zone;
	}

	protected GetInteriorVehicleData buildGetInteriorVehicleDataRequest(boolean subscribe){
		GetInteriorVehicleData getData = new GetInteriorVehicleData();
		getData.setModuleDescription(new ModuleDescription(this.zone,this.type));
		getData.setSubscribed(subscribe);
		return getData;
	}
	
	public RPCRequest getStatus(boolean subscribe){
		return buildGetInteriorVehicleDataRequest(subscribe);
	}
	
	
	protected SetInteriorVehicleData buildSetInteriorVehicleDataRequest(ControlData data){
		SetInteriorVehicleData setData = new SetInteriorVehicleData();
		setData.setModuleData(getBaseModuleData(data));
		return setData;
	}
	
	public RPCRequest setStatus(ControlData data){
		return buildSetInteriorVehicleDataRequest(data);
	}
	
	
	
	/**
	 * Subscribe will send a message to the car to subscribe to that data and the response will 
	 * include the module data
	 * @param subscribe
	 * @return
	 */
	public RPCRequest subscribe(boolean subscribe){
		GetInteriorVehicleData data = buildGetInteriorVehicleDataRequest(subscribe);
		data.setSubscribed(subscribe);
		return data;
	}
	
	protected ButtonPress getButtonPress(ButtonName button, ButtonPressMode pressMode){
		ButtonPress buttonPress = new ButtonPress();
		buttonPress.setZone(zone);
		buttonPress.setModuleType(type);
		buttonPress.setButtonName(button);
		buttonPress.setButtonPressMode(pressMode);
		return buttonPress;
	}
	
	protected ModuleData getBaseModuleData(ControlData data){
		return new ModuleData(type, zone, data);
		
	}
	
	public interface ModuleListener{
		public void onUpdate(ControlData data);
	}
	
}
