package notes.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class LocalFileService {

    private final String folder;

    private Map<UUID, Path> fileDb = new HashMap<>();

    public LocalFileService(@Value("${localfilestorage.folder:}") String folder) {
        this.folder = folder;
    }

    public UUID saveFile(Object object) {
        MultipartFile multipartFile = (MultipartFile) object;
        try {
            byte[] bytes = multipartFile.getBytes();
            System.out.println(multipartFile.getOriginalFilename());
            String fileName = multipartFile.getOriginalFilename();
            if (fileName == null) {
                throw new RuntimeException("No file name provided");
            }
            Path path = Paths.get(folder + fileName);
            Files.write(path, bytes);
            // TODO Should be file's ID from DB
            UUID uuid = UUID.randomUUID();
            fileDb.put(uuid, path);
            return uuid;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not save file with name [" + multipartFile.getOriginalFilename() + "]");
        }
    }

    public Path getFilePath(UUID uuid) {
        return fileDb.get(uuid);
    }
}
