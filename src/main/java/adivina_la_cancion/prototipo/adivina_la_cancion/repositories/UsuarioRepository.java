package adivina_la_cancion.prototipo.adivina_la_cancion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
