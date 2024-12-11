package ar.edu.utn.frba.dds.domain.entities.reportes;


import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class ITextPDF {

  private String rutaRelativa;

  public ITextPDF(String rutaRelativa) {
    this.rutaRelativa = rutaRelativa;
  }

  public void generarPDF(String titulo, String nombreArchivo, List<String> parrafos, String fechaInicio) {
    try {
      // Obtener la fecha actual en formato de cadena
      String fechaActual = LocalDate.now().toString();

      // Crear la carpeta con el nombre de la fecha actual si no existe
      Path rutaDirectorio = Paths.get(rutaRelativa, fechaActual);
      if (!Files.exists(rutaDirectorio)) {
        Files.createDirectories(rutaDirectorio);  // Crear las carpetas necesarias
      }

      // Definir la ruta completa para el archivo PDF
      String rutaArchivo = rutaDirectorio.toString() + File.separator + nombreArchivo + ".pdf";

      // Crear el documento y escribir el contenido
      Document documento = new Document();
      PdfWriter.getInstance(documento, new FileOutputStream(rutaArchivo));
      documento.open();
      documento.addTitle(titulo);
      documento.add(new Paragraph(titulo, new Font(Font.FontFamily.TIMES_ROMAN, 25, Font.BOLDITALIC)));
      String intervalo = "Desde " + fechaInicio + " hasta " + fechaActual;
      documento.add(new Paragraph(intervalo, new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC)));
      documento.add(Chunk.NEWLINE);

      // Agregar los párrafos
      for (String parrafo : parrafos) {
        documento.add(new Paragraph(parrafo, new Font(Font.FontFamily.TIMES_ROMAN, 12)));
      }

      documento.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
