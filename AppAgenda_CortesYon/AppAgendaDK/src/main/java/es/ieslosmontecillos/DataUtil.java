package es.ieslosmontecillos;

import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.converter.JsonConverter;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.RestClient;
import es.ieslosmontecillos.entities.Persona;
import es.ieslosmontecillos.entities.Provincia;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataUtil {

    public static final String HOST = "http://localhost:8080";
    public static final String PATH_API = "/api/v1/";
    
    private ObservableList<Provincia> olProvincias = FXCollections.observableArrayList();
    private ObservableList<Persona> olPersonas = FXCollections.observableArrayList();

    public void obtenerTodasProvincias() {
        System.out.println("Se están solicitando las provincias...");
        RestClient restClient = RestClient.create()
                .method("GET")
                .host(HOST)
                .path(PATH_API + "PROVINCIA");
        GluonObservableList<Provincia> provincias = DataProvider.retrieveList(restClient.createListDataReader(Provincia.class));

        provincias.addListener((ListChangeListener<Provincia>) c -> {

            if (c.next()) {
                olProvincias.add(c.getList().get(c.getFrom()));
            }
        });
    }

    public ObservableList<Provincia> getOlProvincias() {
        return olProvincias;
    }

    public void obtenerTodasPersonas(){
        System.out.println("Se están solicitando las personas...");
        RestClient restClient = RestClient.create()
                .method("GET")
                .host(HOST)
                .path(PATH_API + "PERSONA");
        GluonObservableList<Persona> personas = DataProvider.retrieveList(restClient.createListDataReader(Persona.class));

        personas.addListener((ListChangeListener<Persona>) c -> {
            if(c.next()){
                olPersonas.add(c.getList().get(c.getFrom()));
                System.out.println("Lista personas: "
                        +
                        olPersonas.get(c.getFrom()).getNombre()
                        +
                        "-"
                        +
                        olPersonas.get(c.getFrom()).getApellidos()
                        +
                        "-"
                        +
                        olPersonas.get(c.getFrom()).getFechaNacimiento());
            }
        });
    }
    public ObservableList<Persona> getOlPersonas() {
        return olPersonas;
    }
    public void eliminarPersona(Persona persona){
        int idPersona = persona.getId();
        RestClient restClient = RestClient.create()
                .method("DELETE")
                .host(HOST)
                .path(PATH_API + "PERSONA/"+idPersona);
        GluonObservableList<Persona> personas = DataProvider.retrieveList(restClient.createListDataReader(Persona.class));
    }
    public int addPersona(Persona persona){
        int idPersona = persona.getId();
        JsonConverter<Persona> converter = new JsonConverter<>(Persona.class);
        JsonObject json = converter.writeToJson(persona);
        String dataBody = json.toString();
        System.out.println(dataBody);

        try
        {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(HOST + PATH_API + "PERSONA").openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.getOutputStream().write(dataBody.getBytes());

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader br1 = new BufferedReader(new InputStreamReader(inputStream));

            System.out.println("Response Code:" + httpURLConnection.getResponseCode());
            System.out.println("Response Message:" + httpURLConnection.getResponseMessage());

            StringBuilder response = new StringBuilder();
            String responseSingle = null;

            while ((responseSingle = br1.readLine()) != null) {
                response.append(responseSingle);
            }

            JsonReader jsonReader = Json.createReader(new StringReader(response.toString()));
            JsonObject object = jsonReader.readObject();
            jsonReader.close();
            return object.getInt("id");

        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            return 0;
        }

        /*RestClient restClient = RestClient.create()
                .method("POST")
                .host(HOST)
                .path(PATH_API + "PERSONA")
                .dataString(dataBody)
                .contentType("application/json");
        GluonObservableObject<Persona>
                personaNueva
                =
                DataProvider.retrieveObject(restClient.createObjectDataReader(Persona.class));
        System.out.println(personaNueva.get().getId());*/
    }
    public void actualizarPersona(Persona persona){
        int idPersona = persona.getId();
        JsonConverter<Persona> converter = new JsonConverter<>(Persona.class);
        JsonObject json = converter.writeToJson(persona);
        String dataBody = json.toString();
        System.out.println(dataBody);
        RestClient restClient = RestClient.create()
                .method("PUT")
                .host(HOST)
                .path(PATH_API + "PERSONA/"+idPersona)
                .dataString(dataBody)
                .contentType("application/json");
        GluonObservableObject<Persona> personaActualizada =
                DataProvider.retrieveObject(restClient.createObjectDataReader(Persona.class));
    }
    public Persona findPersonaByID(Integer id){
        int idPersona = id;
        RestClient restClient = RestClient.create()
                .method("GET")
                .host(HOST)
                .path(PATH_API + "PERSONA/"+idPersona);
        GluonObservableObject<Persona>
                persona =
                DataProvider.retrieveObject(restClient.createObjectDataReader(Persona.class));
        persona.initializedProperty().addListener((obs, ov, nv) -> {
            if (nv && persona.get() != null) {
                System.out.println("Recuperando persona seleccionada de la BD"
                        +persona.get().getNombre()+" "+persona.get().getApellidos());
            }
        });
            return persona.get();
    }

    public Provincia findProvinciaByID(Integer id)
    {
        int idProvincia = id;
        RestClient restClient = RestClient.create()
                .method("GET")
                .host(HOST)
                .path(PATH_API + "PROVINCIA/"+idProvincia);
        GluonObservableObject<Provincia> provincia = DataProvider.retrieveObject(restClient.createObjectDataReader(Provincia.class));
        provincia.initializedProperty().addListener((obs, ov, nv) -> {
            if (nv && provincia.get() != null) {
                System.out.println("Recuperando provincia seleccionada de la BD " +
                        provincia.get().getCodigo()+" - "+provincia.get().getNombre());
            }
        });
        return provincia.get();
    }
}