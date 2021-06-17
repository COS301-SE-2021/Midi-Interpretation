package com.noxception.midisense.display;

import com.noxception.midisense.api.DisplayApi;
import com.noxception.midisense.models.DisplayMusicMetadataRequest;
import com.noxception.midisense.models.DisplayMusicMetadataResponse;
import com.noxception.midisense.models.DisplayNoteMetadataRequest;
import com.noxception.midisense.models.DisplayNoteMetadataResponse;
import org.springframework.http.ResponseEntity;

public class DisplayController implements DisplayApi {
    @Override
    public ResponseEntity<DisplayMusicMetadataResponse> displayMusicMetadata(DisplayMusicMetadataRequest body) {
        return null;
    }

    @Override
    public ResponseEntity<DisplayNoteMetadataResponse> displayNoteMetadata(DisplayNoteMetadataRequest body) {
        return null;
    }
}
