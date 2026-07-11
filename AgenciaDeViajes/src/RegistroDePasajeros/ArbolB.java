package RegistroDePasajeros;

import java.util.*;

/**
 * @param <T>
 * @author Andy
 */
public abstract class ArbolB<T extends Comparable<T>> extends
        ArbolMViasBase<T> implements Prub<T> {
   // private int nroDatos; // te pedira implementar los metodos de arbol Binario Base

    public ArbolB() {
        super.orden = 3;
    }

    public ArbolB(int orden) {
        if (orden < ORDEN_MINIMO) {
            throw new IllegalArgumentException("Orden del árbol debe ser al menos 3");
        }
        super.orden = orden;
    }

    private int getNroMaximoDeHijo() {
        return orden;
    }

    private int getNroMaximodeDatos() {
        return orden - 1;
    }

    private int getNroMinimoDeDatos() {
        return getNroMaximodeDatos() / 2;
    }// el valor medio

    private int getNroMinimoDeHijos() {
        return getNroMinimoDeDatos() + 1;
    }

    private void insertarDatoOrdenadoEnElNodo(NodoMVias<T> nodoActual, T datoInsertar) {
        // ESCUCHAR AUDIOS DEL INGENIERO
        if (estanLosDatosLlenos(nodoActual)) {
            nodoActual.datos.add((T) DATO_VACIO);
            nodoActual.hijos.add(NODO_VACIO);
        }
        int nroDatos = nroDeDatosNoVacios(nodoActual);
        int pos = 0;
        while (pos < nroDatos && datoInsertar.compareTo(nodoActual.datos.get(pos)) > 0) {
            pos++;
        }
        for (int i = nroDatos; i > pos; i--) {
            nodoActual.datos.set(i, nodoActual.datos.get(i - 1));
            nodoActual.hijos.set(i + 1, nodoActual.hijos.get(i));
        }
        nodoActual.datos.set(pos, datoInsertar);
    }

    private void dividirYEmpujar(NodoMVias<T> nodoActual, Stack<NodoMVias<T>> pilaDeNodos, T datoInsertar,
            NodoMVias<T> nodoVacio) {
        NodoMVias<T> nodoPadre = null;
        insertarDatoOrdenadoEnElNodo(nodoActual, datoInsertar);
        do {
            int posicionMedia = (nodoActual.datos.size() - 1) / 2;
            T datoMedia = nodoActual.datos.get(posicionMedia);

            // Crear hijo izquierdo con datos[0..posicionMedia-1]
            NodoMVias<T> nodoHijoI = new NodoMVias<>(orden);
            for (int i = 0; i < posicionMedia; i++) {
                insertarDatoOrdenadoEnElNodo(nodoHijoI, nodoActual.datos.get(i));
            }
            // Copiar los hijos correspondientes al hijo izquierdo
            for (int i = 0; i <= posicionMedia; i++) {
                nodoHijoI.hijos.set(i, nodoActual.hijos.get(i));
            }

            nodoActual.datos.remove(posicionMedia);

            // Crear hijo derecho con datos[posicionMedia..fin]
            NodoMVias<T> nodoHijoD = new NodoMVias<>(orden);
            int nroDatosRestantes = nroDeDatosNoVacios(nodoActual);
            for (int i = posicionMedia; i < nroDatosRestantes; i++) {
                insertarDatoOrdenadoEnElNodo(nodoHijoD, nodoActual.datos.get(i));
            }
            // Copiar los hijos correspondientes al hijo derecho
            int nroDatosHijoD = nroDeDatosNoVacios(nodoHijoD);
            for (int i = 0; i <= nroDatosHijoD; i++) {
                nodoHijoD.hijos.set(i, nodoActual.hijos.get(posicionMedia + 1 + i));
            }

            if (!pilaDeNodos.isEmpty()) {
                nodoPadre = pilaDeNodos.pop();
            } else {
                nodoPadre = new NodoMVias<>(orden);
                this.raiz = nodoPadre;
            }
            insertarDatoOrdenadoEnElNodo(nodoPadre, datoMedia);
            int posDelDatoEnNodo = obtenerPosicionDeDatoEnNodo(nodoPadre, datoMedia);
            int posDelNodoPosterior = posDelDatoEnNodo + 1;
            nodoPadre.hijos.set(posDelDatoEnNodo, nodoHijoI);
            nodoPadre.hijos.set(posDelNodoPosterior, nodoHijoD);
            nodoActual = nodoPadre;
        } while (estanLosDatosLlenos(nodoActual));
    }

    @Override
    public void insertar(T datoInsertar) {
        if (esDatoVacio(datoInsertar)) {
            throw new IllegalArgumentException("El dato insertado no puede ser vacio");
        }
        if (esArbolVacio()) {
            raiz = crearNodo(datoInsertar);
        } else {
            NodoMVias<T> nodoActual = raiz;
            Stack<NodoMVias<T>> pilaDeNodos = new Stack<>();
            do {
                int posDelDatoEnNodo = obtenerPosicionDeDatoEnNodo(nodoActual, datoInsertar);
                if (posDelDatoEnNodo != POSICION_INVALIDA) { // 1ra EL DATO A INSERTAR ESTA DENTRO DEL NODO?
                    throw new IllegalArgumentException("El dato insertado ya se encuentra en el nodo");
                }
                 if (esNodoHoja(nodoActual)) { // 2da ES UN NODO HOJA?
                    if (!estanLosDatosLlenos(nodoActual)) { // 2.1 VERIFICAMOS SI SE PUEDE INSERTAR EN EL MISMO NODO
                        insertarDatoOrdenadoEnElNodo(nodoActual, datoInsertar);
                    } else { // 2.2 SI EL NODO SE ENCUENTRA LLENO DIVIDIMOS Y EMPUJAMOS EL DATO
                        dividirYEmpujar(nodoActual, pilaDeNodos, datoInsertar, NODO_VACIO);
                    }
                    nodoActual = NODO_VACIO;
                } else { // 3ra EL NODO TIENE HIJOS --> BUSCAMOS POR DONDE BAJAR
                    posDelDatoEnNodo = obtenerPosicionPorDondeBajar(nodoActual, datoInsertar);
                    pilaDeNodos.push(nodoActual);
                    nodoActual = nodoActual.hijos.get(posDelDatoEnNodo);
                }
            } while (!esNodoVacio(nodoActual));
        }
    }

    /**
     * Elimina el dato en la posicion indicada del nodo, desplazando los datos
     * restantes a la izquierda para mantener la contigüidad.
     * 
     * SOLO desplaza datos, NO desplaza hijos.
     * ¿Por qué? Porque desde eliminar() este método se llama únicamente sobre
     * nodos HOJA (cuyos hijos son todos NODO_VACIO, así que no hay nada que mover).
     * 
     * Para eliminar una clave del padre junto con un hijo (caso fusión),
     * usamos el método separado eliminarClaveYHijoDerecho.
     */
    public void eliminarDatoPosicion(NodoMVias<T> nodoMVias, int posicionEliminar) {
        int nroDatos = nroDeDatosNoVacios(nodoMVias);
        // Desplazar las claves a la izquierda a partir de posicionEliminar
        for (int i = posicionEliminar; i < nroDatos - 1; i++) {
            nodoMVias.datos.set(i, nodoMVias.datos.get(i + 1));
        }
        // Limpiar la última posición que quedó duplicada
        nodoMVias.datos.set(nroDatos - 1, (T) DATO_VACIO);
    }

    /**
     * Elimina la clave en la posición indicada del nodo Y TAMBIÉN elimina
     * el hijo derecho de esa clave (posicionEliminar + 1), desplazando
     * los demás hijos a la izquierda.
     * 
     * Se usa en la FUSIÓN: cuando fusionamos dos hijos del padre,
     * el resultado queda en el hijo IZQUIERDO (posicionEliminar),
     * y el hijo DERECHO (posicionEliminar + 1) se descarta.
     * 
     * Ejemplo visual (orden 4, padre con claves [10, 20, 30] e hijos [H0, H1, H2, H3]):
     *   Si eliminamos clave en posición 1 (el 20), fusionamos H1 con H2:
     *   - H1 absorbe la clave 20 y los datos de H2 → H1 queda con la fusión
     *   - H2 se descarta
     *   - Resultado: claves [10, 30, null], hijos [H0, H1, H3, null]
     */
    private void eliminarClaveYHijoDerecho(NodoMVias<T> nodo, int posicionEliminar) {
        int nroDatos = nroDeDatosNoVacios(nodo);

        // 1. Desplazar las claves a la izquierda
        for (int i = posicionEliminar; i < nroDatos - 1; i++) {
            nodo.datos.set(i, nodo.datos.get(i + 1));
        }
        nodo.datos.set(nroDatos - 1, (T) DATO_VACIO);

        // 2. Desplazar los hijos a la izquierda desde posicionEliminar + 1
        //    (el hijo izquierdo en posicionEliminar se conserva, el derecho en
        //     posicionEliminar + 1 se descarta al ser sobrescrito)
        for (int i = posicionEliminar + 1; i < nroDatos; i++) {
            nodo.hijos.set(i, nodo.hijos.get(i + 1));
        }
        nodo.hijos.set(nroDatos, NODO_VACIO);
    }

    /**
     * Obtiene el nodo que contiene al predecesor inorden.
     * El predecesor es el dato más grande del subárbol izquierdo,
     * es decir, bajamos siempre por el hijo más a la derecha hasta llegar a una hoja.
     * 
     * Va apilando los nodos por los que pasa en pilaDeNodos para que
     * luego prestarseOFusionarse pueda subir si es necesario.
     */
    private NodoMVias<T> obtenerNodoDelPredecesor(NodoMVias<T> nodoActual,
                                                   Stack<NodoMVias<T>> pilaDeNodos) {
        // Bajamos siempre por el hijo más a la derecha hasta llegar a una hoja
        while (!esNodoHoja(nodoActual)) {
            pilaDeNodos.push(nodoActual);
            // El hijo más a la derecha está en la posición = nroDeDatosNoVacios
            nodoActual = nodoActual.hijos.get(nroDeDatosNoVacios(nodoActual));
        }
        return nodoActual;
    }

    /**
     * Método que se encarga de rebalancear el árbol B cuando un nodo
     * queda con menos datos que el mínimo permitido después de una eliminación.
     * 
     * Estrategia (en orden de prioridad):
     * 1. PRESTAR del hermano DERECHO: si el hermano derecho tiene más datos
     *    que el mínimo, le pedimos prestado su menor dato, rotando por el padre.
     * 2. PRESTAR del hermano IZQUIERDO: si el hermano izquierdo tiene más datos
     *    que el mínimo, le pedimos prestado su mayor dato, rotando por el padre.
     * 3. FUSIONAR: si ningún hermano puede prestar, fusionamos el nodo insuficiente
     *    con un hermano, bajando la clave separadora del padre al nodo fusionado.
     *    Si el padre también queda bajo el mínimo, se repite el proceso hacia arriba.
     */
    public void prestarseOFusionarse(NodoMVias<T> nodoInsuficiente, Stack<NodoMVias<T>> pilaDeNodos) {

        // Este bucle permite propagar la fusión hacia arriba si el padre
        // también queda por debajo del mínimo después de cederle una clave
        while (true) {

            // ===== CASO ESPECIAL: el nodo insuficiente es la raíz =====
            // Si la pila está vacía, no hay padre al que pedirle nada.
            // Esto pasa cuando la fusión propagó hasta la raíz y la raíz
            // quedó con 0 claves. En ese caso, su único hijo pasa a ser la nueva raíz.
            if (pilaDeNodos.isEmpty()) {
                if (nroDeDatosNoVacios(nodoInsuficiente) == 0
                        && !esNodoHoja(nodoInsuficiente)) {
                    // La raíz se quedó sin claves, su hijo 0 es la nueva raíz
                    raiz = nodoInsuficiente.hijos.get(0);
                }
                return; // Ya no hay nada más que hacer
            }

            // ===== Obtener el padre y la posición del nodo insuficiente =====
            NodoMVias<T> nodoPadre = pilaDeNodos.pop();

            // Buscar en qué posición de los hijos del padre está nuestro nodo
            int posHijo = 0;
            while (nodoPadre.hijos.get(posHijo) != nodoInsuficiente) {
                posHijo++;
            }

            // ===== Identificar hermanos disponibles =====
            // hermano derecho: existe si posHijo < nroDeDatosNoVacios(nodoPadre)
            // hermano izquierdo: existe si posHijo > 0
            NodoMVias<T> hermanoDer = (posHijo < nroDeDatosNoVacios(nodoPadre))
                    ? nodoPadre.hijos.get(posHijo + 1) : null;
            NodoMVias<T> hermanoIzq = (posHijo > 0)
                    ? nodoPadre.hijos.get(posHijo - 1) : null;

            // =====================================================
            // CASO 1: PRESTAR DEL HERMANO DERECHO
            // =====================================================
            // Condición: el hermano derecho existe y tiene más datos que el mínimo
            //
            // Rotación:
            //   - La clave del padre (que separa nodoInsuficiente del hermanoDer)
            //     baja al nodoInsuficiente
            //   - La menor clave del hermanoDer sube al padre para ocupar su lugar
            //
            // Ejemplo (orden 3, min datos = 1):
            //   Padre: [15]   Insuficiente: []   HermanoDer: [20, 25]
            //   → El 15 baja al insuficiente, el 20 sube al padre
            //   Padre: [20]   Insuficiente: [15]  HermanoDer: [25]
            if (hermanoDer != null && nroDeDatosNoVacios(hermanoDer) > getNroMinimoDeDatos()) {
                // La clave del padre que separa ambos nodos está en posHijo
                T datoPadre = nodoPadre.datos.get(posHijo);
                // La menor clave del hermano derecho (la que subirá al padre)
                T datoHermanoDer = hermanoDer.datos.get(0);

                // 1. Bajar la clave del padre al nodo insuficiente
                insertarDatoOrdenadoEnElNodo(nodoInsuficiente, datoPadre);

                // 2. Subir la clave del hermano derecho al padre (reemplazo directo,
                //    NO eliminamos ni insertamos en el padre para no alterar sus hijos)
                nodoPadre.datos.set(posHijo, datoHermanoDer);

                // 3. Eliminar la clave prestada del hermano derecho
                eliminarDatoPosicion(hermanoDer, 0);

                // Si los nodos NO son hojas, también hay que mover el hijo
                // más a la izquierda del hermano derecho al nodo insuficiente
                if (!esNodoHoja(hermanoDer)) {
                    // El primer hijo del hermano derecho pasa a ser el último hijo del insuficiente
                    int nroDatosInsuficiente = nroDeDatosNoVacios(nodoInsuficiente);
                    nodoInsuficiente.hijos.set(nroDatosInsuficiente, hermanoDer.hijos.get(0));
                    // Desplazar los hijos del hermano derecho a la izquierda
                    int nroDatosHermano = nroDeDatosNoVacios(hermanoDer);
                    for (int j = 0; j < nroDatosHermano + 1; j++) {
                        hermanoDer.hijos.set(j, hermanoDer.hijos.get(j + 1));
                    }
                    hermanoDer.hijos.set(nroDatosHermano + 1, NODO_VACIO);
                }

                return; // El préstamo fue exitoso, no hay que propagar

            // =====================================================
            // CASO 2: PRESTAR DEL HERMANO IZQUIERDO
            // =====================================================
            // Condición: el hermano izquierdo existe y tiene más datos que el mínimo
            //
            // Rotación:
            //   - La clave del padre (que separa hermanoIzq del nodoInsuficiente)
            //     baja al nodoInsuficiente
            //   - La mayor clave del hermanoIzq sube al padre
            //
            // Ejemplo (orden 3, min datos = 1):
            //   Padre: [15]   HermanoIzq: [5, 10]   Insuficiente: []
            //   → El 15 baja al insuficiente, el 10 sube al padre
            //   Padre: [10]   HermanoIzq: [5]   Insuficiente: [15]
            } else if (hermanoIzq != null && nroDeDatosNoVacios(hermanoIzq) > getNroMinimoDeDatos()) {
                // La clave del padre que separa ambos está en posHijo - 1
                T datoPadre = nodoPadre.datos.get(posHijo - 1);
                int nroDatosHermanoIzq = nroDeDatosNoVacios(hermanoIzq);
                // La mayor clave del hermano izquierdo (la que subirá al padre)
                T datoHermanoIzq = hermanoIzq.datos.get(nroDatosHermanoIzq - 1);

                // 1. Bajar la clave del padre al nodo insuficiente
                insertarDatoOrdenadoEnElNodo(nodoInsuficiente, datoPadre);

                // 2. Subir la clave del hermano izquierdo al padre (reemplazo directo)
                nodoPadre.datos.set(posHijo - 1, datoHermanoIzq);

                // 3. Eliminar la clave prestada del hermano izquierdo
                eliminarDatoPosicion(hermanoIzq, nroDatosHermanoIzq - 1);

                // Si los nodos NO son hojas, también hay que mover el hijo
                // más a la derecha del hermano izquierdo al nodo insuficiente
                if (!esNodoHoja(hermanoIzq)) {
                    // Desplazar los hijos del insuficiente a la derecha para hacer espacio
                    int nroDatosInsuficiente = nroDeDatosNoVacios(nodoInsuficiente);
                    for (int j = nroDatosInsuficiente; j > 0; j--) {
                        nodoInsuficiente.hijos.set(j, nodoInsuficiente.hijos.get(j - 1));
                    }
                    // El último hijo del hermano izquierdo pasa a ser el primer hijo del insuficiente
                    nodoInsuficiente.hijos.set(0, hermanoIzq.hijos.get(nroDatosHermanoIzq));
                    hermanoIzq.hijos.set(nroDatosHermanoIzq, NODO_VACIO);
                }

                return; // El préstamo fue exitoso, no hay que propagar

            // =====================================================
            // CASO 3: FUSIONAR (MERGE)
            // =====================================================
            // Ningún hermano puede prestar → fusionamos.
            // Siempre fusionamos en el hijo IZQUIERDO:
            //   - Bajamos la clave separadora del padre al hijo izquierdo
            //   - Pasamos todos los datos del hijo derecho al hijo izquierdo
            //   - Eliminamos la clave y el hijo derecho del padre
            //   - Si el padre queda bajo el mínimo, repetimos el proceso hacia arriba
            //
            // Ejemplo (orden 3, min datos = 1):
            //   Padre: [15]   HijoIzq: [10]   HijoDer: [20]   ← fusionamos
            //   → Bajamos el 15 al HijoIzq, movemos el 20 al HijoIzq
            //   → HijoIzq: [10, 15, 20]   (se eliminan clave y HijoDer del padre)
            //   → El padre quedó con 0 claves → si es la raíz, HijoIzq se vuelve raíz
            } else {
                // Determinar cuál par de hermanos fusionar
                int posFusion; // posición de la clave separadora en el padre
                NodoMVias<T> hijoIzq;
                NodoMVias<T> hijoDer;

                if (posHijo > 0) {
                    // Fusionar con el hermano izquierdo
                    // El nodoInsuficiente es el hijo derecho, hermanoIzq es el hijo izquierdo
                    posFusion = posHijo - 1;
                    hijoIzq = hermanoIzq;
                    hijoDer = nodoInsuficiente;
                } else {
                    // posHijo == 0, fusionar con el hermano derecho
                    // El nodoInsuficiente es el hijo izquierdo, hermanoDer es el hijo derecho
                    posFusion = posHijo; // que es 0
                    hijoIzq = nodoInsuficiente;
                    hijoDer = hermanoDer;
                }

                // 1. Bajar la clave separadora del padre al hijo izquierdo
                T claveSeparadora = nodoPadre.datos.get(posFusion);
                insertarDatoOrdenadoEnElNodo(hijoIzq, claveSeparadora);

                // 2. Pasar todos los datos del hijo derecho al hijo izquierdo
                int nroDatosHijoDer = nroDeDatosNoVacios(hijoDer);
                for (int j = 0; j < nroDatosHijoDer; j++) {
                    insertarDatoOrdenadoEnElNodo(hijoIzq, hijoDer.datos.get(j));
                }

                // 3. Si NO son hojas, también pasar los hijos del hijo derecho
                //    al hijo izquierdo (se colocan al final, después de los hijos existentes)
                if (!esNodoHoja(hijoDer)) {
                    // Calcular dónde empezar a colocar los hijos del hijoDer en el hijoIzq
                    // Los hijos del hijoIzq ya ocupan posiciones 0..nroDatosHijoIzq
                    // (antes de la fusión el hijoIzq tenía nroDatosHijoIzq datos,
                    //  ahora tiene más, pero los hijos originales están en las primeras posiciones)
                    int posInicioHijos = nroDeDatosNoVacios(hijoIzq) - nroDatosHijoDer;
                    for (int j = 0; j <= nroDatosHijoDer; j++) {
                        hijoIzq.hijos.set(posInicioHijos + j, hijoDer.hijos.get(j));
                    }
                }

                // 4. Eliminar la clave separadora y el hijo derecho del padre
                //    eliminarClaveYHijoDerecho quita la clave en posFusion
                //    y desplaza los hijos desde posFusion+1, descartando al hijoDer
                eliminarClaveYHijoDerecho(nodoPadre, posFusion);

                // 5. Verificar si el padre también quedó por debajo del mínimo
                //    Si el padre es la raíz y quedó con 0 claves, el hijoIzq se vuelve raíz
                if (nodoPadre == raiz && nroDeDatosNoVacios(nodoPadre) == 0) {
                    raiz = hijoIzq;
                    return;
                }

                // Si el padre NO es la raíz y quedó bajo el mínimo, propagamos
                if (nroDeDatosNoVacios(nodoPadre) < getNroMinimoDeDatos()) {
                    // El padre ahora es el nodo insuficiente, seguimos el bucle
                    nodoInsuficiente = nodoPadre;
                    // El bucle continuará con el siguiente padre de la pila
                } else {
                    return; // El padre sigue cumpliendo el mínimo, terminamos
                }
            }
        } // fin del while(true)
    }

    @Override
    public void eliminar(T datoEliminar) { //ubicar donde ira el fusionar y el prestar
        if (esDatoVacio(datoEliminar)) {
            throw new IllegalArgumentException("El dato a eliminar no puede ser vacio");
        }
        NodoMVias<T> nodoActual = raiz;
        Stack<NodoMVias<T>> pilaDeNodos = new Stack<>();
        NodoMVias<T> nodoDelDatoEliminar = NODO_VACIO;
        int posDelDatoEliminar = POSICION_INVALIDA;
        while (!esNodoVacio(nodoActual)){
            int posDelDatoEnNodo = obtenerPosicionDeDatoEnNodo(nodoActual,datoEliminar);
            if (posDelDatoEnNodo != POSICION_INVALIDA){
                nodoDelDatoEliminar = nodoActual;
                posDelDatoEliminar = posDelDatoEnNodo;
                nodoActual = NODO_VACIO;
            } else {
                int posPorDondeBajar = obtenerPosicionPorDondeBajar(nodoActual,datoEliminar);
                pilaDeNodos.push(nodoActual);
                nodoActual = nodoActual.hijos.get(posPorDondeBajar);
            }
        }

        if (esNodoVacio(nodoDelDatoEliminar)){
            throw new IllegalArgumentException("El dato no se encuentra en el arbol");
        }

        if (esNodoHoja(nodoDelDatoEliminar)) {
            eliminarDatoPosicion(nodoDelDatoEliminar, posDelDatoEliminar);
            if (nroDeDatosNoVacios(nodoDelDatoEliminar)<getNroMinimoDeDatos()) {
                prestarseOFusionarse(nodoDelDatoEliminar,pilaDeNodos);
            }
        } else {
            pilaDeNodos.push(nodoDelDatoEliminar);
            NodoMVias<T> nodoDelPredecesor = obtenerNodoDelPredecesor(
                    nodoDelDatoEliminar.hijos.get(posDelDatoEliminar),
                    pilaDeNodos
            );
            T datoPredecesor = nodoDelPredecesor.datos.get(
                    nroDeDatosNoVacios(nodoDelPredecesor)-1
            );
            nodoDelPredecesor.datos.set(nroDeDatosNoVacios(nodoDelPredecesor)-1,
                    (T)DATO_VACIO);
            nodoDelDatoEliminar.datos.set(posDelDatoEliminar, datoPredecesor);
            if (nroDeDatosNoVacios(nodoDelPredecesor) < getNroMinimoDeDatos()) {
                prestarseOFusionarse(nodoDelPredecesor,pilaDeNodos);
            }
        }
    }

    @Override
    public T buscar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El dato no puede ser nulo");
        }
        NodoMVias<T> nodoMVias = raiz;
        while (!esNodoVacio(nodoMVias)) {
            int posDato = obtenerPosicionDeDatoEnNodo(nodoMVias, dato);
            if (posDato != POSICION_INVALIDA) {
                return nodoMVias.datos.get(posDato);
            }
            int bajar = obtenerPosicionPorDondeBajar(nodoMVias, dato);
            nodoMVias = nodoMVias.hijos.get(bajar);
        }
        return null;
    }

    @Override
    public boolean contiene(T dato) {
        return buscar(dato) != null;
    }

    @Override
    public int size() {
        return size(raiz);
    }

    private int size(NodoMVias<T> nodoMVias){
        if (esNodoVacio(nodoMVias)){
            return 0;
        }
        int cantidad = nroDeDatosNoVacios(nodoMVias);
        for (int i = 0; i <= nroDeDatosNoVacios(nodoMVias); i++){
            cantidad += size(nodoMVias.hijos.get(i));
        }
        return cantidad;
    }

    @Override
    public int altura() {
        return altura(raiz);
    }

    private int altura(NodoMVias<T> nodoMVias){
        if (esNodoVacio(nodoMVias)){
            return 0;
        }
        int alturaMayor=0;
        for (int i = 0; i <= nroDeDatosNoVacios(nodoMVias); i++){
           int alturaDeUnHijo = altura(nodoMVias.hijos.get(i));
           if (alturaDeUnHijo > alturaMayor){
               alturaMayor = alturaDeUnHijo;
           }
        }
        return alturaMayor + 1;
    }

    @Override
    public void vaciar() {
        raiz = NODO_VACIO;
    }

    @Override
    public boolean esArbolVacio() {
        return esNodoVacio(raiz);
    }

    @Override
    public int nivel() {
        return nivel(raiz);
    }

    private int nivel(NodoMVias<T> nodoMVias){
        if (esNodoVacio(nodoMVias)) {
            return 0;
        }
        int nivelMayor = 0;
        for (int i=0; i <= nroDeDatosNoVacios(nodoMVias); i++){
            int nivelHijo = nivel(nodoMVias.hijos.get(i));
            if (nivelHijo > nivelMayor) {
                nivelMayor = nivelHijo;
            }
        }
        return nivelMayor;
    }

    @Override
    public List<T> recorridoEnPreOrden() {
        List<T> recorrido = new ArrayList<>();
        recorridoEnPreOrden(this.raiz, recorrido);
        return recorrido;
    }

    private void recorridoEnPreOrden(NodoMVias<T> nodoActual, List<T> recorrido) {
        if (esNodoVacio(nodoActual)) {
            return;
        }
        for (int i = 0; i < nroDeDatosNoVacios(nodoActual); i++) {
            T datoActual = nodoActual.datos.get(i);
            recorrido.add(datoActual);
            recorridoEnPreOrden(nodoActual.hijos.get(i), recorrido);
        }
        recorridoEnPreOrden(nodoActual.hijos.get(nroDeDatosNoVacios(nodoActual)), recorrido);
    }

    @Override
    public List<T> recorridoEnInOrden() {
        List<T> recorrido = new ArrayList<>();
        recorridoEnInOrden(this.raiz, recorrido);
        return recorrido;
    }

    private void recorridoEnInOrden(NodoMVias<T> nodoActual, List<T> recorrido) {
        if (esNodoVacio(nodoActual)) {
            return;
        }
        for (int i = 0; i < nroDeDatosNoVacios(nodoActual); i++) {
            recorridoEnInOrden(nodoActual.hijos.get(i), recorrido);
            T datoActual = nodoActual.datos.get(i);
            recorrido.add(datoActual);
        }
        recorridoEnInOrden(nodoActual.hijos.get(nroDeDatosNoVacios(nodoActual)), recorrido);
    }

    @Override
    public List<T> recorridoEnPostOrden() {
        List<T> recorrido = new ArrayList<>();
        recorridoEnPostOrden(this.raiz, recorrido);
        return recorrido;
    }

    private void recorridoEnPostOrden(NodoMVias<T> nodoActual, List<T> recorrido) {
        if (esNodoVacio(nodoActual)) {
            return;
        }
        recorridoEnPostOrden(nodoActual.hijos.get(0), recorrido);
        for (int i = 0; i < nroDeDatosNoVacios(nodoActual); i++) {
            recorridoEnPostOrden(nodoActual.hijos.get(i + 1), recorrido);
            T datoActual = nodoActual.datos.get(i);
            recorrido.add(datoActual);
        }
    }

    @Override
    public List<T> recorridoPorNiveles() {
        if (esArbolVacio()) {
            throw new IllegalArgumentException("El arbol esta vacio");
        }
        List<T> recorrido = new ArrayList<>();
        Queue<NodoMVias<T>> colaDeNodos = new LinkedList<>();
        colaDeNodos.offer(raiz);
        do {
            NodoMVias<T> nodo = colaDeNodos.poll();
            for (int i = 0; i < nroDeDatosNoVacios(nodo); i++) {
                recorrido.add(nodo.datos.get(i));
                if (!esNodoVacio(nodo.hijos.get(i))) {
                    colaDeNodos.offer(nodo.hijos.get(i));
                }
            }
            if (!esNodoVacio(nodo.hijos.get(nroDeDatosNoVacios(nodo)))) {
                colaDeNodos.offer(nodo.hijos.get(nroDeDatosNoVacios(nodo)));
            }
        } while (!colaDeNodos.isEmpty());
        return recorrido;
    }

    public T predecesorInOrden (NodoMVias<T> nodoMVias, int indice){
        NodoMVias<T> nodoPrueba = nodoMVias.hijos.get(indice);
        while (!esNodoHoja(nodoPrueba)){
            nodoPrueba = nodoPrueba.hijos.get(nroDeDatosNoVacios(nodoPrueba));
        }
        return nodoPrueba.datos.get(nroDeDatosNoVacios(nodoPrueba) - 1);
    }

    public int cantidadNodosLlenosDesdeNivel(int nivel){
        return cantidadNodosLlenosDesdeNivel(this.raiz, 0, nivel);
    }

    private int cantidadNodosLlenosDesdeNivel(NodoMVias<T> nodoActual, int nivelActual, int nivelBuscado) {
        if (esNodoVacio(nodoActual)) {
            return 0;
        }
        int cantidad = 0;
        if (nivelActual >= nivelBuscado) {
            if (nroDeDatosNoVacios(nodoActual) == this.orden - 1) {
                cantidad++;
            }
        }
        for (int i = 0; i <= nroDeDatosNoVacios(nodoActual); i++) {
            cantidad += cantidadNodosLlenosDesdeNivel(nodoActual.hijos.get(i),
                    nivelActual + 1, nivelBuscado);
        }
        return cantidad;
    }

    @Override
    public String toString() {
        if (esArbolVacio()) {
            return "Arbol vacio";
        }
        StringBuilder sb = new StringBuilder();
        toString(raiz, sb, "", true);
        return sb.toString();
    }

    private void toString(NodoMVias<T> nodo, StringBuilder sb, String prefijo, boolean esUltimo) {
        if (esNodoVacio(nodo)) {
            return;
        }
        sb.append(prefijo);
        sb.append(esUltimo ? "└── " : "├── ");

        // Mostrar los datos del nodo entre corchetes
        sb.append("[");
        int nroDatos = nroDeDatosNoVacios(nodo);
        for (int i = 0; i < nroDatos; i++) {
            sb.append(nodo.datos.get(i));
            if (i < nroDatos - 1) {
                sb.append(" | ");
            }
        }
        sb.append("]\n");

        // Recorrer los hijos
        String nuevoPrefijo = prefijo + (esUltimo ? "    " : "│   ");
        int totalHijos = nroDatos + 1;
        for (int i = 0; i < totalHijos; i++) {
            if (!esNodoVacio(nodo.hijos.get(i))) {
                toString(nodo.hijos.get(i), sb, nuevoPrefijo, i == totalHijos - 1);
            }
        }
    }
}
