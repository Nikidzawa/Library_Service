package ru.nikidzawa.test;

import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.junit.jupiter.api.*;
import ru.nikidzawa.app.store.entities.ReaderEntity;

import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestTest {

    public static long repeat = 0;

    public static ReaderEntity reader = ReaderEntity.builder()
            .nickname("nikita")
            .name("nikita")
            .password("nikita")
            .mail("nikidzawa@mail.ru")
            .build();

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.basePath = "/api/";
    }

    @Test
    @Order(1)
    public void testCreateReader() {
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .post("readers/registration?name=" + reader.getName() + "&nickname=" + reader.getNickname() + "&password=" + reader.getPassword() + "&mail=" + reader.getMail())
                .then()
                .statusCode(200)
                .extract().response();
        repeat = Long.parseLong(response.path("id").toString());
        reader.setId(repeat);
    }

    @Test
    @Order(2)
    public void unAuthTest() {
        given()
                .when()
                .get("readers")
                .then()
                .statusCode(401);
    }

    @Test
    @Order(3)
    public void testSetAdmRole() {
        RestAssured.filters(new AuthFilter());
        given()
                .pathParam("nickname", reader.getNickname())
                .when()
                .patch("readers/{nickname}/setLibraryAdministratorRole")
                .then()
                .statusCode(200)
                .body("role", equalTo("ADMIN"));
    }

    @Test
    @Order(4)
    public void testCreateBook() {
        String name = "Test Book";
        String author = "Test Author";
        String description = "Test Description";

        given()
                .contentType(ContentType.JSON)
                .queryParam("name", name)
                .queryParam("author", author)
                .queryParam("description", description)
                .when()
                .post("books/create")
                .then()
                .statusCode(200)
                .body("name", equalTo(name))
                .body("author", equalTo(author))
                .body("description", equalTo(description))
                .body("owner", nullValue())
                .body("issue", nullValue())
                .body("deadLine", nullValue());
    }

    @Test
    @Order(5)
    public void testAllBooks() {
        given()
                .when()
                .get("books")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    @Order(6)
    public void testBookInfo() {
        given()
                .pathParam("bookId", repeat)
                .when()
                .get("books/{bookId}")
                .then()
                .statusCode(200)
                .body("name", notNullValue())
                .body("author", notNullValue())
                .body("description", notNullValue())
                .body("owner", nullValue())
                .body("issue", nullValue())
                .body("deadLine", nullValue());
    }

    @Test
    @Order(7)
    public void testEditBook() {
        String newName = "New Name";
        String newAuthor = "New Author";
        String newDescription = "New Description";

        given()
                .pathParam("bookId", repeat)
                .queryParam("name", newName)
                .queryParam("author", newAuthor)
                .queryParam("description", newDescription)
                .when()
                .patch("books/{bookId}/edit")
                .then()
                .statusCode(200)
                .body("name", equalTo(newName))
                .body("author", equalTo(newAuthor))
                .body("description", equalTo(newDescription))
                .body("owner", nullValue())
                .body("issue", nullValue())
                .body("deadLine", nullValue());
    }

    @Test
    @Order(8)
    public void testSetOwner() {
        long days = 7;

        given()
                .pathParam("bookId", repeat)
                .pathParam("readerNickname", reader.getNickname())
                .pathParam("days", days)
                .when()
                .patch("books/{bookId}/setOwner/{readerNickname}/{days}")
                .then()
                .statusCode(200)
                .body("name", notNullValue())
                .body("description", notNullValue())
                .body("author", notNullValue())
                .body("owner", equalTo(reader.getNickname()))
                .body("issue", notNullValue())
                .body("deadLine", notNullValue());
    }

    @Test
    @Order(9)
    public void testReaderBooks() {
        given()
                .pathParam("readerId", reader.getId())
                .when()
                .get("readers/{readerId}/books")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    @Order(10)
    public void testRemoveReader() {
        given()
                .pathParam("bookId", repeat)
                .when()
                .patch("books/{bookId}/removeOwner")
                .then()
                .statusCode(200)
                .body("name", notNullValue())
                .body("description", notNullValue())
                .body("author", notNullValue())
                .body("reader", nullValue())
                .body("issue", nullValue())
                .body("deadLine", nullValue());
    }

    @Test
    @Order(11)
    public void testDeleteBook() {
        given()
                .pathParam("bookId", repeat)
                .when()
                .delete("books/{bookId}/delete")
                .then()
                .statusCode(200)
                .body("message", equalTo("Книга удалена из базы данных"));
    }

    @Test
    @Order(12)
    public void testCheckingDeletionBook() {
        given()
                .when()
                .get("books")
                .then()
                .statusCode(404)
                .body("message", equalTo("Книги не найдены"));
    }

    @Test
    @Order(13)
    public void testAllReaders() {
        given()
                .when()
                .get("readers")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    @Order(14)
    public void testReaderInfo() {
        given()
                .pathParam("readerId", reader.getId())
                .when()
                .get("readers/{readerId}")
                .then()
                .statusCode(200)
                .body("nickname", notNullValue())
                .body("name", notNullValue())
                .body("mail", notNullValue())
                .body("role", equalTo("ADMIN"));
    }

    @Test
    @Order(15)
    public void testEditReader() {
        String newName = "Vladimir";
        String newNickname = "Lenin";

        given()
                .pathParam("readerId", reader.getId())
                .queryParam("name", newName)
                .queryParam("nickname", newNickname)
                .when()
                .patch("readers/{readerId}/edit")
                .then()
                .statusCode(200)
                .body("name", equalTo(newName))
                .body("nickname", equalTo(newNickname));

        reader.setName(newName);
        reader.setNickname(newNickname);
    }

    @Test
    @Order(16)
    public void deleteReader () {
        given()
                .pathParam("readerId", reader.getId())
                .when()
                .delete("readers/{readerId}/delete")
                .then()
                .statusCode(200)
                .body("message", equalTo("Читатель удалён"));
    }

    public static class AuthFilter implements Filter {
        @Override
        public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext context) {
            String credentials = reader.getNickname() + ":" + reader.getPassword();
            String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());

            requestSpec.header(new Header("Authorization", "Basic " + base64Credentials));

            return context.next(requestSpec, responseSpec);
        }
    }

}
