package adivina_la_cancion.prototipo.adivina_la_cancion.dto;

import java.util.List;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Cancion;
import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Ronda;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RondaDTO {

    private List<Cancion> canciones;

    private String cancionCorrectaAudioURL;

    private long finRonda; // Timestamp en milisegundos para compatibilidad con Jackson

    public RondaDTO(Ronda ronda) {
        this.canciones = ronda.getCanciones();
        this.cancionCorrectaAudioURL = ronda.getCancionCorrecta().getAudioURL();
        this.finRonda = ronda.getFinRonda().toEpochMilli(); // Convertir Instant a timestamp en milisegundos
    }
}
