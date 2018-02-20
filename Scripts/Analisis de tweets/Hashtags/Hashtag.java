package tweets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * @author dgaguz
 * 
 * @version 0.1 Permite clasificar los hashtags mas tweeteados contenidos en un archivo JSON, luego los guarda en un archivo txt denominado
 *          hashtags.txt y finalmente los muestra en pantalla todos los hashtags segun el tamaño de la muestra dentro de un frame como un grafico
 *          circular
 */
public class Hashtag {
	/**
	 * Determina el numero de hashtags mas tweeteados que se tomaran en cuenta ende los tweets que se tomara
	 */
	private final static Integer MUESTRA = 30;
	/**
	 * Contiene todos los hashtags leidos del archivo JSON
	 */
	private static List<String> hashtags = new ArrayList<>();

	public static void main(String[] args) {

		JsonParser parser = new JsonParser();

		try {
			// urls de las vistas de couchdb
			// URL url = new URL("http://localhost:5984/tweets_consulta_curso/_design/VistaQuito/_view/vista_quito");
			// URL url = new URL("http://localhost:5984/tweets_consulta_curso/_design/VistaGuayaquil/_view/vista_guayaquil");
			URL url = new URL("http://localhost:5984/tweets_consulta_curso/_design/VistaCuenca/_view/vista_cuenca");
			// URL url = new URL("http://localhost:5984/tweets_consulta_curso/_design/VistaTweetsConsulta/_view/vista_tweets_consulta");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			// lectura de jason
			JsonElement datos = parser.parse(in);
			dumpJSONElement(datos);

			// contador de repeticiones de los hasthtags
			Map<String, Long> ocurrencias = hashtags.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

			// dataset para el grafico circular
			DefaultPieDataset dataset = new DefaultPieDataset();

			// coleccion de hashtags usados para la clasificacion
			Map<String, Long> hashTagsOficiales = new HashMap<>();

			// lista de hash tags escritas en un archivo
			try (BufferedWriter bw = new BufferedWriter(new FileWriter("arch/hashtags.txt"));) {

				// ordenamos las ocurrencias
				ocurrencias = sortByValue(ocurrencias);
				// guardamos en el archivo
				bw.write("[Repeticiones] [#Hashtag]");
				bw.newLine();
				bw.newLine();
				ocurrencias.forEach((k, n) -> {
					System.out.println(n + "\t" + k);
					try {
						// escribimos en el fichero
						bw.write(n + " " + k);
						bw.newLine();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// almacenamiento de los 30 hashtags mas tweeteados
					if (hashTagsOficiales.size() <= MUESTRA) {
						hashTagsOficiales.put(k, n);
						dataset.setValue(k, n);
					}
				});

				// guardamos los cambios del fichero
				bw.flush();
			} catch (IOException e) {
				System.err.println("Error E/S: " + e);
			}

			// generacion de grafico circular
			generarGrafico("Hashtags Populares", dataset, "Gráfico de Pastel de los Hashtags de todos los Tweets Recolectados en Clase");

			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Metodo recursivo para recorrer todo el contenido del fichero JSON y ademas almacena todos los hashtags encontrados.
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
				StringTokenizer st = new StringTokenizer(valor.getAsString());
				String temp;
				while (st.hasMoreTokens()) {
					temp = st.nextToken();
					if (temp.matches("^#[\\w]+"))
						hashtags.add(temp);
				}

			}
		} else if (elemento.isJsonNull()) {
			// System.out.println("Es NULL");
		} else {
			// System.out.println("Es otra cosa");
		}
	}

	/**
	 * Metodo que permite ordenar un map.
	 * 
	 * @param map
	 * @return mapp ordenado por valor
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				// o2 comparado con o1 -> sortAsc
				// o1 comparado con o2 -> sortDes
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	/**
	 * Metodo que muestra en pantalla un grafico circular.
	 * 
	 * @param nombre
	 *            Nombre de la GUI.
	 * @param dataset
	 *            Conjunto de datos del pastel.
	 * @param titulo
	 *            Titulo del grafico circular.
	 */
	public static void generarGrafico(String nombre, DefaultPieDataset dataset, String titulo) {
		// Creando el Grafico
		JFreeChart chart = ChartFactory.createPieChart(titulo, dataset, true, true, false);
		chart.removeLegend();

		// Mostrar Grafico
		ChartFrame frame = new ChartFrame(nombre, chart);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
