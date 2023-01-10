package by.pika.service;

import by.pika.entity.AppDocument;
import by.pika.entity.AppPhoto;
import by.pika.service.enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink(Long docId, LinkType linkType);

}
