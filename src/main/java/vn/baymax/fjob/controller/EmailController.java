package vn.baymax.fjob.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.baymax.fjob.service.SubcriberService;
import vn.baymax.fjob.util.annotation.ApiMessage;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final SubcriberService subcriberService;

    public EmailController(SubcriberService subcriberService) {
        this.subcriberService = subcriberService;
    }

    @GetMapping("/email")
    @ApiMessage("Send email")
    public String sendEmail() {
        this.subcriberService.sendSubscribersEmailJobs();
        return "send mail successfull";
    }

}
