package ar.edu.utn.frba.dds.domain.repositories.imp;

import ar.edu.utn.frba.dds.domain.entities.contacto.Mensaje;

import ar.edu.utn.frba.dds.domain.entities.oferta.OfertaCanjeada;
import ar.edu.utn.frba.dds.domain.entities.usuarios.Usuario;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepositorioMensaje extends Repositorio {
    public List<Mensaje> buscarPorFecha(LocalDateTime fecha) {
        return entityManager().createQuery("from" + Mensaje.class.getName() + "fechaEmision = " + fecha).getResultList();
    }
    public List<Mensaje> buscarPorUsuario(Usuario usuario) {
        return entityManager().createQuery("from" + Mensaje.class.getName() + "usuario_id = " + usuario.getId()).getResultList();
    }
}
