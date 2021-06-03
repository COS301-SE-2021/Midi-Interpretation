package com.noxception.midisense.interpreter;

import com.noxception.midisense.api.InterpreterApi;
import com.noxception.midisense.models.UploadFileRequest;
import com.noxception.midisense.models.UploadFileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InterpreterController implements InterpreterApi {
    @Override
    public ResponseEntity<UploadFileResponse> uploadFile(UploadFileRequest body) {
        return null;
    }
}
