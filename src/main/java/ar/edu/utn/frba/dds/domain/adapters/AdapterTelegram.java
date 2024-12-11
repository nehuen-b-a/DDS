package ar.edu.utn.frba.dds.domain.adapters;

public interface AdapterTelegram {
  void enviar(String texto, Long chatId);
}
