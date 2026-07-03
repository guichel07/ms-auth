package org.acme.Controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import org.acme.DTO.AuthResponseDTO;
import org.acme.DTO.UpdateSellerDTO;
import org.acme.Service.SellerService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/ms-sellers")
@Tag(name = "Sellers", description = "Gestion des vendeurs")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "ADMIN" })
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Récupérer un vendeur par ID")
    @APIResponse(responseCode = "200", description = "Vendeur trouvé")
    @APIResponse(responseCode = "404", description = "Vendeur inexistant")
    @APIResponse(responseCode = "401", description = "Non authentifié")
    public AuthResponseDTO getSellerById(@PathParam("id") Long id) {
        return sellerService.getSellerById(id);
    }

    @GET
    @RolesAllowed({ "ADMIN" })
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Récupérer tous les vendeurs")
    @APIResponse(
        responseCode = "200",
        description = "Liste récupérée avec succès"
    )
    @APIResponse(responseCode = "403", description = "Accès refusé")
    public ArrayList<AuthResponseDTO> getAllSellers() {
        return sellerService.getAllSellers();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "ADMIN" })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Modifier son profil")
    @APIResponse(responseCode = "200", description = "Profil mis à jour")
    @APIResponse(responseCode = "403", description = "Accès refusé")
    @APIResponse(responseCode = "404", description = "Vendeur inexistant")
    public Response updateSeller(
        @PathParam("id") Long id,
        UpdateSellerDTO updateSellerDTO,
        @Context SecurityContext ctx
    ) {
        String emailFromToken = ctx.getUserPrincipal().getName();
        AuthResponseDTO responseDTO = sellerService.updateSeller(
            id,
            updateSellerDTO,
            emailFromToken
        );
        return Response.ok(responseDTO).build();
    }

    @DELETE
    @RolesAllowed({ "ADMIN" })
    @Path("/{id}")
    @Operation(summary = "Supprimer un vendeur")
    @APIResponse(responseCode = "204", description = "Vendeur supprimé")
    @APIResponse(responseCode = "404", description = "Vendeur inexistant")
    public Response deleteSeller(@PathParam("id") Long id) {
        sellerService.deleteSellerId(id);
        return Response.noContent().build();
    }
}
