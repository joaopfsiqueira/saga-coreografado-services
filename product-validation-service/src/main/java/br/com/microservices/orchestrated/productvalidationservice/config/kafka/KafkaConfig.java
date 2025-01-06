package br.com.microservices.orchestrated.productvalidationservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@EnableKafka // Enables detection of @KafkaListener annotations on any Spring-managed bean in the container
@Configuration // essa classe vai possuir configurações durante a subida do contexto
@RequiredArgsConstructor // cria um construtor com todos os atributos final
public class KafkaConfig {

    private static final Integer PARTITION_COUNT = 1;
    private static final Integer REPLICA_COUNT = 1;


    @Value("${spring.kafka.bootstrap-servers}")
    private String boostrapServer;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
    @Value("${spring.kafka.topic.orchestrator}")
    private String orchestratorTopic;
    @Value("${spring.kafka.topic.product-validation-success}")
    private String productValidationSuccessTopic;
    @Value("${spring.kafka.topic.product-validation-failure}")
    private String productValidationFailureTopic;


    @Bean // informando para o spring que esse metodo vai ser CONFIGURÁVEL, bean é um objeto insganciado.
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    // Criando as propriedades do KAFKA.
    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // deserializa a chave, JA QUE É O CONSUMER
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // deserializa o valor, JA QUE É O CONSUMER
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProps());
    }

    private Map<String, Object> producerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // SERELIAZA A CHAVE, JA QUE É O PRODUCER
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // SERELIAZA O VALOR, JA QUE É O PRODUCER
        return props;
    }

    //KAFKA TEMPLATE, QUEM VAI DE FATO INSTANCIAR O KAFKA NO CÓDIGO, ELE QUEM VAI FAZER A COMUNICAÇÃO COM O KAFKA
    // ELE QUEM VAI ENVIAR E RECEBER MENSAGENS
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory());
    }


    private NewTopic buildTopic(String name){
        return TopicBuilder.name(name).replicas(REPLICA_COUNT).partitions(PARTITION_COUNT).build();
    }

    @Bean
    public NewTopic orchestratorTopicTopic() {
        return buildTopic(orchestratorTopic);
    }

    @Bean
    public NewTopic productValidationSuccessTopic() {
        return buildTopic(productValidationSuccessTopic);
    }

    @Bean
    public NewTopic productValidationFailureTopic() {
        return buildTopic(productValidationFailureTopic);
    }

}


