package com.smartdevicelink.proxy.rc.datatypes;

import java.security.InvalidParameterException;
import java.util.Hashtable;

import com.smartdevicelink.proxy.RPCStruct;

/**
 * And Interior zone is defined as a sectioned off area of a vehicle. The dimensions should be set by the total number of seats
 * eg a typical car would have a 2 x 6 x 1 matrix (r x c x l)
 * @author Joey Grover
 *
 */
public class InteriorZone extends RPCStruct {
	
	public static final String KEY_COL 			= "col";
	public static final String KEY_ROW 			= "row";
	public static final String KEY_LEVEL 		= "level";
	public static final String KEY_COL_SPAN 	= "colspan";
	public static final String KEY_ROW_SPAN  	= "rowspan";
	public static final String KEY_LEVEL_SPAN  	= "levelspan";
	
	private static final int LOCATION = 0;
	private static final int SPAN = 1;

	/**
	 * Constructs a newly allocated InteriorZone object
	 */
	public InteriorZone() { }

/**
 * If values aren't set correctly constructor will an InvalidParameterException
 * @param colD a 2 item array [column, column span]
 * @param rowD a 2 item array [row, row span]
 * @param levelD a 2 item array [level, level span]
 */
	public InteriorZone(int[] rowD,int[] colD, int[] levelD) { 
		try{
			this.setRow(rowD[LOCATION]);
			this.setRowSpan(rowD[SPAN]);
			
			this.setColumn(colD[LOCATION]);
			this.setColumnSpan(colD[SPAN]);
			
			this.setLevel(levelD[LOCATION]);
			this.setLevelSpan(levelD[SPAN]);
			
		}catch(Exception e){
			throw new InvalidParameterException("Can't create object, values were invalid");
		}
	}

	/**
	 * Constructs a newly allocated InteriorZone object indicated by the Hashtable parameter
	 * @param hash The Hashtable to use
	 */      
	public InteriorZone(Hashtable<String, Object> hash) {
		super(hash);
	}
	
	
	public Integer getColumn(){
		return (Integer) store.get(KEY_COL);
	}

	public void setColumn(Integer col){
		if (col!=null) {
			store.put(KEY_COL, col);
		} else {
			store.remove(KEY_COL);
		}
	}
	
	public Integer getRow(){
		return (Integer) store.get(KEY_ROW);
	}

	public void setRow(Integer row){
		if (row!=null) {
			store.put(KEY_ROW, row);
		} else {
			store.remove(KEY_ROW);
		}
	}
	
	public Integer getLevel(){
		return (Integer) store.get(KEY_LEVEL);
	}

	public void setLevel(Integer level){
		if (level!=null) {
			store.put(KEY_LEVEL, level);
		} else {
			store.remove(KEY_LEVEL);
		}
	}
	
	public Integer getColumnSpan(){
		return (Integer) store.get(KEY_COL_SPAN);
	}

	public void setColumnSpan(Integer colSpan){
		if (colSpan!=null) {
			store.put(KEY_COL_SPAN, colSpan);
		} else {
			store.remove(KEY_COL_SPAN);
		}
	}
	
	public Integer getRowSpan(){
		return (Integer) store.get(KEY_ROW_SPAN);
	}

	public void setRowSpan(Integer rowSpan){
		if (rowSpan!=null) {
			store.put(KEY_ROW_SPAN, rowSpan);
		} else {
			store.remove(KEY_ROW_SPAN);
		}
	}
	
	public Integer getLevelSpan(){
		return (Integer) store.get(KEY_LEVEL_SPAN);
	}

	public void setLevelSpan(Integer levelSpan){
		if (levelSpan!=null) {
			store.put(KEY_LEVEL_SPAN, levelSpan);
		} else {
			store.remove(KEY_LEVEL_SPAN);
		}
	}
	
	/**
	 * This will check to see if the provided zone is contained within the zone calling this method
	 * @param zone
	 * @return
	 */
	public boolean isContainedWithin(InteriorZone zone){
		if(zone==null){
			return false;
		}
		
		int level = this.getLevel();
		int cLevel = zone.getLevel();
		if(level<= cLevel && cLevel <= (level + this.getLevelSpan())){
			//We are contained in the same level
			int row = this.getRow();
			int cRow = zone.getRow();
			if(row<= cRow && cRow <= (row + this.getRowSpan())){
				//We are within the row
				int col = this.getColumn();
				int cCol = zone.getColumn();
				if(col<= cCol && cCol <= (col + this.getColumnSpan())){
					//We are within the column!
					return true;
				}
			}
			
		}
		return false;
	}
	
	//TODO override to string
	
}
