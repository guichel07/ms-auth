package org.acme.DTO;

public class AuthResponseDTO {

    private String email;
    private String name;
    private String tag;
    private String role;
    private String svgAvatar;
    private String contact;

    public AuthResponseDTO() {}

    public AuthResponseDTO(
        String email,
        String name,
        String tag,
        String role,
        String svgAvatar,
        String contact
    ) {
        this.email = email;
        this.name = name;
        this.tag = tag;
        this.role = role;
        this.svgAvatar = svgAvatar;
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSvgAvatar() {
        return svgAvatar;
    }

    public void setSvgAvatar(String svgAvatar) {
        this.svgAvatar = svgAvatar;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
