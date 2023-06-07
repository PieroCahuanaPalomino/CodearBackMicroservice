package com.codearti.filters.factory;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class EjemploGatewayFilterFactory extends AbstractGatewayFilterFactory<EjemploGatewayFilterFactory.Configuracion>{

	public EjemploGatewayFilterFactory() {
		super(Configuracion.class);
	}

	@Override
	public GatewayFilter apply(Configuracion config) {
		return (exchange, chain) -> {
	
			log.info("ejecutando pre gateway filter factory: " + config.mensaje);
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				
				Optional.ofNullable(config.cookieValor).ifPresent(cookie -> {
					exchange.getResponse().addCookie(ResponseCookie.from(config.cookieNombre, cookie).build());
				});
				
				log.info("ejecutando post gateway filter factory: " + config.mensaje);
				
			}));
		};
	}
	

	@Override
	public String name() {
		return "EjemploCookie";
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList("mensaje", "cookieNombre", "cookieValor");
	}

	@Getter
	@Setter
	public static class Configuracion {

		private String mensaje;
		private String cookieValor;
		private String cookieNombre;
		
	}

}
