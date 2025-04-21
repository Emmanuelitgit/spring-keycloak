package springKeycloak.utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import springKeycloak.dto.KeycloakPermissionsDTO;
import springKeycloak.dto.ResponseDTO;
import springKeycloak.models.User;
import springKeycloak.repositories.UserRepository;


import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AppUtils {

    private final UserRepository userRpo;
    private final RestTemplate restTemplate;
    private final String TOKEN_ENDPOINT = "http://localhost:8080/realms/TestRealm/protocol/openid-connect/token";
    private final String CLIENT_SECRET = "xK7Ds7giCVwRnycwmGWVy90z6cYuyjKA";

    @Autowired
    public AppUtils(UserRepository userRpo, RestTemplate restTemplate) {
        this.userRpo = userRpo;
        this.restTemplate = restTemplate;
    }

    /**
     * This method is used to extract the authenticated username.
     * @return username
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public static String getAuthenticatedUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * This method is used to extract the authenticated user authorities from the security context holder.
     * @return authorities
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public static Collection<? extends GrantedAuthority> getAuthenticatedUserAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    /**
     * This method is used to extract the authenticated user id.
     * @return UUID authenticatedUserId
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public UUID getAuthenticatedUserId(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRpo.findUserByUsername(username);
        if (userOptional.isEmpty()){
            throw new  NullPointerException("User record not found");
        }
        return userOptional.get().getId();
    }

    /**
     * This method is used to handle all responses in the application.
     * @param message
     * @param status
     * @return responseDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public static ResponseDTO getResponseDto(String message, HttpStatus status){
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setMessage(message);
        responseDto.setDate(ZonedDateTime.now());
        responseDto.setStatusCode(status.value());
        return responseDto;
    }

    /**
     * This method is used to handle all responses in the application.
     * @param message
     * @param status
     * @param data
     * @return responseDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public static ResponseDTO getResponseDto(String message, HttpStatus status, Object data){
        if(data==null){
            ResponseDTO responseDto = getResponseDto(message, status);
            return responseDto;
        }
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setMessage(message);
        responseDto.setDate(ZonedDateTime.now());
        responseDto.setStatusCode(status.value());
        responseDto.setData(data);
        return responseDto;
    }

    /**
     * This method is used to get user permissions from keycloak given the access token.
     *
     * @param
     * @return responseDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public List<KeycloakPermissionsDTO> getUserPermissionsFromKeycloak(String accessToken){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "urn:ietf:params:oauth:grant-type:uma-ticket");
        formData.add("client_id", "test-app");
        formData.add("client_secret", CLIENT_SECRET);
        formData.add("username", "eyidana");
        formData.add("password", "1234");
        formData.add("response_mode", "permissions");
        formData.add("subject_token", accessToken);
        formData.add("audience", "test-app");

        // 3. Wrap in HttpEntity
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        // 4. Send POST request
        ResponseEntity<List<KeycloakPermissionsDTO>> response = restTemplate.exchange(
                TOKEN_ENDPOINT,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<List<KeycloakPermissionsDTO>>() {}
        );

        // 5. Print token response
        System.out.println("Response: " + response.getBody().get(0).getRsname());
        return response.getBody();
    }
}