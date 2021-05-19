
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

    A Broker are actually the servers which hold the Topics and Partiition. Kafka cluster can have mutliple Brokers
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

    It is not adivisable to increase the partition during a topiic lifecycle, as it would break the key ordering gurantees.

    More Partition we have More will be the throughput. Because we can have more consumers to read from the Partition. And also the utilizaiton of the broker will also be hight

SEGMENTS:

    Each Partiition is madeup of Segments , which are nothing but files. At any instant only one Segment will be active, that is the segment where we write. 
    Segment has two indexs whcih is used to identify the message
     . Offset to position index- which allows kafka where to read to find a message.
     . timestamp to offset index: whcih allows kafka to find message with a timestamp

     therefore kafka knows where to find data in constant time !

REPLICATION FACTORS:


    Replication Factors denotes how many times a Topic-Partition combination will be repliated(duplicated) across Brokers.

    This is particularly usefull when one of the Broker Fails the data will be retrieved from other Broker. 

    Replcaiton Factor can have value greater than 1. usually 2, 3. If it is 1 , then its a bit risky


    At Any Time Topic-Partition in One Broker will acts as a leader. The Other will just remain passive. If Some one has to read from or write into a partition
    then it will always takes place with the Leader. The other Replicats will just synchronize wiht the leader whenever there is some change.

    It is not adivisable to increase the replication factor during a topiic lifecycle, as it would lead to unexpected performance decrease.


PRODUCERS:

   As Kafka is a data Exchange distribtion system, we have to stream data from source system into Kafka and it also has to be read from Kafka

   Producers are responsible for writing data into the Kafka and Consumers are responsible for reading data from Kafka.
   
    Producers automatically know which which broker and partition to write to . In case of a Broker fails, then Producers recover the data from teh replicas automatically.

   when you send series of messages to producers without a key, then the PRoducers will write these message in a round robin to all the brokers. 
   In case if a input message has a key, then all the message goes into only one partition (this hold true only when the no of partitions remains unchanges)




ACKNOWLDGEMENT:

A Producer can choose to recieve acknowledgent for data writtern on teh topic partitions. There are different types of acknowldgetment

acks=0: Producer wont wait for acknowldgetment(possible data loss). Acks can be set as 0 , if the data transmitted
        are not critical such as logs or other collection of junk data. As the producer is not waiting for ack form the Broker, the latency is low in this approach
acks=1: Producer will wait for acknowldgetment form the leader that the data is saved in the topic partition. The acks can be set as 1 when we can compramise on the least possiblity of data losses
        In this approaches teh Producer waits for acknowldgetment from the leader and hence the latency is little higher than the previous one.
acks=all: Producer will wait for acknowldgetment from leader as well as all replicas. This option is choosen when the transimitted data is critical and no data loss is accepted. Here the Latency is very high because the Producer has to wait
          for data beign writtern on the Leader and it is successfully replicated across all the replicas

        acks=all must be used in conjuction with the property min.insync.replicas. min.insync.replicas denotes that minimum number of replicas that needs to provide acknowldgetment to the Producer. This is critical because, if there are many replication of topic partition
     then the Producer have to wait for acknowldgetment from all the Leader as well as All the broker , which would reduce the latency significantly.

        Letus say if we have 4 brokers(one out of this acts as a leader) and replication factor is 4. Here when the producer writes a data into the kafka , teh data will be writtern into the leader first and all other 3 replicas. 
        Now as the min-insync-replicas is 2 , the producer will wait for acknowldgetment from the Leader and acknowldgetment from 2 of the replicas.

        Let us say if 3 of the replica broker is down. then the PRODUCER will recieve an exception - NOT_ENOUGH_REPLICAS instead of acknowledgement. And The producer have to retry sendign the mesasage again .

PRODUCER RETRIES:
 
        Whenever there is a transient failure( such as NOT_ENOUGH_REPLICAS), the Producer has to handle it by retrying to send teh data again.
        The no of times teh Producer retry sending the data can be configured as property "retries"

        Disadvantages of Retires:
         . When teh data is sent to the Kafka and it is saved successfully, however if the acknowldgetment is not recieved due to some network error,
           and since the acks in not recieved at the Producer, the producer tends to retry sending the same data again to kafka. This would cause  duplicaiton of data at the kafka,
           Improper Ordering of the messages in the Partition. This issue is resolved by Idempontent Producer
        

IDEMPOTENT PRODUCER:

        Idem producer will send the messages , if it didnt get any acknowldgetment, then it will retry sending the same messages again along with an Produce Request ID. At the Kafka Side, Kafka will know this is a duplicate message based on the Produce Request ID and it wont commit the 
        messages, But it will send the acknowldgetment. 
        By Setting the Property of Producer - enable.idempotence= true (for Kafka >=0.11) a Producer can be made as Idemponent Producer

        Refer kafka-beginners-course-master/kafka-producer-twitter/src/main/java/kafka/tutorial2/TwitterProducer.java  to know how to set the 

MESSAGE COMPRESSION:
        The Message which are sent to teh Kafka can be compressed to reduce the size. The Compression type available are gzip.

LINGER & BATCHING:
    The Producer will first send the data to Kafka..and while waiting for the data reachign the kafka, the Producer will smartly batch the second set of messages.

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

    . At most once:
        The offsets are committed as soon as the messsage batch is recieved, if the processign fails after reciving the message, then the messsage will be lost.
    . Atleast once
        The offsets are commited after the message is processed. This can also cause duplication of messages, Hence the we have to make sure the processing is idempotent.
    . always:  
            This is moslty used for Communication between kafka and kafka


CONSUMER POLLING:
    Unlike other messaging bus which has a PUSH model , which pushes data to the consumer, the KAFKA consumer followes poll model.
    that is it will poll to retreive message from broker and then wait for some timeout  and then does the next polling.
    
    Some of the properties whcih helps to control the behaviour of consumer polling
    Fetch.min.bytes: It controls how much data you want to pull atleast on each request. Higher number will have high throughput but at the cost of latency
    Max.poll.records: It controlls how many records to recieve per poll request.

CONSUMER OFFSET COMMITS STRATEGIES:

    enable.auto.commit- true and Synchronous Processing of batches:
            with auto commit , offsets will be committed automatically for you at regular internal
     enable.auto.commit- false and Synchronous Processing of batches:
BOOTSTRAP SERVER:

    Every Broker withing a Kafka cluseter is called Bootstrap server. thats because when you connect to one broker you will get connected to teh entire cluster.
    Each broker in a cluster knows about all other broker, topics and partitions.


ZOOKEEPER:
   Zookeper hold all the Brokers together and it manages them.
   It is also responsible for electing the Leader partitions when a broker fails. Zookeeper sends notificaiton to Kafka in case 
   of any changes, ie whe a broker dies , broker comes up or a topic is deleted

    Kafka Wont work without a zookeeper . zookeeper work with odd no of servers.


##############SNIPPETS##############

INSTALLATION 
 refer the below file for installation commands of kafka
    LinkedInFiles/03_01/Downloading_Source_Code/Code/0-start-kafka/macosx/macosx-setup.sh

STARTING KAFKA and ZOOKEEPER 
LinkedInFiles/03_01/Downloading_Source_Code/Code/0-start-kafka/macosx/macosx-start-kafka.sh

TOPICS CREATION DELETION etc
LinkedInFiles/03_01/Downloading_Source_Code/Code/1-kafka-cli/0-kafka-topics.sh

PRODUCER
LinkedInFiles/03_01/Downloading_Source_Code/Code/1-kafka-cli/1-kafka-console-producer.sh

CONSUMER
LinkedInFiles/03_01/Downloading_Source_Code/Code/1-kafka-cli/2-kafka-console-consumer.sh

IMPLEMENTING CONSUMER/PRODUCER Using Java
    Refer kafka-beginners-course-master/kafka-basics

IMPLEMENTING A TWITTER PRODUCER in Java
    Here the tweets from teh twitter are retrieved through the twitter client and they are sent to the Kafka's Topic Partition
    using Produced created through JAVA.

    Refer kafka-beginners-course-master/kafka-producer-twitter

KAFKA CONNECT:
    Kafka connect is all about code and connectors reuse


    There are four scenarios kafka is involved

    Source to Kafka   - Producer API   (we can use Kafa Connect Source)
    Kafka to Kafka     -Consumer and Producer API  (we will use Kafa Streams)
    Kafka to Sink   - Consumer API     (we will use Kafka Connect Sink)
    Kafka to App -    Consumer API       (we will use Kafka Connect Sink)

    Programmers always import data from same sources such s Databases, JDBC, Couchbase, GolderGate, SAP HANA, Block Chain
    Cassandara, DynamoDB, FTP, IOT, MOngoDB, Twitter etc..

    Programmers always want ot store data in the same sink such as S3, JDBC, Couchbase, GolderGate, SAP HANA, Block Chain
    Cassandara, DynamoDB, FTP, IOT, MOngoDB, Elastic Search etc..


    We program ourself the producers and consumers for above set of sources , it will be very tough to achieve Fault Tolerance, Idempotence, Distribution and Ordering.

    So many programmers have already done this and open sourced


    REFER Diagram-Kafka_Connect.png for the Singificatnce  of Kafka Connect Cluster.
        Source connectors get data from Common Data sources
        Sink Connectors to publish that data in Common Data Stores.

KAFKA STREAMS:
    Kafka Stream is an easy data processign and transformation library within kafka.
    Kafka streams helps in processing the data realtime, which is otherwise can be done with Producer and consumer and its not developer friendly.

    for e.g filter tweets whith 1million likes, combine realted tweets together etc.


SCHEMA REGISTRY:

    In our handson we have not done any validation on the input data we recieved. This could cause a bad data beign writtern by the producer on the kafka 
    and being read by the consumer and passed to the target system. Hence there exsist the schema in Kafka with the which we can define the format of data. It is called as Scheme registry


GUIDELINES FOR PARTITION AND REPLCIATION FACTORS

    . Partition Per Topic
            for small cluster(with <6 brokers) it is advisable to have partition count as 2x no of brokers

            for Big cluster(with >12 brokers) it is advisable to have partition count as 1x no of brokers
    . Replication Factore

            Should be atleast 2 , usually 3 and maximum 4 

LOG CLEANUP POLICIES:

    Many Kafka clusters make the data expire based on a policy. That Concept is called log CLEANUP

    Policy1: log.cleanup.policy= delete
     . Delete based on age of data
     . Delete based on max size of log

    Policy2: log.cleanup.policy=compact
     . Delete the old values corresponding to a key , when a new value is updated for that corresponding key
     . Will Delete old duplicate keys after teh active segment is commited.
     . Infinite Time and space retention
     
    Log Clean up Policy- Delete
    log.retention.hours=168 or 1 week   (no of hours a message can stay in Hours)
    log.retention.bytes=-1    (Max Size of each partition, useful to keep the size of a log under threshold, when teh size increases
                                below the threshold then old data will get deleted)

Multiple Broker Cluster:

    So far we have done a handson with Single Broker Cluster. It is also possible to start multiple Broker Cluster.
    For that you must create multiple server.properties such as server1,server2,server3.properties and each server.properties must have different port number,brokerID and data directory
    and then after startign the zoo keeper, start all three kafka severs.