package adivina_la_cancion.prototipo.adivina_la_cancion.service.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Partida;
import adivina_la_cancion.prototipo.adivina_la_cancion.dto.PartidaDTO;
import adivina_la_cancion.prototipo.adivina_la_cancion.repositories.PartidaRepository;
import adivina_la_cancion.prototipo.adivina_la_cancion.repositories.UsuarioRepository;
import adivina_la_cancion.prototipo.adivina_la_cancion.service.PartidaService;

@RestController
@RequestMapping("/partidas")
@CrossOrigin(origins = {"http://localhost:4200", "https://adivina-la-cancion-frontend.onrender.com"} maxAge = 3600)
public class PartidaController {

    @Autowired
    protected PartidaService partidaService;

    @Autowired
    protected PartidaRepository pr;

    @Autowired
    protected UsuarioRepository ur;

    @GetMapping
    @JsonView({ Views.PartidaPreview.class })
    public ResponseEntity<List<Partida>> obtenerPartidasDisponibles() {
        return partidaService.obtenerPartidasDisponibles();
    }

    @PostMapping()
    @JsonView({ Views.PartidaPreview.class })
    @Transactional
    public ResponseEntity<Partida> crearPartida(@RequestBody PartidaDTO partidaDTO) {
        return partidaService.crearPartida(partidaDTO);
    }

    @PutMapping("/{partidaID}/{usuarioID}/anhadirUsuario")
    @JsonView({ Views.PartidaPreview.class })
    @Transactional
    public ResponseEntity<Partida> anhadirUsuario(
            @PathVariable Long partidaID,
            @PathVariable Long usuarioID,
            @RequestParam(required = false) String codigoAcceso) {
        return partidaService.anhadirUsuario(partidaID, usuarioID, codigoAcceso);
    }

    @PutMapping("/{partidaID}/{usuarioID}/iniciarPartida")
    @Transactional
    public ResponseEntity<Void> iniciarPartida(@PathVariable Long partidaID, @PathVariable Long usuarioID) {
        return partidaService.iniciarPartidaPorAnfitrion(partidaID, usuarioID);
    }
}
