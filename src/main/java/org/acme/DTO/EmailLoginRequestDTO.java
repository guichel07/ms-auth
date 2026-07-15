package org.acme.DTO;

/**
 * LoginRequestDTO
 */
public record EmailLoginRequestDTO(
    String email,
    String password
) {}
