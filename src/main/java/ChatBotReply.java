import java.util.List;

class ChatBotReply {
    String message;
    List<String> keyboardOptions;
    String imageUrl;
    String characterName;

    ChatBotReply(String message, List<String> options) {
        this.message = message;
        keyboardOptions = options;
        imageUrl = null;
        characterName = null;
    }

    ChatBotReply(String message, List<String> options, String imageUrl, String characterName) {
        this.message = message;
        keyboardOptions = options;
        this.imageUrl = imageUrl;
        this.characterName = characterName;
    }
}
