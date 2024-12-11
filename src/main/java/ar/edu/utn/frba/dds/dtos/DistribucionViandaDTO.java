package ar.edu.utn.frba.dds.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistribucionViandaDTO {
    private Long id;
    private Long heladeraOrigenId;
    private String heladeraOrigenNombre;
    private Long heladeraDestinoId;
    private String heladeraDestinoNombre;
    private int cantidadViandas;
    private String motivo;
    private String fecha;
    private boolean terminada;
    private String horaSolicitudEnOrigen;
    private String horaSolicitudEnDestino;
}
