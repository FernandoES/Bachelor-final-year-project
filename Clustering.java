package tfg;
import java.util.Arrays;

/**
 *
 * Clase que permite realizar la agrupación de textos en función de su 
 * similitud
 *
 * @author fernando
 */
public class Clustering {

    public String[][] clusters;
    Clustering(String[] documentos,String idioma,String eleccion,
            String tipoClustering,int k) {
        switch (tipoClustering) {
            case "KMEANS":
                //Matriz que contiene en cada componente de la primera dimensión un
                //grupo diferente de textos, del que el primer componente es el título
                //y los restantes los textos del citado grupo.
                clusters = kMeans(documentos,idioma, eleccion,k);
                break;
            case "HERENCIA":
                //Matriz que contiene en cada componente de la primera dimensión un
                //grupo diferente de textos, del que el primer componente es el título
                //y los restantes los textos del citado grupo.
                clusters = herenciaAglomerativa(documentos,idioma, k);
                break;
        }
    }

    /**
     * Método que agrupa una serie de textos según el método de las k medias
     *
     * @param documentos
     * @param k
     * @return
     */
    public String[][] kMeans(String[] documentos,String idioma,String eleccion,
            int k) {

        Matrices matriz = new Matrices(documentos,idioma);
        int numDocs= matriz.numeros.length;
        int numTerm= matriz.numeros[0].length;
        float numElem;
        float suma;
        float[][] centroide = elegirSemillas(eleccion, matriz.numeros, k);
        k = centroide.length;
        //grupos[cada grupo][cada texto][cada dato de cada texto];
        float grupos[][][] = new float[k][numDocs][numTerm];
        //Rellenamos completamente el array grupos de "-1"
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < numDocs; j++) {
                Arrays.fill(grupos[i][j], -1);
            }
        }
        int grupo;
        boolean algunCambio = true;
        
        //Comprobamos que el algoritmo no haya llegado a la estabilidad
        while (algunCambio) {
            algunCambio = false;
            //Asignamos cada texto al grupo que le corresponde
            for (int i = 0; i < numDocs; i++) {
                grupo = calculaCercano(matriz.numeros[i], centroide);
                System.arraycopy(matriz.numeros[i],0,grupos[grupo][i],0,numTerm);
                for(int j=0;j<k;j++){
                    if(j!=grupo){
                        Arrays.fill(grupos[j][i], -1);
                    }
                }
            }
            //Recalculamos el centroide, que no tiene porqué ser un elemento.
            for (int i = 0; i < k; i++) {//vamos eligiendo cada grupo
                for (int m = 0; m < numTerm; m++) {//elegimos cada dato en todos los textos
                    suma = 0;
                    numElem = 0;
                    for (int j = 0; j < numDocs; j++) {//elegimos cada texto, con la misma posicion
                        //en cuanto a datos.

                        if (grupos[i][j][m] != -1) {
                            suma += grupos[i][j][m];
                            numElem++;
                        }

                    }
                    algunCambio = algunCambio | (centroide[i][m] != (suma / numElem));
                    centroide[i][m] = suma / numElem;
                }
            }
        }
        //Calculamos el máximo número de elementos de un grupo de entre los generados
        int maxNumElem = 0;
        int elementos;
        for (int i = 0; i < k; i++) {
            elementos = 0;
            //Contamos los elementos de cada grupo
            for (int j = 0; j < numDocs; j++) {
                if (grupos[i][j][0] != -1) {
                    elementos++;
                }
            }
            maxNumElem = Math.max(elementos, maxNumElem);
        }
        //Pasamos de la agrupación de números a la agrupación de textos
        String[][] texto = numeroATexto(grupos, documentos, maxNumElem,
                matriz.terminos, centroide);

        return texto;
    }

    /**
     * Método que elige los k elementos a partir de los cuales se comenzarán a
     * agrupar todos los demás en el método de las k medias. Ésta selección se
     * puede realizar eligiendo los elementos más centrados de cada grupo, o de
     * forma aleatoria.
     *
     * @param eleccion
     * @param numeros
     * @param k
     * @return semillas de las uqe comenzar el algoritmo k-means
     */
    public float[][] elegirSemillas(String eleccion, float[][] numeros, int k) {
        int[] posiciones = new int[k];
        int numDocs = numeros.length;
        int numTerm = numeros[0].length;
        float[][] semillas = new float[k][numTerm];
        if (k > numDocs) {
        semillas = new float[numDocs][numTerm];
            System.out.println("documentos insuficientes o grupos excesivos");
            for (int i = 0; i < numDocs; i++) {
                posiciones[i] = i;
            }
            for (int i = 0; i < numDocs; i++) {
                for (int j = 0; j < numTerm; i++) {
                    semillas[i][j] = numeros[posiciones[i]][j];
                }
            }
            return semillas;
        }
        if (eleccion.equals("aleatorio")) {
            int pos = 0;
            int semillaK;
            boolean iguales;
            boolean distintos;
            while (pos < k) {
                iguales = false;
                semillaK = (int) (Math.random() * (numDocs));//Como el aleatorio nunca
                //es 1 y se trunca la parte decimal del total, nunca hay riesgo de 
                //alcanzar numDocs, que sería el siguiente a la última posición del
                //array, por comenzar este en 0.
                 for(int i=0;i<pos;i++){
                if(semillaK==posiciones[i]){
                    iguales=true;
                    break;
                }
                 }
                if(!iguales){ 
                for (int i = 0; i <= pos; i++) {
                    distintos = false;
                    for (int b = 0; b < numTerm; b++) {
                        if (numeros[semillaK][b] != numeros[posiciones[i]][b]) {
                            distintos = true;
                            break;
                        }
                    }
                    iguales = iguales | (!distintos);
                }
                }

                if (!iguales) {
                    posiciones[pos] = semillaK;
                    pos++;
                }
            }

        } else {
            if (!eleccion.equals("centrado")) {
                System.out.println("tipo de semilla desconocido aplicando centrado");
            }
            for (int i = 0; i < k; i++) {
                posiciones[i] = (int) ((int) (i + 0.5) * (numDocs / k));
            }
        }

        for (int i = 0; i < k; i++) {
            System.arraycopy(numeros[posiciones[i]], 0, semillas[i], 0,
                    numTerm);
        }
        return semillas;
    }

    /**
     * Método que calcula el elemento más cercano a un elemento dado.
     *
     * @param elemento
     * @param semillas
     * @return
     */
    public int calculaCercano(float[] elemento, float[][] centroide) {
        float distancia;
        float distanciaMin;
        int grupo = 0;
        distanciaMin = Float.MAX_VALUE;
        for (int i = 0; i < centroide.length; i++) {//Recorremos cada centroide
            distancia = 0;
            //Calculamos la distancia del elemento a cada centroide
            for (int j = 0; j < centroide[i].length; j++) {
                distancia = distancia + Math.abs(elemento[j] - centroide[i][j]);
            }
            if (distancia < distanciaMin) {
                distanciaMin = distancia;
                grupo = i;
            }
        }
        return grupo;
    }

    /**
     * Método que convierte un conjunto de valores en el documento del cual
     * provienen.
     *
     * @param grupos: Números que representan cada texto agrupados
     * @param documentos: Lista de documentos a agrupar
     * @param numero: máximo número de elementos en un grupo
     * @param palabras: Lista de palabras relevantes
     * @param centros: centroides de cada grupo
     * @return textos: string que contiene un grupo en cada primera dimensión,
     * y en la segunda dimensión el título de cada grupo en la primera posición
     * y todos los tweets del propio grupo en las siguientes
     */
    public String[][] numeroATexto(float[][][] grupos, String[] documentos,
            int maxNumElem, String[] palabras, float[][] centros) {
        int posElem;
        int numGrupos= grupos.length;
        int numDocs= grupos[0].length;
        String[][] textos = new String[numGrupos][maxNumElem+1];
        for (int i = 0; i < numGrupos; i++) {
            posElem = 1;
            for (int j = 0; j < numDocs; j++) {
                if (grupos[i][j][0] != -1) {
                    textos[i][posElem] = documentos[j];
                    posElem++;
                }
            }
        }
        String[] titulo = titular(palabras, centros);
        for (int i = 0; i < titulo.length; i++) {
            textos[i][0] = titulo[i];
        }
        return textos;
    }

    /**
     * Método que obtiene la palabra más representativa de un grupo.
     *
     * @param textos
     * @param centros
     * @return
     */
    public String[] titular(String[] textos, float[][] centros) {
        float maxValor;
        int numDocs= centros.length;
        String[] titulares = new String[numDocs];
        int[] posRep = new int[numDocs];
        for (int i = 0; i < numDocs; i++) {
            maxValor = 0;
            for (int j = 0; j < centros[i].length; j++) {
                if (maxValor < centros[i][j]) {
                    maxValor = centros[i][j];
                    posRep[i] = j;
                }
            }
        }
        //Convertimos la posición en palabra
        for (int i = 0; i < numDocs; i++) {
            titulares[i] = textos[posRep[i]];
        }
        return titulares;
    }

    /**
     * 
     * Método que une en un solo grupo todos aquellos grupos que tienen elementos
     * en común, y que recoloca las columnas que tienen grupos para evitar los
     * huecos antes de la última columna con grupos
     * 
     */
    
 
    public int[][] organizarGrupos(int agrupados [][]){
        int numDocs=agrupados.length;
        //Primer grupo para comparar
            for(int i=0;i<numDocs;i++){
                for(int j=0;j<numDocs;j++){
                    if(agrupados[i][j]==-1){
                        break;
                    }
                    //Segundo grupo para comparar
                    for(int m=i+1;m<numDocs;m++){
                        for(int n=0;n<numDocs;n++){
                            if(agrupados[m][n]==-1){
                                break;
                            }
                            //Si un elemento está en dos grupos, volcamos el grupo n en el i
                            if(agrupados[i][j]==agrupados[m][n]&(m!=i|n!=j)){
                                for(int p=0;p<numDocs;p++){
                                               if(agrupados[m][p]==-1){
                                                   break;
                                               }
                                               //Vamos comprobando y coloc. cada elem.
                                                   for(int q=0;q<numDocs;q++){
                                                       if(agrupados[m][p]==agrupados[i][q]){
                                                           break;
                                                       }else if(agrupados[i][q]==-1){
                                                           agrupados[i][q]=agrupados[m][p];
                                                           break;
                                                       }
                                                   }
                                           }
                                //Una vez transpasados todos los elementos vaciamos
                                Arrays.fill(agrupados[m],-1);
                            }
                        }
                    }
                }
            }
            
            //Comprobamos que no haya columnas vacias antes del final
            for(int i=0;i<numDocs;i++){
                //Cuando encontramos una columna vacia la llenamos con la última llena
                if(agrupados[i][0]==-1){
                    for(int j=numDocs-1;j>i;j--){
                        //Localizamos la última columna llena
                        if(agrupados[j][0]!=-1){
                            for(int n=0;n<numDocs;n++){
                                //Localizamos el último elemento de la columna llena
                                if(agrupados[j][n]==-1){
                                    break;
                                }else{
                                    agrupados[i][n]=agrupados[j][n];
                                    agrupados[j][n]=-1;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            return agrupados;
}
    
    /**
     * 
     * Método que recibe un conjunto de strings e indica cuales han de ser 
     * agrupados
     * 
     */
    public int[][] definirUniones(float[][] parcial,float [][]centroides){
        double distGrupos;
            double minima = Double.MAX_VALUE;
            double margen=0.0;//Margen que damos a dos distancias para considerarlas iguales
            int numDocs=centroides.length;//Variable que contiene el num de documentos
            int numTerm=centroides[0].length;//Variable que contiene el num de term valorados
            boolean colocado;//Variable que determina si se ha realizado una colocación
            boolean unir=false;//Variable que determina si puede haber grupos que unir
            //Array que contendrá los grupos a agrupar entre sí
            //En la primera dimensión contiene los grupos a crear
            //En la segunda cada uno de los grupos originarios al entrar al de destino
            int[][] agrupados=new int[numDocs][numDocs];
            for(int i=0;i<numDocs;i++){
                Arrays.fill(agrupados[i],-1);
            }
//Calculamos la distancia mínima entre grupos
            for (int i = 0; i < numDocs; i++) {
                if (centroides[i][0] != -1) {
                    for (int j = i+1; j < numDocs; j++) {
                        if (centroides[j][0] != -1) {
                            distGrupos = 0;
                            //Vamos sumando los componentes de cada distancia entre centroides
                            for (int m = 0; m < numTerm; m++) {
                                distGrupos = distGrupos + Math.abs(
                                        centroides[i][m] - centroides[j][m]);
                                if(distGrupos*(1-margen)>minima){
                                    break;
                                }
                            }
                            //Comprobamos si se ha renovado la distancia minima
                            if(minima>distGrupos*(1+margen)){
                                //En caso de actualizar el valor vaciamos la lista
            for(int n=0;n<numDocs;n++){
                Arrays.fill(agrupados[n],-1);
            }
                minima=distGrupos;
            agrupados[0][0]=i;
            agrupados[0][1]=j;
            unir=false;
            //En caso de alcanzar el valor añadimos a la lista
                            }else if(minima>=distGrupos*(1-margen)&minima<=distGrupos*(1+margen)){
                                unir=true;
                                colocado=false;
                               //Buscamos la columna adecuada
                                for(int m=0;m<numDocs;m++){
                                    if(agrupados[m][0]==-1){
                                        agrupados[m][0]=i;
                                        agrupados[m][1]=j;
                                        colocado=true;
                                        break;
                                    }
                                }
                                if(!colocado){
                                agrupados=organizarGrupos(agrupados);
                                for(int m=0;m<numDocs;m++){
                                    if(agrupados[m][0]==-1){
                                        agrupados[m][0]=i;
                                        agrupados[m][1]=j;
                                        break;
                                    }
                                }
                                }
                    }
                }
            }
            }
            }
            //Comprobamos que no haya grupos repetidos
            if(unir){
                agrupados=organizarGrupos(agrupados);
            }
            return agrupados;
        }
    
    /**
     * Método que une los grupos de parcial y calcula los nuevos centroides
     * de centroides, según se le indica en agrupados
     * @param centroides Cada uno de los centroides de los grupos generados
     * @param parcial Lista de los elementos de cada grupo
     * @param agrupados Lista de los grupos que han de unirse entre sí
     * @return 
     */
    public float[][][] unir(float[][] centroides, float parcial[][],int[][] agrupados){
        int numDocs=centroides.length;//Número de documentos a analizar.
        int numTerm=centroides[0].length;//Número de términos a tener en cuenta.
        for(int i=0;i<numDocs;i++){
            if(agrupados[i][0]==-1){
                break;
            }
                    //obtenemos la última posición
                    int numGrupos=0;
                    for(int j=1;j<numDocs;j++){
                       if(agrupados[i][j]==-1){
                           numGrupos=j;
                           break;
                       }
                    }
                    if(numGrupos==0){
                        numGrupos=numDocs-1;
                    }
                    //Contamos el número de elementos de cada grupo de origen
                    int numElem[]= new int[numGrupos];
                    Arrays.fill(numElem,0);
                    for(int n=0;n<numGrupos;n++){
                        for(int m=0;m<numDocs;m++){
                            if(parcial[agrupados[i][n]][m]==-1){
                                numElem[n]=m;
                                break;
                                }
                        }
                        }
                    //Rellenamos el primer grupo con los demás.
        int acumulados=numElem[0];
                    for(int n=1;n<numGrupos;n++){
                                for (int m = 0; m < numElem[n]; m++) {
                                    parcial[agrupados[i][0]][acumulados+m]
                                            = parcial[agrupados[i][n]][m];
                                    //Y vaciamos el segundo grupo
                                    parcial[agrupados[i][n]][m] = -1;
                                }
                                acumulados=acumulados+numElem[n];
                    }
       //Recalculamos el nuevo centroide resultante, como suma ponderada
                        for(int m=0;m<numTerm;m++){
                            centroides[agrupados[i][0]][m]=centroides[agrupados[i][0]][m]
                                    *numElem[0];
                            for(int n=1;n<numGrupos;n++){
                        centroides[agrupados[i][0]][m]=centroides[agrupados[i][0]][m]
                                +(centroides[agrupados[i][n]][m]*numElem[n]);
                        centroides[agrupados[i][n]][m]=-1;
                    }
                            centroides[agrupados[i][0]][m]=
                                    centroides[agrupados[i][0]][m]/acumulados;
                        }
                    
        }
        float[][][]cntYparc={centroides,parcial};
        return cntYparc;
    }
       
    
    /**
     * Método que calcula los clusters a partir de la técnica de la herencia
     * aglomerativa, empleando centroides.
     *
     */
    public String[][] herenciaAglomerativa(String[] documentos,String idioma,
             int k) {
        Matrices matriz = new Matrices(documentos,idioma);
        int numDocs=matriz.numeros.length;
        int numTerm=matriz.numeros[0].length;
//Array que va conteniendo las referencias a los textos de cada grupo
        float parcial[][] = new float[numDocs][numDocs];
//Lista que contiene el centroide de cada grupo.
        float[][] centroides = new float[numDocs][numTerm];
//Inicializamos la aglomeración con un solo elemento por grupo.
        for(int i=0;i<numDocs;i++){
            Arrays.fill(parcial[i],-1);
         }
        for (int i = 0; i < numDocs; i++) {
            parcial[i][0] = i;
            System.arraycopy(matriz.numeros[i], 0, centroides[i], 0, numTerm);
        }
        int[][] uniones;
        float[][][] cntYparc={centroides,parcial};
        int numGrupos=numDocs;//Numero de grupos que quedan en cada momento
while(k<numGrupos){
        uniones=definirUniones(cntYparc[1],cntYparc[0]);
        cntYparc=unir(cntYparc[0],cntYparc[1],uniones);
        numGrupos=0;
        //Recontamos el número de grupos resultantes
        for(int i=0;i<cntYparc[0].length;i++){
            if(cntYparc[0][i][0]!=-1){
                numGrupos++;
            }
        }
}
        float grupos[][][] = new float[numGrupos][numDocs][numTerm];
        for(int i=0;i<numGrupos;i++){
            for(int j=0;j<numDocs;j++){
                Arrays.fill(grupos[i][j],-1);
            }
        }
        float[][] cntLimpio= new float[numGrupos][numTerm];
        int grupo=0;//Variable que indica qué grupo estamos recorriendo
        for(int i=0;i<numDocs;i++){
            if(cntYparc[0][i][0]!=-1){
                System.arraycopy(cntYparc[0][i],0,cntLimpio[grupo],0,numTerm);
                grupo++;
            }
        }
        grupo=0;
        for (int i = 0; i < numDocs; i++) {//Recorremos cada grupos
            if(cntYparc[1][i][0]!=-1){
            for (int j = 0; j < numDocs; j++) {//Recorremos todos los docs en cada grupo
                if (cntYparc[1][i][j] != -1) {
                    System.arraycopy(matriz.numeros[(int)cntYparc[1][i][j]], 0,
                            grupos[grupo][(int)cntYparc[1][i][j]], 0, numTerm);
                }
            }
            grupo++;
            }
        }
        //Calculamos el máximo número de elementos de un grupo
        int maxNumElem = 0;
        int elementos;
        for (int i = 0; i < numGrupos; i++) {
            elementos = 0;
            for (int j = 0; j < numDocs; j++) {
                if (grupos[i][j][0] != -1) {
                    elementos++;
                }
            }
            maxNumElem = Math.max(elementos, maxNumElem);
        }
        String[][] resultado = numeroATexto(grupos, documentos, maxNumElem,
                matriz.terminos, cntLimpio);
        return resultado;
    }
}