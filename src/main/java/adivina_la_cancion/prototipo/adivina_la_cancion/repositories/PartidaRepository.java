package adivina_la_cancion.prototipo.adivina_la_cancion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.EstadoPartida;
import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Partida;

public interface PartidaRepository extends JpaRepository<Partida, Long> {

    @Query("SELECT p FROM Partida p LEFT JOIN FETCH p.usuarios WHERE p.id = :id")
    Partida findByIdWithUsuarios(Long id);

    @Query("""
    SELECT p FROM Partida p 
    LEFT JOIN FETCH p.usuarios 
    LEFT JOIN FETCH p.respuestasPorUsuario ru 
    LEFT JOIN FETCH ru.respuestas 
    WHERE p.id = :id
    """)
    Partida findByWithData(@Param("id") Long id);

    List<Partida> findByEstadoAndPrivada(EstadoPartida estado, boolean privada);
}
