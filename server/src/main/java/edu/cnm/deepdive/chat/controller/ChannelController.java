package edu.cnm.deepdive.chat.controller;

import edu.cnm.deepdive.chat.model.entity.Channel;
import edu.cnm.deepdive.chat.service.AbstractChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/channels")
public class ChannelController {

  //  to do its job controller needs channel service
   private final AbstractChannelService channelService;

  @Autowired
   public ChannelController(AbstractChannelService channelService) {
    this.channelService = channelService;
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public Channel post(@RequestBody Channel channel) {// FIXME: 3/6/25 Use ResponseEntity}
    return channelService.add(channel);

  }





}
