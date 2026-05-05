package com.example.androidphotos;

import com.example.androidphotos.model.Photo;

/**
 * One cross-album search result with enough data to reopen the photo.
 */
public class SearchResult {

    private final int albumIndex;
    private final int photoIndex;
    private final String albumName;
    private final Photo photo;

    public SearchResult(int albumIndex, int photoIndex, String albumName, Photo photo) {
        this.albumIndex = albumIndex;
        this.photoIndex = photoIndex;
        this.albumName = albumName;
        this.photo = photo;
    }

    public int getAlbumIndex() {
        return albumIndex;
    }

    public int getPhotoIndex() {
        return photoIndex;
    }

    public String getAlbumName() {
        return albumName;
    }

    public Photo getPhoto() {
        return photo;
    }
}
