package adivina_la_cancion.prototipo.adivina_la_cancion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PartidaDTO {

    private String playlistID;

    private int numRondas;
    
    private int numMaxUsuariosPartida;

    private boolean votoModificable;

    private boolean privada;
    
    private String codigoAcceso;    

    private long usuarioID;
}
