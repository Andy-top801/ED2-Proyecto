package RegistroDePasajeros;

import java.util.*;

/**
 * @param <T>
 * @author Andy
 */
public abstract class ArbolB<T extends Comparable<T>> extends
        ArbolMViasBase<T> implements Prub<T> {

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

    private void desplazarDatosALaDerecha(NodoMVias<T> elNodo, int posDelPrimerMayor){
        for (int posDatoAMover = nroDeDatosNoVacios(elNodo) - 1; posDatoAMover >= posDelPrimerMayor; posDatoAMover--) {
            T datoEnTurno = elNodo.datos.get(posDatoAMover);
            elNodo.datos.set(posDatoAMover + 1, datoEnTurno);
            // Movemos el hijo de la derecha del dato en turno
            NodoMVias<T> hijoEnTurno = elNodo.hijos.get(posDatoAMover + 1); 
            elNodo.hijos.set(posDatoAMover + 2, hijoEnTurno);
        }
        // Desplaza el hijo de la izquierda del bloque que acabamos de mover, 
        // para no aplastarlo ni perderlo en la memoria.
        elNodo.hijos.set(posDelPrimerMayor + 1, elNodo.hijos.get(posDelPrimerMayor));
    }

    private void eliminarHoja(NodoMVias<T> nodoActual,Stack<NodoMVias<T>> pilaDeNodos,Stack<Integer>pilaDePosicionesDeNodos,int posDelDatoAEliminar){
        eliminarDatoDelNodo(nodoActual,posDelDatoAEliminar);
        while (!esNodoVacio(nodoActual) && nroDeDatosNoVacios(nodoActual) < getNroMinimoDeDatos()) { 
            if (!pilaDeNodos.isEmpty()) {
                NodoMVias<T> nodoPadre = pilaDeNodos.pop();
                int posDelHijoEnNodoPadre = pilaDePosicionesDeNodos.pop();
                prestarOrFusionar(nodoActual,nodoPadre,posDelHijoEnNodoPadre);
                nodoActual = nodoPadre;
            } else {
                nodoActual = NODO_VACIO;
            }
        }
                    
        if (!esNodoHoja(raiz) && nroDeDatosNoVacios(raiz) == 0) {
           raiz = raiz.hijos.get(0);
        }
    }
    
    private void eliminarDatoDelNodo(NodoMVias<T> nodoActual, int  posDelDatoAEliminar){
        int nroDatos = nroDeDatosNoVacios(nodoActual);
        //  Desplazamos los DATOS 
        for (int i = posDelDatoAEliminar; i < nroDatos - 1; i++) {
            nodoActual.datos.set(i, nodoActual.datos.get(i + 1));
        }
        nodoActual.datos.set(nroDatos - 1, (T) DATO_VACIO); // Limpiar último dato
    }
    
    private void eliminarHijoDelNodo(NodoMVias<T> elNodo, int posDelHijoAEliminar) {
        int nroDatos = nroDeDatosNoVacios(elNodo);
        // Desplazamos los hijos un espacio hacia la izquierda
        for (int i = posDelHijoAEliminar; i < nroDatos; i++) {
            elNodo.hijos.set(i, elNodo.hijos.get(i + 1));
        }
        // eliminamos el último hijo que quedó duplicado
        elNodo.hijos.set(nroDatos, NODO_VACIO);
    }
    
    private void prestarDelHermanoDerecho(NodoMVias<T> nodoActual,NodoMVias<T> nodoPadre, int posHermanoDerecho, int posDelHijoEnNodoPadre){
        NodoMVias<T> nodoHermanoDerecho = nodoPadre.hijos.get(posHermanoDerecho);
        NodoMVias<T> hijoDelHermanoDerecho = NODO_VACIO;
        T datoDelHermanoDerecho = nodoHermanoDerecho.datos.get(0);
        
        // Si tiene hijo el datoDelHermanoDerecho 
        if (!esNodoHoja(nodoHermanoDerecho)) {
            hijoDelHermanoDerecho = nodoHermanoDerecho.hijos.get(0);
            eliminarHijoDelNodo(nodoHermanoDerecho, 0);
        }
        
        // eliminamos el datoDelHermano derecho
        eliminarDatoDelNodo(nodoHermanoDerecho,0);
        
        // guardamos el datoDelPadre antes de reemplazarlo
        T datoDelPadre = nodoPadre.datos.get(posDelHijoEnNodoPadre);
        
        // reemplazamos al padre por el dato del hermanoDerecho
        nodoPadre.datos.set(posDelHijoEnNodoPadre, datoDelHermanoDerecho);
        
        // colocamos el hijoPrestado del hermanoDerecho como que tendran los mayores del ultimo dato
        nodoActual.hijos.set(nroDeDatosNoVacios(nodoActual)+1, hijoDelHermanoDerecho);
        
        // colocamos el datoDelPadre en el ultimo espacio ya que siempre sera mayor
         nodoActual.datos.set(nroDeDatosNoVacios(nodoActual), datoDelPadre); 
    }
    
    private void prestarDelHermanoIzquierdo(NodoMVias<T> nodoActual,NodoMVias<T> nodoPadre, int posHermanoIzquierdo, int posDelHijoEnNodoPadre){
        NodoMVias<T> nodoHermanoIzquierdo = nodoPadre.hijos.get(posHermanoIzquierdo);
        int posUltimoDatoHermano = nroDeDatosNoVacios(nodoHermanoIzquierdo) - 1;
        T datoDelHermanoIzquierdo = nodoHermanoIzquierdo.datos.get(posUltimoDatoHermano);
        NodoMVias<T> hijoDelHermanoIzquierdo = NODO_VACIO;
        
        if (!esNodoHoja(nodoHermanoIzquierdo)) {
            //el último hijo está en el índice N
            hijoDelHermanoIzquierdo = nodoHermanoIzquierdo.hijos.get(nroDeDatosNoVacios(nodoHermanoIzquierdo));
            // Como es el último, no hay que desplazar nada, solo lo borramos
            nodoHermanoIzquierdo.hijos.set(nroDeDatosNoVacios(nodoHermanoIzquierdo), NODO_VACIO);
        }

        // Eliminamos el último dato de forma segura
        eliminarDatoDelNodo(nodoHermanoIzquierdo, posUltimoDatoHermano);
        
        // Rotación de datos con el padre
        // El separador está en la misma posición que el hermano izquierdo
        T datoDelPadre = nodoPadre.datos.get(posHermanoIzquierdo); 
        nodoPadre.datos.set(posHermanoIzquierdo, datoDelHermanoIzquierdo);

        // desplazamos los datos una pos a la derecha 
        desplazarDatosALaDerecha(nodoActual, 0);

        //Insertar los elementos robados en los huecos que acaban de quedar en el índice 0
        nodoActual.datos.set(0, datoDelPadre);
        nodoActual.hijos.set(0, hijoDelHermanoIzquierdo);
    }
    
    private void fusionarConHermanoDerecho(NodoMVias<T> nodoActual, NodoMVias<T> nodoPadre, int posHermanoDerecho){
        NodoMVias<T> hermanoDerecho = nodoPadre.hijos.get(posHermanoDerecho);
        int indiceDatoDestino = nroDeDatosNoVacios(nodoActual); 
        
        //  Bajamos el datoDelPadre 
        T datoDelPadre = nodoPadre.datos.get(posHermanoDerecho - 1);
        nodoActual.datos.set(indiceDatoDestino, datoDelPadre);
        indiceDatoDestino++; // Avanzamos a la siguiente espacio libre de dato
        int indiceHijoDestino = indiceDatoDestino;
        
        // Copiamos los datos del hermano derecho
        int datosHermano = nroDeDatosNoVacios(hermanoDerecho);
        for (int i = 0; i < datosHermano; i++) {
            nodoActual.datos.set(indiceDatoDestino, hermanoDerecho.datos.get(i));
            indiceDatoDestino++; // Avanza al siguiente hueco
        }
    
        // Copiamos los hijos del hermano derecho
        for (int i = 0; i <= datosHermano; i++) {
            nodoActual.hijos.set(indiceHijoDestino, hermanoDerecho.hijos.get(i));
            indiceHijoDestino++; // Avanza de forma independiente
        }
        
        //  eliminamos el hermano Derecho
        eliminarHijoDelNodo(nodoPadre, posHermanoDerecho);
        
        // eliminamos el dato que bajo
        eliminarDatoDelNodo(nodoPadre, posHermanoDerecho - 1); 
    }
    
    private void fusionarConHermanoIzquierdo(NodoMVias<T> nodoActual, NodoMVias<T> nodoPadre, int posHermanoIzquierdo){
        NodoMVias<T> hermanoIzquierdo = nodoPadre.hijos.get(posHermanoIzquierdo);
        int indiceDatoDestino = nroDeDatosNoVacios(hermanoIzquierdo); 
    
        //  Bajamos el datoDelPadre 
        T datoDelPadre = nodoPadre.datos.get(posHermanoIzquierdo);
        hermanoIzquierdo.datos.set(indiceDatoDestino, datoDelPadre);
        indiceDatoDestino++; // Avanzamos al siguiente espacio libre
        int indiceHijoDestino = indiceDatoDestino;
    
        //  Copiamos los datos del nodoActual al hermano izquierdo
        int datosActuales = nroDeDatosNoVacios(nodoActual);
        for (int i = 0; i < datosActuales; i++) {
            hermanoIzquierdo.datos.set(indiceDatoDestino, nodoActual.datos.get(i));
            indiceDatoDestino++; 
        }

        // Copiamos los hijos del nodoActual al hermano izquierdo
        for (int i = 0; i <= datosActuales; i++) {
            hermanoIzquierdo.hijos.set(indiceHijoDestino, nodoActual.hijos.get(i));
            indiceHijoDestino++; 
        }
    
        // Eliminamos el nodoActual de los hijos del nodoPadre 
        eliminarHijoDelNodo(nodoPadre, posHermanoIzquierdo + 1);

        // Eliminamos el dato que bajó del nodoPadre
        eliminarDatoDelNodo(nodoPadre, posHermanoIzquierdo);
    }
    
    private void prestarOrFusionar(NodoMVias<T> nodoActual, NodoMVias<T> nodoPadre, int posDelHijoEnNodoPadre){
        int posHermanoIzquierdo = posDelHijoEnNodoPadre - 1;
        int posHermanoDerecho = posDelHijoEnNodoPadre + 1;
        // 1. Prestarse un dato del hermano de la SIGUIENTE posición (Derecho)
        if (posHermanoDerecho <= nroDeDatosNoVacios(nodoPadre) && 
            nroDeDatosNoVacios(nodoPadre.hijos.get(posHermanoDerecho)) > getNroMinimoDeDatos()) {
            prestarDelHermanoDerecho(nodoActual, nodoPadre, posHermanoDerecho, posDelHijoEnNodoPadre);
        } 
        // 2. Prestarse un dato del hermano de la ANTERIOR posición (Izquierdo)
        else if (posHermanoIzquierdo >= 0 && 
                 nroDeDatosNoVacios(nodoPadre.hijos.get(posHermanoIzquierdo)) > getNroMinimoDeDatos()) {
            prestarDelHermanoIzquierdo(nodoActual, nodoPadre, posHermanoIzquierdo, posDelHijoEnNodoPadre);
        } 
        // 3. Fusionar con el hermano de la SIGUIENTE posición (Derecho)
        else if (posHermanoDerecho <= nroDeDatosNoVacios(nodoPadre)) {
            fusionarConHermanoDerecho(nodoActual, nodoPadre, posHermanoDerecho);
        } 
        // 4. Fusionar con el hermano de la ANTERIOR posición (Izquierdo)
        else if (posHermanoIzquierdo >= 0) {
            fusionarConHermanoIzquierdo(nodoActual, nodoPadre, posHermanoIzquierdo);
        }
    }
    
    // MÉTODO CORREGIDO: Reemplaza antes de eliminar para protegerse del Underflow
    private void reemplazarYEliminarPredecesorInOrden(NodoMVias<T> nodoOriginal, int posDelDatoOriginal, Stack<NodoMVias<T>> pilaDeNodos, Stack<Integer> pilaDePosicionesDeNodos){
        // Guardamos el nodo original en el rastro
        pilaDeNodos.push(nodoOriginal);
        pilaDePosicionesDeNodos.push(posDelDatoOriginal);
        
        // Bajamos por el hijo izquierdo
        NodoMVias<T> nodoActual = nodoOriginal.hijos.get(posDelDatoOriginal);
        
        // Bajamos lo más a la derecha posible
        while (!esNodoHoja(nodoActual)) {
            int posMasALaDerecha = nroDeDatosNoVacios(nodoActual);
            pilaDePosicionesDeNodos.push(posMasALaDerecha);
            pilaDeNodos.push(nodoActual);
            nodoActual = nodoActual.hijos.get(posMasALaDerecha);
        }
        
        // Obtenemos el valor del predecesor in-orden
        int posDelPredecesorInOrden = nroDeDatosNoVacios(nodoActual) - 1;
        T predecesorInOrden = nodoActual.datos.get(posDelPredecesorInOrden);
        
        // ¡REEMPLAZO SEGURO! Hacemos el cambio antes de que eliminarHoja modifique la estructura
        nodoOriginal.datos.set(posDelDatoOriginal, predecesorInOrden);
        
        // Eliminamos el dato duplicado de la hoja
        eliminarHoja(nodoActual, pilaDeNodos, pilaDePosicionesDeNodos, posDelPredecesorInOrden);
    }

    @Override
    public void eliminar(T datoAEliminar) {
        if (datoAEliminar == null) {
            throw new IllegalArgumentException("El arbol no acepta datos nulos !");
        }
        
        if (esArbolVacio()) {
            throw new IllegalArgumentException("EL ARBOL ESTA VACIO no se Puede eliminar");
        }
        
        NodoMVias<T> nodoActual = raiz;
        Stack<NodoMVias<T>> pilaDeNodos = new Stack<>(); // para recordar los nodos que fuimos bajando hasta llegar al nodo que tiene el datoAEliminar
        Stack<Integer> pilaDePosicionesDeNodos = new Stack<>(); // para recordar las posiciones de los nodos por los que fuimos bajando
        int posDelDatoEnNodo = POSICION_INVALIDA;
        
        do {
            posDelDatoEnNodo = obtenerPosicionDeDatoEnNodo(nodoActual, datoAEliminar);
            if (posDelDatoEnNodo != POSICION_INVALIDA) { // el dato Se Encuentra en el nodoActual
                
                if (esNodoHoja(nodoActual)) { 
                    // 1 CASO : el dato se encuentra en una hoja
                    eliminarHoja(nodoActual,pilaDeNodos,pilaDePosicionesDeNodos,posDelDatoEnNodo);
                    nodoActual = NODO_VACIO;
                } else {
                    // 2 CASO CORREGIDO: el dato se encuentra en un nodo NO HOJA
                    reemplazarYEliminarPredecesorInOrden(nodoActual, posDelDatoEnNodo, pilaDeNodos, pilaDePosicionesDeNodos);
                    nodoActual = NODO_VACIO;
                }
                
            } else { // no esta en el nodo
                int posPorDondeBajar = obtenerPosicionPorDondeBajar(nodoActual,datoAEliminar);
                pilaDePosicionesDeNodos.push(posPorDondeBajar);
                pilaDeNodos.push(nodoActual);
                nodoActual = nodoActual.hijos.get(posPorDondeBajar);
            }
 
        } while (!esNodoVacio(nodoActual));
        
        if (posDelDatoEnNodo == POSICION_INVALIDA) {
            throw new IllegalArgumentException("NO EXISTE EL DATO A ELIMINAR EN EL NODO");
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
