package adivina_la_cancion.prototipo.adivina_la_cancion.dto;

import java.util.List;
import java.util.stream.Collectors;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Usuario;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuariosDTO {

    private List<UsuarioSanitizado> usuarios;
    
    public UsuariosDTO(List<Usuario> usuarios) {
        this.usuarios = usuarios.stream().map(UsuarioSanitizado::new).collect(Collectors.toList());
    }
}
