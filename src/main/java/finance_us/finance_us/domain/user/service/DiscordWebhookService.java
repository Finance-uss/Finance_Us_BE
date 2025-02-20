package finance_us.finance_us.domain.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
            // JSON 객체 생성
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();

            // user 정보 추가
            ObjectNode userNode = objectMapper.createObjectNode();
            userNode.put("id", user.getId());
            userNode.put("name", user.getName());
            payload.set("user", userNode);

            // requestDTO 추가
            ObjectNode requestNode = objectMapper.createObjectNode();
            requestNode.put("content", requestDTO.getContent());

            if (requestDTO.getImgUrl() != null && !requestDTO.getImgUrl().isEmpty()) {
                requestNode.put("imgUrl", requestDTO.getImgUrl()); // 이미지가 있는 경우만 추가
            }

            payload.set("requestDTO", requestNode);

            // HTTP 요청 전송
            ResponseEntity<String> response = restTemplate.postForEntity(nodeServerUrl + "/send-auth", payload, String.class);

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.DISCORD_ERROR);
        }
    }
}
