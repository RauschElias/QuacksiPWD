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

}