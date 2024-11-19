package es.ieslosmontecillos;

import com.gluonhq.charm.glisten.mvc.View;
import es.ieslosmontecillos.entities.Persona;
import es.ieslosmontecillos.entities.Provincia;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class InicioController {
    @FXML
    private View inicio;
    private DataUtil dataUtil;
    ObservableList<Provincia> olProv;
    ObservableList<Persona> olPers;
    private Pane rootMain = new Pane();

    @Deprecated
    public void iniciaApp(Event event) {
        try {
            System.out.println("Iniciando App");
            FXMLLoader
                    fxmlLoader
                    =
                    new FXMLLoader(getClass().getResource("views/AgendaView.fxml"));
            Pane rootAgendaView = fxmlLoader.load();
            rootMain.getChildren().add(rootAgendaView);
            AgendaViewController agendaViewController = fxmlLoader.getController();
            agendaViewController.setDataUtil(dataUtil);
            agendaViewController.setOlProvincias(olProv);
            agendaViewController.setOlPersonas(olPers);
            agendaViewController.cargarTodasPersonas();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
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
