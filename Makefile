

install:
	@mvn clean install

server:
	@mvn spring-boot:run

test:
	@mvn clean test
