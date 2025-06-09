package controleur;


import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

public class JwtManager {
    // pour SHA256 : 256 bits mini
    private static final String SECRET_KEY = "bachibouzoukbachibouzoukbachibouzoukbachibouzouk";

    public static String createJWT(int id, String pseudo) {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        SecretKey signingKey = Keys.hmacShaKeyFor(keyBytes);
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(240); // 4 mn
        Date expDate = Date.from(expiration);
        // Let's set the JWT Claims
        String token = Jwts.builder()
                .id(UUID.randomUUID().toString().replace("-", ""))
                .issuedAt(Date.from(now))
                .subject("Authentification pour Fakord")
                .issuer("salim.chabchoub@etu.univ-lyon1.fr")
                .claim("id",id)
                .claim("pseudo",pseudo)
                .expiration(expDate)
                .signWith(signingKey)
                .compact();
        return token;
    }

    public static Claims decodeJWT(String jwt) throws Exception {
        // This line will throw an exception if it is not a signed JWS (as expected)
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        SecretKey signingKey = Keys.hmacShaKeyFor(keyBytes);
        JwtParser parser = Jwts.parser()
                .verifyWith(signingKey)
                .build();
        Claims claims = parser.parseSignedClaims(jwt).getPayload();
        return claims;
    }


}
