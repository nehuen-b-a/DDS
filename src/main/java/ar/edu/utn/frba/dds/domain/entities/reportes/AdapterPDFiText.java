package ar.edu.utn.frba.dds.domain.entities.reportes;

import java.util.List;

public class AdapterPDFiText implements IAdapterPDF {
  private ITextPDF iText;

  public AdapterPDFiText(ITextPDF iText) {
    this.iText = iText;
  }

  @Override
  public void exportarPDF(String titulo, String nombreArchivo, List<String> parrafos, String fechaInicio) {
    iText.generarPDF(titulo, nombreArchivo, parrafos, fechaInicio);
  }
}
