package Krasnovsky.DeleteBot.service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

public class DataExtractor {
	private static boolean urlStatus = false;

	public boolean extractData(Message message) {
		Map<String, String> data = new HashMap<>();
		data.put("url", "<N/A>");
		data.put("email", "<N/A>");
		data.put("code", "<N/A>");

		if (message.hasEntities()) {
			for (MessageEntity entity : message.getEntities()) {
				if (entity.getType().equals("url") && entity.getOffset() != null && entity.getLength() != null) {
					urlStatus = true;
					String url = message.getText().substring(entity.getOffset(),
							entity.getOffset() + entity.getLength());
					String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);
					data.put("url", decodedUrl);
				} else if (entity.getType().equals("email") && entity.getOffset() != null
						&& entity.getLength() != null) {
					String email = message.getText().substring(entity.getOffset(),
							entity.getOffset() + entity.getLength());
					data.put("email", email);
				} else if (entity.getType().equals("code") && entity.getOffset() != null
						&& entity.getLength() != null) {
					String code = message.getText().substring(entity.getOffset(),
							entity.getOffset() + entity.getLength());
					data.put("code", code);
				}
			}
		} else if (message.getCaption() != null) {
			int captionLenth = message.getCaption().length();
			System.out.println(message.getCaption() + " caption");
			for (MessageEntity entity : message.getCaptionEntities()) {
				if (entity.getType().equals("url") && entity.getOffset() != null && entity.getLength() != null) {
					String url = entity.getText().substring(entity.getOffset(),
							entity.getOffset() + entity.getLength());
					String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);
					urlStatus = decodedUrl.length() == captionLenth ? true : false;
					data.put("url", decodedUrl);
				} else if (entity.getType().equals("email") && entity.getOffset() != null
						&& entity.getLength() != null) {
					String email = entity.getText().substring(entity.getOffset(),
							entity.getOffset() + entity.getLength());
					data.put("email", email);
				} else if (entity.getType().equals("code") && entity.getOffset() != null
						&& entity.getLength() != null) {
					String code = entity.getText().substring(entity.getOffset(),
							entity.getOffset() + entity.getLength());
					data.put("code", code);
				}
			}
		}

		String reply = "Вот что я нашёл:\n" + "URL: " + data.get("url") + "\n" + "E-mail: " + data.get("email") + "\n"
				+ "Пароль: " + data.get("code");

		// Отправка ответного сообщения
		// replace "message.reply(reply)" with your actual implementation
		System.out.println(reply + "\n" + urlStatus + "urlSTATUS");
		return urlStatus;
	}
}
