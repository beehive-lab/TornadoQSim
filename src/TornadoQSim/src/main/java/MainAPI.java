import circuit.Circuit;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class MainAPI {

    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        Reader reader = new FileReader("circuit_test1.json");
        Circuit circuit = gson.fromJson(reader, Circuit.class);
        reader.close();
    }
}
