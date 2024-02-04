# Library Microservice
Полностью рабочий рест апи микросервис для библиотек.

Клиент и база данных развёрнуты в докер-контейнерах.

Внедрён spring security, а это означает, что у микросервиса есть версия для администраторов с полным функционалом 
по контролю за всем, а есть версия для обычных читателей, где они могут посмотреть информацию о себе и своих книгах, 
а так же посмотреть какие книги вообще есть в библиотеке.


Присутствует документация на Swagger, все ошибки обработаны.

Полный стек:
* Java 21
* Spring Boot
* Spring Data JPA
* Spring Security 6
* Docker
* Swagger
* postgreSQL
* Lombok

![screen.png](src%2Fmain%2Fresources%2Fimgs%2Fscreen.png)

![screen2.png](src%2Fmain%2Fresources%2Fimgs%2Fscreen2.png)