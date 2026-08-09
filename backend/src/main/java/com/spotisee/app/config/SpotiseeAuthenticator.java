package com.spotisee.app.config;

import com.spotisee.app.dao.AuthDao;
import com.spotisee.app.models.User;
import com.spotisee.app.models.dao.UserFromDb;
import io.dropwizard.auth.Authenticator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static com.spotisee.app.config.Constants.ROLE_USER;

public class SpotiseeAuthenticator implements Authenticator<String, User> {

    private final SecretKey key;
    private final AuthDao authDao;

    public SpotiseeAuthenticator(AuthDao authDao) {
        this.authDao = authDao;

        String verySecretThing = "a9RYwA1PkEjv1nclOlMafh0dikj8sVA/tXkcBNlsVyU=";
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(verySecretThing));

//        --- code to generate the verySecretThing
//        this.key = Jwts.SIG.HS256.key().build();
//        System.out.println(Encoders.BASE64.encode(this.key.getEncoded()));
    }

    public Optional<User> authenticate(String token) {
        return validateToken(token);
    }

    public Optional<String> generateToken(String username, String password) {
        Optional<UserFromDb> userFromDb = authDao.getUser(username);

        if (userFromDb.isPresent() && BCrypt.checkpw(password, userFromDb.get().getPasswordHash())) {
            Set<String> roles = authDao.getUserRoles(userFromDb.get().getUserId());
            return Optional.of(createToken(
                    new User(userFromDb.get().getUserId(),
                            userFromDb.get().getActiveUploadId(),
                            userFromDb.get().getUsername(),
                            roles)
            ));
        }
        return Optional.empty();
    }

    public void register(String username, String password) {
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        long userId = authDao.registerUser(username, passwordHash);

        authDao.addUserRole(userId, ROLE_USER);
    }

    public boolean validUsername(String username) {
        Optional<UserFromDb> userFromDb = authDao.getUser(username);
        return userFromDb.isEmpty();
    }

    private String createToken(User user) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("username", user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(3, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }

    private Optional<User> validateToken(String token) {
        JwtParser parser = Jwts.parser().verifyWith(key).build();
        Claims claims;
        try {
            claims = parser.parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            return Optional.empty();
        }
        String username = claims.get("username", String.class);
        Date expiration = claims.getExpiration();

        if (expiration.before(Date.from(Instant.now()))) {
            return Optional.empty();
        }

        Optional<UserFromDb> userFromDb = authDao.getUser(username);
        if (userFromDb.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new User(
                userFromDb.get().getUserId(),
                userFromDb.get().getActiveUploadId(),
                username,
                authDao.getUserRoles(userFromDb.get().getUserId())
        ));
    }
}
