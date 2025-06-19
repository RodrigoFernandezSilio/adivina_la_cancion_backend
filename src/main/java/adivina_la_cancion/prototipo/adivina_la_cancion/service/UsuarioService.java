package adivina_la_cancion.prototipo.adivina_la_cancion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Usuario;
import adivina_la_cancion.prototipo.adivina_la_cancion.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    protected UsuarioRepository usuarioRepo;

    public ResponseEntity<Long> crearUsuario(String usuarioNombre) {
        Usuario usuario = new Usuario(usuarioNombre);
        usuarioRepo.save(usuario);
        return new ResponseEntity<Long>(usuario.getId(), HttpStatus.OK);
    }

    public Usuario obteneUsuario(Long usuarioID) {
        return usuarioRepo.findById(usuarioID).orElse(null);
    }

}
