# Amazon-Alexa-Custom-Skill

==================
BUILD INSTRUCTIONS
==================

For each of the registration, patient, web modules, the build instructions are as follows. We
explain later how to start those modules with the resulting jar files.

Go to source/registration folder. Execute 
mvn clean package

This will produce registration-0.0.1-SNAPSHOT.jar in target folder. 

Go to source/patient folder. Execute 
mvn clean package

This will produce patient-0.0.1-SNAPSHOT.jar in target folder. 

Go to source/web folder. Execute 
mvn clean package

This will produce web-0.0.1-SNAPSHOT.jar in target folder. 

For the patient monitor, build instructions are as follows.

Go to source/monitor folder. Execute 
mvn assembly:assembly -DdescriptorId=jar-with-dependencies package

This will produce monitor-0.0.1-SNAPSHOT-jar-with-dependencies.jar. It needs to be uploaded into
your Lambda function as the deployment package. When you configure the interaction model for the custom skill, 
use the IntentSchema.json and SampleUtterances.txt files under speechAssets folder. You can use the sample
utterances for testing purposes.

==========================================
RUNNING registration, patient, web MODULES
==========================================

+++++++++++++++++++++++++++++++++
Simple Configuration: Same Server
+++++++++++++++++++++++++++++++++

For testing purposes, the simplest configuration is to run the registration, patient and web 
modules in the same server. This way, during startup you can take advantage of the default host 
and port settings. First start registration module, followed by patient and then the web modules.
We explain those steps below.

Open a console and go to source/registration/target folder.
Execute java -jar registration-0.0.1-SNAPSHOT.jar

Wait until the service starts. When you see something like below, service will have started:
…
2135: INFO  RegistrationServer - Started RegistrationServer in 8.703 seconds (JVM running for 9.916)

Open another console and go to source/patient/target folder.
Execute java -jar patient-0.0.1-SNAPSHOT.jar

Wait until the service starts. When you see something like below, service will have started:
…
2210: INFO  PatientServer - Started PatientServer in 12.454 seconds (JVM running for 13.635)
2210: INFO  DiscoveryClient - DiscoveryClient_PATIENT-SERVICE/10.1.10.231:patient-service:2222 - registration status: 204

If you want to test load balancing and fault tolerance, you can start another instance of the patient service.
Because the default configuration is already using (default) port 2222, and in this simple configuration we are 
using the same server, we have to start any other patient instance at another port, e.g. 2223.

Open another console and go to source/patient/target folder.
Execute java -jar patient-0.0.1-SNAPSHOT.jar http://localhost:1111/eureka 2223

Wait until the second patient service starts. When you see something like below, service will have started:
…
2291: INFO  Http11NioProtocol - Starting ProtocolHandler ["http-nio-2223"]
2291: INFO  NioSelectorPool - Using a shared selector for servlet write/read
2291: INFO  PatientServer - Started PatientServer in 10.778 seconds (JVM running for 12.026)

Finally, to start the web service open a console and go to source/web/target folder.
Execute java -jar web-0.0.1-SNAPSHOT.jar

Wait until the service starts. When you see something like below, service will have started:
…
2419: INFO  WebServer - Started WebServer in 8.307 seconds (JVM running for 9.514)

At this point, you can run some tests. For example, open a console and run:
curl http://localhost:3333/patient/byname/doe

It should return

[{"id":1,"name":"John Doe","number":1002,"pulse":75,"temperature":98,"sysp":110,"diasp":70},
{"id":3,"name":"Jane Doe","number":1004,"pulse":75,"temperature":98,"sysp":130,"diasp":70}]

Example Read Operations
———————————————————————
Obtain vitals of one or more patients via name matching: http://localhost:3333/patient/byname/{name} e.g.
curl http://localhost:3333/patient/byname/doe

Obtain vitals of one particular patient via medical record number: http://localhost:3333/patient/bynumber/(mrn} e.g.
curl http://localhost:3333/patient/bynumber/1002

Obtain patients with abnormal vitals: http://localhost:3333/patient/abnormal e.g.
curl http://localhost:3333/patient/abnormal


Example Write Operation
————————————————————————
Set vitals of a particular patient:
http://localhost:3333/patient/setVitals/{mrn}/{diasp}/{sysp}/{pulse}/{temperature} e.g.
curl http://localhost:3333/patient/setVitals/1002/72/112/78/99 


Testing Fault Tolerance
———————————————————————
If you run multiple patient modules for fault tolerance, the web module could randomly choose either patient server while
making a REST call. After running the curl example above several times, shut down one of the patient services. Continue
with running curl once every couple of seconds. Initially you will get internal server error for some requests. After a
while, you will see that error will go away and curl will return correct response consistently.  

Note: While running multiple patient modules in parallel, because each module uses an imbedded H2 database, a value updated in one database may not be in sync with the other one. In a real application patient modules will be accessing the same database and therefore an abnormality like this will not occur. If you test the write operation please keep that in mind.

++++++++++++++++++++++++
Multi-node Configuration
++++++++++++++++++++++++

To emulate a more realistic situation, in the example below we will deploy each of the registration, patient and web modules
into a different server. For fault tolerance, we will two instances of the patient service in two different servers. Below
table gives the server IP addresses where the modules are deployed. Port number corresponds to Tomcat port of the particular 
module. 

Module		IP		Port
web		172.31.56.37	1112
patient		172.31.49.176	2221
patient		172.31.61.189	2221 (that could have been a different port than other patient service)
registration	172.31.60.250	3331

In 172.31.60.250, start registration module as follows.
java -jar registration-0.0.1-SNAPSHOT.jar localhost 1112

In 172.31.49.176, start patient module as follows.
java -jar patient-0.0.1-SNAPSHOT.jar http://172.31.60.250:1112/eureka 2221

In 172.31.61.189, start patient module as follows.
java -jar patient-0.0.1-SNAPSHOT.jar http://172.31.60.250:1112/eureka 2221

In 172.31.56.37, start web module as follows.
java -jar web-0.0.1-SNAPSHOT.jar http://172.31.60.250:1112/eureka 3331

