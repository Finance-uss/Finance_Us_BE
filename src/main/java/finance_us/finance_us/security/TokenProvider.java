//package finance_us.finance_us.security;
//
//import finance_us.finance_us.domain.user.entity.User;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.Date;
//
//@Slf4j
//@Service
//public class TokenProvider {
//    private static final String SECRET_KEY="NMA8JPctFuna59f5";
//
//    public String create(User user) {
//        Date expireDate=Date.from(
//                Instant.now()
//                        .plus(1, ChronoUnit.DAYS));
//        return Jwts.builder()
//                .signWith(SignatureAlgorithm.HS512,SECRET_KEY)
//                .setSubject(String.valueOf(user.getId()))
//                .setIssuer("todo app")
//                .setIssuedAt(new Date())
//                .setExpiration(expireDate)
//                .compact();
//    }
//
//    public String validateAndGetUserId(String token) {
//        Claims claims=Jwts.parser()
//                .setSigningKey(SECRET_KEY)
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.getSubject();
//    }
//
//    public Claims validateAndGetClaims(String token) {
//        try {
//            // 토큰이 유효할 경우 Claims 반환
//            return Jwts.parser()
//                    .setSigningKey(SECRET_KEY)
//                    .parseClaimsJws(token)
//                    .getBody();
//        } catch (ExpiredJwtException e) {
//            // 토큰이 만료된 경우에도 Claims 반환 가능
//            return e.getClaims();
//        }
//    }
//
//}
//
