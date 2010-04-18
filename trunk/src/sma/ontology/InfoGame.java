package sma.ontology;

import java.io.*;
import sma.gui.UtilsGUI;

/**
 * <p><B>Title:</b> IA2-SMA</p>
 * <p><b>Description:</b> Practical exercise 2009-10. Robocup Rescue.</p>
 * Information about the current game. This object is initialized from a file
 * and contains the number of agents of each kind, the duration of the game,
 * the duration of a turn, the basic information of each cell (type). When
 * the central and the coordinator agents initialize the agents and their 
 * positions, this object will contain more information, and finally it
 * contains the results of the events that take place (blocked streets, fires, etc.)
 * depending on the type of the cell.
 * <p><b>Copyright:</b> Copyright (c) 2009</p>
 * <p><b>Company:</b> Universitat Rovira i Virgili (<a
 * href="http://www.urv.cat">URV</a>)</p>
 * @author David Isern & Joan Albert López
 * @version 2.0
 * @see sma.CoordinatorAgent
 * @see sma.CentralAgent
 */
public class InfoGame implements java.io.Serializable {

  private long gameDuration;
  private long timeout;
  private double fireProbability;
  private double blockProbability;
  private int turn;
  private Cell[][] map;
  private int numAmbulances;
  private int numPolicemen;
  private int numFiremen;
  private int numParticularCars;

  static private boolean DEBUG = false;

  /**
   * Do not use it
   */
  public InfoGame() {
  }

  public long getGameDuration() { return this.gameDuration; }
  private void setGameDuration(long d) { this.gameDuration = d; }

  public int getNumRows() { return this.map.length; }
//  private void setNumRows(int n) { this.numRows = n; }

  public int getNumColumns() { return this.map[0].length; }
//  private void setNumColumns(int n) { this.numColumns = n; }

  public long getTimeout() { return this.timeout; }
  private void setTimeout(long n) { this.timeout = n; }

  public int getTurn() { return this.turn; }
  public void incrTurn() { this.turn++; }

  public Cell[][] getMap() { return this.map; }

  public Cell getCell(int x, int y) { return this.map[x][y]; }
  public void setCell(int x, int y, Cell c) { this.map[x][y] = c; }


  public double getFireProbability() {return fireProbability;}

  public void setFireProbability(double fireProbability) {this.fireProbability = fireProbability;}

  public double getBlockProbability() {return blockProbability;}

  public void setBlockProbability(double blockProbability) {this.blockProbability = blockProbability;}

  public boolean isEndGame() { return ((this.turn*this.timeout)>this.gameDuration); }

  public int getNumAmbulances() { return this.numAmbulances; }

  public int getNumFiremen() { return this.numFiremen; }

  public int getNumPolicemen() { return this.numPolicemen; }

  public int getNumParticularCars() { return this.numParticularCars; }
  
  private void showMessage(String s) {
    if(this.DEBUG)
      System.out.println(s);
  }

//  /**
//   * We write the string specified into a file.
//   * @param content String to write
//   * @param file Pathname of the file
//   * @return Nothing
//   */
//  private void writeFile(String content, File file) throws IOException {
//    StringBuffer sb = new StringBuffer(content);
//    PrintStream outFile = new PrintStream(new FileOutputStream(file));
//    for (int i = 0; i < content.length(); i++) {
//      outFile.print(sb.charAt(i));
//    }
//    //    System.out.println(content.length()+" characters write");
//  }


  public void writeGameResult(String fileOutput, Cell[][] t) throws IOException, Exception {
    File file= new File(fileOutput);
    String content = "" + this.getGameDuration()+"\n"+this.getTimeout()+"\n";
    for(int r=0; r<t.length; r++) {
      for(int c=0; c<t[0].length; c++) {
        Cell ca = t[r][c];
        content = content + Cell.getCellType(ca.getCellType());
        if(ca.getCellType()==Cell.BUILDING)
          content = content + ca.getNumPeople();
        content+="\t";
      }
      content+="\n";
    }
    UtilsGUI.writeFile(content,file);
    showMessage("File written");
  }


  public void readGameFile (String file) throws IOException,Exception {
	int idHospital = 0;
	this.numAmbulances=0;
	
    File f= new File(file);

    StreamTokenizer tokenizer = new StreamTokenizer(new FileReader(f));
    
    int NROWS = 0, NCOLS = 0;
    int line= 0;  // current line
    int ncol = 0, nrow = 0;
    for (int lexema= tokenizer.nextToken();
         lexema != StreamTokenizer.TT_EOF;
         lexema= tokenizer.nextToken()) {

      if (line != tokenizer.lineno()) {
    	  line = tokenizer.lineno();
        showMessage("[line " + line + "]");
        if(line>10) { nrow++; ncol=0; }
      }

      switch (lexema) {
        case StreamTokenizer.TT_NUMBER:
          showMessage("  numero: " + tokenizer.nval);
          if(line==1) this.setGameDuration((long)tokenizer.nval);
          if(line==2) this.setTimeout((long)tokenizer.nval);
          if(line==3) NROWS = (int)tokenizer.nval;
          if(line==4) {
            NCOLS = (int) tokenizer.nval;
            this.map = new Cell[NROWS][NCOLS];
          }
          if(line==5) { this.fireProbability = (double) tokenizer.nval; }
          if(line==6) { this.blockProbability = (double) tokenizer.nval;  }
          if(line==7) { this.numPolicemen = (int) tokenizer.nval; }
          if(line==8) { this.numFiremen = (int) tokenizer.nval;  }
          if(line==9) { this.numParticularCars = (int) tokenizer.nval; }
          break;
        case StreamTokenizer.TT_WORD:
          showMessage("  word: " + tokenizer.sval+" ");
          //possible cases: S, Bx, H i G
          String token = tokenizer.sval;
          if(token.equalsIgnoreCase("S")) {
            this.map[nrow][ncol] = new Cell(Cell.STREET);
          }
          if(token.startsWith("H")) {
        	//we must separate the H and the number of ambulances
        	this.map[nrow][ncol] = new Cell(Cell.HOSPITAL);
            String numA = token.substring(1,token.length());
            int num = Integer.parseInt(numA);
            ((Cell)map[nrow][ncol]).setNumAmbulances(num);  
            ((Cell)map[nrow][ncol]).setIdHospital(idHospital); idHospital++;
            numAmbulances+=num;
          }
          if(token.equalsIgnoreCase("G")) {
              this.map[nrow][ncol] = new Cell(Cell.GAS_STATION);
          }
          if(token.startsWith("B")) {
            //we must separate the B and the number of people
            this.map[nrow][ncol] = new Cell(Cell.BUILDING);
            String numP = token.substring(1,token.length());
            int num = Integer.parseInt(numP);
            ((Cell)map[nrow][ncol]).setNumPersones(num);
          }
          this.map[nrow][ncol].setRow(nrow);
          this.map[nrow][ncol].setColumn(ncol);
          showMessage( ((Cell)map[nrow][ncol]).toString() );
          ncol++;
          break;
        case '"': case '\'':
          showMessage("  string: " + tokenizer.sval);
          break;
        default:
          showMessage("  altres: " + (char)tokenizer.ttype);
          break;
      }
    } //endfor
  }




//  /**
//   * do not use it .... only for testing
//   * @param args String[]
//   */
//  public static void main(String[] args) {
//    InfoPartida p = new InfoPartida();
//
//    try {
//      p.llegirFitxerPartida("partida.txt");
//      p.escriureResultatPartida("resultat.txt",p.getTauler());
//      InterficieGrafica gui = new InterficieGrafica(p);
//      gui.setVisible(true);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }

} //endof class InfoPartida
