package com.codearti.controller.keycloak;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codearti.dto.keycloak.ResponseMessage;
import com.codearti.model.keycloak.User;
import com.codearti.service.CartService;
import com.codearti.service.keycloak.KeycloakService;

import lombok.RequiredArgsConstructor;


@RefreshScope
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "v1")
public class UserControllerKeycloak {

	
	@Autowired
	private KeycloakService keycloakService;
	
	 @PostMapping("/create")
	    public ResponseEntity<ResponseMessage> create(@RequestBody User user){
	        Object[] obj = keycloakService.createUser(user);
	        int status = (int) obj[0];
	        ResponseMessage message = (ResponseMessage) obj[1];
	        return ResponseEntity.status(status).body(message);
	    }
	
}
