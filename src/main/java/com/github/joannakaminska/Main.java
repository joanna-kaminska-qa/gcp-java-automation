package com.github.joannakaminska;

import com.google.cloud.storage.*;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class Main {
    // Twoja stała ścieżka do klucza
    private static final String KEY_PATH = "C:\\Users\\Joanna\\Desktop\\Testowanie\\Praktyka\\GCP-Trial\\project-e3943191-ac62-4fa2-8f9-67a860e78db9.json";
    private static final String BUCKET_NAME = "bucket-joanny-1777296500266";

    public static void main(String[] args) {
        try {
            Storage storage = polaczZChmura();

            // TERAZ MOŻESZ WYBIERAĆ CO CHCESZ ZROBIĆ, ODKOMENTOWUJĄC LINJKI:

            // 1. Wysyłanie pliku
           // wyslijPlik(storage, "powitanie.txt", "To jest nowa, wyedytowana treść mojego powitania!");

            // 2. Pobieranie i czytanie pliku
           // czytajPlik(storage, "powitanie.txt");

            // 3. Usuwanie pliku
            // usunPlik(storage, "raport.txt");

            // Pobieramy plik z chmury i zapisujemy go np. na Pulpicie (zmień nazwę użytkownika w ścieżce!)
            pobierzPlikNaDysk(storage, "powitanie.txt", "C:\\Users\\Joanna\\Desktop\\pobrane_z_chmury.txt");

        } catch (Exception e) {
            System.err.println("Wystąpił błąd: " + e.getMessage());
        }
    }

    // --- TWOJE NARZĘDZIA (METODY) ---

    private static Storage polaczZChmura() throws Exception {
        return StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(KEY_PATH)))
                .build()
                .getService();
    }

    private static void wyslijPlik(Storage storage, String nazwaPliku, String tresc) {
        BlobId blobId = BlobId.of(BUCKET_NAME, nazwaPliku);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
        storage.create(blobInfo, tresc.getBytes(StandardCharsets.UTF_8));
        System.out.println(">>> Wysłano plik: " + nazwaPliku);
    }

    private static void czytajPlik(Storage storage, String nazwaPliku) {
        byte[] content = storage.readAllBytes(BUCKET_NAME, nazwaPliku);
        System.out.println(">>> Treść pliku z chmury: " + new String(content, StandardCharsets.UTF_8));
    }

    private static void usunPlik(Storage storage, String nazwaPliku) {
        boolean usunieto = storage.delete(BUCKET_NAME, nazwaPliku);
        System.out.println(">>> Czy usunięto plik " + nazwaPliku + "? " + usunieto);
    }

    private static void pobierzPlikNaDysk(Storage storage, String nazwaZChmury, String sciezkaLokalna) {
        Blob blob = storage.get(BUCKET_NAME, nazwaZChmury);
        blob.downloadTo(java.nio.file.Paths.get(sciezkaLokalna));
        System.out.println(">>> Plik " + nazwaZChmury + " został pobrany na dysk: " + sciezkaLokalna);
    }
}