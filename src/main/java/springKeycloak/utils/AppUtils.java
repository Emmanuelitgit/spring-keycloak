package springKeycloak.utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import springKeycloak.dto.ResponseDTO;
import springKeycloak.models.User;
import springKeycloak.repositories.UserRepository;
import springKeycloak.service.UserService;


import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Component
public class AppUtils {

    private final UserRepository userRpo;

    @Autowired
    public AppUtils(UserRepository userRpo) {
        this.userRpo = userRpo;
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

}