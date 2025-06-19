package adivina_la_cancion.prototipo.adivina_la_cancion.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Cancion;
import adivina_la_cancion.prototipo.adivina_la_cancion.domain.ListaReproduccion;
import api.deezer.objects.Playlist;
import api.deezer.objects.Track;
import api.deezer.objects.data.PlaylistData;

@Service
public class MusicaService {

    private DeezerService deezerService;

    public MusicaService(DeezerService deezerService) {
        this.deezerService = deezerService;
    }

    public ResponseEntity<List<ListaReproduccion>> buscarPlaylists(String q) {
        List<ListaReproduccion> listaReproduccionList = new ArrayList<>();

        PlaylistData playlistData = deezerService.buscarPlaylists(q, 0, 20);
        if (playlistData != null && playlistData.getData() != null) {
            listaReproduccionList = convertirPlaylistDataEnListaReproduccionList(playlistData);

            return new ResponseEntity<>(listaReproduccionList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<ListaReproduccion>> buscarPlaylistsValidas(String q) {
        List<ListaReproduccion> listaReproduccionList = new ArrayList<>();
        List<ListaReproduccion> listaReproduccionValidaList = new ArrayList<>();


        PlaylistData playlistData = deezerService.buscarPlaylists(q, 0, 10);
        if (playlistData != null && playlistData.getData() != null) {
            listaReproduccionList = convertirPlaylistDataEnListaReproduccionList(playlistData);

            anhadirCanciones(listaReproduccionList);

            for (ListaReproduccion listaReproduccion : listaReproduccionList) {
                Set<Cancion> canciones = listaReproduccion.getCanciones();
                long cancionesConPreview = canciones.stream().filter(cancion -> cancion.getAudioURL() != null).count();
                if (cancionesConPreview >= 4) {
                    listaReproduccionValidaList.add(listaReproduccion);
                }
            }

            return new ResponseEntity<>(listaReproduccionValidaList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<ListaReproduccion> convertirPlaylistDataEnListaReproduccionList(PlaylistData playlistData) {
        List<ListaReproduccion> listaReproduccionList = new ArrayList<>();

        for (Playlist playlist : playlistData.getData()) {
            if (playlist != null) {
                listaReproduccionList.add(convertirPlaylistEnListaReproduccion(playlist));
            }
        }

        return listaReproduccionList;
    }

    private ListaReproduccion convertirPlaylistEnListaReproduccion(Playlist playlist) {
        return new ListaReproduccion(
                String.valueOf(playlist.getId()),
                playlist.getTitle(),
                playlist.getPicture());
    }

    private void anhadirCanciones(List<ListaReproduccion> listaReproduccionList) {
        for (ListaReproduccion listaReproduccion : listaReproduccionList) {
            Set<Cancion> cancionSet = obtenerCanciones(listaReproduccion.getId());
            listaReproduccion.setCanciones(cancionSet);
        }
    }

    /**
     * Obtiene las canciones de una playlist a partir de su ID.
     *
     * @param playlistId ID de la playlist en formato String.
     * @return Un conjunto de canciones asociadas a la playlist.
     */
    private Set<Cancion> obtenerCanciones(String playlistId) {
        long playlistIdLong = Long.parseLong(playlistId);
        List<Track> trackList = deezerService.obtenerTodosTracksDePlaylist(playlistIdLong);
        List<Cancion> cancionList = convertirTrackListEnCancionList(trackList);
        return new HashSet<>(cancionList);
    }

    private List<Cancion> convertirTrackListEnCancionList(List<Track> trackList) {
        return trackList.stream()
                .map(this::convertirTrackEnCancion)
                .collect(Collectors.toList());
    }

    private Cancion convertirTrackEnCancion(Track track) {
        return new Cancion(
            String.valueOf(track.getId()),
            track.getTitle(),
            track.getPreview());
    }

    /**
     * Verifica si una playlist de Deezer es válida.
     *
     * @param playlistId ID de la playlist en formato String.
     * @return ResponseEntity con un valor booleano que indica si la playlist es válida o no.
     *         - `true` si la playlist tiene al menos 4 canciones con preview.
     *         - `false` si tiene menos de 4 canciones con preview.
     *         - Retorna un `500 Internal Server Error` si no se pudo obtener la playlist.
     */
    public ResponseEntity<Boolean> esPlaylistsValida(String playlistId) {
        Playlist playlist = deezerService.obtenerPlaylist(Long.parseLong(playlistId));

        if (playlist != null) {
            Set<Cancion> cancionSet = obtenerCanciones(playlist.getId().toString());

            long cancionesConPreview = cancionSet.stream().filter(cancion -> cancion.getAudioURL() != null).count();

            if (cancionesConPreview >= 4) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

        /**
     * Verifica si una playlist es válida y la devuelve en formato de ListaReproduccion.
     *
     * @param playlistId ID de la playlist en formato String.
     * @return ResponseEntity con los siguientes posibles resultados:
     *         - `200 OK` y el objeto ListaReproduccion si la playlist tiene al menos 4 canciones con audio disponible.
     *         - `404 Not Found` si la playlist no fue encontrada en Deezer.
     */
    public ResponseEntity<ListaReproduccion> obtenerPlaylist(String playlistId) {
        Playlist playlist = deezerService.obtenerPlaylist(Long.parseLong(playlistId));

        if (playlist != null) {
            ListaReproduccion listaReproduccion = convertirPlaylistEnListaReproduccion(playlist);

            Set<Cancion> canciones = obtenerCanciones(listaReproduccion.getId());
            
            listaReproduccion.setCanciones(canciones);
            return new ResponseEntity<>(listaReproduccion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Verifica si una playlist de Deezer es válida y la devuelve en formato de ListaReproduccion.
     *
     * @param playlistId ID de la playlist en formato String.
     * @return ResponseEntity con los siguientes posibles resultados:
     *         - `200 OK` y el objeto ListaReproduccion si la playlist tiene al menos 4 canciones con audio disponible.
     *         - `400 Bad Request` si la playlist tiene menos de 4 canciones con audio disponible.
     *         - `404 Not Found` si la playlist no fue encontrada en Deezer.
     */
    public ResponseEntity<ListaReproduccion> obtenerPlaylistValida(String playlistId) {
        Playlist playlist = deezerService.obtenerPlaylist(Long.parseLong(playlistId));

        if (playlist != null) {
            ListaReproduccion listaReproduccion = convertirPlaylistEnListaReproduccion(playlist);

            Set<Cancion> canciones = obtenerCanciones(listaReproduccion.getId());

            long cancionesConPreview = canciones.stream().filter(cancion -> cancion.getAudioURL() != null).count();
            if (cancionesConPreview >= 4) {
                listaReproduccion.setCanciones(canciones);
                return new ResponseEntity<>(listaReproduccion, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
