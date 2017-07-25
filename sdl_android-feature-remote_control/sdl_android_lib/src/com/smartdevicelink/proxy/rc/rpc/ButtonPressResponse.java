package com.smartdevicelink.proxy.rc.rpc;

import java.util.Hashtable;

import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.proxy.RPCResponse;

public class ButtonPressResponse extends RPCResponse {

	/**
	 * Constructs a newly allocated ButtonPressResponse object
	 */
	public ButtonPressResponse() { 
        super(FunctionID.BUTTON_PRESS.toString());
	}

    /**
    *<p>Constructs a newly allocated ButtonPressResponse object indicated by the Hashtable parameter</p>
    *@param hash The Hashtable to use
    */    
    public ButtonPressResponse(Hashtable<String, Object> hash) {
        super(hash);
    }
}
