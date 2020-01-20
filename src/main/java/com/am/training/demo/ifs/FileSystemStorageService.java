package com.am.training.demo.ifs;

import com.am.training.demo.DemoApplication;
import com.am.training.demo.entity.DataItem;
import com.am.training.demo.exception.EmptyListException;
import com.am.training.demo.processor.CsvProcessor;
import com.am.training.demo.service.DataItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


@Service
public class FileSystemStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class);


    @Autowired
    private DataItemService dataItemService;

    @Autowired
    public FileSystemStorageService( ) throws IOException {


    }

    @Override
    public synchronized void store(MultipartFile file) {

        logger.info("Trying to store file");
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            String path = DemoApplication.rootLocation.toAbsolutePath()+"\\"+filename;
            File filePath = new File(path);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

                if(Files.exists(filePath.toPath()))
                logger.info("File Path from storing the file: " + path);




            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }finally {

        }

        File folder = new File(String.valueOf(DemoApplication.rootLocation.toAbsolutePath()));
        logger.info("Trying to delete all files");
        File[] listOfFiles = folder.listFiles();
        for(File theFile: listOfFiles){
            String fileName = theFile.getName();
            logger.info("Trying to delete "+fileName);
            if(theFile.delete()){
                logger.info("The file "+fileName + " is deleted.");
            }else{
                logger.info("Failed to delete "+fileName);
            }
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(DemoApplication.rootLocation, 1)
                    .filter(path -> !path.equals(DemoApplication.rootLocation))
                    .map(DemoApplication.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return DemoApplication.rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(DemoApplication.rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(DemoApplication.rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
