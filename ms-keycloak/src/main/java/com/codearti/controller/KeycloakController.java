package com.codearti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codearti.model.dto.ResponseMessage;
import com.codearti.model.dto.UserDto;
import com.codearti.service.KeycloakService;

import lombok.RequiredArgsConstructor;

@RefreshScope
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "v1")
public class KeycloakController {

	@Autowired
	private KeycloakService keycloakService;
	
	 @PostMapping("/create")
	    public ResponseEntity<ResponseMessage> create(@RequestBody UserDto user){
	        Object[] obj = keycloakService.createUser(user);
	        int status = (int) obj[0];
	        ResponseMessage message = (ResponseMessage) obj[1];
	        return ResponseEntity.status(status).body(message);
	    }
}
