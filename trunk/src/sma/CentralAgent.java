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
import java.util.*;

/**
 * <p><B>Title:</b> IA2-SMA</p>
 * <p><b>Description:</b> Practical exercise 2009-10. Robocup Rescue.</p>
 * <p><b>Copyright:</b> Copyright (c) 2009</p>
 * <p><b>Company:</b> Universitat Rovira i Virgili (<a
 * href="http://www.urv.cat">URV</a>)</p>
 * @author not attributable
 * @version 2.0
 */
public class CentralAgent extends Agent {

  private sma.gui.GraphicInterface gui;
  private sma.ontology.InfoGame game;

  private AID coordinatorAgent;

  private HashMap particularCars; 
  //Containg {identifier (Integer), cell (Cell)} to know the position of particular cars

  public CentralAgent() {
    super();
  }

  /**
   * Es mostra un missatge a l'àrea de logs de la GUI
   * @param str String per mostrar
   */
  private void showMessage(String str) {
    if (gui!=null) gui.showLog(str + "\n");
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


//    showMessage("Agent (" + getLocalName() + ") .... [OK]");

    // Register the agent to the DF
    ServiceDescription sd1 = new ServiceDescription();
    sd1.setType(UtilsAgents.CENTRAL_AGENT);
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

    try {
      this.game = new InfoGame(); //serà l'bjecte amb les dades de la partida
      this.game.readGameFile("game.txt");
      //game.writeGameResult("result.txt", game.getMap());
    } catch(Exception e) {
      e.printStackTrace();
      System.err.println("Game NOT loaded ... [KO]");
    }
    try {
      this.gui = new GraphicInterface(game);
      gui.setVisible(true);
      showMessage("Game loaded ... [OK]");
    } catch (Exception e) {
      e.printStackTrace();
    }


   /**************************************************/

   //afegir behaviours

   // busco AgentCoordinador
   ServiceDescription searchCriterion = new ServiceDescription();
   searchCriterion.setType(UtilsAgents.COORDINATOR_AGENT);
   this.coordinatorAgent = UtilsAgents.searchAgent(this, searchCriterion);
   // searchAgent is a blocking method, so we will obtain always a correct AID

   // we compose a message for the coordinator with the information of the game
   ACLMessage requestInicial = new ACLMessage(ACLMessage.REQUEST);
   requestInicial.clearAllReceiver();
   requestInicial.addReceiver(this.coordinatorAgent);
   requestInicial.setOntology(UtilsAgents.ONTOLOGY);
   requestInicial.setLanguage(UtilsAgents.LANGUAGE);
   requestInicial.setProtocol(InteractionProtocol.FIPA_REQUEST);
   try {
     requestInicial.setContentObject(game);
   } catch (Exception e) {
     e.printStackTrace();
   }

   //we add a behaviour that sends the message and waits for an answer
   this.addBehaviour(new RequesterBehaviour(this, requestInicial));

   // setup finished. When we receive the last inform, the agent itself will add
   // a behaviour to send/receive actions

  } //endof setup


  void initializeInfoAgents(Cell[] cells) {
    // actualitzacio de partida.tauler
    for(int k=0; k<cells.length; k++) {
      this.game.setCell(cells[k].getRow(), cells[k].getColumn(), cells[k]);
    }
  }

  /*************************************************************************/

  /**
    * <p><B>Title:</b> IA2-SMA</p>
    * <p><b>Description:</b> Practical exercise 2009-10. Robocup Rescue.</p>
    * This class lets send the REQUESTs for any agent. Concretely it is used in the 
    * initialization of the game. The Central Agent sends a REQUEST with the information
    * of the game (instance of <code>InfoGame</code>), this is processed by the coordinator
    * which sends an AGREE, places the agents within the map, and finally sends an iNFORM 
    * with the list of occupied cells (instance of <code>CellList</code>). The Central Agent
    * must process all this information and place within the map all the agents, place all
    * the particular cars (it decides how), visualize it in the GUI and initiate the game
    * with turn number 1. The game is processed by another behaviour that we add after the
    * INFORM has been processed.
    * <p><b>Copyright:</b> Copyright (c) 2009</p>
    * <p><b>Company:</b> Universitat Rovira i Virgili (<a
    * href="http://www.urv.cat">URV</a>)</p>
    * @author David Isern
    * @version 1.0
    * @see sma.ontology.Cell
    * @see sma.ontology.InfoGame
    * @see sma.ontology.CellList
   */
  class RequesterBehaviour extends AchieveREInitiator {

    private ACLMessage msgSent = null;
    private boolean finish = false;

    public RequesterBehaviour(Agent myAgent, ACLMessage requestMsg) {
      super(myAgent, requestMsg);
      showMessage("AchieveREInitiator starts...");
      msgSent = requestMsg;
    }

    /**
     * Handle AGREE messages
     * @param msg Message to handle
     */
    protected void handleAgree(ACLMessage msg) {
      showMessage("AGREE received from "+ ( (AID)msg.getSender()).getLocalName());
    }

    /**
     * Handle INFORM messages
     * @param msg Message
     */
    protected void handleInform(ACLMessage msg) {
      showMessage("INFORM received from "+ ( (AID)msg.getSender()).getLocalName()+" ... [OK]");
      try {
        Object content = msg.getContentObject();
        if (content instanceof CellList) {
          showMessage("collection of cells with agents has been received: ");
          CellList listCaselles = (CellList)content;
          Cell[] elements = listCaselles.getCellList();
          for(int k=0; k<listCaselles.getCellList().length; k++) {
            showMessage(k+": "+elements[k].toString() );
          }
       
          //Place the cars
          particularCars = new HashMap();
          showMessage("initial positions of the particular cars: ");
          Cell[] cotxes = new Cell[((CentralAgent)myAgent).game.getNumParticularCars()];
          for(int k=0; k<cotxes.length; k++) {
        	cotxes[k]=game.getCell(k, 0);
        	cotxes[k].addCar(new Integer(k));
        	particularCars.put(new Integer(k), cotxes[k]);
          }
          /** @todo change the manual assignation of the cars to a random assignation */
          for(int k=0; k<cotxes.length; k++) {
              showMessage(k+": "+cotxes[k].toString() );
          }
          
          // now we can place them in the "map" and show them in the GUI
          initializeInfoAgents(elements);
          initializeInfoAgents(cotxes);
          
          /**@todo Add a new behaviour which initiates the turns of the game */

        }
      } catch (Exception e) {
        showMessage("Incorrect content: "+e.toString());
      }
    }

    /**
     * Handle NOT-UNDERSTOOD messages
     * @param msg Message
     */
    protected void handleNotUnderstood(ACLMessage msg) {
      showMessage("This message NOT UNDERSTOOD. \n");
    }

    /**
     * Handle FAILURE messages
     * @param msg Message
     */
    protected void handleFailure(ACLMessage msg) {
      showMessage("The action has failed.");

    } //End of handleFailure

    /**
     * Handle REFUSE messages
     * @param msg Message
     */
    protected void handleRefuse(ACLMessage msg) {
      showMessage("Action refused.");
    }
  } //Endof class RequesterBehaviour


  /*************************************************************************/


} //endof class AgentCentral
