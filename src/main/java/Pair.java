public class Pair<K, L>{
    private K first;
    private L second;

    public Pair(K first, L second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public L getSecond() {
        return second;
    }
}
