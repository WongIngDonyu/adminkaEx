package org.web.gatewayservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.web.api.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web.GroupDto;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
    private final DomainServiceGrpc.DomainServiceBlockingStub domainServiceStub;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public GroupController(DomainServiceGrpc.DomainServiceBlockingStub domainServiceStub, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.domainServiceStub = domainServiceStub;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "groups", key = "#id")
    @GetMapping("/{id}")
    public String getGroup(@PathVariable String id) throws InvalidProtocolBufferException {
        GetGroupRequest request = GetGroupRequest.newBuilder().setId(id).build();
        GroupResponse response = domainServiceStub.getGroup(request);
        return JsonFormat.printer().print(response);
    }

    @Cacheable(value = "groups", key = "'allGroups'")
    @GetMapping
    public List<String> getAllGroups() {
        logger.info("Received GET /api/groups request");
        try {
            EmptyRequest request = EmptyRequest.newBuilder().build();
            ListGroupsResponse response = domainServiceStub.listGroups(request);
            List<String> jsonResponseList = response.getGroupsList().stream().map(group -> {
                try {
                    return JsonFormat.printer().print(group);
                } catch (Exception e) {
                    throw new RuntimeException("Error serializing GroupResponse to JSON", e);
                }
            }).toList();
            logger.info("Response for GET /api/groups: {}", jsonResponseList);
            return jsonResponseList;
        } catch (Exception e) {
            logger.error("Error during GET /api/groups: {}", e.getMessage(), e);
            throw new RuntimeException("Error fetching groups: " + e.getMessage());
        }
    }

    @CacheEvict(value = "groups", key = "#id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable String id) {
        logger.info("Received DELETE /api/groups/{} request", id);
        rabbitTemplate.convertAndSend("groupExchange", "groupDelete", id);
        return ResponseEntity.accepted().build();
    }

    @CacheEvict(value = "groups", allEntries = true)
    @PostMapping
    public ResponseEntity<Void> createGroup(@RequestBody GroupDto groupRequest) {
        logger.info("Received POST /api/groups request: {}", groupRequest);
        try {
            String jsonMessage = toJson(groupRequest);
            rabbitTemplate.convertAndSend("groupExchange", "groupCreate", jsonMessage);
        } catch (Exception e) {
            logger.error("Failed to serialize GroupDto to JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize GroupDto to JSON", e);
        }
        return ResponseEntity.accepted().build();
    }

    @CacheEvict(value = "groups", allEntries = true)
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateGroup(@PathVariable String id, @RequestBody GroupDto groupRequest) {
        logger.info("Received PUT /api/groups/{} request: {}", id, groupRequest);
        try {
            groupRequest.setId(id);
            String jsonMessage = toJson(groupRequest);
            rabbitTemplate.convertAndSend("groupExchange", "groupUpdate", jsonMessage);
        } catch (Exception e) {
            logger.error("Failed to serialize GroupDto to JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize GroupDto to JSON", e);
        }
        return ResponseEntity.accepted().build();
    }

    private String toJson(GroupDto groupDto) {
        try {
            return objectMapper.writeValueAsString(groupDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert GroupDto to JSON", e);
        }
    }
}
