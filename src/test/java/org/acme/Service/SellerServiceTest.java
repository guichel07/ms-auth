package org.acme.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.ws.rs.core.Response;
import org.acme.DTO.AuthRequestDTO;
import org.acme.DTO.AuthResponseDTO;
import org.acme.DTO.UpdateSellerDTO;
import org.acme.Entity.SellerEntity;
import org.acme.Exception.BusinessException;
import org.acme.Repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    SellerRepository sellerRepository;

    SellerServiceIpml sellerService;

    private SellerEntity fakeSeller;

    @BeforeEach
    void setUp() {
        // ✅ Constructeur au lieu de l'injection de champ
        sellerService = new SellerServiceIpml(sellerRepository);

        fakeSeller = new SellerEntity();
        fakeSeller.setId(1L);
        fakeSeller.setEmail("john@acme.org");
        fakeSeller.setName("John");
        fakeSeller.setRole("SELLER");
        fakeSeller.setTag("JOHN");
        fakeSeller.setContact("0600000000");
        fakeSeller.setPassword(
            io.quarkus.elytron.security.common.BcryptUtil.bcryptHash(
                "secret123"
            )
        );
    }

    // ─── getSellerById ────────────────────────────────────────────

    @Test
    void getSellerById_shouldReturnDTO_whenSellerExists() {
        when(sellerRepository.findById(1L)).thenReturn(fakeSeller);

        AuthResponseDTO result = sellerService.getSellerById(1L);

        assertEquals("john@acme.org", result.getEmail());
        assertEquals("John", result.getName());
    }

    @Test
    void getSellerById_shouldThrow404_whenNotFound() {
        when(sellerRepository.findById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () ->
            sellerService.getSellerById(99L)
        );

        assertEquals(Response.Status.NOT_FOUND, ex.getErrorCode());
    }

    // ─── registerSeller ───────────────────────────────────────────

    @Test
    void register_shouldThrow400_whenEmailBlank() {
        AuthRequestDTO dto = new AuthRequestDTO(
            "",
            "secret123",
            "John",
            "SELLER",
            null,
            null,
            null
        );

        BusinessException ex = assertThrows(BusinessException.class, () ->
            sellerService.registerSeller(dto)
        );

        assertEquals(Response.Status.BAD_REQUEST, ex.getErrorCode());
        assertEquals("L'email est obligatoire", ex.getMessage());
    }

    @Test
    void register_shouldThrow400_whenEmailInvalid() {
        AuthRequestDTO dto = new AuthRequestDTO(
            "not-an-email",
            "secret123",
            "John",
            "SELLER",
            null,
            null,
            null
        );

        BusinessException ex = assertThrows(BusinessException.class, () ->
            sellerService.registerSeller(dto)
        );

        assertEquals(Response.Status.BAD_REQUEST, ex.getErrorCode());
        assertEquals("L'email est invalide", ex.getMessage());
    }

    @Test
    void register_shouldThrow400_whenPasswordTooShort() {
        AuthRequestDTO dto = new AuthRequestDTO(
            "john@acme.org",
            "123",
            "John",
            "SELLER",
            null,
            null,
            null
        );

        BusinessException ex = assertThrows(BusinessException.class, () ->
            sellerService.registerSeller(dto)
        );

        assertEquals(Response.Status.BAD_REQUEST, ex.getErrorCode());
    }

    @Test
    void register_shouldThrow409_whenEmailAlreadyUsed() {
        AuthRequestDTO dto = new AuthRequestDTO(
            "john@acme.org",
            "secret123",
            "John",
            "SELLER",
            null,
            null,
            null
        );
        when(sellerRepository.findByEmail("john@acme.org")).thenReturn(
            fakeSeller
        );

        BusinessException ex = assertThrows(BusinessException.class, () ->
            sellerService.registerSeller(dto)
        );

        assertEquals(Response.Status.CONFLICT, ex.getErrorCode());
    }

    @Test
    void register_shouldPersist_whenValid() {
        AuthRequestDTO dto = new AuthRequestDTO(
            "new@acme.org",
            "secret123",
            "Alice",
            "SELLER",
            null,
            null,
            null
        );

        when(sellerRepository.findByEmail("new@acme.org")).thenReturn(null);
        when(sellerRepository.findByTag(anyString())).thenReturn(null);
        doNothing()
            .when(sellerRepository)
            .persist(any(SellerEntity.class));

        AuthResponseDTO result = sellerService.registerSeller(dto);

        assertEquals("new@acme.org", result.getEmail());
        verify(sellerRepository, times(1)).persist(any(SellerEntity.class));
    }

    // ─── loginWithEmail ───────────────────────────────────────────

    @Test
    void login_shouldReturnDTO_whenCredentialsValid() {
        AuthRequestDTO dto = new AuthRequestDTO("john@acme.org", "secret123");
        when(sellerRepository.findByEmail("john@acme.org")).thenReturn(
            fakeSeller
        );

        AuthResponseDTO result = sellerService.loginWithEmail(dto);

        assertEquals("john@acme.org", result.getEmail());
    }

    @Test
    void login_shouldThrow401_whenEmailNotFound() {
        AuthRequestDTO dto = new AuthRequestDTO(
            "unknown@acme.org",
            "secret123"
        );
        when(sellerRepository.findByEmail("unknown@acme.org")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () ->
            sellerService.loginWithEmail(dto)
        );

        assertEquals(Response.Status.UNAUTHORIZED, ex.getErrorCode());
    }

    @Test
    void login_shouldThrow401_whenWrongPassword() {
        AuthRequestDTO dto = new AuthRequestDTO("john@acme.org", "wrongpass");
        when(sellerRepository.findByEmail("john@acme.org")).thenReturn(
            fakeSeller
        );

        BusinessException ex = assertThrows(BusinessException.class, () ->
            sellerService.loginWithEmail(dto)
        );

        assertEquals(Response.Status.UNAUTHORIZED, ex.getErrorCode());
    }

    // ─── updateSeller ─────────────────────────────────────────────

    @Test
    void update_shouldThrow404_whenNotFound() {
        when(sellerRepository.findById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () ->
            sellerService.updateSeller(
                99L,
                new UpdateSellerDTO("X", null, null, null),
                "john@acme.org"
            )
        );

        assertEquals(Response.Status.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void update_shouldThrow403_whenNotOwner() {
        when(sellerRepository.findById(1L)).thenReturn(fakeSeller);

        BusinessException ex = assertThrows(BusinessException.class, () ->
            sellerService.updateSeller(
                1L,
                new UpdateSellerDTO("X", null, null, null),
                "hacker@evil.com"
            )
        );

        assertEquals(Response.Status.FORBIDDEN, ex.getErrorCode());
    }

    @Test
    void update_shouldThrow409_whenTagTaken() {
        when(sellerRepository.findById(1L)).thenReturn(fakeSeller);

        SellerEntity other = new SellerEntity();
        other.setId(2L);
        when(sellerRepository.findByTag("TAKE")).thenReturn(other);

        BusinessException ex = assertThrows(BusinessException.class, () ->
            sellerService.updateSeller(
                1L,
                new UpdateSellerDTO(null, "TAKE", null, null),
                "john@acme.org"
            )
        );

        assertEquals(Response.Status.CONFLICT, ex.getErrorCode());
    }

    @Test
    void update_shouldUpdateFields_whenValid() {
        when(sellerRepository.findById(1L)).thenReturn(fakeSeller);
        when(sellerRepository.findByTag("NEWY")).thenReturn(null);

        AuthResponseDTO result = sellerService.updateSeller(
            1L,
            new UpdateSellerDTO("NewName", "NEWY", null, "0611111111"),
            "john@acme.org"
        );

        assertEquals("NewName", result.getName());
        assertEquals("NEWY", result.getTag());
    }

    // ─── deleteSellerId ───────────────────────────────────────────

    @Test
    void delete_shouldThrow404_whenNotFound() {
        when(sellerRepository.deleteById(99L)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () ->
            sellerService.deleteSellerId(99L)
        );

        assertEquals(Response.Status.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void delete_shouldSucceed_whenExists() {
        when(sellerRepository.deleteById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> sellerService.deleteSellerId(1L));
    }
}
