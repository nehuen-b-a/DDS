package ar.edu.utn.frba.dds.domain.adapters;

import ar.edu.utn.frba.dds.domain.entities.contacto.Contacto;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class AdaptadaTelegramBot extends TelegramLongPollingBot implements AdapterTelegram {
  public Long chatId;

  @Override
  public void enviar(String texto, Long chatId) {
    sendMessage(chatId, texto);
  }

  private void sendMessage(Long id, String texto){
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(id.toString());
    sendMessage.setText(texto);
    try {
      execute(sendMessage);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getBotUsername() {
    return "DdeSisBot"; // https://t.me/DdeSisBot
  }

  @Override
  public String getBotToken() {
    return "6850656325:AAGRzXDDukji5T-PqtF4px1V-_XibNZY7Fg"; // Esto no debería estar acá
  }

  @Override
  public void onUpdateReceived(Update update) {

    if (update.hasMessage() && update.getMessage().hasText()) {
      String messageText = update.getMessage().getText();
      this.chatId = update.getMessage().getChatId();

      switch (messageText) {
        case "/start":
          sendMessage(this.chatId, "Medio de contacto definido correctamente");
          break;
        default:
          break;
      }
    }
  }

  /*
  public static void main(String[] args) throws TelegramApiException {
    TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

    try {
      botsApi.registerBot(new AdaptadaTelegramBot());
      System.out.println("Bot funcionando!");
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }
  */

}
