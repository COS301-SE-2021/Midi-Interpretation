package com.noxception.midisense.display;

import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.dataclass.TestingDictionary;
import com.noxception.midisense.display.exceptions.InvalidTrackException;
import com.noxception.midisense.display.rrobjects.*;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
import com.noxception.midisense.interpreter.rrobjects.InterpretKeySignatureRequest;
import com.noxception.midisense.interpreter.rrobjects.InterpretKeySignatureResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
class DisplayServiceImplTest extends MIDISenseUnitTest {

    @Autowired
    private DisplayServiceImpl displayService;

    @BeforeEach
    public void setUp() {
        LogType[] monitorList = {LogType.DEBUG};
        this.monitor(monitorList);
    }

    @Test
    @DisplayName("Tests getting metadata with a valid file designator, should return a trio of key sig, time sig and tempo.")
    @Tag(TestTags.VALID_INPUT)
    public void testGetPieceMetadataValidFile() throws InvalidDesignatorException {
        GetPieceMetadataRequest req = new GetPieceMetadataRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        GetPieceMetadataResponse res = displayService.getPieceMetadata(req);
        logAllFields(res);
    }

    @Test
    @DisplayName("Tests getting track info with a valid file designator, should return a list of instrument lines.")
    @Tag(TestTags.VALID_INPUT)
    public void testGetTrackInfoValidFile() throws InvalidDesignatorException {
        GetTrackInfoRequest req = new GetTrackInfoRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator));
        GetTrackInfoResponse res = displayService.getTrackInfo(req);
        logAllFields(res);
    }

    @Test
    @DisplayName("Tests getting metadata with a valid file designator, should return a trio of key sig, time sig and tempo")
    @Tag(TestTags.VALID_INPUT)
    public void testGetTrackMetadataValidFile() throws InvalidDesignatorException, InvalidTrackException, IOException {
        GetTrackMetadataRequest req = new GetTrackMetadataRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator),TestingDictionary.display_all_valid_track_index);
        GetTrackMetadataResponse res = displayService.getTrackMetadata(req);
        FileWriter myWriter = new FileWriter("GetTrackMetadataSuccess.txt");
        myWriter.write(res.getTrackString());
        myWriter.close();
        logAllFields(res);
    }

    @Test
    @DisplayName("Tests getting metadata with a valid file designator, should return a trio of key sig, time sig and tempo")
    @Tag(TestTags.VALID_INPUT)
    public void testGetPieceOverviewValidFile() throws InvalidDesignatorException, InvalidTrackException, IOException {
        GetTrackOverviewRequest req = new GetTrackOverviewRequest(UUID.fromString(TestingDictionary.interpreter_all_validFileDesignator),TestingDictionary.display_all_valid_track_index);
        GetTrackOverviewResponse res = displayService.getTrackOverview(req);
        logAllFields(res);
    }

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
