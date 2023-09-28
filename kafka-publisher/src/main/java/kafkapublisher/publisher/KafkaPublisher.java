package kafkapublisher.publisher;

import kafkapublisher.model.entity.Outbox;
import kafkapublisher.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KafkaPublisher {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final OutboxRepository outboxRepository;

	public void publish(String topicName, Object message) {
		try {
// If you want try to see outbox records are sent to kafka, uncomment this code block.
//            if(topicName.equals(topicName)){
//                throw new RuntimeException();
//            }
			kafkaTemplate.send(topicName, message);
		} catch (Exception e) {
			String key = UUID.randomUUID().toString();
			Outbox outbox = new Outbox(topicName, key, topicName, message);
			outboxRepository.save(outbox);
		}

	}
}
