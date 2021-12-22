package dad.enviaremailbackground;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Controller implements Initializable {

	private Model model = new Model();

	public Controller() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Email.fxml"));
		loader.setController(this);
		loader.load();
	}

	@FXML
	private Label asuntoLabel;

	@FXML
	private TextField asuntoText;

	@FXML
	private Button cerrarButton;

	@FXML
	private TextField contraText;

	@FXML
	private Label destinatarioLabel;

	@FXML
	private TextField destinatarioText;

	@FXML
	private GridPane emailGrid;

	@FXML
	private Button enviarButton;

	@FXML
	private TextField mensajeText;

	@FXML
	private TextField smptText;

	@FXML
	private TextField puertoText;

	@FXML
	private Label remitenteLabel;

	@FXML
	private TextField remitenteText;

	@FXML
	private Label smptLabel;

	@FXML
	private CheckBox sslCheck;

	@FXML
	private Label sslLabel;

	@FXML
	private Button vaciarButton;

	@FXML
	private VBox vboxText;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// bindings
		model.smtpProperty().bindBidirectional(smptText.textProperty());
		model.puertoProperty().bindBidirectional(puertoText.textProperty());
		model.remitenteProperty().bindBidirectional(remitenteText.textProperty());
		model.contraProperty().bindBidirectional(contraText.textProperty());
		model.destinatarioProperty().bindBidirectional(destinatarioText.textProperty());
		model.asuntoProperty().bindBidirectional(asuntoText.textProperty());
		model.mensajeProperty().bindBidirectional(mensajeText.textProperty());
	}

	@FXML
	void onCerrarAction(ActionEvent event) {
		Node source = (Node) event.getSource(); // devuelve el elemento que hice click
		Stage stage = (Stage) source.getScene().getWindow(); // devuelve la ventana donde se encuentra el elemento
		stage.close(); // cierra la ventana
	}

	@FXML
	void onVaciarAction(ActionEvent event) {
		model.setSmtp("");
		model.setPuerto("");
		model.setRemitente("");
		model.setContra("");
		model.setDestinatario("");
		model.setAsunto("");
		model.setMensaje("");
	}

	public GridPane getEmailGrid() {
		return emailGrid;
	}

	public Model getModel() {
		return model;
	}
	
	// Enviar es segundo plano el email
	@FXML
	void onEnviarActionSegundoPlano(ActionEvent event) {
		Email nuevoEmail = new SimpleEmail();
		Task<Void> enviarEmail = new Task<Void>() {
			
			@Override
			protected Void call() throws Exception{
				nuevoEmail.setHostName(model.getSmtp());
				nuevoEmail.setSmtpPort(Integer.parseInt(model.getPuerto()));
				nuevoEmail.setAuthenticator(new DefaultAuthenticator(model.getRemitente(), model.getContra()));
				nuevoEmail.setSSLOnConnect(true);
				nuevoEmail.setFrom(model.getRemitente());
				nuevoEmail.addTo(model.getDestinatario());
				nuevoEmail.setSubject(model.getAsunto());
				nuevoEmail.setMsg(model.getMensaje());
				
				nuevoEmail.send();
				return null;
				
			}
		};
		
		enviarEmail.setOnSucceeded(e -> {
			Alert alerta = new Alert(AlertType.INFORMATION);
			alerta.setTitle("Mensaje enviado");
			alerta.setHeaderText("Mensaje enviado con exito a '" + destinatarioText.textProperty().getValue() + "'.");
			alerta.show();
		});
		
		enviarEmail.setOnFailed(e -> {
			Alert alerta = new Alert(AlertType.ERROR);
			alerta.setTitle("Error");
			alerta.setHeaderText("No se pudo enviar el email");
			alerta.setContentText(e.getSource().getException().getMessage());
			alerta.show();
		});
		
		new Thread(enviarEmail).start();
	}

}
