package vn.baymax.fjob.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.baymax.fjob.domain.Subscriber;
import vn.baymax.fjob.service.SubcriberService;
import vn.baymax.fjob.util.SecurityUtil;
import vn.baymax.fjob.util.annotation.ApiMessage;
import vn.baymax.fjob.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SubcriberController {
    private final SubcriberService subcriberService;

    public SubcriberController(SubcriberService subcriberService) {
        this.subcriberService = subcriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("create new subcriber")
    public ResponseEntity<Subscriber> create(@RequestBody Subscriber subscriber) throws IdInvalidException {
        boolean isExist = this.subcriberService.isExistsByEmail(subscriber.getEmail());
        if (isExist) {
            throw new IdInvalidException("email " + subscriber.getEmail() + " already exist");

        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subcriberService.create(subscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("update new subcriber")
    public ResponseEntity<Subscriber> update(@RequestBody Subscriber subscriber) throws IdInvalidException {
        Subscriber subsDB = this.subcriberService.findById(subscriber.getId());
        if (subsDB == null) {
            throw new IdInvalidException("subscriber with id: " + subscriber.getId() + " not found");
        }
        return ResponseEntity.ok().body(this.subcriberService.update(subsDB, subscriber));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("get subcriber's skill")
    public ResponseEntity<Subscriber> getSubSkill() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        return ResponseEntity.ok().body(this.subcriberService.findByEmail(email));
    }

}
