package org.web.domainservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.web.domainservice.models.Group;

public interface GroupRepository extends MongoRepository<Group, String> {
}