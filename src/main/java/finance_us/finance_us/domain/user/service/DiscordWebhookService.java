package finance_us.finance_us.domain.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import finance_us.finance_us.domain.user.dto.AuthRequestDTO;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class DiscordWebhookService {

    @Value("${node.server.url}")
    private String nodeServerUrl;
    private final RestTemplate restTemplate = new RestTemplate();//http 통신 위해 설정

    public void sendAuthRequest(User user, AuthRequestDTO.userAuthRequestDTO requestDTO) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("user", Map.of("id", user.getId(), "name", user.getName()));
            payload.put("requestDTO", Map.of("content", requestDTO.getContent(), "imgUrl", requestDTO.getImgUrl()));

            ResponseEntity<String> response = restTemplate.postForEntity(nodeServerUrl + "/send-auth", payload, String.class);

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.DISCORD_ERROR);
        }
    }
}
