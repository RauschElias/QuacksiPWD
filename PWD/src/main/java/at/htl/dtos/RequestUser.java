package at.htl.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUser {
    String username;
    String pwd;
    String pwdRepeat;
    String email;
}
