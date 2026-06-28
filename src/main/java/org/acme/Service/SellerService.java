package org.acme.Service;

import java.util.ArrayList;
import org.acme.DTO.AuthRequestDTO;
import org.acme.DTO.AuthResponseDTO;
import org.acme.DTO.UpdateSellerDTO;

public interface SellerService {
    AuthResponseDTO getSellerById(Long id);

    ArrayList<AuthResponseDTO> getAllSellers();

    AuthResponseDTO registerSeller(AuthRequestDTO authRequestDTO);

    AuthResponseDTO loginWithEmail(AuthRequestDTO authRequestDTO);

    AuthResponseDTO loginWithId(AuthRequestDTO authRequestDTO);

    AuthResponseDTO updateSeller(
        Long id,
        UpdateSellerDTO updateSellerDTO,
        String emailFromToken
    );

    AuthResponseDTO getSellerByEmail(String email);

    void deleteSellerId(Long id);
}
