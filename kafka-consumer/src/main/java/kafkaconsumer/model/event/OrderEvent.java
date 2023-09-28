package kafkaconsumer.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class OrderEvent {

	private String id;
	private String username;
	private String price;
	private String createdTime;
}
