package adivina_la_cancion.prototipo.adivina_la_cancion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.ListaRespuesta;
import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Partida;
import adivina_la_cancion.prototipo.adivina_la_cancion.repositories.PartidaRepository;

@Service
public class PartidaServiceAux {

    @Autowired
    protected PartidaRepository partidaRepo;

    @Transactional
    public ListaRespuesta analizarRespuestasYemitirResultados(long partidaID, int indRonda) {
        Partida partida = partidaRepo.findById(partidaID).get();
        partida.completarRespuestasRonda(indRonda);
        return partida.obtenerRespuestasRonda(indRonda);
    }
}