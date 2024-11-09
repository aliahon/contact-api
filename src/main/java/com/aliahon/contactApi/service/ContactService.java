package com.aliahon.contactApi.service;

import com.aliahon.contactApi.Domain.Contact;
import com.aliahon.contactApi.repo.ContactRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.aliahon.contactApi.constant.Constant.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepo contactRepo;

    public Page<Contact> getAllContacts(int page, int size){
        return contactRepo.findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    public Contact getContact(String id){
        return contactRepo.findById(id).orElseThrow(()->new RuntimeException("Contact not found"));
    }

    public Contact createContact(Contact contact){
        return contactRepo.save(contact);
    }

    public void deleteContact(String id){
        contactRepo.deleteById(id);
    }

    public String uploadPhoto(String id, MultipartFile file){
        Contact contact =getContact(id);
        String photoUrl= imgFunction.apply(id,file);
        contact.setImgUrl(photoUrl);
        contactRepo.save(contact);
        return photoUrl;
    }

    private final Function<String, String> fileExtention = filename -> Optional
            .of(filename).
            filter(name-> name.contains("."))
            .map(name->"."+name.substring(filename.indexOf(".")+1))
            .orElse(".png");

    private final BiFunction<String, MultipartFile,String> imgFunction=(id, img)->{
        String filename = id+fileExtention.apply(img.getOriginalFilename());
        try {
            Path path = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if(!Files.exists(path)){Files.createDirectories(path);}
            Files.copy(img.getInputStream(), path
                    .resolve(filename), REPLACE_EXISTING);

            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/contact/img/"+filename).toUriString();
        }catch (Exception e){
            throw new RuntimeException("Unable to save image!");
        }
    };
}
