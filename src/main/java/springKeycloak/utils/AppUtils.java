package springKeycloak.utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import springKeycloak.models.User;
import springKeycloak.repositories.UserRepository;
import springKeycloak.service.UserService;


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

    public static String getAuthenticatedUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static Collection<? extends GrantedAuthority> getAuthenticatedUserAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    public UUID getAuthenticatedUserId(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRpo.findUserByUsername(username);
        if (userOptional.isEmpty()){
            throw new  NullPointerException("User record not found");
        }
        return userOptional.get().getId();
    }

}