package com.smartdevicelink.proxy.rc.rpc;

import java.util.Hashtable;

import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rc.datatypes.InteriorZone;
import com.smartdevicelink.proxy.rc.enums.ModuleType;
import com.smartdevicelink.proxy.rpc.enums.ButtonName;
import com.smartdevicelink.proxy.rpc.enums.ButtonPressMode;

public class ButtonPress extends RPCRequest {

	
	public static final String KEY_ZONE					= "zone";
	public static final String KEY_MODULE_TYPE			= "moduleType";
	public static final String KEY_BUTTON_NAME 			= "buttonName";
	public static final String KEY_BUTTON_PRESS_MODE 	= "buttonPressMode";
	
	
	/**
	 * Constructs a newly allocated ButtonPress object
	 */
	public ButtonPress() { 
        super(FunctionID.BUTTON_PRESS.toString());
	}

    /**
    *<p>Constructs a newly allocated ButtonPress object indicated by the Hashtable parameter</p>
    *@param hash The Hashtable to use
    */    
    public ButtonPress(Hashtable<String, Object> hash) {
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
	
	public ModuleType getModuleType(){
		return (ModuleType) store.get(KEY_MODULE_TYPE);
	}

	public void setModuleType(ModuleType moduleType){
		if (moduleType!=null) {
			parameters.put(KEY_MODULE_TYPE, moduleType);
		} else {
			parameters.remove(KEY_MODULE_TYPE);
		}
	}
	
	public ButtonName getButtonName(){
		return (ButtonName) store.get(KEY_BUTTON_NAME);
	}

	public void setButtonName(ButtonName buttonName){
		if (buttonName!=null) {
			parameters.put(KEY_BUTTON_NAME, buttonName);
		} else {
			parameters.remove(KEY_BUTTON_NAME);
		}
	}
	
	public ButtonPressMode getButtonPressMode(){
		return (ButtonPressMode) store.get(KEY_BUTTON_PRESS_MODE);
	}

	public void setButtonPressMode(ButtonPressMode pressMode){
		if (pressMode!=null) {
			parameters.put(KEY_BUTTON_PRESS_MODE, pressMode);
		} else {
			parameters.remove(KEY_BUTTON_PRESS_MODE);
		}
	}
}
