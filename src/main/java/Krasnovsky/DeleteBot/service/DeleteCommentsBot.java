package Krasnovsky.DeleteBot.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.LeaveChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import Krasnovsky.DeleteBot.config.BotConfig;
import Krasnovsky.DeleteBot.config.BotConstants;
import lombok.Getter;

@Getter
@Component
public class DeleteCommentsBot extends TelegramLongPollingBot {

    private static long CHANNEL_ID = BotConstants.CHANNEL_ID_PARK;
    private static long TEST_ID = BotConstants.TEST_ID;
    private static long MONEY_CHANNEL_ID = BotConstants.MONEY_CHANNEL_ID;
    private static long BUSINESS_CHANNEL_ID = BotConstants.BUSINESS_CHANNEL_ID;
    private static long BUSINESS_CHAT_ID = BotConstants.BUSINESS_CHAT_ID;
    private static long BOSS_ID = BotConstants.BOSS_ID;
    private static long FATHER_ID = BotConstants.FATHER_ID;
    private static final String URL_REGEX = BotConstants.URL_REGEX;
    private static final Pattern URL_SYMBOLS = Pattern.compile(URL_REGEX);

    final BotConfig config;

    @SuppressWarnings("deprecation")
    public DeleteCommentsBot(BotConfig config) {
	this.config = config;
    }

    @Override
    public String getBotUsername() {

	return config.getBotName();
    }

    @Override
    public String getBotToken() {

	return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

	System.out.println("MESSAGE: " + update.getMessage().toString());

	if (update.hasMessage()) {

	    Message message = update.getMessage(); // Получаем новое сообщение

	    long userId = message.getFrom().getId();
	    System.out.println("//// USER_ID " + userId);
	    CHANNEL_ID = message.getChatId();// Получаем CHAT_ID отправителя
	    if (CHANNEL_ID != MONEY_CHANNEL_ID && CHANNEL_ID != BUSINESS_CHANNEL_ID && CHANNEL_ID != TEST_ID
		    && CHANNEL_ID != BUSINESS_CHAT_ID) {
		leaveGroup(CHANNEL_ID);
	    }
	    // if (userId != BOSS_ID) { // Исключаем сообщения автора && userId != Father_ID
	    // канала

	    Instant instant = Instant.ofEpochSecond(message.getDate()); // получаем дату
	    LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()); // сообщения //
											       // сообщения
	    // GetChatMember getChatMember = new
	    // GetChatMember(message.getChatId().toString(), userId);
	    // Integer joinDate = chatMember.getDate();
	    // long joinDateMillis = (long) joinDate * 1000;
//				System.out.println(message.getText() + " TEXT " + "\n\n");
//				System.out.println(message + " sender " + userId + "\n\n");
//				System.out.println(message.getChatId());

	    boolean urlStatus = checkUrlStatus(message); // есть ли в сообщении URL
	    System.out.println(urlStatus);

	    if (message.hasPhoto() && message.getText() == null) {
		if (checkPaint(message, urlStatus) && userId != 0 && message.getChatId() != null) {
		    // blockUser(CHANNEL_ID, userId);
		}
	    } else if (message.getText() != null) {
		if (checkMessage(message, urlStatus) && urlStatus == true) {
		    // blockUser(CHANNEL_ID, userId);
		}

	    }
	    // else if (message.isReply()) {
//					Message repliedMessage = message.getReplyToMessage();
//					System.out.println(repliedMessage);
//
//				} else if (update.hasChannelPost()) {
//
//					if (update.getChannelPost().getChatId() == CHANNEL_ID) {
//						Message message1 = update.getChannelPost();
//						urlStatus = checkUrlStatus(message1);
//						if (message1.getText() != null) {
//							String messageText = message1.getText();
//							checkMessage(messageText, urlStatus);
//						} else if (message1.hasPhoto() && message1.getText() == null) {
//							checkPaint(message1, urlStatus);
//						}
//					}
//				}
	    // } BOSS ID
	    if (userId == 873614042) {
		deleteMessage(message);
		sendMessage(CHANNEL_ID, "Фу! пользователь полное Г" + " " + "ID" + " " + userId);
	    }
	}
    }

    private static final Pattern PUNCT_SYMBOLS = Pattern.compile("[!\"#$%&'()*+,-./:;<«=»>4⃣?@\\[\\]^_`{|}~]\\n");

    private static final Pattern SMILES = Pattern.compile(
	    "[\\ud800-\\udbff\\udc00-\\udfff\\ud83c\\ud000-\\udfff" + "\\ud83d\\ud000-\\udfff\\ud83e\\ud000-\\udfff]");

    public static String removePunctuations(String source) {
	return PUNCT_SYMBOLS.matcher(source).replaceAll("");
    }

    public static String removeUrl(String source) {
	return URL_SYMBOLS.matcher(source).replaceAll("");
    }

    public static String removeSmiles(String source) {
	return SMILES.matcher(source).replaceAll("");
    }

    private boolean checkMessage(Message message, boolean urlStatus) {
	String messageText = message.getText();
	messageText = removeUrl(messageText);
	messageText = removePunctuations(messageText);
	messageText = removeSmiles(messageText);

	List<String> words = Arrays.asList(messageText.split(" "));
	System.out.println("!!!WORDS :" + words.toString());
//		Pattern cyrillic = Pattern.compile("^[а-яА-Я]+$");
//		Pattern latin = Pattern.compile("^[a-zA-Z]+$");
//		Matcher mCyrillic;
//		Matcher mLatin;
//
//		for (String word : words) {
//			mCyrillic = cyrillic.matcher(word);
//			mLatin = latin.matcher(word);
//			if (mCyrillic.find() && !mLatin.find()) {
//				System.out.println(word + " is cyrillic");
//			} else if (!mCyrillic.find() && mLatin.find()) {
//				System.out.println(word + " is latin");
//			} else if (!mCyrillic.find() && !mLatin.find() && word.length() > 1) {
//				System.out.println(word + " is mixed");
//			}
//		}

	String regex = "\\p{L}\\p{M}*|\\p{N}+|[\\p{P}\\p{S}]+";

	Pattern pattern = Pattern.compile(regex);
	Matcher matcher = pattern.matcher(messageText);

//		if (words.contains("Путин") || words.contains("Зеленский") || words.contains("Жириновский")
//				|| words.contains("Кадры точно не для детей!") || words.contains("Жиринọвскọгọ") && urlStatus == true) {
	if (matcher.find() && urlStatus == true) {
	    deleteMessage(message); // удаление сообщения
	    sendMessage(CHANNEL_ID, "Фу! плохой пользователь" + " " + "TEXT" + " " + urlStatus);
	    urlStatus = false;
	    return true;
	}
	/*
	 * if (!mCyrillic.find() && !mLatin.find()) { c break; }
	 * 
	 * }
	 */
	return false;

	// if (messageText.contains("τ") || messageText.contains("π") ||
	// messageText.contains("ρ")
	// || messageText.contains("u")) {
	// sendMessage(CHANNEL_ID, "Фу! плохой пользователь TEXT");
	// }
    }

    private void deleteMessage(Message message) {
	DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(CHANNEL_ID), message.getMessageId());
	try {
	    execute(deleteMessage);
	} catch (TelegramApiException e) {
	    e.printStackTrace();
	}
    }

    private void blockUser(Long chatId, Long userId) {
	RestrictChatMember restrictChatMember = new RestrictChatMember();
	restrictChatMember.setChatId(chatId);
	restrictChatMember.setUserId(userId);
	try {
	    execute(restrictChatMember);
	} catch (TelegramApiException e) {
	    e.printStackTrace();
	}
    }

    private Boolean checkPaint(Message message, boolean urlStatus) {

	List<PhotoSize> photoList = message.getPhoto(); // Получение информации о фото

	if (!photoList.isEmpty() && urlStatus == true) { // Проверка наличия фото
	    deleteMessage(message); // удаление сообщения
	    sendMessage(CHANNEL_ID, "Фу! плохой пользователь PHOTO" + " " + !photoList.isEmpty() + " " + urlStatus);
	    urlStatus = false;
	    return true;
	} else {
	    sendMessage(CHANNEL_ID, "Хороший пользователь PHOTO" + !photoList.isEmpty() + urlStatus);
	    return false;
	}
    }

    private boolean containsUrl(String text) {
	Pattern pattern = Pattern.compile(URL_REGEX);
	Matcher matcher = pattern.matcher(text);
	return matcher.find();
    }

    private boolean checkUrlStatus(Message message) {
	List<Boolean> tests = new ArrayList<>();
	if (message.getText() != null) {
	    tests.add(message.getText().matches(URL_REGEX));
	}
	if (message.getEntities() != null) {
	    for (MessageEntity entity : message.getEntities()) {
		tests.add(entity.getUrl() != null);
		tests.add(entity.getText().matches(URL_REGEX));
	    }
	}
	if (message.getCaptionEntities() != null) {
	    for (MessageEntity entity : message.getCaptionEntities()) {
		tests.add(entity.getUrl() != null);
		tests.add(entity.getText().matches(URL_REGEX));
	    }
	}
	System.out.println(tests);
	System.out.println("URLS :" + tests);
	if (tests.contains(true)) {
	    return true;
	} else {
	    return false;
	}
    }

    /// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private void sendMessage(long chatId, String textToSend) {
	SendMessage message = new SendMessage();
	message.setChatId(TEST_ID);
	message.setText(textToSend);

	try {
	    execute(message);
	} catch (TelegramApiException e) {

	}
    }

    public void leaveGroup(long groupId) {
	LeaveChat leaveChat = new LeaveChat();
	leaveChat.setChatId(groupId);

	try {
	    execute(leaveChat);
	    System.out.println("Бот покинул группу");
	} catch (TelegramApiException e) {
	    e.printStackTrace();
	}
    }
}
/// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! отправка
/// сообщения в чат
//	public void sendPostWithImage(long groupId, String imagePath) {
//		SendPhoto sendPhoto = new SendPhoto();
//		sendPhoto.setChatId(groupId);
//		sendPhoto.setPhoto(new InputFile(new File(imagePath)));
//		sendPhoto.setCaption("");
//
//		try {
//			// Отправка поста с изображением
//			Message sentMessage = execute(sendPhoto);
//			System.out.println("Пост с изображением успешно отправлен. ID сообщения: " + sentMessage.getMessageId());
//		} catch (TelegramApiException e) {
//			System.out.println("Ошибка при отправке поста с изображением: " + e.getMessage());
//		}
//	}