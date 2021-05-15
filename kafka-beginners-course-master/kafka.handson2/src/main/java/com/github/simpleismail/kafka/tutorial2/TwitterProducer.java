package com.github.simpleismail.kafka.tutorial2;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwitterProducer {

    public TwitterProducer(){

    }

    public static void main(String[] args) {
        new TwitterProducer().run();

    }
    public void run(){
        //create a twitter client
        //loop to send tweets to kafka

        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
        Client client= createTwitterClient(msgQueue);
        client.connect();
          //create a kafka producer
        KafkaProducer<String ,String> producer= createKafkaProducer();

        // add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("Stopping applicaiton");
            client.stop();
            producer.close();
            System.out.println("Done");
        }));

        while (!client.isDone()) {
            String msg = null;
            try {
                msg = msgQueue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(msg !=null){
                producer.send(new ProducerRecord<>("twitter_tweet", null, msg), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if(exception!=null){
                            System.out.println("Something bad happened"+exception);
                        }
                    }
                });
                System.out.println(msg);
            }

        }
    }

    public KafkaProducer<String, String> createKafkaProducer() {
        Properties properties=new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String,String> producer=new KafkaProducer<>(properties);
       return  producer;
    };

    String consumerKey="i8GYb9qlEtuorBM04UAcwLsEm";
    String consumerSecret="b9h42Iy3BjRXk8GF3ycCAHiJM8hRJPmIobsBTWuoYzx6vmzkS8";
    String token ="111910738-xvd7xxfa2watxdUDfRPKriv0fLI5EsDYJ6dMPC8F";
    String secret="9OR2lczfty9MqjQXoV5c0CkXCwCAUwwtiWxFVyJ95IZbk";
    public Client createTwitterClient(BlockingQueue msgQueue){
/** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
// Optional: set up some followings and track terms
        List<String> terms = Lists.newArrayList("Bitcoin");
        hosebirdEndpoint.trackTerms(terms);

// These secrets should be read from a config file
        Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);
        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")                              // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));                       // optional: use this if you want to process client events

        Client hosebirdClient = builder.build();
        return hosebirdClient;
    }
}
