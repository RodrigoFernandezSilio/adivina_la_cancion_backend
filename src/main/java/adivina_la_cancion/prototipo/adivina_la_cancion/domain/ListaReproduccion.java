package adivina_la_cancion.prototipo.adivina_la_cancion.domain;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;

import adivina_la_cancion.prototipo.adivina_la_cancion.service.api.Views;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class ListaReproduccion {

    @NonNull
    @Id 
    @JsonView({Views.PartidaPreview.class})
    private String id;

    @NonNull
    @JsonView({Views.PartidaPreview.class})
    private String nombre;

    @NonNull
    @JsonView({Views.PartidaPreview.class})
    private String imagenUrl;

    // @ManyToMany(cascade = CascadeType.ALL)
    @Transient
    private Set<Cancion> canciones;
}
