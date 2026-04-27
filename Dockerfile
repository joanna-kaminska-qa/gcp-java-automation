# 1. Używamy gotowego obrazu z Javą
FROM eclipse-temurin:17-jdk-focal

# 2. Tworzymy folder wewnątrz kontenera, gdzie wrzucimy nasze pliki
WORKDIR /app

# 3. Kopiujemy plik Gradle i kod źródłowy (żeby kontener mógł zbudować projekt)
COPY . .

# 4. Nadajemy uprawnienia do uruchomienia Gradle
RUN chmod +x gradlew

# 5. Komenda, która odpali testy, gdy kontener wystartuje
CMD ["./gradlew", "test", "--tests", "CloudStorageTest"]