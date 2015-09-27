package tfg;

import java.util.Arrays;

/**
 *
 * Clase que carga una serie de documentos de un/unos archivo/s json y 
 * los muestra ordenados por temática.
 * 
 * @author fernando
 */
public class Principal {
    
    public String[] documentos;
    public Clustering grupo;
    final String IDIOMA="documentos/prohibidasEspana.txt";
    final int NUMEROGRUPOS=3;
public final String TIPOCLUSTERING="KMEANS";
//                                 "HERENCIA";
    public final String ELECCION = //"aleatorio";
                                  "centrado";
    public static void main(String []args){
        Principal principal1= new Principal();
        mostrarPorConsola(principal1.grupo);
        comprobar(principal1.grupo );
    }
    
    public Principal(){
        this.documentos= cargar();
       this.grupo = new Clustering(documentos,IDIOMA,ELECCION,TIPOCLUSTERING,
               NUMEROGRUPOS);
    }
    
    public String[] cargar(){
//        String nombreArchivo1= "documentos/madrid.json";
        String nombreArchivo2= "documentos/real_madrid.json";
        String nombreArchivo3="documentos/qashqai.json";
//        String nombreArchivo4="documentos/iphone.json";
        String nombreArchivo5="documentos/sevilla.json";
//        String nombreArchivo6="documentos/uc3m.json";
//        Cargador cargador1= new Cargador(nombreArchivo1);
        Cargador cargador2= new Cargador(nombreArchivo2);
        Cargador cargador3= new Cargador(nombreArchivo3);
//        Cargador cargador4= new Cargador(nombreArchivo4);
        Cargador cargador5= new Cargador(nombreArchivo5);
//        Cargador cargador6= new Cargador(nombreArchivo6);
        String[] documentos= new String[
//                cargador1.documentos.length
                +cargador2.documentos.length
                +cargador3.documentos.length
//                +cargador4.documentos.length
                +cargador5.documentos.length
//                +cargador6.documentos.length
];
        int longitudTotal= 
//                cargador1.documentos.length
                +cargador2.documentos.length
                +cargador3.documentos.length
//                +cargador4.documentos.length
                +cargador5.documentos.length
//                +cargador6.documentos.length
        ;
        int posicion1=0;
        int posicion2=0;
        int posicion3=0;
        int posicion4=0;
        int posicion5=0;
        int posicion6=0;
        while(posicion1+posicion2+posicion3+posicion4+posicion5+posicion6<longitudTotal){
//            if(posicion1<cargador1.documentos.length){
//                documentos[posicion1+posicion2+posicion3+posicion4+posicion5+
//                        posicion6]=cargador1.documentos[posicion1];
//                posicion1++;
//            }
            if(posicion2<cargador2.documentos.length){
                documentos[posicion1+posicion2+posicion3+posicion4+posicion5+
                        posicion6]=cargador2.documentos[posicion2];
                posicion2++;
            }
            if(posicion3<cargador3.documentos.length){
                documentos[posicion1+posicion2+posicion3+posicion4+posicion5+
                        posicion6]=cargador3.documentos[posicion3];
                posicion3++;
            }
//            if(posicion4<cargador4.documentos.length){
//                documentos[posicion1+posicion2+posicion3+posicion4+posicion5+
//                        posicion6]=cargador4.documentos[posicion4];
//                posicion1++;
//            }
            if(posicion5<cargador5.documentos.length){
                documentos[posicion1+posicion2+posicion3+posicion4+posicion5+
                        posicion6]=cargador5.documentos[posicion5];
                posicion5++;
            }
//            if(posicion6<cargador6.documentos.length){
//                documentos[posicion1+posicion2+posicion3+posicion4+posicion5+
//                        posicion6]=cargador6.documentos[posicion6];
//                posicion6++;
//            }
        }
        return documentos;
    }
    public static void mostrarPorConsola(Clustering grupo){
        int longitud;
            boolean  []ocupado=new boolean[grupo.clusters[0].length];
            Arrays.fill(ocupado, false);
        System.out.println("Se muestran los clusters formados");
        for (int i = 0; i < grupo.clusters.length; i++) {
            longitud=0;
            System.out.println("Grupo llamado: "+grupo.clusters[i][0]+"******************************");
            for (int j = 1; j < grupo.clusters[i].length; j++) {
                if(grupo.clusters[i][j]!=null){
                    ocupado[j]=true;
                    longitud++;
                System.out.println(grupo.clusters[i][j]+"\n");
                }
                else{
                    break;
                }
            }
            if(longitud==0){
                longitud=grupo.clusters[i].length;
            }
                    System.out.println("El grupo "+i+" tiene "+longitud+" clusters diferentes.");
        }
    }
    public static void comprobar(Clustering grupo){
        
//        String nombreArchivo1= "documentos/madrid.json";
        String nombreArchivo2= "documentos/real_madrid.json";
        String nombreArchivo3="documentos/qashqai.json";
//        String nombreArchivo4="documentos/iphone.json";
        String nombreArchivo5="documentos/sevilla.json";
//        String nombreArchivo6="documentos/uc3m.json";
//        Cargador cargador1= new Cargador(nombreArchivo1);
        Cargador cargador2= new Cargador(nombreArchivo2);
        Cargador cargador3= new Cargador(nombreArchivo3);
//        Cargador cargador4= new Cargador(nombreArchivo4);
        Cargador cargador5= new Cargador(nombreArchivo5);
//        Cargador cargador6= new Cargador(nombreArchivo6);
        int num;
        Cargador[] cargadores={
//        cargador1 
        cargador2 
        ,cargador3 
//        cargador4
        ,cargador5 
//        ,cargador6
        };
        System.out.println("+++++++++Comenzamos la comprobación++++++++++++++");
        for(int n=0; n<cargadores.length;n++){//Recorremos cada grupo
            for(int k=0;k<grupo.clusters.length;k++){//Recorremos cada grupo
                num=0;
                
        for(int i=0;i<cargadores[n].documentos.length;i++){//Recorremos cada elem
            for(int j=0;j<grupo.clusters[k].length;j++){//Recorremos cada elem
                if(cargadores[n].documentos[i].equals(grupo.clusters[k][j])){
                    num++;
//                    break;
                }
        }
        }
            System.out.println("El grupo "+n+" de entrada tiene "+num+" valores"
                    +" en común con el grupo "+k+" de salida");
        
    }
        }
    }
}
