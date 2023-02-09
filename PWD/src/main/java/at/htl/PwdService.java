package at.htl;

import com.google.common.hash.Hashing;
import at.htl.entity.WebUser;
import lombok.Getter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@ApplicationScoped
public class PwdService {

    // in memory list for password changes
    // as this simple structure doesn't require persisted password change tokens
    @Getter
    List<PasswordChange> passwordChanges = new LinkedList<>();

    public static final String saltAdditive = "_this_is_my_secure_salt_additive_no_one_is_ever_gonna_find_it_23p07640973609764=/&$=/&=/)$&";

    @Inject
    EntityManager manager;
    @Inject
    Validator validator;


    private String hashPwd(WebUser user) {
        return getHash(user.getPwSalt() + saltAdditive + user.getPwd());
    }

    public WebUser getUser(String name) {
        List<WebUser> u = manager.createNamedQuery("getUserByName", WebUser.class).setParameter("name",name).getResultList();
        return u.get(0);
    }

    private String  getHash(String hash) {
        return Hashing.sha512().hashString(hash,StandardCharsets.UTF_8).toString();
    }

    public String addNewUser(String username, String pwd, String email) {
        WebUser newUser = new WebUser();
        newUser.setUserName(username);
        Random rnd = new Random();
        byte[] rndBytes = new byte[16];
        rnd.nextBytes(rndBytes);
        newUser.setPwSalt(new String(Base64.getEncoder().encode(rndBytes)));
        newUser.setPwd(pwd);
        newUser.setPwd(hashPwd(newUser));
        newUser.setEmail(email);

        try {
            manager.persist(newUser);
        } catch (ConstraintViolationException e){
            return e.getMessage();
        }

        System.out.println("created with pw: " + newUser.getPwd());
        return newUser.getUserName() + " created";
    }

    public String requestPasswordChange(String username) {
        PasswordChange ch = new PasswordChange();
        List<WebUser> u =manager.createNamedQuery("getUserByName", WebUser.class).setParameter("name",username).getResultList();
        System.out.println(u);
        ch.user = u.get(0);
        ch.validUntil = LocalDateTime.now().plusMinutes(15);
        Random rnd = new Random();
        byte[] rndBytes = new byte[16];
        rnd.nextBytes(rndBytes);
        String token = new String(Base64.getEncoder().encode(rndBytes));
        ch.token = token;
        ch.validUntil = LocalDateTime.now().plusMinutes(5);
        System.out.println("pwchange " + ch);
        passwordChanges.add(ch);
        return token;
    }

    public boolean changePwd(String token, String pw) {
        for (PasswordChange ch: passwordChanges) {
            if (ch.token.equals(token)
                    && ch.validUntil.toEpochSecond(ZoneOffset.UTC)
                    >= LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
                        ch.user.setPwd(pw);
                        ch.user.setPwd(hashPwd(ch.user));
                        manager.persist(ch.user);
                        return true;
            }
        }
        return false;
    }

    public boolean userExists(String username) {
        List<WebUser> u =manager.createNamedQuery("getUserByName",WebUser.class).setParameter("name",username).getResultList();
        System.out.println(u.size());
        return u.size() >= 1;
    }

    public String login(String name, String password) {
        List<WebUser> queryResult = manager.createNamedQuery("getUserByName",WebUser.class).setParameter("name",name).getResultList();

        if (queryResult.isEmpty())
            return "User does not exist";

        WebUser user = queryResult.get(0);
        String correctPassword = user.getPwd();
        user.setUserName(name);
        user.setPwd(password);
        user.setPwd(hashPwd(user));

        Result validationResult = validate(user);
        String errors = "";
        errors += validationResult.getMessage();

        System.out.println("pwd: " + user.getPwd() + "   correctPWD: " + correctPassword);

        if (errors.isEmpty())
            return errors;
        else if(!user.getPwd().equals(correctPassword)){
            return "Password is wrong";
        }

        return "logged in";
    }

    public Result validate(WebUser newUser){
        Set<ConstraintViolation<WebUser>> violations = validator.validate(newUser);
        if (violations.isEmpty()){
            return new Result("");
        }
        else{
            return new Result(violations);
        }
    }
}