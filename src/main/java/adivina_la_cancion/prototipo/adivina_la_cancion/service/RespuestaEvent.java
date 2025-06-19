package adivina_la_cancion.prototipo.adivina_la_cancion.service;

import java.time.Instant;

import org.springframework.context.ApplicationEvent;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Cancion;
import lombok.Getter;

@Getter
public class RespuestaEvent extends ApplicationEvent {

    private Long partidaID;

    private Long usuarioID;

    private Cancion cancionSeleccionada;

    private Instant instanteRespuesta;

    public RespuestaEvent(Object source, Long partidaID, Long usuarioID, Cancion cancionSeleccionada, Instant instanteRespuesta) {
        super(source);
        this.partidaID = partidaID;
        this.usuarioID = usuarioID;
        this.cancionSeleccionada = cancionSeleccionada;
        this.instanteRespuesta = instanteRespuesta;
    }
}
