package sma;

import java.lang.*;
import java.io.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import jade.util.leap.ArrayList;
import jade.util.leap.List;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPANames.InteractionProtocol;
import jade.lang.acl.*;
import jade.content.*;
import jade.content.onto.*;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import sma.ontology.*;
import sma.gui.*;

/**
 * <p><B>Title:</b> IA2-SMA</p>
 * <p><b>Description:</b> Pràctica del curs 2009-10. Robocup Rescue.</p>
 * <p><b>Copyright:</b> Copyright (c) 2009</p>
 * <p><b>Company:</b> Universitat Rovira i Virgili (<a
 * href="http://www.urv.cat">URV</a>)</p>
 * @author not attributable
 * @version 2.0
 */
public class CoordinatorAgent extends Agent {

  private InfoGame game;

  private AID centralAgent;

  private java.util.List<Cell> agentList;


  public CoordinatorAgent() {
  }

  /**
   * A message is shown in the log area of the GUI
   * @param str String per mostrar
   */
  private void showMessage(String str) {
    System.out.println(getLocalName() + ": " + str);
  }


  /**
   * Agent setup method - called when it first come on-line. Configuration
   * of language to use, ontology and initialization of behaviours.
   */
  protected void setup() {

    /**** Very Important Line (VIL) *********/
    this.setEnabledO2ACommunication(true, 1);
    /****************************************/

    // Register the agent to the DF
    ServiceDescription sd1 = new ServiceDescription();
    sd1.setType(UtilsAgents.COORDINATOR_AGENT);
    sd1.setName(getLocalName());
    sd1.setOwnership(UtilsAgents.OWNER);
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.addServices(sd1);
    dfd.setName(getAID());
    try {
      DFService.register(this, dfd);
      showMessage("Registered to the DF");
    }
    catch (FIPAException e) {
      System.err.println(getLocalName() + " registration with DF " +
                         "unsucceeded. Reason: " + e.getMessage());
      doDelete();
    }


   /**************************************************/

   // add behaviours


   // we wait for the initialization of the game
    MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchProtocol(
        InteractionProtocol.FIPA_REQUEST),
        MessageTemplate.MatchOntology("serialized-object"));
    MessageTemplate mt2 = MessageTemplate.and(mt,
                                              MessageTemplate.
                                              MatchPerformative(ACLMessage.REQUEST));

   this.addBehaviour(new RequestResponseBehaviour(this, mt));

   // Setup finished. When the last inform is received, the agent itself will add
   // a behavious to send/receive actions

  } //endof setup


  /*************************************************************************/

  /**
   * <p><B>Title:</b> IA2-SMA</p>
   * <p><b>Description:</b> Practical exercise 2009-10. Robocup Rescue.</p>
   * Class that receives the REQUESTs from any agent. Concretely it is used 
   * at the beginning of the game. The Central Agent sends a REQUEST with all
   * the information of the game (instance of <code>InfoGame</code>), it processes
   * it and sends an AGREE, places the agents within the map, and finally it sends
   * an INFORM with the list of occupied cells (instance of <code>CellList</code>).
   * <p><b>Copyright:</b> Copyright (c) 2009</p>
   * <p><b>Company:</b> Universitat Rovira i Virgili (<a
   * href="http://www.urv.cat">URV</a>)</p>
   * @author David Isern
   * @version 1.0
   * @see sma.ontology.Cell
   * @see sma.ontology.InfoGame
   * @see sma.ontology.CellList
   */
  private class RequestResponseBehaviour extends AchieveREResponder {

    InfoGame partidaRebuda;

    /**
     * Constructor for the <code>RequestResponseBehaviour</code> class.
     * @param myAgent The agent owning this behaviour
     * @param mt Template to receive future responses in this conversation
     */
    public RequestResponseBehaviour(CoordinatorAgent myAgent, MessageTemplate mt) {
      super(myAgent, mt);
      showMessage("Waiting REQUESTs from authorized agents");
    }

    protected ACLMessage prepareResponse(ACLMessage msg) {

      /* method called when the message has been received. If the message to send
       * is an AGREE the behaviour will continue with the method prepareResultNotification. */

      ACLMessage reply = msg.createReply();
      //mirem el contingut del missatge
      try {
        Object contentRebut = (Object)msg.getContentObject();
        //showMessage(getLocalName()+" ha rebut un objecte: "+contentRebut.getClass());

        if(contentRebut instanceof sma.ontology.InfoGame) {
          showMessage("Info of the game received");
          reply.setPerformative(ACLMessage.AGREE);
          // when the configuration of the game has been received, we must create and place
          // all the agents. The firemen and policemen in a random cell, and the ambulances 
          // next to a hospital.
          // This can be done by the different coordinators or not.

          /** @todo change the manual assignation of the cars to a random assignation **/

          // the coordinator is supposed to send the problems and wait for the actions, 
          // therefore, it does not need to have memory, only the current conversation 
          // threads

          // it can keep a list of the occupied cells and monitorize all the actions.
          // Therefore, we can have lists of firemen, ambulances and policemen. Each Cell
          // will indicate where each element is placed.
          this.partidaRebuda = (InfoGame)contentRebut;
        }

      } catch (Exception e) {
        e.printStackTrace();
      }

      showMessage("Answer sent"); //: \n"+reply.toString());
      return reply;
    } //endof prepareResponse



    /**
     * All the ambulances must be placed next to a hospital, so they are not placed randomly.
     * @param map Cell[][] Current map
     * @return Cell where the ambulance can be placed
     * @see sma.ontology.Cell
     */
    private Cell placeNearHospitalCell(Cell[][] map, Cell hospitalCell) {
      int ihosp = hospitalCell.getRow(), jhosp = hospitalCell.getColumn();
      if (map[ihosp-1][jhosp-1].getCellType()==Cell.STREET && !map[ihosp-1][jhosp-1].isThereAVehicle()) return map[ihosp-1][jhosp-1];
      else if (map[ihosp-1][jhosp].getCellType()==Cell.STREET && !map[ihosp-1][jhosp].isThereAVehicle()) return map[ihosp-1][jhosp];
      else if (map[ihosp][jhosp-1].getCellType()==Cell.STREET && !map[ihosp][jhosp-1].isThereAVehicle()) return map[ihosp][jhosp-1];
      else if (map[ihosp+1][jhosp+1].getCellType()==Cell.STREET && !map[ihosp+1][jhosp+1].isThereAVehicle()) return map[ihosp+1][jhosp+1];
      else if (map[ihosp][jhosp+1].getCellType()==Cell.STREET && !map[ihosp][jhosp+1].isThereAVehicle()) return map[ihosp][jhosp+1];
      else if (map[ihosp+1][jhosp].getCellType()==Cell.STREET && !map[ihosp+1][jhosp].isThereAVehicle()) return map[ihosp+1][jhosp];
      else if (map[ihosp-1][jhosp+1].getCellType()==Cell.STREET && !map[ihosp-1][jhosp+1].isThereAVehicle()) return map[ihosp-1][jhosp+1];
      else if (map[ihosp+1][jhosp-1].getCellType()==Cell.STREET && !map[ihosp+1][jhosp-1].isThereAVehicle()) return map[ihosp+1][jhosp-1];
      else return null;
    } 


    private java.util.List<Cell> placeAgents(InfoGame currentGame) throws Exception {
      java.util.List<Cell> agents = new java.util.ArrayList<Cell>();

      int idHospital=0, numAmbulancesCell=0;
      for(int k=0; k<this.partidaRebuda.getNumAmbulances(); k++) {
    	  //System.out.println("amb");
    	  // Here we place all the ambulances
    	  Cell posHospital = null; 
          boolean found=false;
          while (!found){
        	  int i=0,j=0;
        	  while (i<this.partidaRebuda.getMap().length && !found) {
        		j=0;
    	        while(j<this.partidaRebuda.getMap()[0].length && !found) {
    	          if(this.partidaRebuda.getMap()[i][j].getCellType()==Cell.HOSPITAL && this.partidaRebuda.getMap()[i][j].getIdHospital()==idHospital) {
    	        	if (this.partidaRebuda.getMap()[i][j].getNumAmbulances()>numAmbulancesCell) {
    	        		posHospital=this.partidaRebuda.getMap()[i][j]; found=true; numAmbulancesCell++;
    	        	}else{
    	        		idHospital++; numAmbulancesCell=0;
    	        	}
    	          }
    	          j++;
    	        }
    	        i++;
    	      }
          }
          InfoAgent ambulance = new InfoAgent(InfoAgent.AMBULANCE);
          ambulance.setIdHospital(idHospital);
          Cell positionNHospital = placeNearHospitalCell(this.partidaRebuda.getMap(), posHospital);
    	  positionNHospital.addAgent(ambulance);
    	  agents.add(positionNHospital);
      }
     

      for(int k=0; k<this.partidaRebuda.getNumFiremen(); k++) {
        InfoAgent b = new InfoAgent(InfoAgent.FIREMAN);
        ((this.partidaRebuda.getMap())[3][k+1]).addAgent(b);
        agents.add( partidaRebuda.getCell(3,k+1));
      }

      for(int k=0; k<this.partidaRebuda.getNumPolicemen(); k++) {
    	  InfoAgent p = new InfoAgent(InfoAgent.POLICEMAN);
        ((this.partidaRebuda.getMap())[9][k+1]).addAgent(p);
        agents.add( partidaRebuda.getCell(9,k+1));
      }
      

      return agents;

    }

    /**
     * This method is called after the response has been sent and only when
     * one of the following two cases arise: the response was an agree message
     * OR no response message was sent. This default implementation return null
     * which has the effect of sending no result notification. Programmers
     * should override the method in case they need to react to this event.
     * @param msg ACLMessage the received message
     * @param response ACLMessage the previously sent response message
     * @return ACLMessage to be sent as a result notification (i.e. one of
     * inform, failure).
     */
    protected ACLMessage prepareResultNotification(ACLMessage msg,
                                                   ACLMessage response) {

      // it is important to make the createReply in order to keep the same context of
      // the conversation
      ACLMessage reply = msg.createReply();
      reply.setPerformative(ACLMessage.INFORM);

      /** @todo change the manual assignation of the agents to a random assignation.*/
      try {
        java.util.List<Cell> agents  = placeAgents(this.partidaRebuda);
        CellList listCaselles = new CellList();
        listCaselles.setCellList(agents);
        reply.setContentObject(listCaselles);
      } catch (Exception e) {
        reply.setPerformative(ACLMessage.FAILURE);
        System.err.println(e.toString());
        e.printStackTrace();
      }
      showMessage("Answer sent"); //+reply.toString());
      return reply;

    } //endof prepareResultNotification


    /**
     *  No need for any specific action to reset this behaviour
     */
    public void reset() {
    }

  } //end of RequestResponseBehaviour


  /*************************************************************************/


} //endof class AgentCentral
