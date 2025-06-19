package adivina_la_cancion.prototipo.adivina_la_cancion.dto;

import java.util.List;
import java.util.stream.Collectors;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.ListaRespuesta;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ListaRespuestaDTO {

    private List<RespuestaDTO> respuestasDTO;

    public ListaRespuestaDTO(ListaRespuesta listaRespuesta) {
        this.respuestasDTO = listaRespuesta.getRespuestas().stream().map(RespuestaDTO::new).collect(Collectors.toList());
    }
}
