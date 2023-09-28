package kafkaretryjob.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPublisher {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void publish(String topicName, Object message) {
		kafkaTemplate.send(topicName, message);
	}
}
