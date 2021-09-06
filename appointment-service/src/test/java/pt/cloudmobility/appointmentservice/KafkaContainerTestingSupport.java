package pt.cloudmobility.appointmentservice;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface KafkaContainerTestingSupport {

  KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.1.1"));

  @DynamicPropertySource
  static void kafkaProperties(DynamicPropertyRegistry registry) {
    kafka.start();
    registry.add("spring.cloud.stream.kafka.binder.brokers", kafka::getBootstrapServers);
    // Use the actual production binders instead, and that requires disabling the test
    // binder autoconfiguration
    registry.add(
        "spring.autoconfigure.exclude",
        () -> "org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration");
  }

  default KafkaProducer<String, Object> createProducer() {
    Map<String, Object> producerProps = new HashMap<>(getProducerProps());
    return new KafkaProducer<>(producerProps);
  }

  default KafkaConsumer<String, String> createConsumer(String group, String topicName) {
    Map<String, Object> consumerProps = new HashMap<>(getConsumerProps(group));
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
    consumer.subscribe(List.of(topicName));
    return consumer;
  }

  private Map<String, String> getProducerProps() {
    Map<String, String> producerProps = new HashMap<>();
    producerProps.put(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringSerializer");
    producerProps.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringSerializer");
    producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
    return producerProps;
  }

  private Map<String, String> getConsumerProps(String group) {
    var consumerProps = new HashMap<String, String>();
    consumerProps.put(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
    consumerProps.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
    consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, group);
    return consumerProps;
  }
}
