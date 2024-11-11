package es.ieslosmontecillos;

import com.gluonhq.charm.glisten.mvc.View;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;


import java.io.IOException;

public class InicioController {
    @FXML
    private View inicio;
    private DataUtil dataUtil;
    ObservableList<Provincia> olProv;
    ObservableList<Persona> olPers;
    private Pane rootMain = new Pane();

    @FXML
    public void iniciaApp(Event event) {
        try {

            FXMLLoader
                    fxmlLoader
                    =
                    new FXMLLoader(getClass().getResource("fxml/AgendaView.fxml"));
            Pane rootAgendaView = fxmlLoader.load();
            rootMain.getChildren().add(rootAgendaView);
            AgendaViewController agendaViewController = fxmlLoader.getController();
            agendaViewController.setDataUtil(dataUtil);
            agendaViewController.setOlProvincias(olProv);
            agendaViewController.setOlPersonas(olPers);
            agendaViewController.cargarTodasPersonas();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error al cargar la vista");
            alert.setContentText("No se ha podido cargar AgendaView.fxml: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void setRootMain(Pane rootMain) {
        this.rootMain = rootMain;
    }

    public void setDataUtil(DataUtil dataUtil) {
        this.dataUtil = dataUtil;
    }

    public void setOlProv(ObservableList<Provincia> olProv) {
        this.olProv = olProv;
    }

    public void setOlPers(ObservableList<Persona> olPers) {
        this.olPers = olPers;
    }

}
