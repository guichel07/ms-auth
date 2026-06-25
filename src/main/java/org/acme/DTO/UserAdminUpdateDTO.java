package org.acme.DTO;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Modification d'un AUTRE utilisateur par un admin (nom, rôle, contact, statut actif/inactif).
 * Distinct de UserUpdateDTO (auto-modification du profil connecté, sans rôle ni statut).
 */
public record UserAdminUpdateDTO(
    @Size(min = 2, message = "Le nom doit faire au moins 2 caractères")
    String name,

    @Pattern(regexp = "ADMIN|SELLER", message = "Le rôle doit être ADMIN ou SELLER")
    String role,

    @Pattern(regexp = "^0[1-9]\\d{8}$", message = "Le numéro de contact est invalide")
    String contact,

    Boolean active
) {}
