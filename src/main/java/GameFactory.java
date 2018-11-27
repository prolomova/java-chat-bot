import java.lang.reflect.InvocationTargetException;

public class GameFactory implements IGameFactory {
    @Override
    public IGame create(Class<? extends IGame> game, String fileName)
    {
        try {
            return game
                    .getDeclaredConstructor(String.class)
                    .newInstance(fileName);
        }
        catch (NoSuchMethodException |
                IllegalAccessException |
                InstantiationException |
                InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}