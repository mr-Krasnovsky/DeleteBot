package Krasnovsky.DeleteBot.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import Krasnovsky.DeleteBot.config.BotConfig;

@Component
public class DeleteCommentsBot extends TelegramLongPollingBot {

	private static long CHANNEL_ID = -1001889571875L;
	private static long BOSS_ID = 163862455;
	private static long Father_ID = 241147591;

	private static final String URL_REGEX = "https?://\\S+";

	final BotConfig config;

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

		if (update.hasMessage()) {

			Message message = update.getMessage(); // Получаем новое сообщение
			Long userId = message.getFrom().getId(); // Получаем USER_ID отправителя

			if (userId != BOSS_ID && userId != Father_ID) { // Отсекаем сообщения автора канала

				Instant instant = Instant.ofEpochSecond(message.getDate());
				LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()); // получаем дату
																									// сообщения

				GetChatMember getChatMember = new GetChatMember(message.getChatId().toString(), userId);

				String member = getChatMember.getMethod();
				// Integer joinDate = chatMember.getDate();
				// long joinDateMillis = (long) joinDate * 1000;

				System.out.println(message + " sender " + userId + "\n\n" + " date " + dateTime);
				System.out.println(message.getChatId());

				boolean urlStatus = checkUrlStatus(message);
				if (message.getText() != null) {
					String messageText = message.getText();
					checkMessage(messageText, urlStatus);
				} else if (message.hasPhoto() && message.getText() == null) {
					checkPaint(message, urlStatus);
				} else if (message.isReply()) {
					Message repliedMessage = message.getReplyToMessage();
					System.out.println(repliedMessage);
				}
			} else if (update.hasChannelPost()) {
				if (update.getChannelPost().getChatId() == CHANNEL_ID) {
					Message message1 = update.getChannelPost();
					boolean urlStatus = checkUrlStatus(message1);
					if (message1.getText() != null) {
						String messageText = message1.getText();
						checkMessage(messageText, urlStatus);
					} else if (message1.hasPhoto() && message1.getText() == null) {
						checkPaint(message1, urlStatus);
					}
				}
			}
		}
	}

	private static final Pattern PUNCT_SYMBOLS = Pattern.compile("[!\"#$%&'()*+,-./:;<«=»>4⃣?@\\[\\]^_`{|}~]\\n");
	private static final Pattern URL_SYMBOLS = Pattern.compile(URL_REGEX);
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

	private void checkMessage(String messageText, boolean urlStatus) {
		messageText = removeUrl(messageText);
		messageText = removePunctuations(messageText);
		messageText = removeSmiles(messageText);

		String[] words = messageText.split(" ");
		Pattern cyrillic = Pattern.compile("^[а-яА-Я]+$");
		Pattern latin = Pattern.compile("^[a-zA-Z]+$");
		Matcher mCyrillic;
		Matcher mLatin;

		for (String word : words) {
			mCyrillic = cyrillic.matcher(word);
			mLatin = latin.matcher(word);
			if (mCyrillic.find() && !mLatin.find()) {
				System.out.println(word + " is cyrillic");
			} else if (!mCyrillic.find() && mLatin.find()) {
				System.out.println(word + " is latin");
			} else if (!mCyrillic.find() && !mLatin.find() && word.length() > 1) {
				System.out.println(word + " is mixed");

			}
		}
		/*
		 * if (!mCyrillic.find() && !mLatin.find()) { c break; }
		 * 
		 * }
		 */

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

	private void checkPaint(Message message, boolean urlStatus) {

		List<PhotoSize> photoList = message.getPhoto(); // Получение информации о фото

		if (!photoList.isEmpty() && urlStatus == true) { // Проверка наличия фото
			// deleteMessage(message); // удаление сообщения
			sendMessage(CHANNEL_ID, "Фу! плохой пользователь" + " " + !photoList.isEmpty() + " " + urlStatus);
			urlStatus = false;
		} else {
			checkMessage(message.getCaption(), urlStatus);
			// sendMessage(CHANNEL_ID, "Хороший пользователь PHOTO" + !photoList.isEmpty() +
			// urlStatus);
		}
	}

	private boolean containsUrl(String text) {
		Pattern pattern = Pattern.compile(URL_REGEX);
		Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}

	private boolean checkUrlStatus(Message message) {
		DataExtractor extractor = new DataExtractor();
		return extractor.extractData(message);
	}

	/// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	private void sendMessage(long chatId, String textToSend) {
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText(textToSend);

		try {
			execute(message);
		} catch (TelegramApiException e) {

		}
	}
}
