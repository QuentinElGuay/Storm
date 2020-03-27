## Page visit counter
Simple Apache Storm project based on the lecture ["Gérez des flux de données temps réel"](https://openclassrooms.com/fr/courses/4451251-gerez-des-flux-de-donnees-temps-reel) (Manager real time datafows) by [Régis Behmo](https://openclassrooms.com/fr/membres/regisb-1) on **OpenClassrooms**. I do not own this code. If you re-use and publish it, please share the author too.

### Run the project
Requisits:
- Java JDK (here OpenJDK-8)
- Apache Maven
- Apache Storm
- Apache ZooKeeper

Package the project with Maven:

```bash
cd /home/workspace/urlVisits
mvn package
```
More useful Maven commands (not required to run the project):

```bash
# If you want to create an Eclipse project
mvn eclipse:eclipse

# To generate a new Maven project (select all default options and choose an artifactId and groupId.
# Do not forget to add dependencies to storm-core and storm-server in the pom.xml (example in this repository).
mvn archetype:generate
```


Configure Storm to run locally:
- open  _/home/apache-storm-2.1.0/conf/storm.yaml_.
- uncomment and change the following lines:

```
storm.zookeeper.servers:
     - "localhost"
nimbus.seeds: ["localhost"]
```

Run ZooKeeper in a terminal tab:

```bash
./home/zookeeper/bin/zkServer.sh start-foreground
```

In another tab, run Nimbus:

```bash
./home/kafka_2.13-2.4.1/bin/storm nimbus
```

In another tab, run the supervisor:

```bash
./home/kafka_2.13-2.4.1/bin/storm supervisor
```

Finally, in another tab execute your topology: 

```bash
./home/kafka_2.13-2.4.1/bin/storm jar /home/workspace/urlVisits/target/urlVisits-1.0-SNAPSHOT.jar urlVisits.App remote

```

Please note that the  _remote_  argument is used to run on the cluster. If you remove it, the topology will run locally. [There are other ways](https://storm.apache.org/releases/current/Local-mode.html) of doing it like running:

```bash
storm local /home/workspace/urlVisits/target/urlVisits-1.0-SNAPSHOT.jar urlVisits.App
```

To access Storm UI:

```bash
./home/kafka_2.13-2.4.1/bin/storm ui
```

Then access [http://localhost:8080/](http://localhost:8080/).

