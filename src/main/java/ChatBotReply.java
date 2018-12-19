import java.util.List;

class ChatBotReply {
    String message;
    List<String> keyboardOptions;
    String imageUrl;
    String nextImg;

    public ChatBotReply(String message, List<String> options) {
        this.message = message;
        keyboardOptions = options;
        imageUrl = null;
        nextImg = null;
    }

    public ChatBotReply(String message, List<String> options, String imageUrl, String nextImg) {
        this.message = message;
        keyboardOptions = options;
        this.imageUrl = imageUrl;
        this.nextImg = nextImg;
    }

    public ChatBotReply(String message, List<String> options, String imageUrl) {
        this.message = message;
        keyboardOptions = options;
        this.imageUrl = imageUrl;
        nextImg = null;
    }
}
