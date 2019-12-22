# Widget Service
### What
API for widgets

### How
Keeps `z` index of the widgets unique, moves the widgets around in the case of collisions.

Repository implemented with an in memory solution and with a H2 embedded database. 
Implementation to be wired depends on the  `application.repository.type` in `application.yml`
- `h2` is the value for database implementation 
- `memory` is the value for in memory implementation 

### Features
- Spring Boot
- Spring Web
- Spring Data JPA
- Lombok
- H2 (embedded)
- Jacoco is used for test coverage.