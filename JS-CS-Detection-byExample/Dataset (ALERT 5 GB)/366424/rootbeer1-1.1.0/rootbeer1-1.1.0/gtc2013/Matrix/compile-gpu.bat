cd ../../
ant jar
pack-rootbeer.bat

cd gtc2013/Matrix/
ant jar
java -jar ../../Rootbeer.jar MatrixApp.jar MatrixApp-GPU.jar
