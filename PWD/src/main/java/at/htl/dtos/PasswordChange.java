package at.htl.dtos;

import at.htl.entity.WebUser;

import java.time.LocalDateTime;

public class PasswordChange {
    public String token;
    public WebUser user;
    public LocalDateTime validUntil;
}
