package kafkapublisher.controller;

import kafkapublisher.model.request.Order;
import lombok.RequiredArgsConstructor;
import kafkapublisher.publisher.KafkaPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class KafkaController {

	private final KafkaPublisher kafkaPublisher;
	String topicName = "order-create";

	@PostMapping
	public void sendEvent(@RequestBody Order order) {
		kafkaPublisher.publish(topicName, order);
	}
}
