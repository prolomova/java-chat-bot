public class GeographicalCoords {
    public double lat;
    public double lon;

    public Pair<Double, Double> transformToRect(){
        // Номер зоны Гаусса-Крюгера (если точка рассматривается в системе
        // координат соседней зоны, то номер зоны следует присвоить вручную)
        int zone =  (int) (lon / 6.0 + 1);

        // Параметры эллипсоида Красовского
        double a = 6378245.0;          // Большая (экваториальная) полуось
        double b = 6356863.019;        // Малая (полярная) полуось
        double e2 = (Math.pow(a, 2) - Math.pow(b, 2)) / Math.pow(a, 2);  // Эксцентриситет
        double n = (a - b) / (a + b);        // Приплюснутость


        // Параметры зоны Гаусса-Крюгера
        double F = 1.0;                   // Масштабный коэффициент
        double Lat0 = 0.0;                // Начальная параллель (в радианах)
        double Lon0 = (zone * 6 - 3) * Math.PI / 180;  // Центральный меридиан (в радианах)
        double N0 = 0.0;                  // Условное северное смещение для начальной параллели
        double E0 = zone * 1e6 + 500000.0;    // Условное восточное смещение для центрального меридиана

        // Перевод широты и долготы в радианы
        double Lat = lat * Math.PI / 180.0;
        double Lon = lon * Math.PI / 180.0;

        // Вычисление переменных для преобразования
        double sinLat = Math.sin(Lat);
        double cosLat = Math.cos(Lat);
        double tanLat = Math.tan(Lat);

        double v = a * F * Math.pow(1 - e2 * Math.pow(sinLat, 2), -0.5);
        double p = a * F * (1 - e2) * Math.pow(1 - e2 * Math.pow(sinLat, 2), -1.5);
        double n2 = v / p - 1;
        double M1 = (1 + n + 5.0 / 4.0 * Math.pow(n, 2) + 5.0 / 4.0 * Math.pow(n, 3)) * (Lat - Lat0);
        double M2 = (3 * n + 3 * Math.pow(n, 2) + 21.0 / 8.0 * Math.pow(n, 3)) * Math.sin(Lat - Lat0) * Math.cos(Lat + Lat0);
        double M3 = (15.0 / 8.0 * Math.pow(n, 2) + 15.0 / 8.0 * Math.pow(n, 3)) * Math.sin(2 * (Lat - Lat0)) * Math.cos(2 * (Lat + Lat0));
        double M4 = 35.0 / 24.0 * Math.pow(n, 3) * Math.sin(3 * (Lat - Lat0)) * Math.cos(3 * (Lat + Lat0));
        double M = b * F * (M1 - M2 + M3 - M4);
        double I = M + N0;
        double II = v / 2 * sinLat * cosLat;
        double III = v / 24 * sinLat * Math.pow(cosLat, 3) * (5 - Math.pow(tanLat, 2) + 9 * n2);
        double IIIA = v / 720 * sinLat * Math.pow(cosLat, 5) * (61 - 58 * Math.pow(tanLat, 2) + Math.pow(tanLat, 4));
        double IV = v * cosLat;
        double V = v / 6 * Math.pow(cosLat, 3) * (v / p - Math.pow(tanLat, 2));
        double VI = v / 120 * Math.pow(cosLat, 5) * (5 - 18 * Math.pow(tanLat, 2) + Math.pow(tanLat, 4) + 14 * n2 - 58 * Math.pow(tanLat, 2) * n2);

        // Вычисление северного и восточного смещения (в метрах)
        double N = I + II * Math.pow(Lon - Lon0, 2) + III * Math.pow(Lon - Lon0, 4) + IIIA * Math.pow(Lon - Lon0, 6);
        double E = E0 + IV * (Lon - Lon0) + V * Math.pow(Lon - Lon0, 3) + VI * Math.pow(Lon - Lon0, 5);
        return new Pair<>(N, E);
    }

    public GeographicalCoords(double lon, double lat){
        this.lon = lon;
        this.lat = lat;
    }
}
