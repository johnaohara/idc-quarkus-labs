#!/bin/bash
set -e
############
## Settings
############
container_spring_name=spring-boot
container_spring_port=8080
container_spring_image=spring/todo

container_quarkus_jvm_name=quarkus-jvm
container_quarkus_jvm_port=8081
container_quarkus_jvm_image=quarkus-jvm/todo

container_quarkus_native_name=quarkus-native
container_quarkus_native_port=8082
container_quarkus_native_image=quarkus-native/todo

container_spring_vets_name=spring-boot-vets
container_spring_vets_port=8083
container_spring_vets_image=spring/vets

container_quarkus_jvm_vets_name=quarkus-jvm-vets
container_quarkus_jvm_vets_port=8084
container_quarkus_jvm_vets_image=quarkus-jvm/vets

container_quarkus_native_vets_name=quarkus-native-vets
container_quarkus_native_vets_port=8085
container_quarkus_native_vets_image=quarkus-native/vets

container_cpu_limit=2
container_memory_limit=500M

psql_db_name=todo
psql_db_vets_name=vets
psql_db_user=todo
psql_db_password=todo

psql_db_host=192.168.0.70

DEBUG=false

############
## Functions
############
function create_database_container {
  printf "Starting PostgreSQL database $2 in pod $1 "
#  podman run --ulimit memlock=-1:-1 -d --rm=true --pod=$1-pod --memory-swappiness=0 --name $1-db -e POSTGRES_USER=${psql_db_user} -e POSTGRES_PASSWORD=${psql_db_password} -e POSTGRES_DB=$2 postgres:10.5 > /dev/null
  if [ "$DEBUG" = true ]; then echo ""; echo "podman run -d --rm=true --pod=$1-pod --memory-swappiness=0 --name $1-db -e POSTGRES_USER=${psql_db_user} -e POSTGRES_PASSWORD=${psql_db_password} -e POSTGRES_DB=$2 postgres:10.5"; fi;

  podman run -d --rm=true --pod=$1-pod --memory-swappiness=0 --name $1-db -e POSTGRES_USER=${psql_db_user} -e POSTGRES_PASSWORD=${psql_db_password} -e POSTGRES_DB=$2 postgres:10.5 > /dev/null
  # Waiting for the database to start
  while ! (podman exec -it $1-db psql -U ${psql_db_user} $2 -c "select 1" > /dev/null 2>&1)
  do
    sleep .2
    printf "."
  done
  echo "[DONE]"
}

function prepopulate_database {
  printf "Creating database tables and content "
  execute_db_statement $1 ${psql_db_name} "create table Todo (
        id int8 not null,
          completed boolean not null,
          ordering int4,
          title varchar(255),
          url varchar(255),
          primary key (id)
      )"
  execute_db_statement $1 ${psql_db_name} "create sequence hibernate_sequence start with 1 increment by 1"
  execute_db_statement $1 ${psql_db_name} "alter table if exists Todo drop constraint if exists unique_title_constraint"
  execute_db_statement $1 ${psql_db_name} "alter table if exists Todo add constraint unique_title_constraint unique (title)"
  execute_db_statement $1 ${psql_db_name} "INSERT INTO todo(id, title, completed, ordering, url) VALUES (nextval('hibernate_sequence'), 'Introduction to Quarkus', true, 0, null)"
  execute_db_statement $1 ${psql_db_name} "INSERT INTO todo(id, title, completed, ordering, url) VALUES (nextval('hibernate_sequence'), 'Hibernate with Panache', false, 1, null)"
  execute_db_statement $1 ${psql_db_name} "INSERT INTO todo(id, title, completed, ordering, url) VALUES (nextval('hibernate_sequence'), 'Visit Quarkus web site', false, 2, 'https://quarkus.io')"
  execute_db_statement $1 ${psql_db_name} "INSERT INTO todo(id, title, completed, ordering, url) VALUES (nextval('hibernate_sequence'), 'Star Quarkus project', false, 3, 'https://github.com/quarkusio/quarkus/')"
  echo "[DONE]"
}

function prepopulate_vets_database {
  printf "Creating vets database $1-db tables and content "
  execute_db_statement $1 ${psql_db_vets_name} "CREATE TABLE IF NOT EXISTS vets (
          id int8 not null,
          first_name varchar(30),
          last_name varchar(30),
          primary key (id)
        )"

  execute_db_statement $1 ${psql_db_vets_name} "CREATE INDEX vets_last_name on vets (last_name)"

  execute_db_statement $1 ${psql_db_vets_name} "CREATE TABLE IF NOT EXISTS specialties (
          id int8 not null,
          name varchar(80),
          primary key (id)
        )"
  execute_db_statement $1 ${psql_db_vets_name} "CREATE INDEX specialties_name on specialties (name)"


  execute_db_statement $1 ${psql_db_vets_name} "CREATE TABLE IF NOT EXISTS vet_specialties (
          vet_id int8 not null REFERENCES vets(id),
          specialty_id int8 not null REFERENCES specialties(id),
          UNIQUE (vet_id, specialty_id)
        )"

  execute_db_statement $1 ${psql_db_vets_name} "create sequence hibernate_sequence start with 1 increment by 1"
  execute_db_statement $1 ${psql_db_vets_name} "INSERT INTO vets (id, first_name, last_name) VALUES (1, 'James', 'Carter')"
  execute_db_statement $1 ${psql_db_vets_name} "INSERT INTO vets (id, first_name, last_name) VALUES (2, 'Helen', 'Leary')"
  execute_db_statement $1 ${psql_db_vets_name} "INSERT INTO vets (id, first_name, last_name) VALUES (3, 'Linda', 'Douglas')"
  execute_db_statement $1 ${psql_db_vets_name} "INSERT INTO vets (id, first_name, last_name) VALUES (4, 'Rafael', 'Ortega')"
  execute_db_statement $1 ${psql_db_vets_name} "INSERT INTO vets (id, first_name, last_name) VALUES (5, 'Henry', 'Stevens')"
  execute_db_statement $1 ${psql_db_vets_name} "INSERT INTO vets (id, first_name, last_name) VALUES (6, 'Sharon', 'Jenkins')"

  execute_db_statement $1 ${psql_db_vets_name} "INSERT INTO specialties (id, name) VALUES (1, 'radiology')"
  execute_db_statement $1 ${psql_db_vets_name} "INSERT INTO specialties (id, name) VALUES (2, 'surgery')"
  execute_db_statement $1 ${psql_db_vets_name} "INSERT INTO specialties (id, name) VALUES (3, 'dentistry')"

  execute_db_statement $1 ${psql_db_vets_name} "INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (2, 1)"
  execute_db_statement $1 ${psql_db_vets_name} "INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (3, 2)"
  execute_db_statement $1 ${psql_db_vets_name} "INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (3, 3)"
  execute_db_statement $1 ${psql_db_vets_name} "INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (4, 2)"
  execute_db_statement $1 ${psql_db_vets_name} "INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (5, 1)"


  echo "[DONE]"
}

function execute_db_statement {
  if [ "$DEBUG" = true ]; then echo ""; echo "podman exec -it $1-db psql -U ${psql_db_user} $2 -c \"$3\""; fi;
  podman exec -it $1-db psql -U ${psql_db_user} $2 -c "$3" > /dev/null
  printf "."
}

function create_pod {
  printf "Creating pod $1 using port $2 \t"
  if [ "$DEBUG" = true ]; then echo ""; echo "podman pod create --name=$1-pod -p $2"; fi;
  podman pod create --name=$1-pod -p $2 > /dev/null
  echo "[DONE]"
}

function create_container_in_pod {
  local name=$1
  local image=$2
  local port=$3
  local endpoint=$4
  shift 4
  local env="$*"
  
  printf "Starting ${image} container in pod ${name}-pod using port ${port} "
  if [ "$DEBUG" = true ]; then echo ""; echo "podman run -d --rm --cpus=${container_cpu_limit} --memory=${container_memory_limit} --pod=\"${name}-pod\" --name=${name} ${env} ${image}"; fi;
  podman run -d --rm --cpus=${container_cpu_limit} --memory=${container_memory_limit} --pod="${name}-pod" --name=${name} ${env} ${image} > /dev/null
  while ! (curl -sf http://localhost:${port}${endpoint} > /dev/null)
  do
    sleep .2
    printf "."
  done
  echo "[DONE]"
}

#############
## Script
############

# Setup pods and databases
create_pod                  ${container_quarkus_native_name}  ${container_quarkus_native_port}
#create_database_container   ${container_quarkus_native_name}  ${psql_db_name}
#prepopulate_database        ${container_quarkus_native_name}

create_pod                  ${container_quarkus_jvm_name}     ${container_quarkus_jvm_port}
#create_database_container   ${container_quarkus_jvm_name}     ${psql_db_name}
#prepopulate_database        ${container_quarkus_jvm_name}

create_pod                  ${container_spring_name}          ${container_spring_port}
#create_database_container   ${container_spring_name}          ${psql_db_name}
#prepopulate_database        ${container_spring_name}

create_pod                  ${container_spring_vets_name}     ${container_spring_vets_port}
#create_database_container   ${container_spring_vets_name}     ${psql_db_vets_name}
#prepopulate_vets_database   ${container_spring_vets_name}

create_pod                  ${container_quarkus_jvm_vets_name}     ${container_quarkus_jvm_vets_port}
#create_database_container   ${container_quarkus_jvm_vets_name}     ${psql_db_vets_name}
#prepopulate_vets_database   ${container_quarkus_jvm_vets_name}

create_pod                  ${container_quarkus_native_vets_name}     ${container_quarkus_native_vets_port}
#create_database_container   ${container_quarkus_native_vets_name}     ${psql_db_vets_name}
#prepopulate_vets_database   ${container_quarkus_native_vets_name}


# Starting the cloud native runtime and wait for first response
create_container_in_pod ${container_quarkus_native_name} ${container_quarkus_native_image} ${container_quarkus_native_port} "/" -e QUARKUS_HTTP_PORT=${container_quarkus_native_port} -e QUARKUS_DATASOURCE_URL=jdbc:postgresql://${psql_db_host}/${psql_db_name}
create_container_in_pod ${container_quarkus_jvm_name} ${container_quarkus_jvm_image} ${container_quarkus_jvm_port} "/" -e QUARKUS_HTTP_PORT=${container_quarkus_jvm_port} -e QUARKUS_DATASOURCE_URL=jdbc:postgresql://${psql_db_host}/${psql_db_name}
create_container_in_pod ${container_spring_name} ${container_spring_image} ${container_spring_port} "/" -e SPRING_HTTP_PORT=${container_spring_port} -e SPRING_DATASOURCE_URL=jdbc:postgresql://${psql_db_host}/${psql_db_name}
create_container_in_pod ${container_spring_vets_name} ${container_spring_vets_image} ${container_spring_vets_port} "/vets" -e SPRING_HTTP_PORT=${container_spring_vets_port} -e SPRING_DATASOURCE_URL=jdbc:postgresql://${psql_db_host}/${psql_db_vets_name}
create_container_in_pod ${container_quarkus_jvm_vets_name} ${container_quarkus_jvm_vets_image} ${container_quarkus_jvm_vets_port} "/vets" -e QUARKUS_HTTP_PORT=${container_quarkus_jvm_vets_port} -e QUARKUS_DATASOURCE_URL=jdbc:postgresql://${psql_db_host}/${psql_db_vets_name}
create_container_in_pod ${container_quarkus_native_vets_name} ${container_quarkus_native_vets_image} ${container_quarkus_native_vets_port} "/vets" -e QUARKUS_HTTP_PORT=${container_quarkus_native_vets_port} -e QUARKUS_DATASOURCE_URL=jdbc:postgresql://${psql_db_host}/${psql_db_vets_name}

echo "Displaying stats for containers: "
  if [ "$DEBUG" = true ]; then echo ""; echo "podman stats --no-stream $(test \"podman\" = \"podman\" && echo \"--no-reset\") ${container_spring_name} ${container_quarkus_jvm_name} ${container_quarkus_native_name}"; fi;

podman stats --no-stream $(test "podman" = "podman" && echo "--no-reset") ${container_spring_name} ${container_quarkus_jvm_name} ${container_quarkus_native_name} ${container_spring_vets_name} ${container_quarkus_jvm_vets_name} ${container_quarkus_native_vets_name}

