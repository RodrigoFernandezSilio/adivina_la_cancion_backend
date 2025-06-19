package adivina_la_cancion.prototipo.adivina_la_cancion.dto;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Respuesta;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RespuestaDTO {

    private long instanteRespuesta;

    private int puntuacion;

    public RespuestaDTO(Respuesta respuesta) {
        if (respuesta.getInstanteRespuesta() != null) {
            this.instanteRespuesta = respuesta.getInstanteRespuesta().toEpochMilli();
        }
        if (respuesta.getPuntuacion() != null) {
            this.puntuacion = respuesta.getPuntuacion();
        }
    }
}
