package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/file")
    public String listUploadedFiles(Map<String, Object> model) throws IOException, SQLException, NoSuchAlgorithmException, NoSuchPaddingException {

        final String url = "jdbc:postgresql://localhost/postgres";
        final String user = "postgres";
        final String password = "root";
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        model.put("items", shrek.getListOfData());
        return "uploadForm";
    }

    @GetMapping("/us")
    public String main(Map<String, Object> model) throws IOException, SQLException, NoSuchAlgorithmException, NoSuchPaddingException {
        final String url = "jdbc:postgresql://localhost/postgres";
        final String user = "postgres";
        final String password = "root";
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        model.put("items", shrek.getListOfData());
        return "uploadFormUser";
    }

    @GetMapping("/login")
    public String login(Map<String, Object> model) throws IOException, SQLException, NoSuchAlgorithmException, NoSuchPaddingException {
        return "login";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        final String url = "jdbc:postgresql://localhost/postgres";
        final String user = "postgres";
        final String password = "root";
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        shrek.addFile(connection, file);

        return "redirect:/file";
    }

    @PostMapping("/log")
    public String handleLogo(@RequestParam("logo") String logo) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {

        if (logo.equals("admin@admin")) {
            return "redirect:/file";
        } else {
            return "redirect:/us";
        }

    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}

