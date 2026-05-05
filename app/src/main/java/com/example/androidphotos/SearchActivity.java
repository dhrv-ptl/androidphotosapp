package com.example.androidphotos;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.androidphotos.model.Album;
import com.example.androidphotos.model.AppData;
import com.example.androidphotos.model.Photo;
import com.example.androidphotos.model.Tag;
import com.example.androidphotos.storage.DataStore;

/**
 * Searches photos across all albums by one or two tag-value criteria.
 */
public class SearchActivity extends AppCompatActivity {

    private static final String MODE_SINGLE = "Single";
    private static final String MODE_AND = "AND";
    private static final String MODE_OR = "OR";

    private AppData appData;
    private Spinner firstTypeSpinner;
    private Spinner secondTypeSpinner;
    private Spinner modeSpinner;
    private AutoCompleteTextView firstValueInput;
    private AutoCompleteTextView secondValueInput;
    private View secondCriteriaContainer;

    private ArrayAdapter<String> firstSuggestionsAdapter;
    private ArrayAdapter<String> secondSuggestionsAdapter;
    private SearchResultAdapter resultAdapter;
    private final List<SearchResult> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        appData = DataStore.load(this);

        firstTypeSpinner = findViewById(R.id.spinner_first_tag_type);
        secondTypeSpinner = findViewById(R.id.spinner_second_tag_type);
        modeSpinner = findViewById(R.id.spinner_search_mode);
        firstValueInput = findViewById(R.id.input_first_tag_value);
        secondValueInput = findViewById(R.id.input_second_tag_value);
        secondCriteriaContainer = findViewById(R.id.layout_second_criteria);
        Button searchButton = findViewById(R.id.button_run_search);
        ListView resultsListView = findViewById(R.id.list_search_results);
        TextView emptyResultsView = findViewById(R.id.text_empty_results);

        setupTypeSpinners();
        setupModeSpinner();
        setupAutocomplete();

        resultAdapter = new SearchResultAdapter(this, results);
        resultsListView.setAdapter(resultAdapter);
        resultsListView.setEmptyView(emptyResultsView);
        resultsListView.setOnItemClickListener((parent, view, position, id) -> openResult(position));

        searchButton.setOnClickListener(view -> runSearch());
        updateSecondCriteriaVisibility();
        refreshAutocompleteSuggestions(firstTypeSpinner, firstValueInput, firstSuggestionsAdapter);
        refreshAutocompleteSuggestions(secondTypeSpinner, secondValueInput, secondSuggestionsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appData = DataStore.load(this);
        refreshAutocompleteSuggestions(firstTypeSpinner, firstValueInput, firstSuggestionsAdapter);
        refreshAutocompleteSuggestions(secondTypeSpinner, secondValueInput, secondSuggestionsAdapter);
    }

    private void setupTypeSpinners() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{Tag.TYPE_PERSON, Tag.TYPE_LOCATION}
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstTypeSpinner.setAdapter(typeAdapter);
        secondTypeSpinner.setAdapter(typeAdapter);

        firstTypeSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onSelectionChanged() {
                refreshAutocompleteSuggestions(firstTypeSpinner, firstValueInput, firstSuggestionsAdapter);
            }
        });
        secondTypeSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onSelectionChanged() {
                refreshAutocompleteSuggestions(secondTypeSpinner, secondValueInput, secondSuggestionsAdapter);
            }
        });
    }

    private void setupModeSpinner() {
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{MODE_SINGLE, MODE_AND, MODE_OR}
        );
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);
        modeSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onSelectionChanged() {
                updateSecondCriteriaVisibility();
            }
        });
    }

    private void setupAutocomplete() {
        firstSuggestionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        secondSuggestionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());

        firstValueInput.setAdapter(firstSuggestionsAdapter);
        firstValueInput.setThreshold(1);
        secondValueInput.setAdapter(secondSuggestionsAdapter);
        secondValueInput.setThreshold(1);

        firstValueInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                refreshAutocompleteSuggestions(firstTypeSpinner, firstValueInput, firstSuggestionsAdapter);
            }
        });
        secondValueInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                refreshAutocompleteSuggestions(secondTypeSpinner, secondValueInput, secondSuggestionsAdapter);
            }
        });
    }

    private void refreshAutocompleteSuggestions(Spinner typeSpinner,
                                                AutoCompleteTextView inputView,
                                                ArrayAdapter<String> adapter) {
        String type = String.valueOf(typeSpinner.getSelectedItem());
        String prefix = inputView.getText() == null ? "" : inputView.getText().toString();
        List<String> suggestions = appData.getAutocompleteValues(type, prefix);
        adapter.clear();
        adapter.addAll(suggestions);
        adapter.notifyDataSetChanged();
    }

    private void updateSecondCriteriaVisibility() {
        boolean singleMode = MODE_SINGLE.equals(String.valueOf(modeSpinner.getSelectedItem()));
        secondCriteriaContainer.setVisibility(singleMode ? View.GONE : View.VISIBLE);
    }

    private void runSearch() {
        String firstType = String.valueOf(firstTypeSpinner.getSelectedItem());
        String firstValue = safeTrim(firstValueInput.getText() == null ? null : firstValueInput.getText().toString());
        String secondType = String.valueOf(secondTypeSpinner.getSelectedItem());
        String secondValue = safeTrim(secondValueInput.getText() == null ? null : secondValueInput.getText().toString());
        String mode = String.valueOf(modeSpinner.getSelectedItem());

        if (firstValue.isEmpty()) {
            firstValueInput.setError(getString(R.string.error_search_first_blank));
            return;
        }

        if (!MODE_SINGLE.equals(mode) && secondValue.isEmpty()) {
            secondValueInput.setError(getString(R.string.error_search_second_blank));
            return;
        }

        results.clear();
        List<Album> albums = appData.getAlbums();
        for (int albumIndex = 0; albumIndex < albums.size(); albumIndex++) {
            Album album = albums.get(albumIndex);
            List<Photo> photos = album.getPhotos();
            for (int photoIndex = 0; photoIndex < photos.size(); photoIndex++) {
                Photo photo = photos.get(photoIndex);
                boolean firstMatches = matchesByPrefix(photo, firstType, firstValue);
                boolean include;
                if (MODE_SINGLE.equals(mode)) {
                    include = firstMatches;
                } else {
                    boolean secondMatches = matchesByPrefix(photo, secondType, secondValue);
                    include = MODE_AND.equals(mode)
                            ? (firstMatches && secondMatches)
                            : (firstMatches || secondMatches);
                }

                if (include) {
                    results.add(new SearchResult(albumIndex, photoIndex, album.getName(), photo));
                }
            }
        }

        resultAdapter.notifyDataSetChanged();
    }

    private boolean matchesByPrefix(Photo photo, String type, String valuePrefix) {
        String prefix = valuePrefix.toLowerCase(Locale.ROOT);
        for (com.example.androidphotos.model.Tag tag : photo.getTags()) {
            if (tag.getType().equals(type)
                    && tag.getValue().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private void openResult(int position) {
        SearchResult result = results.get(position);
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra(PhotoActivity.EXTRA_ALBUM_INDEX, result.getAlbumIndex());
        intent.putExtra(PhotoActivity.EXTRA_PHOTO_INDEX, result.getPhotoIndex());
        startActivity(intent);
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
