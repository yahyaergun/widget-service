# Widget Service
### What
API for widgets

### How
Keeps `z` index of the widgets unique, moves the widgets around in the case of collisions.

Repository implemented with an in memory solution and with a postgres database. 
Implementation to be wired depends on the  `application.repository.type` in `application.yml`
- `db` is the value for database implementation 
- `memory` is the value for in memory implementation 

Integration tests run with H2 embedded.


Application is dockerized, docker-compose consists of applicaton and postgres database.

`./start.sh` can be used to start the application and postgres database. Shortcut for:
    
    - mvn clean install 
    - docker-compose build
    - docker-compose up

### Features
- Spring Boot
- Spring Web
- Spring Data JPA
- Lombok
- PostgreSQL
- Jacoco is used for test coverage.