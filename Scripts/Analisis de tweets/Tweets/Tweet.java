package tweets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * @author dgaguz
 * 
 * @version 0.1 Permite recorrer un archivo JSON y guardar todos los tweets que no poseean hashtags.
 */
public class Tweet {

	/**
	 * Lista de tweets que no poseen un hashtag
	 */
	private static List<String> tweets = new ArrayList<>();

	public static void main(String[] args) {

		JsonParser parser = new JsonParser();

		try {
			// urls de las vistas de couchdb
			// URL url = new URL("http://localhost:5984/tweets_consulta_curso/_design/VistaQuito/_view/vista_quito");
			// URL url = new URL("http://localhost:5984/tweets_consulta_curso/_design/VistaGuayaquil/_view/vista_guayaquil");
			// URL url = new URL("http://localhost:5984/tweets_consulta_curso/_design/VistaCuenca/_view/vista_cuenca");
			URL url = new URL("http://localhost:5984/tweets_consulta_curso/_design/VistaTweetsConsulta/_view/vista_tweets_consulta");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			// lectura de jason
			JsonElement datos = parser.parse(in);
			dumpJSONElement(datos);

			try (BufferedWriter bw = new BufferedWriter(new FileWriter("arch/tweets.txt"));) {
				// recorremos la lista de tweets
				tweets.forEach(v -> {
					System.out.println(v);
					try {
						// escribimos en el fichero
						bw.write(v);
						bw.newLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				});

				// guardamos los cambios del fichero
				bw.flush();

			} catch (IOException e) {
				System.err.println("Error E/S: " + e);
			}

			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Metodo recursivo para recorrer todo el contenido del fichero JSON y ademas almacena todos los tweets sin hashtags encontrados.
	 * 
	 * @param elemento
	 *            Datos parseados de un archivo JSON.
	 */
	public static void dumpJSONElement(JsonElement elemento) {
		if (elemento.isJsonObject()) {
			// System.out.println("Es objeto");
			JsonObject obj = elemento.getAsJsonObject();
			java.util.Set<java.util.Map.Entry<String, JsonElement>> entradas = obj.entrySet();
			java.util.Iterator<java.util.Map.Entry<String, JsonElement>> iter = entradas.iterator();
			while (iter.hasNext()) {
				java.util.Map.Entry<String, JsonElement> entrada = iter.next();
				// System.out.println("Clave: " + entrada.getKey());
				// System.out.println("Valor:");
				dumpJSONElement(entrada.getValue());
			}

		} else if (elemento.isJsonArray()) {
			JsonArray array = elemento.getAsJsonArray();
			// System.out.println("Es array. Numero de elementos: " +
			// array.size());
			java.util.Iterator<JsonElement> iter = array.iterator();
			while (iter.hasNext()) {
				JsonElement entrada = iter.next();
				dumpJSONElement(entrada);
			}
		} else if (elemento.isJsonPrimitive()) {
			// System.out.println("Es primitiva");
			JsonPrimitive valor = elemento.getAsJsonPrimitive();
			if (valor.isBoolean()) {
				// System.out.println("Es booleano: " + valor.getAsBoolean());
			} else if (valor.isNumber()) {
				// System.out.println("Es numero: " + valor.getAsNumber());
			} else if (valor.isString()) {
				// System.out.println("Es texto: " + valor.getAsString());
				if (valor.getAsString().length() != 18) {
					StringTokenizer st = new StringTokenizer(valor.getAsString());
					String temp;
					Boolean interruptor = false;
					while (st.hasMoreTokens()) {
						temp = st.nextToken();
						if (temp.matches("^#[\\w]+")) {
							interruptor = true;
							break;
						}
					}
					if (!interruptor) {
						tweets.add(valor.getAsString());
					}
				}

			}
		} else if (elemento.isJsonNull()) {
			// System.out.println("Es NULL");
		} else {
			// System.out.println("Es otra cosa");
		}
	}

}
