package tfg;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Clase que permite crear las matrices numéricas asociadas a los documentos
 * recibidos.
 *
 * @author fernando
 */
public class Matrices {

    float[][] numeros;
    String[] textos;
    String[] terminos;

    Matrices(String[] documentos, String idioma) {
        numeros = generarMatriz(documentos,idioma);
        textos = new String[documentos.length];
        System.arraycopy(documentos, 0, textos, 0, documentos.length);
        terminos = terminosImportantes(textos,idioma);
    }

    /*
     * Método que calcula la frecuencia NORMALIZADA de un término en un texto respecto al 
     * total de términos del documento.
     */
    public float frecuenciaTermino(String termino,
            String texto) {
        float repeticion = (float) repeticionPalabras(termino, texto);
        float numPalabras = (float) contarPalabras(texto);
        float frecuencia = repeticion / numPalabras;
        return frecuencia;
    }

    /*
     * Método que cuenta el número de palabras de un documento.
     */
    public float contarPalabras(String texto) {
        String[] fraccionado = texto.split(" ");
        float numeroPalabras = (float) fraccionado.length;
        return numeroPalabras;
    }
    /*
     * Metodo que limpia un texto de símbolos distintos del espacio que separan
     * palabras, y evita que haya varios espacios juntos
     */

    public String limpiar(String texto, String idioma) {
        //texto=Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto=texto.toLowerCase();
        //Eliminamos tildes
        String original="áàäéèëíìïóòöúùç'";
        String limpio=  "aaaeeeiiiooouuc ";
        String []prohibidas;//Array con las palabras que no se tendrán en cuenta
        String prohibidasJuntas="";
        String linea;
        for(int i=0;i<original.length();i++){
            texto=texto.replace(original.charAt(i),limpio.charAt(i));
          }
        //Cargamos la lista de palabras prohibidas
        try{
                 FileReader fr= new FileReader(idioma);
                 BufferedReader bf = new BufferedReader(fr);
                 int pos=0;
                 while((linea=bf.readLine())!=null){
                 prohibidasJuntas+=" "+linea+" \n";
                 }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Matrices.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Matrices.class.getName()).log(Level.SEVERE, null, ex);
        }
                 prohibidas= prohibidasJuntas.split("\n");
        texto=texto.replaceAll("[^abcdefghijklmnñopqrstuvwxyz ]","");
        texto=texto.replace(" ","  ");
        for(int i=0;i<prohibidas.length;i++){
            texto=texto.replace(prohibidas[i]," ");
        }
        int longitudVieja=texto.length()+1;
        while (longitudVieja != texto.length()) {
            longitudVieja = texto.length();
            texto = texto.replace("  ", " ");
        }
        return texto;
    }
    /*
     * Método que calcula el número de veces que aparece un termino en un texto.
     */

    public float repeticionPalabras(String termino, String texto) {
        float vecesPalabra = -1;
        int pos=0;
        int longitudPalabra=termino.length();
        while(pos!=-1){
            pos=texto.indexOf(termino, pos+longitudPalabra);
            vecesPalabra++;
        }
        return vecesPalabra;
    }
    /*
     * Método que calcula la frecuencia inversa de un término en un conjunto 
     * de documentos. Es decir, el logaritmo de la división del total de
     * documentos entre los que contienen el citado término.
     */

    public float frecuenciaInversaDocumento(String termino, String documentos[]) {
        float numeroDocumentos = documentos.length;
        float numeroContiene = 0;
        float frecuencia;
        for (int posicion = 0; posicion < numeroDocumentos; posicion++) {
            if (documentos[posicion].indexOf(termino)!=-1) {
                numeroContiene++;
            }
        }
        frecuencia = (float) Math.log(numeroDocumentos /(1+numeroContiene));
        return frecuencia;
    }
    /*
     * Frecuencia normalizada del término en el texto, empleada para contar la 
     * relación de un documento con un término.
     */

    public float[][] tfidf(String[] terminos, String[] documentos) {
        int numeroDocumentos = documentos.length;
        int numeroTerminos = terminos.length;
        float[][] matriz= new float[numeroDocumentos][numeroTerminos];
        float inversa;
        for (int nTerm = 0; nTerm < numeroTerminos; nTerm++) {
            inversa=frecuenciaInversaDocumento(terminos[nTerm],documentos);
        for (int nDoc = 0; nDoc < numeroDocumentos; nDoc++) {
            matriz[nDoc][nTerm]=frecuenciaTermino(terminos[nTerm], documentos[nDoc])*inversa;
        }
        }
        return matriz;
    }
   
    /*
     * Método que devuelve el conjunto de términos a tener en cuenta en un 
     * conjunto de documentos dado.
     */

    public String[] terminosImportantes(String documentos[],String idioma){
        int numDocs=documentos.length;
        //Limpiamos los documentos de las palabras que no son válidas
        String[] limpio= new String[numDocs];
        for(int i=0;i<numDocs;i++){
            limpio[i]=limpiar(documentos[i],idioma);
        }
        //Creamos una lista de potenciales palabras
        String conjunto="";
        for(int i=0;i<numDocs;i++){
            conjunto=conjunto+limpio[i]+" ";
        }
        String fraccionado[]=conjunto.split(" ");
        Arrays.sort(fraccionado);
        int posResultante=0;
        String[] sinRepetir=new String[fraccionado.length];
        for(int i=0;i<fraccionado.length-1;i++){
            if(!fraccionado[i].equals(fraccionado[i+1])){
                sinRepetir[posResultante]=fraccionado[i];
                posResultante++;
            }
        }
        sinRepetir = Arrays.copyOfRange(sinRepetir, 0, posResultante);
        //Comprobamos en cuantos documentos aparece cada palabra
        int [] apariciones= new int[sinRepetir.length];
        Arrays.fill(apariciones, 0);
        
        for(int i=0;i<sinRepetir.length;i++){//Recorremos cada palabra
            for(int j=0;j<numDocs;j++){//Recorremos cada documento
        if(limpio[j].indexOf(sinRepetir[i])!=-1){
            apariciones[i]++;
        }
            }
        }
        String[] seleccion=new String[sinRepetir.length];
        int pos=0;
        int min=(int) Math.floor(numDocs*(0.65));
        int max=(int) Math.floor(numDocs*(0.72));
        for(int i=0;i<sinRepetir.length;i++){
            if(apariciones[i]<=max&apariciones[i]>=min){
                seleccion[pos]=sinRepetir[i];
                pos++;
            }
        }
        seleccion= Arrays.copyOfRange(seleccion,0,pos);
        return seleccion;
    }
    
    /*
     * Método que genera una matriz con la valoración de la presencia de 
     * cada termino en cada documento.
     */

    public float[][] generarMatriz(String[] documentos,String idioma) {
        String[] termino = terminosImportantes(documentos,idioma);
        int numeroDocumentos = documentos.length;
        int numeroTerminos = termino.length;
        System.out.println("Longitud documentos "+numeroDocumentos);
        System.out.println("Longitud Términos "+numeroTerminos);
        float[][] matriz = tfidf(termino,documentos);
        return matriz;
    }
}