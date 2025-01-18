package org.web.domainservice.rabbitmq;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.web.domainservice.models.Group;
import org.web.domainservice.repositories.GroupRepository;

import java.util.Map;

@Service
public class GroupMessageListener {

    private final GroupRepository groupRepository;
    private final JsonParser jsonParser;

    public GroupMessageListener(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        this.jsonParser = JsonParserFactory.getJsonParser();
    }

    @RabbitListener(queues = "groupCreateQueue")
    public void handleCreateGroup(String message) {
        System.out.println("Message received: " + message);
        try {
            Map<String, Object> groupMap = jsonParser.parseMap(message);
            System.out.println("Parsed Map: " + groupMap);
            Group group = new Group();
            group.setName((String) groupMap.get("name"));
            group.setDescription((String) groupMap.get("description"));
            groupRepository.save(group);
            System.out.println("Group saved to MongoDB: " + group);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "groupUpdateQueue")
    public void handleUpdateGroup(String message) {
        try {
            Map<String, Object> groupMap = jsonParser.parseMap(message);
            Group group = groupRepository.findById((String) groupMap.get("id")).orElseThrow(() -> new RuntimeException("Group not found"));
            group.setName((String) groupMap.get("name"));
            group.setDescription((String) groupMap.get("description"));
            groupRepository.save(group);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    @RabbitListener(queues = "groupDeleteQueue")
    public void handleDeleteGroup(String groupId) {
        groupRepository.deleteById(groupId);
    }
}