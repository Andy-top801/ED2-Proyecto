package ClasificacionDeViajes;

import java.util.List;

public class MonticuloMaximo<T extends Comparable<T>> extends Monticulo<T> {
    public MonticuloMaximo(){
        super();
    }

    public MonticuloMaximo(List<T> lista){
        super(lista);
    }

    @Override
    protected boolean seNecesitaIntercambiar(T datoDelPadre, T datoDelHijo) {
        return datoDelPadre.compareTo(datoDelHijo) < 0;
    }
    
}
