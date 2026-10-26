# **Galagraft**
> *Videojuego arcade tipo Galaga con temática Minecraft, desarrollado en Java con el motor LibGDX.*

<img src="https://img.shields.io/badge/version-1.0.0-blue" alt="version"> [![Last Commit](https://img.shields.io/github/last-commit/visceralcricket/Galagraft-game/main)](https://github.com/visceralcricket/Galagraft-game/tree/main)

## **Premisa**
* **"Galagraft"** es un videojuego estilo *shoot 'em up* arcade fuertemente inspirado en Galaga donde el jugador controla a un Shulker. El objetivo es defender tu End City destruyendo oleadas de enemigos (mobs de Minecraft) que bajan por la pantalla para avanzar de ronda. Cada ronda aumenta la velocidad y la cantidad de enemigos.

## **Cómo compilar y ejecutar el juego**

### Requisitos
* JDK 8 hasta JDK 17 (el proyecto compila con compliance 1.8 y el *wrapper* de Gradle soporta hasta la 17).
* No hace falta instalar Gradle a mano: el proyecto incluye el *wrapper* (`gradlew` / `gradlew.bat`). Atención: la primera vez que se ejecute va a descargar LibGDX y sus dependencias, por lo que requiere internet.

### Importante: Directorio de trabajo (*Working Directory*)
Si vas a correr el juego desde un IDE, asegúrate de que el directorio de trabajo esté apuntando a la carpeta `assets/`. De lo contrario, el juego va a crashear al iniciar porque no encontrará las texturas ni los sonidos. Si lo corres por consola con el comando de abajo, Gradle lo hace automático.
En **macOS**, tienes que agregar el argumento `-XstartOnFirstThread` a la JVM.

### Opción A: Ejecutar desde línea de comandos (Recomendado)
Abre la terminal en la raíz del proyecto y ejecuta:

```bash
# Linux / macOS
./gradlew desktop:run

# Windows
gradlew.bat desktop:run

# Si quieres generar un .jar ejecutable (queda guardado en desktop/build/libs/)
./gradlew desktop:dist
```

### Opción B: Ejecutar desde Eclipse
1. `File → Import... → Gradle → Existing Gradle Project`.
2. Una vez cargado, clic derecho sobre `desktop/src/com/pdjteam/galagraft/DesktopLauncher.java` → `Run As → Java Application`.
3. Recuerda revisar la configuración de ejecución (Run Configurations) para confirmar que el working directory es `escu-track/assets/` (o la ruta donde tengas el repositorio).

## **Distribución de directorios**
<pre><code><i><span style="color: #00fed4ed;">¿Cómo se organiza el código?</span></i></code></pre>

* <code><b><span style="color: #23c523d4;">core/src/com/pdjteam/galagraft/</span></b></code>: Módulo principal donde vive toda la lógica del juego.
    * <code><b><span style="color: #f43009da;">Galagraft.java</span></b></code>: Clase orquestadora principal (extiende `Game`).
    * <code><b><span style="color: #009dff;">config/</span></b></code>: Configuraciones generales y constantes.
    * <code><b><span style="color: #009dff;">engine/entities/</span></b></code>: Clases del modelo del juego (EntidadDinamica, Shulker, Enemigos, Proyectiles).
    * <code><b><span style="color: #009dff;">strategies/</span></b></code>: Lógica de disparos y power-ups aplicando patrón Strategy.
    * <code><b><span style="color: #009dff;">view/</span></b></code>: Todo lo relacionado a las pantallas (Menú, Juego, GameOver).

* <code><b><span style="color: #23c523d4;">desktop/src/</span></b></code>: Punto de entrada para escritorio (LWJGL3).
* <code><b><span style="color: #23c523d4;">assets/</span></b></code>: Texturas, sprites, efectos de sonido y música de fondo.

## **Requerimientos (GMs) implementados**
``` diff
+ GM-1 / GM-2: Explicación y análisis de reestructuración del juego base aplicados.
+ GM-3: Diseño UML y separación MVC (GameLogic vs PantallaJuego).
+ GM-4: Uso de clase abstracta (EntidadDinamica) e interfaz (Colisionable).
+ GM-5: Estricto encapsulamiento (atributos private con getters/setters lógicos).
+ GM-6: Patrón Singleton implementado en el Shulker (jugador único).
+ GM-7: Patrón Strategy aplicado en los tipos de disparo (DisparoSimple, DisparoTriple).
+ GM-8: Patrón Template Method en la clase Enemigo para definir el ciclo de actualización base.
- GM-9 [PENDIENTE]: Patrón Builder diseñado para ser utilizado en la instanciación de las rondas/oleadas de enemigos.
+ GM-10: Uso extensivo de Github con ramas y múltiples commits.
```

## **Problemas conocidos**
```diff
@@ A nivel de lógica de procedimientos / sistemas @@
@@ | -- Ninguno registrado aún. -- | @@
```

# **Changelog - Galagraft**
<small>*Nota: Este changelog utiliza fechas en ISO estándar: YY-MM-DD.*</small>

## [1.0.0] - 2026-10-25
> Versión funcional con la mayoría de requerimientos del proyecto y mejoras QoL implementados.

### Añadido
+ Patrones de diseño implementados en su totalidad (Strategy para disparos, Singleton para el Shulker, Template Method para enemigos).
+ Añadidos distintos tipos de enemigos (mobs de Minecraft) con comportamientos específicos.
+ Sistema de Power-Ups implementado (tecla E de prueba para activarlo) -> actualmente solo existe el power-up de disparo triple con mayor cadencia de disparo.
+ HUD completo con contador de vidas, puntuación actual y récord.
+ Fondo de pantalla para GameOver y de fondo durante la partida además de múltiples pistas de audio y efectos que afectan la calidad de la experiencia.
+ Música de fondo durante la partida.

### Cambios
+ Se desacopló toda la lógica matemática y de físicas de `PantallaJuego` hacia `GameLogic`, respetando el Principio de Responsabilidad Única (SRP).
+ Se arregló el bug donde se generaba una ronda nueva con 0 vidas al morir por un choque directo.
+ Arreglado el crash de OpenGL al presionar ESC (ahora LibGDX cierra los procesos del `SpriteBatch` de forma segura).
+ Modificada cadencia de disparo del jugador e implementado cooldown/tiempo de espera para evitar el spamming/tecleo descontrolado al disparar a los enemigos.

## [0.5.0] - 2026-08-30
> Reestructuración del juego base de asteroides a Galagraft.

### Añadido
+ Añadida clase `Shulker` y soporte para proyectiles propios.
+ Enemigos reconfigurados para moverse en oleadas estilo Galaga (de arriba hacia abajo).
+ Agregados assets iniciales de Minecraft para reemplazar texturas de los asteroides.
+ Añadidos sprites creados a mano tanto para el jugador y sus proyectiles como para los enemigos.

### Cambios
+ Se eliminó el rebote entre enemigos para mantener el estilo de juego arcade requerido.
+ La nave ahora se mueve exclusivamente de forma lateral (A/D o flechas) con velocidad constante, eliminando la inercia del juego original.

## [0.1.0] - 2026-08-15
> Repositorio inicial.
+ Clonación del juego base de asteroides proporcionado para el proyecto.
+ Configuración inicial de Gradle y adaptación del entorno de trabajo.