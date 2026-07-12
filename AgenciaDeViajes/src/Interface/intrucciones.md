# Instrucciones — Agencia de Viajes Bolivia

## Cómo ejecutar

Ejecutar la clase `Interface.Formulario` desde NetBeans (clic derecho → Run File).

La aplicación viene **precargada** con los 9 departamentos de Bolivia, las rutas aéreas con precios en Bs., y un ranking inicial de destinos.

---

## Pestaña 1: Compra de Pasaje (Vista del Pasajero)

El pasajero puede comprar su boleto aéreo y calificar su destino.

### Flujo de compra
1. Escribir su **nombre completo**
2. Seleccionar el **departamento de origen** y **destino** de los desplegables
3. Presionar **"Calcular Ruta y Precio"** → el sistema ejecuta Dijkstra para encontrar la ruta más barata
4. Revisar la ruta y precio calculados
5. Presionar **"Comprar Pasaje"** → se genera un número de boleto automáticamente y se registra en el Árbol B
6. (Opcional) **Calificar el destino** con una puntuación de 1 a 100 → actualiza el ranking en la pestaña 4

### Departamentos disponibles
La Paz, Cochabamba, Santa Cruz, Oruro, Potosí, Tarija, Sucre, Trinidad (Beni), Cobija (Pando)

### Precios
El precio se calcula automáticamente usando el **algoritmo de Dijkstra** sobre el grafo de rutas. El sistema encuentra la ruta de menor costo entre origen y destino.

---

## Pestaña 2: Recepción (Vista del Recepcionista)

El recepcionista de la aerolínea puede verificar y gestionar los pasajeros.

### Operaciones
- **Buscar** — Ingresa el número de boleto para ver los datos completos del pasajero
- **Confirmar Vuelo** — Verifica si el pasajero está registrado y autoriza el abordaje
- **Cancelar Vuelo** — Elimina el registro del pasajero del sistema (pide confirmación)

### Gestión del Registro (Árbol B)
- **Mostrar Árbol** — Visualiza la estructura del Árbol B
- **Recorrido InOrden** — Lista todos los pasajeros ordenados por número de boleto
- **Por Niveles** — Lista los pasajeros recorriendo el árbol nivel por nivel
- **Cantidad de Pasajeros** — Total de pasajeros registrados

---

## Pestaña 3: Rutas de Vuelo (Grafo + Dijkstra + Prim)

Muestra la red de rutas aéreas entre departamentos. Viene precargada con 9 departamentos y 17 rutas.

### Operaciones
- **Agregar/Eliminar vértices** — Gestionar departamentos/ciudades
- **Agregar/Eliminar aristas** — Gestionar rutas con su costo en Bs.
- **Mostrar Grafo** — Ver la lista de adyacencias completa
- **Ejecutar Dijkstra** — Calcula los caminos más cortos desde un origen
- **Mostrar Ruta** — Muestra la ruta óptima hacia un destino específico
- **Ejecutar Prim** — Calcula el Árbol de Expansión Mínima (MST)
- **Mostrar MST** — Muestra las aristas del MST y su costo total

---

## Pestaña 4: Ranking de Destinos (Montículo Máximo)

Ranking de los destinos turísticos que se **actualiza automáticamente** con las calificaciones de los pasajeros (pestaña 1).

### Ranking inicial precargado

| Posición | Departamento | Calificación |
|----------|-------------|-------------|
| 1 | Santa Cruz | 95 |
| 2 | La Paz | 92 |
| 3 | Cochabamba | 90 |
| 4 | Trinidad | 88 |
| 5 | Tarija | 86 |
| 6 | Potosí | 83 |
| 7 | Sucre | 81 |
| 8 | Oruro | 78 |
| 9 | Cobija | 75 |

### Cómo se actualiza el ranking
Cuando un pasajero califica un destino en la pestaña 1, la nueva calificación se **promedia** con la calificación actual del lugar. El montículo máximo se reconstruye automáticamente para mantener el orden correcto.

### Operaciones
- **Insertar Lugar** — Agregar un nuevo destino al ranking
- **Ver Más Popular** — Muestra el destino con mayor calificación
- **Eliminar Más Popular** — Extrae el destino más popular del ranking
- **Mostrar Ranking Completo** — Lista todos los destinos de mayor a menor
- **Actualizar** — Cambiar la calificación de un lugar por posición en el montículo

---

## Estructuras de datos

| Módulo | Estructura | Clase |
|--------|------------|-------|
| Registro de Pasajeros | Árbol B (orden 3) | `ArbolB<Boleto>` |
| Rutas Aéreas | Grafo Pesado (lista de adyacencias) | `Grafo_Pesado<String>` |
| Ranking de Destinos | Montículo Máximo | `MonticuloMaximo<Lugar>` |

## Datos del Boleto

| Campo | Descripción |
|-------|-------------|
| `nroBoleto` | Autogenerado (a partir de 1000) |
| `nombrePersona` | Nombre ingresado por el pasajero |
| `precioDeBoleto` | Calculado por Dijkstra (costo mínimo de la ruta) |
| `Origen` | Número del departamento de origen (1-9) |
| `Destino` | Número del departamento de destino (1-9) |