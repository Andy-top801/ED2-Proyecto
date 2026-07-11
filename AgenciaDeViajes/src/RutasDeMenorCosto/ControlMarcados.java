package RutasDeMenorCosto;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jherargonzales
 */
public class ControlMarcados {
        private List<Boolean> marcados;
        
        public ControlMarcados(int nroDeVertices){
            if (nroDeVertices <= 0) {
                throw new IllegalArgumentException("Cantidad de los vertices debe ser mayor a 0");
            }
            marcados = new ArrayList<>();
            for (int i = 0; i < nroDeVertices; i++) {
                marcados.add(false);
            }
        }
        
        public void marcarVertice(int posDeVertice){
            marcados.set(posDeVertice, true);
        }
        
        public void desmarcarVertice(int posDeVertice){
            marcados.set(posDeVertice, false);
        }
        
        public void desmarcarTodos(){
            for (int i = 0; i < marcados.size(); i++) {
                marcados.set(i,false);
            }
        }
        
        public boolean estaVerticeMarcado(int posDeVertice){
            return marcados.get(posDeVertice);
        }
        
        public boolean estanTodosMarcados(){
            return !marcados.contains(false);
        }
  
    
}
