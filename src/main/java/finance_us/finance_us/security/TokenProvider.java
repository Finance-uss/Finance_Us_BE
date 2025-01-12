
package finance_us.finance_us.security;

import finance_us.finance_us.domain.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class TokenProvider {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512); // 단일 토큰용 비밀키

    // 토큰 생성
    public String generateToken(String username, Long userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1시간 유효
                .signWith(SECRET_KEY)
                .compact();
    }

    // 토큰 검증 및 클레임 추출
    public Claims extractClaims(String token) {
        try {
            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰에서도 클레임 추출
            return e.getClaims();
        }
    }

    // 사용자명 추출
    public String extractUsernameFromToken(String token) {
        return extractClaims(token).getSubject(); // JWT의 subject에서 사용자명 추출
    }

    // 사용자 ID 추출
    public Long extractUserIdFromToken(String token) {
        return extractClaims(token).get("userId", Long.class); // JWT의 "userId" 클레임에서 추출
    }

    // 토큰 유효성 확인
    public boolean isValidToken(String token) {
        try {
            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(jwtToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}