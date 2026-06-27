package org.acme.Service;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Set;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class TokenService {

    @ConfigProperty(name = "smallrye.jwt.decrypt.key.string")
    String secretKey;

    public String generateEncryptedToken(String email, String role) {
        return Jwt.issuer("https://acme.org/issuer")
            .upn(email)
            .groups(Set.of(role))
            .expiresIn(3600)
            .innerSign()
            .encryptWithSecret(secretKey);
    }
}
