
# Hospital Vida Salud - T2 - Sebastian Sernaque Pachas

## Descripción del proyecto

Este proyecto consiste en desarrollar un sistema para el Hospital Vida Salud, con el objetivo de organizar algunos de sus procesos administrativos, como el registro de pacientes, la programación de citas y el registro de atenciones.

La solución está dividida en varios microservicios. Cada uno se encarga de una función determinada y se comunica con los demás cuando es necesario. De esta manera, el sistema puede organizarse por partes y facilitar los cambios y el mantenimiento.

El proyecto se desarrolla con fines académicos y utiliza información ficticia para las pruebas.

## Objetivos

- Organizar las funciones principales del sistema hospitalario.
- Permitir el registro, consulta y actualización de información de pacientes.
- Gestionar las citas y las atenciones registradas.
- Facilitar la comunicación entre los diferentes servicios.
- Guardar la información en una base de datos.
- Realizar comprobaciones automáticas del código mediante GitHub Actions.

## Microservicios del proyecto

El sistema está compuesto por los siguientes siete proyectos:

- **Eureka:** permite registrar los servicios y facilita que puedan encontrarse entre sí.
- **Gateway:** recibe las solicitudes y las dirige al servicio correspondiente.
- **Pacientes:** administra la información de los pacientes.
- **Citas:** se encarga de gestionar las citas médicas.
- **Notificaciones:** permite gestionar los avisos generados por el sistema.
- **Auditoría:** permite registrar información sobre las operaciones y los eventos importantes.
- **Atenciones:** administra los registros de las atenciones relacionadas con los pacientes y las citas.

Cada microservicio cuenta con su propio proyecto y archivo `pom.xml`.


## Arquitectura

El sistema está organizado en varios microservicios. Las solicitudes ingresan por el Gateway, que las dirige al servicio correspondiente. Eureka permite registrar y localizar los servicios. Los microservicios gestionan sus propias funciones y utilizan la base de datos para guardar la información. RabbitMQ permite enviar mensajes entre servicios, mientras que Notificaciones y Auditoría pueden procesar los eventos que correspondan.

La siguiente imagen de texto muestra la organización general del proyecto:

```text
                      USUARIO 
                         |
                         v
                 +----------------+
                 |    GATEWAY     |
                 +----------------+
                         |
                         v
                 +----------------+
                 |     EUREKA     |
                 +----------------+
                         |
          +--------------+--------------+
          |              |              |
          v              v              v
   +-------------+ +-------------+ +-------------+
   |  PACIENTES  | |    CITAS    | | ATENCIONES  |
   +-------------+ +-------------+ +-------------+
          |              |              |
          +--------------+--------------+
                         |
                         v
              +---------------------+
              |    SQL + R2DBC      |
              +---------------------+

   Comunicación mediante mensajes, cuando corresponda:

              +---------------------+
              |      RABBITMQ       |
              +---------------------+
                         |
                  +------+------+
                  |             |
                  v             v
          +---------------+ +---------------+
          | NOTIFICACIONES| |   AUDITORÍA   |
          +---------------+ +---------------+

   Integración continua:

   +--------+      +-----------------+      +----------------+
   |  GIT   | ---> | GITHUB ACTIONS  | ---> | COMPILACIÓN Y  |
   | GITHUB |      |                 |      | PRUEBAS        |
   +--------+      +-----------------+      +----------------+
```

Este diagrama presenta los componentes principales del sistema. La conexión a la base de datos, el uso de RabbitMQ y la comunicación entre los servicios deben corresponder a la configuración realmente implementada en el proyecto.
## Tecnologías utilizadas

- Java 25.
- Spring Boot para desarrollar los servicios.
- Spring Cloud Gateway para dirigir las solicitudes.
- Eureka Discovery Server para registrar y localizar los servicios.
- Spring WebFlux para trabajar con operaciones reactivas.
- Spring Data R2DBC para acceder a la base de datos de forma reactiva.
- Mono y Flux para manejar resultados reactivos.
- MySQL como sistema de base de datos.
- RabbitMQ para la comunicación mediante mensajes.
- Maven para administrar las dependencias y compilar los proyectos.
- Git y GitHub para controlar las versiones del código.
- GitHub Actions para automatizar las verificaciones del proyecto.

## Pruebas automatizadas

Se implementaron pruebas automatizadas en los microservicios de Pacientes, Citas y Atenciones.

Estas pruebas permiten comprobar el comportamiento de determinadas funciones sin tener que realizar todas las verificaciones manualmente.

Los otros microservicios también forman parte del proceso de compilación configurado en GitHub Actions. La ejecución de las pruebas automatizadas depende de los servicios que tienen pruebas implementadas en el proyecto.

## Integración continua con GitHub Actions

El proyecto cuenta con un flujo de integración continua configurado en el archivo `.github/workflows/ci.yml`.

Cuando se suben cambios al repositorio, GitHub Actions ejecuta las tareas configuradas para compilar los microservicios y realizar las pruebas correspondientes. También se contempla la ejecución del flujo cuando se crea una solicitud de integración dirigida a la rama principal `master`.

Si una de las tareas falla, la ejecución aparece como fallida y permite revisar los registros para encontrar el problema. Después de realizar las correcciones, se pueden volver a subir los cambios y comprobar el resultado.

El objetivo es detectar errores durante el desarrollo y verificar que los cambios no generen problemas en las tareas automatizadas.

## Requisitos para ejecutar el proyecto

Antes de ejecutar los microservicios, se necesita tener instalado y configurado lo siguiente:

- JDK compatible con Java 25.
- Maven.
- MySQL y la base de datos configurada para los servicios que la utilizan.
- RabbitMQ para las funciones que dependan de la mensajería.
- Eclipse u otro entorno de desarrollo compatible con proyectos Java.

También se deben revisar los puertos, las direcciones de conexión y las variables de configuración de cada microservicio.

## Cómo compilar y ejecutar

Cada microservicio tiene su propia carpeta. Para compilar un servicio, se debe abrir una terminal dentro de esa carpeta y ejecutar:

```bash
mvn clean package
```

Para ejecutar las pruebas automatizadas de los servicios que las tienen implementadas, se puede utilizar:

```bash
mvn verify
```

Los microservicios deben iniciarse según sus dependencias y la configuración del proyecto. Antes de probar las operaciones, se debe verificar que los servicios necesarios estén activos y que la conexión con la base de datos funcione correctamente.

## Repositorio

El código fuente del proyecto se encuentra disponible en GitHub:

https://github.com/max130qt-sys/hospital-vida-salud-t2

## Curso

Desarrollo de Aplicaciones Web II - Evaluación T2

Caso de estudio: Hospital Vida Salud.
