package adivina_la_cancion.prototipo.adivina_la_cancion.domain;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class ListaRespuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NonNull
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Respuesta> respuestas;

}
