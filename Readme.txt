
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
