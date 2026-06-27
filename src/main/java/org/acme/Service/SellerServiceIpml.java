package org.acme.Service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.acme.DTO.AuthRequestDTO;
import org.acme.DTO.AuthResponseDTO;
import org.acme.DTO.UpdateSellerDTO;
import org.acme.Entity.SellerEntity;
import org.acme.Exception.BusinessException;
import org.acme.Repository.SellerRepository;

@ApplicationScoped
public class SellerServiceIpml implements SellerService {

    private final SellerRepository sellerRepository;

    public SellerServiceIpml(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    @Override
    public AuthResponseDTO getSellerById(Long id) {
        SellerEntity sellerEntity = sellerRepository.findById(id);

        if (sellerEntity == null) {
            throw new BusinessException(
                Response.Status.NOT_FOUND,
                "Seller not found"
            );
        }

        return new AuthResponseDTO(
            sellerEntity.getEmail(),
            sellerEntity.getName(),
            sellerEntity.getTag(),
            sellerEntity.getRole(),
            sellerEntity.getSvgAvatar(),
            sellerEntity.getContact()
        );
    }

    @Override
    public ArrayList<AuthResponseDTO> getAllSellers() {
        return sellerRepository
            .listAll()
            .stream()
            .map(s ->
                new AuthResponseDTO(
                    s.getEmail(),
                    s.getName(),
                    s.getTag(),
                    s.getRole(),
                    s.getSvgAvatar(),
                    s.getContact()
                )
            )
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    @Transactional
    public AuthResponseDTO registerSeller(AuthRequestDTO authRequestDTO) {
        if (
            authRequestDTO.email() == null || authRequestDTO.email().isBlank()
        ) {
            throw new BusinessException(
                Response.Status.BAD_REQUEST,
                "L'email est obligatoire"
            );
        }

        if (
            !authRequestDTO
                .email()
                .matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")
        ) {
            throw new BusinessException(
                Response.Status.BAD_REQUEST,
                "L'email est invalide"
            );
        }

        if (authRequestDTO.name() == null || authRequestDTO.name().isBlank()) {
            throw new BusinessException(
                Response.Status.BAD_REQUEST,
                "Le nom est obligatoire"
            );
        }

        if (
            authRequestDTO.password() == null ||
            authRequestDTO.password().length() < 6
        ) {
            throw new BusinessException(
                Response.Status.BAD_REQUEST,
                "Le mot de passe doit faire au moins 6 caractères"
            );
        }

        if (authRequestDTO.role() == null || authRequestDTO.role().isBlank()) {
            throw new BusinessException(
                Response.Status.BAD_REQUEST,
                "Le rôle est obligatoire"
            );
        }

        if (
            sellerRepository.findByEmail(
                authRequestDTO.email().toLowerCase()
            ) != null
        ) {
            throw new BusinessException(
                Response.Status.CONFLICT,
                "Cet email est déjà utilisé"
            );
        }

        SellerEntity newSeller = new SellerEntity();
        newSeller.setEmail(authRequestDTO.email().toLowerCase().trim());
        newSeller.setName(authRequestDTO.name().trim());
        newSeller.setRole(authRequestDTO.role());
        newSeller.setPassword(BcryptUtil.bcryptHash(authRequestDTO.password()));
        newSeller.setTag(generateTag(authRequestDTO.name()));
        newSeller.setSvgAvatar(generateDefaultAvatar(authRequestDTO.name()));
        newSeller.setContact(authRequestDTO.contact());

        sellerRepository.persist(newSeller);

        return new AuthResponseDTO(
            newSeller.getEmail(),
            newSeller.getName(),
            newSeller.getTag(),
            newSeller.getRole(),
            newSeller.getSvgAvatar(),
            newSeller.getContact()
        );
    }

    private String generateTag(String name) {
        String base = name.replaceAll("\\s+", "").toUpperCase();
        base = base.length() >= 4 ? base.substring(0, 4) : base;

        String tag = base;
        while (sellerRepository.findByTag(tag) != null) {
            tag =
                base.substring(0, 3) +
                (char) ('A' + new java.util.Random().nextInt(26));
        }
        return tag;
    }

    private String generateDefaultAvatar(String name) {
        String initials =
            name.trim().length() >= 2
                ? name.trim().substring(0, 2).toUpperCase()
                : name.trim().toUpperCase();

        return String.format(
            """
                <svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'>
                    <circle cx='50' cy='50' r='50' fill='#6366f1'/>
                    <text x='50' y='55' text-anchor='middle'
                          dominant-baseline='middle'
                          font-size='35' fill='white' font-family='sans-serif'>%s</text>
                </svg>
            """,
            initials
        );
    }

    @Override
    public AuthResponseDTO loginWithEmail(AuthRequestDTO authRequestDTO) {
        SellerEntity seller = sellerRepository.findByEmail(
            authRequestDTO.email()
        );

        if (seller == null) {
            throw new BusinessException(
                Response.Status.UNAUTHORIZED,
                "Identifiants incorrects"
            );
        }

        boolean passwordMatches =
            io.quarkus.elytron.security.common.BcryptUtil.matches(
                authRequestDTO.password(),
                seller.getPassword()
            );

        if (!passwordMatches) {
            throw new BusinessException(
                Response.Status.UNAUTHORIZED,
                "Identifiants incorrects"
            );
        }

        return new AuthResponseDTO(
            seller.getEmail(),
            seller.getName(),
            seller.getTag(),
            seller.getRole(),
            seller.getSvgAvatar(),
            seller.getContact()
        );
    }

    @Override
    public AuthResponseDTO loginWithId(AuthRequestDTO authRequestDTO) {
        SellerEntity seller = sellerRepository.findById(authRequestDTO.id());

        if (seller == null) {
            throw new BusinessException(
                Response.Status.UNAUTHORIZED,
                "Identifiants incorrects"
            );
        }

        boolean passwordMatches =
            io.quarkus.elytron.security.common.BcryptUtil.matches(
                authRequestDTO.password(),
                seller.getPassword()
            );

        if (!passwordMatches) {
            throw new BusinessException(
                Response.Status.UNAUTHORIZED,
                "Identifiants incorrects"
            );
        }

        return new AuthResponseDTO(
            seller.getEmail(),
            seller.getName(),
            seller.getTag(),
            seller.getRole(),
            seller.getSvgAvatar(),
            seller.getContact()
        );
    }

    @Override
    @Transactional
    public AuthResponseDTO updateSeller(
        Long id,
        UpdateSellerDTO updateSellerDTO,
        String emailFromToken
    ) {
        SellerEntity seller = sellerRepository.findById(id);
        if (seller == null) {
            throw new BusinessException(
                Response.Status.NOT_FOUND,
                "Seller introuvable"
            );
        }

        if (!seller.getEmail().equals(emailFromToken)) {
            throw new BusinessException(
                Response.Status.FORBIDDEN,
                "Accès refusé"
            );
        }

        if (
            updateSellerDTO.name() != null && !updateSellerDTO.name().isBlank()
        ) {
            seller.setName(updateSellerDTO.name().trim());
        }

        if (
            updateSellerDTO.svgAvatar() != null &&
            !updateSellerDTO.svgAvatar().isBlank()
        ) {
            seller.setSvgAvatar(updateSellerDTO.svgAvatar());
        }

        if (
            updateSellerDTO.contact() != null &&
            !updateSellerDTO.contact().isBlank()
        ) {
            seller.setContact(updateSellerDTO.contact().trim());
        }

        if (updateSellerDTO.tag() != null && !updateSellerDTO.tag().isBlank()) {
            SellerEntity existingTag = sellerRepository.findByTag(
                updateSellerDTO.tag()
            );
            if (existingTag != null && !existingTag.getId().equals(id)) {
                throw new BusinessException(
                    Response.Status.CONFLICT,
                    "Ce tag est déjà utilisé"
                );
            }
            seller.setTag(updateSellerDTO.tag().toUpperCase().trim());
        }

        return new AuthResponseDTO(
            seller.getEmail(),
            seller.getName(),
            seller.getTag(),
            seller.getRole(),
            seller.getSvgAvatar(),
            seller.getContact()
        );
    }

    @Override
    @Transactional
    public void deleteSellerId(Long id) {
        if (!sellerRepository.deleteById(id)) {
            throw new BusinessException(
                Response.Status.NOT_FOUND,
                "Seller not found"
            );
        }
    }
}
