package adivina_la_cancion.prototipo.adivina_la_cancion.service.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.ListaReproduccion;
import adivina_la_cancion.prototipo.adivina_la_cancion.service.MusicaService;

@RestController
@RequestMapping("/musica")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600) // Permitir solicitudes desde http://localhost:4200
public class MusicaController {

    @Autowired
    protected MusicaService musicaService;

    @GetMapping("/playlists")
    public ResponseEntity<List<ListaReproduccion>> buscarPlaylists(
            @RequestParam(required = true) String q,
            @RequestParam(required = false, defaultValue = "false") boolean validas) {
        if (validas == true) {
            return musicaService.buscarPlaylists(q);
        } else {
            return musicaService.buscarPlaylistsValidas(q);
        }
    }

    @GetMapping("/playlists/{playlistId}")
    public ResponseEntity<ListaReproduccion> obtenerPlaylist(
            @PathVariable String playlistId,
            @RequestParam(required = false, defaultValue = "false") boolean validas) {

        if (validas) {
            return musicaService.obtenerPlaylistValida(playlistId);
        } else {
            return musicaService.obtenerPlaylist(playlistId);
        }
    }
}
