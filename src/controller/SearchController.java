package controller;

import app.Photos;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Photo;
import model.PhotosData;
import model.Tag;
import model.User;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/**
 * Controller for the search view.
 *
 * @author Dhruv Patel
 */
public class SearchController {

    @FXML
    private Label messageLabel;

    @FXML
    private Label resultsLabel;

    @FXML
    private RadioButton dateModeRadio;

    @FXML
    private RadioButton tagModeRadio;

    @FXML
    private VBox dateSearchBox;

    @FXML
    private VBox tagSearchBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> tagQueryModeComboBox;

    @FXML
    private ComboBox<String> tagTypeOneComboBox;

    @FXML
    private TextField tagValueOneField;

    @FXML
    private VBox secondTagBox;

    @FXML
    private ComboBox<String> tagTypeTwoComboBox;

    @FXML
    private TextField tagValueTwoField;

    @FXML
    private ListView<Photo> resultsListView;

    private final ToggleGroup searchModeGroup = new ToggleGroup();
    private final List<Photo> searchResults = new ArrayList<>();

    private Photos app;
    private PhotosData data;
    private User user;

    /**
     * Stores the application reference.
     *
     * @param app application
     */
    public void setApp(Photos app) {
        this.app = app;
    }

    /**
     * Stores the application data.
     *
     * @param data application data
     */
    public void setData(PhotosData data) {
        this.data = data;
        populateTagTypeChoices();
    }

    /**
     * Stores the current user.
     *
     * @param user current user
     */
    public void setUser(User user) {
        this.user = user;
        if (messageLabel != null) {
            messageLabel.setText("Search photos for: " + user.getUsername());
        }
    }

    /**
     * Initializes the view.
     */
    @FXML
    private void initialize() {
        dateModeRadio.setToggleGroup(searchModeGroup);
        tagModeRadio.setToggleGroup(searchModeGroup);
        dateModeRadio.setSelected(true);

        tagQueryModeComboBox.setItems(FXCollections.observableArrayList("Single Tag", "AND", "OR"));
        tagQueryModeComboBox.getSelectionModel().selectFirst();
        tagQueryModeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateSearchModeVisibility());
        searchModeGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> updateSearchModeVisibility());

        resultsListView.setCellFactory(listView -> new ListCell<>() {
            private final ImageView thumbnailView = new ImageView();
            private final Label nameLabel = new Label();
            private final Label captionLabel = new Label();
            private final VBox textBox = new VBox(4);
            private final HBox content = new HBox(10);

            {
                thumbnailView.setFitWidth(60);
                thumbnailView.setFitHeight(60);
                thumbnailView.setPreserveRatio(true);
                textBox.getChildren().addAll(nameLabel, captionLabel);
                content.getChildren().addAll(thumbnailView, textBox);
            }

            @Override
            protected void updateItem(Photo photo, boolean empty) {
                super.updateItem(photo, empty);
                if (empty || photo == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                nameLabel.setText(Path.of(photo.getAbsoluteFilePath()).getFileName().toString());
                captionLabel.setText(photo.getCaption().isBlank() ? "(No caption)" : photo.getCaption());
                thumbnailView.setImage(loadImage(photo));
                setText(null);
                setGraphic(content);
            }
        });

        updateSearchModeVisibility();
        updateResults();
    }

    /**
     * Runs the selected search.
     */
    @FXML
    private void handleSearch() {
        if (user == null) {
            return;
        }

        try {
            searchResults.clear();
            if (dateModeRadio.isSelected()) {
                runDateSearch();
            } else {
                runTagSearch();
            }
            searchResults.sort(Comparator.comparing(Photo::getAbsoluteFilePath));
            updateResults();
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Creates an album from the current search results.
     */
    @FXML
    private void handleCreateAlbumFromResults() {
        if (searchResults.isEmpty()) {
            showError("There are no search results to save.");
            return;
        }

        String albumName = showCreateAlbumDialog();
        if (albumName == null) {
            return;
        }
        if (albumName.isBlank()) {
            showError("Please enter an album name.");
            return;
        }
        if (user.hasAlbum(albumName)) {
            showError("Album already exists.");
            return;
        }

        try {
            user.createAlbum(albumName);
            for (Photo photo : searchResults) {
                user.addPhotoToAlbum(albumName, photo.getAbsoluteFilePath());
            }
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Returns to the user albums screen.
     */
    @FXML
    private void handleBack() {
        try {
            app.showUserAlbumsView(user);
        } catch (IOException exception) {
            showError("Unable to return to the album list.");
        }
    }

    private void runDateSearch() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Please choose both a start date and an end date.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("The end date must be on or after the start date.");
        }

        for (Photo photo : user.getPhotos()) {
            LocalDate photoDate = photo.getPhotoDate();
            if (!photoDate.isBefore(startDate) && !photoDate.isAfter(endDate)) {
                searchResults.add(photo);
            }
        }
    }

    private void runTagSearch() {
        String firstType = selectedTagType(tagTypeOneComboBox);
        String firstValue = requiredText(tagValueOneField, "first tag value");
        String mode = tagQueryModeComboBox.getValue();

        String secondType = null;
        String secondValue = null;
        if (!"Single Tag".equals(mode)) {
            secondType = selectedTagType(tagTypeTwoComboBox);
            secondValue = requiredText(tagValueTwoField, "second tag value");
        }

        for (Photo photo : user.getPhotos()) {
            boolean firstMatch = hasTag(photo, firstType, firstValue);
            boolean matches;
            if ("Single Tag".equals(mode)) {
                matches = firstMatch;
            } else {
                boolean secondMatch = hasTag(photo, secondType, secondValue);
                matches = "AND".equals(mode) ? firstMatch && secondMatch : firstMatch || secondMatch;
            }

            if (matches) {
                searchResults.add(photo);
            }
        }
    }

    private boolean hasTag(Photo photo, String typeName, String value) {
        for (Tag tag : photo.getTags()) {
            if (tag.getTypeName().equals(typeName) && tag.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private void updateSearchModeVisibility() {
        boolean dateMode = dateModeRadio.isSelected();
        dateSearchBox.setManaged(dateMode);
        dateSearchBox.setVisible(dateMode);
        tagSearchBox.setManaged(!dateMode);
        tagSearchBox.setVisible(!dateMode);

        boolean showSecondTag = !"Single Tag".equals(tagQueryModeComboBox.getValue());
        secondTagBox.setManaged(showSecondTag);
        secondTagBox.setVisible(showSecondTag);
    }

    private void populateTagTypeChoices() {
        if (data == null || tagTypeOneComboBox == null || tagTypeTwoComboBox == null) {
            return;
        }

        List<String> tagTypes = data.getTagDefinitions().stream()
                .map(definition -> definition.getName())
                .sorted()
                .toList();

        tagTypeOneComboBox.setItems(FXCollections.observableArrayList(tagTypes));
        tagTypeTwoComboBox.setItems(FXCollections.observableArrayList(tagTypes));
        if (!tagTypes.isEmpty()) {
            tagTypeOneComboBox.getSelectionModel().selectFirst();
            tagTypeTwoComboBox.getSelectionModel().selectFirst();
        }
    }

    private void updateResults() {
        if (resultsListView != null) {
            resultsListView.setItems(FXCollections.observableArrayList(searchResults));
        }
        if (resultsLabel != null) {
            resultsLabel.setText("Results: " + searchResults.size());
        }
    }

    private String showCreateAlbumDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(Photos.class.getResource("/view/create-album-from-search-dialog.fxml"));
            Scene scene = new Scene(loader.load());

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create Album From Results");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(resultsListView.getScene().getWindow());
            dialogStage.setScene(scene);

            CreateAlbumFromSearchDialogController controller = loader.getController();
            controller.setStage(dialogStage);

            dialogStage.showAndWait();
            return controller.getAlbumName();
        } catch (IOException exception) {
            showError("Unable to open the create album dialog.");
            return null;
        }
    }

    private String selectedTagType(ComboBox<String> comboBox) {
        String value = comboBox.getValue();
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Please choose a tag type.");
        }
        return value;
    }

    private String requiredText(TextField field, String name) {
        String value = field.getText() == null ? "" : field.getText().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Please enter the " + name + ".");
        }
        return value;
    }

    private Image loadImage(Photo photo) {
        Image image = new Image(Path.of(photo.getAbsoluteFilePath()).toUri().toString(), 60, 60, true, true, false);
        return image.isError() ? null : image;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Search Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
