package by.pika.service.impl;

import by.pika.dao.AppUserDAO;
import by.pika.dao.RawDataDAO;
import by.pika.entity.AppUser;
import by.pika.entity.RawData;
import by.pika.service.MainService;
import by.pika.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static by.pika.enums.UserState.BASIC_STATE;

@Service
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDao;

    private final ProducerService producerService;

    private final AppUserDAO appUserDAO;

    public MainServiceImpl(RawDataDAO rawDataDao, ProducerService producerService, AppUserDAO appUserDAO) {
        this.rawDataDao = rawDataDao;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
    }

    @Override
    public void processTextMessage(Update update) {
    saveRawData(update);
        var textMessage = update.getMessage();
        var telegramUser = textMessage.getFrom();
        var appUser = findOrSaveAppUser(telegramUser);

        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Hello from NODE");
        producerService.producerAnswer(sendMessage);
    }

    private AppUser findOrSaveAppUser(User telegramUser){
        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if(persistentAppUser == null){
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO:
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();

        rawDataDao.save(rawData);
    }


}
