package gui;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

import java.net.URL;
import java.util.*;

public class SellerFormController implements Initializable {

    private Seller entity;

    private SellerService service;

    private final List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private TextField textFieldId;

    @FXML
    private TextField textFieldName;

    @FXML
    private Label labelErrorName;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonCancel;

    public void setSeller(Seller entity){
        this.entity = entity;
    }

    public void setSellerService(SellerService service){
        this.service = service;
    }

    public void subscribeDataChangeListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }

    @FXML
    private void onButtonSaveAction(ActionEvent event){
        if(entity == null){
            throw new IllegalStateException("Entity was null");
        }
        if(service == null){
            throw new IllegalStateException("Service was null");
        }
        try{
            entity = getFormData();
            service.saveOrUpdate(entity);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        }
        catch (ValidationException e){
            setErrorMessages(e.getErrors());
        }
        catch (DbException e ){
            Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
        }
    }

    private void notifyDataChangeListeners() {
        for (DataChangeListener listener : dataChangeListeners){
            listener.onDataChanged();
        }
    }

    private Seller getFormData() {
        Seller object = new Seller();

        ValidationException exception = new ValidationException("Validation error");

        object.setId(Utils.tryParseToInt(textFieldId.getText()));

        if(textFieldName.getText() == null || textFieldName.getText().trim().equals("")){
            exception.addError("name", "Field can't be empty");
        }

        object.setName(textFieldName.getText());

        if(exception.getErrors().size() > 0){
            throw exception;
        }

        return object;
    }

    @FXML
    private void onButtonCancelAction(ActionEvent event){
        Utils.currentStage(event).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes(){
        Constraints.setTextFieldInteger(textFieldId);
        Constraints.setTextFieldMaxLength(textFieldName, 30);
    }

    public void updateFormData(){
        if(entity == null){
            throw new IllegalStateException("Entity was null");
        }
        textFieldId.setText(String.valueOf(entity.getId()));
        textFieldName.setText(entity.getName());
    }

    public void setErrorMessages(Map<String, String> errors){
        Set<String> fields = errors.keySet();

        if(fields.contains("name")){
            labelErrorName.setText(errors.get("name"));
        }
    }
}
