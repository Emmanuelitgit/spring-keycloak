package springKeycloak.dto;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KeycloakPermissionsDTO {
    private String rsid;
    private String rsname;
    private List<String> scopes;

    public KeycloakPermissionsDTO() {
    }

    public String getRsid() {
        return rsid;
    }

    public void setRsid(String rsid) {
        this.rsid = rsid;
    }

    public String getRsname() {
        return rsname;
    }

    public void setRsname(String rsname) {
        this.rsname = rsname;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
}
