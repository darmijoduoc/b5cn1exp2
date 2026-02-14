package cl.duocuc.darmijo.users.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
public class CreateUserParams {
    @NotBlank
    @Email(message = "El email no tiene un formato válido")
    private String email;
    @NotBlank
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String displayName;
    @NotBlank
    @Size(min = 12, max = 20, message = "La contraseña debe tener entre 12 y 20 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,20}$",
        message = "La contraseña debe contener al menos una letra mayúscula, una letra minúscula, un número y un carácter especial")
    private String password;
    @NotBlank
    @Pattern(
        regexp = "^(ADMIN|WORKER)$",
        message = "El rol debe ser ADMIN o WORKER"
    )
    
    private String rol;
    
    public CreateUserParams() {
    }
    
    public CreateUserParams(String email, String displayName, String password, String rol) {
        this.email = email;
        this.displayName = displayName;
        this.password = password;
        this.rol = rol;
    }
    
    public String toString() {
        return "CreateUserParams{" +
            "email='" + email + '\'' +
            ", displayName='" + displayName + '\'' +
            ", password='[PROTECTED]'" +
            '}';
    }
}
