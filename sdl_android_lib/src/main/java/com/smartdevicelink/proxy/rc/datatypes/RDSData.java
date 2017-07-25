package com.smartdevicelink.proxy.rc.datatypes;

import java.util.Hashtable;

import com.smartdevicelink.proxy.RPCStruct;

public class RDSData extends RPCStruct {

	public static final String KEY_PS 	= "PS";
	public static final String KEY_RT 	= "RT";
	public static final String KEY_CT 	= "CT";
	public static final String KEY_PI 	= "PI";
	public static final String KEY_PTY 	= "PTY";
	public static final String KEY_TP 	= "TP";
	public static final String KEY_TA 	= "TA";
	public static final String KEY_REG	 = "REG";

	/**
	 * Constructs a newly allocated RDSData object
	 */
	public RDSData() { }

	/**
	 * Constructs a newly allocated RDSData object indicated by the Hashtable parameter
	 * @param hash The Hashtable to use
	 */      
	public RDSData(Hashtable<String, Object> hash) {
		super(hash);
	}

	/**
	 * Program Service Name
	 * @return
	 */
	public String getPS(){
		return (String) store.get(KEY_PS);
	}

	public void setPS(String ps){
		if (ps != null) {
			store.put(KEY_PS, ps);
		} else {
			store.remove(KEY_PS);
		}
	}

	/**
	 * Radio Text
	 * @return
	 */
	public String getRT(){
		return (String) store.get(KEY_RT);
	}

	public void setRT(String rt){
		if (rt != null) {
			store.put(KEY_RT, rt);
		} else {
			store.remove(KEY_RT);
		}
	}

	/**
	 * The clock text in UTC format as YYYY-MM-DDThh:mm:ss.sTZD
	 * @return
	 */
	public String getCT(){
		return (String) store.get(KEY_CT);
	}

	public void setCT(String ct){
		if (ct!=null) {
			store.put(KEY_CT, ct);
		} else {
			store.remove(KEY_CT);
		}
	}

	/**
	 * Program Identification - the call sign for the radio station
	 * @return
	 */
	public String getPI(){
		return (String) store.get(KEY_PI);
	}

	public void setPI(String pi){
		if (pi!=null) {
			store.put(KEY_PI, pi);
		} else {
			store.remove(KEY_PI);
		}
	}

	/**
	 * The program type - The region should be used to differentiate between EU and North America program types 
	 * @return
	 */
	public Integer getPTY(){
		return (Integer) store.get(KEY_PTY);
	}

	public void setPTY(Integer pty){
		if (pty!=null) {
			store.put(KEY_PTY, pty);
		} else {
			store.remove(KEY_PTY);
		}
	}

	/**
	 * Traffic Program Identification - Identifies a station that offers traffic
	 * @return
	 */
	public Boolean getTP(){
		return (Boolean) store.get(KEY_TP);
	}

	public void setTP(Boolean tp){
		if (tp!=null) {
			store.put(KEY_TP, tp);
		} else {
			store.remove(KEY_TP);
		}
	}
	
	/**
	 * Traffic Announcement Identification - Indicates an ongoing traffic announcement
	 * @return
	 */
	public Boolean getTA(){
		return (Boolean) store.get(KEY_TA);
	}

	public void setTA(Boolean ta){
		if (ta!=null) {
			store.put(KEY_TA, ta);
		} else {
			store.remove(KEY_TA);
		}
	}

	/**
	 * Region
	 * @return
	 */
	public String getREG(){
		return (String) store.get(KEY_REG);
	}

	public void setREG(String region){
		if (region!=null) {
			store.put(KEY_REG, region);
		} else {
			store.remove(KEY_REG);
		}
	}

}
