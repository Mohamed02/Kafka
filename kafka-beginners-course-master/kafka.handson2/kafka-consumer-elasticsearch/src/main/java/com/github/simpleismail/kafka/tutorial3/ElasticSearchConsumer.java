package com.github.simpleismail.kafka.tutorial3;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ElasticSearchConsumer {


        public static RestHighLevelClient createClient(){
            // replace with your own credentials
            String hostname = "kafka-twitter-7236145617.us-east-1.bonsaisearch.net";
            String username = "qczgvxry"; // needed only for bonsai
            String password = "t69y5sts6v"; // needed only for bonsai

            // credentials provider help supply username and password
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password));

            RestClientBuilder builder = RestClient.builder(
                    new HttpHost(hostname, 443, "https"))
                    .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                        @Override
                        public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                        }
                    });

            RestHighLevelClient client = new RestHighLevelClient(builder);
            return client;
        }
        public static KafkaConsumer<String,String> createConsumer(String topic){
            String bootstrapServers = "127.0.0.1:9092";
            String groupId = "kafka-demo-elasticsearch";


            // create consumer configs
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            // create consumer
            KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

            consumer.subscribe(Arrays.asList(topic));
            return  consumer;
        }
    public static void main(String[] args) throws IOException {
            RestHighLevelClient client = createClient();

        KafkaConsumer<String,String> consumer= createConsumer("twitter_tweet");

        while(true){
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100)); // new in Kafka 2.0.0

            for (ConsumerRecord<String, String> record : records){

                IndexRequest indexRequest= new IndexRequest("twitter");
                indexRequest.source(record.value(), XContentType.JSON);
                IndexResponse indexResponse= client.index(indexRequest, RequestOptions.DEFAULT);
                String id=indexResponse.getId();
                System.out.println("ID is "+id);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            }

    }
}
