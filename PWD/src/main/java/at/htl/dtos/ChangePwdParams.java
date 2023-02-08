package at.htl.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePwdParams {
    String token;
    String pwd;
}
