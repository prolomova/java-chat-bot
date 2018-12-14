import org.telegram.telegrambots.meta.api.objects.Location;

public interface IGame {
    String getInitialMessage(int userId);

    ChatBotReply proceedRequest(Location request, int userId);

    void markActive(int userId);
    void markInactive(int userId);

    boolean isActive(int userId);
}
