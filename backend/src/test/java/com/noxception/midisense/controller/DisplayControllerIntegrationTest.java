package com.noxception.midisense.controller;

import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.dataclass.TestingDictionary;
import com.noxception.midisense.models.DisplayGetPieceMetadataRequest;
import com.noxception.midisense.models.DisplayGetTrackInfoRequest;
import com.noxception.midisense.models.DisplayGetTrackMetadataRequest;
import com.noxception.midisense.models.DisplayGetTrackOverviewRequest;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DisplayControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Tests getting piece metadata with a valid file designator")
    public void testGetPieceMetadataValidDesignator() throws Exception {
        DisplayGetPieceMetadataRequest request = new DisplayGetPieceMetadataRequest();
        request.setFileDesignator(TestingDictionary.display_all_validFileDesignator);
        MvcResult response = TestingDictionary.mockRequest("display","getPieceMetadata",request, mvc);
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting track info with a valid file designator")
    public void testGetTrackInfoValidDesignator() throws Exception {
        DisplayGetTrackInfoRequest request = new DisplayGetTrackInfoRequest();
        request.setFileDesignator(TestingDictionary.display_all_validFileDesignator);
        MvcResult response = TestingDictionary.mockRequest("display","getTrackInfo",request, mvc);
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting track metadata with a valid file designator")
    public void testGetTrackMetadataValidDesignator() throws Exception {
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();
        request.setFileDesignator(TestingDictionary.display_all_validFileDesignator);
        request.setTrackIndex((int) TestingDictionary.display_all_valid_track_index);
        MvcResult response = TestingDictionary.mockRequest("display","getTrackMetadata",request, mvc);
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting track overview with a valid file designator")
    public void testGetTrackOverviewValidDesignator() throws Exception {
        DisplayGetTrackOverviewRequest request = new DisplayGetTrackOverviewRequest();
        request.setFileDesignator(TestingDictionary.display_all_validFileDesignator);
        request.setTrackIndex((int) TestingDictionary.display_all_valid_track_index);
        MvcResult response = TestingDictionary.mockRequest("display","getTrackOverview",request, mvc);
        Assertions.assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting piece metadata with an invalid file designator")
    public void testGetPieceMetadataInvalidDesignator() throws Exception {
        DisplayGetPieceMetadataRequest request = new DisplayGetPieceMetadataRequest();
        request.setFileDesignator(TestingDictionary.display_all_invalidFileDesignator);
        MvcResult response = TestingDictionary.mockRequest("display","getPieceMetadata",request, mvc);
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting track info with an invalid file designator")
    public void testGetTrackInfoInvalidDesignator() throws Exception {
        DisplayGetTrackInfoRequest request = new DisplayGetTrackInfoRequest();
        request.setFileDesignator(TestingDictionary.display_all_invalidFileDesignator);
        MvcResult response = TestingDictionary.mockRequest("display","getTrackInfo",request, mvc);
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting track metadata with an invalid file designator")
    public void testGetTrackMetadataInvalidDesignator() throws Exception {
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();
        request.setFileDesignator(TestingDictionary.display_all_invalidFileDesignator);
        request.setTrackIndex((int) TestingDictionary.display_all_valid_track_index);
        MvcResult response = TestingDictionary.mockRequest("display","getTrackMetadata",request, mvc);
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting track overview with an invalid file designator")
    public void testGetTrackOverviewInvalidDesignator() throws Exception {
        DisplayGetTrackOverviewRequest request = new DisplayGetTrackOverviewRequest();
        request.setFileDesignator(TestingDictionary.display_all_invalidFileDesignator);
        request.setTrackIndex((int) TestingDictionary.display_all_valid_track_index);
        MvcResult response = TestingDictionary.mockRequest("display","getTrackOverview",request, mvc);
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting track metadata with an invalid track index and valid file")
    public void testGetTrackMetadataInvalidTrackIndex() throws Exception {
        DisplayGetTrackMetadataRequest request = new DisplayGetTrackMetadataRequest();
        request.setFileDesignator(TestingDictionary.display_all_validFileDesignator);
        request.setTrackIndex((int) TestingDictionary.display_all_invalid_track_index);
        MvcResult response = TestingDictionary.mockRequest("display","getTrackMetadata",request, mvc);
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }

    @Test
    @DisplayName("Tests getting track overview with an invalid track index and valid file")
    public void testGetTrackOverviewInvalidTrackIndex() throws Exception {
        DisplayGetTrackOverviewRequest request = new DisplayGetTrackOverviewRequest();
        request.setFileDesignator(TestingDictionary.display_all_validFileDesignator);
        request.setTrackIndex((int) TestingDictionary.display_all_invalid_track_index);
        MvcResult response = TestingDictionary.mockRequest("display","getTrackOverview",request, mvc);
        Assertions.assertEquals(400, response.getResponse().getStatus());
    }
    
}
