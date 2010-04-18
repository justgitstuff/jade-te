
To execute the practical exercise with the Central Agent and the Coordinator Agent use PRAC.BAT. To start JADE with its gui, in order to manually introduce the agents, use PRAC2.BAT.

These agents initialize the information of the simulation and the Central Agent send this information to the Coordinator Agent. This is the first step of the process.

(each team will have to change the folders according to their configuration, or will have to define an execution profile if they use a tool as Eclipse)

The Central Agent reads the configuration of the game. This information is obatined from "game.txt". The format is the following:

60000 //game_duration_miliseconds
1000  //time_to_think_miliseconds
10    //rows
10    //columns
0.2  //probability of generation of a fire
0.3  //probability of generation of a block
3     //number of police cars
3     //number of fire trucks
2     //number of private cars
S B0 B0 S B0 B0 S S S S
S B0 B0 S B0 B0 S S B8 B9
S B0 B0 S B0 B0 S S S S
S S S S S S S S S S
S B0 S B0 S S S H2 G S
S B0 S B0 S S S S S S
S B0 B0 B0 S B0 B0 B0 B0 B0
S S S S S S S S S S
S H3 B0 B0 B0 B0 B0 B0 S B0
S S S S S S S S S S


The first 9 lines determine the configuration of the simulation, while the next ones determine the topology of the city.
Within the city, we may have streets (S), gas stations (G), buildings with an initial number of persons (Bx) and hospitals with a number of ambulances (Hy).

The class sma.InfoGame reads this kind of files. We also have methods to write the information of the city, in order to compare the initial state and the final state.



