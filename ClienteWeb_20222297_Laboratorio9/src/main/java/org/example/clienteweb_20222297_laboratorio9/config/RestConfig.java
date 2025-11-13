package org.example.clienteweb_20222297_laboratorio9.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class RestConfig {

    @Value("${api.username:admin}")
    private String username;

    @Value("${api.password:admin123}")
    private String password;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Agregar interceptor para autenticación Basic
        restTemplate.getInterceptors().add(new BasicAuthInterceptor(username, password));
        
        return restTemplate;
    }

    /**
     * Interceptor que añade el header de autenticación Basic a todas las peticiones
     */
    private static class BasicAuthInterceptor implements ClientHttpRequestInterceptor {
        private final String authHeader;

        public BasicAuthInterceptor(String username, String password) {
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            this.authHeader = "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, 
                                           ClientHttpRequestExecution execution) throws IOException {
            request.getHeaders().set("Authorization", authHeader);
            return execution.execute(request, body);
        }
    }
}
