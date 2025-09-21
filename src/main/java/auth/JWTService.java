package auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTService {

    private final String secret;

    public JWTService(String secret) {
        this.secret = secret;
    }

    public String generarToken(String idExe) {
        return Jwts.builder()
                .claim("id", idExe)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }
}
