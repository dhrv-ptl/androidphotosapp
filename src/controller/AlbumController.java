package controller;

import app.Photos;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.Album;
import model.Photo;
import model.PhotosData;
import model.Tag;
import model.TagDefinition;
import model.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Controller for the album view.
 *
 * @author Dhruv Patel
 */
public class AlbumController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final String NEW_TAG_TYPE_OPTION = "Create New Tag Type...";

    @FXML
    private Label albumLabel;

    @FXML
    private ListView<Photo> photoListView;

    @FXML
    private ImageView previewImageView;

    @FXML
    private Label captionLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label pathLabel;

    @FXML
    private ListView<Tag> tagListView;

    private Photos app;
    private PhotosData data;
    private User user;
    private Album album;

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
        updateMessage();
    }

    /**
     * Stores the current user.
     *
     * @param user current user
     */
    public void setUser(User user) {
        this.user = user;
        updateMessage();
    }

    /**
     * Stores the current album.
     *
     * @param album current album
     */
    public void setAlbum(Album album) {
        this.album = album;
        updateMessage();
        refreshPhotoList(null);
    }

    /**
     * Initializes the view.
     */
    @FXML
    private void initialize() {
        photoListView.setCellFactory(listView -> new ListCell<>() {
            private final ImageView thumbnailView = new ImageView();
            private final Label nameLabel = new Label();
            private final Label cellCaptionLabel = new Label();
            private final VBox textBox = new VBox(4);
            private final HBox content = new HBox(10);

            {
                thumbnailView.setFitWidth(60);
                thumbnailView.setFitHeight(60);
                thumbnailView.setPreserveRatio(true);
                textBox.getChildren().addAll(nameLabel, cellCaptionLabel);
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
                String caption = photo.getCaption().isBlank() ? "(No caption)" : photo.getCaption();
                cellCaptionLabel.setText(caption);
                thumbnailView.setImage(loadImage(photo, 60, 60));
                setText(null);
                setGraphic(content);
            }
        });

        tagListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Tag tag, boolean empty) {
                super.updateItem(tag, empty);
                if (empty || tag == null) {
                    setText(null);
                    return;
                }
                setText(tag.getTypeName() + ": " + tag.getValue());
            }
        });

        photoListView.getSelectionModel().selectedItemProperty().addListener((observable, oldPhoto, newPhoto) ->
                updateSelectedPhotoDetails(newPhoto));
        updateMessage();
        updateSelectedPhotoDetails(null);
    }

    /**
     * Adds a photo to the album from the local computer.
     */
    @FXML
    private void handleAddPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Photo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Image Files", "*.bmp", "*.gif", "*.jpg", "*.jpeg", "*.png"));

        File selectedFile = fileChooser.showOpenDialog(photoListView.getScene().getWindow());
        if (selectedFile == null) {
            return;
        }

        String absolutePath = selectedFile.toPath().toAbsolutePath().normalize().toString();
        if (!isSupportedImageFile(absolutePath)) {
            showError("Please choose a bmp, gif, jpg, jpeg, or png image.");
            return;
        }
        if (album.containsPhoto(absolutePath)) {
            showError("That photo is already in this album.");
            return;
        }

        try {
            user.addPhotoToAlbum(album.getName(), absolutePath);
            refreshPhotoList(absolutePath);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Removes the selected photo from this album.
     */
    @FXML
    private void handleRemovePhoto() {
        Photo selectedPhoto = getSelectedPhoto();
        if (selectedPhoto == null) {
            showError("Please select a photo to remove.");
            return;
        }

        boolean removed = user.removePhotoFromAlbum(album.getName(), selectedPhoto.getAbsoluteFilePath());
        if (!removed) {
            showError("Unable to remove the selected photo.");
            return;
        }

        refreshPhotoList(null);
    }

    /**
     * Updates the caption for the selected photo.
     */
    @FXML
    private void handleRecaptionPhoto() {
        Photo selectedPhoto = getSelectedPhoto();
        if (selectedPhoto == null) {
            showError("Please select a photo to recaption.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selectedPhoto.getCaption());
        dialog.setTitle("Recaption Photo");
        dialog.setHeaderText(null);
        dialog.setContentText("Caption:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        selectedPhoto.setCaption(result.get());
        refreshPhotoList(selectedPhoto.getAbsoluteFilePath());
    }

    /**
     * Adds a tag to the selected photo.
     */
    @FXML
    private void handleAddTag() {
        Photo selectedPhoto = getSelectedPhoto();
        if (selectedPhoto == null) {
            showError("Please select a photo first.");
            return;
        }

        TagInput tagInput = showAddTagDialog();
        if (tagInput == null) {
            return;
        }

        try {
            TagDefinition definition = resolveTagDefinition(tagInput);
            if (!definition.isMultiValue() && selectedPhoto.hasTagType(definition.getName())) {
                if (!confirmSingleValueReplacement(definition.getName())) {
                    return;
                }
                selectedPhoto.removeTagsByType(definition.getName());
            }

            boolean added = selectedPhoto.addTag(definition, tagInput.value());
            if (!added) {
                showError("That tag already exists on this photo.");
                return;
            }

            refreshPhotoList(selectedPhoto.getAbsoluteFilePath());
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Deletes the selected tag from the selected photo.
     */
    @FXML
    private void handleDeleteTag() {
        Photo selectedPhoto = getSelectedPhoto();
        if (selectedPhoto == null) {
            showError("Please select a photo first.");
            return;
        }

        Tag selectedTag = tagListView.getSelectionModel().getSelectedItem();
        if (selectedTag == null) {
            showError("Please select a tag to delete.");
            return;
        }

        boolean removed = selectedPhoto.removeTag(selectedTag.getDefinition(), selectedTag.getValue());
        if (!removed) {
            showError("Unable to delete the selected tag.");
            return;
        }

        refreshPhotoList(selectedPhoto.getAbsoluteFilePath());
    }

    /**
     * Shows the previous photo in the album.
     */
    @FXML
    private void handlePreviousPhoto() {
        selectAdjacentPhoto(-1);
    }

    /**
     * Shows the next photo in the album.
     */
    @FXML
    private void handleNextPhoto() {
        selectAdjacentPhoto(1);
    }

    /**
     * Copies the selected photo to another album.
     */
    @FXML
    private void handleCopyPhoto() {
        Photo selectedPhoto = getSelectedPhoto();
        if (selectedPhoto == null) {
            showError("Please select a photo to copy.");
            return;
        }

        Album destinationAlbum = chooseDestinationAlbum();
        if (destinationAlbum == null) {
            return;
        }

        String absolutePath = selectedPhoto.getAbsoluteFilePath();
        if (destinationAlbum.containsPhoto(absolutePath)) {
            showError("That photo is already in the destination album.");
            return;
        }

        try {
            user.addPhotoToAlbum(destinationAlbum.getName(), absolutePath);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Moves the selected photo to another album.
     */
    @FXML
    private void handleMovePhoto() {
        Photo selectedPhoto = getSelectedPhoto();
        if (selectedPhoto == null) {
            showError("Please select a photo to move.");
            return;
        }

        Album destinationAlbum = chooseDestinationAlbum();
        if (destinationAlbum == null) {
            return;
        }

        String absolutePath = selectedPhoto.getAbsoluteFilePath();
        if (destinationAlbum.containsPhoto(absolutePath)) {
            showError("That photo is already in the destination album.");
            return;
        }

        try {
            user.addPhotoToAlbum(destinationAlbum.getName(), absolutePath);
            user.removePhotoFromAlbum(album.getName(), absolutePath);
            refreshPhotoList(null);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Returns to the user's album list.
     */
    @FXML
    private void handleBack() {
        try {
            app.showUserAlbumsView(user);
        } catch (IOException exception) {
            showError("Unable to return to the album list.");
        }
    }

    private void updateMessage() {
        if (albumLabel != null && user != null && album != null) {
            albumLabel.setText("Album: " + album.getName() + " (" + user.getUsername() + ")");
        }
    }

    private void refreshPhotoList(String photoPathToSelect) {
        if (photoListView == null || album == null) {
            return;
        }

        photoListView.setItems(FXCollections.observableArrayList(album.getPhotos()));
        if (photoPathToSelect != null) {
            Photo photo = album.getPhotoByPath(photoPathToSelect);
            if (photo != null) {
                photoListView.getSelectionModel().select(photo);
                return;
            }
        }

        if (!photoListView.getItems().isEmpty()) {
            photoListView.getSelectionModel().selectFirst();
        } else {
            updateSelectedPhotoDetails(null);
        }
    }

    private void updateSelectedPhotoDetails(Photo photo) {
        if (previewImageView == null || captionLabel == null || dateLabel == null
                || pathLabel == null || tagListView == null) {
            return;
        }

        if (photo == null) {
            previewImageView.setImage(null);
            captionLabel.setText("Caption: ");
            dateLabel.setText("Date: ");
            pathLabel.setText("Path: ");
            tagListView.setItems(FXCollections.observableArrayList());
            return;
        }

        previewImageView.setImage(loadImage(photo, 420, 320));
        String caption = photo.getCaption().isBlank() ? "(No caption)" : photo.getCaption();
        captionLabel.setText("Caption: " + caption);
        dateLabel.setText("Date: " + formatDate(photo.getPhotoDate()));
        pathLabel.setText("Path: " + photo.getAbsoluteFilePath());
        tagListView.setItems(FXCollections.observableArrayList(
                photo.getTags().stream()
                        .sorted(Comparator.comparing(Tag::getTypeName).thenComparing(Tag::getValue))
                        .toList()
        ));
    }

    private Photo getSelectedPhoto() {
        return photoListView.getSelectionModel().getSelectedItem();
    }

    private void selectAdjacentPhoto(int offset) {
        if (photoListView.getItems().isEmpty()) {
            showError("There are no photos in this album.");
            return;
        }

        int currentIndex = photoListView.getSelectionModel().getSelectedIndex();
        if (currentIndex < 0) {
            photoListView.getSelectionModel().selectFirst();
            return;
        }

        int nextIndex = currentIndex + offset;
        if (nextIndex < 0 || nextIndex >= photoListView.getItems().size()) {
            showError("No more photos in that direction.");
            return;
        }

        photoListView.getSelectionModel().select(nextIndex);
        photoListView.scrollTo(nextIndex);
    }

    private Album chooseDestinationAlbum() {
        if (user == null || album == null) {
            return null;
        }

        List<Album> destinationAlbums = new ArrayList<>();
        for (Album userAlbum : user.getAlbums()) {
            if (!userAlbum.getName().equals(album.getName())) {
                destinationAlbums.add(userAlbum);
            }
        }

        if (destinationAlbums.isEmpty()) {
            showError("Create another album first.");
            return null;
        }

        ChoiceDialog<Album> dialog = new ChoiceDialog<>(destinationAlbums.getFirst(), destinationAlbums);
        dialog.setTitle("Choose Album");
        dialog.setHeaderText(null);
        dialog.setContentText("Destination album:");

        Optional<Album> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private Image loadImage(Photo photo, double width, double height) {
        Image image = new Image(Path.of(photo.getAbsoluteFilePath()).toUri().toString(),
                width, height, true, true, false);
        if (image.isError()) {
            return null;
        }
        return image;
    }

    private boolean isSupportedImageFile(String absolutePath) {
        String lowerCasePath = absolutePath.toLowerCase(Locale.ROOT);
        return lowerCasePath.endsWith(".bmp")
                || lowerCasePath.endsWith(".gif")
                || lowerCasePath.endsWith(".jpg")
                || lowerCasePath.endsWith(".jpeg")
                || lowerCasePath.endsWith(".png");
    }

    private TagInput showAddTagDialog() {
        Dialog<TagInput> dialog = new Dialog<>();
        dialog.setTitle("Add Tag");
        dialog.setHeaderText(null);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll(
                data.getTagDefinitions().stream()
                        .map(TagDefinition::getName)
                        .sorted()
                        .toList()
        );
        typeComboBox.getItems().add(NEW_TAG_TYPE_OPTION);
        if (!typeComboBox.getItems().isEmpty()) {
            typeComboBox.getSelectionModel().selectFirst();
        }

        Label typeNameLabel = new Label("New type name:");
        TextField typeNameField = new TextField();
        CheckBox multiValueCheckBox = new CheckBox("Multi-value tag type");
        Label valueLabel = new Label("Value:");
        TextField valueField = new TextField();

        typeNameLabel.setVisible(false);
        typeNameLabel.setManaged(false);
        typeNameField.setVisible(false);
        typeNameField.setManaged(false);
        multiValueCheckBox.setVisible(false);
        multiValueCheckBox.setManaged(false);

        typeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean createNew = NEW_TAG_TYPE_OPTION.equals(newValue);
            typeNameLabel.setVisible(createNew);
            typeNameLabel.setManaged(createNew);
            typeNameField.setVisible(createNew);
            typeNameField.setManaged(createNew);
            multiValueCheckBox.setVisible(createNew);
            multiValueCheckBox.setManaged(createNew);
        });

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(new Label("Tag type:"), 0, 0);
        gridPane.add(typeComboBox, 1, 0);
        gridPane.add(typeNameLabel, 0, 1);
        gridPane.add(typeNameField, 1, 1);
        gridPane.add(multiValueCheckBox, 1, 2);
        gridPane.add(valueLabel, 0, 3);
        gridPane.add(valueField, 1, 3);

        dialog.getDialogPane().setContent(gridPane);
        dialog.setResultConverter(buttonType -> {
            if (buttonType != addButtonType) {
                return null;
            }

            String selectedType = typeComboBox.getValue();
            if (selectedType == null || selectedType.isBlank()) {
                throw new IllegalArgumentException("Please choose a tag type.");
            }

            String value = valueField.getText() == null ? "" : valueField.getText().trim();
            if (value.isEmpty()) {
                throw new IllegalArgumentException("Please enter a tag value.");
            }

            if (NEW_TAG_TYPE_OPTION.equals(selectedType)) {
                String newTypeName = typeNameField.getText() == null ? "" : typeNameField.getText().trim();
                if (newTypeName.isEmpty()) {
                    throw new IllegalArgumentException("Please enter a new tag type name.");
                }
                return new TagInput(newTypeName, value, true, multiValueCheckBox.isSelected());
            }

            return new TagInput(selectedType, value, false, false);
        });

        try {
            Optional<TagInput> result = dialog.showAndWait();
            return result.orElse(null);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
            return null;
        }
    }

    private TagDefinition resolveTagDefinition(TagInput tagInput) {
        if (tagInput.newType()) {
            TagDefinition existingDefinition = data.getTagDefinition(tagInput.typeName());
            if (existingDefinition != null) {
                return existingDefinition;
            }
            return data.addUserTagDefinition(tagInput.typeName(), tagInput.multiValue());
        }

        TagDefinition definition = data.getTagDefinition(tagInput.typeName());
        if (definition == null) {
            throw new IllegalArgumentException("The selected tag type does not exist.");
        }
        return definition;
    }

    private String formatDate(LocalDate date) {
        return date == null ? "N/A" : DATE_FORMATTER.format(date);
    }

    private boolean confirmSingleValueReplacement(String tagTypeName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Replace Tag");
        alert.setHeaderText(null);
        alert.setContentText("The tag type \"" + tagTypeName + "\" allows only one value. Replace the current value?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Album Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private record TagInput(String typeName, String value, boolean newType, boolean multiValue) {
    }
}
