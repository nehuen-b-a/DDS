package ar.edu.utn.frba.dds.domain.entities.reportes;

import java.util.List;

public interface IAdapterPDF {
  void exportarPDF(String titulo, String nombreArchivo, List<String> parrafos, String fechaInicio);
}
