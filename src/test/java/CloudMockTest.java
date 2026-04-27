import com.google.cloud.storage.*;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class CloudMockTest {

    // Używamy uniwersalnego kontenera, który po prostu udaje serwer Google
    @Container
    private static final GenericContainer<?> GCS_SERVER = new GenericContainer<>(
            DockerImageName.parse("fsouza/fake-gcs-server")
    ).withExposedPorts(4443).withCommand("-scheme http");

    private static Storage storage;
    private static final String BUCKET_NAME = "testowy-bucket";

    @BeforeAll
    static void setup() {
        // Pobieramy adres IP i port, który Docker nadał naszej udawanej chmurze
        String host = "http://" + GCS_SERVER.getHost() + ":" + GCS_SERVER.getMappedPort(4443);

        storage = StorageOptions.newBuilder()
                .setHost(host)
                .setProjectId("test-project")
                .build()
                .getService();

        storage.create(BucketInfo.of(BUCKET_NAME));
    }

    @Test
    @DisplayName("Test symulacji: Wysyłka do uniwersalnego kontenera")
    void testLokalnejWysylki() {
        String nazwaPliku = "test-mock.txt";
        storage.create(           // W prawdziwym Google Cloud Twój bucket już istnieje. W Testcontainers startujemy z pustym serwerem, więc musimy ten „kubełek” sobie stworzyć na starcie, żeby mieć gdzie wrzucić plik.
                BlobInfo.newBuilder(BUCKET_NAME, nazwaPliku).build(),
                "Dane wysłane do uniwersalnego symulatora!".getBytes(StandardCharsets.UTF_8)
        );

        assertTrue(storage.get(BUCKET_NAME, nazwaPliku).exists());
        System.out.println(">>> Sukces! Symulator w Dockerze odebrał dane.");
    }
}