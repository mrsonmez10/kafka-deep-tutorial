package kafkaretryjob.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import kafkaretryjob.publisher.KafkaPublisher;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class Consumer {

	private final KafkaPublisher kafkaPublisher;
	private static final String ORDER_CREATE_KAFKA_CONSUMER_ERROR = "order-create.kafkaconsumer.error";
	private static final Map<String, Integer> retryCountsByTopicNames = new HashMap<>();
	private static final String RETRY_COUNT_KEY = "retryCount";
	private static final String ERROR_KEY = ".error";
	private static final String RETRY_KEY = ".retry";

	@PostConstruct
	private void init() {
		retryCountsByTopicNames.put(ORDER_CREATE_KAFKA_CONSUMER_ERROR, 5);
	}

	private static final String GROUP_ID = "KafkaRetryJob-GroupId";

	@KafkaListener(topics = {
			ORDER_CREATE_KAFKA_CONSUMER_ERROR}, groupId = GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
	public void listener(@Payload JsonNode jsonNode, ConsumerRecord consumerRecord) {
		int retryCount = getRetryCount(jsonNode);
		String errorTopic = consumerRecord.topic();
		if (isRetryable(errorTopic, retryCount)) {
			retryCount++;
			((ObjectNode) jsonNode).put(RETRY_COUNT_KEY, retryCount);
			kafkaPublisher.publish(errorToRetryTopic(errorTopic), jsonNode);
		}
	}

	private static int getRetryCount(JsonNode jsonNode) {
		JsonNode retryCountNode = jsonNode.get("retryCount");
		return retryCountNode == null ? 0 : retryCountNode.asInt();
	}

	private boolean isRetryable(String topic, int currentRetryCount) {
		int retryCountLimit = retryCountsByTopicNames.getOrDefault(topic, 0);
		boolean limitless = retryCountLimit == 0;
		return limitless || currentRetryCount < retryCountLimit;
	}

	private String errorToRetryTopic(String errorTopic) {
		return errorTopic.replace(ERROR_KEY, RETRY_KEY);
	}
}
