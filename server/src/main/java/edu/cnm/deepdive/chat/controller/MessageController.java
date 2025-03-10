package edu.cnm.deepdive.chat.controller;

import edu.cnm.deepdive.chat.service.AbstractMessageService;
import edu.cnm.deepdive.chat.service.AbstractUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/channels/{channelKey)/messages")
public class MessageController {

  private final AbstractMessageService messageService;
  private final AbstractUserService userService;

  @Autowired
  public MessageController(AbstractMessageService messageService, AbstractUserService userService) {
    this.messageService = messageService;
    this.userService = userService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)




}
