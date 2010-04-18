package sma.ontology;

import jade.core.AID;

/**
 * <p><B>Title:</b> IA2-SMA</p>
 * <p><b>Description:</b> Practical exercise 2009-10. Robocup Rescue.</p>
 * <p><b>Copyright:</b> Copyright (c) 2009</p>
 * <p><b>Company:</b> Universitat Rovira i Virgili (<a
 * href="http://www.urv.cat">URV</a>)</p>
 * @author David Isern & Joan Albert López
 * @version 2.0
 */
public class InfoAgent extends Object implements java.io.Serializable {

  static public int FIREMAN = 0;
  static public int AMBULANCE = 1;
  static public int POLICEMAN = 2;

  private int typeAgent = -1;
  private AID aid;
  
  private int gasoline = 100;
  
  //only for ambulances
  private int idHospital = -1;
 
  public int getGasoline() {
	return gasoline;
}

public void setGasoline(int gasoline) {
	this.gasoline = gasoline;
}

public int getIdHospital() {
	return idHospital;
}

public void setIdHospital(int idHospital) {
	this.idHospital = idHospital;
}

public boolean equals(InfoAgent a) {
	return a.getAID().equals(this.aid);
  }

  public AID getAID() { return this.aid; }
  public void setAID(AID aid) { this.aid = aid; }

  public int getAgentType() { return this.typeAgent; }
  public void setAgentType(int type) throws Exception {
    if((type!=FIREMAN) && (type!=AMBULANCE) && (type!=POLICEMAN)) throw new Exception("Unkown type");
    this.typeAgent = type;
  }

  public String getAgentType(int type) throws Exception {
    if(type==FIREMAN) return "F";
    if(type==AMBULANCE) return "A";
    if(type==POLICEMAN) return "P";
    throw new Exception("Unkown type");
  }

  public String toString() {
    String str = "";
    try {
      str = "(info-agent (agent-type "+
          this.getAgentType(this.getAgentType()) +")"+
          "(aid "+ this.getAID().getLocalName()+")"+
          "(gasoline " + this.getGasoline() + ")";
      	if (this.getAgentType()==InfoAgent.AMBULANCE) str += "(belongs to hospital " + this.getIdHospital() + ")";
      	else str += ")";
      return str;
    } catch (Exception e) {
//      System.err.println("InfoAgent Error: "+e.toString());
      try {
        str = "(info-agent (agent-type " +
            this.getAgentType(this.getAgentType()) + ")" +
            "(aid <empty>))"+
            "(gasoline " + this.getGasoline() + ")";
            if (this.getAgentType()==InfoAgent.AMBULANCE) str += "(belongs to hospital " + this.getIdHospital() + ")";
          	else str += ")";
        return str;
      } catch (Exception e2) {
        str = "(info-agent (agent-type <empty>) (aid <empty>))"+
        "(gasoline " + this.getGasoline() + ")" ;
        if (this.getAgentType()==InfoAgent.AMBULANCE) str += "(belongs to hospital " + this.getIdHospital() + ")";
      	else str += ")";
        return str;
      }
    }
  }



  /**
   * Default constructor
   * @param agentType int Type of the agent we want to save (FIREMAN, AMBULANCE or POLICEMAN)
   * @param aid AID Agent identifier
   * @throws Exception Errors in the assignation
   */
  public InfoAgent(int agentType, AID aid) throws Exception {
    this.setAgentType(agentType);
    this.setAID(aid);
  }


  /**
   * Constructor for the information of the agent, without its AID
   * @param agentType int (ex. InfoAgent.AMBULANCE)
   * @throws Exception Errors in the assignation
   * @see InfoAgent#AMBULANCIA
   */
  public InfoAgent(int agentType) throws Exception {
    this.setAgentType(agentType);
  }


} //endof class InfoAgent
