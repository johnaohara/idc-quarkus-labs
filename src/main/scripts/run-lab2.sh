
#!/bin/bash
set -e

script_dir=$(dirname $0)



############
## Settings
############

container_runtime=podman
container_network_name=host
container_stats_extra_settings="--no-reset"

container_db_name=postgresql

container_spring_name=spring-boot
container_spring_port=8080
container_spring_image=spring/todo

container_quarkus_jvm_name=quarkus-jvm
container_quarkus_jvm_port=8081
container_quarkus_jvm_image=quarkus-jvm/todo

container_quarkus_native_name=quarkus-native
container_quarkus_native_port=8082
container_quarkus_native_image=quarkus-native/todo

container_cpu_limit=4
container_memory_limit=512M

psql_db_host=localhost
psql_db_name=todo-db
psql_db_user=todo
psql_db_password=todo

echo "Using database: $psql_db_host"

if [ "$(uname)" == "Darwin" ]; then
  container_runtime=docker
  container_stats_extra_settings=""
  container_network_name=idc_demo_network
  psql_db_host=${container_db_name}
fi



############
## Functions
############
function run_database_script {
    local database_pod=$1
    local db_script=$2
    ${container_runtime} exec -i postgresql sh -c "echo \"$db_script\" | psql -U todo todo-db" > /dev/null
}

function create_database_container {
  printf "Starting PostgreSQL with name ${container_db_name} "
  ${container_runtime} run --ulimit memlock=-1:-1 -d --rm=true -p 5432:5432 \
        --network=${container_network_name} \
        --name ${container_db_name} \
        -e POSTGRES_USER=${psql_db_user} \
        -e POSTGRES_PASSWORD=${psql_db_password} \
        -e POSTGRES_DB=${psql_db_name} postgres:14 > /dev/null
  # Waiting for the database to start
  while ! (${container_runtime} exec -it ${container_db_name} psql -U ${psql_db_user} ${psql_db_name} -c "select 1" > /dev/null 2>&1)
  do
    sleep .2
    printf "."
  done
  echo "[DONE]"
}

function prepopulate_database {
  printf "Creating database tables\t "
  local _dtd_script=$(cat ${script_dir}/../sql/ddl.sql)
  run_database_script $1 "${_dtd_script}"
  echo "[DONE]"
  printf "Creating database content\t "
  local _import_script=$(cat ${script_dir}/../sql/data.sql)
  run_database_script $1 "${_import_script}"
  echo "[DONE]"
}

function create_container {
  local name=$1
  local image=$2
  local port=$3
  shift 3
  local env="$*"
 
  printf "Starting ${image} container using port ${port} "

  ${container_runtime} run -d --rm \
          --cpus=${container_cpu_limit} \
          --memory=${container_memory_limit} \
          -p ${port}:${port} \
          --network=${container_network_name} \
          --name=${name} \
          ${env} \
          ${image} > /dev/null

  while ! (curl -sf http://localhost:${port}/api > /dev/null)
  do
    sleep .2
    printf "."
  done
  echo "[DONE]"
}

#############
## Script
############

printf "Creating network for containers "
${container_runtime} network create ${container_network_name} > /dev/null 2>&1 || true
echo "[DONE]"


target_app=$1

if [ "${target_app}" == "db" ]; then
  create_database_container   ${container_db_name}
  prepopulate_database        ${container_db_name}
elif [ "${target_app}" == "spring" ]; then
  create_container ${container_spring_name} ${container_spring_image} ${container_spring_port} -e SPRING_HTTP_PORT=${container_spring_port} -e SPRING_DATASOURCE_URL=jdbc:postgresql://${psql_db_host}/${psql_db_name}
elif [ "${target_app}" == "quarkus-jvm" ]; then
  create_container ${container_quarkus_jvm_name} ${container_quarkus_jvm_image} ${container_quarkus_jvm_port} -e QUARKUS_HTTP_PORT=${container_quarkus_jvm_port} -e QUARKUS_DATASOURCE_URL=jdbc:postgresql://${psql_db_host}/${psql_db_name}
elif [ "${target_app}" == "quarkus-native" ]; then
  create_container ${container_quarkus_native_name} ${container_quarkus_native_image} ${container_quarkus_native_port} -e QUARKUS_HTTP_PORT=${container_quarkus_native_port} -e QUARKUS_DATASOURCE_URL=jdbc:postgresql://${psql_db_host}/${psql_db_name}
else
  echo Unknow target app ${target_app}
fi
