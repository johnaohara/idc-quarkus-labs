UNAME := $(shell uname)

ifeq ($(UNAME), Linux)
	os=linux
endif
ifeq ($(UNAME), Darwin)
	os=mac
endif

.PHONY: all spring spring-vets quarkus-jvm quarkus-native quarkus-vets-jvm quarkus-vets-native build run clean stop

all: build run

build: spring-vets spring quarkus-jvm quarkus-native quarkus-vets-jvm quarkus-vets-native

spring:
	@$(MAKE) -C spring-todo build

spring-vets:
	@$(MAKE) -C spring-vets build

quarkus-jvm:
	@$(MAKE) -C quarkus-todo build-jvm

quarkus-native:
	@$(MAKE) -C quarkus-todo build-native

quarkus-vets-jvm:
	@$(MAKE) -C quarkus-vets build-jvm

quarkus-vets-native:
	@$(MAKE) -C quarkus-vets build-native

run: stop
	./src/main/scripts/run-$(os).sh

clean: 
	@$(MAKE) -C quarkus-todo clean
	@$(MAKE) -C spring-todo clean
	@$(MAKE) -C spring-vets clean

stop: 
	src/main/scripts/stop-$(os).sh





