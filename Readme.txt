
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


Replication Factors denotes how many times a Topic-Partition combination will be repliated across Broker.

This is particularly usefull when one of the Broker Fails the data will be retrieved from other Broker. 

Replcaiton Factor can have value greater than 1. usually 2, 3. If it is 1 , then its a bit risky





