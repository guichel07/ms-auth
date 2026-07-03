package org.acme.Controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.acme.DTO.AuthRequestDTO;
import org.acme.DTO.AuthResponseDTO;
import org.acme.Service.SellerService;
import org.acme.Service.TokenService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/ms-auth")
@Tag(name = "Authentification", description = "Login et register")
public class AuthController {

    private final SellerService sellerService;
    private final TokenService tokenService;

    public AuthController(
        SellerService sellerService,
        TokenService tokenService
    ) {
        this.sellerService = sellerService;
        this.tokenService = tokenService;
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Créer un compte",
        description = "Enregistre un nouveau vendeur"
    )
    @APIResponse(responseCode = "200", description = "Compte créé avec succès")
    @APIResponse(responseCode = "400", description = "Données invalides")
    @APIResponse(responseCode = "409", description = "Email déjà utilisé")
    public Response register(AuthRequestDTO authRequestDTO) {
        AuthResponseDTO responseDTO = sellerService.registerSeller(
            authRequestDTO
        );
        String token = tokenService.generateEncryptedToken(
            responseDTO.getEmail(),
            responseDTO.getRole()
        );
        NewCookie jwtCookie = buildJwtCookie(token);
        return Response.status(Response.Status.CREATED)
            .entity(responseDTO)
            .cookie(jwtCookie)
            .build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Se connecter",
        description = "Authentifie un vendeur par email et mot de passe"
    )
    @APIResponse(responseCode = "200", description = "Connexion réussie")
    @APIResponse(responseCode = "401", description = "Identifiants incorrects")
    public Response login(AuthRequestDTO authRequestDTO) {
        AuthResponseDTO responseDTO = sellerService.loginWithEmail(
            authRequestDTO
        );
        String token = tokenService.generateEncryptedToken(
            responseDTO.getEmail(),
            responseDTO.getRole()
        );
        NewCookie jwtCookie = buildJwtCookie(token);
        return Response.ok(responseDTO).cookie(jwtCookie).build();
    }

    @GET
    @Path("/me")
    @RolesAllowed({ "SELLER", "ADMIN" })
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Vérifier la session active",
        description = "Retourne les infos du vendeur connecté si le JWT est valide"
    )
    @APIResponse(responseCode = "200", description = "Session valide")
    @APIResponse(responseCode = "401", description = "Non authentifié")
    public Response me(@Context SecurityContext ctx) {
        String emailFromToken = ctx.getUserPrincipal().getName();
        AuthResponseDTO responseDTO = sellerService.getSellerByEmail(
            emailFromToken
        );
        return Response.ok(responseDTO).build();
    }

    @POST
    @Path("/logout")
    @Operation(
        summary = "Se déconnecter",
        description = "Supprime le cookie JWT"
    )
    @APIResponse(responseCode = "200", description = "Déconnexion réussie")
    public Response logout() {
        return Response.ok().cookie(buildExpiredJwtCookie()).build();
    }

    private NewCookie buildJwtCookie(String token) {
        return new NewCookie.Builder("jwt")
            .value(token)
            .path("/")
            .maxAge(3600)
            .secure(false)
            .httpOnly(true)
            .sameSite(NewCookie.SameSite.LAX)
            .build();
    }

    private NewCookie buildExpiredJwtCookie() {
        return new NewCookie.Builder("jwt")
            .value("")
            .path("/")
            .maxAge(0)
            .secure(false)
            .httpOnly(true)
            .sameSite(NewCookie.SameSite.LAX)
            .build();
    }
}
