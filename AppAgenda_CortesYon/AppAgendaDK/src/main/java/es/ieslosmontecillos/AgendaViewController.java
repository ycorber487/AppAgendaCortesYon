package es.ieslosmontecillos;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AgendaViewController implements Initializable {

    private DataUtil dataUtil;  // Objeto para acceder a las utilidades de datos
    private ObservableList<Provincia> olProvincias;  // Lista observable de provincias
    private ObservableList<Persona> olPersonas;  // Lista observable de personas
    private Persona personaSeleccionada;  // Persona seleccionada en la tabla

    @FXML
    private AnchorPane rootAgendaView;  // Vista raíz de la agenda

    // Elementos de la interfaz gráfica
    @FXML
    private TableView<Persona> tableViewAgenda;  // Tabla para mostrar las personas
    @FXML
    private TextField textFieldNombre;  // Campo de texto para el nombre
    @FXML
    private TableColumn<Persona, String> columnNombre;  // Columna de nombre en la tabla
    @FXML
    private TextField textFieldApellidos;  // Campo de texto para apellidos
    @FXML
    private TableColumn<Persona, String> columnApellidos;  // Columna de apellidos en la tabla
    @FXML
    private TableColumn<Persona, String> columnProvincia;  // Columna de provincia en la tabla
    @FXML
    private TableColumn<Persona, String> columnEmail;  // Columna de email en la tabla

    // Métodos para establecer datos de provincias y personas
    public void setDataUtil(DataUtil dataUtil) {
        this.dataUtil = dataUtil;
    }

    public void setOlProvincias(ObservableList<Provincia> olProvincias) {
        this.olProvincias = olProvincias;
    }

    public void setOlPersonas(ObservableList<Persona> olPersonas) {
        this.olPersonas = olPersonas;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configura las columnas de la tabla con las propiedades de las personas
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Configura la columna de provincia con el nombre de la provincia
        columnProvincia.setCellValueFactory(cellData -> {
            SimpleStringProperty property = new SimpleStringProperty();
            if (cellData.getValue().getProvincia() != null) {
                property.setValue(cellData.getValue().getProvincia().getNombre());
            }
            return property;
        });

        // Acción cuando se selecciona una persona en la tabla y muestre su nombre y apellidos
        tableViewAgenda.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    personaSeleccionada = newValue;
                    if (personaSeleccionada != null) {
                        // Si hay una persona seleccionada, mostrar sus datos en los campos de texto
                        textFieldNombre.setText(personaSeleccionada.getNombre());
                        textFieldApellidos.setText(personaSeleccionada.getApellidos());
                    } else {
                        // Si no hay selección, limpiar los campos
                        textFieldNombre.setText("");
                        textFieldApellidos.setText("");
                    }
                });
    }

    // Metodo para cargar todas las personas en la tabla
    public void cargarTodasPersonas() {
        if (olPersonas != null) {
            tableViewAgenda.setItems(FXCollections.observableArrayList(olPersonas));
        }
    }

    // Acción cuando se presiona el botón "Guardar"
    @FXML
    public void onActionButtonGuardar(ActionEvent actionEvent) {
        if (personaSeleccionada == null) return;  // No hacer nada si no hay persona seleccionada

        // Actualiza los datos de la persona seleccionada
        personaSeleccionada.setNombre(textFieldNombre.getText());
        personaSeleccionada.setApellidos(textFieldApellidos.getText());
        dataUtil.actualizarPersona(personaSeleccionada);

        // Actualiza la tabla con los nuevos datos
        int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
        tableViewAgenda.getItems().set(numFilaSeleccionada, personaSeleccionada);

        // Veulve a enfocar la fila seleccionada en la tabla
        TablePosition<Persona, String> pos = new TablePosition<>(tableViewAgenda, numFilaSeleccionada, null);
        tableViewAgenda.getFocusModel().focus(pos);
        tableViewAgenda.requestFocus();
    }

    // Acción cuando se presiona el botón "Suprimir"
    @FXML
    public void onActionButtonSuprimir(ActionEvent actionEvent) {
        if (personaSeleccionada == null) return;  // No hace nada si no hay persona seleccionada

        // Crea una alerta de confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("¿Desea suprimir el siguiente registro?");
        alert.setContentText(personaSeleccionada.getNombre() + " " + personaSeleccionada.getApellidos());

        Optional<ButtonType> result = alert.showAndWait();  // Muestra la alerta y espera la respuesta
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Si el usuario confirma, eliminar la persona
            dataUtil.eliminarPersona(personaSeleccionada);
            tableViewAgenda.getItems().remove(personaSeleccionada);
            tableViewAgenda.getFocusModel().focus(null);
            tableViewAgenda.requestFocus();
        } else {
            // Si el usuario cancela, restaura la selección en la tabla
            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada, personaSeleccionada);
            TablePosition<Persona, String> pos = new TablePosition<>(tableViewAgenda, numFilaSeleccionada, null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        }
    }

    // Acción cuando se presiona el botón "Editar"
    @FXML
    public void onActionButtonEditar(ActionEvent actionEvent) {
        try {
            // Carga la vista de detalles desde el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/PersonaDetalleView.fxml"));
            Parent rootDetalleView = fxmlLoader.load();

            PersonaDetalleViewController personaDetalleViewController = fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);  // Establecer la vista raíz

            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);  // Pasar la tabla actual al controlador de detalles
            personaDetalleViewController.setDataUtil(dataUtil);  // Pasar el objeto DataUtil al controlador de detalles

            // Si hay una persona seleccionada, la pasa al controlador
            personaDetalleViewController.setPersona(personaSeleccionada, false);

            personaDetalleViewController.mostrarDatos();  // Mostrar los datos de la persona en la vista de detalles

            // Oculta la vista actual de la agenda
            rootAgendaView.setVisible(false);

            // Añade la vista de detalles al StackPane principal para que se muestre
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);

        } catch (IOException ex) {
            System.out.println("Error volcado" + ex);  // Muestra error en caso de fallo
        }
    }

    // Acción cuando se presiona el botón "Nuevo"
    @FXML
    public void onActionButtonNuevo(ActionEvent actionEvent) {
        try {
            // Cargar la vista de detalles desde el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/PersonaDetalleView.fxml"));
            Parent rootDetalleView = fxmlLoader.load();

            PersonaDetalleViewController personaDetalleViewController = fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);  // Establece la vista raíz

            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);  // Pasa la tabla actual al controlador de detalles
            personaDetalleViewController.setDataUtil(dataUtil);  // Pasa el objeto DataUtil al controlador de detalles

            // Crea una nueva persona para agregarla
            personaSeleccionada = new Persona();
            personaDetalleViewController.setPersona(personaSeleccionada, true);

            personaDetalleViewController.mostrarDatos();  // Mostrar los datos de la persona en la vista de detalles

            // Oculta la vista actual de la agenda
            rootAgendaView.setVisible(false);

            // Añade la vista de detalles al StackPane principal para que se muestre
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);

        } catch (IOException ex) {
            System.out.println("Error volcado" + ex);  // Mostrar error en caso de fallo
        }
    }



}
