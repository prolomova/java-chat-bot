public interface IGameFactory {
    IGame create(Class<? extends IGame> gameClass, String fileName);
}
