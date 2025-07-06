package adivina_la_cancion.prototipo.adivina_la_cancion.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import adivina_la_cancion.prototipo.adivina_la_cancion.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = {"http://localhost:4200", "https://adivina-la-cancion-frontend.onrender.com"}, maxAge = 3600)
public class UsuarioController {

    @Autowired
    protected UsuarioService usuarioService;

    @PostMapping("/{usuarioNombre}")
    @Transactional
    public ResponseEntity<Long> crearUsuario(@PathVariable String usuarioNombre) {
        return usuarioService.crearUsuario(usuarioNombre);
    }
}
