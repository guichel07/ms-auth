package org.acme.security.jwt;

import io.smallrye.jwt.build.Jwt;
import java.util.Arrays;
import java.util.HashSet;
import org.eclipse.microprofile.jwt.Claims;

/**
 * A utility class to generate and print a JWT token string to stdout.
 */
public class GenerateToken {

    /**
     * Generates and prints a JWT token.
     */
    public static void main(String[] args) {
        String token = Jwt.issuer("https://example.com/issuer")
            .upn("jdoe@quarkus.io")
            .groups(new HashSet<>(Arrays.asList("User", "Admin")))
            .claim(Claims.birthdate.name(), "2001-07-13")
            .sign();

        System.out.println(token);
        System.exit(0);
    }
}
