package kafkaconsumer.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import kafkaconsumer.consumer.exception.BusinessException;
import kafkaconsumer.consumer.exception.ValidationException;
import kafkaconsumer.model.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import kafkaconsumer.publisher.KafkaPublisher;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.nio.channels.AlreadyConnectedException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.jar.JarException;

@Component
@RequiredArgsConstructor
public class Consumer {

	private final KafkaPublisher kafkaPublisher;
	private static final String TOPIC_NAME = "order-create";
	private static final String RETRY_TOPIC_NAME = "order-create.kafkaconsumer.retry";
	private static final String ERROR_TOPIC_NAME = "order-create.kafkaconsumer.error";
	private static final String GROUP_ID = "KafkaOrderConsumer-GroupId";
	private final ObjectMapper objectMapper;

	@KafkaListener(topics = {TOPIC_NAME}, groupId = GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
	public void listener(@Payload OrderEvent event, ConsumerRecord c) throws Exception {
		try {
			consume(event);
		} catch (BusinessException | ValidationException exception) {
		} catch (ConnectException connectException) {
			String value = (String) c.value();
			JsonNode jsonNode = objectMapper.readTree(value);
			kafkaPublisher.publish(RETRY_TOPIC_NAME, jsonNode);
		}
	}

	@KafkaListener(topics = RETRY_TOPIC_NAME, groupId = GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
	public void listener2(@Payload OrderEvent event, ConsumerRecord c) throws Exception {
		try {
			consume(event);
		} catch (BusinessException | ValidationException exception) {
		} catch (ConnectException connectException) {
			String value = (String) c.value();
			JsonNode jsonNode = objectMapper.readTree(value);
			kafkaPublisher.publish(ERROR_TOPIC_NAME, jsonNode);
		}
	}

	private void consume(OrderEvent event) throws Exception {
		int error = ThreadLocalRandom.current().nextInt(0, 10);
		//If you want to send all events to retry/error topic, uncomment this code block.
//		error = 9;
		if (error < 7) {
			System.out.println("I Consumed");
		} else if (error == 7) {
			throw new BusinessException("BusinessException");
		} else if (error == 8) {
			throw new ValidationException("ValidationException");
		} else {
			throw new ConnectException("Connect to mssql is failed");
		}
	}

}
