package cl.duocuc.darmijo.users.models;

public class ForgotPasswordParams {
    private String email;

    public ForgotPasswordParams() {
    }

    public ForgotPasswordParams(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
