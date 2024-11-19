package es.ieslosmontecillos;

import es.ieslosmontecillos.entities.Persona;
import es.ieslosmontecillos.entities.Provincia;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

public class PersonaDetalleViewController
{
    public static final char CASADO='c';
    public static final char SOLTERO='s';
    public static final char VIUDO='v';

    public static final String CARPETA_FOTOS="Fotos";

    @javafx.fxml.FXML
    public ImageView imageViewFoto;

    @javafx.fxml.FXML
    private TextField textFieldNombre;
    @javafx.fxml.FXML
    private TextField textFieldSalario;
    @javafx.fxml.FXML
    private RadioButton radioButtonCasado;
    @javafx.fxml.FXML
    private TextField textFieldTelefono;
    @javafx.fxml.FXML
    private TextField textFieldEmail;
    @javafx.fxml.FXML
    private RadioButton radioButtonViudo;
    @javafx.fxml.FXML
    private DatePicker datePickerFechaNacimiento;
    @javafx.fxml.FXML
    private TextField textFieldNumHijos;
    @javafx.fxml.FXML
    private ComboBox<Provincia> comboBoxProvincia;
    @javafx.fxml.FXML
    private CheckBox checkBoxJubilado;
    @javafx.fxml.FXML
    private RadioButton radioButtonSoltero;
    @javafx.fxml.FXML
    private TextField textFieldApellidos;

    private TableView<Persona> tableViewPrevio;
    private Persona persona;
    private DataUtil dataUtil;
    private boolean nuevaPersona;

    private Pane rootAgendaView;
    @javafx.fxml.FXML
    private Pane rootPersonaDetalleView;

    public void setRootAgendaView(Pane rootAgendaView){
        this.rootAgendaView = rootAgendaView;
    }

    public void setTableViewPrevio(TableView<Persona> tableViewPrevio){
        this.tableViewPrevio=tableViewPrevio;
    }

    public void setPersona(Persona persona, Boolean nuevaPersona) {
        if (!nuevaPersona) {
            this.persona = persona;
        } else {
            this.persona = new Persona();
        }
        this.nuevaPersona = nuevaPersona;
    }

    @javafx.fxml.FXML
    public void initialize() {
    }

    @javafx.fxml.FXML
    public void onActionButtonCancelar(ActionEvent actionEvent) {
        int numFilaSeleccionada =
                tableViewPrevio.getSelectionModel().getSelectedIndex();
        TablePosition<Persona, String> pos = new TablePosition<>(tableViewPrevio,
                numFilaSeleccionada,null);
        tableViewPrevio.getFocusModel().focus(pos);
        tableViewPrevio.requestFocus();
        volverAgenda();
    }

    @javafx.fxml.FXML
    public void onActionButtonGuardar(ActionEvent actionEvent)
    {
        persona.setNombre(textFieldNombre.getText());
        persona.setApellidos(textFieldApellidos.getText());
        persona.setTelefono(textFieldTelefono.getText());
        persona.setEmail(textFieldEmail.getText());

        boolean errorFormato = false;

        if (!textFieldNumHijos.getText().isEmpty()) {

            try {
                persona.setNumHijos(Integer.valueOf(textFieldNumHijos.getText()));
            } catch (NumberFormatException ex) {
                errorFormato = true;
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Número de hijos no válido");
                alert.showAndWait();
                textFieldNumHijos.requestFocus();
            }
        }

        if (!textFieldSalario.getText().isEmpty()){
            try {
                Double dSalario = Double.valueOf(textFieldSalario.getText());
                persona.setSalario(dSalario);
            } catch(NumberFormatException ex) {
                errorFormato = true;
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Salario no válido");
                                alert.showAndWait();
                textFieldSalario.requestFocus();
            }
        }

        persona.setJubilado(checkBoxJubilado.isSelected() ? 1 : 0);

        if (radioButtonCasado.isSelected()){
            persona.setEstadoCivil(Character.toString(CASADO));
        } else if (radioButtonSoltero.isSelected()){
            persona.setEstadoCivil(Character.toString(SOLTERO));
        } else if (radioButtonViudo.isSelected()){
            persona.setEstadoCivil(Character.toString(VIUDO));
        }

        if (datePickerFechaNacimiento.getValue() != null){
            LocalDate localDate = datePickerFechaNacimiento.getValue();
            ZonedDateTime zonedDateTime =
                    localDate.atStartOfDay(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            Date date = Date.from(instant);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaComoCadena = sdf.format(date);
            persona.setFechaNacimiento(fechaComoCadena);
        } else {
            persona.setFechaNacimiento(null);
        }

        if (comboBoxProvincia.getValue() != null)
        {
            persona.setProvincia(comboBoxProvincia.getValue());
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,"Debe indicar una provincia");
                    alert.showAndWait();
            errorFormato = true;
        }

        // Recoger datos de pantalla
        if (!errorFormato) { // Los datos introducidos son correctos
            try {
                int numFilaSeleccionada;

                if (nuevaPersona){
                    persona.setId(dataUtil.addPersona(persona));

                    tableViewPrevio.getItems().add(persona);
                    numFilaSeleccionada = tableViewPrevio.getItems().size()- 1;
                    tableViewPrevio.getSelectionModel().select(numFilaSeleccionada);
                    tableViewPrevio.scrollTo(numFilaSeleccionada);
                } else {
                    dataUtil.actualizarPersona(persona);
                    numFilaSeleccionada=
                            tableViewPrevio.getSelectionModel().getSelectedIndex();
                    tableViewPrevio.getItems().set(numFilaSeleccionada,persona);
                }

                TablePosition<Persona, String> pos = new TablePosition<>(tableViewPrevio,
                        numFilaSeleccionada,null);

                tableViewPrevio.getFocusModel().focus(pos);
                tableViewPrevio.requestFocus();
            } catch (Exception ex) { //Los datos introducidos no cumplen requisitos
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("No se han podido guardar los cambios. "
                                +
                                "Compruebe que los datos cumplen los requisitos");
                alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();
            }
        }

        volverAgenda();
    }

    private void volverAgenda()
    {
        StackPane rootMain =
                (StackPane) rootPersonaDetalleView.getScene().getRoot();
        rootMain.getChildren().remove(rootPersonaDetalleView);
        rootAgendaView.setVisible(true);
    }

    public void setDataUtil(DataUtil dataUtil)
    {
        this.dataUtil = dataUtil;
    }

    public void mostrarDatos(){
        textFieldNombre.setText(persona.getNombre());
        textFieldApellidos.setText(persona.getApellidos());
        textFieldTelefono.setText(persona.getTelefono());
        textFieldEmail.setText(persona.getEmail());
        // Falta implementar el código para el resto de controles

        if (persona.getNumHijos() != null){
            textFieldNumHijos.setText(persona.getNumHijos().toString());
        }
        if (persona.getSalario() != null){
            textFieldSalario.setText(persona.getSalario().toString());
        }
        if (persona.getJubilado() != null && persona.getJubilado() == 1) {
            checkBoxJubilado.setSelected(true);
        } else {
            checkBoxJubilado.setSelected(false);
        }

        if (persona.getEstadoCivil() != null){
            switch(persona.getEstadoCivil().charAt(0)){
                case CASADO:
                    radioButtonCasado.setSelected(true);
                    break;
                case SOLTERO:
                    radioButtonSoltero.setSelected(true);
                    break;
                case VIUDO:
                    radioButtonViudo.setSelected(true);
                    break;
            }
        }

        try {
            if (persona.getFechaNacimiento() != null){
                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                Date fecNac = formato.parse(persona.getFechaNacimiento());
                LocalDate fechaNac =
                        fecNac.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                datePickerFechaNacimiento.setValue(fechaNac);
            }
        } catch (ParseException e) {
            e.printStackTrace(System.out);
        }

        comboBoxProvincia.setItems(dataUtil.getOlProvincias());

        if (persona.getProvincia() != null){
            comboBoxProvincia.setValue(persona.getProvincia());
        }

        comboBoxProvincia.setCellFactory(
                (ListView<Provincia> l)-> new ListCell<>(){
                    @Override
                    protected void updateItem(Provincia provincia, boolean empty){
                        super.updateItem(provincia, empty);
                        if (provincia == null || empty){
                            setText("");
                        } else {
                            setText(provincia.getCodigo()+"-"+provincia.getNombre());
                        }
                    }
                });

        comboBoxProvincia.setConverter(new StringConverter<>(){
            @Override
            public String toString(Provincia provincia){
                if (provincia == null){
                    return null;
                } else {
                    return provincia.getCodigo()+"-"+provincia.getNombre();
                }
            }

            @Override
            public Provincia fromString(String string) {
                return null;
            }
        });

        if (persona.getFoto() != null){
            String imageFileName=persona.getFoto();
            File file = new File(CARPETA_FOTOS+"/"+imageFileName);

            if (file.exists()){
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            } else {
                Alert alert=new Alert(Alert.AlertType.INFORMATION,"No se encuentra la imagen en "+ file.toURI());
                        alert.showAndWait();
            }
        }
    }

    @javafx.fxml.FXML
    private void onActionButtonExaminar(ActionEvent event){
        File carpetaFotos = new File(CARPETA_FOTOS);
        if (!carpetaFotos.exists()){
            carpetaFotos.mkdir();
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes (jpg, png)", "*.jpg", "*.png"),
                new FileChooser.ExtensionFilter("Todos los archivos","*.*"));
        File file = fileChooser.showOpenDialog(rootPersonaDetalleView.getScene().getWindow());
        if (file != null){
            try {
                Files.copy(file.toPath(),new File(CARPETA_FOTOS+
                        "/"+file.getName()).toPath());
                persona.setFoto(file.getName());
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            } catch (FileAlreadyExistsException ex){
                Alert alert = new Alert(Alert.AlertType.WARNING,"Nombre de archivo duplicado");
                        alert.showAndWait();
            } catch (IOException ex){
                Alert alert = new Alert(Alert.AlertType.WARNING,"No se ha podido guardar la imagen");
                        alert.showAndWait();
            }
        }
    }

    @javafx.fxml.FXML
    private void onActionSuprimirFoto(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar supresión de imagen");
        alert.setHeaderText("¿Desea SUPRIMIR el archivo asociado a la imagen,\n" +
                "quitar la foto pero MANTENER el archivo, \no CANCELAR la operación?");
        alert.setContentText("Elija la opción deseada:");
        ButtonType buttonTypeEliminar = new ButtonType("Suprimir");
        ButtonType buttonTypeMantener = new ButtonType("Mantener");
        ButtonType buttonTypeCancel = new ButtonType("Cancelar",
                ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeEliminar, buttonTypeMantener, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == buttonTypeEliminar) {
            String imageFileName = persona.getFoto();
            File file = new File(CARPETA_FOTOS + "/" + imageFileName);
            if (file.exists()) {
                file.delete();
            }
            persona.setFoto(null);
            imageViewFoto.setImage(null);
        } else if (result.isPresent() && result.get() == buttonTypeMantener) {
            persona.setFoto(null);
            imageViewFoto.setImage(null);
        }
    }
}
