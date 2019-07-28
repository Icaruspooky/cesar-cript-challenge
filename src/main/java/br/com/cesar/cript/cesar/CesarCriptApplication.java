package br.com.cesar.cript.cesar;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.FileWriter;
import java.net.URI;
import java.net.URISyntaxException;

@SpringBootApplication
public class CesarCriptApplication {

	public static void main(String[] args) throws URISyntaxException {
		SpringApplication.run(CesarCriptApplication.class, args);
		RestTemplate restTemplate = new RestTemplate();
        String token = "31c06c3e64d7df497c03add5ab95b9bccf9dff1f";
		ReceivedMessage receivedMessage = restTemplate.getForObject("https://api.codenation.dev/v1/challenge/dev-ps/generate-data?token=" + token, ReceivedMessage.class);

		receivedMessage.setDecifrado(receivedMessage.decode());
		receivedMessage.setResumo_criptografico(DigestUtils.sha1Hex(receivedMessage.getDecifrado()));

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String jsonString = objectMapper.writeValueAsString(receivedMessage);
			FileWriter jsonFile = new FileWriter("answer.json") ;
			jsonFile.write(jsonString);
			jsonFile.flush();
			jsonFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		FileSystemResource fsResource = new FileSystemResource("answer.json");
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("answer", fsResource);
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		URI serverUrl = new URI("https://api.codenation.dev/v1/challenge/dev-ps/submit-solution?token=" + token);
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
			System.out.println(objectMapper.writeValueAsString(response));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
