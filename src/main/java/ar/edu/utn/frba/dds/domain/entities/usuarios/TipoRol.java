package ar.edu.utn.frba.dds.domain.entities.usuarios;

import io.javalin.security.RouteRole;

public enum TipoRol implements RouteRole {
  ADMIN,
  PERSONA_HUMANA,
  PERSONA_JURIDICA,
  TECNICO
}
