BASEPATH=$(pwd)
echo $BASEPATH
export CLASSPATH="bin/:$BASEPATH/lib/Base64.jar:$BASEPATH/lib/iiop.jar:$BASEPATH/lib/jade.jar:$BASEPATH/lib/jadeTools.jar:$BASEPATH/lib/commons-codec-1.3.jar"
#CLASSPATH="./lib/jade.jar:./lib/jadeTools.jar:./lib/commons-codec-1.3.jar"
#echo $CLASSPATH
java -Xmx400m -Xms400m jade.Boot -nomtp central:sma.CentralAgent coord:s/ma.CoordinatorAgent
#java -cp "lib/jade.jar:bin/" -Xmx400m -Xms400m jade.Boot -nomtp central:sma.CentralAgent coord:s/ma.CoordinatorAgent
