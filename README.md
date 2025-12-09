# TejoBar - Spring Boot Migration

## Requisitos
- Java 21 (JDK 21)
- MySQL Server (Base de datos `tejobar` creada)

## Cómo abrir en IntelliJ IDEA
1.  Abre IntelliJ IDEA.
2.  Selecciona **Open** (Abrir).
3.  Navega a la carpeta `c:/Users/ASUS/Downloads/tejo_bar/tejo_bar` y selecciona el archivo `build.gradle`.
4.  Selecciona **Open as Project** (Abrir como proyecto).
5.  Espera a que IntelliJ descargue las dependencias e indexe el proyecto.

## Cómo ejecutar la aplicación
### Opción A: Desde IntelliJ
1.  Busca el archivo `src/main/java/com/tejobar/tejo_bar/TejoBarApplication.java`.
2.  Haz clic derecho sobre el archivo o sobre la clase `TejoBarApplication`.
3.  Selecciona **Run 'TejoBarApplication'**.

### Opción B: Desde la terminal
1.  Abre una terminal en la carpeta del proyecto.
2.  Ejecuta el comando:
    ```bash
    ./gradlew bootRun
    ```

## Verificar
Abre tu navegador y ve a: [http://localhost:8080](http://localhost:8080)
