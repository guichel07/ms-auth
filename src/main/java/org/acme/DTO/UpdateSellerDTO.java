package org.acme.DTO;

public record UpdateSellerDTO(
    String name,
    String tag,
    String svgAvatar,
    String contact
) {}
