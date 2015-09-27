package tfg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.*;

/**
 *
 * Clase que carga documentos de un archivo json dado
 * 
 * @author fernando
 */
public class Cargador {

    String[] documentos;
    Cargador(String nombreArchivo){
        documentos=cargaJson(nombreArchivo);
     }
     public String[] cargaJson(String nombreArchivo){
         String[] datos=null;
         String texto="";
         String linea;
             try{
                 FileReader fr= new FileReader(nombreArchivo);
                 BufferedReader bf = new BufferedReader(fr);
                 while((linea=bf.readLine())!=null){
                 texto+=linea+"\n";
                 }
             JSONArray jsar= new JSONArray(texto);
             int longitud= //(int)(jsar.length()*0.025);
                             3;
             datos=new String[longitud];
             JSONObject[] json=new JSONObject[longitud];
             System.out.println("Se han cargado del archivo "+nombreArchivo+", "
                     +json.length+" documentos");
             for(int i=0;i<longitud;i++){
                 json[i]=(JSONObject)jsar.get(i);
                 datos[i]=i+" "+json[i].getInt("id")+" "
                         +json[i].getString("text")+" ";
             }
             }
          catch (FileNotFoundException fnfe){
			fnfe.printStackTrace();
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
    
     return datos;
     }
}