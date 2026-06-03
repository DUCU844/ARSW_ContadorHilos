# Laboratorio: Concurrencia con Hilos
### Java Threads vs Go Goroutines — Contador con múltiples hilos

---

## Descripción

Este laboratorio tiene como objetivo comprender el comportamiento de los **hilos de ejecución (threads) en Java** y las 
**goroutines en Go**, observando cómo varía el tiempo de ejecución al incrementar la cantidad de unidades de 
concurrencia para una tarea de I/O intensiva: imprimir números del `1` al `50.000.000`.

Cada hilo/goroutine recibe un subrango del total y lo imprime de forma independiente. El experimento permite identificar
el fenómeno de **contención de I/O**: cuando múltiples hilos compiten por escribir en la consola (recurso compartido), 
el tiempo total **aumenta** en lugar de disminuir.

---

## Manejo de Concurrencia

### Java — `java.lang.Thread`

El programa implementa la interfaz `Runnable`, separando la lógica del hilo de su ciclo de vida. Cada instancia de 
`Contador` define su rango de trabajo (`inicio–fin`) y mide su propio tiempo con `System.nanoTime()`.

- **`Thread[] + Contador[]`**: se crean y lanzan todos los hilos antes de esperar (`start → join`).
- **`join()`**: bloquea el hilo principal hasta que cada hilo hijo termine.
- **`System.out.println()`** usa `PrintStream`, que es `synchronized` internamente → todas las llamadas se serializan 
- en un solo lock, generando **alta contención** con muchos hilos.

### Go — Goroutines y `sync.WaitGroup`

Go usa goroutines, hilos verdes gestionados por el scheduler del runtime (modelo **M:N threading**). Son 
significativamente más ligeras que los threads del SO (~2KB de stack inicial vs ~1MB en Java).

- **`sync.WaitGroup`**: equivalente al `join()` de Java. `wg.Add(1)` antes de lanzar, `defer wg.Done()` al terminar, 
- `wg.Wait()` en el main.
- **`go goroutine()`**: lanzamiento con la palabra clave `go`, sin necesidad de clase/interfaz.
- **`fmt.Println()`** también serializa la salida, pero el scheduler de Go distribuye las goroutines entre los núcleos 
- disponibles (`GOMAXPROCS`) de forma más eficiente.

### ¿Por qué más hilos = más tiempo en este caso?

Cuando la tarea es **I/O intensiva** (imprimir por consola), el recurso compartido es el buffer de `stdout`. Todos los 
hilos compiten por el mismo lock al hacer print, convirtiendo el trabajo en **secuencial con overhead adicional** de 
context-switching. El paralelismo real ocurre con tareas **CPU-bound** (cálculo matemático puro) donde no hay recursos 
compartidos.

---

## Resultados

### Java — Threads

| Hilos | Tiempo (ms) | Rango   | 
|-------|-------------|---------|
| 1     | 9350        | 5000000 |
| 100   | 19423       | 5000000 |
| 500   | 18974       | 5000000 | 
| 1000  | 12857       | 5000000 |
| 2000  | 12291       | 5000000 |
| 5000  | 13872       | 5000000 |

> Los valores de los hilos los tomamos hasta un rango 5000000 por temas de recursos, pero podemos evidenciar que 
mayor cantidad de hilos no implica un mejor tiempo de ejecución.

### Go — Goroutines

| Goroutines | Tiempo         | Rango   |
|------------|----------------|---------|
| 1          | 4m10.9194714s  | 5000000 |
| 100        | 4m41.7500333s  | 5000000 |
| 500        | 10m38.1320994s | 5000000 |
| 1000       | 16m59.3359358s | 5000000 |
| 2000       | 6m16.3077088s  | 5000000 |
| 5000       | 5m30.3802046s  | 5000000 |

> Go es generalmente 3–5× más rápido en este escenario por el scheduler M:N y el menor overhead de goroutines vs 
threads del SO.

---

## Prerrequisitos e Instalación

### Java

Requisito mínimo: **JDK 11+** (recomendado JDK 21).

```bash
# Ubuntu/Debian
sudo apt update && sudo apt install openjdk-21-jdk

# macOS
brew install openjdk@21

# Windows
winget install EclipseAdoptium.Temurin.21.JDK

# Verificar
java -version
javac -version
```

### Go

Requisito mínimo: **Go 1.18+** (recomendado Go 1.22+).

```bash
# Ubuntu/Debian
sudo apt update && sudo apt install golang-go

# macOS
brew install go

# Windows
winget install GoLang.Go

# Verificar
go version
```

---

## Ejecución

### Java

```bash
# 1. Compilar
javac Contador.java

# 2. Ejecutar
java Contador

# Ingresar cuando se solicite:
# Ingrese el límite: 50000000
# Ingrese la cantidad de hilos: 4

# Consejo: redirigir salida para medir solo tiempo de CPU
java Contador > /dev/null
```

### Go

```bash
# Ejecutar directamente
go run contador.go

# O compilar y ejecutar el binario (más rápido)
go build -o contador contador.go
./contador

# Con salida redirigida
go run contador.go > /dev/null
```

---

## Contribución

Este repositorio es parte de un laboratorio académico. Las contribuciones son bienvenidas:

1. Haz fork del repositorio y crea una rama descriptiva: `feature/add-python-version`
2. Mantén el estilo de código: nombres en español, comentarios descriptivos
3. Si agregas un nuevo lenguaje, incluye su sección en este README con la tabla de resultados completa
4. Abre un Pull Request describiendo qué cambió y por qué

**Ideas para extender el laboratorio:**
- Repetir con tarea CPU-bound (calcular números primos) para ver el efecto contrario
- Agregar versión con `ExecutorService` / `ThreadPoolExecutor` en Java
- Comparar con Python (`threading` vs `multiprocessing`) o Rust (`std::thread`)
- Graficar los resultados de la tabla con matplotlib o Chart.js

---

*Escuela Colombiana de Ingeniería Julio Garavito · Ingeniería de Sistemas*