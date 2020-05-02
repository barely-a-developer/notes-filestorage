package notes.controllers;

import lombok.RequiredArgsConstructor;
import notes.models.StorageFileDto;
import notes.services.LocalFileService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FilesController {

    private final LocalFileService localFileService;

    @PostMapping(path = "/", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<StorageFileDto> uploadFile(@RequestParam(name = "file") MultipartFile file) {
        UUID fileUuid = localFileService.saveFile(file);
        return new ResponseEntity<>(StorageFileDto.builder().storageId(fileUuid.toString()).build(), HttpStatus.CREATED);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable("id") String id) throws IOException {

        Path path = localFileService.getFilePath(UUID.fromString(id));

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + path.getFileName());
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                             .headers(header)
                             .contentType(MediaType.parseMediaType("application/octet-stream"))
                             .body(resource);
    }
}
