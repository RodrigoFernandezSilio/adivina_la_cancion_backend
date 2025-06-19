package adivina_la_cancion.prototipo.adivina_la_cancion.dto;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Usuario;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa una versión segura del usuario, eliminando datos sensibles.
 * Se utiliza para compartir información pública sin comprometer la seguridad.
 */

@Data
@NoArgsConstructor
public class UsuarioSanitizado {

    private String nombre;

    public UsuarioSanitizado(Usuario usuario) {
        this.nombre = usuario.getNombre();
    }
}
