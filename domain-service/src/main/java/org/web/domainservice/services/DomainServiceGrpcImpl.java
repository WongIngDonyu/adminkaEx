package org.web.domainservice.services;

import com.web.api.grpc.*;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;
import org.web.domainservice.models.Group;
import org.web.domainservice.repositories.GroupRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class DomainServiceGrpcImpl extends DomainServiceGrpc.DomainServiceImplBase {

    private final GroupRepository groupRepository;

    public DomainServiceGrpcImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public void createGroup(CreateGroupRequest request, StreamObserver<GroupResponse> responseObserver) {
        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group = groupRepository.save(group);
        GroupResponse response = GroupResponse.newBuilder().setId(group.getId()).setName(group.getName()).setDescription(group.getDescription()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getGroup(GetGroupRequest request, StreamObserver<GroupResponse> responseObserver) {
        Group group = groupRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException("Group not found"));
        GroupResponse response = GroupResponse.newBuilder().setId(group.getId()).setName(group.getName()).setDescription(group.getDescription()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateGroup(UpdateGroupRequest request, StreamObserver<GroupResponse> responseObserver) {
        Group group = groupRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException("Group not found"));
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group = groupRepository.save(group);
        GroupResponse response = GroupResponse.newBuilder().setId(group.getId()).setName(group.getName()).setDescription(group.getDescription()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteGroup(DeleteGroupRequest request, StreamObserver<DeleteGroupResponse> responseObserver) {
        groupRepository.deleteById(request.getId());
        DeleteGroupResponse response = DeleteGroupResponse.newBuilder().setId(request.getId()).setMessage("Group deleted successfully").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listGroups(EmptyRequest request, StreamObserver<ListGroupsResponse> responseObserver) {
        List<Group> groups = groupRepository.findAll();
        List<GroupResponse> groupResponses = groups.stream().map(group -> GroupResponse.newBuilder().setId(group.getId()).setName(group.getName()).setDescription(group.getDescription()).build()).collect(Collectors.toList());
        ListGroupsResponse response = ListGroupsResponse.newBuilder().addAllGroups(groupResponses).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}