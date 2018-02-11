package SpringUserService.repository;

import java.util.List;

import SpringUserService.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource (collectionResourceRel = "users", path = "users")
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByEmail(@Param("email") String email);
}