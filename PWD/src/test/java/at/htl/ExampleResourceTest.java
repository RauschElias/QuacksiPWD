package at.htl;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ExampleResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .body("{\"username\":\"robert\", \"pwd\":\"test\", \"pwdRepeat\":\"test\", \"email\":\"robert@test.at\"}")
                .header("content-type", "application/json").when()
                .post("/service/register")
                .then()
                .statusCode(200)
                .body(is("robert created"));
    }

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
    public void testRequestChangePWD() {
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
                .body("{\"username\":\"robert\", \"pwd\":\"test\", \"pwdRepeat\":\"test\", \"email\":\"robert@test.at\"}")
                .header("content-type", "application/json").when()
                .post("/service/register")
                .then()
                .statusCode(200)
                .body(is("robert created"));
    }

}