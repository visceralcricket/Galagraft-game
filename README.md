# **Galagraft**
> *Videojuego arcade tipo Galaga con temática Minecraft, desarrollado con LibGDX a partir de un juego base de asteroides.*

<img src="https://img.shields.io/badge/version-1.0-blue" alt="version">

## **Premisa**
* **"Galagraft"** es un videojuego estilo arcade (inspirado en Galaga) donde el jugador controla un Shulker que debe destruir todos los enemigos de la pantalla para avanzar de ronda. Cada ronda trae más enemigos y a mayor velocidad.
* Reglas actuales: 3 vidas, +10 puntos por enemigo destruido, una pausa breve tras recibir un golpe (la nave tiembla, es invulnerable y asteroides y balas se detienen unos 50 cuadros) y un récord (*HighScore*) que se conserva solo mientras el programa está abierto.
* Controles: flechas (`←` `→` `↑` `↓`) para cambiar la velocidad de la nave (cada pulsación suma o resta 1 a la velocidad, con inercia) y `ESPACIO` para disparar. En el menú y en *Game Over*, cualquier tecla o clic continúa. 

> **Nota sobre este documento:** Los controles necesitan revisarse para que sean mas comodos para el usuario,  cambiarlo a "AD"
para moverse laterlamente y tener una velocidad constante

## **Visión del juego nuevo**
> El proyecto actual es un **juego base** de asteroides. Se usará como punto de partida para crear un **juego nuevo**; al mismo tiempo se corregirá el juego base y se aplicarán los patrones y principios de la evaluación.

### Del juego base al juego nuevo
* **Nave:** de nave genérica a un **Shulker** (la clase `Shulker` ya existe).
* **Enemigos:** de asteroides (`Ball2`) a **mobs de Minecraft**.
* **Estilo Galaga:** en cada ronda baja una oleada de enemigos.
* **Potenciadores:** objetos que mejoran al Shulker durante un tiempo.
* **Se mantiene:** rondas, vidas, puntaje y récord.

### Mecánicas objetivo
* **Shulker:** se mueve solo hacia los lados con `A` / `D` a velocidad constante (hoy usa las 4 flechas, con inercia) y dispara con `ESPACIO`.
* **Enemigos:** avanzan de forma constante hacia los lados y hacia abajo, y ya no rebotan entre sí (hoy sí lo hacen).
* **Rondas:** cada ronda trae una oleada nueva, más difícil que la anterior.
* **Potenciadores:** los enemigos pueden soltarlos al morir y el Shulker los recoge. Ejemplos propuestos: disparo doble o triple, escudo, vida extra, disparo teledirigido.

### Decisiones por definir
* Qué mobs de Minecraft se usarán y cómo se comportará cada uno (en este documento, Creeper, Zombie y Esqueleto son solo ejemplos).
* Condición de derrota: llegar al borde inferior, chocar con el Shulker, o ambas.
* Lista final de potenciadores y su duración.
* Si habrá un jefe cada cierto número de rondas.
* Origen de los sprites: los personajes de Minecraft pertenecen a Mojang/Microsoft; conviene documentar la fuente o usar arte propio.

## **Plan de trabajo**
> Orden: primero se corrige el juego base, después se refactoriza y por último se construye el juego nuevo. Al terminar cada punto, cambiar `-` por `+`.

```diff
@@ Fases @@
- F0 Corregir el juego base: errores [ALTO] y los [MEDIO] que sobreviven al rediseño.
- F1 Refactorizar: clases abstractas, interfaces, Singleton, Template Method, Builder y Strategy.
- F2 Juego nuevo: Shulker con A/D, mobs de Minecraft, oleadas por ronda y potenciadores.
- F3 Pulido: sprites y sonidos, balance, pausa, récord persistente y README final.

@@ Criterios de evaluación a implementar @@
- Aplicación de encapsulamiento y principios de orientación a objetos
- Aplicación del patrón de diseño Singleton
- Aplicación del patrón de diseño Strategy
- Aplicación del patrón de diseño Template Method
- Aplicación del patrón de diseño Builder
```

## **Cómo compilar y ejecutar el programa**

### Requisitos
* JDK 8 a 17 (el proyecto compila con compliance 1.8 y el *wrapper* usa Gradle 7.3.3, que soporta hasta JDK 17).
* No hace falta instalar Gradle: se usa el *wrapper* incluido (`gradlew` / `gradlew.bat`). La primera ejecución descarga LibGDX 1.11.0 (backend LWJGL3), por lo que requiere internet.

### Opción A: Ejecutar desde Eclipse
1. `File → Import... → Gradle → Existing Gradle Project` (el proyecto incluye la configuración de Buildship).
2. Clic derecho sobre `desktop/src/com/pdjteam/galagraft/DesktopLauncher.java` → `Run As → Java Application`.

### Opción B: Ejecutar por línea de comandos
Desde la carpeta raíz del proyecto:

```bash
# Linux / macOS
./gradlew desktop:run

# Windows
gradlew.bat desktop:run

# Generar un .jar ejecutable (queda en desktop/build/libs/)
./gradlew desktop:dist
```

### Notas sobre plataforma y *assets*
* El módulo `desktop` declara `assets/` como carpeta de recursos y la tarea `run` fija ese directorio como directorio de trabajo. Si se ejecuta de otra forma, el **directorio de trabajo debe ser `assets/`**, de lo contrario las texturas y sonidos no se encuentran.
* En **macOS** la aplicación debe iniciarse con el argumento de JVM `-XstartOnFirstThread` (la tarea `run` de Gradle ya lo agrega).

## **Distribución de directorios**
<pre><code><i><span style="color: #00fed4ed;">Cómo se organiza el código?</span></i></code></pre>

* <code><b><span style="color: #23c523d4;">core/src/</span></b></code>: Módulo principal. Contiene la lógica del juego, independiente de la plataforma.

  * <code><b><span style="color: #009dff;">com/pdjteam/galagraft/</span></b></code>

    * <code><b><span style="color: #f43009da;">Galagraft.java</span></b></code>: Clase principal del juego (extiende `Game`). Crea el `SpriteBatch` y la fuente compartidos, guarda el récord y abre la pantalla de menú.

    * <code><b><span style="color: #009dff;">view/</span></b></code>: Pantallas del juego (implementan `Screen`).
        * <code><b><span style="color: #f43009da;">PantallaMenu.java</span></b></code>: Pantalla de bienvenida. Inicia una partida con cualquier tecla o clic.
        * <code><b><span style="color: #f43009da;">PantallaJuego.java</span></b></code>: Pantalla de la partida. Carga recursos, crea la nave y los asteroides, resuelve colisiones, puntaje, dificultad y transiciones. *(Concentra demasiadas responsabilidades, ver SRP.)*
        * <code><b><span style="color: #f43009da;">PantallaGameOver.java</span></b></code>: Pantalla de derrota. Reinicia la partida con cualquier tecla o clic.

    * <code><b><span style="color: #009dff;">engine/entities/</span></b></code>: Entidades del juego.
        * <code><b><span style="color: #f43009da;">Shulker.java</span></b></code>: Nave del jugador (movimiento, disparo, vidas y estado "herido").
        * <code><b><span style="color: #f43009da;">Enemigos.java</span></b></code>: Enemigos (movimiento, rebote en bordes y entre asteroides). 
        <span style="opacity:0.5">Eliminar que reboten entre si y que el movimiento sea constante lateral y pabajo</span>
        * <code><b><span style="color: #f43009da;">Bullet.java</span></b></code>: Proyectil disparado por el shulker.

* <code><b><span style="color: #23c523d4;">desktop/src/</span></b></code>: Módulo de escritorio (LWJGL3).

  * <code><b><span style="color: #009dff;">com/pdjteam/galagraft/</span></b></code>
    * <code><b><span style="color: #f43009da;">DesktopLauncher.java</span></b></code>: Punto de entrada. Configura la ventana (60 FPS, título) y lanza `Galagraft`.

* <code><b><span style="color: #23c523d4;">assets/</span></b></code>: Recursos del juego (imágenes y audio, todos en una sola carpeta plana).
  * En uso: `MainShip3.png`, `Rocket2.png`, `aGreyMedium4.png`, `explosion.ogg`, `hurt.ogg`, `pop-sound.mp3`, `piano-loops.wav`.
  * Sin referencias en el código: `aGreyLarge.png`, `aGreySmall.png`, `badlogic.jpg` y `assets.txt` (restos de la plantilla de LibGDX; `assets.txt` menciona un `libgdx.png` que ni siquiera existe).

* <code><b><span style="color: #23c523d4;">Archivos de build</span></b></code>: `build.gradle` (raíz, dependencias y versiones), `core/build.gradle`, `desktop/build.gradle` (tareas `run`, `debug`, `dist`), `settings.gradle`, `gradle.properties` y `gradle/wrapper/`.

### Orden de archivos: observaciones y estructura propuesta
```diff
@@ Problemas de organización actuales @@
- La lógica de juego vive en `view`: PantallaJuego decide colisiones, puntaje y dificultad.
- `Shulker` (engine.entities) importa `PantallaJuego` (view): dependencia circular entre paquetes.
- No hay paquetes para recursos, configuración ni estrategias; `engine` solo tiene `entities`.
- `assets/` mezcla imágenes y audio, y los nombres no siguen una convención.
- Assets sin uso y `assets.txt` obsoleto (ver lista arriba).
- Nombres poco claros: `Ball2` (asteroide; pasará a `Enemigo`) y `balls1`/`balls2`.
- `Shulker` sí sirve para el juego nuevo, pero en `PantallaJuego` la variable se llama `nave`.
- El código aún usa `Ball2.java`: el renombre a `Enemigo` (singular) es parte de la F1.
```

<pre><code><i><span style="color: #00fed4ed;">Estructura objetivo (propuesta, aún no aplicada)</span></i>
core/src/com/pdjteam/galagraft/
├── Galagraft.java
├── config/            ConfiguracionRonda (Builder) y constantes del juego
├── resources/         GestorRecursos, GestorAudio y RegistroPuntaje (Singleton)
├── engine/
│   ├── entities/      Entidad (abstracta), Shulker, Enemigo (abstracta), Bala, Potenciador (abstracta)
│   ├── mobs/          Subclases de Enemigo (p. ej. Creeper, Zombie, Esqueleto)
│   ├── powerups/      Subclases de Potenciador (p. ej. DisparoDoble, Escudo, VidaExtra)
│   └── strategies/    Movimiento, disparo, control y dificultad (Strategy)
└── view/              PantallaBase (abstracta), PantallaMenu, PantallaJuego, PantallaGameOver

assets/
├── images/            Shulker, mobs, potenciadores y proyectiles
└── audio/             Música y efectos de sonido
</code></pre>

## **Aplicación de encapsulamiento y principios de orientación a objetos**
> Estado: **parcialmente respetado**. Los atributos son `private`, pero una clase puede dejar a otra en un estado inválido y no existen clases abstractas ni interfaces propias.

### Encapsulamiento
* **`Ball2.setXSpeed` / `setySpeed`** (L69-77): `Shulker` cambia la velocidad del asteroide desde afuera (L92-97).
  * Corrección: que `Ball2` ofrezca un comportamiento (`rebotar`) en vez de setters.
* **`Shulker.setVidas`** (L128): no valida ni actualiza `destruida`; es el origen del bug de la ronda con 0 vidas.
  * Corrección: validar dentro de la clase.
* **`Galagraft.setHighScore`** (L47): cualquiera puede bajar el récord; la regla "solo si es mayor" vive en `PantallaJuego` (L144-145).
  * Corrección: un método `registrarPuntaje(int)` que aplique la regla.
* **`Galagraft.getBatch()` / `getFont()`**: entregan recursos compartidos; `PantallaJuego` cambia la escala global de la fuente (L78) y encadena llamadas (Ley de Demeter).
  * Corrección: escala propia por pantalla.
* **`Ball2`** (L11-15, L30, L42): `x` e `y` duplican la posición del `Sprite`.
  * Corrección: una sola fuente de verdad.
* **`Bullet.checkCollision`** (L37) y **`Shulker.checkCollision`** (L88): consultas que además modifican estado.
  * Corrección: separar la consulta de la reacción.
* **Visibilidad y código sin uso**: `dibujaEncabezado` es público (L76); `agregarBala` devuelve un `boolean` que nadie usa (L163); `Galagraft.nombreJuego` (L12), `Shulker.getX/getY` (L126-127) y `PantallaJuego.camera` (L21, L47-48) no se usan.

### Principios SOLID y otros

#### SRP: responsabilidad única
* **`PantallaJuego`**: carga assets, crea entidades, resuelve colisiones y puntaje, define la dificultad, dibuja el HUD y cambia de pantalla.
* **`Shulker.draw`** (L39-86): lee teclado, mueve, controla bordes, dispara y dibuja en un solo método.
* **`Shulker.checkCollision`** (L88-115): detecta, rebota, resta vidas y reproduce sonido.
* **`Galagraft`**: ciclo de vida, recursos gráficos y puntaje.
* Corrección: separar recursos, lógica de ronda y vista; separar `actualizar` de `dibujar`.

#### OCP: abierto/cerrado
* **Dificultad** (`PantallaJuego.render`, L154-155): cambiarla obliga a editar la clase.
* **Nuevos enemigos o armas**: obligan a editar `PantallaJuego` (L32-33) y `Shulker.draw` (L80-84).
* **`PantallaMenu` y `PantallaGameOver`** (L37): crean `PantallaJuego` con parámetros escritos a mano.

#### LSP: sustitución de Liskov
* No hay jerarquía propia: `Ball2`, `Bullet` y `Shulker` no comparten tipo.
* Sus métodos no son intercambiables: `draw(batch)` vs `draw(batch, PantallaJuego)` (`Shulker` L39), y `checkCollision` devuelve `void` en `Ball2` y `boolean` en las otras dos.
* Corrección: al crear `Entidad`, unificar estas firmas.

#### ISP: segregación de interfaces
* Las 3 pantallas implementan `Screen` completo con métodos vacíos "TODO Auto-generated" (`resize`, `pause`, `resume`, `hide`; en Menu y Game Over también `show` y `dispose`).
* Corrección: extender `ScreenAdapter` de LibGDX.

#### DIP: inversión de dependencias
* **`Shulker`** (L10, L82): una entidad depende de la vista concreta `PantallaJuego`.
  * Corrección: depender de una interfaz ("receptor de balas").
* **`Ball2`, `Bullet`, `Shulker`**: usan directamente `Gdx.graphics`, `Gdx.input` y `Gdx.audio`, por lo que no se pueden probar sin el motor.
* **`PantallaJuego`**: depende de clases concretas y las pantallas se crean entre sí con `new`.

#### DRY y abstracción
* **`render`** de `PantallaMenu` y `PantallaGameOver` (L24-42): casi idénticos.
* **Chequeo de bordes** repetido en `Ball2.update` (L38-41), `Shulker.draw` (L64-67) y `Bullet.update` (L24-29).
* **Rebote de velocidades** duplicado en `Ball2.checkCollision` (L52-65) y `Shulker.checkCollision` (L91-99).
* **`balls1` y `balls2`** guardan lo mismo y se sincronizan a mano (L32-33, L71-72, L96-97, L137-138).
* **Abstracción**: no existe ninguna clase abstracta ni interfaz propia.

#### Nombres y limpieza
* Nombres poco claros: `Ball2`, `balls1`, `balls2`, `ss`, `bb`, `vidas2`; mezcla de español e inglés; `getXSpeed` vs `getySpeed`.
* Números mágicos: 800/640, 1200/800, 45, 50, 3, 10.
* Código comentado: `Shulker` L49-61 y L101-104, `PantallaJuego` L103.
* Imports redundantes: `Shulker` L11, `DesktopLauncher` L5.
* Comentarios desactualizados: "64x64" (`PantallaJuego` L58) vs 45x45 (`Shulker` L36); "Se destruyen ambos" (`Bullet` L39).
* Faltan `@Override` en `Galagraft` (L17, L26, L30).

### Cómo se aplicará en el juego nuevo
* **Encapsulamiento:** el Shulker valida sus vidas, el récord solo cambia con `registrarPuntaje` y los enemigos reciben daño con un método propio (`recibirDanio`) en vez de setters públicos.
* **SRP:** `PantallaJuego` solo coordina; la formación de enemigos, las colisiones y el HUD pasan a clases propias.
* **OCP:** agregar un mob o un potenciador es crear una subclase, sin editar `PantallaJuego`.
* **LSP:** cualquier `Enemigo` o `Potenciador` se puede usar donde se espera su clase base, con el mismo contrato.
* **ISP:** interfaces pequeñas (`Dibujable`, `Colisionable`, `ReceptorDeBalas`) en lugar de una clase que lo hace todo.
* **DIP:** `PantallaJuego` depende de `Enemigo` y `Potenciador` (abstractos), no de cada mob; el Shulker depende de `ReceptorDeBalas`, no de la pantalla.
* **Herencia y polimorfismo:** `Entidad` → `Shulker`, `Enemigo`, `Bala` y `Potenciador`; la lista de enemigos guarda `Enemigo` y llama a sus métodos sin conocer el mob concreto.

## **Aplicación del patrón de diseño Singleton**
> Estado: **no aplicado**.

### Dónde aplicarlo en el juego base
* **`GestorRecursos`** (clase nueva): cargar cada textura y sonido una sola vez (hoy en `PantallaJuego` L50-62 y L70) y liberarlos en `Galagraft.dispose()` (L30-33). Soluciona la fuga de memoria.
* **`RegistroPuntaje`** (clase nueva): concentra el récord (hoy en `Galagraft` L15, L43-49 y `PantallaJuego` L144-145) y lo guarda con `Preferences` de LibGDX.
* **`Configuracion`** (opcional): solo si cambia en ejecución (menú de opciones); si no, bastan constantes `final static`.

### Precauciones
* `Galagraft` **no** debe ser Singleton: `DesktopLauncher` (L13) debe instanciarlo.
* Crear la instancia después de `Galagraft.create()`: los recursos de LibGDX necesitan el contexto gráfico y de audio.
* Evitar que se convierta en estado global oculto: pasarlo por constructor cuando sea posible.

### En el juego nuevo
* **`GestorRecursos`:** guarda las texturas de cada mob, del Shulker y de los potenciadores, y los sonidos. Con muchos mobs, cargarlos en cada ronda agravaría la fuga de memoria.
* **`GestorAudio`:** mantiene la música de fondo sin reiniciarla entre rondas (corrige el error de la música) y controla el volumen global.
* **`RegistroPuntaje`:** récord persistente entre ejecuciones.

## **Aplicación del patrón de diseño Strategy**
> Estado: **no aplicado**. El comportamiento variable está escrito dentro de las clases.

### Dónde aplicarlo en el juego base
* **Movimiento del asteroide**: `Ball2.update` (L34-43).
  * Hoy: solo lineal con rebote.
  * Estrategias: lineal, zigzag, sinusoidal, perseguidor.
* **Disparo de la nave**: `Shulker.draw` (L80-84).
  * Hoy: un proyectil de velocidad 3.
  * Estrategias: simple, doble, ráfaga, diagonal.
* **Progresión de dificultad**: `PantallaJuego.render` (L154-155).
  * Hoy: +3 de velocidad y +10 asteroides por ronda.
  * Estrategias: lineal, exponencial, por oleadas, con tope.
* **Control de la nave**: `Shulker.draw` (L44-47).
  * Hoy: teclado con inercia.
  * Estrategias: teclado directo, mouse o táctil.
* **Respuesta a colisión**: `Ball2.checkCollision` (L52-65) y `Shulker.checkCollision` (L88-115).
  * Hoy: siempre rebote.
  * Estrategias: rebote, destrucción, división del asteroide.

### Idea general
* La clase que usa el comportamiento guarda una interfaz de estrategia y le delega el cálculo, de modo que se puede cambiar en ejecución sin modificar la clase.

### En el juego nuevo
* **Movimiento de enemigos:** `MovimientoFormacion` (lateral constante, baja un escalón al llegar al borde), `MovimientoPicada` (se lanza contra el Shulker, estilo Galaga) y `MovimientoZigzag`. Cada mob recibe la suya.
* **Disparo:** simple, doble, triple y teledirigido (inspirado en el proyectil del Shulker de Minecraft). Los potenciadores **cambian la estrategia del Shulker en ejecución** y la restauran al expirar.
* **Dificultad por ronda:** decide filas, columnas, tipos de mob y velocidad, con tope.
* **Control:** `A` / `D` con velocidad constante; se pueden agregar otras (flechas, mouse).

## **Aplicación del patrón de diseño Template Method**
> Estado: **no aplicado**. Hay esqueletos de algoritmo repetidos donde solo cambian algunos pasos.

### Dónde aplicarlo en el juego base
* **`PantallaBase`** (clase abstracta nueva, extiende `ScreenAdapter`): para `PantallaMenu`, `PantallaGameOver` y `PantallaJuego`.
  * Esqueleto: limpiar pantalla → actualizar proyección → dibujar → procesar entrada (hoy repetido en `PantallaMenu` y `PantallaGameOver`, L24-42).
  * Pasos que cambian: `dibujar()` y `procesarEntrada()`.
  * Elimina además los métodos vacíos "TODO".
* **`Entidad`** (clase abstracta nueva): para `Ball2`, `Bullet` y `Shulker`.
  * Esqueleto: mover → revisar bordes → sincronizar el `Sprite` (hoy repetido en `Ball2.update` L34-43, `Bullet.update` L22-31 y `Shulker.draw` L63-69).
  * Paso que cambia: qué ocurre al tocar el borde (el asteroide rebota, la bala se destruye, la nave rebota con inercia).
* **Ciclo de ronda** en `PantallaJuego.render` (opcional): actualizar → colisiones → dibujar → verificar fin. Permitiría modos de juego distintos.

### Nota
* El método plantilla debe ser `final` para que las subclases redefinan los pasos pero no el orden.

### En el juego nuevo
* **`Enemigo`** (abstracta): `actualizar` = mover (usa la estrategia) → intentar disparar → revisar bordes. Cada mob redefine pasos: `intentarDisparar()` (el Esqueleto dispara, el Zombie no) y `alMorir()` (el Creeper explota y daña a los cercanos; otros sueltan un potenciador).
* **`Potenciador`** (abstracta): `recoger` = reproducir sonido → `aplicarEfecto` → programar la expiración → `revertirEfecto`. Cada potenciador solo define su efecto y su duración.

## **Aplicación del patrón de diseño Builder**
> Estado: **no aplicado**. Hay constructores con muchos parámetros posicionales del mismo tipo.

### Dónde aplicarlo en el juego base
* **`PantallaJuego`** (constructor L37-38): 7 parámetros enteros. Las llamadas con números sueltos están en `PantallaMenu` (L37), `PantallaGameOver` (L37) y `PantallaJuego` (L154-155).
  * Propuesta: `ConfiguracionRonda` con Builder y valores por defecto (ronda 1, 3 vidas, puntaje 0).
* **`Shulker`** (constructor L29): 6 parámetros, con dos `Texture` y dos `Sound` fáciles de intercambiar (uso en `PantallaJuego` L59-62). Tamaño 45x45 fijo (L36) y 3 vidas por defecto (L18).
  * Propuesta: `NaveBuilder` con validación en `build()`.
* **`Ball2`** (constructor L17) y su bucle de creación (`PantallaJuego` L66-72): 6 parámetros; la posición y velocidad aleatorias se calculan en la pantalla.
  * Propuesta: `AsteroideBuilder`.
* **`Bullet`** (constructor L16, opcional): 5 parámetros; solo si se agregan más tipos de proyectil.

## **Funcionalidades pendientes**
```diff
@@ Juego nuevo (Galaga x Minecraft) @@
+ Shulker: moverse de lado con A / D a velocidad constante.
+ Shulker: proyectil propio (estilo Shulker de Minecraft).
+ Enemigos: mobs de Minecraft en lugar de asteroides.
+ Enemigos: formación que avanza constante hacia los lados y hacia abajo.
+ Enemigos: eliminar el rebote entre ellos.
+ Rondas: una oleada nueva por ronda, con dificultad creciente y tope.
+ Potenciadores: caen al morir un enemigo y tienen efecto temporal.
+ HUD: vidas, ronda, puntaje, récord y potenciador activo.
+ Jefe cada cierto número de rondas (opcional).

@@ Mejoras al juego base @@
+ Guardar el récord entre ejecuciones (Preferences) y actualizarlo en vivo en el HUD.
+ Mostrar puntaje y récord en Game Over.
+ Pausa (P / ESC) y salir con ESC.
+ Retardo antes de aceptar la entrada en Game Over.

@@ Técnico / arquitectura @@
+ Usar `Viewport` (p. ej. FitViewport) y fijar el tamaño de ventana en DesktopLauncher.
+ Movimiento basado en `delta` en lugar de píxeles por cuadro.
+ Centralizar constantes (rutas de assets, tamaños, velocidades, resolución).
+ Aplicar los patrones y principios descritos arriba.
+ Pruebas unitarias de la lógica pura (colisiones, puntaje, dificultad).

@@ Build / repositorio / assets @@
+ Gradle: quitar `gwt-gradle-plugin` y `gretty` (build.gradle L12-13).
+ Gradle: quitar las versiones sin uso (build.gradle L26-30) y los
  repositorios snapshot heredados de la plantilla.
+ Gradle: cambiar `main =` por `mainClass =` en desktop/build.gradle
  (L11 y L24); `main` se elimina en Gradle 8.
+ Actualizar LibGDX (1.11.0 es una versión antigua).
+ Convertir piano-loops.wav (≈22 MB) a OGG o MP3.
+ Eliminar assets sin uso (aGreyLarge.png, aGreySmall.png,
  badlogic.jpg, assets.txt).
+ Reorganizar assets/ en subcarpetas y unificar los nombres.
+ Documentar el origen de los sprites de los mobs o usar arte propio.
```

## **Problemas conocidos**
Gravedad: **[ALTO]** afecta la partida o los recursos; **[MEDIO]** comportamiento incorrecto acotado; **[BAJO]** detalle o experiencia de usuario.

Fase en que se resuelve: **[F0]** se corrige ahora en el juego base; **[F1]** al refactorizar; **[F2]** desaparece con el rediseño del juego; **[F3]** en el pulido.

```diff
@@ A nivel de renderizado / visual @@
- [ALTO][F0] Resolución inconsistente
  (DesktopLauncher; PantallaMenu L20; PantallaGameOver L20; PantallaJuego L48)
  La ventana no tiene tamaño fijado (LWJGL3 usa 640x480 por defecto), pero
  las cámaras son de 1200x800 y 800x640. Las entidades se mueven según el
  tamaño real de la ventana. `resize(1200, 800)` no hace nada porque el
  método está vacío (Menu y GameOver L38; Juego L147 y L156).
  PantallaJuego nunca aplica su cámara: hereda la proyección que dejó el
  menú en el batch compartido.
  Probable resultado: el juego ocupa solo una esquina. [Verificar visualmente]
- [MEDIO][F2] Asteroides sobre la nave al iniciar la ronda
  (PantallaJuego L67-70)
  No hay zona segura: el choque resta una vida de inmediato.
- [MEDIO][F2] Asteroides "pegados" (Ball2.checkCollision L52-65)
  Si siguen solapados tras el rebote, invierten su velocidad en cada
  cuadro y vibran.
- [BAJO][F3] Texto de Game Over (PantallaGameOver L32 y L36)
  Dice "Pincha en cualquier lado" pero también acepta teclas; no muestra
  el puntaje.
- [BAJO][F3] El HighScore del HUD solo se actualiza al morir
  (PantallaJuego L144-145).

@@ A nivel de lógica de procedimientos / sistemas @@
- [ALTO][F0] Fuga de recursos (PantallaJuego L59-62, L70, L197-202)
  Una Texture por asteroide y texturas y sonidos nuevos en cada ronda;
  dispose() solo libera la explosión y la música. La memoria crece con
  cada ronda y cada reinicio. Solución definitiva: GestorRecursos (F1).
- [ALTO][F0] Ronda siguiente con 0 vidas
  (PantallaJuego L135-140 y L153; Shulker L117-119)
  Si la nave choca con el último asteroide en su última vida, el asteroide
  se elimina y estaDestruido() aún es falso (la nave está "herida").
  Entonces balls1.size()==0 crea la ronda siguiente con 0 vidas, en vez de
  mostrar Game Over.
- [MEDIO][F0] Una bala destruye varios asteroides
  (PantallaJuego L93-101; Bullet L37-45)
  El bucle no hace break y la bala no revisa si ya fue destruida: suma
  +10 por cada asteroide.
- [MEDIO][F1] Música superpuesta y reiniciada
  (PantallaJuego L52-56, L154-158 y L170)
  Cada ronda crea un Music nuevo (WAV de ≈22 MB) antes de liberar el
  anterior, y show() llama a play() otra vez.
- [MEDIO][F0] Falta return tras setScreen (PantallaJuego L143-159)
  El método sigue ejecutándose; si coincidieran ambas condiciones,
  cambiaría de pantalla y liberaría recursos dos veces.
- [MEDIO][F2] Posición inicial del asteroide (Ball2 L17-33)
  El sprite se ubica con los parámetros originales (L30) y no con los
  corregidos. El segundo if de cada eje puede pisar al primero.
  El parámetro size (20-29) no cambia el tamaño visual.
- [MEDIO][F2] Todos los asteroides parten hacia arriba a la derecha
  (PantallaJuego L69). Las velocidades iniciales siempre son positivas.
- [MEDIO][F2] Dificultad sin tope (PantallaJuego L154-155 y L114-123)
  +10 asteroides y +3 de velocidad por ronda; colisiones O(n²).
  En rondas altas queda injugable y lento.
- [BAJO][F0] setVolume(1, 0.5f) mal usado (PantallaJuego L51)
  El primer parámetro es el id de una reproducción en curso, no regula el
  volumen del sonido. [Verificar en la documentación]
- [BAJO][F0] Galagraft.dispose() no llama a super.dispose() (L30-33).
- [BAJO][F3] Game Over se salta fácil (PantallaGameOver L36)
  Cualquier tecla o clic reinicia de inmediato.
- [BAJO][F3] Movimiento dependiente de los FPS
  delta no se usa y el estado "herido" dura 50 cuadros.
```
