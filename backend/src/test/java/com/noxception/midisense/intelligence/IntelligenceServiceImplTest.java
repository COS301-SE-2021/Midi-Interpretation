package com.noxception.midisense.intelligence;

import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import com.noxception.midisense.config.MIDISenseConfig;
import com.noxception.midisense.dataclass.MIDISenseUnitTest;
import com.noxception.midisense.dataclass.TestingDictionary;
import com.noxception.midisense.display.exceptions.InvalidTrackException;
import com.noxception.midisense.display.rrobjects.*;
import com.noxception.midisense.interpreter.exceptions.InvalidDesignatorException;
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

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class IntelligenceServiceImplTest extends MIDISenseUnitTest {


}
