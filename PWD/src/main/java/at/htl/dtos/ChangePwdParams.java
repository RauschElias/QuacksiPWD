package at.htl.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChangePwdParams {
    String token;
    String pwd;
}
