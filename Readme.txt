
What is Kafka?

Let us say there is a multipe Software systems withing an organization which hold different types of data.
In addition there may also be some third party systems , an organization would rely on for some kind of data.

Practically when one system or application requires data sourced by another system or application, we have to build and integration interface
between the two application inorder to fetch data. The integration layer handles following aspects
1. Security
2. Data format in which the data is exchanged such as JSON, CSV, XML etc
3. Communication Protocol such as HTTP, FTP, SFTP, TCP etc.


Let us consider an organization which has 6 system which from the data is sourced and it is consumed by 4 system , we have to build
6x4 =24 types integration between these application to stream the data. Here is where Kafka comes in where , it acts as a single 
distributed system which acts as an integration layer between multiple application and there by relaying data between application.
thus we can avoid building multipe integration between those.


TOPICS, PARTITIONS and OFFSETS


TOPICS:

    Topic is a particular streamm of data. It is equivalent to a table in a database. You can have any nummer of TOPICS

    Topics are split into Partitions. when you create a topic you have to specify how many partitions you have

    The Messages within a partition is ordered and when ever a message is added to a partition , it gets an id called OFFSET and it will get increment 
    by one and added as id for the next message 

    Each partition has it own series of offsets id and it is indpendent of offsets other partitions

    The Data is retained on the partition for a limited period- one week is the default value

    The Data writtern in the partition are immutable and hence it cannot be changed


BROKER and TOPICS: 

A Broker are actually the servers which hold the Topics and patications. Kafka cluster can have mutliple Brokers
Each broker is identified by its ID which is and integer.
A cluster can have any number of Broker but is good to have atlest 3 brokers to start within

The Topics-partitions will be distributed across multiple brokers in a kafka cluster.
If you connect to the any broker within a cluster it is equivalent to you have connected to entire cluster as all the 
broker are interconnected

The Topics and Partiition will be distrubed across multipel broker. Let us say if we have Three Brokers with id 101.102.103 and 
we have TopicA with three partition (0,1,2). then 

TopicA Partition0 will be there in Broker 101
TopicA Partition 1 will there in broker 102
Topic A Partition 2 will be ther in Broker 103

Its like the they are just distributed across brokers . If a topic as only two partition then it be distributed across two partitions


REPLICATION FACTORS:


    Replication Factors denotes how many times a Topic-Partition combination will be repliated(duplicated) across Brokers.

    This is particularly usefull when one of the Broker Fails the data will be retrieved from other Broker. 

    Replcaiton Factor can have value greater than 1. usually 2, 3. If it is 1 , then its a bit risky


    At Any Time Topic-Partition in One Broker will acts as a leader. The Other will just remain passive. If Some one has to read from or write into a partition
    then it will always takes place with the Leader. The other Replicats will just synchronize wiht the leader whenever there is some change.



PRODUCERS:

   As Kafka is a data Exchange distribtion system, we have to stream data from source system into Kafka and it also has to be read from Kafka

   Producers are responsible for writing data into the Kafka and Consumers are responsible for reading data from Kafka.
   
    Producers automatically know which which broker and partition to write to . In case of a Broker fails, then Producers recover the data from teh replicas automatically.

   when you send series of messages to producers without a key, then the PRoducers will write these message in a round robin to all the brokers. 
   In case if a input message has a key, then all the message goes into only one partition (this hold true only when the no of partitions remains unchanges)


ACKNOWLDGEMENT:

A Producer can choose to recieve acknowledgent for data writtern on teh topic partitions. There are different types of acknowldgetment

acks=0: Producer wont wait for acknowldgetment(possible data loss)
acks=1: Producer will wait for leader acknowldgetment()
acks=all: Producer will wait for acknowldgetment from leader as well as all replicas

CONSUMERS:
 
 Consumers know which broker to read daata from , if any Broker fails , the Consumer will know which broker to read data from.
 Data is always read in order within a partition. However the order is not mainted while readign  across partitions.

 CONSUMER GROUPS:

 Consumers read data in consumer groups. Each Consumer within a group read from exclusive partitions.

 If you have more consumers than the partitions, then some of the consumers will remain inacive.

Consumer Offsets:

  Kafka stores the offset at which a consumer group has been reading. this will be offset is be stored in __Consumer_offsets.
  this will be commited once teh consumer success fully processes teh data recieved from kafka.
  This offsets acts as a book mark  while readign the data. So that if the readign data fails, we ahve read teh data from where we left.


  The consumer can choose to commit the offset on various scenarios

    . At most once
    . Atleast once
    . always

 
BOOTSTRAP SERVER:

    Every Broker withing a Kafka cluseter is called Bootstrap server. thats because when you connect to one broker you will get connected to teh entire cluster.
    Each broker in a cluster knows about all other broker, topics and partitions.


ZOOKEEPER:
   Zookeper hold all the Brokers together and it manages them.
   It is also responsible for electing the Leader partitions when a broker fails. Zookeeper sends notificaiton to Kafka in case 
   of any changes, ie whe a broker dies , broker comes up or a topic is deleted

    Kafka Wont work without a zookeeper . zookeeper work with odd no of servers.




