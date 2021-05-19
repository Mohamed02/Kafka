package com.github.ismail;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback {
    public static void main(String[] args) {
    Logger logger= LoggerFactory.getLogger(ProducerDemoWithCallback.class);
    Properties properties=new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer .class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String,String> producer=new KafkaProducer<>(properties);
    //crete a producer
    ProducerRecord<String,String> record= new ProducerRecord<>("first_topic","sent message with call back");
        producer.send(record, new Callback() {
        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            //executes every time a record is successfully sent or an exception is thrown
            if(exception==null){
                System.out.println("the message sent");
              logger.info("Recieved new metadata. \n"+
                      "Topic:"+ metadata.topic()+ "\n"+
                      "Partition:"+ metadata.partition()+ "\n"+
                       "Offset:"+ metadata.offset()+ "\n" +
                      "TimeStamp:"+ metadata.timestamp()+ "\n"
              );
            }
            else{
            logger.error("Error while sending data");

            }
        }
    });
      producer.flush();
        producer.close();
}
}
