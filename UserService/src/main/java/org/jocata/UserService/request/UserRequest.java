package org.jocata.UserService.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.jocata.UserService.customAnnotation.AgeLimit;
import org.jocata.UserService.model.MyUser;
import org.jocata.Utils.UserIdentifier;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String name;

    private String email;

    @NotBlank
    private String phoneNo;


    private String address;

    private String password;

    @AgeLimit(minimumAge = 16, message = "Your age should not be lesser than 16")
    private String dob;

    private UserIdentifier userIdentifier;

    private String userIdentifierValue;

    public MyUser toUser() {
        return MyUser.builder().
                name(this.name).
                email(this.email).
                phoneNo(this.phoneNo).
                address(this.address).
                password(this.password).
                userIdentifier(this.userIdentifier).
                userIdentifierValue(this.userIdentifierValue).
                build();
    }

}
