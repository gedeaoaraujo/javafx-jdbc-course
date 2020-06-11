package gui;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class SellerFormController implements Initializable {

    private Seller entity;

    private SellerService sellerService;

    private DepartmentService departmentService;

    private final List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private TextField textFieldId;

    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private DatePicker datePickerBirthDate;

    @FXML
    private TextField textFieldBaseSalary;

    @FXML
    private ComboBox<Department> comboBoxDepartment;

    @FXML
    private Label labelErrorName;

    @FXML
    private Label labelErrorEmail;

    @FXML
    private Label labelErrorBirthDate;

    @FXML
    private Label labelErrorBaseSalary;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonCancel;

    private ObservableList<Department> observableList;

    public void setSeller(Seller entity){
        this.entity = entity;
    }

    public void setServices(SellerService sellerService, DepartmentService departmentService){
        this.sellerService = sellerService;
        this.departmentService = departmentService;
    }

    public void subscribeDataChangeListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }

    @FXML
    private void onButtonSaveAction(ActionEvent event){
        if(entity == null){
            throw new IllegalStateException("Entity was null");
        }
        if(sellerService == null){
            throw new IllegalStateException("Service was null");
        }
        try{
            entity = getFormData();
            sellerService.saveOrUpdate(entity);
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
        Constraints.setTextFieldMaxLength(textFieldName, 70);
        Constraints.setTextFieldDouble(textFieldBaseSalary);
        Constraints.setTextFieldMaxLength(textFieldEmail, 60);
        Utils.formatDatePicker(datePickerBirthDate, "dd/MM/yyyy");

        initializeComboBoxDepartment();
    }

    public void updateFormData(){
        if(entity == null){
            throw new IllegalStateException("Entity was null");
        }
        textFieldId.setText(String.valueOf(entity.getId()));
        textFieldName.setText(entity.getName());
        textFieldEmail.setText(entity.getEmail());
        textFieldBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));

        Locale.setDefault(Locale.US);

        if(entity.getBirthDate() != null) {
            datePickerBirthDate.setValue(
                LocalDate.ofInstant(entity.getBirthDate().toInstant(),ZoneId.systemDefault())
            );
        }

        if(entity.getDepartment() == null) {
            comboBoxDepartment.getSelectionModel().selectFirst();
        } else {
            comboBoxDepartment.setValue(entity.getDepartment());
        }
    }

    public void loadAssociatedObjects() {
        if(departmentService == null){
            throw new IllegalStateException("DepartmentService was null");
        }
        List<Department> list = departmentService.findAll();
        observableList = FXCollections.observableArrayList(list);
        comboBoxDepartment.setItems(observableList);
    }

    public void setErrorMessages(Map<String, String> errors){
        Set<String> fields = errors.keySet();

        if(fields.contains("name")){
            labelErrorName.setText(errors.get("name"));
        }
    }

    private void initializeComboBoxDepartment(){
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>(){
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        comboBoxDepartment.setCellFactory(factory);
        comboBoxDepartment.setButtonCell(factory.call(null));
    }
}
