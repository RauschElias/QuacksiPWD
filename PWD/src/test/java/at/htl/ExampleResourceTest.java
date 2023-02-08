package at.htl;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wildfly.common.Assert;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ExampleResourceTest {
    @Inject
    PwdService service;

    @Test
    public void testLogin() {
        given()
                .body("{\"username\":\"thomas\", \"pwd\":\"test\", \"pwdRepeat\":\"test\", \"email\":\"thomas@test.at\"}")
                .header("content-type", "application/json").when()
                .post("/service/register")
                .then()
                .statusCode(200)
                .body(is("thomas created"));

        given()
                .body("{\"username\":\"thomas\", \"pwd\":\"test\"}")
                .header("content-type", "application/json").when()
                .post("/service/login")
                .then()
                .statusCode(200)
                .body(is("logged in"));

        given()
                .body("{\"username\":\"thomas\", \"pwd\":\"d\", \"pwdRepeat\":\"d\", \"email\":\"thomas@test.at\"}")
                .header("content-type", "application/json").when()
                .post("/service/login")
                .then()
                .statusCode(200)
                .body(is("Password is wrong"));

        given()
                .body("{\"username\":\"d\", \"pwd\":\"adasdasd\", \"pwdRepeat\":\"adasdasd\", \"email\":\"thomas@test.at\"}")
                .header("content-type", "application/json").when()
                .post("/service/login")
                .then()
                .statusCode(200)
                .body(is("User does not exist"));

    }

    @Test
    public void testAddNewUser() {
        given()
                .body("{\"username\":\"robert\", \"pwd\":\"test\", \"pwdRepeat\":\"test\", \"email\":\"robert@test.at\"}")
                .header("content-type", "application/json").when()
                .post("/service/register")
                .then()
                .statusCode(200)
                .body(is("robert created"));
    }

    @Test
    public void testChangePWD() {

        given()
                .body("{\"username\":\"robert2\", \"pwd\":\"test\", \"pwdRepeat\":\"test\", \"email\":\"robert@test.at\"}")
                .header("content-type", "application/json").when()
                .post("/service/register")
                .then()
                .statusCode(200)
                .body(is("robert2 created"));

        given()
                .body("{\"username\":\"robert3\"}")
                .header("content-type", "application/json").when()
                .get("/service/reset")
                .then()
                .statusCode(200)
                .body(is("Username existiert nicht"));

        given()
                .body("{\"username\":\"robert2\"}")
                .header("content-type", "application/json").when()
                .get("/service/reset")
                .then()
                .statusCode(200)
                .body(is("Ihnen wurde eine Email gesendet"));

        given()
                .body("{\"token\":\"ssds\",\"pw\":\"t\"}")
                .header("content-type", "application/json").when()
                .patch("/service/reset/")
                .then()
                .statusCode(200)
                .body(is("Unknown token"));

        given()
                .body("{\"pw\":t}")
                .header("content-type", "application/json").when()
                .patch("/service/reset/" + service.getPasswordChanges().get(0))
                .then()
                .statusCode(200)
                .body(is("Password wurde geändert"));
    }

    @Test
    public void hurensohn(){
        Assertions.assertTrue(service.userExists("robert2"));
    }
}

