package alex.tir.storage.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public final class JWTUtils {

    private JWTUtils() {
    }

    public static String generateToken(
            String subject, Instant expiration, String secret) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT
                .create()
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(subject)
                .withExpiresAt(java.sql.Date.from(expiration))
                .withIssuedAt(new Date())
                .sign(algorithm);
    }


    public static String verifyToken(String token, String secret) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm).build().verify(token).getSubject();
    }

}
