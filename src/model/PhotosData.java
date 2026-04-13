package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Root application data container.
 *
 * @author Dhruv Patel
 */
public class PhotosData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, User> usersByUsername = new LinkedHashMap<>();
    private final Map<String, TagDefinition> tagDefinitionsByName = new LinkedHashMap<>();

    /**
     * Creates the root data object with preset tag types.
     */
    public PhotosData() {
        addPresetTagDefinition("person", true);
        addPresetTagDefinition("location", false);
    }

    /**
     * Returns all users in insertion order.
     *
     * @return users
     */
    public List<User> getUsers() {
        return new ArrayList<>(usersByUsername.values());
    }

    /**
     * Returns all tag definitions in insertion order.
     *
     * @return tag definitions
     */
    public List<TagDefinition> getTagDefinitions() {
        return new ArrayList<>(tagDefinitionsByName.values());
    }

    /**
     * Finds a user by username.
     *
     * @param username username
     * @return user or null
     */
    public User getUser(String username) {
        return usersByUsername.get(normalizeRequired(username, "username"));
    }

    /**
     * Returns whether a username already exists.
     *
     * @param username username
     * @return true if present
     */
    public boolean hasUser(String username) {
        return usersByUsername.containsKey(normalizeRequired(username, "username"));
    }

    /**
     * Creates a user.
     *
     * @param username username
     * @return created user
     */
    public User addUser(String username) {
        String normalizedUsername = normalizeRequired(username, "username");
        if (usersByUsername.containsKey(normalizedUsername)) {
            throw new IllegalArgumentException("User already exists.");
        }

        User user = new User(normalizedUsername);
        usersByUsername.put(normalizedUsername, user);
        return user;
    }

    /**
     * Removes a user.
     *
     * @param username username
     * @return true if removed
     */
    public boolean removeUser(String username) {
        return usersByUsername.remove(normalizeRequired(username, "username")) != null;
    }

    /**
     * Finds a tag definition by name.
     *
     * @param name tag type name
     * @return tag definition or null
     */
    public TagDefinition getTagDefinition(String name) {
        return tagDefinitionsByName.get(normalizeRequired(name, "name"));
    }

    /**
     * Returns whether a tag type already exists.
     *
     * @param name tag type name
     * @return true if present
     */
    public boolean hasTagDefinition(String name) {
        return tagDefinitionsByName.containsKey(normalizeRequired(name, "name"));
    }

    /**
     * Adds a user-defined tag type.
     *
     * @param name tag type name
     * @param multiValue true for multi-value tags
     * @return created tag definition
     */
    public TagDefinition addUserTagDefinition(String name, boolean multiValue) {
        String normalizedName = normalizeRequired(name, "name");
        if (tagDefinitionsByName.containsKey(normalizedName)) {
            throw new IllegalArgumentException("Tag type already exists.");
        }

        TagDefinition definition = new TagDefinition(normalizedName, multiValue, false);
        tagDefinitionsByName.put(normalizedName, definition);
        return definition;
    }

    /**
     * Removes a user-defined tag type.
     *
     * @param name tag type name
     * @return true if removed
     */
    public boolean removeUserTagDefinition(String name) {
        String normalizedName = normalizeRequired(name, "name");
        TagDefinition definition = tagDefinitionsByName.get(normalizedName);
        if (definition == null || definition.isPreset()) {
            return false;
        }
        return tagDefinitionsByName.remove(normalizedName) != null;
    }

    private void addPresetTagDefinition(String name, boolean multiValue) {
        TagDefinition definition = new TagDefinition(name, multiValue, true);
        tagDefinitionsByName.put(definition.getName(), definition);
    }

    private static String normalizeRequired(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null.");
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank.");
        }
        return trimmed;
    }
}
