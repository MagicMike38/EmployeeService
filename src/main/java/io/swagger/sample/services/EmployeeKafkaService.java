package io.swagger.sample.services;

import io.swagger.sample.model.Employee;
import io.swagger.sample.serializers.EmployeeDeserializer;
import io.swagger.sample.serializers.EmployeeSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class EmployeeKafkaService implements AbstractKakfaService<Employee> {

    private final EmployeeService employeeService;
    private final Properties kafkaProps;

    public EmployeeKafkaService(Properties props) throws IOException {
        this.kafkaProps = props;
        System.out.println("ooooooooooo emp serv kafka const");
        employeeService = new EmployeeService(props);
    }

    public boolean publish(Employee employee) {

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProps.getProperty("PRODUCER_BOOTSTRAP_SERVERS_CONFIG"));
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProps.getProperty("PRODUCER_ACKS_CONFIG"));
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProps.get("PRODUCER_BATCH_SIZE_CONFIG"));
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaProps.get("PRODUCER_BUFFER_MEMORY_CONFIG"));

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, EmployeeSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EmployeeSerializer.class.getName());

        Producer<Integer, Employee> producer = new KafkaProducer<>(props);

        producer.send(new ProducerRecord<>(kafkaProps.getProperty("TOPIC_NAME"), employee.getId(), employee));
        producer.close();

        return true;
    }

    public boolean consume() {

        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProps.getProperty("CONSUMER_BOOTSTRAP_SERVERS_CONFIG"));
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProps.getProperty("CONSUMER_AUTO_OFFSET_RESET_CONFIG"));
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, kafkaProps.getProperty("CONSUMER_GROUP_ID_CONFIG"));

        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EmployeeDeserializer.class.getName());

        KafkaConsumer<Integer, Employee> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(kafkaProps.getProperty("TOPIC_NAME")));

        ConsumerRecords<Integer, Employee> records = consumer.poll(Duration.ofMillis(100));
        System.out.println("Records: "+records);
        for (ConsumerRecord<Integer, Employee> record : records) {
            try{
            employeeService.createEmployee(record.value());}
            catch (Exception ex){
                System.out.println("Exception occurred" + ex.getMessage());
            }
        }
        consumer.close();

        return true;
    }
}
