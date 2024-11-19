package es.ieslosmontecillos;

import es.ieslosmontecillos.entities.Persona;
import es.ieslosmontecillos.entities.Provincia;
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

public class AgendaViewController implements Initializable
{
    private DataUtil dataUtil;
    private ObservableList<Provincia> olProvincias;
    private ObservableList<Persona> olPersonas;
    private Persona personaSeleccionada;

    @FXML
    private TableView<Persona> tableViewAgenda;
    @FXML
    private TableColumn<Persona,String> columnNombre;
    @FXML
    private TableColumn<Persona,String> columnApellidos;
    @FXML
    private TableColumn<Persona,String> columnEmail;
    @FXML
    private TableColumn<Persona,String> columnProvincia;
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private AnchorPane rootAgendaView;

    public void setDataUtil(DataUtil dataUtil){
        this.dataUtil=dataUtil;
    }
    public void setOlProvincias(ObservableList<Provincia> olProvincias) {
        this.olProvincias = olProvincias;
    }
    public void setOlPersonas(ObservableList<Persona> olPersonas) {
        this.olPersonas = olPersonas;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnApellidos.setCellValueFactory(new
                PropertyValueFactory<>("apellidos"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnProvincia.setCellValueFactory(
                cellData->{
                    SimpleStringProperty property=new SimpleStringProperty();
                    if (cellData.getValue().getProvincia()!= null){
                        property.setValue(cellData.getValue().getProvincia().getNombre());
                    }
                    return property;

                });
        tableViewAgenda.getSelectionModel().selectedItemProperty().addListener(
                (observable,oldValue,newValue)->{
                    personaSeleccionada=newValue;
                    if (personaSeleccionada != null){
                        textFieldNombre.setText(personaSeleccionada.getNombre());
                        textFieldApellidos.setText(personaSeleccionada.getApellidos());
                    } else {
                        textFieldNombre.setText("");
                        textFieldApellidos.setText("");
                    }
                });
    }

    public void cargarTodasPersonas(){
        tableViewAgenda.setItems(FXCollections.observableArrayList(olPersonas));
    }

    @FXML
    public void onActionButtonGuardar(ActionEvent actionEvent)
    {

        if (personaSeleccionada == null) return;

        personaSeleccionada.setNombre(textFieldNombre.getText());
        personaSeleccionada.setApellidos(textFieldApellidos.getText());
        dataUtil.actualizarPersona(personaSeleccionada);
        int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
        tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada);

        TablePosition<Persona, String> pos = new
                TablePosition<>(tableViewAgenda,numFilaSeleccionada,null);

        tableViewAgenda.getFocusModel().focus(pos);
        tableViewAgenda.requestFocus();

    }

    @FXML
    public void onActionButtonSuprimir(ActionEvent actionEvent)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("¿Desea suprimir el siguiente registro?");
        alert.setContentText(personaSeleccionada.getNombre() + " "
                + personaSeleccionada.getApellidos());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK)
        {
            // Acciones a realizar si el usuario acepta
            dataUtil.eliminarPersona(personaSeleccionada);
            tableViewAgenda.getItems().remove(personaSeleccionada);
            tableViewAgenda.getFocusModel().focus(null);
            tableViewAgenda.requestFocus();
        }
        else
        {
            // Acciones a realizar si el usuario cancela
            int numFilaSeleccionada=
                    tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada);
            TablePosition<Persona, String> pos = new TablePosition<>(tableViewAgenda,
                    numFilaSeleccionada,null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        }
    }

    @FXML
    public void onActionButtonEditar(ActionEvent actionEvent)
    {
        try
        {
            abrirDetalle(false);
        }
        catch (IOException ex)
        {
            System.out.println("Error volcado"+ex);
        }
    }

    @FXML
    public void onActionButtonNuevo(ActionEvent actionEvent)
    {
        try
        {
            abrirDetalle(true);
        }
        catch (IOException ex)
        {
            System.out.println("Error volcado"+ex);
        }
    }

    private void abrirDetalle(boolean nuevaPersona) throws IOException
    {
        // Cargar la vista de detalle
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/PersonaDetalleView.fxml"));
        Parent rootDetalleView=fxmlLoader.load();

        PersonaDetalleViewController personaDetalleViewController =
                (PersonaDetalleViewController) fxmlLoader.getController();
        personaDetalleViewController.setRootAgendaView(rootAgendaView);

        personaDetalleViewController.setTableViewPrevio(tableViewAgenda);
        personaDetalleViewController.setDataUtil(dataUtil);

        if (nuevaPersona)
        {
            personaSeleccionada = new Persona();
            personaDetalleViewController.setPersona(personaSeleccionada,true);
        }
        else
        {
            personaDetalleViewController.setPersona(personaSeleccionada,false);
        }

        personaDetalleViewController.mostrarDatos();

        // Ocultar la vista de la lista
        rootAgendaView.setVisible(false);
        //Añadir la vista detalle al StackPane principal para que se muestre
        StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
        rootMain.getChildren().add(rootDetalleView);
    }
}
