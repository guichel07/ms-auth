package org.acme.DTO;

import org.acme.Entity.UserEntity;

/**
 * SellerDTO
 */
public record UserDTO(
    String email,
    String name,
    String tag,
    String role,
    String svgAvatar,
    String contact
) {
    public static UserDTO fromEntity(UserEntity entity) {
        return new UserDTO(
            entity.getEmail(),
            entity.getName(),
            entity.getTag(),
            entity.getRole(),
            entity.getSvgAvatar(),
            entity.getContact()
        );
    }
}
