package com.codearti.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.codearti.events.Event;
import com.codearti.events.EventType;
import com.codearti.events.ProductCreatedEvent;
import com.codearti.model.dto.ProductResponseDto;

@Component
public class ProductEventService {
	
	@Autowired
	private KafkaTemplate<String, Event<?>> producer;
	
	@Value("${topic.customer.name:product}")
	private String topicCustomer;
	
	public void publish(ProductResponseDto productDto) {

		ProductCreatedEvent created = new ProductCreatedEvent();
		created.setData(productDto);
		created.setId(UUID.randomUUID().toString());
		created.setType(EventType.CREATED);
		created.setDate(new Date());

		this.producer.send(topicCustomer, created);
	}
	
	

}