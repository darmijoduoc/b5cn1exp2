package cl.duocuc.darmijo.users.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticateUserParams {
    @NotBlank(message = "Email no debe estar vacío")
    private String email;
    @NotBlank(message = "Contraseña no debe estar vacía")
    private String password;
    
    public String toString() {
        return "AuthenticateUserParams{" +
            "email='" + email + '\'' +
            ", password='[PROTECTED]'" +
            '}';
    }
}
