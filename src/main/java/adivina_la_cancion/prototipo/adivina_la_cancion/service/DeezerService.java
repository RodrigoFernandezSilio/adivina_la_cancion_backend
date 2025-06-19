package adivina_la_cancion.prototipo.adivina_la_cancion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;

import api.deezer.objects.Playlist;
import api.deezer.objects.Track;
import api.deezer.objects.data.PlaylistData;
import api.deezer.objects.data.TrackData;

@Service
public class DeezerService {

    private final RestTemplate restTemplate;
    private static final String DEEZER_API_URL = "https://api.deezer.com";

    public DeezerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Realiza una búsqueda de playlists en Deezer según el término proporcionado.
     * Realiza una llamada a la API de Deezer y convierte la respuesta JSON en un objeto PlaylistData
     *
     * @param q Término de búsqueda para encontrar playlists relacionadas.
     * @param index Índice de inicio de la paginación
     * @param limit Cantidad de resultados por página
     * @return Un objeto {@link PlaylistData} que contiene la lista de playlists encontradas, o null si ocurre un error durante la búsqueda.
     */
    public PlaylistData buscarPlaylists(String q, int index, int limit) {
        String url = UriComponentsBuilder.fromHttpUrl(DEEZER_API_URL)
                .path("/search/playlist")
                .queryParam("q", q)
                .queryParam("index", index)
                .queryParam("limit", limit)
                .toUriString();

        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);

            // En la última página de resultados, Deezer no devuelve "next" y la librería externa de Deezer 
            // espera siempre ese campo, así que se añade manualmente para evitar errores.            
            jsonResponse = asegurarCampoNext(jsonResponse);

            Gson gson = new Gson();
            return gson.fromJson(jsonResponse, PlaylistData.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Realiza una búsqueda de playlists en Deezer con valores por defecto para la paginación.
     * 
     * @param q Término de búsqueda para encontrar playlists relacionadas.
     * @return Un objeto PlaylistData con los resultados de la búsqueda, usando index = 0 y limit = 25.
     */
    public PlaylistData buscarPlaylists(String q) {
        return buscarPlaylists(q, 0, 25);
    }


    /**
     * Obtiene la lista de tracks de una playlist en Deezer a partir de su ID.
     * Realiza una solicitud a la API de Deezer y convierte la respuesta JSON en un objeto TrackData.
     *
     * @param playlistId ID único de la playlist en Deezer.
     * @param index Índice de inicio de la paginación
     * @param limit Cantidad de resultados por página
     * @return Un objeto TrackData que contiene la lista de tracks de la playlist, o null} si ocurre un error durante la consulta.
     */
    public TrackData obtenerTracksDePlaylist(long playlistId, int index, int limit) {
        String url = UriComponentsBuilder.fromHttpUrl(DEEZER_API_URL)
                .path("/playlist/{playlistId}/tracks")
                .queryParam("index", index)
                .queryParam("limit", limit)
                .buildAndExpand(playlistId)
                .toUriString();

        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);

            // En la última página de resultados, Deezer no devuelve "next" y la librería externa de Deezer 
            // espera siempre ese campo, así que se añade manualmente para evitar errores.            
            jsonResponse = asegurarCampoNext(jsonResponse);
            
            Gson gson = new Gson();
            return gson.fromJson(jsonResponse, TrackData.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Track> obtenerTodosTracksDePlaylist(long playlistId) {
        List<Track> trackList = new ArrayList<>();
        int index = 0;
        int limit = 100;
        TrackData trackData;
        
        do {
            // Obtener los tracks de la playlist con paginación
            trackData = obtenerTracksDePlaylist(playlistId, index, limit);
        
            if (trackData != null && trackData.getData() != null) {
                trackList.addAll(trackData.getData()); // Agregar los tracks obtenidos a la lista
                index += limit; // Avanzar al siguiente página de tracks
            }
            System.out.println(trackData.getNext());
        } while (trackData != null && trackData.getNext() != null && trackData.getNext() == "");
        return trackList;
    }



    /**
     * Obtiene los detalles de una playlist en Deezer según el ID proporcionado.
     * Realiza una llamada a la API de Deezer y convierte la respuesta JSON en un objeto Playlist.
     *
     * @param playlistId ID de la playlist a obtener.
     * @return Un objeto {@link Playlist} con la información de la playlist, o null si ocurre un error.
     */
    public Playlist obtenerPlaylist(long playlistId) {
        String url = UriComponentsBuilder.fromHttpUrl(DEEZER_API_URL)
                .path("/playlist/")
                .path(String.valueOf(playlistId))
                .toUriString();

        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);

            Gson gson = new Gson();
            return gson.fromJson(jsonResponse, Playlist.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Asegura que el JSON de respuesta contenga el campo "next".
     * Si no está presente, lo añade con un valor vacío.
     *
     * @param jsonResponse JSON original obtenido de la API.
     * @return JSON modificado con el campo "next" si no existía.
     */
    private String asegurarCampoNext(String jsonResponse) {
        if (!jsonResponse.contains("\"next\"")) {  
            return jsonResponse.replace("}", ", \"next\": \"\" }");
        }
        return jsonResponse;
    }
}
