package cl.duocuc.darmijo.users.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddRoleParams {
    @NotBlank()
    @Size(min = 3, max = 30, message = "El rol debe tener entre 3 y 100 caracteres")
    private String role;
    @NotBlank
    
    public AddRoleParams() {
    }
    
    public AddRoleParams(String role) {
        this.role = role;
    }
}