import com.google.cloud.storage.*;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CloudStorageTest {

    private static Storage storage;
    private static final String BUCKET_NAME = "bucket-joanny-1777296500266";

    // Klucz musi znajdować się w głównym folderze projektu
    private static final String KEY_PATH = "project-key.json";

    @BeforeAll
    static void setup() throws Exception {
        String gcpKeyJson = System.getenv("GCP_KEY_JSON");
        String projectId = System.getenv("GCP_PROJECT_ID");
        GoogleCredentials credentials;

        if (gcpKeyJson != null && !gcpKeyJson.isEmpty()) {
            // SCENARIUSZ GITHUB: Używamy klucza ze zmiennej środowiskowej
            System.out.println(">>> Inicjalizacja: Używam klucza z GitHub Secrets");
            credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(gcpKeyJson.getBytes(StandardCharsets.UTF_8))
            );
        } else {
            // SCENARIUSZ LOKALNY: Używamy pliku project-key.json
            System.out.println(">>> Inicjalizacja: Używam pliku lokalnego: " + KEY_PATH);
            credentials = GoogleCredentials.fromStream(new FileInputStream(KEY_PATH));
        }

        StorageOptions.Builder optionsBuilder = StorageOptions.newBuilder().setCredentials(credentials);

        // Jeśli mamy podane Project ID w env, to je ustawiamy
        if (projectId != null && !projectId.isEmpty()) {
            optionsBuilder.setProjectId(projectId);
        }

        storage = optionsBuilder.build().getService();
    }

    @Test
    @DisplayName("Test pozytywny 1: Sprawdzenie obecności powitania")
    public void testCzyPlikIstniejeWChmurze() {
        boolean exists = storage.get(BUCKET_NAME, "powitanie.txt").exists();
        assertTrue(exists, "Błąd: Plik powitanie.txt nie został znaleziony!");
    }

    @Test
    @DisplayName("Test pozytywny 2: Tworzenie i sprawdzanie nowego pliku")
    public void testTworzeniaINowegoPliku() {
        String nazwaNowegoPliku = "test_automatyczny_docker.txt";
        String zawartosc = "To jest plik stworzony przez kontener Docker!";

        // 1. Tworzymy plik w chmurze
        storage.create(
                BlobInfo.newBuilder(BUCKET_NAME, nazwaNowegoPliku).build(),
                zawartosc.getBytes(StandardCharsets.UTF_8)
        );

        // 2. Pobieramy go, aby sprawdzić czy faktycznie tam jest
        Blob blob = storage.get(BUCKET_NAME, nazwaNowegoPliku);

        // Asercja: sprawdzamy czy obiekt nie jest nullem (czyli czy istnieje)
        assertNotNull(blob, "Błąd! Plik nie został poprawnie utworzony w chmurze.");
    }

    @AfterEach
    void wyslijRaport(TestInfo testInfo) {
        // 1. Przygotowanie daty do nazwy raportu
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dataLudzka = sdf.format(new Date());

        // 2. Tworzenie nazwy pliku raportu
        String nazwaTestu = testInfo.getDisplayName().replace(" ", "_");
        String nazwaRaportu = "raport_" + nazwaTestu + "_" + dataLudzka + ".txt";

        // 3. Treść raportu
        String tresc = "=== RAPORT Z TESTU ===\n" +
                "Nazwa: " + testInfo.getDisplayName() + "\n" +
                "Data wykonania: " + new Date() + "\n" +
                "Status: Wykonano pomyślnie w kontenerze Docker.";

        // 4. Wysyłka raportu do GCP
        try {
            BlobId blobId = BlobId.of(BUCKET_NAME, nazwaRaportu);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
            storage.create(blobInfo, tresc.getBytes(StandardCharsets.UTF_8));
            System.out.println(">>> Automat OChK: Wysłano raport: " + nazwaRaportu);
        } catch (Exception e) {
            System.err.println("Błąd wysyłania raportu: " + e.getMessage());
        }
    }
}