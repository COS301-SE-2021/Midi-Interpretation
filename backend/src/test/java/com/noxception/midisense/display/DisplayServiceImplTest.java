package com.noxception.midisense.display;

import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.dataclass.TestingDictionary;
import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.display.DisplayServiceImpl;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.exceptions.InvalidKeySignatureException;
import com.noxception.midisense.interpreter.exceptions.InvalidUploadException;
import com.noxception.midisense.interpreter.rrobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.awt.image.TileObserver;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DisplayServiceImplTest extends MIDISenseUnitTest {

    private DisplayServiceImpl displayService;

    @BeforeEach
    public void setUp() {
        displayService = new DisplayServiceImpl();
    }

    //TODO: CLAUDIO DO ALL UNIT TESTS FOR THE 4 FUNCTIONS TOMORROW MORNING
    //below are sample tests I used in unit testing on another branch to make it easier for myself

    /* SAMPLE
    @Test
    @DisplayName("Tests uploading with a valid file byte array, should store in MIDIPool.")
    @Tag(TestTags.VALID_INPUT)
    public void testUploadFileValidFile() throws InvalidUploadException{
        byte[] validFileContents = TestingDictionary.interpreter_uploadFile_validFileContents;
        UploadFileRequest req = new UploadFileRequest(validFileContents);
        UploadFileResponse res = interpreterService.uploadFile(req);
        log(res.getFileDesignator());
    }


    @Test
    @DisplayName("Tests interpreting key signature with a valid file designator, should return a valid key signature object.")
    @Tag(TestTags.VALID_INPUT)
    public void testInterpretKeySignatureValidFile() throws InvalidDesignatorException, InvalidKeySignatureException {
        InterpretKeySignatureRequest req = new InterpretKeySignatureRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        InterpretKeySignatureResponse res = interpreterService.interpretKeySignature(req);
        log(res.getKeySignature());
    }

    @Test
    @DisplayName("Tests interpreting Key Signature with an invalid file designator, should throw an exception.")
    @Tag(TestTags.MALFORMED_INPUT)
    public void testInterpretKeySignatureInvalidFile(){
        InterpretKeySignatureRequest req = new InterpretKeySignatureRequest(UUID.fromString(TestingDictionary.interpreter_all_invalidFileDesignator));
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretKeySignature(req),
                "No processing should happen if a file doesn't exist.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.FILE_SYSTEM_EXCEPTION_TEXT));
    }

    @Test
    @DisplayName("Tests interpreting Key Siganture with an empty request, should throw an exception.")
    @Tag(TestTags.EMPTY_INPUT)
    public void testInterpretKeySignatureEmptyRequest(){
        InvalidDesignatorException thrown = assertThrows(InvalidDesignatorException.class,
                ()->interpreterService.interpretKeySignature(null),
                "A null request should not be processed.");
        assertTrue(thrown.getMessage().contains(MIDISenseConfig.EMPTY_REQUEST_EXCEPTION_TEXT));
    }
    */

    
}
