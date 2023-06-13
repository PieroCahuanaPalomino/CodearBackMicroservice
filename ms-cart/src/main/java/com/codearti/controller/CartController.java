package com.codearti.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codearti.model.dto.CartRequestDeleteDto;
import com.codearti.model.dto.CartRequestDto;
import com.codearti.model.dto.CartResponseDto;
import com.codearti.service.CartService;

import lombok.RequiredArgsConstructor;

@RefreshScope
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "v1")
public class CartController {
	
	private final CartService service;
	
	@Value("${configuration.texto}")
	private String text;
	
	private final Environment env;

	@GetMapping(value = "/{customerId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE })
	public ResponseEntity<CartResponseDto> findByCustomerId(@PathVariable Long customerId){
		CartResponseDto result = service.findByCustomerId(customerId);
		return ResponseEntity.ok(result);
	}
	
	@PostMapping(value = "/{customerId}/item", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE }, 
			consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE })
	public ResponseEntity<CartResponseDto> addItem(@PathVariable Long customerId, 
												   @Valid @RequestBody CartRequestDto cartRequestDto) {
		CartResponseDto result = service.addItem(customerId, cartRequestDto);
		return ResponseEntity.ok(result);
	}
	
	@DeleteMapping(value = "/{customerId}/item", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE }, 
	consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE })
	public ResponseEntity<CartResponseDto> removeItem(@PathVariable Long customerId, 
											   		  @Valid @RequestBody CartRequestDeleteDto cartRequestDeleteDto) {
		CartResponseDto result = service.removeItem(customerId, cartRequestDeleteDto);
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/configuration")
	public ResponseEntity<?> getConfiguration(@Value("${server.port}") String port){
		Map<String, String> json = new HashMap<>();
		json.put("texto", text);
		json.put("puerto", port);
		
		if(env.getActiveProfiles().length>0 && env.getActiveProfiles()[0].equals("dev")) {
			json.put("autor.nombre", env.getProperty("configuration.autor.nombre"));
			json.put("autor.email", env.getProperty("configuration.autor.email"));
		}
		
		return new ResponseEntity<Map<String, String>>(json, HttpStatus.OK);
	}

}