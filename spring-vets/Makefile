UNAME := $(shell uname)

ifeq ($(UNAME), Linux)
	os=linux
endif
ifeq ($(UNAME), Darwin)
	os=mac
endif

.PHONY: build build-mac build-linux clean

build: build-$(os)

build-mac: target/vets.jar
	docker build -q -f src/main/docker/Dockerfile -t spring/vets .

build-linux: target/vets.jar
	podman build -q -f src/main/docker/Dockerfile -t spring/vets .

target/vets.jar:
	./mvnw package


clean:
	./mvnw clean




