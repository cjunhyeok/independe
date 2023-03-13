package community.independe.controller.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberForm {

    @NotEmpty
    private String username;
    @NotEmpty
    private String userPassword;
    @NotEmpty
    private String nickname;
    private String role;
    private String email;
    private String number;

    private String city;
    private String street;
    private String zipcode;
}
