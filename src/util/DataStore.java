package util;

import model.PhotosData;
import model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Loads and saves the application's serialized data.
 *
 * @author Dhruv Patel
 */
public final class DataStore {

    /**
     * Root data directory.
     */
    public static final Path DATA_DIRECTORY = Path.of("data");

    /**
     * Serialized application data file.
     */
    public static final Path DATA_FILE = DATA_DIRECTORY.resolve("photos-data.ser");

    /**
     * Stock photo directory.
     */
    public static final Path STOCK_DIRECTORY = DATA_DIRECTORY.resolve("stock");

    private DataStore() {
    }

    /**
     * Loads the application data. If no valid saved state exists, a default state
     * with admin, stock, and the stock album is created.
     *
     * @return loaded or bootstrapped application data
     */
    public static PhotosData load() {
        ensureDataDirectories();

        if (Files.exists(DATA_FILE)) {
            PhotosData savedData = readSavedData();
            if (savedData != null) {
                return savedData;
            }
        }

        PhotosData defaultData = createDefaultData();
        save(defaultData);
        return defaultData;
    }

    /**
     * Saves the application data to disk.
     *
     * @param data application data
     */
    public static void save(PhotosData data) {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null.");
        }

        ensureDataDirectories();

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(Files.newOutputStream(DATA_FILE))) {
            outputStream.writeObject(data);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save application data.", exception);
        }
    }

    /**
     * Returns the serialized data file path.
     *
     * @return data file path
     */
    public static Path getDataFilePath() {
        return DATA_FILE.toAbsolutePath().normalize();
    }

    /**
     * Returns the stock photo directory path.
     *
     * @return stock directory path
     */
    public static Path getStockDirectoryPath() {
        return STOCK_DIRECTORY.toAbsolutePath().normalize();
    }

    private static PhotosData readSavedData() {
        try (ObjectInputStream inputStream =
                     new ObjectInputStream(Files.newInputStream(DATA_FILE))) {
            Object object = inputStream.readObject();
            if (object instanceof PhotosData photosData) {
                return photosData;
            }
        } catch (IOException | ClassNotFoundException | RuntimeException exception) {
            return null;
        }
        return null;
    }

    private static PhotosData createDefaultData() {
        PhotosData data = new PhotosData();
        data.addUser("admin");

        User stockUser = data.addUser("stock");
        stockUser.createAlbum("stock");
        loadStockPhotos(stockUser);
        return data;
    }

    private static void loadStockPhotos(User stockUser) {
        try (Stream<Path> pathStream = Files.list(STOCK_DIRECTORY)) {
            pathStream.filter(Files::isRegularFile)
                    .filter(DataStore::isSupportedImageFile)
                    .sorted()
                    .forEach(path -> stockUser.addPhotoToAlbum("stock", path.toAbsolutePath().toString()));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load stock photos.", exception);
        }
    }

    private static boolean isSupportedImageFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg")
                || fileName.endsWith(".png")
                || fileName.endsWith(".gif")
                || fileName.endsWith(".bmp");
    }

    private static void ensureDataDirectories() {
        try {
            Files.createDirectories(DATA_DIRECTORY);
            Files.createDirectories(STOCK_DIRECTORY);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to prepare data directories.", exception);
        }
    }
}
