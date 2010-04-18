package sma.ontology;

import java.util.*;
import java.io.Serializable;


/**
 * <p><B>Title:</b> IA2-SMA</p> *
 * <p><b>Description:</b> Practical exercise 2009-10. Robocup Rescue.
 * This class keeps all the information about a cell in the map.
 * <p><b>Copyright:</b> Copyright (c) 2009</p> *
 * <p><b>Company:</b> Universitat Rovira i Virgili (<a
 * href="http://www.urv.cat">URV</a>)</p>
 *
 * @author not attributable
 * @version 2.0
 */
public class Cell implements Serializable {

  static public int BUILDING = 1;
  static public int STREET = 2;
  static public int HOSPITAL = 3;
  static public int GAS_STATION = 4;

  private int type;

  private InfoAgent agent = null;
  private Integer car = null;
  //Depending on how you implement private cars, this variable may be an identifier of the car
  //or a pointer to a structure representing a car
  
  //only for buildings
  private int burned = 0;
  private int numPeople = 0;
  private int numInjuredPeople = 0;
  
  //only for streets
  private boolean blocked = false;
  
  //only for hospitals
  private int idHospital = -1; 
  private int numAmbulances = 0; //number of ambulances associated 

public Cell(int type) {
    this.type = type;
  }

  /**************************************************************************/

  public int getCellType() { return this.type; }
  public void setCellType(int newType) throws Exception {
    if((newType!=BUILDING) && (newType!=STREET) && (newType!=HOSPITAL) && (newType!=GAS_STATION))
      throw new Exception("Unknown type");
    this.type = newType;
  }
  static public String getCellType(int tipus) {
    if(tipus==BUILDING) return "B";
    if(tipus==STREET) return "S";
    if(tipus==HOSPITAL) return "H";
    if(tipus==GAS_STATION) return "G";
    return "";
  }

  /**************************************************************************/

  public int getNumPeople() throws Exception {
    if(this.getCellType()!=BUILDING) throw new Exception("Wrong operation");
    return this.numPeople; }
  public void setNumPersones(int n) throws Exception {
    if(this.getCellType()!=BUILDING) throw new Exception("Wrong operation");
    this.numPeople = n; }

  /**************************************************************************/

  public int getNumInjuredPeople() throws Exception {
    if(this.getCellType()!=BUILDING) throw new Exception("Wrong operation");
    return this.numInjuredPeople; }
  public void setNumPersonesFerides(int n) throws Exception {
    if(this.getCellType()!=BUILDING) throw new Exception("Wrong operation");
    if(n>this.numPeople) throw new Exception("More injured people than the total number of people");
    this.numInjuredPeople = n;
  }

  /**************************************************************************/

  public int getBurned() throws Exception {
    if(this.getCellType()!=BUILDING) throw new Exception("Wrong operation");
    return this.burned; }
  public void setBurned(int n) throws Exception {
    if(this.getCellType()!=BUILDING) throw new Exception("Wrong operation");
    this.burned = n; }

  /**************************************************************************/

  public boolean isBlocked() throws Exception {
    if(this.getCellType()!=STREET) throw new Exception("Wrong operation");
    return this.blocked;
  }
  public void setBlocked(boolean newValue) throws Exception {
    if(this.getCellType()!=STREET) throw new Exception("Wrong operation");
    this.blocked = newValue;
  }
  
  /**************************************************************************/

  public int getNumAmbulances() {
  	return numAmbulances;
  }

  public void setNumAmbulances(int numAmbulances) {
  	this.numAmbulances = numAmbulances;
  }
  
  /**************************************************************************/


  public int getIdHospital() {
  	return idHospital;
  }

  public void setIdHospital(int idHospital) {
  	this.idHospital = idHospital;
  }
  
  /**************************************************************************/


  public boolean isThereAnAgent() { 
	  return (agent!=null);
  }
  public boolean isThereACar() { 
	  return (car!=null);
  }
  public boolean isThereAVehicle() { 
	 return (isThereAnAgent() || isThereACar()); 
  }
  public void addAgent(InfoAgent newAgent) throws Exception {
    if((this.getCellType()==BUILDING) || (this.getCellType()==HOSPITAL) || (this.getCellType()==GAS_STATION))
      throw new Exception("Wrong operation");
    if((this.getCellType()==STREET) && (this.isThereAVehicle()))
      throw new Exception("Full STREET cell");
    if(this.isAgent(newAgent))
      throw new Exception("Repeated InfoAgent");

    // if everything is OK, we add the new agent to the cell
    this.agent=newAgent;
  }
  public void addCar(Integer i) throws Exception {
	    if((this.getCellType()==BUILDING) || (this.getCellType()==HOSPITAL) || (this.getCellType()==GAS_STATION))
	      throw new Exception("Wrong operation");
	    if((this.getCellType()==STREET) && (this.isThereAVehicle()))
	      throw new Exception("Full STREET cell");
	    if(this.isCar(i))
	        throw new Exception("Repeated car");
	    
	    // if everything is OK, we add the new car to the cell
	    this.car=i;  
  }
  private boolean isAgent(InfoAgent infoAgent) {
	if (infoAgent==null) return false;
	else{
		if (this.agent!=null) return this.agent.equals(infoAgent);
		else return false;	
	}	
  }
  private boolean isCar(Integer i) {
		if (i==null) return false;
		else{
			if (car!=null) return this.car.intValue()==i.intValue();
			else return false;	
		}	
  }
  public void removeAgent(InfoAgent oldInfoAgent) throws Exception {
    if((this.getCellType()==BUILDING) || (this.getCellType()==HOSPITAL) || (this.getCellType()==GAS_STATION))
      throw new Exception("Wrong operation");
    if(this.agent==null)
      throw new Exception("No agents in this cell");
    if(!this.isAgent(oldInfoAgent)) throw new Exception("InfoAgent not here");
    // if everything is OK, we remove the agent from the cell
    this.agent=null;
  }
  public void removeCar(Integer i) throws Exception {
	    if((this.getCellType()==BUILDING) || (this.getCellType()==HOSPITAL) || (this.getCellType()==GAS_STATION))
	      throw new Exception("Wrong operation");
	    if(this.car==null)
	        throw new Exception("No particular car in this cell");
	    if(!this.isCar(i)) throw new Exception("The car is not here");
	    
	    this.car=null;
  }
  
  public InfoAgent getAgent() { return this.agent; }
  public Integer getCar() { return this.car; }

  /**************************************************************************/
  private int row = -1;
  private int column = -1;

  public void setRow(int i) { this.row = i; }
  public int getRow() { return this.row; }

  public void setColumn(int i) { this.column = i; }
  public int getColumn() { return this.column; }
  /**************************************************************************/

  public String toString() {
    String str = "";
    try {
      str =
          "(cell-type " +
          this.getCellType(this.getCellType())+ " "+
          "(row " +
          this.getRow()+ ")"+
          "(column " +
          this.getColumn()+ ")";

      if (this.type == this.STREET) {
        str = str +
            "(blocked " + this.isBlocked() + ")";
            if (this.isThereAVehicle()) str=str+"(is-there-a-vehicle ";
            else str=str+"(empty ";
        if (agent!=null) str = str + this.agent.toString();
        else if (car!=null) str = str + this.car.toString();
        str = str + ")";
      }
      if (this.type == this.BUILDING) {
        str = str +
            "(burned " + this.getBurned() +
            ")(victims " + this.getNumInjuredPeople() +
            ")(people " + this.getNumPeople() + ")";
      }
      str = str + ")";
    } catch (Exception e) {
      e.printStackTrace();
    }
    return str;
  }



} //endof class Cell
