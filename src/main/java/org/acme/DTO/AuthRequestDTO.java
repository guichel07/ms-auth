package org.acme.DTO;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record AuthRequestDTO(
    @Schema(hidden = true) Long id,
    String email,
    String password,
    String name,
    String role,
    @Schema(hidden = true) String svgAvatar,
    @Schema(hidden = true) String tag,
    String contact
) {
    public AuthRequestDTO(
        String email,
        String password,
        String name,
        String role,
        String svgAvatar,
        String tag,
        String contact
    ) {
        this(null, email, password, name, role, svgAvatar, tag, contact);
    }

    public AuthRequestDTO(String email, String password) {
        this(null, email, password, null, null, null, null, null);
    }

    public AuthRequestDTO(Long id) {
        this(id, null, null, null, null, null, null, null);
    }
}
