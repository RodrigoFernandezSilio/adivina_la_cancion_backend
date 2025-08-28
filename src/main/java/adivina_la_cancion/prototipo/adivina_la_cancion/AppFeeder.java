package adivina_la_cancion.prototipo.adivina_la_cancion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.ListaReproduccion;
import adivina_la_cancion.prototipo.adivina_la_cancion.domain.ModoPuntuacion;
import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Partida;
import adivina_la_cancion.prototipo.adivina_la_cancion.dto.PartidaDTO;
import adivina_la_cancion.prototipo.adivina_la_cancion.service.MusicaService;
import adivina_la_cancion.prototipo.adivina_la_cancion.service.api.PartidaController;
import adivina_la_cancion.prototipo.adivina_la_cancion.service.api.UsuarioController;


@Component
public class AppFeeder implements CommandLineRunner {

    // @Autowired
    // protected PartidaRepository pr;

    // @Autowired
    // protected UsuarioRepository ur;

    @Autowired
    protected MusicaService musicaService;

    @Autowired
    protected UsuarioController usuarioController;

    @Autowired
    protected PartidaController partidaController;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // feedPlaylists();

        System.out.println("Application feeded\n\n");
    }

    private void feedPlaylists() {
        // TODO Borrar
        // Cancion cancion1 = new Cancion("Nombre de cancion1", "audioURL de cancion1");
        // Cancion cancion2 = new Cancion("Nombre de cancion2", "audioURL de cancion2");
        // Cancion cancion3 = new Cancion("Nombre de cancion3", "audioURL de cancion3");
        // Cancion cancion4 = new Cancion("Nombre de cancion4", "audioURL de cancion4");

        // Playlist playlist1 = new Playlist("Nombre de playlist1", new HashSet<>(Arrays.asList(cancion1, cancion2, cancion3, cancion4)));

        // plr.save(playlist1);

        Long user1Id = usuarioController.crearUsuario("Rodrigo").getBody();
        Long user2Id = usuarioController.crearUsuario("Juan").getBody();
        Long user3Id = usuarioController.crearUsuario("Ana").getBody();
        Long user4Id = usuarioController.crearUsuario("Lucía").getBody();
        Long user5Id = usuarioController.crearUsuario("Carlos").getBody();
        Long user6Id = usuarioController.crearUsuario("María").getBody();
        Long user7Id = usuarioController.crearUsuario("Diego").getBody();
        Long user8Id = usuarioController.crearUsuario("Laura").getBody();
        Long user9Id = usuarioController.crearUsuario("Sofía").getBody();
        Long user10Id = usuarioController.crearUsuario("Andrés").getBody(); 
        
        Long user15Id = usuarioController.crearUsuario("Carlos").getBody();
        Long user16Id = usuarioController.crearUsuario("María").getBody();
        Long user17Id = usuarioController.crearUsuario("Diego").getBody();
        Long user18Id = usuarioController.crearUsuario("Laura").getBody();
        Long user19Id = usuarioController.crearUsuario("Sofía").getBody();
        
        ListaReproduccion listaReproduccionRock = musicaService.buscarPlaylists("Rock").getBody().get(0);
        ListaReproduccion listaReproduccionPop = musicaService.buscarPlaylists("Pop").getBody().get(0); //Reggaeton y Trap 2016-2023 //Pop
        ListaReproduccion listaReproduccionJazz = musicaService.buscarPlaylists("Jazz").getBody().get(0);
        ListaReproduccion listaReproduccionHouse = musicaService.buscarPlaylists("House").getBody().get(0);
        
        // Partida 1 - Rock
        Partida partida1 = partidaController.crearPartida(
            new PartidaDTO(listaReproduccionRock.getId(), 3, 10, true, ModoPuntuacion.FIJO, false, null, user1Id)
        ).getBody();
        partidaController.anhadirUsuario(partida1.getId(), user2Id, null);
        partidaController.anhadirUsuario(partida1.getId(), user3Id, null);
        partidaController.anhadirUsuario(partida1.getId(), user15Id, null);
        partidaController.anhadirUsuario(partida1.getId(), user16Id, null);
        partidaController.anhadirUsuario(partida1.getId(), user17Id, null);
        partidaController.anhadirUsuario(partida1.getId(), user18Id, null);
        partidaController.anhadirUsuario(partida1.getId(), user19Id, null);

        // Partida 2 - Pop
        Partida partida2 = partidaController.crearPartida(
            new PartidaDTO(listaReproduccionPop.getId(), 3, 2, true, ModoPuntuacion.PROGRESIVO, false, null, user4Id)
        ).getBody();
        // partidaController.anhadirUsuario(partida2.getId(), user5Id, null);
        // partidaController.anhadirUsuario(partida2.getId(), user6Id, null);

        // Partida 3 - Jazz
        Partida partida3 = partidaController.crearPartida(
            new PartidaDTO(listaReproduccionJazz.getId(), 10, 6, true, ModoPuntuacion.PROGRESIVO, true, "1234", user7Id)
        ).getBody();
        partidaController.anhadirUsuario(partida3.getId(), user8Id, "1234");

        // Partida 4 - Electrónica
        Partida partida4 = partidaController.crearPartida(
            new PartidaDTO(listaReproduccionHouse.getId(), 20, 8, true, ModoPuntuacion.FIJO, false, null, user9Id)
        ).getBody();
        partidaController.anhadirUsuario(partida4.getId(), user10Id, null);
    }

}
