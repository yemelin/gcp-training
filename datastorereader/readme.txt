credentials:
create service account and save json to <credpath>

build:
mvn clean package shade:shade

run:
cd target
java -cp <jarfile> -Dcredpath=<credpath> com.vvy.dsreader.App
