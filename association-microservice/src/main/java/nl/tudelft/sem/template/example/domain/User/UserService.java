package nl.tudelft.sem.template.example.domain.User;

import nl.tudelft.sem.template.example.domain.association.AssociationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private final transient UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     *
     * @param userId
     * @return the corresponding memberId
     */
    public Optional<User> getMemberById(String userId){
        return userRepository.findByUserId(userId);
    }

    /**
     *
     * @param memberId
     * @param name
     * @throws NoSuchAssociationException
     *
     * save a member to the repository
     */
    public void addUser(String memberId, String name) {
        userRepository.save(new User(memberId, name));
    }


    /**
     *
     * @param service
     * @param associationId
     * @throws NoSuchAssociationException
     *
     * checks if there is an association correspond to the id
     */
    private void checkAssociationExists(AssociationService service, int associationId) throws NoSuchAssociationException{
        try{
            service.getAssociation(associationId).get();
        }catch (NoSuchElementException e){
            throw new NoSuchAssociationException();
        }
    }
}
