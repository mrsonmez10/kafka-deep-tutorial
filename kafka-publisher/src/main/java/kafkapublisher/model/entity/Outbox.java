package kafkapublisher.model.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "outbox")
@Getter
@Setter
@TypeDef(name = "json", typeClass = JsonType.class)
public class Outbox {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Column(columnDefinition = "varchar(255)", name = "aggregatetype")
	private String aggregateType;

	@Column(columnDefinition = "varchar(255)", name = "aggregateid")
	private String aggregateId;

	@Column(columnDefinition = "varchar(255)")
	private String type;

	@Type(type = "json")
	@Column(columnDefinition = "jsonb")
	private Object payload;

	public Outbox(String aggregateType, String aggregateId, String type, Object payload) {
		this.aggregateType = aggregateType;
		this.aggregateId = aggregateId;
		this.type = type;
		this.payload = payload;
	}

	public Outbox() {

	}
}