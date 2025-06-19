package adivina_la_cancion.prototipo.adivina_la_cancion.service;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Partida;
import adivina_la_cancion.prototipo.adivina_la_cancion.domain.Usuario;

@Service
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    PartidaService partidaService;

    UsuarioService usuarioService;

    public CustomHandshakeInterceptor(PartidaService partidaService, UsuarioService usuarioService) {
        this.partidaService = partidaService;
        this.usuarioService = usuarioService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        String path = request.getURI().getPath(); // Obtiene la parte del path de la URI

        // URI esperada:
        // ws://localhost:8080/webSocketPartida/{partidaID}/{usuarioID}
        // Ejemplo de path: /webSocketPartida/{partidaID}/{usuarioID}

        if (path != null) {
            String[] elementosPath = path.split("/");

            if (elementosPath.length == 4) {
                long partidaID = Long.parseLong(elementosPath[2]);
                long usuarioID = Long.parseLong(elementosPath[3]);

                Partida partida = partidaService.obtenerPartidaConUsuarios(partidaID);
                Usuario usuario = usuarioService.obteneUsuario(usuarioID);
                if (partida != null && usuario != null) {
                    if (partida.getUsuarios().contains(usuario)) {
                        attributes.put("partidaID", partidaID);
                        attributes.put("partida", partida);
                        attributes.put("usuario", usuario);
                        attributes.put("usuarioID", usuarioID);
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception exception) {
    }
}
